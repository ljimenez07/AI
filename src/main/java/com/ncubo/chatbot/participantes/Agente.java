package com.ncubo.chatbot.participantes;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

public abstract class Agente extends Participante{

	private final ArrayList<WorkSpace> misWorkSpaces;
	private Topico miTopico;
	private String nombreDeWorkspaceActual;
	private String nombreDeLaIntencionGeneralActiva;
	private boolean hayQueEvaluarEnNivelSuperior;
	private boolean noEntendiLaUltimaRespuesta;
	private int numeroDeIntentosActualesEnRepetirUnaPregunta;
	private boolean cambiarDeTema = false;
	private boolean cambiarDeTemaForzosamente = false;
	private boolean pareceQueQuiereCambiarDeTemaForzosamente = false;
	private boolean abordarElTemaPorNOLoEntendi = false;
	private boolean hayIntencionNoAsociadaANingunWorkspace;
	private LogDeLaConversacion miHistorico = new LogDeLaConversacion();
	private BitacoraDao miBitacora;
	
	public Agente(ArrayList<WorkSpace> miWorkSpaces){
		this.noEntendiLaUltimaRespuesta = true;
		this.hayQueEvaluarEnNivelSuperior = true;
		this.misWorkSpaces = miWorkSpaces;
		this.nombreDeLaIntencionGeneralActiva = "";
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
	}
	
	public MessageResponse llamarAWatson(String mensaje){
		return miTopico.hablarConWatsonEnElNivelSuperior(null, mensaje).messageResponse();
	}
	
	public Respuesta enviarRespuestaAWatson(String respuestaDelCliente, Frase frase){
		Respuesta respuesta = null;
		if(hayQueEvaluarEnNivelSuperior){
			respuesta = analizarRespuestaInicial(respuestaDelCliente, frase);
		}else{
			respuesta = analizarRespuesta(respuestaDelCliente, frase);
		}
		return respuesta;
	}
	
	public Respuesta analizarRespuestaInicial(String respuestaDelCliente, Frase frase){
		Respuesta respuesta = miTopico.hablarConWatsonEnElNivelSuperior(frase, respuestaDelCliente);
		cambiarDeTemaForzosamente = false;
		
		try{ // TODO Buscar si hay mas de una intension de peso ALTO
			String intencionDelCliente = respuesta.obtenerLaIntencionDeConfianzaDeLaRespuesta().getNombre();
			WorkSpace workspace = extraerUnWorkspaceConLaIntencion(intencionDelCliente);
			if(workspace != null && ! intencionDelCliente.equals("")){
				nombreDeLaIntencionGeneralActiva = intencionDelCliente;
				cambiarDeTema = true; // Buscar otro tema
				if(pareceQueQuiereCambiarDeTemaForzosamente){
					cambiarDeTemaForzosamente = true;
					pareceQueQuiereCambiarDeTemaForzosamente = false;
					noEntendiLaUltimaRespuesta = true;
				}else{
					noEntendiLaUltimaRespuesta = false;
				}

				abordarElTemaPorNOLoEntendi = false;
				this.hayIntencionNoAsociadaANingunWorkspace = false;
			}else{
				System.out.println("Intencion no asociada a ningun workspace");
				if (! intencionDelCliente.equals("")){
					nombreDeLaIntencionGeneralActiva = intencionDelCliente;	
				}else{
					nombreDeLaIntencionGeneralActiva = Constantes.INTENCION_NO_ENTIENDO;
				}
				hayIntencionNoAsociadaANingunWorkspace = true;
				noEntendiLaUltimaRespuesta = true;
			}
			hayQueEvaluarEnNivelSuperior = false;
		}catch(Exception e){
			System.out.println("No hay ninguna intencion real o de confianza");
			nombreDeLaIntencionGeneralActiva = Constantes.INTENCION_NO_ENTIENDO;
			hayIntencionNoAsociadaANingunWorkspace = true;
			noEntendiLaUltimaRespuesta = true;
		}

		return respuesta;
	}
	
