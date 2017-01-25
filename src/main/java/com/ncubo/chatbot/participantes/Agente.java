package com.ncubo.chatbot.participantes;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.ibm.watson.developer_cloud.conversation.v1.model.Intent;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ncubo.chatbot.bitacora.LogDeLaConversacion;
import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.configuracion.Constantes.ModoDeLaVariable;
import com.ncubo.chatbot.partesDeLaConversacion.Frase;
import com.ncubo.chatbot.partesDeLaConversacion.Respuesta;
import com.ncubo.chatbot.partesDeLaConversacion.Salida;
import com.ncubo.chatbot.partesDeLaConversacion.Tema;
import com.ncubo.chatbot.watson.WorkSpace;
import com.ncubo.db.BitacoraDao;
import com.ncubo.niveles.Topico;

// Es como el watson de Ncubo
public abstract class Agente extends Participante{

	private final ArrayList<WorkSpace> misWorkSpaces;
	//private final Hashtable<String, ConversacionConWatson> misConversacionesConWatson = new Hashtable<String, ConversacionConWatson>();
	private Topico miTopico;
	//private String nombreDelWorkSpaceGeneral;
	private String nombreDeWorkspaceActual;
	private String nombreDeLaIntencionGeneralActiva;
	//private String nombreDeLaIntencionEspecificaActiva;
	//private boolean estaEnElWorkspaceGeneral;
	private boolean hayQueEvaluarEnNivelSuperior;
	private boolean noEntendiLaUltimaRespuesta;
	private int numeroDeIntentosActualesEnRepetirUnaPregunta;
	private boolean cambiarDeTema = false;
	private boolean abordarElTemaPorNOLoEntendi = false;
	private boolean hayIntencionNoAsociadaANingunWorkspace;
	private LogDeLaConversacion miHistorico = new LogDeLaConversacion();
	private BitacoraDao miBitacora;
	
	public Agente(ArrayList<WorkSpace> miWorkSpaces){
		this.noEntendiLaUltimaRespuesta = true;
		//this.estaEnElWorkspaceGeneral = true;
		this.hayQueEvaluarEnNivelSuperior = true;
		this.misWorkSpaces = miWorkSpaces;
		//this.nombreDelWorkSpaceGeneral = "";
		this.nombreDeLaIntencionGeneralActiva = "";
		//this.nombreDeLaIntencionEspecificaActiva = "";
		this.numeroDeIntentosActualesEnRepetirUnaPregunta = 0;
		this.hayIntencionNoAsociadaANingunWorkspace = false;
		this.inicializarContextos();
		miBitacora = new BitacoraDao();
	}
	
	public Agente(){
		misWorkSpaces = null;
	}
	
	private void inicializarContextos(){
		
		miTopico = new Topico(misWorkSpaces.get(0));
		nombreDeWorkspaceActual = misWorkSpaces.get(0).getNombre();
		
		/*
		for(WorkSpace workspace: misWorkSpaces){
			//ConversacionConWatson conversacion = new ConversacionConWatson(workspace.getUsuarioIBM(), workspace.getContrasenaIBM(), workspace.getIdIBM());
			//String contexto = conversacion.enviarMSG("", null).getContext().toString();
			//conversacion.setElContextoConWatson(contexto);
			//misConversacionesConWatson.put(workspace.getNombre(), conversacion);
			//System.out.println("En el workspace "+workspace.getNombre()+" se tiene el Contexto: "+contexto);
			nombreDeWorkspaceActual = workspace.getNombre();
			nombreDelWorkSpaceGeneral = workspace.getNombre();
		}
		
		if(nombreDelWorkSpaceGeneral == ""){
			throw new ChatException("No existe un workspace general en para conectarse a Watson IBM");
		}*/
	}
	
	public void agregarHistorico(Salida miSalida){
		miHistorico.agregarHistorialALaConversacion(miSalida);
	}
	
	public LogDeLaConversacion verMiHistorico(){
		return miHistorico;
	}
	
