package com.ncubo.logicaDeLasConversaciones;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.partesDeLaConversacion.Afirmacion;
import com.ncubo.chatbot.partesDeLaConversacion.CaracteristicaDeLaFrase;
import com.ncubo.chatbot.partesDeLaConversacion.Despedida;
import com.ncubo.chatbot.partesDeLaConversacion.Frase;
import com.ncubo.chatbot.partesDeLaConversacion.HiloDeLaConversacion;
import com.ncubo.chatbot.partesDeLaConversacion.Pregunta;
import com.ncubo.chatbot.partesDeLaConversacion.Respuesta;
import com.ncubo.chatbot.partesDeLaConversacion.Salida;
import com.ncubo.chatbot.partesDeLaConversacion.Saludo;
import com.ncubo.chatbot.partesDeLaConversacion.Tema;
import com.ncubo.chatbot.partesDeLaConversacion.TemaPendiente;
import com.ncubo.chatbot.partesDeLaConversacion.Temario;
import com.ncubo.chatbot.partesDeLaConversacion.TemasPendientesDeAbordar;
import com.ncubo.chatbot.participantes.Agente;
import com.ncubo.chatbot.participantes.Cliente;
import com.ncubo.db.ConsultaDao;
import com.ncubo.estadisticas.Estadisticas;

public class Conversacion {

	//private Participantes participantes;
	private Cliente participante;
	private HiloDeLaConversacion hilo; // Mantiene el contexto, osea todas las intenciones y entidades, sabe que se dijo 
	private Temario temario;
	private Agente agente;
	private Tema temaActual = null;
	private Frase fraseActual = null;
	private Tema temaActualDelWorkSpaceEspecifico = null;
	private Estadisticas estadisticasTemasTratados;
	private ArrayList<Salida> miUltimaSalida;
	private final Constantes.ModoDeLaVariable modoDeResolucionDeResultadosFinales;
	private Date fechaDelUltimoRegistroDeLaConversacion;
	private final TemasPendientesDeAbordar temasPendientes;
	
	public Conversacion(Temario temario, Cliente participante, ConsultaDao consultaDao, Agente miAgente){
		// Hacer lamdaba para agregar los participantes
		//this.participantes = new Participantes();
		temasPendientes = new TemasPendientesDeAbordar();
		this.participante = participante;
		this.modoDeResolucionDeResultadosFinales = temario.contenido().obtenerModoDeTrabajo();
		//this.agente = new Agente(temario.contenido().getMiWorkSpaces());
		this.agente = miAgente;
		this.agente.manifestarseEnFormaOral();
		this.agente.manifestarseEnFormaVisual();
		
		this.hilo = new HiloDeLaConversacion();
		//this.participantes.agregar(agente).agregar(participante);
		this.temario = temario;
		estadisticasTemasTratados = new Estadisticas(consultaDao);
		miUltimaSalida = new ArrayList<>();
		fechaDelUltimoRegistroDeLaConversacion = Calendar.getInstance().getTime();
	}
	
	public void cambiarParticipante(Cliente participante){
		this.participante = participante;
	}
	
	public Cliente obtenerElParticipante(){
		return this.participante;
	}
	
	public Agente obtenerAgenteDeLaMuni(){
		return this.agente;
	}
	
	public ArrayList<Salida> inicializarLaConversacion(){
		ArrayList<Salida> misSalidas = new ArrayList<Salida>();
		
		System.out.println("");
		System.out.println("Iniciar conversacion ...");
		System.out.println("");
		
		this.temaActual = this.temario.buscarTemaPorLaIntencion(Constantes.INTENCION_SALUDAR);
		
		Saludo saludoGeneral = (Saludo) this.temario.extraerFraseDeSaludoInicial(CaracteristicaDeLaFrase.esUnSaludo);
		misSalidas.add(agente.decirUnaFrase(saludoGeneral, null, temaActual, participante, modoDeResolucionDeResultadosFinales));
		ponerComoYaTratado(saludoGeneral);
		
		Pregunta queQuiere = (Pregunta) this.temario.extraerFraseDeSaludoInicial(CaracteristicaDeLaFrase.esUnaPregunta);
		misSalidas.add(agente.decirUnaFrase(queQuiere, null, temaActual, participante, modoDeResolucionDeResultadosFinales));
		
		ponerComoYaTratado(queQuiere);
		miUltimaSalida = misSalidas;
		
		fechaDelUltimoRegistroDeLaConversacion = Calendar.getInstance().getTime();
		
		agente.cambiarANivelSuperior();
		
		return misSalidas;
	}
	
