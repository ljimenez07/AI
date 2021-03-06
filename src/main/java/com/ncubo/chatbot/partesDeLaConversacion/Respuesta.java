package com.ncubo.chatbot.partesDeLaConversacion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.watson.ConversacionConWatson;
import com.ncubo.chatbot.watson.Entidades;
import com.ncubo.chatbot.watson.Intencion;
import com.ncubo.chatbot.watson.Intenciones;

public class Respuesta {

	private Frase miFrase;
	private Tema miTema;
	private Entidades misEntidades;
	private Intenciones misIntenciones;
	private ConversacionConWatson miConversacion;
	private String miContexto;
	
	private MessageResponse watsonRespuesta;
	private boolean terminoElTema;
	private boolean hayUnAnythingElse;
	private boolean seTerminoElBloque;
	private String fraseActivada;
	private List<String> nombresDeOracionesAfirmativas;
	private boolean hayOracionesAfirmativas;
	private String loQueElClienteDijo = "";
	private boolean hayProblemasEnLaComunicacionConWatson;
	private boolean teminoDeProcesarLaRespuestaDelAgente = false;
	
	public Respuesta(Frase frase, Tema tema, ConversacionConWatson conversacion, String context){
		this.terminoElTema = false;
		this.fraseActivada = "";
		this.hayUnAnythingElse = false;
		this.seTerminoElBloque = false;
		this.miFrase = frase;
		this.miTema = tema;
		this.miConversacion = conversacion;
		this.miContexto = context;
		this.misEntidades = new Entidades();
		this.misIntenciones = new Intenciones();
		this.nombresDeOracionesAfirmativas = null;
		this.hayOracionesAfirmativas = false;
		this.watsonRespuesta = null;
		this.hayProblemasEnLaComunicacionConWatson = false;
	}
	
	public Respuesta(ConversacionConWatson conversacion, String context){
		this.terminoElTema = false;
		this.fraseActivada = "";
		this.hayUnAnythingElse = false;
		this.seTerminoElBloque = false;
		this.miFrase = null;
		this.miConversacion = conversacion;
		this.miContexto = context;
		this.misEntidades = new Entidades();
		this.misIntenciones = new Intenciones();
		this.nombresDeOracionesAfirmativas = null;
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
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
			System.out.println("ERROR EN LA COMUNICACION CON WATSON USANDO 'CONVERSATION'. SE VA A VOLVER INTENTAR");
			watsonRespuesta = this.miConversacion.enviarAWatson(texto, this.miContexto);
			if (watsonRespuesta == null){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {}
				System.out.println("ERROR EN LA COMUNICACION CON WATSON USANDO 'CONVERSATION'. SE VA A VOLVER INTENTAR");
				watsonRespuesta = this.miConversacion.enviarAWatson(texto, this.miContexto);
				if (watsonRespuesta == null){
					System.out.println("Error en la comunicacion con watson usando 'Conversation'. No se pudo responder al texto: "+texto);
					hayProblemasEnLaComunicacionConWatson = true;
				}
			}
		}
		
		procesarLaRespuestaDeWatson(this.miConversacion, watsonRespuesta);
		teminoDeProcesarLaRespuestaDelAgente = true;
	}
	
	public boolean teminoDeProcesarLaRespuestaDelAgente(){
		return teminoDeProcesarLaRespuestaDelAgente;
	}
	
	private void procesarLaRespuestaDeWatson(ConversacionConWatson conversacion, MessageResponse watsonRespuesta){

		this.misEntidades = conversacion.entidadesQueWatsonIdentifico(watsonRespuesta);
		this.misIntenciones = conversacion.probablesIntenciones(watsonRespuesta);
		
		this.terminoElTema = (obtenerElementoDelContextoDeWatson(Constantes.TERMINO_EL_TEMA).equals("true"));
		this.hayUnAnythingElse = (obtenerElementoDelContextoDeWatson(Constantes.ANYTHING_ELSE).equals("true"));
		this.seTerminoElBloque = (obtenerElementoDelContextoDeWatson(Constantes.TERMINO_BLOQUE).equals("true"));
		this.fraseActivada = obtenerElementoDelContextoDeWatson(Constantes.NODO_ACTIVADO);
		obtenerNombresDeOracionesAfirmativas();
		
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
			try{
				miContexto = new JSONObject(watsonRespuesta.getContext()).toString();
			}catch(Exception e){
				entendi = false;
			}
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
	
	/*public boolean quiereCambiarIntencion(){
		return this.cambiarIntencion;
	}
	
	public boolean cambiarAGeneral(){
		return this.cambiarAGeneral;
	}*/
	
	public MessageResponse messageResponse(){
		return watsonRespuesta;
	}
	
	public String obtenerElementoDelContextoDeWatson(String variableDeContexto){
		try{
			return watsonRespuesta.getContext().get(variableDeContexto).toString();
		}catch(Exception e){
			return "";
		}
	}
	
	private void obtenerNombresDeOracionesAfirmativas(){
		String afirmativas = obtenerElementoDelContextoDeWatson(Constantes.ORACIONES_AFIRMATIVAS);
		if(afirmativas.equals("")){
			hayOracionesAfirmativas = false;
		}else{
			hayOracionesAfirmativas = true;
			System.out.println("Oraciones afirmativas: "+afirmativas);
			afirmativas = afirmativas.replace("[", "").replace("]", "");
			nombresDeOracionesAfirmativas = new ArrayList<String>(Arrays.asList(afirmativas.split(",")));			
		}
	}
	
	public Frase obtenerLaFrase(){
		return this.miFrase;
	}
	
	public Tema obtenerElTema(){
		return this.miTema;
	}
	
	public boolean hayOracionesAfirmativasActivas(){
		return this.hayOracionesAfirmativas || (nombresDeOracionesAfirmativas != null);
	}
	
	public List<String> obtenerLosNombresDeLasOracionesAfirmativasActivas(){
		return this.nombresDeOracionesAfirmativas;
	}
	
	public String obtenerFraseActivada(){
		return this.fraseActivada;
	}
	
	public boolean hayProblemasEnLaComunicacionConWatson(){
		return this.hayProblemasEnLaComunicacionConWatson;
	}
	
	public boolean seTerminoElBloque() {
		return seTerminoElBloque;
	}

	public void setSeTerminoElBloque(boolean seTerminoElBloque) {
		this.seTerminoElBloque = seTerminoElBloque;
	}

}