	public boolean guardarUnaConversacionEnLaDB(String idSesion, String idCliente){
		try {
			miBitacora.insertar(idSesion, idCliente, miHistorico);
			return true;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	//public MessageResponse llamarAWatson(String mensaje, String nombreWorkspace){
	public MessageResponse llamarAWatson(String mensaje){
		//return misConversacionesConWatson.get(nombreWorkspace).enviarAWatson(mensaje);
		return miTopico.hablarConWatson(null, mensaje).messageResponse();
	}
	
	public Respuesta enviarRespuestaAWatson(String respuestaDelCliente, Frase frase){
		Respuesta respuesta = null;
		//if(estaEnElWorkspaceGeneral){
		if(hayQueEvaluarEnNivelSuperior){
			respuesta = analizarRespuestaInicial(respuestaDelCliente, frase);
		}else{
			respuesta = analizarRespuesta(respuestaDelCliente, frase);
		}
		
		return respuesta;
	}
	
	public Respuesta analizarRespuestaInicial(String respuestaDelCliente, Frase frase){
		Respuesta respuesta = miTopico.hablarConWatsonEnElNivelSuperior(frase, respuestaDelCliente);
		
		//respuesta = new Respuesta(frase, misConversacionesConWatson.get(nombreDeWorkspaceActual), misConversacionesConWatson.get(nombreDeWorkspaceActual).getElContextoConWatson());
		//respuesta.llamarAWatson(respuestaDelCliente);

		noEntendiLaUltimaRespuesta = (! respuesta.entendiLaRespuesta()) && (frase.esMandatorio()) && 
				(numeroDeIntentosActualesEnRepetirUnaPregunta != Constantes.MAXIMO_DE_INTENTOS_OPCIONALES);
		if(noEntendiLaUltimaRespuesta){
			numeroDeIntentosActualesEnRepetirUnaPregunta ++;
		}else{
			if(numeroDeIntentosActualesEnRepetirUnaPregunta == Constantes.MAXIMO_DE_INTENTOS_OPCIONALES){
				// Abordar el tema
				abordarElTemaPorNOLoEntendi = true; // Buscar otro tema
				cambiarDeTema = true; // Buscar otro tema
			}else{
				// Actualizar contexto
				//misConversacionesConWatson.get(nombreDeWorkspaceActual).setElContextoConWatson(respuesta.getMiContexto());
				abordarElTemaPorNOLoEntendi = false;
				
				// Analizar si ya tengo que cambiar de workspace
				try{
					String intencionDelCliente = respuesta.obtenerLaIntencionDeConfianzaDeLaRespuesta().getNombre();
					WorkSpace workspace = extraerUnWorkspaceConLaIntencion(intencionDelCliente);
					// if( (nombreDeWorkspaceActual.equals(nombreDelWorkSpaceGeneral)) && workspace != null && ! intencionDelCliente.equals("")){
					if(workspace != null && ! intencionDelCliente.equals("")){
						//nombreDeWorkspaceActual = workspace.getNombre();
						nombreDeLaIntencionGeneralActiva = intencionDelCliente;
						//System.out.println(String.format("Cambiando al workspace %s e intencion %s", nombreDeWorkspaceActual, nombreDeLaIntencionGeneralActiva));
						cambiarDeTema = true; // Buscar otro tema
						//nombreDeLaIntencionEspecificaActiva = determinarLaIntencionDeConfianzaEnUnWorkspace(respuestaDelCliente, nombreDeWorkspaceActual).getIntent();
						//nombreDeLaIntencionEspecificaActiva = determinarLaIntencionDeConfianzaEnUnWorkspace(respuestaDelCliente).getIntent();
						abordarElTemaPorNOLoEntendi = false;
						//estaEnElWorkspaceGeneral = false;
						hayQueEvaluarEnNivelSuperior = false;
						this.hayIntencionNoAsociadaANingunWorkspace = false;
					}else{
						System.out.println("Intencion no asociada a ningun workspace");
						if (! intencionDelCliente.equals("")){
							nombreDeLaIntencionGeneralActiva = intencionDelCliente;	
						}else{
							nombreDeLaIntencionGeneralActiva = Constantes.INTENCION_NO_ENTIENDO;
						}
						//nombreDeLaIntencionEspecificaActiva = "";
						hayIntencionNoAsociadaANingunWorkspace = true;
					}
				}catch(Exception e){
					System.out.println("No hay ninguna intencion real o de confianza");
					nombreDeLaIntencionGeneralActiva = Constantes.INTENCION_NO_ENTIENDO;
					//nombreDeLaIntencionEspecificaActiva = "";
					hayIntencionNoAsociadaANingunWorkspace = true;
				}
			}
			numeroDeIntentosActualesEnRepetirUnaPregunta = 0;
		}
		
		return respuesta;
	}
	
	public Respuesta analizarRespuesta(String respuestaDelCliente, Frase frase){
		Respuesta respuesta = miTopico.hablarConWatson(frase, respuestaDelCliente);
		
		//respuesta = new Respuesta(frase, misConversacionesConWatson.get(nombreDeWorkspaceActual), misConversacionesConWatson.get(nombreDeWorkspaceActual).getElContextoConWatson());
		//respuesta.llamarAWatson(respuestaDelCliente);
		int maximoIntentos = frase.obtenerNumeroIntentosFallidos();
		
		noEntendiLaUltimaRespuesta = (! (respuesta.entendiLaRespuesta() && ! respuesta.hayAlgunAnythingElse())) && 
				(numeroDeIntentosActualesEnRepetirUnaPregunta != maximoIntentos);
		if(noEntendiLaUltimaRespuesta){
			System.out.println("No entendi la respuesta ...");
			// Validar si es que el usuario cambio de intencion general
			Intent miIntencion = this.determinarLaIntencionGeneral(respuestaDelCliente);
			if(elClienteQuiereCambiarDeIntencionGeneral(miIntencion)){
				if(! miIntencion.getIntent().equals(nombreDeLaIntencionGeneralActiva)){
					System.out.println("Se requiere cambiar a NIVER SUPERIOR ...");
					this.seTieneQueGenerarUnNuevoContextoParaWatsonEnElWorkspaceActualConRespaldo();;
					//cambiarAWorkspaceGeneral();
					cambiarANivelSuperior();
					respuesta = enviarRespuestaAWatson(respuestaDelCliente, frase); // General
				}else{
					this.seTieneQueGenerarUnNuevoContextoParaWatsonEnElWorkspaceActualConRespaldo();
				}
				cambiarDeTema = true;
			}else{
				if(frase.esMandatorio()){	
					numeroDeIntentosActualesEnRepetirUnaPregunta ++;
				}
			}
		}else{
			// Actualizar contexto
			//misConversacionesConWatson.get(nombreDeWorkspaceActual).setElContextoConWatson(respuesta.getMiContexto());
			miTopico.actualizarContexto(respuesta.getMiContexto());
			if(numeroDeIntentosActualesEnRepetirUnaPregunta == maximoIntentos){
				// Abordar el tema
				cambiarDeTema = true; // Buscar otro tema
				//cambiarAWorkspaceGeneral();
				cambiarANivelSuperior();
				noEntendiLaUltimaRespuesta = true;
				// TODO decir frase me rindo
				abordarElTemaPorNOLoEntendi = true;
			}else{
				abordarElTemaPorNOLoEntendi = false;
				
				// Analizar si tengo que cambiar de workspace
				cambiarDeTema = respuesta.seTerminoElTema() || respuesta.quiereCambiarIntencion();
				if(cambiarDeTema){
					String laIntencion = respuesta.obtenerLaIntencionDeConfianzaDeLaRespuesta().getNombre();
					if( ! laIntencion.equals("")){
						borrarUnaVariableDelContexto(Constantes.ID_TEMA); // Solo se borra el id cuando el tema termina
						if(! laIntencion.equals("afirmacion")){ // TODO Esto no deberia ir aca
							//nombreDeLaIntencionEspecificaActiva = laIntencion;
							nombreDeLaIntencionGeneralActiva = laIntencion;
						}
						seTieneQueGenerarUnNuevoContextoParaWatsonEnElWorkspaceActualConRespaldo();
					}
				}
			}
			numeroDeIntentosActualesEnRepetirUnaPregunta = 0;
		}
		borrarUnaVariableDelContexto(Constantes.ANYTHING_ELSE);
	    borrarUnaVariableDelContexto(Constantes.NODO_ACTIVADO);
	    borrarUnaVariableDelContexto(Constantes.ORACIONES_AFIRMATIVAS);
	    borrarUnaVariableDelContexto(Constantes.CAMBIAR_A_GENERAL);
	    borrarUnaVariableDelContexto(Constantes.TERMINO_EL_TEMA);
		borrarUnaVariableDelContexto(Constantes.CAMBIAR_INTENCION);
		
		return respuesta;
	}
	
	private WorkSpace extraerUnWorkspaceConLaIntencion(String nombreDeLaIntencion){
		for(WorkSpace workspace: misWorkSpaces){
			if(workspace.tieneLaIntencion(nombreDeLaIntencion)){
				return workspace;
			}
		}
		return null;
	}
	
	public void seTieneQueGenerarUnNuevoContextoParaWatsonEnElWorkspaceActualConRespaldo(){
		// Respaldar la variables de contexto que tengo
		//Hashtable<String, String> misVariables = respaldarVariablesDeContexto(misConversacionesConWatson.get(nombreDeWorkspaceActual).getElContextoConWatson());
		Hashtable<String, String> misVariables = respaldarVariablesDeContexto(miTopico.obtenerElContexto());

		// Generar la nueva converzacion
		seTieneQueGenerarUnNuevoContextoParaWatsonEnElWorkspaceActual();
		
		// Agregar a la nueva converzacion el respaldo de las variables de contexto
		agregarVariablesDeContexto(misVariables);
	}
	
	public void seTieneQueGenerarUnNuevoContextoParaWatsonEnElWorkspaceActual(){
		//ConversacionConWatson conversacion = misConversacionesConWatson.get(nombreDeWorkspaceActual);
		//String nuevoContexto = conversacion.enviarMSG("", null).getContext().toString();
		//misConversacionesConWatson.get(nombreDeWorkspaceActual).setElContextoConWatson(nuevoContexto);
		miTopico.reiniciarContexto();
	}
	
	private Hashtable<String, String> respaldarVariablesDeContexto(String contexto){
		Hashtable<String, String> misVariables = new Hashtable<String, String>();
		
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(contexto);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		Iterator<?> json_keys = jsonObj.keys();
		while( json_keys.hasNext() ){
			String json_key = (String) json_keys.next();
			if( ! json_key.equals("system") && ! json_key.equals("conversation_id")){
				try {
					System.out.println("Respaldando: "+json_key);
					misVariables.put(json_key, jsonObj.getString(json_key));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return misVariables;
	}
	
	private void agregarVariablesDeContexto(Hashtable<String, String> variables){
		Enumeration<String> misLlaves = variables.keys();
		while(misLlaves.hasMoreElements()){
			String key = misLlaves.nextElement();
			activarValiableEnElContextoDeWatson(key, variables.get(key));
		}
	}
		
	public Intent determinarLaIntencionGeneral(String mensaje){
		//Intent intencion = determinarLaIntencionDeConfianzaEnUnWorkspace(mensaje, nombreDelWorkSpaceGeneral);
		Intent intencion = determinarLaIntencionDeConfianzaEnUnWorkspace(mensaje);
		return intencion;
	}
	
	//private Intent determinarLaIntencionDeConfianzaEnUnWorkspace(String mensaje, String nombreDelWorkSpace){
	private Intent determinarLaIntencionDeConfianzaEnUnWorkspace(String mensaje){
		//List<Intent> intenciones = llamarAWatson(mensaje, nombreDelWorkSpace).getIntents();
		List<Intent> intenciones = llamarAWatson(mensaje).getIntents();
		Intent intencion = null;
		double confidence = 0;
		
		for(int index = 0; index < intenciones.size(); index ++){
			if(intenciones.get(index).getConfidence() > confidence){
				confidence = intenciones.get(index).getConfidence();
				intencion = intenciones.get(index);
			}
		}
		//System.out.println("La intencion general en "+nombreDelWorkSpace+" es: "+intencion.getIntent());
		return intencion;
	}
	
	private boolean elClienteQuiereCambiarDeIntencionGeneral(Intent intencion){
		if(intencion != null){
			return (intencion.getConfidence() >= Constantes.WATSON_CONVERSATION_CONFIDENCE); 
		}else{
			return false;
		}
	}
	
	public boolean hayQueCambiarDeTema(){
		return cambiarDeTema;
	}
	
	public void yaNoCambiarDeTema(){
		cambiarDeTema = false;
	}
	
	public void cambiarDeTema(){
		cambiarDeTema = true;
	}
	
	public boolean entendiLaUltimaPregunta(){
		return ! noEntendiLaUltimaRespuesta;
	}
	
	public String obtenerNombreDelWorkspaceActual(){
		return nombreDeWorkspaceActual;
	}
	
	/*public void establecerNombreDelWorkspaceActual(String nombreDelWorkSpace){
		estaEnElWorkspaceGeneral = false;
		nombreDeWorkspaceActual = nombreDelWorkSpace;
	}*/
	
	/*public void cambiarAWorkspaceGeneral(){
		estaEnElWorkspaceGeneral = true;
		nombreDeWorkspaceActual = nombreDelWorkSpaceGeneral;
	}*/
	
	public void cambiarANivelSuperior(){
		this.hayQueEvaluarEnNivelSuperior = true;
	}
	
	public void activarTemaEnElContextoDeWatson(String nombreTema){
		activarValiableEnElContextoDeWatson(Constantes.ID_TEMA, nombreTema);
		//System.out.println("Contexto modificado: "+misConversacionesConWatson.get(nombreDeWorkspaceActual).getElContextoConWatson());
		System.out.println("Contexto modificado: "+miTopico.obtenerElContexto());
	}
	
	public void activarTemaEnElContextoDeWatsonEnWorkspaceEspecifico(String nombreTema, String nombreWorkspace){
		//activarValiableEnElContextoDeWatson(Constantes.ID_TEMA, nombreTema, nombreWorkspace);
		activarValiableEnElContextoDeWatson(Constantes.ID_TEMA, nombreTema);
		//System.out.println("Contexto modificado: "+misConversacionesConWatson.get(nombreWorkspace).getElContextoConWatson());
		System.out.println("Contexto modificado: "+miTopico.obtenerElContexto());
	}
	
	/*public void activarValiableEnElContextoDeWatson(String nombre, String valor){
		activarValiableEnElContextoDeWatson(nombre, valor);
	}*/
	
	//public void activarValiableEnElContextoDeWatson(String nombre, String valor, String nombreWorkspace){
	public void activarValiableEnElContextoDeWatson(String nombre, String valor){
		//String context = misConversacionesConWatson.get(nombreWorkspace).getElContextoConWatson();
		String context = miTopico.obtenerElContexto();

		System.out.println(context);
		JSONObject obj = null;
		try {
			obj = new JSONObject(context);
			obj.put(nombre, valor);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//misConversacionesConWatson.get(nombreWorkspace).setElContextoConWatson(obj.toString());
		miTopico.actualizarContexto(obj.toString());
	}
	
	//public void activarValiableEnElContextoDeWatson(String nombre, double valor, String nombreWorkspace){
	public void activarValiableEnElContextoDeWatson(String nombre, double valor){
		//String context = misConversacionesConWatson.get(nombreWorkspace).getElContextoConWatson();
		String context = miTopico.obtenerElContexto();
		System.out.println(context);
		JSONObject obj = null;
		try {
			obj = new JSONObject(context);
			obj.put(nombre, valor);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		miTopico.actualizarContexto(obj.toString());
		//misConversacionesConWatson.get(nombreWorkspace).setElContextoConWatson(obj.toString());
	}
	
	public void borrarUnaVariableDelContexto(String nombreDeLaVariable){
		//borrarUnaVariableDelContextoEnUnWorkspace(nombreDeLaVariable, nombreDeWorkspaceActual);
		borrarUnaVariableDelContextoEnUnWorkspace(nombreDeLaVariable);
	}
	
	//public void borrarUnaVariableDelContextoEnUnWorkspace(String nombreDeLaVariable, String nombreWorkspace){
	public void borrarUnaVariableDelContextoEnUnWorkspace(String nombreDeLaVariable){
		//String context = misConversacionesConWatson.get(nombreWorkspace).getElContextoConWatson();
		String context = miTopico.obtenerElContexto();
		JSONObject obj = null;
		try {
			obj = new JSONObject(context);
			obj.remove(nombreDeLaVariable);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("Contexto modificado: "+obj.toString());
		//misConversacionesConWatson.get(nombreWorkspace).setElContextoConWatson(obj.toString());
		miTopico.actualizarContexto(obj.toString());
	}
	
	
	public Respuesta inicializarTemaEnWatson(String respuestaDelCliente){
		//return inicializarTemaEnWatsonWorkspaceEspecifico(respuestaDelCliente, nombreDeWorkspaceActual);
		return inicializarTemaEnWatsonWorkspaceEspecifico(respuestaDelCliente);
	}
	
	//public Respuesta inicializarTemaEnWatsonWorkspaceEspecifico(String respuestaDelCliente, String nombreWorkspace){
	public Respuesta inicializarTemaEnWatsonWorkspaceEspecifico(String respuestaDelCliente){
		//Respuesta respuesta = new Respuesta(misConversacionesConWatson.get(nombreWorkspace), misConversacionesConWatson.get(nombreWorkspace).getElContextoConWatson());
		//respuesta.llamarAWatson(respuestaDelCliente);
		
		Respuesta respuesta = miTopico.hablarConWatson(null, respuestaDelCliente);
		
		//String context = response.getContext().toString();
		String context = respuesta.messageResponse().getContext().toString();
		//misConversacionesConWatson.get(nombreWorkspace).setElContextoConWatson(context);
		miTopico.actualizarContexto(context);
		
		/*borrarUnaVariableDelContextoEnUnWorkspace(Constantes.ANYTHING_ELSE, nombreWorkspace);
		borrarUnaVariableDelContextoEnUnWorkspace(Constantes.NODO_ACTIVADO, nombreWorkspace);
		borrarUnaVariableDelContextoEnUnWorkspace(Constantes.ORACIONES_AFIRMATIVAS, nombreWorkspace);
		borrarUnaVariableDelContextoEnUnWorkspace(Constantes.CAMBIAR_A_GENERAL, nombreWorkspace);
		borrarUnaVariableDelContextoEnUnWorkspace(Constantes.TERMINO_EL_TEMA, nombreWorkspace);
		borrarUnaVariableDelContextoEnUnWorkspace(Constantes.CAMBIAR_INTENCION, nombreWorkspace);*/
		
		borrarUnaVariableDelContextoEnUnWorkspace(Constantes.ANYTHING_ELSE);
		borrarUnaVariableDelContextoEnUnWorkspace(Constantes.NODO_ACTIVADO);
		borrarUnaVariableDelContextoEnUnWorkspace(Constantes.ORACIONES_AFIRMATIVAS);
		borrarUnaVariableDelContextoEnUnWorkspace(Constantes.CAMBIAR_A_GENERAL);
		borrarUnaVariableDelContextoEnUnWorkspace(Constantes.TERMINO_EL_TEMA);
		borrarUnaVariableDelContextoEnUnWorkspace(Constantes.CAMBIAR_INTENCION);
		
		return respuesta;
	}
	
	public String obtenerNodoActivado(MessageResponse response){
		try{
			return response.getContext().get(Constantes.NODO_ACTIVADO).toString();
		}catch(Exception e){
			System.out.println("No existe el id nodo");
			return "";
		}
	}
	
	public String obtenerNombreDeLaIntencionGeneralActiva() {
		return this.nombreDeLaIntencionGeneralActiva;
	}
	
	/*
	public String obtenernombreDeLaIntencionEspecificaActiva(){
		return nombreDeLaIntencionEspecificaActiva;
	}
	
	public void establecerNombreDeLaIntencionEspecificaActiva(String nombreDeLaIntencion) {
		this.nombreDeLaIntencionEspecificaActiva = nombreDeLaIntencion;
	}*/
	
	public boolean hayIntencionNoAsociadaANingunWorkspace(){
		return this.hayIntencionNoAsociadaANingunWorkspace;
	}
	
	public boolean seTieneQueAbordarElTema(){
		return abordarElTemaPorNOLoEntendi;
	}
	
	public void yaNoSeTieneQueAbordarElTema(){
		abordarElTemaPorNOLoEntendi = false;
	}
	
	public String obtenerElContexto(){
		return miTopico.obtenerElContexto();
	}
	
	public abstract Salida decirUnaFrase(Frase frase, Respuesta respuesta, Tema tema, Cliente cliente, ModoDeLaVariable modoDeResolucionDeResultadosFinales);
	
	public abstract Salida volverAPreguntarUnaFrase(Frase pregunta, Respuesta respuesta, Tema tema, Cliente cliente, ModoDeLaVariable modoDeResolucionDeResultadosFinales);
	
	public abstract Salida volverAPreguntarUnaFraseConMeRindo(Frase pregunta, Respuesta respuesta, Tema tema, boolean meRindo, Cliente cliente, ModoDeLaVariable modoDeResolucionDeResultadosFinales);
	
	public static void main(String[] args) throws Exception {
		/*Agente agente = new Agente();
		agente.respaldarVariablesDeContexto("{system={dialog_request_counter=2.0, dialog_stack=[{dialog_node=node_2_1479401409144}], dialog_turn_counter=2.0}, idTema=quiereMovimientos, conversation_id=95225b89-891d-43a5-9db2-2a190d730343, quiereMovimientos=true, oracionesAfirmativas=[movimientos], nodo=preguntarPorOtraConsulta}");*/
	}
}