	public ArrayList<Salida> analizarLaRespuestaConWatson(String respuestaDelCliente, boolean esModoConsulta) throws Exception{
		ArrayList<Salida> misSalidas = new ArrayList<Salida>();
		Respuesta respuesta = null;
		
		boolean hayTemaActualDiciendose = this.temaActual != null;
		if(hayTemaActualDiciendose){
			respuesta = agente.enviarRespuestaAWatson(respuestaDelCliente, fraseActual);
			this.hilo.agregarUnaRespuesta(respuesta);
	
			if (respuesta.hayProblemasEnLaComunicacionConWatson()){
				String nombreFrase = obtenerUnaFraseAfirmativa(Constantes.FRASES_INTENCION_ERROR_CON_WATSON);
				Afirmacion errorDeComunicacionConWatson = (Afirmacion) this.temario.contenido().frase(nombreFrase);
				misSalidas.add(agente.decirUnaFrase(errorDeComunicacionConWatson, respuesta, temaActual, participante, modoDeResolucionDeResultadosFinales));
				ponerComoYaTratado(errorDeComunicacionConWatson);
			}else{
				String idFraseActivada = respuesta.obtenerFraseActivada();
				if(! verificarIntencionNoAsociadaANingunWorkspace(misSalidas, respuesta)){
					respuesta = analizarResultadosDelAgente(misSalidas, idFraseActivada, respuesta, respuestaDelCliente);
				}else{
					if(! hayAlgunaPreguntaEnLasSalidas(misSalidas) && temasPendientes.hayTemasPendientes()){
						TemaPendiente temaPendiente = temasPendientes.extraerElSiquienteTema();
						this.temaActual = temaPendiente.getTemaActual();
						this.fraseActual = temaPendiente.getFraseActual();
						agente.cambiarElContexto(temaPendiente.getContextoCognitivo());
						volverlARetomarUnTema(misSalidas, respuesta);
					}
				}
			}
			
		}else{
			if(temasPendientes.hayTemasPendientes()){ // TODO Sacar un tema top de la Pila
				TemaPendiente temaPendiente = temasPendientes.extraerElSiquienteTema();
				this.temaActual = temaPendiente.getTemaActual();
				this.fraseActual = temaPendiente.getFraseActual();
				agente.cambiarElContexto(temaPendiente.getContextoCognitivo());
				volverlARetomarUnTema(misSalidas, respuesta);
			}else{
				agente.cambiarANivelSuperior();
				respuesta = agente.enviarRespuestaAWatson(respuestaDelCliente, fraseActual);
				if(esModoConsulta){
					if(! verificarIntencionNoAsociadaANingunWorkspace(misSalidas, respuesta)){
						String idFraseActivada = respuesta.obtenerFraseActivada();
						if(misSalidas.isEmpty()){
							respuesta = analizarResultadosDelAgente(misSalidas, idFraseActivada, respuesta, respuestaDelCliente);
						}else{
							decirTemaPreguntarPorOtraCosa(misSalidas, respuesta, respuestaDelCliente);
						}
					}else{
						if(! hayAlgunaPreguntaEnLasSalidas(misSalidas) && temasPendientes.hayTemasPendientes()){
							TemaPendiente temaPendiente = temasPendientes.extraerElSiquienteTema();
							this.temaActual = temaPendiente.getTemaActual();
							this.fraseActual = temaPendiente.getFraseActual();
							agente.cambiarElContexto(temaPendiente.getContextoCognitivo());
							volverlARetomarUnTema(misSalidas, respuesta);
						}
					}
				}else{// TODO Buscar el siguiente tema a tocar (modo cuestionario - E.g: Hacer preguntas seguidas)
				}
			}
		}
		
		if(! hayAlgunaPreguntaEnLasSalidas(misSalidas) && temasPendientes.hayTemasPendientes() && respuesta.seTerminoElTema()){ // TODO Sacar un tema top de la Pila
			TemaPendiente temaPendiente = temasPendientes.extraerElSiquienteTema();
			this.temaActual = temaPendiente.getTemaActual();
			this.fraseActual = temaPendiente.getFraseActual();
			agente.cambiarElContexto(temaPendiente.getContextoCognitivo());
			volverlARetomarUnTema(misSalidas, respuesta);
		}else{
			if(! hayAlgunaPreguntaEnLasSalidas(misSalidas) && esModoConsulta && respuesta.seTerminoElTema() && 
					! temasPendientes.hayTemasPendientes() && ! existeLaFraseEnLasSalidas(misSalidas, "noQuiereHacerOtraConsulta"))
				decirTemaPreguntarPorOtraCosa(misSalidas, respuesta, respuestaDelCliente);
		}
		
		if(misSalidas.isEmpty()){
			decirTemaNoEntendi(misSalidas, respuesta);
		}
		
		fechaDelUltimoRegistroDeLaConversacion = Calendar.getInstance().getTime();
		miUltimaSalida = misSalidas;
		
		return misSalidas;
	}
	
