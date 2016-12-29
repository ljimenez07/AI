package com.ncubo.chatbot.consola;

import java.sql.SQLException;
import java.util.ArrayList;
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
import com.ncubo.chatbot.partesDeLaConversacion.Temario;
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
	private Tema temaActual;
	private Frase fraseActual = null;
	private Tema temaActualDelWorkSpaceEspecifico = null;
	private Frase fraseActualDelWorkSpaceEspecifico = null;
	private boolean hayUnWorkspaceEspecifico = false;
	private Estadisticas estadisticasTemasTratados;
	
	public Conversacion(Temario temario, Cliente participante, ConsultaDao consultaDao){
		// Hacer lamdaba para agregar los participantes
		//this.participantes = new Participantes();
		this.participante = participante;
		this.agente = new Agente(temario.contenido().getMiWorkSpaces());
		this.agente.manifestarseEnFormaOral();
		this.agente.manifestarseEnFormaVisual();
		
		this.hilo = new HiloDeLaConversacion();
		//this.participantes.agregar(agente).agregar(participante);
		this.temario = temario;
		estadisticasTemasTratados = new Estadisticas(consultaDao);
	}
	
	public void cambiarParticipante(Cliente participante){
		this.participante = participante;
	}
	
	public Cliente obtenerElParticipante(){
		return this.participante;
	}
	
	public ArrayList<Salida> inicializarLaConversacion(){
		ArrayList<Salida> misSalidas = new ArrayList<Salida>();
		
		System.out.println("");
		System.out.println("Iniciar conversacion ...");
		System.out.println("");
		
		this.temaActual = this.temario.buscarTema(Constantes.FRASE_SALUDO);
		
		Saludo saludoGeneral = (Saludo) this.temario.extraerFraseDeSaludoInicial(CaracteristicaDeLaFrase.esUnSaludo);
		misSalidas.add(agente.decir(saludoGeneral, null, temaActual));
		ponerComoYaTratado(saludoGeneral);
		
		Pregunta queQuiere = (Pregunta) this.temario.extraerFraseDeSaludoInicial(CaracteristicaDeLaFrase.esUnaPregunta);
		misSalidas.add(agente.decir(queQuiere, null, temaActual));
		fraseActual = queQuiere;
		ponerComoYaTratado(queQuiere);
		return misSalidas;
	}
	
	public ArrayList<Salida> analizarLaRespuestaConWatson(String respuestaDelCliente) throws Exception{
		ArrayList<Salida> misSalidas = new ArrayList<Salida>();
		
		Respuesta respuesta = agente.enviarRespuestaAWatson(respuestaDelCliente, fraseActual);
		this.hilo.agregarUnaRespuesta(respuesta);

		if (respuesta.hayProblemasEnLaComunicacionConWatson()){
			Afirmacion errorDeComunicacionConWatson = (Afirmacion) this.temario.contenido().frase(Constantes.FRASE_ERROR_CON_WATSON);
			misSalidas.add(agente.decir(errorDeComunicacionConWatson, respuesta, temaActual));
			fraseActual = errorDeComunicacionConWatson;
			ponerComoYaTratado(errorDeComunicacionConWatson);
		}else{
			if(! verificarIntencionNoAsociadaANingunWorkspace(misSalidas, respuesta)){
				String idFraseActivada = respuesta.obtenerFraseActivada();
				if(respuesta.cambiarAGeneral()){
					extraerOracionesAfirmarivasYPreguntas(misSalidas, respuesta, idFraseActivada);
					//this.temaActual = this.temario.buscarTema(Constantes.FRASE_SALUDO);
					agente.cambiarAWorkspaceGeneral();
					
					if (misSalidas.isEmpty()){
						return analizarLaRespuestaConWatson(respuestaDelCliente);
					}
				}else{
					if(agente.hayQueCambiarDeTema()){
						
						idFraseActivada = respuesta.obtenerFraseActivada();
						extraerOracionesAfirmarivasYPreguntas(misSalidas, respuesta, idFraseActivada);
						String laIntencion = agente.obtenernombreDeLaIntencionEspecificaActiva();
						
						if(agente.seTieneQueAbordarElTema()){
							agente.yaNoSeTieneQueAbordarElTema();
							misSalidas.add(agente.volverAPreguntarConMeRindo(fraseActual, respuesta, temaActual, true));
						}
						
						this.temaActual = this.temario.proximoTemaATratar(temaActual, hilo.verTemasYaTratadosYQueNoPuedoRepetir(), agente.obtenerNombreDelWorkspaceActual(), laIntencion);
						agente.yaNoCambiarDeTema();
						agregarVariablesDeContextoDelClienteAWatson(temaActual);
						if(this.temaActual == null){ // Ya no hay mas temas	
							this.temaActual = this.temario.buscarTema(Constantes.FRASE_SALUDO);
							
							if(this.temario.buscarTema(agente.obtenerNombreDelWorkspaceActual(), laIntencion) == null && ! laIntencion.equals("afirmacion") && ! laIntencion.equals("negacion")){
								agente.cambiarAWorkspaceGeneral();
							}
						}else{
							if (idFraseActivada.equals("")){ // Quiere decir que no hay ninguna pregunta en la salida
								System.out.println("El proximo tema a tratar es: "+this.temaActual.obtenerIdTema());
								
								// Activar en el contexto el tema
								agente.activarTemaEnElContextoDeWatson(this.temaActual.obtenerIdTema());
								
								// llamar a watson y ver que bloque se activo
								respuesta = agente.inicializarTemaEnWatson(respuestaDelCliente);
								idFraseActivada = agente.obtenerNodoActivado(respuesta.messageResponse());
								
								System.out.println("Id de la frase a decir: "+idFraseActivada);
								extraerOracionesAfirmarivasYPreguntas(misSalidas, respuesta, idFraseActivada);
							}
						}
						if(this.temaActual != null){
							if( (! this.temaActual.obtenerIdTema().equals(Constantes.FRASE_SALUDO)) && (! this.temaActual.obtenerIdTema().equals(Constantes.FRASE_DESPEDIDA)) )
								ponerComoYaTratado(this.temaActual);
						}
					}else{
						if (agente.entendiLaUltimaPregunta()){
							
							idFraseActivada = respuesta.obtenerFraseActivada();
							extraerOracionesAfirmarivasYPreguntas(misSalidas, respuesta, idFraseActivada);
							
						}else{ 
							// Verificar que fue	
							System.out.println("No entendi la ultima pregunta");
							if(fraseActual.esMandatorio()){
								misSalidas.add(agente.volverAPreguntar(fraseActual, respuesta, temaActual));
							}
						}
					}
					
					if(respuesta.seTerminoElTema()){
						Tema miTema = this.temario.proximoTemaATratar(temaActual, hilo.verTemasYaTratadosYQueNoPuedoRepetir(), agente.obtenerNombreDelWorkspaceActual(), agente.obtenernombreDeLaIntencionEspecificaActiva());
						if(miTema == null){
							this.temaActual = this.temario.buscarTema(Constantes.FRASE_SALUDO);
							agente.borrarUnaVariableDelContexto(Constantes.TERMINO_EL_TEMA);
							agente.cambiarAWorkspaceGeneral();
						}
					}
				}
			}
		}
		
		if(misSalidas.isEmpty()){
			decirTemaNoEntendi(misSalidas, respuesta);
		}
		
		return misSalidas;
	}
	
	
	private void decirTemaNoEntendi(ArrayList<Salida> misSalidas, Respuesta respuesta){
		System.out.println("No entendi bien ...");
		this.temaActual = this.temario.buscarTema(Constantes.INTENCION_NO_ENTIENDO);
		
		Afirmacion fueraDeContexto = (Afirmacion) this.temaActual.buscarUnaFrase(Constantes.INTENCION_NO_ENTIENDO);
		misSalidas.add(agente.decir(fueraDeContexto, respuesta, temaActual));
		fraseActual = fueraDeContexto;
		ponerComoYaTratado(this.temaActual);
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
	
	private boolean verificarIntencionNoAsociadaANingunWorkspace(ArrayList<Salida> misSalidas, Respuesta respuesta) throws Exception{
		if(agente.hayIntencionNoAsociadaANingunWorkspace()){
			if(agente.obtenerNombreDeLaIntencionGeneralActiva().equals(Constantes.INTENCION_SALUDAR)){
				System.out.println("Quiere saludar ...");
				this.temaActual = this.temario.buscarTema(Constantes.FRASE_SALUDO);

				Afirmacion saludar = (Afirmacion) this.temaActual.buscarUnaFrase("saludar");
				misSalidas.add(agente.decir(saludar, respuesta, temaActual));
				
				ponerComoYaTratado(saludar);
				
				Pregunta queQuiere = (Pregunta) this.temario.extraerFraseDeSaludoInicial(CaracteristicaDeLaFrase.esUnaPregunta);
				misSalidas.add(agente.decir(queQuiere, respuesta, temaActual));
				fraseActual = queQuiere;
				ponerComoYaTratado(queQuiere);
				
			}else if(agente.obtenerNombreDeLaIntencionGeneralActiva().equals(Constantes.INTENCION_DESPEDIDA)){
				System.out.println("Quiere despedirce ...");
				this.temaActual = this.temario.buscarTema(Constantes.FRASE_DESPEDIDA);
				
				Despedida saludar = (Despedida) this.temaActual.buscarUnaFrase(Constantes.FRASE_DESPEDIDA);
				misSalidas.add(agente.decir(saludar, respuesta, temaActual));
				fraseActual = saludar;
				ponerComoYaTratado(saludar);
				
				Afirmacion despedidaCerrarSesion = (Afirmacion) this.temaActual.buscarUnaFrase("despedidaCerrarSesion");
				misSalidas.add(agente.decir(despedidaCerrarSesion, respuesta, temaActual));
				fraseActual = despedidaCerrarSesion;
				ponerComoYaTratado(despedidaCerrarSesion);
				
			}else if(agente.obtenerNombreDeLaIntencionGeneralActiva().equals(Constantes.INTENCION_FUERA_DE_CONTEXTO) ||
					agente.obtenerNombreDeLaIntencionGeneralActiva().equals(Constantes.INTENCION_QUE_PUEDEN_PREGUNTAR)){
				System.out.println("Esta fuera de contexto ...");
				this.temaActual = this.temario.buscarTema(Constantes.FRASE_FUERA_DE_CONTEXTO);
				
				Afirmacion fueraDeContexto = (Afirmacion) this.temaActual.buscarUnaFrase("fueraDeContextoGeneral");
				misSalidas.add(agente.decir(fueraDeContexto, respuesta, temaActual));
				fraseActual = fueraDeContexto;
				ponerComoYaTratado(this.temaActual);
				
			}else if(agente.obtenerNombreDeLaIntencionGeneralActiva().equals(Constantes.INTENCION_NO_ENTIENDO)){
				decirTemaNoEntendi(misSalidas, respuesta);
				
			} else if(agente.obtenerNombreDeLaIntencionGeneralActiva().equals(Constantes.INTENCION_DESPISTADOR)){
				System.out.println("Quiere despistar  ...");

				Afirmacion despistar = (Afirmacion)  this.temario.frase(Constantes.INTENCION_DESPISTADOR);
				misSalidas.add(agente.decir(despistar, respuesta, temaActual));
				fraseActual = despistar;
				ponerComoYaTratado(despistar);
				
			}
			return true;
		}else{
			return false;
		}
	}
	
	private void extraerOracionesAfirmarivasYPreguntas(ArrayList<Salida> misSalidas, Respuesta respuesta, String idFraseActivada){
		extraerOracionesAfirmarivasYPreguntasDeWorkspaceEspecifico(misSalidas, respuesta, idFraseActivada, false);
	}
	
	private void extraerOracionesAfirmarivasYPreguntasDeWorkspaceEspecifico(ArrayList<Salida> misSalidas, Respuesta respuesta, String idFraseActivada, Boolean estaEnWorkSpaceEspecifico){
		Pregunta miPregunta = null;
		agregarOracionesAfirmativasDeWorkspaceEspecifico(misSalidas, respuesta.obtenerLosIDsDeLasOracionesAfirmativasActivas(), respuesta, estaEnWorkSpaceEspecifico);
		if( ! idFraseActivada.equals("")){
			
			if(estaEnWorkSpaceEspecifico){
				miPregunta = (Pregunta) this.temaActualDelWorkSpaceEspecifico.buscarUnaFrase(idFraseActivada);
				misSalidas.add(agente.decir(miPregunta, respuesta, temaActualDelWorkSpaceEspecifico));
				fraseActualDelWorkSpaceEspecifico = miPregunta;
			}else{
				miPregunta = (Pregunta) this.temaActual.buscarUnaFrase(idFraseActivada);
				misSalidas.add(agente.decir(miPregunta, respuesta, temaActual));
				fraseActual = miPregunta;
			}
			ponerComoYaTratado(miPregunta);
		}
	}
	
	private void extraerOracionesAfirmarivasYPreguntasDeWorkspaceEspecificoSinActualizar(ArrayList<Salida> misSalidas, Respuesta respuesta, String idFraseActivada, Boolean estaEnWorkSpaceEspecifico){
		Pregunta miPregunta = null;
		agregarOracionesAfirmativasDeWorkspaceEspecificoSinActualizar(misSalidas, respuesta.obtenerLosIDsDeLasOracionesAfirmativasActivas(), respuesta, estaEnWorkSpaceEspecifico);
		if( ! idFraseActivada.equals("")){
			
			if(estaEnWorkSpaceEspecifico){
				miPregunta = (Pregunta) this.temaActualDelWorkSpaceEspecifico.buscarUnaFrase(idFraseActivada);
				misSalidas.add(agente.decir(miPregunta, respuesta, temaActualDelWorkSpaceEspecifico));
			}else{
				miPregunta = (Pregunta) this.temaActual.buscarUnaFrase(idFraseActivada);
				misSalidas.add(agente.decir(miPregunta, respuesta, temaActual));
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
					if( ! yaExisteEstaSalida(misSalidas, miAfirmacion.getIdFrase()) ){
						misSalidas.add(agente.decir(miAfirmacion, respuesta, temaActualDelWorkSpaceEspecifico));
						fraseActualDelWorkSpaceEspecifico = miAfirmacion;
					}
				}else{
					miAfirmacion = (Afirmacion) this.temaActual.buscarUnaFrase(afirmativas.get(index));
					if( ! yaExisteEstaSalida(misSalidas, miAfirmacion.getIdFrase()) ){
						misSalidas.add(agente.decir(miAfirmacion, respuesta, temaActual));
						fraseActual = miAfirmacion;
					}
				}
				ponerComoYaTratado(miAfirmacion);
			}
		}
	}
	
	private void agregarOracionesAfirmativasDeWorkspaceEspecificoSinActualizar(ArrayList<Salida> misSalidas, List<String> afirmativas, Respuesta respuesta, boolean estaEnWorkSpaceEspecifico){
		Afirmacion miAfirmacion = null;
		if(afirmativas != null && respuesta != null){
			for(int index = 0; index < afirmativas.size(); index++){
				if(estaEnWorkSpaceEspecifico){
					miAfirmacion = (Afirmacion) this.temaActualDelWorkSpaceEspecifico.buscarUnaFrase(afirmativas.get(index));
					if( ! yaExisteEstaSalida(misSalidas, miAfirmacion.getIdFrase()) ){
						misSalidas.add(agente.decir(miAfirmacion, respuesta, temaActualDelWorkSpaceEspecifico));
					}
				}else{
					miAfirmacion = (Afirmacion) this.temaActual.buscarUnaFrase(afirmativas.get(index));
					if( ! yaExisteEstaSalida(misSalidas, miAfirmacion.getIdFrase()) ){
						misSalidas.add(agente.decir(miAfirmacion, respuesta, temaActual));
					}
				}
				ponerComoYaTratado(miAfirmacion);
			}
		}
	}
	
	private boolean yaExisteEstaSalida(ArrayList<Salida> misSalidas, String idFrase){
		boolean resultado = false;
		
		for(int index = 0; index < misSalidas.size(); index ++){
			if(misSalidas.get(index).getFraseActual().getIdFrase().equals(idFrase)){
				resultado = true;
				break;
			}
		}
		
		return resultado;
	}
	
	private void ponerComoYaTratado(Frase frase){
		hilo.ponerComoDichoEsta(frase);
	}
	
	private void ponerComoYaTratado(Tema tema)
	{
		if ( ! hilo.existeTema(temaActual)){ //si quiere que solo lo cuente una vez
			estadisticasTemasTratados.darSeguimiento(temaActual);
		}
		if(tema.sePuedeRepetir()){
			hilo.ponerComoDichoEste(tema);
		}else{
			hilo.noPuedoRepetir(tema);
		}
	}
	
	private void ponerComoYaTratadoTemaEspecifico(Tema tema)
	{
		if ( ! hilo.existeTemaEspecifico(temaActualDelWorkSpaceEspecifico)){ //si quiere que solo lo cuente una vez
			estadisticasTemasTratados.darSeguimiento(temaActualDelWorkSpaceEspecifico);
		}
		
		hilo.noPuedoRepetirTemaEspecifico(temaActualDelWorkSpaceEspecifico);
	}
	
	public void borrarTemasEspecificosYaDichos(){
		hilo.borrarTemasEspecificosYaDichos();
	}
	
	public void guardarEstadiscitas(String idSesion) throws ClassNotFoundException, SQLException
	{
		estadisticasTemasTratados.guardarEstadiscitasEnBaseDeDatos(idSesion);
	}
	
}

