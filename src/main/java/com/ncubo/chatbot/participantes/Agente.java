package com.ncubo.chatbot.participantes;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.ibm.watson.developer_cloud.conversation.v1.model.Intent;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.exceptiones.ChatException;
import com.ncubo.chatbot.partesDeLaConversacion.Frase;
import com.ncubo.chatbot.partesDeLaConversacion.Respuesta;
import com.ncubo.chatbot.watson.ConversationWatson;
import com.ncubo.chatbot.watson.WorkSpace;

// Es como el watson de Ncubo
public class Agente extends Participante{

	private final ArrayList<WorkSpace> miWorkSpaces;
	private final Hashtable<String, String> miContextos = new Hashtable<String, String>();
	private final Hashtable<String, ConversationWatson> miWatsonConversacions = new Hashtable<String, ConversationWatson>();
	private String nombreDelWorkSpaceGeneral;
	private static final int MAXIMO_DE_INTENTOS_OPCIONALES = 4; // Sino se aborda el tema
	private String nombreDeWorkspaceActual;
	private String nombreDeLaIntencionGeneralActiva;
	private String nombreDeLaIntencionEspecificaActiva;
	private boolean estaEnElWorkspaceGeneral;
	private boolean noEntendiLaUltimaRespuesta;
	private boolean noEntendiLaUltimaRespuestaWSEspecifico;
	private int numeroDeIntentosActualesEnRepetirUnaPregunta;
	private int numeroDeIntentosActualesEnRepetirUnaPreguntaWSEspecifico;
	private boolean cambiarDeTema = false;
	private boolean cambiarDeTemaWSEspecifico = true;
	private boolean abordarElTemaPorNOLoEntendiEspecifico = false;
	private boolean abordarElTemaPorNOLoEntendi = false;
	private boolean hayIntencionNoAsociadaANingunWorkspace;
	
	public Agente(ArrayList<WorkSpace> miWorkSpaces){
		this.noEntendiLaUltimaRespuesta = true;
		this.noEntendiLaUltimaRespuestaWSEspecifico = true;
		this.estaEnElWorkspaceGeneral = true;
		this.miWorkSpaces = miWorkSpaces;
		this.nombreDelWorkSpaceGeneral = "";
		this.nombreDeLaIntencionGeneralActiva = "";
		this.nombreDeLaIntencionEspecificaActiva = "";
		this.numeroDeIntentosActualesEnRepetirUnaPregunta = 1;
		this.numeroDeIntentosActualesEnRepetirUnaPreguntaWSEspecifico = 1;
		this.hayIntencionNoAsociadaANingunWorkspace = false;
		this.inicializarContextos();
	}
	
	public Agente(){
		miWorkSpaces = null;
	}
	
	private void inicializarContextos(){
		for(WorkSpace workspace: miWorkSpaces){
			ConversationWatson conversacion = new ConversationWatson(workspace.getUsuarioIBM(), workspace.getContrasenaIBM(), workspace.getIdIBM());
			String contexto = conversacion.enviarMSG("", null).getContext().toString();
			miWatsonConversacions.put(workspace.getNombre(), conversacion);
			miContextos.put(workspace.getNombre(), contexto);
			System.out.println("En el workspace "+workspace.getNombre()+" se tiene el Contexto: "+contexto);
			if(workspace.getTipo().equals(Constantes.WORKSPACE_GENERAL)){
				nombreDeWorkspaceActual = workspace.getNombre();
				nombreDelWorkSpaceGeneral = workspace.getNombre();
			}
		}
		
		if(nombreDelWorkSpaceGeneral == ""){
			throw new ChatException("No existe un workspace general en para conectarse a Watson IBM");
		}
	}
	
	public MessageResponse llamarAWatson(String mensaje, String nombreWorkspace){
		return miWatsonConversacions.get(nombreWorkspace).enviarAWatson(mensaje, miContextos.get(nombreWorkspace));
	}
	