	private Respuesta analizarResultadosDelAgente(ArrayList<Salida> misSalidas, String idFraseActivada, Respuesta respuesta, String respuestaDelCliente){
		if(agente.seTieneQueAbordarElTema()){
			agente.yaNoSeTieneQueAbordarElTema();
			misSalidas.add(agente.volverAPreguntarUnaFraseConMeRindo(fraseActual, respuesta, temaActual, true, participante, modoDeResolucionDeResultadosFinales));
			temaActual = null;
		}else{
			if (agente.entendiLaUltimaPregunta()){
				idFraseActivada = respuesta.obtenerFraseActivada();
				extraerOracionesAfirmarivasYPreguntas(misSalidas, respuesta, idFraseActivada);
				
				if(agente.hayQueCambiarDeTema() && misSalidas.isEmpty()){// Hay que buscar un nuevo tema y no he dicho nada aun
					respuesta = cambiarDeTema(idFraseActivada, respuestaDelCliente, misSalidas, respuesta);
					if(respuesta.seTerminoElTema())
						temaActual = null;
				}else{
					temaActual = null;
					agente.cambiarANivelSuperior();
				}
			}else{ 
				if (agente.hayQueCambiarDeTemaForzosamente()){ // TODO Analizar si hay mas de un tema en cola
					if(! temaActual.obtenerNombre().equals("preguntarPorOtraConsulta"))
						this.temasPendientes.agregarUnTema(new TemaPendiente(temaActual, fraseActual, agente.obtenerMiUltimoContexto()));
					
					agente.cambiarANivelSuperior();
					respuesta = agente.enviarRespuestaAWatson(respuestaDelCliente, fraseActual);
					
					respuesta = cambiarDeTema(idFraseActivada, respuestaDelCliente, misSalidas, respuesta); 
				}
				else{
					// Verificar que fue lo que paso	
					System.out.println("No entendi la ultima pregunta");
					if(fraseActual.esMandatorio()){
						misSalidas.add(agente.volverAPreguntarUnaFrase(fraseActual, respuesta, temaActual, participante, modoDeResolucionDeResultadosFinales));
					}else{
						temaActual = null;
						fraseActual = null;
					}
				}
			}
		}
		return respuesta;
	}
	
