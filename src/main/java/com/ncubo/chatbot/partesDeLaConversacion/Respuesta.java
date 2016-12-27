package com.ncubo.chatbot.partesDeLaConversacion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.watson.ConversationWatson;
import com.ncubo.chatbot.watson.Entidades;
import com.ncubo.chatbot.watson.Intencion;
import com.ncubo.chatbot.watson.Intenciones;

public class Respuesta {

	private Frase miFrase;
	private Entidades misEntidades;
	private Intenciones misIntenciones;
	private ConversationWatson miConversacion;
	private String miContexto;
	
	private MessageResponse watsonRespuesta;
	private boolean terminoElTema;
	private boolean hayUnAnythingElse;
	private boolean cambiarIntencion;
	private boolean cambiarAGeneral;
	private String fraseActivada;
	private List<String> idsDeOracionesAfirmativas;
	private boolean hayOracionesAfirmativas;
	private String loQueElClienteDijo;
	private boolean hayProblemasEnLaComunicacionConWatson;
	
	public Respuesta(Frase frase, ConversationWatson conversacion, String context){
		this.terminoElTema = false;
		this.fraseActivada = "";
		this.hayUnAnythingElse = false;
		this.cambiarIntencion = false;
		this.miFrase = frase;
		this.miConversacion = conversacion;
		this.miContexto = context;
		this.misEntidades = new Entidades();
		this.misIntenciones = new Intenciones();
		this.idsDeOracionesAfirmativas = null;
		this.hayOracionesAfirmativas = false;
		this.watsonRespuesta = null;
		this.hayProblemasEnLaComunicacionConWatson = false;
	}
	
	public Respuesta(ConversationWatson conversacion, String context){
		this.terminoElTema = false;
		this.fraseActivada = "";
		this.hayUnAnythingElse = false;
		this.cambiarIntencion = false;
		this.miFrase = null;
		this.miConversacion = conversacion;
		this.miContexto = context;
		this.misEntidades = new Entidades();
		this.misIntenciones = new Intenciones();
		this.idsDeOracionesAfirmativas = null;
		this.hayOracionesAfirmativas = false;
		this.hayProblemasEnLaComunicacionConWatson = false;
	}
	
	public void llamarAWatson(String texto){
		// con el id de seccion, pasar la respuesta en texto a conversation
		// del xml de respuesta de watson set al analizador cuales fueron los intenciones y las entidades (limitado a la pregunta)
		// Si la respuesta fue pobre o de baja confianza hay que confirmar
		this.loQueElClienteDijo = texto;
		watsonRespuesta = this.miConversacion.enviarAWatson(texto, this.miContexto);
		if (watsonRespuesta == null){
			System.out.println("ERROR EN LA COMUNICACION CON WATSON");
			watsonRespuesta = this.miConversacion.enviarAWatson(texto, this.miContexto);
			if (watsonRespuesta == null){
				System.out.println("ERROR EN LA COMUNICACION CON WATSON");
				hayProblemasEnLaComunicacionConWatson = true;
			}
		}
		
		procesarLaRespuestaDeWatson(this.miConversacion, watsonRespuesta);
	}
	
	private void procesarLaRespuestaDeWatson(ConversationWatson conversacion, MessageResponse watsonRespuesta){

		this.misEntidades = conversacion.entidadesQueWatsonIdentifico(watsonRespuesta);
		this.misIntenciones = conversacion.probablesIntenciones(watsonRespuesta);
		
		this.terminoElTema = (obtenerElementoDelContextoDeWatson(Constantes.TERMINO_EL_TEMA).equals("true"));
		this.hayUnAnythingElse = (obtenerElementoDelContextoDeWatson(Constantes.ANYTHING_ELSE).equals("true"));
		this.cambiarIntencion = (obtenerElementoDelContextoDeWatson(Constantes.CAMBIAR_INTENCION).equals("true"));
		this.fraseActivada = obtenerElementoDelContextoDeWatson(Constantes.NODO_ACTIVADO);
		this.cambiarAGeneral = (obtenerElementoDelContextoDeWatson(Constantes.CAMBIAR_A_GENERAL).equals("true"));
		obtenerIDsDeOracionesAfirmativas();
		
	}
	
	public String loQueElClienteDijoFue(){
		return loQueElClienteDijo;
	}
	
	public boolean entendiLaRespuesta(){
		
		boolean entendi = true;
		boolean lasIntencionesEstanBien = true;
		boolean lasEntidadesEstanBien = true;
		
		if (miFrase instanceof Pregunta){
			Pregunta pregunta = (Pregunta) miFrase;
			
			if(pregunta.intenciones().obtenerTodasLasIntenciones().size() > 0){
				lasIntencionesEstanBien = pregunta.verificarSiLasIntencionesExistenYSonDeConfianza(this.misIntenciones);
			}
			
			if(pregunta.entidades().obtenerTodasLasEntidades().size() > 0){
				// lasEntidadesEstanBien = this.pregunta.verificarSiTodasLasEntidadesExisten(this.misEntidades);
			}
		}
		
		entendi = lasIntencionesEstanBien && lasEntidadesEstanBien;
		
		if(entendi){
			miContexto = watsonRespuesta.getContext().toString();
		}
		
		return entendi;
	}
	
	public Intencion obtenerLaIntencionDeLaRespuesta(){
		return this.misIntenciones.obtenerLaDeMayorConfianza(0);
	}
	
	public Intencion obtenerLaIntencionDeConfianzaDeLaRespuesta(){
		return this.misIntenciones.obtenerLaDeMayorConfianza(Constantes.WATSON_CONVERSATION_CONFIDENCE);
	}
	
	public String getMiContexto() {
		return miContexto;
	}
	
	public boolean seTerminoElTema(){
		return this.terminoElTema;
	}
	
	public boolean hayAlgunAnythingElse(){
		return this.hayUnAnythingElse;
	}
	
	public boolean quiereCambiarIntencion(){
		return this.cambiarIntencion;
	}
	
	public boolean cambiarAGeneral(){
		return this.cambiarAGeneral;
	}
	
	public MessageResponse messageResponse(){
		return watsonRespuesta;
	}
	
	private String obtenerElementoDelContextoDeWatson(String variableDeContexto){
		try{
			return watsonRespuesta.getContext().get(variableDeContexto).toString();
		}catch(Exception e){
			return "";
		}
	}
	
	private void obtenerIDsDeOracionesAfirmativas(){
		String afirmativas = obtenerElementoDelContextoDeWatson(Constantes.ORACIONES_AFIRMATIVAS);
		if(afirmativas.equals("")){
			hayOracionesAfirmativas = false;
		}else{
			hayOracionesAfirmativas = true;
			System.out.println("Oraciones afirmativas: "+afirmativas);
			afirmativas = afirmativas.replace("[", "").replace("]", "");
			idsDeOracionesAfirmativas = new ArrayList<String>(Arrays.asList(afirmativas.split(",")));			
		}
	}
	
	public boolean hayOracionesAfirmativasActivas(){
		return this.hayOracionesAfirmativas || (idsDeOracionesAfirmativas != null);
	}
	
	public List<String> obtenerLosIDsDeLasOracionesAfirmativasActivas(){
		return this.idsDeOracionesAfirmativas;
	}
	
	public String obtenerFraseActivada(){
		return this.fraseActivada;
	}
	
	public boolean hayProblemasEnLaComunicacionConWatson(){
		return this.hayProblemasEnLaComunicacionConWatson;
	}
}