	public Respuesta enviarRespuestaAWatson(String respuestaDelCliente, Frase frase){
		Respuesta respuesta = null;
		if(estaEnElWorkspaceGeneral){
			respuesta = analizarRespuestaGeneral(respuestaDelCliente, frase);
		}else{
			respuesta = analizarRespuesta(respuestaDelCliente, frase);
		}
		
		return respuesta;
	}
	
	public Respuesta analizarRespuestaGeneral(String respuestaDelCliente, Frase frase){
		Respuesta respuesta = null;
		
		respuesta = new Respuesta(frase, miWatsonConversacions.get(nombreDeWorkspaceActual), miContextos.get(nombreDeWorkspaceActual));
		respuesta.llamarAWatson(respuestaDelCliente);
		
		noEntendiLaUltimaRespuesta = (! respuesta.entendiLaRespuesta()) && (frase.esMandatorio()) && 
				(numeroDeIntentosActualesEnRepetirUnaPregunta != MAXIMO_DE_INTENTOS_OPCIONALES);
		if(noEntendiLaUltimaRespuesta){
			numeroDeIntentosActualesEnRepetirUnaPregunta ++;
		}else{
			if(numeroDeIntentosActualesEnRepetirUnaPregunta == MAXIMO_DE_INTENTOS_OPCIONALES){
				// Abordar el tema
				abordarElTemaPorNOLoEntendi = true; // Buscar otro tema
				cambiarDeTema = true; // Buscar otro tema
			}else{
				// Actualizar contexto
				miContextos.put(nombreDeWorkspaceActual, respuesta.getMiContexto());
				abordarElTemaPorNOLoEntendi = false;
				
				// Analizar si ya tengo que cambiar de workspace
				try{
					String intencionDelCliente = respuesta.obtenerLaIntencionDeConfianzaDeLaRespuesta().getNombre();
					WorkSpace workspace = extraerUnWorkspaceConLaIntencion(intencionDelCliente);
					if( (nombreDeWorkspaceActual.equals(nombreDelWorkSpaceGeneral)) && workspace != null && ! intencionDelCliente.equals("")){
						nombreDeWorkspaceActual = workspace.getNombre();
						nombreDeLaIntencionGeneralActiva = intencionDelCliente;
						System.out.println(String.format("Cambiando al workspace %s e intencion %s", nombreDeWorkspaceActual, nombreDeLaIntencionGeneralActiva));
						cambiarDeTema = true; // Buscar otro tema
						nombreDeLaIntencionEspecificaActiva = determinarLaIntencionDeConfianzaEnUnWorkspace(respuestaDelCliente, nombreDeWorkspaceActual).getIntent();
						abordarElTemaPorNOLoEntendi = false;
						estaEnElWorkspaceGeneral = false;
						this.hayIntencionNoAsociadaANingunWorkspace = false;
					}else{
						System.out.println("Intencion no asociada a ningun workspace");
						if (! intencionDelCliente.equals("")){
							nombreDeLaIntencionGeneralActiva = intencionDelCliente;	
						}else{
							nombreDeLaIntencionGeneralActiva = Constantes.INTENCION_NO_ENTIENDO;
						}
						nombreDeLaIntencionEspecificaActiva = "";
						hayIntencionNoAsociadaANingunWorkspace = true;
					}
				}catch(Exception e){
					System.out.println("No hay ninguna intencion real o de confianza");
					nombreDeLaIntencionGeneralActiva = Constantes.INTENCION_NO_ENTIENDO;
					nombreDeLaIntencionEspecificaActiva = "";
					hayIntencionNoAsociadaANingunWorkspace = true;
				}
				
			}
			numeroDeIntentosActualesEnRepetirUnaPregunta = 1;
		}
		
		return respuesta;
	}
	