	private boolean hayAlgunaPreguntaEnLasSalidas(ArrayList<Salida> misSalidas){
		for(Salida miSalida: misSalidas){
			if(miSalida.getFraseActual() instanceof Pregunta)
				return true;
		}
		return false;
	}
	
	private boolean existeLaFraseEnLasSalidas(ArrayList<Salida> misSalidas, String idNombeFrase){
		for(Salida miSalida: misSalidas){
			if(miSalida.getFraseActual().obtenerNombreDeLaFrase().equals(idNombeFrase))
				return true;
		}
		return false;
	}
	
	private Respuesta cambiarDeTema(String idFraseActivada, String respuestaDelCliente, ArrayList<Salida> misSalidas, Respuesta respuesta){
		String laIntencion = agente.obtenerNombreDeLaIntencionGeneralActiva();
		Tema temaNuevo = this.temario.proximoTemaATratar(temaActual, hilo.verTemasYaTratadosYQueNoPuedoRepetir(), agente.obtenerNombreDelWorkspaceActual(), laIntencion);
		if( temaNuevo != null){
			temaActual = temaNuevo;
			
			TemaPendiente temaPrimitivo = temasPendientes.buscarUnTemaPendiente(temaActual);
			boolean esteTemaEstaPendiente = temaPrimitivo != null;
			if(esteTemaEstaPendiente)
				temasPendientes.borrarUnTemaPendiente(temaPrimitivo);
			
			agente.yaNoCambiarDeTema();
			agregarVariablesDeContextoDelClienteAWatson(temaActual);
			
			if (idFraseActivada.equals("")){ // Quiere decir que no hay ninguna pregunta en la salida
				System.out.println("El proximo tema a tratar es: "+this.temaActual.obtenerIdTema());
				
				// Activar en el contexto el tema
				agente.activarTemaEnElContextoDeWatson(this.temaActual.obtenerNombre());
				
				// llamar a watson y ver que bloque se activo
				respuesta = agente.inicializarTemaEnWatson(respuestaDelCliente);
				idFraseActivada = agente.obtenerNodoActivado(respuesta.messageResponse());
				
				System.out.println("Id de la frase a decir: "+idFraseActivada);
				extraerOracionesAfirmarivasYPreguntas(misSalidas, respuesta, idFraseActivada);
			}
			
			Tema temaSaludo = this.temario.buscarTemaPorLaIntencion(Constantes.INTENCION_SALUDAR);
			Tema temaDespedida = this.temario.buscarTemaPorLaIntencion(Constantes.INTENCION_DESPEDIDA);
			if( (! this.temaActual.equals(temaSaludo)) && (! this.temaActual.equals(temaDespedida)))
				ponerComoYaTratado(this.temaActual);
		}else{ // No entiendo
			decirTemaNoEntendi(misSalidas, respuesta);
			agente.cambiarANivelSuperior();
		}
		return respuesta;
	}
	
	private void decirTemaPreguntarPorOtraCosa(ArrayList<Salida> misSalidas, Respuesta respuesta, String respuestaDelCliente){
		System.out.println("Se va a preguntar por otra cosa ...");							
		this.temaActual = this.temario.buscarTemaPorLaIntencion(Constantes.INTENCION_PREGUNTAR_POR_OTRA_CONSULTA);

		// Activar en el contexto el tema
		agente.activarTemaEnElContextoDeWatson(this.temaActual.obtenerNombre());
		
		// llamar a watson y ver que bloque se activo
		respuesta = agente.inicializarTemaEnWatson(respuestaDelCliente);
		String idFraseActivada = agente.obtenerNodoActivado(respuesta.messageResponse());
		
		System.out.println("Id de la frase a decir: "+idFraseActivada);
		extraerOracionesAfirmarivasYPreguntas(misSalidas, respuesta, idFraseActivada, this.temaActual);
		
		ponerComoYaTratado(this.temaActual);
		agente.yaNoCambiarANivelSuperior();
	}
	
