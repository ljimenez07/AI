package com.ncubo.logicaDeLasConversaciones;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.json.JSONException;
import org.json.JSONObject;
import com.ibm.watson.developer_cloud.conversation.v1.model.Intent;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.RetrieveAndRank;
import com.ncubo.chatbot.bitacora.Dialogo;
import com.ncubo.chatbot.bloquesDeLasFrases.BloquePendiente;
import com.ncubo.chatbot.bloquesDeLasFrases.FrasesDelBloque;
import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.contexto.Variable;
import com.ncubo.chatbot.contexto.VariablesDeContexto;
import com.ncubo.chatbot.partesDeLaConversacion.Afirmacion;
import com.ncubo.chatbot.partesDeLaConversacion.CaracteristicaDeLaFrase;
import com.ncubo.chatbot.partesDeLaConversacion.Despedida;
import com.ncubo.chatbot.partesDeLaConversacion.Frase;
import com.ncubo.chatbot.partesDeLaConversacion.HiloDeLaConversacion;
import com.ncubo.chatbot.partesDeLaConversacion.IntencionesNoReferenciadas;
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
	private FrasesDelBloque frasesDelBloqueActual = null;
	private Estadisticas estadisticasTemasTratados;
	private ArrayList<Salida> miUltimaSalida;
	private final Constantes.ModoDeLaVariable modoDeResolucionDeResultadosFinales;
	private Date fechaDelUltimoRegistroDeLaConversacion;
	private final TemasPendientesDeAbordar temasPendientes;
	private Email email;
	private final InformacionDelCliente informacionDelCliente;
	private static HttpSolrClient solrClient;
	private static RetrieveAndRank service = new RetrieveAndRank(); 
	private BloquePendiente bloquePendiente = null;
	private boolean generarAudio = true;
	private String userRetrieveAndRank;
	private String passwordRetrieveAndRank;
	private String collectionName;
	private String clusterId;
	private String rankerId;
	
	private IntencionesNoReferenciadas intencionesNoReferenciadas;
	
	public Conversacion(Cliente participante, ConsultaDao consultaDao, Agente miAgente, InformacionDelCliente cliente, IntencionesNoReferenciadas intenciones, boolean generarAudio){
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
		intencionesNoReferenciadas = intenciones;
		this.generarAudio = generarAudio;
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
		
		this.temaActual = this.agente.obtenerTemario().buscarTemaPorLaIntencion(intencionesNoReferenciadas.getINTENCION_SALUDAR());
		
		Saludo saludoGeneral = (Saludo) this.agente.obtenerTemario().extraerFraseDeSaludoInicial(CaracteristicaDeLaFrase.esUnSaludo,intencionesNoReferenciadas.getINTENCION_SALUDAR());
		misSalidas.add(agente.decirUnaFrase(saludoGeneral, null, temaActual, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente(),generarAudio));
		ponerComoYaTratado(this.temaActual, saludoGeneral);
		
		Pregunta queQuiere = (Pregunta) this.agente.obtenerTemario().extraerFraseDeSaludoInicial(CaracteristicaDeLaFrase.esUnaPregunta,intencionesNoReferenciadas.getINTENCION_SALUDAR());
		misSalidas.add(agente.decirUnaFrase(queQuiere, null, temaActual, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente(),generarAudio));
		
		ponerComoYaTratado(this.temaActual, queQuiere);
		fechaDelUltimoRegistroDeLaConversacion = Calendar.getInstance().getTime();
		
		misSalidas = agregarSalidasAlHistorico(misSalidas, fechaDelUltimoRegistroDeLaConversacion);
		
		miUltimaSalida = misSalidas;
		
		
		agente.cambiarANivelSuperior();
		
		return misSalidas;
	}
	
	public ArrayList<Salida> analizarLaRespuestaConWatson(String respuestaDelCliente, boolean esModoConsulta, Hashtable<String, Variable> cookiesEnVariables) throws Exception{
		ArrayList<Salida> misSalidas = new ArrayList<Salida>();
		Respuesta respuesta = null;
		
		actualizarCookiesEnMemoriaDeLaConversacion(cookiesEnVariables);
		
		boolean hayTemaActualDiciendose = this.temaActual != null;
		if(hayTemaActualDiciendose){
			
			respuesta = agente.enviarRespuestaAWatson(respuestaDelCliente, fraseActual, temaActual, intencionesNoReferenciadas.getINTENCION_NO_ENTIENDO(), participante);
			this.hilo.agregarUnaRespuesta(respuesta);
	
			if (respuesta.hayProblemasEnLaComunicacionConWatson()){
				String nombreFrase = obtenerUnaFraseAfirmativa(intencionesNoReferenciadas.getFRASES_INTENCION_ERROR_CON_WATSON());
				Afirmacion errorDeComunicacionConWatson = (Afirmacion) this.agente.obtenerTemario().contenido().frase(nombreFrase);
				misSalidas.add(agente.decirUnaFrase(errorDeComunicacionConWatson, respuesta, temaActual, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente(),generarAudio));
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
						this.frasesDelBloqueActual = temaPendiente.getFraseDelBloqueActual();
						this.bloquePendiente = temaPendiente.getBloqueActual();
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
				this.frasesDelBloqueActual = temaPendiente.getFraseDelBloqueActual();
				this.bloquePendiente = temaPendiente.getBloqueActual();
				agente.setMiTopico(temaPendiente.getMiTopico());
				agente.cambiarElContexto(temaPendiente.getContextoCognitivo());
				volverlARetomarUnTema(misSalidas, respuesta);
			}else{
				agente.cambiarANivelSuperior();
				respuesta = agente.enviarRespuestaAWatson(respuestaDelCliente, fraseActual, temaActual, intencionesNoReferenciadas.getINTENCION_NO_ENTIENDO(), participante);
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
							this.frasesDelBloqueActual = temaPendiente.getFraseDelBloqueActual();
							this.bloquePendiente = temaPendiente.getBloqueActual();
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
			this.frasesDelBloqueActual = temaPendiente.getFraseDelBloqueActual();
			this.bloquePendiente = temaPendiente.getBloqueActual();
			agente.setMiTopico(temaPendiente.getMiTopico());
			agente.cambiarElContexto(temaPendiente.getContextoCognitivo());
			volverlARetomarUnTema(misSalidas, respuesta);
		}else{
			if(! hayAlgunaPreguntaEnLasSalidas(misSalidas) && esModoConsulta && respuesta.seTerminoElTema() && 
					! temasPendientes.hayTemasPendientes() && ! existeLaFraseEnLasSalidas(misSalidas, "noQuiereHacerOtraConsulta") && ! hayAlgunaDespedidaEnLasSalidas(misSalidas)){ // // && ! existeLaFraseEnLasSalidas(misSalidas, obtenerUnaFraseDespedida(Constantes.FRASES_INTENCION_DESPEDIDA))
				if(misSalidas.isEmpty()){
					decirTemaNoEntendi(misSalidas, respuesta);
				}else{
					decirTemaPreguntarPorOtraCosa(misSalidas, respuesta, respuestaDelCliente);
				}
			}
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
			frasesDelBloqueActual = null;
		}else{
			if (agente.entendiLaUltimaPregunta()){
				idFraseActivada = respuesta.obtenerFraseActivada();
				extraerOracionesAfirmarivasYPreguntas(misSalidas, respuesta, idFraseActivada);
				
				agente.actualizarTodasLasVariablesDeContexto(respuesta, participante);
				
				if(agente.hayQueCambiarDeTema()){
					hilo.limpiarLosBloquesConcluidosDelTemaActual();
					bloquePendiente = null;
					if(misSalidas.isEmpty()){// Hay que buscar un nuevo tema y no he dicho nada aun
						respuesta = cambiarDeTema(idFraseActivada, respuestaDelCliente, misSalidas, respuesta);
						if(respuesta.seTerminoElTema()){
							temaActual = null;
							frasesDelBloqueActual = null;
						}
					}else{
						temaActual = null;
						frasesDelBloqueActual = null;
						agente.cambiarANivelSuperior();
					}
				}else if(respuesta.seTerminoElBloque()){
					
					// Preguntar si hay bloque en cola
					if (bloquePendiente != null){
						this.temaActual = bloquePendiente.getTemaActual();
						this.frasesDelBloqueActual = bloquePendiente.getBloqueActual();
						this.fraseActual = bloquePendiente.getFraseActual();
						agente.cambiarElContexto(bloquePendiente.getContextoCognitivo());
						
						volverlARetomarUnBloque(misSalidas, respuesta);
						bloquePendiente = null;
					}else{
						hilo.agregarBloqueConcluido(frasesDelBloqueActual);
						respuesta = cambiarDeBloque(idFraseActivada, respuestaDelCliente, misSalidas, respuesta, true);
						if(respuesta.seTerminoElTema()){
							temaActual = null;
							frasesDelBloqueActual = null;
						}
					}
					
				}
			}else{ 
				if (agente.hayQueCambiarDeTemaForzosamente()){ // TODO Analizar si hay mas de un tema en cola
					
					boolean esParteDelMismoTema = false;
					// Verificar si en el tema que esta tiene bloques
					if(temaActual != null){
						if(temaActual.elTemaTieneBloques() & frasesDelBloqueActual != null){
							// Verificar si es una DUDA de ese tema (buscar si la intencion forma parte del tema)
							String laIntencion = agente.obtenerNombreDeLaIntencionGeneralActiva();
							if(temaActual.existeLaIntencionEnElTema(laIntencion)){
								bloquePendiente = new BloquePendiente(temaActual, frasesDelBloqueActual, fraseActual, agente.getMiUltimoTopico().obtenerElContexto());
								esParteDelMismoTema = true;
								// Activar el bloque de duda
								respuesta = cambiarDeBloque(idFraseActivada, respuestaDelCliente, misSalidas, respuesta, false);
								if(respuesta.seTerminoElTema()){
									temaActual = null;
									frasesDelBloqueActual = null;
								}
							}
						}
					}
					
					if( ! esParteDelMismoTema){
						if(temaActual != null)
							if(! temaActual.getNombre().equals("preguntarPorOtraConsulta"))
								this.temasPendientes.agregarUnTema(new TemaPendiente(temaActual, fraseActual, agente.getMiUltimoTopico(), frasesDelBloqueActual, bloquePendiente));
						
						agente.cambiarANivelSuperior();
						respuesta = agente.enviarRespuestaAWatson(respuestaDelCliente, fraseActual, temaActual, intencionesNoReferenciadas.getINTENCION_NO_ENTIENDO(), participante);
						
						respuesta = cambiarDeTema(idFraseActivada, respuestaDelCliente, misSalidas, respuesta); 
					}
					
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
										this.temasPendientes.agregarUnTema(new TemaPendiente(temaActual, fraseActual, agente.getMiTopico(), frasesDelBloqueActual, bloquePendiente));
									else{
										decirTemaPreguntarPorOtraCosa(misSalidas, respuesta, respuestaDelCliente);
									}
								}else{
									decirTemaPreguntarPorOtraCosa(misSalidas, respuesta, respuestaDelCliente);
								}
							}
						}else{
							temaActual = null;
							//fraseActual = null;	
							frasesDelBloqueActual = null;
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
	
	private Respuesta cambiarDeBloque(String idFraseActivada, String respuestaDelCliente, ArrayList<Salida> misSalidas, Respuesta respuesta, boolean hayQueBuscarBloque){
		FrasesDelBloque bloqueADecir = null;
		
		if(hayQueBuscarBloque){
			bloqueADecir = temaActual.buscarSiguienteBloqueADecir(hilo.obtenerBloquesConcluidos(), frasesDelBloqueActual, participante);
			
			if(bloqueADecir != null){
				frasesDelBloqueActual = bloqueADecir;
				agente.activarValiableEnElContextoDeWatson(Constantes.ID_BLOQUE, frasesDelBloqueActual.getIdDelBloque());
				
				agente.activarTemaEnElContextoDeWatson(this.temaActual.getNombre());
				agente.activarValiableEnElContextoDeWatson("dialog_node", "root");
				
				// llamar a watson y ver que bloque se activo
				respuesta = agente.inicializarTemaEnWatson(respuestaDelCliente, respuesta, true, participante, fraseActual, temaActual);
				
				if (respuesta.hayProblemasEnLaComunicacionConWatson()){
					String nombreFrase = obtenerUnaFraseAfirmativa(intencionesNoReferenciadas.getFRASES_INTENCION_ERROR_CON_WATSON());
					Afirmacion errorDeComunicacionConWatson = (Afirmacion) this.agente.obtenerTemario().contenido().frase(nombreFrase);
					misSalidas.add(agente.decirUnaFrase(errorDeComunicacionConWatson, respuesta, temaActual, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente(), generarAudio));
					ponerComoYaTratado(this.temaActual, errorDeComunicacionConWatson);
				}else{
					idFraseActivada = agente.obtenerNodoActivado(respuesta.messageResponse());
					System.out.println("Id de la frase a decir: "+idFraseActivada);
					extraerOracionesAfirmarivasYPreguntas(misSalidas, respuesta, idFraseActivada);
				}
			}else{
				frasesDelBloqueActual = null;
			}
		}else{
			agente.borrarUnaVariableDelContexto(Constantes.ID_BLOQUE);;
			
			agente.activarTemaEnElContextoDeWatson(this.temaActual.getNombre());
			agente.activarValiableEnElContextoDeWatson("dialog_node", "root");
			
			// llamar a watson y ver que bloque se activo
			respuesta = agente.inicializarTemaEnWatson(respuestaDelCliente, respuesta, true, participante, fraseActual, temaActual);
			
			if (respuesta.hayProblemasEnLaComunicacionConWatson()){
				String nombreFrase = obtenerUnaFraseAfirmativa(intencionesNoReferenciadas.getFRASES_INTENCION_ERROR_CON_WATSON());
				Afirmacion errorDeComunicacionConWatson = (Afirmacion) this.agente.obtenerTemario().contenido().frase(nombreFrase);
				misSalidas.add(agente.decirUnaFrase(errorDeComunicacionConWatson, respuesta, temaActual, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente(), generarAudio));
				ponerComoYaTratado(this.temaActual, errorDeComunicacionConWatson);
			}else{
				idFraseActivada = agente.obtenerNodoActivado(respuesta.messageResponse());
				System.out.println("Id de la frase a decir: "+idFraseActivada);
				extraerOracionesAfirmarivasYPreguntas(misSalidas, respuesta, idFraseActivada);
			}
		}
		
		return respuesta;
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
			
			if (idFraseActivada.equals("")){ // Quiere decir que no hay ninguna pregunta en la salida
				System.out.println("El proximo tema a tratar es: "+this.temaActual.getIdTema());
				
				// Activar en el contexto el tema
				if(temaActual.elTemaTieneBloques()){
					FrasesDelBloque bloqueADecir = temaActual.buscarSiguienteBloqueADecir(hilo.obtenerBloquesConcluidos(), frasesDelBloqueActual, participante);
					if(bloqueADecir != null){
						frasesDelBloqueActual = bloqueADecir;
						agente.activarValiableEnElContextoDeWatson(Constantes.ID_BLOQUE, frasesDelBloqueActual.getIdDelBloque());
					}
				}
				
				agente.activarTemaEnElContextoDeWatson(this.temaActual.getNombre());
				agente.activarValiableEnElContextoDeWatson("dialog_node", "root");
				
				// llamar a watson y ver que bloque se activo
				respuesta = agente.inicializarTemaEnWatson(respuestaDelCliente, respuesta, true, participante, fraseActual, temaActual);
				
				if (respuesta.hayProblemasEnLaComunicacionConWatson()){
					String nombreFrase = obtenerUnaFraseAfirmativa(intencionesNoReferenciadas.getFRASES_INTENCION_ERROR_CON_WATSON());
					Afirmacion errorDeComunicacionConWatson = (Afirmacion) this.agente.obtenerTemario().contenido().frase(nombreFrase);
					misSalidas.add(agente.decirUnaFrase(errorDeComunicacionConWatson, respuesta, temaActual, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente(), generarAudio));
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
						Respuesta respuesta = agente.inicializarTemaEnWatson(respuestaDelCliente, null, true, participante, fraseActual, temaActual);
						
						if ( ! respuesta.hayProblemasEnLaComunicacionConWatson()){
							String idFraseActivada = agente.obtenerNodoActivado(respuesta.messageResponse());
							
							if( ! idFraseActivada.isEmpty()){
								try{
									System.out.println("Id de la frase a recordar: "+idFraseActivada);
									Frase miPregunta = (Pregunta) temaNuevo.buscarUnaFrase(idFraseActivada, frasesDelBloqueActual);
									
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
									if( ! temaNuevo.getNombre().equals("preguntarPorOtraConsulta")){
										TemaPendiente nuevoTemaPriminivo = new TemaPendiente(temaNuevo, miPregunta, context, agente.getMiUltimoTopico(), frasesDelBloqueActual, bloquePendiente);
										this.temasPendientes.agregarUnTema(nuevoTemaPriminivo);
									}
									agente.seTieneQueGenerarUnNuevoContextoParaWatsonEnElWorkspaceActualConRespaldo();
								}catch(Exception e){}
								
							}
						}
					}
				}
			}
		}
	}
	
	private void decirTemaPreguntarPorOtraCosa(ArrayList<Salida> misSalidas, Respuesta respuesta, String respuestaDelCliente){
		System.out.println("Se va a preguntar por otra cosa ...");							
		this.temaActual = this.agente.obtenerTemario().buscarTemaPorLaIntencion(intencionesNoReferenciadas.getINTENCION_PREGUNTAR_POR_OTRA_CONSULTA());

		agente.inicializarTemaEnWatson(respuestaDelCliente, respuesta, false, participante, fraseActual, temaActual);
		
		// Activar en el contexto el tema
		agente.activarTemaEnElContextoDeWatson(this.temaActual.getNombre());
		
		// llamar a watson y ver que bloque se activo
		respuesta = agente.inicializarTemaEnWatson(respuestaDelCliente, respuesta, true, participante, fraseActual, temaActual);
		
		if (respuesta.hayProblemasEnLaComunicacionConWatson()){
			String nombreFrase = obtenerUnaFraseAfirmativa(intencionesNoReferenciadas.getFRASES_INTENCION_ERROR_CON_WATSON());
			Afirmacion errorDeComunicacionConWatson = (Afirmacion) this.agente.obtenerTemario().contenido().frase(nombreFrase);
			misSalidas.add(agente.decirUnaFrase(errorDeComunicacionConWatson, respuesta, temaActual, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente(), generarAudio));
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
		misSalidas.add(agente.decirUnaFrase(fraseActual, respuesta, temaActual, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente(), generarAudio));
		agente.yaNoCambiarANivelSuperior();
	}
	
	private void volverlARetomarUnBloque(ArrayList<Salida> misSalidas, Respuesta respuesta){
		misSalidas.add(agente.decirUnaFrase(fraseActual, respuesta, temaActual, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente(), generarAudio));
		agente.yaNoCambiarANivelSuperior();
	}
	
	private void decirFraseRecordatoria(ArrayList<Salida> misSalidas, Respuesta respuesta){
		System.out.println("Frase recordatoria ...");
		String nombreFrase = obtenerUnaFraseAfirmativa(intencionesNoReferenciadas.getFRASES_INTENCION_RECORDAR_TEMAS());
		
		Afirmacion fraseRecordatoria = (Afirmacion) this.agente.obtenerTemario().frase(nombreFrase);
		misSalidas.add(agente.decirUnaFrase(fraseRecordatoria, respuesta, null, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente(), generarAudio));

	}
	
	private void decirTemaNoEntendi(ArrayList<Salida> misSalidas, Respuesta respuesta){
		System.out.println("No entendi bien ...");
		Tema miTema = this.agente.obtenerTemario().buscarTema(intencionesNoReferenciadas.getINTENCION_NO_ENTIENDO());
		String nombreFrase = obtenerUnaFraseTipoPregunta(intencionesNoReferenciadas.getFRASES_INTENCION_NO_ENTIENDO());
		
		Pregunta fueraDeContexto = (Pregunta) this.agente.obtenerTemario().frase(nombreFrase);
		misSalidas.add(agente.decirUnaFrase(fueraDeContexto, respuesta, miTema, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente(), generarAudio));
		ponerComoYaTratado(miTema, fueraDeContexto);
	}
	
	private boolean verificarIntencionNoAsociadaANingunWorkspace(ArrayList<Salida> misSalidas, Respuesta respuesta, String respuestaDelCliente) throws Exception{
		if(agente.hayIntencionNoAsociadaANingunWorkspace()){
			
			/*if (temaActual != null && fraseActual != null){
				if(! temaActual.getNombre().equals("preguntarPorOtraConsulta"))
					this.temasPendientes.agregarUnTema(new TemaPendiente(temaActual, fraseActual, agente.getMiUltimoTopico()));
			}*/
			
			Tema miTema = null;
			if(agente.obtenerNombreDeLaIntencionGeneralActiva().equals(intencionesNoReferenciadas.getINTENCION_SALUDAR())){
				System.out.println("Quiere saludar ...");
				boolean esResaludar = false;
				
				String saludo = obtenerUnaFraseAfirmativa(intencionesNoReferenciadas.getFRASES_INTENCION_SALUDAR());
				miTema = this.agente.obtenerTemario().buscarTemaPorLaIntencion(intencionesNoReferenciadas.getINTENCION_SALUDAR());
				
				ArrayList<Dialogo> conversacionCompleta = verHistorialDeLaConversacion();
				if(conversacionCompleta != null){
					if(conversacionCompleta.size() > 0){
						esResaludar = true;
					}
				}
				
				if (temaActual != null && fraseActual != null){
					if(! temaActual.getNombre().equals("preguntarPorOtraConsulta")){
						this.temasPendientes.agregarUnTema(new TemaPendiente(temaActual, fraseActual, agente.getMiUltimoTopico(), frasesDelBloqueActual, bloquePendiente));
					}
				}
				
				try{
					if(esResaludar){
						try{
							Afirmacion saludar = (Afirmacion) miTema.buscarUnaFrase("reSaludar", frasesDelBloqueActual);
							misSalidas.add(agente.decirUnaFrase(saludar, respuesta, miTema, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente(), generarAudio));
							ponerComoYaTratado(miTema, saludar);
						}catch(Exception e){
							Afirmacion saludar = (Afirmacion) miTema.buscarUnaFrase(saludo, frasesDelBloqueActual);
							misSalidas.add(agente.decirUnaFrase(saludar, respuesta, miTema, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente(), generarAudio));
							ponerComoYaTratado(miTema, saludar);
						}
					}else{
						Afirmacion saludar = (Afirmacion) miTema.buscarUnaFrase(saludo, frasesDelBloqueActual);
						misSalidas.add(agente.decirUnaFrase(saludar, respuesta, miTema, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente(), generarAudio));
						ponerComoYaTratado(miTema, saludar);
					}
					
				}catch(Exception e){}
				
				if(temasPendientes.hayTemasPendientes()){ // TODO Sacar un tema top de la Pila
					TemaPendiente temaPendiente = temasPendientes.extraerElSiquienteTema();
					this.temaActual = temaPendiente.getTemaActual();
					this.fraseActual = temaPendiente.getFraseActual();
					this.frasesDelBloqueActual = temaPendiente.getFraseDelBloqueActual();
					this.bloquePendiente = temaPendiente.getBloqueActual();
					agente.setMiTopico(temaPendiente.getMiTopico());
					agente.cambiarElContexto(temaPendiente.getContextoCognitivo());
					volverlARetomarUnTema(misSalidas, respuesta);
				}else{
					Pregunta queQuiere = (Pregunta) this.agente.obtenerTemario().extraerFraseDeSaludoInicial(CaracteristicaDeLaFrase.esUnaPregunta,intencionesNoReferenciadas.getINTENCION_SALUDAR());
					misSalidas.add(agente.decirUnaFrase(queQuiere, respuesta, miTema, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente(), generarAudio));
					//ponerComoYaTratado(temaActual, queQuiere);
				}
				
				//temasPendientes.borrarLosTemasPendientes();
				
			}else if(agente.obtenerNombreDeLaIntencionGeneralActiva().equals(intencionesNoReferenciadas.getINTENCION_DESPEDIDA())){
				System.out.println("Quiere despedirse ...");
				miTema = this.agente.obtenerTemario().buscarTemaPorLaIntencion(intencionesNoReferenciadas.getINTENCION_DESPEDIDA());
				String nombreFrase = obtenerUnaFraseDespedida(intencionesNoReferenciadas.getFRASES_INTENCION_DESPEDIDA());
				
				try{
					Despedida saludar = (Despedida) miTema.buscarUnaFrase(nombreFrase, frasesDelBloqueActual);
					misSalidas.add(agente.decirUnaFrase(saludar, respuesta, miTema, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente(), generarAudio));
					ponerComoYaTratado(miTema, saludar);
				}catch(Exception e){}
				
				temasPendientes.borrarLosTemasPendientes();
				
			}else if(agente.obtenerNombreDeLaIntencionGeneralActiva().equals(intencionesNoReferenciadas.getINTENCION_FUERA_DE_CONTEXTO())){
				System.out.println("Esta fuera de contexto ...");
				
				if (temaActual != null && fraseActual != null){
					if(! temaActual.getNombre().equals("preguntarPorOtraConsulta")){
						this.temasPendientes.agregarUnTema(new TemaPendiente(temaActual, fraseActual, agente.getMiUltimoTopico(), frasesDelBloqueActual, bloquePendiente));
					}
				}
				
				miTema = this.agente.obtenerTemario().buscarTemaPorLaIntencion(intencionesNoReferenciadas.getINTENCION_FUERA_DE_CONTEXTO());
				String nombreFrase = obtenerUnaFraseAfirmativa(intencionesNoReferenciadas.getFRASES_INTENCION_FUERA_DE_CONTEXTO());
				
				try{
					Afirmacion fueraDeContexto = (Afirmacion) miTema.buscarUnaFrase(nombreFrase, frasesDelBloqueActual);
					misSalidas.add(agente.decirUnaFrase(fueraDeContexto, respuesta, miTema, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente(), generarAudio));
					ponerComoYaTratado(miTema, fueraDeContexto);
				}catch(Exception e){}
				
			}else if(agente.obtenerNombreDeLaIntencionGeneralActiva().equals(intencionesNoReferenciadas.getINTENCION_NO_ENTIENDO())){
				decirTemaNoEntendi(misSalidas, respuesta);
			}else if(agente.obtenerNombreDeLaIntencionGeneralActiva().equals(intencionesNoReferenciadas.getINTENCION_DESPISTADOR())){
				System.out.println("Quiere despistar  ...");
				
				if (temaActual != null && fraseActual != null){
					if(! temaActual.getNombre().equals("preguntarPorOtraConsulta")){
						this.temasPendientes.agregarUnTema(new TemaPendiente(temaActual, fraseActual, agente.getMiUltimoTopico(), frasesDelBloqueActual, bloquePendiente));
					}
				}
				
				miTema = this.agente.obtenerTemario().buscarTemaPorLaIntencion(intencionesNoReferenciadas.getINTENCION_DESPISTADOR());
				String nombreFrase = obtenerUnaFraseTipoPregunta(intencionesNoReferenciadas.getFRASES_INTENCION_DESPISTADOR());
				
				try{
					Afirmacion fueraDeContexto = (Afirmacion) this.agente.obtenerTemario().frase(nombreFrase);
					misSalidas.add(agente.decirUnaFrase(fueraDeContexto, respuesta, miTema, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente(), generarAudio));
					ponerComoYaTratado(miTema, fueraDeContexto);
				}catch(Exception e){
					Pregunta despistar = (Pregunta) this.agente.obtenerTemario().frase(nombreFrase);
					misSalidas.add(agente.decirUnaFrase(despistar, respuesta, miTema, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente(), generarAudio));
					ponerComoYaTratado(miTema, despistar);
				}
				
			}else if(agente.obtenerNombreDeLaIntencionGeneralActiva().equals(intencionesNoReferenciadas.getINTENCION_REPETIR_ULTIMA_FRASE())){
				System.out.println("Quiere repetir  ...");
				String idFrase = obtenerUnaFraseAfirmativa(intencionesNoReferenciadas.getFRASES_INTENCION_REPETIR());
				
				Afirmacion conjuncion = (Afirmacion) this.agente.obtenerTemario().frase(idFrase);

				if(!miUltimaSalida.get(0).getFraseActual().obtenerNombreDeLaFrase().equals(idFrase))
					miUltimaSalida.add(0, agente.decirUnaFrase(conjuncion, respuesta, temaActual, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente(), generarAudio));
				for(Salida salida: miUltimaSalida){
					misSalidas.add(agente.decirUnaFrase(salida.getFraseActual(), respuesta, temaActual, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente(), generarAudio));
				}
	
				//temasPendientes.borrarLosTemasPendientes();
				
			}else if(agente.obtenerNombreDeLaIntencionGeneralActiva().equals(intencionesNoReferenciadas.getINTENCION_AGRADECIMIENTO())){
				System.out.println("Esta agradeciendo ...");	
				
				if (temaActual != null && fraseActual != null){
					if(! temaActual.getNombre().equals("preguntarPorOtraConsulta")){
						this.temasPendientes.agregarUnTema(new TemaPendiente(temaActual, fraseActual, agente.getMiUltimoTopico(), frasesDelBloqueActual, bloquePendiente));
					}
				}
				
				miTema = this.agente.obtenerTemario().buscarTemaPorLaIntencion(intencionesNoReferenciadas.getINTENCION_AGRADECIMIENTO());

				String frase = obtenerUnaFraseAfirmativa(intencionesNoReferenciadas.getFRASES_INTENCION_AGRADECIMIENTO());
				Afirmacion queQuiere = (Afirmacion) this.agente.obtenerTemario().frase(frase);
				misSalidas.add(agente.decirUnaFrase(queQuiere, respuesta, miTema, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente(), generarAudio));
				ponerComoYaTratado(miTema, queQuiere);
				
			}else if(agente.obtenerNombreDeLaIntencionGeneralActiva().equals(intencionesNoReferenciadas.getINTENCION_QUE_PUEDEN_PREGUNTAR())){
				System.out.println("Quiere saber que hago ...");
				
				if (temaActual != null && fraseActual != null){
					if(! temaActual.getNombre().equals("preguntarPorOtraConsulta")){
						this.temasPendientes.agregarUnTema(new TemaPendiente(temaActual, fraseActual, agente.getMiUltimoTopico(), frasesDelBloqueActual, bloquePendiente));
					}
				}
				
				miTema = this.agente.obtenerTemario().buscarTemaPorLaIntencion(intencionesNoReferenciadas.getINTENCION_QUE_PUEDEN_PREGUNTAR());

				String frase = obtenerUnaFraseAfirmativa(intencionesNoReferenciadas.getFRASES_INTENCION_QUE_PUEDEN_PREGUNTAR());
				Afirmacion queQuiere = (Afirmacion) this.agente.obtenerTemario().frase(frase);
				misSalidas.add(agente.decirUnaFrase(queQuiere, respuesta, miTema, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente(), generarAudio));
				ponerComoYaTratado(miTema, queQuiere);
			}
			
			return true;
		}else{
			return false;
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
			
			try{
				miPregunta = (Pregunta) tema.buscarUnaFrase(idFraseActivada, frasesDelBloqueActual);
				misSalidas.add(agente.decirUnaFrase(miPregunta, respuesta, tema, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente(), generarAudio));
				fraseActual = miPregunta;
				ponerComoYaTratado(tema, miPregunta);
			}catch(Exception e){}
		}
	}
	
	private void agregarOracionesAfirmativasDeWorkspaceEspecifico(ArrayList<Salida> misSalidas, List<String> afirmativas, Respuesta respuesta){
		Afirmacion miAfirmacion = null;
		if(afirmativas != null && respuesta != null){
			for(int index = 0; index < afirmativas.size(); index++){
				try{
					if(afirmativas.get(index).equals("envioExitosoDeCorreo")){
						String email = respuesta.obtenerElementoDelContextoDeWatson("email");
						if(this.enviarCorreo(email)){
							miAfirmacion = (Afirmacion) this.temaActual.buscarUnaFrase("envioExitosoDeCorreo", frasesDelBloqueActual);
						}else{
							miAfirmacion = (Afirmacion) this.temaActual.buscarUnaFrase("envioFallidoDeCorreo", frasesDelBloqueActual);
						}
					}
					if(respuesta.obtenerElementoDelContextoDeWatson("enviarInfoAlCorreo").equals("true"))
					{
						String email = respuesta.obtenerElementoDelContextoDeWatson("email");
						if(this.enviarRequisitosCorreo(email, afirmativas.get(index))){
							miAfirmacion = (Afirmacion) this.temaActual.buscarUnaFrase("envioExitosoDeCorreo", frasesDelBloqueActual);
						}else{
							miAfirmacion = (Afirmacion) this.temaActual.buscarUnaFrase("envioFallidoDeCorreo", frasesDelBloqueActual);
						}
					}else{
						miAfirmacion = (Afirmacion) this.temaActual.buscarUnaFrase(afirmativas.get(index), frasesDelBloqueActual);
					}
					
					if(miAfirmacion != null){
						if( ! yaExisteEstaSalida(misSalidas, miAfirmacion.obtenerNombreDeLaFrase()) ){
							misSalidas.add(agente.decirUnaFrase(miAfirmacion, respuesta, temaActual, participante, modoDeResolucionDeResultadosFinales, informacionDelCliente.getIdDelCliente(), generarAudio));
							fraseActual = miAfirmacion;
						}
						ponerComoYaTratado(this.temaActual, miAfirmacion);
					}
				}catch(Exception e){}
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
		String tittle = "Conversacion con el agente de la "+informacionDelCliente.getNombreDelCliente()+" - "+Calendar.getInstance().getTime();
		return email.sendEmail(tittle, correos, body);
	}
	
	public boolean enviarRequisitosCorreo(String correo, String requisitos){
		try{
			Afirmacion miAfirmacion = (Afirmacion) this.temaActual.buscarUnaFrase(requisitos, frasesDelBloqueActual);
			String body = miAfirmacion.texto().getTextoDeLaFrase();
			String tittle = temaActual.getDescripcion()+" - "+Calendar.getInstance().getTime();;
			return email.sendEmail(tittle, correo, body);
		}catch(Exception e){
			return false;
		}
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
		    	 Frase frase = new Afirmacion(1, titulo[0], "retrieveAndRank", null , null, 1, new ArrayList<Variable>(), null);	
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
	
	public ArrayList<Dialogo> verHistorialDeLaConversacion(){
		return agente.verMiHistorico().verHistorialDeLaConversacion();
	}
	
	public String obtenerValorDeLaVariable(String nombreDeLaVariable){
		String comando = String.format("show %s;", nombreDeLaVariable);
		String valor = "";
		try {
			valor = participante.evaluarCondicion(comando);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return valor;
	}
	
	private void actualizarCookiesEnMemoriaDeLaConversacion(Hashtable<String, Variable> cookiesEnVariables){
		if(cookiesEnVariables != null){
			Hashtable<String, Variable> variables = VariablesDeContexto.getInstance().obtenerTodasLasVariablesDeMiContexto();
			Enumeration<String> keys = cookiesEnVariables.keys();
			
			while(keys.hasMoreElements()){
				String key = keys.nextElement();
				Variable variable = cookiesEnVariables.get(key);
				
				if(variables.containsKey(variable.getNombre())){
					establecerValorDeLaVariable(variable.getNombre(), variable.getValorDeLaVariable()[0]);
				}
			}
		}
	}
	
	private boolean establecerValorDeLaVariable(String nombreDeLaVariable, String valorDeLaVariable){
		String comando = String.format("%s = '%s'; show %s;", nombreDeLaVariable, valorDeLaVariable, nombreDeLaVariable);
		boolean resultado = false;
		try {
			String valor = participante.evaluarCondicion(comando);
			if(valor.contains(valorDeLaVariable))
				resultado = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultado;
	}
	
}
