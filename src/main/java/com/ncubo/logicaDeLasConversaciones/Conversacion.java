package com.ncubo.logicaDeLasConversaciones;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.json.JSONException;
import org.json.JSONObject;
import com.ibm.watson.developer_cloud.conversation.v1.model.Intent;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.RetrieveAndRank;
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
import com.ncubo.chatbot.partesDeLaConversacion.TemasPendientesDeAbordar;
import com.ncubo.chatbot.participantes.Agente;
import com.ncubo.chatbot.participantes.Cliente;
import com.ncubo.db.ConsultaDao;
import com.ncubo.email.Email;
import com.ncubo.email.GeneradorDeEmails;
import com.ncubo.estadisticas.Estadisticas;
import com.ncubo.retrieve_and_rank.v1.model.RankResult;
import com.ncubo.retrieve_and_rank.v1.model.SolrResult;
import com.ncubo.retrieve_and_rank.v1.model.SolrResults;
import com.ncubo.retrieve_and_rank.v1.payload.QueryRequestPayload;
import com.ncubo.retrieve_and_rank.v1.payload.QueryResponsePayload;
import com.ncubo.retrieve_and_rank.v1.utils.HttpSolrClientUtils;
import com.ncubo.retrieve_and_rank.v1.utils.SolrUtils;

public class Conversacion {

	//private Participantes participantes;
	private Cliente participante;
	private HiloDeLaConversacion hilo; // Mantiene el contexto, osea todas las intenciones y entidades, sabe que se dijo 
	//private Temario temario;
	private Agente agente;
	private Tema temaActual = null;
	private Frase fraseActual = null;
	private Estadisticas estadisticasTemasTratados;
	private ArrayList<Salida> miUltimaSalida;
	private final Constantes.ModoDeLaVariable modoDeResolucionDeResultadosFinales;
	private Date fechaDelUltimoRegistroDeLaConversacion;
	private final TemasPendientesDeAbordar temasPendientes;
	private Email email;
	private final InformacionDelCliente informacionDelCliente;
	private static HttpSolrClient solrClient;
	private static RetrieveAndRank service = new RetrieveAndRank(); 
	
	private String userRetrieveAndRank;
	private String passwordRetrieveAndRank;
	private String collectionName;
	private String clusterId;
	private String rankerId;
	private boolean seActivoElRetrieveAndRank = false;
	
	public Conversacion(Cliente participante, ConsultaDao consultaDao, Agente miAgente, InformacionDelCliente cliente, 
			String user, String password, String cluster, String collection, String ranker){
		// Hacer lamdaba para agregar los participantes
		//this.participantes = new Participantes();
		this.informacionDelCliente = cliente;
		temasPendientes = new TemasPendientesDeAbordar();
		this.participante = participante;
		//this.agente = new Agente(temario.contenido().getMiWorkSpaces());
		this.agente = miAgente;
		this.agente.manifestarseEnFormaOral();
		this.agente.manifestarseEnFormaVisual();
		this.modoDeResolucionDeResultadosFinales = this.agente.obtenerTemario().contenido().obtenerModoDeTrabajo();
		this.hilo = new HiloDeLaConversacion();
		//this.participantes.agregar(agente).agregar(participante);
		//this.temarios = temarios;
		estadisticasTemasTratados = new Estadisticas(consultaDao);
		miUltimaSalida = new ArrayList<>();
		fechaDelUltimoRegistroDeLaConversacion = Calendar.getInstance().getTime();
		email = new Email();
		//temario = temarios.get(0);
		this.userRetrieveAndRank = user;
		this.passwordRetrieveAndRank = password;
		this.collectionName = collection;
		this.rankerId = ranker;
		this.clusterId = cluster;
	}
	
	public void cambiarParticipante(Cliente participante){
		this.participante = participante;
	}
	
	public Cliente obtenerElParticipante(){
		return this.participante;
	}
	
	public Agente obtenerAgente(){
		return this.agente;
	}
	