	private void volverlARetomarUnTema(ArrayList<Salida> misSalidas, Respuesta respuesta){
		decirFraseRecordatoria(misSalidas, respuesta);
		misSalidas.add(agente.decirUnaFrase(fraseActual, respuesta, temaActual, participante, modoDeResolucionDeResultadosFinales));
		agente.yaNoCambiarANivelSuperior();
		ponerComoYaTratado(temaActual);
	}
	
	private void decirFraseRecordatoria(ArrayList<Salida> misSalidas, Respuesta respuesta){
		System.out.println("Frase recordatoria ...");
		String nombreFrase = obtenerUnaFraseAfirmativa(Constantes.FRASES_INTENCION_RECORDAR_TEMAS);
		
		Afirmacion fraseRecordatoria = (Afirmacion) this.temario.frase(nombreFrase);
		misSalidas.add(agente.decirUnaFrase(fraseRecordatoria, respuesta, null, participante, modoDeResolucionDeResultadosFinales));

	}
	
	private void decirTemaNoEntendi(ArrayList<Salida> misSalidas, Respuesta respuesta){
		System.out.println("No entendi bien ...");
		Tema miTema = this.temario.buscarTema(Constantes.INTENCION_NO_ENTIENDO);
		String nombreFrase = obtenerUnaFraseAfirmativa(Constantes.FRASES_INTENCION_NO_ENTIENDO);
		
		Afirmacion fueraDeContexto = (Afirmacion) this.temario.frase(nombreFrase);
		misSalidas.add(agente.decirUnaFrase(fueraDeContexto, respuesta, miTema, participante, modoDeResolucionDeResultadosFinales));
		ponerComoYaTratado(miTema);
	}
	