	public Respuesta analizarRespuesta(String respuestaDelCliente, Frase frase){
		Respuesta respuesta = miTopico.hablarConWatson(frase, respuestaDelCliente);
		cambiarDeTemaForzosamente = false;
		hayIntencionNoAsociadaANingunWorkspace = false;
		int maximoIntentos = frase.obtenerNumeroIntentosFallidos();
		
		noEntendiLaUltimaRespuesta = (! (respuesta.entendiLaRespuesta() && ! respuesta.hayAlgunAnythingElse())) && 
				(numeroDeIntentosActualesEnRepetirUnaPregunta != maximoIntentos);
		if(noEntendiLaUltimaRespuesta){
			System.out.println("No entendi la respuesta ...");
			// Validar si es que el usuario cambio de intencion general
			Intent miIntencion = this.determinarLaIntencionGeneral(respuestaDelCliente);
			if(elClienteQuiereCambiarDeIntencionGeneral(miIntencion)){
				System.out.println("Se requiere cambiar a NIVER SUPERIOR ...");
				this.seTieneQueGenerarUnNuevoContextoParaWatsonEnElWorkspaceActualConRespaldo();;
				cambiarANivelSuperior();
				if( ! respuesta.seTerminoElTema()){
					pareceQueQuiereCambiarDeTemaForzosamente = true;
				}
				nombreDeLaIntencionGeneralActiva = miIntencion.getIntent();
				respuesta = enviarRespuestaAWatson(respuestaDelCliente, frase); // General
				cambiarDeTema = true;
			}else{
				if(frase.esMandatorio()){	
					numeroDeIntentosActualesEnRepetirUnaPregunta ++;
				}
			}
		}else{
			miTopico.actualizarContexto(respuesta.getMiContexto()); // Actualizar contexto
			if(numeroDeIntentosActualesEnRepetirUnaPregunta == maximoIntentos){
				cambiarDeTema = true; // Buscar otro tema
				cambiarANivelSuperior();
				noEntendiLaUltimaRespuesta = true;
				abordarElTemaPorNOLoEntendi = true; // Decir frase me rindo
			}else{
				abordarElTemaPorNOLoEntendi = false;
				cambiarDeTema = respuesta.seTerminoElTema();
				if(cambiarDeTema){
					String laIntencion = respuesta.obtenerLaIntencionDeConfianzaDeLaRespuesta().getNombre();
					if( ! laIntencion.equals("")){
						borrarUnaVariableDelContexto(Constantes.ID_TEMA); // Solo se borra el id cuando el tema termina
						if(! laIntencion.equals("afirmacion")){ // TODO Esto no deberia ir aca
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
	
	public void seTieneQueGenerarUnNuevoContextoParaWatsonEnElWorkspaceActualConRespaldo(){
		// Respaldar la variables de contexto que tengo
		Hashtable<String, String> misVariables = respaldarVariablesDeContexto(miTopico.obtenerElContexto());

		// Generar la nueva converzacion
		seTieneQueGenerarUnNuevoContextoParaWatsonEnElWorkspaceActual();
		
		// Agregar a la nueva converzacion el respaldo de las variables de contexto
		agregarVariablesDeContexto(misVariables);
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
	
	public void seTieneQueGenerarUnNuevoContextoParaWatsonEnElWorkspaceActual(){
		miTopico.reiniciarContexto();
	}
	
	private void agregarVariablesDeContexto(Hashtable<String, String> variables){
		Enumeration<String> misLlaves = variables.keys();
		while(misLlaves.hasMoreElements()){
			String key = misLlaves.nextElement();
			activarValiableEnElContextoDeWatson(key, variables.get(key));
		}
	}
	
	private WorkSpace extraerUnWorkspaceConLaIntencion(String nombreDeLaIntencion){
		for(WorkSpace workspace: misWorkSpaces){
			if(workspace.tieneLaIntencion(nombreDeLaIntencion)){
				return workspace;
			}
		}
		return null;
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
		}
	}
	
	public Intent determinarLaIntencionGeneral(String mensaje){
		Intent intencion = determinarLaIntencionDeConfianzaEnUnWorkspace(mensaje);
		return intencion;
	}
	
	private Intent determinarLaIntencionDeConfianzaEnUnWorkspace(String mensaje){
		List<Intent> intenciones = llamarAWatson(mensaje).getIntents();
		Intent intencion = null;
		double confidence = 0;
		
		for(int index = 0; index < intenciones.size(); index ++){
			if(intenciones.get(index).getConfidence() > confidence){
				confidence = intenciones.get(index).getConfidence();
				intencion = intenciones.get(index);
			}
		}
		return intencion;
	}
	
	private ArrayList<Intent> determinarLasDosIntencionDeConfianzaEnUnWorkspace(String mensaje){
		List<Intent> intenciones = llamarAWatson(mensaje).getIntents();
		ArrayList<Intent> respuesta = new ArrayList<Intent>();
		
		Collections.sort(intenciones, new Comparator<Intent>() {

	        public int compare(Intent laPrimeraIntencion, Intent laSegundaIntencion) {
	            return laSegundaIntencion.getConfidence().compareTo(laPrimeraIntencion.getConfidence());
	        }
	    });
		Double valorDeAmbas = intenciones.get(0).getConfidence() + intenciones.get(1).getConfidence();
		if(valorDeAmbas >= 0.8 && intenciones.get(1).getConfidence() >= 0.3){
			respuesta.add(intenciones.get(0));
			respuesta.add(intenciones.get(1));
		}
		
		return respuesta;
	}
	
	public void activarTemaEnElContextoDeWatson(String nombreTema){
		activarValiableEnElContextoDeWatson(Constantes.ID_TEMA, nombreTema);
		System.out.println("Contexto modificado: "+miTopico.obtenerElContexto());
	}
	
	public void activarValiableEnElContextoDeWatson(String nombre, String valor){
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
	}
	
	public Respuesta inicializarTemaEnWatson(String respuestaDelCliente){
		return inicializarTemaEnWatsonWorkspaceEspecifico(respuestaDelCliente);
	}
	
	public Respuesta inicializarTemaEnWatsonWorkspaceEspecifico(String respuestaDelCliente){

		Respuesta respuesta = miTopico.hablarConWatson(null, respuestaDelCliente);
		
		String context = respuesta.messageResponse().getContext().toString();
		miTopico.actualizarContexto(context);
		
		borrarUnaVariableDelContexto(Constantes.ANYTHING_ELSE);
		borrarUnaVariableDelContexto(Constantes.NODO_ACTIVADO);
		borrarUnaVariableDelContexto(Constantes.ORACIONES_AFIRMATIVAS);
		borrarUnaVariableDelContexto(Constantes.CAMBIAR_A_GENERAL);
		borrarUnaVariableDelContexto(Constantes.TERMINO_EL_TEMA);
		borrarUnaVariableDelContexto(Constantes.CAMBIAR_INTENCION);
		
		return respuesta;
	}
	
	public void borrarUnaVariableDelContexto(String nombreDeLaVariable){
		String context = miTopico.obtenerElContexto();
		JSONObject obj = null;
		try {
			obj = new JSONObject(context);
			obj.remove(nombreDeLaVariable);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		miTopico.actualizarContexto(obj.toString());
	}
	
	public String obtenerNodoActivado(MessageResponse response){
		try{
			return response.getContext().get(Constantes.NODO_ACTIVADO).toString();
		}catch(Exception e){
			System.out.println("No existe el id nodo");
			return "";
		}
	}
	
	private boolean elClienteQuiereCambiarDeIntencionGeneral(Intent intencion){
		if(intencion != null){
			return (intencion.getConfidence() >= Constantes.WATSON_CONVERSATION_CONFIDENCE); 
		}else{
			return false;
		}
	}
	
	public void cambiarANivelSuperior(){
		this.hayQueEvaluarEnNivelSuperior = true;
	}
	
	public String obtenerNombreDeLaIntencionGeneralActiva() {
		return this.nombreDeLaIntencionGeneralActiva;
	}
	
	public boolean hayIntencionNoAsociadaANingunWorkspace(){
		return this.hayIntencionNoAsociadaANingunWorkspace;
	}
	
	public boolean entendiLaUltimaPregunta(){
		return ! noEntendiLaUltimaRespuesta;
	}
	
	public boolean hayQueCambiarDeTemaForzosamente(){
		return cambiarDeTemaForzosamente;
	}
	
	public boolean hayQueCambiarDeTema(){
		return cambiarDeTema;
	}
	
	public boolean seTieneQueAbordarElTema(){
		return abordarElTemaPorNOLoEntendi;
	}
	
	public void yaNoSeTieneQueAbordarElTema(){
		abordarElTemaPorNOLoEntendi = false;
	}
	
	public String obtenerNombreDelWorkspaceActual(){
		return nombreDeWorkspaceActual;
	}
	
	public void yaNoCambiarDeTema(){
		cambiarDeTema = false;
	}
	
	public String obtenerElContexto(){
		return miTopico.obtenerElContexto();
	}
	
	public String obtenerMiUltimoContexto(){
		return miTopico.obtenerMiUltimoContexto();
	}
	
	public void cambiarElContexto(String contexto){
		miTopico.actualizarContexto(contexto);
	}
	
	public abstract Salida decirUnaFrase(Frase frase, Respuesta respuesta, Tema tema, Cliente cliente, ModoDeLaVariable modoDeResolucionDeResultadosFinales);
	
	public abstract Salida volverAPreguntarUnaFrase(Frase pregunta, Respuesta respuesta, Tema tema, Cliente cliente, ModoDeLaVariable modoDeResolucionDeResultadosFinales);
	
	public abstract Salida volverAPreguntarUnaFraseConMeRindo(Frase pregunta, Respuesta respuesta, Tema tema, boolean meRindo, Cliente cliente, ModoDeLaVariable modoDeResolucionDeResultadosFinales);
	
}