	public Respuesta analizarRespuesta(String respuestaDelCliente, Frase frase){
		Respuesta respuesta = null;
		
		respuesta = new Respuesta(frase, miWatsonConversacions.get(nombreDeWorkspaceActual), miContextos.get(nombreDeWorkspaceActual));
		respuesta.llamarAWatson(respuestaDelCliente);
		
		noEntendiLaUltimaRespuesta = (! (respuesta.entendiLaRespuesta() && ! respuesta.hayAlgunAnythingElse())) && 
				(numeroDeIntentosActualesEnRepetirUnaPregunta != MAXIMO_DE_INTENTOS_OPCIONALES);
		if(noEntendiLaUltimaRespuesta){
			System.out.println("No entendi la respuesta ...");
			// Validar si es que el usuario cambio de intencion general
			Intent miIntencion = this.determinarLaIntencionGeneral(respuestaDelCliente);
			if(elClienteQuiereCambiarDeIntencionGeneral(miIntencion)){
				if(! miIntencion.getIntent().equals(nombreDeLaIntencionGeneralActiva)){
					System.out.println("Se requiere cambiar a WROKSPACE GENERAL ...");
					this.seTieneQueGenerarUnNuevoContextoParaWatsonEnElWorkspaceActualConRespaldo();;
					cambiarAWorkspaceGeneral();
					respuesta = enviarRespuestaAWatson(respuestaDelCliente, frase); // General
					//respuesta = enviarRespuestaAWatson(respuestaDelClinete, frase); // Especifico
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
			if(numeroDeIntentosActualesEnRepetirUnaPregunta == MAXIMO_DE_INTENTOS_OPCIONALES){
				// Abordar el tema
				cambiarDeTema = true; // Buscar otro tema
				// Actualizar contexto
				miContextos.put(nombreDeWorkspaceActual, respuesta.getMiContexto());
				cambiarAWorkspaceGeneral();
				noEntendiLaUltimaRespuesta = true;
				// TODO decir frase me rindo
				abordarElTemaPorNOLoEntendi = true;
			}else{
				// Actualizar contexto
				miContextos.put(nombreDeWorkspaceActual, respuesta.getMiContexto());
				abordarElTemaPorNOLoEntendi = false;
				
				// Analizar si tengo que cambiar de workspace
				cambiarDeTema = respuesta.seTerminoElTema() || respuesta.quiereCambiarIntencion();
				if(cambiarDeTema){
					String laIntencion = respuesta.obtenerLaIntencionDeConfianzaDeLaRespuesta().getNombre();
					if( ! laIntencion.equals("")){
						borrarUnaVariableDelContexto(Constantes.ID_TEMA); // Solo se borra el id cuando el tema termina
						if(! laIntencion.equals("afirmacion")) // TODO Esto no deberia ir aca
						{
							nombreDeLaIntencionEspecificaActiva = laIntencion;
						}
						seTieneQueGenerarUnNuevoContextoParaWatsonEnElWorkspaceActualConRespaldo();
					}
				}
			}
			numeroDeIntentosActualesEnRepetirUnaPregunta = 1;
		}
		borrarUnaVariableDelContexto(Constantes.ANYTHING_ELSE);
	    borrarUnaVariableDelContexto(Constantes.NODO_ACTIVADO);
	    borrarUnaVariableDelContexto(Constantes.ORACIONES_AFIRMATIVAS);
	    borrarUnaVariableDelContexto(Constantes.CAMBIAR_A_GENERAL);
	    borrarUnaVariableDelContexto(Constantes.TERMINO_EL_TEMA);
		borrarUnaVariableDelContexto(Constantes.CAMBIAR_INTENCION);
		
		return respuesta;
	}

	public Respuesta analizarRespuestaWSEspecifico(String respuestaDelCliente, Frase frase, String nombreWorkSpace){
		
		Respuesta respuesta = new Respuesta(frase, miWatsonConversacions.get(nombreWorkSpace), miContextos.get(nombreWorkSpace));
		respuesta.llamarAWatson(respuestaDelCliente);
		
		noEntendiLaUltimaRespuestaWSEspecifico = (! (respuesta.entendiLaRespuesta() && ! respuesta.hayAlgunAnythingElse())) && 
				(numeroDeIntentosActualesEnRepetirUnaPreguntaWSEspecifico != MAXIMO_DE_INTENTOS_OPCIONALES);
		if(noEntendiLaUltimaRespuestaWSEspecifico){
			System.out.println("No entendi la respuesta ...");
			if(frase.esMandatorio()){	
				numeroDeIntentosActualesEnRepetirUnaPreguntaWSEspecifico ++;
			}
		}else{
			if(numeroDeIntentosActualesEnRepetirUnaPreguntaWSEspecifico == MAXIMO_DE_INTENTOS_OPCIONALES){
				// Abordar el tema
				cambiarDeTemaWSEspecifico = true;
				miContextos.put(nombreWorkSpace, respuesta.getMiContexto());
				
				noEntendiLaUltimaRespuesta = true;
				// TODO decir frase me rindo
				abordarElTemaPorNOLoEntendiEspecifico = true;
				
			}else{
				abordarElTemaPorNOLoEntendiEspecifico = false;
				// Actualizar contexto
				miContextos.put(nombreWorkSpace, respuesta.getMiContexto());
				
				// Analizar si tengo que cambiar de workspace
				cambiarDeTemaWSEspecifico = respuesta.seTerminoElTema() || respuesta.quiereCambiarIntencion();
				if(cambiarDeTemaWSEspecifico){
					borrarUnaVariableDelContextoEnUnWorkspace(Constantes.ID_TEMA, nombreWorkSpace); // Solo se borra el id cuando el tema termina
				}
			}
			numeroDeIntentosActualesEnRepetirUnaPreguntaWSEspecifico = 1;
		}
		borrarUnaVariableDelContextoEnUnWorkspace(Constantes.ANYTHING_ELSE, nombreWorkSpace);
		borrarUnaVariableDelContextoEnUnWorkspace(Constantes.NODO_ACTIVADO, nombreWorkSpace);
		borrarUnaVariableDelContextoEnUnWorkspace(Constantes.ORACIONES_AFIRMATIVAS, nombreWorkSpace);
		borrarUnaVariableDelContextoEnUnWorkspace(Constantes.CAMBIAR_A_GENERAL, nombreWorkSpace);
		borrarUnaVariableDelContextoEnUnWorkspace(Constantes.TERMINO_EL_TEMA, nombreWorkSpace);
		borrarUnaVariableDelContextoEnUnWorkspace(Constantes.CAMBIAR_INTENCION, nombreWorkSpace);
		
		return respuesta;
	}
	
	private WorkSpace extraerUnWorkspaceConLaIntencion(String nombreDeLaIntencion){
		for(WorkSpace workspace: miWorkSpaces){
			if(workspace.tieneLaIntencion(nombreDeLaIntencion)){
				return workspace;
			}
		}
		return null;
	}
	
	public void seTieneQueGenerarUnNuevoContextoParaWatsonEnElWorkspaceActualConRespaldo(){
		// Respaldar la variables de contexto que tengo
		Hashtable<String, String> misVariables = respaldarVariablesDeContexto(miContextos.get(nombreDeWorkspaceActual));
		
		// Generar la nueva converzacion
		seTieneQueGenerarUnNuevoContextoParaWatsonEnElWorkspaceActual();
		
		// Agregar a la nueva converzacion el respaldo de las variables de contexto
		agregarVariablesDeContexto(misVariables);
	}
	
	public void seTieneQueGenerarUnNuevoContextoParaWatsonEnElWorkspaceActual(){
		ConversationWatson conversacion = miWatsonConversacions.get(nombreDeWorkspaceActual);
		String nuevoContexto = conversacion.enviarMSG("", null).getContext().toString();
		miContextos.put(nombreDeWorkspaceActual, nuevoContexto);
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
			if( ! json_key.equals("system") && ! json_key.equals("conversation_id") && ! json_key.equals("estaLogueado")){
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
		
	private Intent determinarLaIntencionGeneral(String mensaje){
		Intent intencion = determinarLaIntencionDeConfianzaEnUnWorkspace(mensaje, nombreDelWorkSpaceGeneral);
		return intencion;
	}
	
	private Intent determinarLaIntencionDeConfianzaEnUnWorkspace(String mensaje, String nombreDelWorkSpace){
		List<Intent> intenciones = llamarAWatson(mensaje, nombreDelWorkSpace).getIntents();
		Intent intencion = null;
		double confidence = 0;
		
		for(int index = 0; index < intenciones.size(); index ++){
			if(intenciones.get(index).getConfidence() > confidence){
				confidence = intenciones.get(index).getConfidence();
				intencion = intenciones.get(index);
			}
		}
		System.out.println("La intencion general en "+nombreDelWorkSpace+" es: "+intencion.getIntent());
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
	
	public boolean hayQueCambiarDeTemaWSEspecifico(){
		return cambiarDeTemaWSEspecifico;
	}
	
	public void yaNoCambiarDeTemaWSEspecifico(){
		cambiarDeTemaWSEspecifico = false;
	}
	
	public void cambiarDeTemaWSEspecifico(){
		cambiarDeTemaWSEspecifico = true;
	}
	
	public boolean entendiLaUltimaPregunta(){
		return ! noEntendiLaUltimaRespuesta;
	}
	
	public boolean entendiLaUltimaPreguntaWSEspecifico(){
		return ! noEntendiLaUltimaRespuestaWSEspecifico;
	}
	
	public String obtenerNombreDelWorkspaceActual(){
		return nombreDeWorkspaceActual;
	}
	
	public void establecerNombreDelWorkspaceActual(String nombreDelWorkSpace){
		estaEnElWorkspaceGeneral = false;
		nombreDeWorkspaceActual = nombreDelWorkSpace;
	}
	
	public void cambiarAWorkspaceGeneral(){
		estaEnElWorkspaceGeneral = true;
		nombreDeWorkspaceActual = nombreDelWorkSpaceGeneral;
	}
	
	public void activarTemaEnElContextoDeWatson(String nombreTema){
		activarValiableEnElContextoDeWatson(Constantes.ID_TEMA, nombreTema);
		System.out.println("Contexto modificado: "+miContextos.get(nombreDeWorkspaceActual));
	}
	
	public void activarTemaEnElContextoDeWatsonEnWorkspaceEspecifico(String nombreTema, String nombreWorkspace){
		activarValiableEnElContextoDeWatson(Constantes.ID_TEMA, nombreTema, nombreWorkspace);
		System.out.println("Contexto modificado: "+miContextos.get(nombreWorkspace));
	}
	
	public void activarValiableEnElContextoDeWatson(String nombre, String valor){
		activarValiableEnElContextoDeWatson(nombre, valor, nombreDeWorkspaceActual);
	}
	
	public void activarValiableEnElContextoDeWatson(String nombre, String valor, String nombreWorkspace){
		String context = miContextos.get(nombreWorkspace);
		System.out.println(context);
		JSONObject obj = null;
		try {
			obj = new JSONObject(context);
			obj.put(nombre, valor);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		miContextos.put(nombreWorkspace, obj.toString());
	}
	
	public void activarValiableEnElContextoDeWatson(String nombre, double valor, String nombreWorkspace){
		String context = miContextos.get(nombreWorkspace);
		System.out.println(context);
		JSONObject obj = null;
		try {
			obj = new JSONObject(context);
			obj.put(nombre, valor);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		miContextos.put(nombreWorkspace, obj.toString());
	}
	
	public void borrarUnaVariableDelContexto(String nombreDeLaVariable){
		borrarUnaVariableDelContextoEnUnWorkspace(nombreDeLaVariable, nombreDeWorkspaceActual);
	}
	
	public void borrarUnaVariableDelContextoEnUnWorkspace(String nombreDeLaVariable, String nombreWorkspace){
		String context = miContextos.get(nombreWorkspace);
		JSONObject obj = null;
		try {
			obj = new JSONObject(context);
			obj.remove(nombreDeLaVariable);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("Contexto modificado: "+obj.toString());
		miContextos.put(nombreWorkspace, obj.toString());
	}
	
	public Respuesta inicializarTemaEnWatson(String respuestaDelCliente){
		return inicializarTemaEnWatsonWorkspaceEspecifico(respuestaDelCliente, nombreDeWorkspaceActual);
	}
	
	public Respuesta inicializarTemaEnWatsonWorkspaceEspecifico(String respuestaDelCliente, String nombreWorkspace){
		Respuesta respuesta = new Respuesta(miWatsonConversacions.get(nombreWorkspace), miContextos.get(nombreWorkspace));
		//MessageResponse response = miWatsonConversacions.get(nombreDeWorkspaceActual).enviarAWatson(respuestaDelCliente, miContextos.get(nombreDeWorkspaceActual));
		respuesta.llamarAWatson(respuestaDelCliente);
		//String context = response.getContext().toString();
		String context = respuesta.messageResponse().getContext().toString();
		miContextos.put(nombreWorkspace, context);
		
		borrarUnaVariableDelContextoEnUnWorkspace(Constantes.ANYTHING_ELSE, nombreWorkspace);
		borrarUnaVariableDelContextoEnUnWorkspace(Constantes.NODO_ACTIVADO, nombreWorkspace);
		borrarUnaVariableDelContextoEnUnWorkspace(Constantes.ORACIONES_AFIRMATIVAS, nombreWorkspace);
		borrarUnaVariableDelContextoEnUnWorkspace(Constantes.CAMBIAR_A_GENERAL, nombreWorkspace);
		borrarUnaVariableDelContextoEnUnWorkspace(Constantes.TERMINO_EL_TEMA, nombreWorkspace);
		borrarUnaVariableDelContextoEnUnWorkspace(Constantes.CAMBIAR_INTENCION, nombreWorkspace);
		
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
	
	public void establecerNombreDeLaIntencionGeneralActiva(String nombreDeLaIntencion) {
		this.nombreDeLaIntencionGeneralActiva = nombreDeLaIntencion;
	}
	
	public String obtenernombreDeLaIntencionEspecificaActiva(){
		return nombreDeLaIntencionEspecificaActiva;
	}
	
	public void establecerNombreDeLaIntencionEspecificaActiva(String nombreDeLaIntencion) {
		this.nombreDeLaIntencionEspecificaActiva = nombreDeLaIntencion;
	}
	
	public boolean hayIntencionNoAsociadaANingunWorkspace(){
		return this.hayIntencionNoAsociadaANingunWorkspace;
	}
	
	public static void main(String[] args) throws Exception {
		Agente agente = new Agente();
		agente.respaldarVariablesDeContexto("{system={dialog_request_counter=2.0, dialog_stack=[{dialog_node=node_2_1479401409144}], dialog_turn_counter=2.0}, idTema=quiereMovimientos, conversation_id=95225b89-891d-43a5-9db2-2a190d730343, quiereMovimientos=true, oracionesAfirmativas=[movimientos], nodo=preguntarPorOtraConsulta}");
	}
	
	public boolean seTieneQueAbordarElTema(){
		return abordarElTemaPorNOLoEntendi;
	}
	
	public void yaNoSeTieneQueAbordarElTema(){
		abordarElTemaPorNOLoEntendi = false;
	}
	
	public boolean seTieneQueAbordarElTemaEspecifico(){
		return abordarElTemaPorNOLoEntendiEspecifico;
	}
	
	public void yaNoSeTieneQueAbordarElTemaEspecifico(){
		abordarElTemaPorNOLoEntendiEspecifico = false;
	}
	
}