	private boolean verificarIntencionNoAsociadaANingunWorkspace(ArrayList<Salida> misSalidas, Respuesta respuesta) throws Exception{
		if(agente.hayIntencionNoAsociadaANingunWorkspace()){
			Tema miTema = null;
			if(agente.obtenerNombreDeLaIntencionGeneralActiva().equals(Constantes.INTENCION_SALUDAR)){
				System.out.println("Quiere saludar ...");
				
				String saludo = obtenerUnaFraseAfirmativa(Constantes.FRASES_INTENCION_SALUDAR);
				miTema = this.temario.buscarTemaPorLaIntencion(Constantes.INTENCION_SALUDAR);

				Afirmacion saludar = (Afirmacion) miTema.buscarUnaFrase(saludo);
				misSalidas.add(agente.decirUnaFrase(saludar, respuesta, miTema, participante, modoDeResolucionDeResultadosFinales));
				ponerComoYaTratado(saludar);
				
				Pregunta queQuiere = (Pregunta) this.temario.extraerFraseDeSaludoInicial(CaracteristicaDeLaFrase.esUnaPregunta);
				misSalidas.add(agente.decirUnaFrase(queQuiere, respuesta, miTema, participante, modoDeResolucionDeResultadosFinales));
				ponerComoYaTratado(queQuiere);
				
				temasPendientes.borrarLosTemasPendientes();
				
			}else if(agente.obtenerNombreDeLaIntencionGeneralActiva().equals(Constantes.INTENCION_DESPEDIDA)){
				System.out.println("Quiere despedirse ...");
				miTema = this.temario.buscarTemaPorLaIntencion(Constantes.INTENCION_DESPEDIDA);
				String nombreFrase = obtenerUnaFraseDespedida(Constantes.FRASES_INTENCION_DESPEDIDA);
				
				Despedida saludar = (Despedida) miTema.buscarUnaFrase(nombreFrase);
				misSalidas.add(agente.decirUnaFrase(saludar, respuesta, miTema, participante, modoDeResolucionDeResultadosFinales));
				ponerComoYaTratado(saludar);
				
				temasPendientes.borrarLosTemasPendientes();
				
			}else if(agente.obtenerNombreDeLaIntencionGeneralActiva().equals(Constantes.INTENCION_FUERA_DE_CONTEXTO)){
				System.out.println("Esta fuera de contexto ...");
				miTema = this.temario.buscarTemaPorLaIntencion(Constantes.INTENCION_FUERA_DE_CONTEXTO);
				String nombreFrase = obtenerUnaFraseAfirmativa(Constantes.FRASES_INTENCION_FUERA_DE_CONTEXTO);
				
				Afirmacion fueraDeContexto = (Afirmacion) miTema.buscarUnaFrase(nombreFrase);
				misSalidas.add(agente.decirUnaFrase(fueraDeContexto, respuesta, miTema, participante, modoDeResolucionDeResultadosFinales));
				ponerComoYaTratado(miTema);
				
				temasPendientes.borrarLosTemasPendientes();

				decirTemaPreguntarPorOtraCosa(misSalidas, respuesta, "watson");
				
			}else if(agente.obtenerNombreDeLaIntencionGeneralActiva().equals(Constantes.INTENCION_NO_ENTIENDO)){
				decirTemaNoEntendi(misSalidas, respuesta);
				temasPendientes.borrarLosTemasPendientes();
				
			}else if(agente.obtenerNombreDeLaIntencionGeneralActiva().equals(Constantes.INTENCION_DESPISTADOR)){
				System.out.println("Quiere despistar  ...");
				miTema = this.temario.buscarTemaPorLaIntencion(Constantes.INTENCION_DESPISTADOR);
				String nombreFrase = obtenerUnaFraseAfirmativa(Constantes.FRASES_INTENCION_DESPISTADOR);
				
				Afirmacion despistar = (Afirmacion)  this.temario.frase(nombreFrase);
				misSalidas.add(agente.decirUnaFrase(despistar, respuesta, miTema, participante, modoDeResolucionDeResultadosFinales));
				ponerComoYaTratado(despistar);
				
				temasPendientes.borrarLosTemasPendientes();
				
			}else if(agente.obtenerNombreDeLaIntencionGeneralActiva().equals(Constantes.INTENCION_REPETIR_ULTIMA_FRASE)){
				System.out.println("Quiere repetir  ...");
				String idFrase = obtenerUnaFraseAfirmativa(Constantes.FRASES_INTENCION_REPETIR);
				
				Afirmacion conjuncion = (Afirmacion) this.temario.frase(idFrase);

				if(!miUltimaSalida.get(0).getFraseActual().obtenerNombreDeLaFrase().equals(idFrase))
					miUltimaSalida.add(0, agente.decirUnaFrase(conjuncion, respuesta, temaActual, participante, modoDeResolucionDeResultadosFinales));
				for(Salida salida: miUltimaSalida){
					misSalidas.add(agente.decirUnaFrase(salida.getFraseActual(), respuesta, temaActual, participante, modoDeResolucionDeResultadosFinales));
				}
	
				temasPendientes.borrarLosTemasPendientes();
				
			}else if(agente.obtenerNombreDeLaIntencionGeneralActiva().equals(Constantes.INTENCION_AGRADECIMIENTO)){
				System.out.println("Esta agradeciendo ...");							
				miTema = this.temario.buscarTemaPorLaIntencion(Constantes.INTENCION_AGRADECIMIENTO);

				String frase = obtenerUnaFraseAfirmativa(Constantes.FRASES_INTENCION_AGRADECIMIENTO);
				Afirmacion queQuiere = (Afirmacion) this.temario.frase(frase);
				misSalidas.add(agente.decirUnaFrase(queQuiere, respuesta, miTema, participante, modoDeResolucionDeResultadosFinales));
				ponerComoYaTratado(queQuiere);
			
			}else if(agente.obtenerNombreDeLaIntencionGeneralActiva().equals(Constantes.INTENCION_QUE_PUEDEN_PREGUNTAR)){
				System.out.println("Quiere saber que hago ...");							
				miTema = this.temario.buscarTemaPorLaIntencion(Constantes.INTENCION_QUE_PUEDEN_PREGUNTAR);

				String frase = obtenerUnaFraseAfirmativa(Constantes.FRASES_INTENCION_QUE_PUEDEN_PREGUNTAR);
				Afirmacion queQuiere = (Afirmacion) this.temario.frase(frase);
				misSalidas.add(agente.decirUnaFrase(queQuiere, respuesta, miTema, participante, modoDeResolucionDeResultadosFinales));
				ponerComoYaTratado(queQuiere);
				
				decirTemaPreguntarPorOtraCosa(misSalidas, respuesta, "watson");
			}
			
			return true;
		}else{
			return false;
		}
	}
	