	public ArrayList<Salida> inicializarLaConversacion(){
		ArrayList<Salida> misSalidas = new ArrayList<Salida>();
		
		System.out.println("");
		System.out.println("Iniciar conversacion ...");
		System.out.println("");
		
		this.temaActual = this.agente.obtenerTemario().buscarTemaPorLaIntencion(Constantes.INTENCION_SALUDAR);
		
		Saludo saludoGeneral = (Saludo) this.agente.obtenerTemario().extraerFraseDeSaludoInicial(CaracteristicaDeLaFrase.esUnSaludo);
		misSalidas.add(agente.decirUnaFrase(saludoGeneral, null, temaActual, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente()));
		ponerComoYaTratado(this.temaActual, saludoGeneral);
		
		Pregunta queQuiere = (Pregunta) this.agente.obtenerTemario().extraerFraseDeSaludoInicial(CaracteristicaDeLaFrase.esUnaPregunta);
		misSalidas.add(agente.decirUnaFrase(queQuiere, null, temaActual, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente()));
		
		ponerComoYaTratado(this.temaActual, queQuiere);
		fechaDelUltimoRegistroDeLaConversacion = Calendar.getInstance().getTime();
		
		misSalidas = agregarSalidasAlHistorico(misSalidas, fechaDelUltimoRegistroDeLaConversacion);
		
		miUltimaSalida = misSalidas;
		
		
		agente.cambiarANivelSuperior();
		
		return misSalidas;
	}
	
