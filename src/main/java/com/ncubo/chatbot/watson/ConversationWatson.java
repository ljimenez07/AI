package com.ncubo.chatbot.watson;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.service.exception.BadRequestException;
import com.ibm.watson.developer_cloud.service.exception.InternalServerErrorException;
import com.ibm.watson.developer_cloud.service.exception.UnauthorizedException;
import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.parser.Operador;
import com.ncubo.chatbot.parser.Operador.TipoDeOperador;
import com.ibm.watson.developer_cloud.conversation.v1.model.Intent;
import com.ibm.watson.developer_cloud.conversation.v1.model.Entity;

public class ConversationWatson {

	private ConversationService service;
	//private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private String usuario;
	private String contrasena;
	private String idConversacion;
	private final static String FECHA_VERSION_WATSON = "2016-09-20";
	
	public ConversationWatson(String usuario, String contrasena, String idConversacion) {
		
		/*DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		this.service = new ConversationService(dateFormat.format(date));*/
		this.service = new ConversationService(FECHA_VERSION_WATSON);
		this.usuario = usuario;
		this.contrasena = contrasena;
		this.service.setUsernameAndPassword(this.usuario, this.contrasena);
		this.idConversacion = idConversacion;
	}
	
	public String getMSJ(String jsonData){
		String result = "";
		try{
			JSONObject obj;
			obj = new JSONObject(jsonData);
			obj = new JSONObject(obj.get("output").toString());
			result = obj.getJSONArray("text").toString().replace("[", "").replace("]", "").replace("\"", "");
			//logger.info("Watson response: "+result);
		}catch (Exception e){
			//logger.info("Watson response: "+jsonData);
		}
		return result;
	}
	
	public Intenciones probablesIntenciones(MessageResponse response){
		
		Intenciones intenciones = new Intenciones();
		try{
			List<Intent> intents = response.getIntents();
			for(int i = 0; i < intents.size(); i++ )
			{
				intenciones.agregar(new Intencion(intents.get(i).getIntent(), intents.get(i).getConfidence().doubleValue()){});
			}
		}catch(Exception e){
			System.out.println("Error al cargar las intenciones: "+e.getMessage());
		}
		return intenciones;
	}
	
	
	/**
	   * This method is used to get the entities identified in the user input
	   * @param response The response returned by the service including output, input and context
	   * @return The entities identified in the user input
	   */
	public Entidades entidadesQueWatsonIdentifico(MessageResponse response)
	{
		Entidades misEntidades = new Entidades();
		try{
			List<Entity> entities = response.getEntities();
			for(int i = 0; i < entities.size(); i++ ){
				Hashtable<String, Operador> valores = new Hashtable<String, Operador>();
				valores.put(entities.get(i).getValue(), new Operador(TipoDeOperador.AND));
				misEntidades.agregar(new Entidad(entities.get(i).getEntity(), valores));
			}
		}catch(Exception e){
			System.out.println("Error al cargar las entidades: "+e.getMessage());
		}
		return misEntidades;
	}
	
	public MessageResponse enviarMSG(String msg, Map<String, Object> myContext){
		//logger.info("Asking to Watson: "+msg);
		MessageResponse response = null;
		try {
		  // Your code goes here
			MessageRequest newMessage = new MessageRequest.Builder()
					.inputText(msg)
					//.entity(new Entity("countries", "Costa Rica", null))
					//.intent(new Intent("want_house", null))
					.context(myContext)
					.build();
			
			response = service.message(idConversacion, newMessage).execute();
			//logger.info("Watson Reponse: "+response.toString());
		} catch (IllegalArgumentException e) {
		  // Missing or invalid parameter
			//logger.info("Error1: "+e.getMessage()+" al enviar text");
		} catch (BadRequestException e) {
		  // Missing or invalid parameter
			//logger.info("Error2: "+e.getMessage()+" al enviar text");
		} catch (UnauthorizedException e) {
		  // Access is denied due to invalid credentials
			//logger.info("Error3: "+e.getMessage()+" al enviar text");
		} catch (InternalServerErrorException e) {
		  // Internal Server Error
			//logger.info("Error4: "+e.getMessage()+" al enviar text");
		}
		
		return response;
	}
	
	public MessageResponse enviarAWatson(String msg, String context){
		//logger.info("Asking to Watson: "+msg);
		JSONObject obj = null;
		System.out.println("CONTEXTO a enviar a Watson: "+context);
		try {
			obj = new JSONObject(context);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> myContext = null;
		try {
			myContext = mapper.readValue(obj.toString(), new TypeReference<Map<String, Object>>(){});
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		MessageResponse response = null;
		try {
		  // Your code goes here
			MessageRequest newMessage = new MessageRequest.Builder()
					.inputText(msg)
					.alternateIntents(true)
					//.entity(new Entity("countries", "Costa Rica", null))
					//.intent(new Intent("want_house", null))
					.context(myContext)
					.build();
			
			response = service.message(idConversacion, newMessage).execute();
			System.out.println("CONTEXTO recibido de Watson: "+response.getContext());
		} catch (IllegalArgumentException e) {
			System.out.println("Error1: "+e.getMessage()+" al enviar text (Missing or invalid parameter)");
		} catch (BadRequestException e) {
			System.out.println("Error2: "+e.getMessage()+" al enviar text (Missing or invalid parameter)");
		} catch (UnauthorizedException e) {
			System.out.println("Error3: "+e.getMessage()+" al enviar text (Access is denied due to invalid credentials)");
		} catch (InternalServerErrorException e) {
			System.out.println("Error2: "+e.getMessage()+" al enviar text (Internal Server Error)");
		}
		
		return response;
	}
	
	public String activarTemaEnElContextoDeWatson(String context, String nombreTema){
		JSONObject obj = null;
		try {
			obj = new JSONObject(context);
			obj.put("idTema", nombreTema);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Contexto modificado: "+obj.toString());
		return obj.toString();
	}
	
	/*public static void main(String[] args) throws Exception {

		ConversationWatson myConversation = new ConversationWatson(Constantes.WATSON_CONVERSATION_USER, 
				Constantes.WATSON_CONVERSATION_PASS, Constantes.WATSON_CONVERSATION_ID);
		MessageResponse result = myConversation.enviarMSG("Hello!", null);
		myConversation.getMSJ(result.toString());
		//myConversation.probablesIntenciones(result);
		//myConversation.getEntities(result.toString());
		//myConversation.entidadesQueWatsonIdentifico(result);
		String context = myConversation.activarTemaEnElContextoDeWatson(result.getContext().toString(), "quiereEnCondominio");
		result = myConversation.enviarAWatson("", context);
		
		result = myConversation.enviarMSG("rent a car", result.getContext());
		
		result = myConversation.enviarMSG("yes", result.getContext());
		result = myConversation.enviarMSG("yes", result.getContext());
		
		//result = myConversation.enviarMSG("Estoy buscondo un lote", null);
		//result = myConversation.getMSJ(result);
		//System.out.println(result);
	}*/
}