	private void agregarVariablesDeContextoDelClienteAWatson(Tema tema){
		if(tema == null){
			return;
		}
		List<String> misValiables = tema.obtenerVariablesDeContextoQueElTemaOcupa();
		if(! misValiables.isEmpty()){
			for (String variable: misValiables){
				if(variable.equals("estaLogueado")){
					/*if (participante != null){
						try {
							boolean estaLogueado = this.participante.obtenerEstadoDeLogeo();
							agente.activarValiableEnElContextoDeWatson("estaLogueado", String.valueOf(estaLogueado));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							System.out.println("Error al activar contexto en Watson: "+e.getMessage());
						}
					}else{
						agente.activarValiableEnElContextoDeWatson("estaLogueado", "false");
					}*/
				}
			}
		}
	}
	
	private void extraerOracionesAfirmarivasYPreguntas(ArrayList<Salida> misSalidas, Respuesta respuesta, String idFraseActivada){
		extraerOracionesAfirmarivasYPreguntasDeWorkspaceEspecifico(misSalidas, respuesta, idFraseActivada, false);
	}
	
	private void extraerOracionesAfirmarivasYPreguntas(ArrayList<Salida> misSalidas, Respuesta respuesta, String idFraseActivada, Tema tema){
		extraerOracionesAfirmarivasYPreguntasDeWorkspaceEspecifico(misSalidas, respuesta, idFraseActivada, false, tema);
	}
	
	private void extraerOracionesAfirmarivasYPreguntasDeWorkspaceEspecifico(ArrayList<Salida> misSalidas, Respuesta respuesta, String idFraseActivada, Boolean estaEnWorkSpaceEspecifico){
		extraerOracionesAfirmarivasYPreguntasDeWorkspaceEspecifico(misSalidas, respuesta, idFraseActivada, estaEnWorkSpaceEspecifico, temaActual);
	}
	
	private void extraerOracionesAfirmarivasYPreguntasDeWorkspaceEspecifico(ArrayList<Salida> misSalidas, Respuesta respuesta, String idFraseActivada, Boolean estaEnWorkSpaceEspecifico, Tema tema){
		Pregunta miPregunta = null;
		agregarOracionesAfirmativasDeWorkspaceEspecifico(misSalidas, respuesta.obtenerLosNombresDeLasOracionesAfirmativasActivas(), respuesta, estaEnWorkSpaceEspecifico);
		if( ! idFraseActivada.equals("")){
			
			if(estaEnWorkSpaceEspecifico){
				miPregunta = (Pregunta) this.temaActualDelWorkSpaceEspecifico.buscarUnaFrase(idFraseActivada);
				misSalidas.add(agente.decirUnaFrase(miPregunta, respuesta, temaActualDelWorkSpaceEspecifico, participante, modoDeResolucionDeResultadosFinales));
			}else{
				miPregunta = (Pregunta) tema.buscarUnaFrase(idFraseActivada);
				misSalidas.add(agente.decirUnaFrase(miPregunta, respuesta, tema, participante, modoDeResolucionDeResultadosFinales));
				fraseActual = miPregunta;
			}
			ponerComoYaTratado(miPregunta);
		}
	}
	