	public ArrayList<Salida> analizarLaRespuestaConWatson(String respuestaDelCliente, boolean esModoConsulta) throws Exception{
		ArrayList<Salida> misSalidas = new ArrayList<Salida>();
		Respuesta respuesta = null;
		seActivoElRetrieveAndRank = false;
		
		boolean hayTemaActualDiciendose = this.temaActual != null;
		if(hayTemaActualDiciendose){
			respuesta = agente.enviarRespuestaAWatson(respuestaDelCliente, fraseActual);
			this.hilo.agregarUnaRespuesta(respuesta);
	
			if (respuesta.hayProblemasEnLaComunicacionConWatson()){
				String nombreFrase = obtenerUnaFraseAfirmativa(Constantes.FRASES_INTENCION_ERROR_CON_WATSON);
				Afirmacion errorDeComunicacionConWatson = (Afirmacion) this.agente.obtenerTemario().contenido().frase(nombreFrase);
				misSalidas.add(agente.decirUnaFrase(errorDeComunicacionConWatson, respuesta, temaActual, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente()));
				ponerComoYaTratado(this.temaActual, errorDeComunicacionConWatson);
			}else{
				String idFraseActivada = respuesta.obtenerFraseActivada();
				if(! verificarIntencionNoAsociadaANingunWorkspace(misSalidas, respuesta,respuestaDelCliente)){
					respuesta = analizarResultadosDelAgente(misSalidas, idFraseActivada, respuesta, respuestaDelCliente);
				}else{
					if(! hayAlgunaPreguntaEnLasSalidas(misSalidas) && temasPendientes.hayTemasPendientes()){
						TemaPendiente temaPendiente = temasPendientes.extraerElSiquienteTema();
						this.temaActual = temaPendiente.getTemaActual();
						this.fraseActual = temaPendiente.getFraseActual();
						agente.setMiTopico(temaPendiente.getMiTopico());
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
				agente.setMiTopico(temaPendiente.getMiTopico());
				agente.cambiarElContexto(temaPendiente.getContextoCognitivo());
				volverlARetomarUnTema(misSalidas, respuesta);
			}else{
				agente.cambiarANivelSuperior();
				respuesta = agente.enviarRespuestaAWatson(respuestaDelCliente, fraseActual);
				if(esModoConsulta){
					if(! verificarIntencionNoAsociadaANingunWorkspace(misSalidas, respuesta, respuestaDelCliente)){
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
							agente.setMiTopico(temaPendiente.getMiTopico());
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
			agente.setMiTopico(temaPendiente.getMiTopico());
			agente.cambiarElContexto(temaPendiente.getContextoCognitivo());
			volverlARetomarUnTema(misSalidas, respuesta);
		}else{
			if(! hayAlgunaPreguntaEnLasSalidas(misSalidas) && esModoConsulta && respuesta.seTerminoElTema() && 
					! temasPendientes.hayTemasPendientes() && ! existeLaFraseEnLasSalidas(misSalidas, "noQuiereHacerOtraConsulta") && ! hayAlgunaDespedidaEnLasSalidas(misSalidas)) // && ! existeLaFraseEnLasSalidas(misSalidas, obtenerUnaFraseDespedida(Constantes.FRASES_INTENCION_DESPEDIDA))
				decirTemaPreguntarPorOtraCosa(misSalidas, respuesta, respuestaDelCliente);
		}
		
		if(misSalidas.isEmpty()){
			decirTemaNoEntendi(misSalidas, respuesta);
		}
		
		
		fechaDelUltimoRegistroDeLaConversacion = Calendar.getInstance().getTime();
		misSalidas = agregarSalidasAlHistorico(misSalidas, fechaDelUltimoRegistroDeLaConversacion);
		
		miUltimaSalida = misSalidas;
		
		return misSalidas;
	}
	
	private Respuesta analizarResultadosDelAgente(ArrayList<Salida> misSalidas, String idFraseActivada, Respuesta respuesta, String respuestaDelCliente){
		if(agente.seTieneQueAbordarElTema()){
			agente.yaNoSeTieneQueAbordarElTema();
			misSalidas.add(agente.volverAPreguntarUnaFraseConMeRindo(fraseActual, respuesta, temaActual, true, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente()));
			temaActual = null;
		}else{
			if (agente.entendiLaUltimaPregunta()){
				idFraseActivada = respuesta.obtenerFraseActivada();
				extraerOracionesAfirmarivasYPreguntas(misSalidas, respuesta, idFraseActivada);
				
				if(agente.hayQueCambiarDeTema() && misSalidas.isEmpty()){// Hay que buscar un nuevo tema y no he dicho nada aun
					respuesta = cambiarDeTema(idFraseActivada, respuestaDelCliente, misSalidas, respuesta);
					if(respuesta.seTerminoElTema())
						temaActual = null;
				}else if(agente.hayQueCambiarDeTema() && ! misSalidas.isEmpty()){
					temaActual = null;
					agente.cambiarANivelSuperior();
				}
			}else{ 
				if (agente.hayQueCambiarDeTemaForzosamente()){ // TODO Analizar si hay mas de un tema en cola
					if(temaActual != null)
						if(! temaActual.getNombre().equals("preguntarPorOtraConsulta"))
							this.temasPendientes.agregarUnTema(new TemaPendiente(temaActual, fraseActual, agente.getMiUltimoTopico()));
					
					agente.cambiarANivelSuperior();
					respuesta = agente.enviarRespuestaAWatson(respuestaDelCliente, fraseActual);
					
					respuesta = cambiarDeTema(idFraseActivada, respuestaDelCliente, misSalidas, respuesta); 
				}
				else{
					// Verificar que fue lo que paso	
					System.out.println("No entendi la ultima pregunta");
					if(fraseActual != null){
						if(fraseActual.esMandatorio()){
							//analizarRespuestaRetrieveAndRank(respuestaDelCliente, misSalidas, respuesta);
							if(misSalidas.isEmpty())
								misSalidas.add(agente.volverAPreguntarUnaFrase(fraseActual, respuesta, temaActual, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente()));
							else{
								//seActivoElRetrieveAndRank = true;
								if(temaActual != null){
									if(! temaActual.getNombre().equals("preguntarPorOtraConsulta"))
										this.temasPendientes.agregarUnTema(new TemaPendiente(temaActual, fraseActual, agente.getMiTopico()));
									else{
										decirTemaPreguntarPorOtraCosa(misSalidas, respuesta, respuestaDelCliente);
									}
								}else{
									decirTemaPreguntarPorOtraCosa(misSalidas, respuesta, respuestaDelCliente);
								}
							}
						}else{
							temaActual = null;
							fraseActual = null;	
						}
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
	
	private boolean hayAlgunaDespedidaEnLasSalidas(ArrayList<Salida> misSalidas){
		for(Salida miSalida: misSalidas){
			if(miSalida.getFraseActual() instanceof Despedida)
				return true;
		}
		return false;
	}
	
	private boolean existeLaFraseEnLasSalidas(ArrayList<Salida> misSalidas, String idNombeFrase){
		if(!misSalidas.isEmpty())
		for(Salida miSalida: misSalidas){
			if(miSalida.getFraseActual().obtenerNombreDeLaFrase().equals(idNombeFrase))
				return true;
		}
		return false;
	}
	
	private Respuesta cambiarDeTema(String idFraseActivada, String respuestaDelCliente, ArrayList<Salida> misSalidas, Respuesta respuesta){
		String laIntencion = agente.obtenerNombreDeLaIntencionGeneralActiva();
		agregarUnSegundoTemaImportanteADecirComoPendiente(laIntencion, respuestaDelCliente);
		
		Tema temaNuevo = this.agente.obtenerTemario().proximoTemaATratar(temaActual, hilo.verTemasYaTratadosYQueNoPuedoRepetir(), agente.obtenerNombreDelWorkspaceActual(), laIntencion);
		if( temaNuevo != null){
			temaActual = temaNuevo;
			
			TemaPendiente temaPrimitivo = temasPendientes.buscarUnTemaPendiente(temaActual);
			boolean esteTemaEstaPendiente = temaPrimitivo != null;
			if(esteTemaEstaPendiente)
				temasPendientes.borrarUnTemaPendiente(temaPrimitivo);
			
			agente.yaNoCambiarDeTema();
			agregarVariablesDeContextoDelClienteAWatson(temaActual);
			
			if (idFraseActivada.equals("")){ // Quiere decir que no hay ninguna pregunta en la salida
				System.out.println("El proximo tema a tratar es: "+this.temaActual.getIdTema());
				
				// Activar en el contexto el tema
				agente.activarTemaEnElContextoDeWatson(this.temaActual.getNombre());
				
				// llamar a watson y ver que bloque se activo
				respuesta = agente.inicializarTemaEnWatson(respuestaDelCliente, respuesta, true);
				
				if (respuesta.hayProblemasEnLaComunicacionConWatson()){
					String nombreFrase = obtenerUnaFraseAfirmativa(Constantes.FRASES_INTENCION_ERROR_CON_WATSON);
					Afirmacion errorDeComunicacionConWatson = (Afirmacion) this.agente.obtenerTemario().contenido().frase(nombreFrase);
					misSalidas.add(agente.decirUnaFrase(errorDeComunicacionConWatson, respuesta, temaActual, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente()));
					ponerComoYaTratado(this.temaActual, errorDeComunicacionConWatson);
				}else{
					idFraseActivada = agente.obtenerNodoActivado(respuesta.messageResponse());
					System.out.println("Id de la frase a decir: "+idFraseActivada);
					extraerOracionesAfirmarivasYPreguntas(misSalidas, respuesta, idFraseActivada);
				}

			}
		}else{ // No entiendo
			decirTemaNoEntendi(misSalidas, respuesta);
			agente.cambiarANivelSuperior();
		}
		return respuesta;
	}
	
	private void agregarUnSegundoTemaImportanteADecirComoPendiente(String intencionPrincipal, String respuestaDelCliente){
		ArrayList<Intent> misIntencionesDeConfianza = agente.obtenerLasDosUltimasIntencionesDeConfianza();
		if(! misIntencionesDeConfianza.isEmpty()){
			if( ! misIntencionesDeConfianza.get(1).getIntent().equals(intencionPrincipal)){
				Tema temaNuevo = this.agente.obtenerTemario().proximoTemaATratar(temaActual, hilo.verTemasYaTratadosYQueNoPuedoRepetir(), agente.obtenerNombreDelWorkspaceActual(), misIntencionesDeConfianza.get(1).getIntent());
				if( temaNuevo != null){
					TemaPendiente temaPrimitivo = temasPendientes.buscarUnTemaPendiente(temaNuevo);
					boolean esteTemaEstaPendiente = temaPrimitivo != null;
					if( ! esteTemaEstaPendiente){
						// Activar en el contexto el tema
						agente.activarTemaEnElContextoDeWatson(temaNuevo.getNombre());
						
						// llamar a watson y ver que bloque se activo
						Respuesta respuesta = agente.inicializarTemaEnWatson(respuestaDelCliente, null, true);
						
						if ( ! respuesta.hayProblemasEnLaComunicacionConWatson()){
							String idFraseActivada = agente.obtenerNodoActivado(respuesta.messageResponse());
							
							if( ! idFraseActivada.isEmpty()){
								System.out.println("Id de la frase a recordar: "+idFraseActivada);
								Frase miPregunta = (Pregunta) temaNuevo.buscarUnaFrase(idFraseActivada);
								
								String context = respuesta.messageResponse().getContext().toString();
								JSONObject obj = null;
								try {
									obj = new JSONObject(context);
									obj.remove(Constantes.NODO_ACTIVADO);
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								context = obj.toString();
								if(! temaNuevo.getNombre().equals("preguntarPorOtraConsulta")){
									TemaPendiente nuevoTemaPriminivo = new TemaPendiente(temaNuevo, miPregunta, context, agente.getMiUltimoTopico());
									this.temasPendientes.agregarUnTema(nuevoTemaPriminivo);
								}
								agente.seTieneQueGenerarUnNuevoContextoParaWatsonEnElWorkspaceActualConRespaldo();
							}
						}
					}
				}
			}
		}
	}
	
	private void decirTemaPreguntarPorOtraCosa(ArrayList<Salida> misSalidas, Respuesta respuesta, String respuestaDelCliente){
		System.out.println("Se va a preguntar por otra cosa ...");							
		this.temaActual = this.agente.obtenerTemario().buscarTemaPorLaIntencion(Constantes.INTENCION_PREGUNTAR_POR_OTRA_CONSULTA);

		agente.inicializarTemaEnWatson(respuestaDelCliente, respuesta, false);
		
		// Activar en el contexto el tema
		agente.activarTemaEnElContextoDeWatson(this.temaActual.getNombre());
		
		// llamar a watson y ver que bloque se activo
		respuesta = agente.inicializarTemaEnWatson(respuestaDelCliente, respuesta, true);
		
		if (respuesta.hayProblemasEnLaComunicacionConWatson()){
			String nombreFrase = obtenerUnaFraseAfirmativa(Constantes.FRASES_INTENCION_ERROR_CON_WATSON);
			Afirmacion errorDeComunicacionConWatson = (Afirmacion) this.agente.obtenerTemario().contenido().frase(nombreFrase);
			misSalidas.add(agente.decirUnaFrase(errorDeComunicacionConWatson, respuesta, temaActual, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente()));
			ponerComoYaTratado(this.temaActual, errorDeComunicacionConWatson);
		}else{
			String idFraseActivada = agente.obtenerNodoActivado(respuesta.messageResponse());
			System.out.println("Id de la frase a decir: "+idFraseActivada);
			extraerOracionesAfirmarivasYPreguntas(misSalidas, respuesta, idFraseActivada, this.temaActual);
		}
		
		agente.yaNoCambiarANivelSuperior();
	}
	
	private void volverlARetomarUnTema(ArrayList<Salida> misSalidas, Respuesta respuesta){
		decirFraseRecordatoria(misSalidas, respuesta);
		misSalidas.add(agente.decirUnaFrase(fraseActual, respuesta, temaActual, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente()));
		agente.yaNoCambiarANivelSuperior();
	}
	
	private void decirFraseRecordatoria(ArrayList<Salida> misSalidas, Respuesta respuesta){
		System.out.println("Frase recordatoria ...");
		String nombreFrase = obtenerUnaFraseAfirmativa(Constantes.FRASES_INTENCION_RECORDAR_TEMAS);
		
		Afirmacion fraseRecordatoria = (Afirmacion) this.agente.obtenerTemario().frase(nombreFrase);
		misSalidas.add(agente.decirUnaFrase(fraseRecordatoria, respuesta, null, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente()));

	}
	
	private void decirTemaNoEntendi(ArrayList<Salida> misSalidas, Respuesta respuesta){
		System.out.println("No entendi bien ...");
		Tema miTema = this.agente.obtenerTemario().buscarTema(Constantes.INTENCION_NO_ENTIENDO);
		String nombreFrase = obtenerUnaFraseTipoPregunta(Constantes.FRASES_INTENCION_NO_ENTIENDO);
		
		Pregunta fueraDeContexto = (Pregunta) this.agente.obtenerTemario().frase(nombreFrase);
		misSalidas.add(agente.decirUnaFrase(fueraDeContexto, respuesta, miTema, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente()));
		ponerComoYaTratado(miTema, fueraDeContexto);
	}
	
	private boolean verificarIntencionNoAsociadaANingunWorkspace(ArrayList<Salida> misSalidas, Respuesta respuesta, String respuestaDelCliente) throws Exception{
		if(agente.hayIntencionNoAsociadaANingunWorkspace()){
			
			if (temaActual != null && fraseActual != null){
				if(! temaActual.getNombre().equals("preguntarPorOtraConsulta"))
					this.temasPendientes.agregarUnTema(new TemaPendiente(temaActual, fraseActual, agente.getMiUltimoTopico()));
			}
			
			Tema miTema = null;
			if(agente.obtenerNombreDeLaIntencionGeneralActiva().equals(Constantes.INTENCION_SALUDAR)){
				System.out.println("Quiere saludar ...");
				
				String saludo = obtenerUnaFraseAfirmativa(Constantes.FRASES_INTENCION_SALUDAR);
				miTema = this.agente.obtenerTemario().buscarTemaPorLaIntencion(Constantes.INTENCION_SALUDAR);

				Afirmacion saludar = (Afirmacion) miTema.buscarUnaFrase(saludo);
				misSalidas.add(agente.decirUnaFrase(saludar, respuesta, miTema, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente()));
				ponerComoYaTratado(miTema, saludar);
				
				Pregunta queQuiere = (Pregunta) this.agente.obtenerTemario().extraerFraseDeSaludoInicial(CaracteristicaDeLaFrase.esUnaPregunta);
				misSalidas.add(agente.decirUnaFrase(queQuiere, respuesta, miTema, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente()));
				//ponerComoYaTratado(temaActual, queQuiere);
				
				temasPendientes.borrarLosTemasPendientes();
				
			}else if(agente.obtenerNombreDeLaIntencionGeneralActiva().equals(Constantes.INTENCION_DESPEDIDA)){
				System.out.println("Quiere despedirse ...");
				miTema = this.agente.obtenerTemario().buscarTemaPorLaIntencion(Constantes.INTENCION_DESPEDIDA);
				String nombreFrase = obtenerUnaFraseDespedida(Constantes.FRASES_INTENCION_DESPEDIDA);
				
				Despedida saludar = (Despedida) miTema.buscarUnaFrase(nombreFrase);
				misSalidas.add(agente.decirUnaFrase(saludar, respuesta, miTema, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente()));
				ponerComoYaTratado(miTema, saludar);
				
				temasPendientes.borrarLosTemasPendientes();
				
			}else if(agente.obtenerNombreDeLaIntencionGeneralActiva().equals(Constantes.INTENCION_FUERA_DE_CONTEXTO)){
				System.out.println("Esta fuera de contexto ...");
					miTema = this.agente.obtenerTemario().buscarTemaPorLaIntencion(Constantes.INTENCION_FUERA_DE_CONTEXTO);
					String nombreFrase = obtenerUnaFraseAfirmativa(Constantes.FRASES_INTENCION_FUERA_DE_CONTEXTO);
					
					Afirmacion fueraDeContexto = (Afirmacion) miTema.buscarUnaFrase(nombreFrase);
					misSalidas.add(agente.decirUnaFrase(fueraDeContexto, respuesta, miTema, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente()));
					ponerComoYaTratado(miTema, fueraDeContexto);
				
			}else if(agente.obtenerNombreDeLaIntencionGeneralActiva().equals(Constantes.INTENCION_NO_ENTIENDO)){
				decirTemaNoEntendi(misSalidas, respuesta);
			}else if(agente.obtenerNombreDeLaIntencionGeneralActiva().equals(Constantes.INTENCION_DESPISTADOR)){
				System.out.println("Quiere despistar  ...");
				miTema = this.agente.obtenerTemario().buscarTemaPorLaIntencion(Constantes.INTENCION_DESPISTADOR);
				String nombreFrase = obtenerUnaFraseTipoPregunta(Constantes.FRASES_INTENCION_DESPISTADOR);
				
				Pregunta despistar = (Pregunta) this.agente.obtenerTemario().frase(nombreFrase);
				
				misSalidas.add(agente.decirUnaFrase(despistar, respuesta, miTema, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente()));
				ponerComoYaTratado(miTema, despistar);
				
			}else if(agente.obtenerNombreDeLaIntencionGeneralActiva().equals(Constantes.INTENCION_REPETIR_ULTIMA_FRASE)){
				System.out.println("Quiere repetir  ...");
				String idFrase = obtenerUnaFraseAfirmativa(Constantes.FRASES_INTENCION_REPETIR);
				
				Afirmacion conjuncion = (Afirmacion) this.agente.obtenerTemario().frase(idFrase);

				if(!miUltimaSalida.get(0).getFraseActual().obtenerNombreDeLaFrase().equals(idFrase))
					miUltimaSalida.add(0, agente.decirUnaFrase(conjuncion, respuesta, temaActual, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente()));
				for(Salida salida: miUltimaSalida){
					misSalidas.add(agente.decirUnaFrase(salida.getFraseActual(), respuesta, temaActual, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente()));
				}
	
				//temasPendientes.borrarLosTemasPendientes();
				
			}else if(agente.obtenerNombreDeLaIntencionGeneralActiva().equals(Constantes.INTENCION_AGRADECIMIENTO)){
				System.out.println("Esta agradeciendo ...");							
				miTema = this.agente.obtenerTemario().buscarTemaPorLaIntencion(Constantes.INTENCION_AGRADECIMIENTO);

				String frase = obtenerUnaFraseAfirmativa(Constantes.FRASES_INTENCION_AGRADECIMIENTO);
				Afirmacion queQuiere = (Afirmacion) this.agente.obtenerTemario().frase(frase);
				misSalidas.add(agente.decirUnaFrase(queQuiere, respuesta, miTema, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente()));
				ponerComoYaTratado(miTema, queQuiere);
				
			}else if(agente.obtenerNombreDeLaIntencionGeneralActiva().equals(Constantes.INTENCION_QUE_PUEDEN_PREGUNTAR)){
				System.out.println("Quiere saber que hago ...");							
				miTema = this.agente.obtenerTemario().buscarTemaPorLaIntencion(Constantes.INTENCION_QUE_PUEDEN_PREGUNTAR);

				String frase = obtenerUnaFraseAfirmativa(Constantes.FRASES_INTENCION_QUE_PUEDEN_PREGUNTAR);
				Afirmacion queQuiere = (Afirmacion) this.agente.obtenerTemario().frase(frase);
				misSalidas.add(agente.decirUnaFrase(queQuiere, respuesta, miTema, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente()));
				ponerComoYaTratado(miTema, queQuiere);
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
		List<String> misValiables = tema.getVariablesDeContextoQueElTemaOcupa();
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
		extraerOracionesAfirmarivasYPreguntasDeWorkspaceEspecifico(misSalidas, respuesta, idFraseActivada, this.temaActual);
	}
	
	private void extraerOracionesAfirmarivasYPreguntas(ArrayList<Salida> misSalidas, Respuesta respuesta, String idFraseActivada, Tema tema){
		extraerOracionesAfirmarivasYPreguntasDeWorkspaceEspecifico(misSalidas, respuesta, idFraseActivada, tema);
	}
	
	private void extraerOracionesAfirmarivasYPreguntasDeWorkspaceEspecifico(ArrayList<Salida> misSalidas, Respuesta respuesta, String idFraseActivada, Tema tema){
		Pregunta miPregunta = null;
		agregarOracionesAfirmativasDeWorkspaceEspecifico(misSalidas, respuesta.obtenerLosNombresDeLasOracionesAfirmativasActivas(), respuesta);
		if( ! idFraseActivada.equals("")){
			
			miPregunta = (Pregunta) tema.buscarUnaFrase(idFraseActivada);
			misSalidas.add(agente.decirUnaFrase(miPregunta, respuesta, tema, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente()));
			fraseActual = miPregunta;
			ponerComoYaTratado(tema, miPregunta);
		}
	}
	
	private void agregarOracionesAfirmativasDeWorkspaceEspecifico(ArrayList<Salida> misSalidas, List<String> afirmativas, Respuesta respuesta){
		Afirmacion miAfirmacion = null;
		if(afirmativas != null && respuesta != null){
			for(int index = 0; index < afirmativas.size(); index++){
				if(afirmativas.get(index).equals("envioExitosoDeCorreo")){
					String email = respuesta.obtenerElementoDelContextoDeWatson("email");
					if(this.enviarCorreo(email)){
						miAfirmacion = (Afirmacion) this.temaActual.buscarUnaFrase("envioExitosoDeCorreo");
					}else{
						miAfirmacion = (Afirmacion) this.temaActual.buscarUnaFrase("envioFallidoDeCorreo");
					}
				}else{
					miAfirmacion = (Afirmacion) this.temaActual.buscarUnaFrase(afirmativas.get(index));
				}
				
				if( ! yaExisteEstaSalida(misSalidas, miAfirmacion.obtenerNombreDeLaFrase()) ){
					misSalidas.add(agente.decirUnaFrase(miAfirmacion, respuesta, temaActual, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente()));
					fraseActual = miAfirmacion;
				}
				ponerComoYaTratado(this.temaActual, miAfirmacion);
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
	
	private void ponerComoYaTratado(Tema tema, Frase frase){
		if(tema != null){
			if ( ! hilo.existeTema(tema)){ //si quiere que solo lo cuente una vez
				estadisticasTemasTratados.darSeguimiento(tema);
			}
			
			if(tema.sePuedeRepetir()){
				hilo.ponerComoDichoEste(tema);
			}else{
				hilo.noPuedoRepetir(tema);
			}
		}
		
		if(frase != null){
			hilo.ponerComoDichoEsta(frase);
		}
	}
	
	public void guardarEstadisticas(String idCliente, String idSesion) throws ClassNotFoundException, SQLException{
		estadisticasTemasTratados.guardarEstadisticasEnBaseDeDatos(idCliente, idSesion);
	}
	
	public Date obtenerLaFechaDelUltimoRegistroDeLaConversacion(){
		return fechaDelUltimoRegistroDeLaConversacion;
	}
	
	private String obtenerUnaFraseAfirmativa(String[] frases){
		String frase = frases[0];
		int n = (int)Math.floor(Math.random()*frases.length);
		if(this.agente.obtenerTemario().frase(frases[n]).esUnaOracionAfirmativa())
				frase = frases[n];
		else 
			obtenerUnaFraseAfirmativa(frases);
		return frase;
	}
	
	private String obtenerUnaFraseTipoPregunta(String[] frases){
		String frase = frases[0];
		int n = (int)Math.floor(Math.random()*frases.length);
		if(this.agente.obtenerTemario().frase(frases[n]).esUnaPregunta())
				frase = frases[n];
		else 
			obtenerUnaFraseAfirmativa(frases);
		return frase;
	}
	
	private String obtenerUnaFraseDespedida(String[] frases){
		String frase = frases[0];
		int n = (int)Math.floor(Math.random()*frases.length);
		if(this.agente.obtenerTemario().frase(frases[n]).esUnaDespedida())
				frase = frases[n];
		else obtenerUnaFraseAfirmativa(frases);
		return frase;
	}
	
	public boolean enviarCorreo(String correos){
		//String correos = "sgonzales@cecropiasolutions.com";
		GeneradorDeEmails generador = new GeneradorDeEmails();
		String body = generador.generarNuevoCorreo(agente.verMiHistorico().verHistorialDeLaConversacion());
		String tittle = "Conversacion con el agente de la Muni - "+Calendar.getInstance().getTime();
		return email.sendEmail(tittle, correos, body);
	}
	
	private ArrayList<Salida> agregarSalidasAlHistorico(ArrayList<Salida> misSalidas, Date fecha){
		for(Salida salida:misSalidas){
			salida.setMiFecha(fecha);
			agente.verMiHistorico().agregarHistorialALaConversacion(salida);
		}
		return misSalidas;
	}
	
	private ArrayList<Salida> analizarRespuestaRetrieveAndRank(String respuestaDelCliente, ArrayList<Salida> misSalidas, Respuesta respuesta){
		try{
			RetrieveAndRank service = new RetrieveAndRank();
			service.setUsernameAndPassword(userRetrieveAndRank, passwordRetrieveAndRank);
			solrClient = getSolrClient("https://gateway.watsonplatform.net/retrieve-and-rank/api", userRetrieveAndRank, passwordRetrieveAndRank);
			SolrUtils solrUtils = new SolrUtils(solrClient, null, collectionName, rankerId);
			QueryRequestPayload body = new QueryRequestPayload();
			body.setQuery(respuestaDelCliente);
			
			QueryResponsePayload queryResponse = new QueryResponsePayload();
		      
			queryResponse.setQuery(body.getQuery());

		      SolrResults rankedResults = solrUtils.search(body, true);
		      queryResponse.setRankedResults(rankedResults.getResult());


		      // 1. Collects all the documents ids to retrieve the title and body in a single query
		      ArrayList<String> idsOfDocsToRetrieve = new ArrayList<>();

		      for (RankResult answer : queryResponse.getRankedResults()) {
		        idsOfDocsToRetrieve.add(answer.getAnswerId());
		        answer.setSolrRank(rankedResults.getIds().indexOf(answer.getAnswerId()));
		      }

		      // 2. Query Solr to retrieve document title and body
		      Map<String, SolrResult> idsToDocs = solrUtils.getDocumentsByIds(idsOfDocsToRetrieve);


		      // 3. Update the queryResponse with the body and title
		      for (RankResult answer : queryResponse.getRankedResults()) {
		        answer.setBody(idsToDocs.get(answer.getAnswerId()).getBody());
		        answer.setTitle(idsToDocs.get(answer.getAnswerId()).getTitle());
		      }
		     
		      double confianza = 0.50;
		      System.out.println("Respuesta de Retrieve and Rank:"   +queryResponse.getRankedResults().get(0).getScore());
		     if(queryResponse.getRankedResults().get(0).getScore()>confianza) {
		    	 Salida salida = new Salida();
		    	 String[] titulo = queryResponse.getRankedResults().get(0).getTitle().split("-");
		    	 Frase frase = new Afirmacion(1, titulo[0], "retrieveAndRank", null , null, 1, null);	
		    	 salida.escribir(queryResponse.getRankedResults().get(0).getBody(), respuesta, temaActual, frase);
		    	 try{
		    		 salida.setMiSonido(queryResponse.getRankedResults().get(0).getBody(), informacionDelCliente.getIdDelCliente());
		    	 }catch(Exception | Error error){
		    		 System.out.println(error.getMessage());
		    	 }
		    	 misSalidas.add(salida);
		     }
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
	   return misSalidas;
	}
	private HttpSolrClient getSolrClient(String uri, String username, String password) {
	    return new HttpSolrClient(service.getSolrUrl(clusterId), HttpSolrClientUtils.createHttpClient(uri, username, password));
	}
}