	private void agregarOracionesAfirmativasDeWorkspaceEspecifico(ArrayList<Salida> misSalidas, List<String> afirmativas, Respuesta respuesta, boolean estaEnWorkSpaceEspecifico){
		Afirmacion miAfirmacion = null;
		if(afirmativas != null && respuesta != null){
			for(int index = 0; index < afirmativas.size(); index++){
				if(estaEnWorkSpaceEspecifico){
					miAfirmacion = (Afirmacion) this.temaActualDelWorkSpaceEspecifico.buscarUnaFrase(afirmativas.get(index));
					if( ! yaExisteEstaSalida(misSalidas, miAfirmacion.obtenerNombreDeLaFrase()) ){
						misSalidas.add(agente.decirUnaFrase(miAfirmacion, respuesta, temaActualDelWorkSpaceEspecifico, participante, modoDeResolucionDeResultadosFinales));
					}
				}else{
					miAfirmacion = (Afirmacion) this.temaActual.buscarUnaFrase(afirmativas.get(index));
					if( ! yaExisteEstaSalida(misSalidas, miAfirmacion.obtenerNombreDeLaFrase()) ){
						misSalidas.add(agente.decirUnaFrase(miAfirmacion, respuesta, temaActual, participante, modoDeResolucionDeResultadosFinales));
						fraseActual = miAfirmacion;
					}
				}
				ponerComoYaTratado(miAfirmacion);
			}
		}
	}
	
	private boolean yaExisteEstaSalida(ArrayList<Salida> misSalidas, String idFrase){
		boolean resultado = false;
		
		for(int index = 0; index < misSalidas.size(); index ++){
			if(misSalidas.get(index).getFraseActual().obtenerNombreDeLaFrase().equals(idFrase)){
				resultado = true;
				break;
			}
		}
		
		return resultado;
	}
	
	private void ponerComoYaTratado(Frase frase){
		
		if ( ! hilo.existeTema(temaActual)){ //si quiere que solo lo cuente una vez
			estadisticasTemasTratados.darSeguimiento(temaActual);
		}
		hilo.ponerComoDichoEsta(frase);
	}
	
	private void ponerComoYaTratado(Tema tema){
		
		if ( ! hilo.existeTema(tema)){ //si quiere que solo lo cuente una vez
			estadisticasTemasTratados.darSeguimiento(tema);
		}
		
		if(tema.sePuedeRepetir()){
			hilo.ponerComoDichoEste(tema);
		}else{
			hilo.noPuedoRepetir(tema);
		}
	}
	
	public void guardarEstadiscitas(String idSesion) throws ClassNotFoundException, SQLException{
		estadisticasTemasTratados.guardarEstadiscitasEnBaseDeDatos(idSesion);
	}
	
	public Date obtenerLaFechaDelUltimoRegistroDeLaConversacion(){
		return fechaDelUltimoRegistroDeLaConversacion;
	}
	
	private String obtenerUnaFraseAfirmativa(String[] frases){
		String frase = frases[0];
		int n = (int)Math.floor(Math.random()*frases.length);
		if(this.temario.frase(frases[n]).esUnaOracionAfirmativa())
				frase = frases[n];
		else 
			obtenerUnaFraseAfirmativa(frases);
		return frase;
	}
	
	private String obtenerUnaFraseTipoPregunta(String[] frases){
		String frase = frases[0];
		int n = (int)Math.floor(Math.random()*frases.length);
		if(this.temario.frase(frases[n]).esUnaPregunta())
				frase = frases[n];
		else 
			obtenerUnaFraseAfirmativa(frases);
		return frase;
	}
	
	private String obtenerUnaFraseDespedida(String[] frases){
		String frase = frases[0];
		int n = (int)Math.floor(Math.random()*frases.length);
		if(this.temario.frase(frases[n]).esUnaDespedida())
				frase = frases[n];
		else obtenerUnaFraseAfirmativa(frases);
		return frase;
	}
}
