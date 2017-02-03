package com.ncubo.chatbot.partesDeLaConversacion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ncubo.chatbot.configuracion.Constantes.TiposDeVariables;
import com.ncubo.chatbot.contexto.Variable;
import com.ncubo.chatbot.contexto.VariablesDeContexto;
import com.ncubo.chatbot.watson.TextToSpeechWatson;

public class Salida implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8818069211558314639L;
	
	private String miTexto;
	private Sonido miSonido;
	private ArrayList<Vineta> misVinetas;
	private Respuesta miRespuesta;
	private Tema temaActual;
	private Frase fraseActual;
	private boolean seTerminoElChat;
	private Date miFecha;
	private String miTextoConPlaceholder;
	
	public Salida(){
		miTexto = "";
		miSonido = null;
		misVinetas = new ArrayList<Vineta>();
		miRespuesta = null;
		temaActual = null;
		seTerminoElChat = false;
		miFecha = new Date();
		miTextoConPlaceholder = "";
	}
	
	public void escribir(String texto, Respuesta respuesta, Tema tema, Frase frase){
		this.miTexto = texto;
		this.miRespuesta = respuesta;
		this.temaActual = tema;
		this.fraseActual = frase;
		//System.out.println(texto);
	}
	
	public void escribir(Sonido sonido, Respuesta respuesta, Tema tema, Frase frase){
		this.miSonido = sonido;
		this.miRespuesta = respuesta;
		this.temaActual = tema;
		this.fraseActual = frase;
		//System.out.println(sonido.url());
	} 
	
	public void escribir(Vineta vineta, Respuesta respuesta, Tema tema, Frase frase){
		if(vineta != null && ! existeLaVineta(vineta))
			this.misVinetas.add(vineta);
		this.miRespuesta = respuesta;
		this.temaActual = tema;
		this.fraseActual = frase;
		//System.out.println(vineta.url());
	} 
	
	public void escribir(String texto, Sonido sonido, Respuesta respuesta, Tema tema, Frase frase){
		this.miTexto = texto;
		this.miSonido = sonido;
		this.miRespuesta = respuesta;
		this.temaActual = tema;
		this.fraseActual = frase;
	}	
	
	public void escribir(ComponentesDeLaFrase miFrase, Respuesta respuesta, Tema tema, Frase frase, String keyAudio){
		this.miTexto = miFrase.getTextoDeLaFrase();
		this.miSonido = miFrase.getAudio(keyAudio);
		if(miFrase.getVineta() != null && ! existeLaVineta(miFrase.getVineta()))
			this.misVinetas.add(miFrase.getVineta());
		this.miRespuesta = respuesta;
		this.temaActual = tema;
		this.fraseActual = frase;
	}
	
	public void escribir(ComponentesDeLaFrase miFrase, Respuesta respuesta, Tema tema, Frase frase){
		this.miTexto = miFrase.getTextoDeLaFrase();
		this.miSonido = miFrase.getAudio("audio");
		if(miFrase.getVineta() != null && ! existeLaVineta(miFrase.getVineta()))
			this.misVinetas.add(miFrase.getVineta());
		this.miRespuesta = respuesta;
		this.temaActual = tema;
		this.fraseActual = frase;
	}
	
	private boolean existeLaVineta(Vineta vineta){
		for(Vineta miVineta: misVinetas){
			if (miVineta.getContenido().trim().equals(vineta.getContenido().trim()))
				return true;
		}
		return false;
	}
	public String getMiTexto() {
		return miTexto;
	}

	public void setMiTexto(String texto) {
		miTexto = texto;
	}
	
	
	public String getMiTextoConPlaceholder() {
		return miTextoConPlaceholder;
	}

	public void setMiTextoConPlaceholder(String miTextoConPlaceholder) {
		this.miTextoConPlaceholder = miTextoConPlaceholder;
	}

	public Sonido getMiSonido() {
		if(miSonido == null){
			return new Sonido("", "");
		}
		return miSonido;
	}
	
	public void setMiSonido(String textoParaReproducir){
		String nombreDelArchivo = TextToSpeechWatson.getInstance().getAudioToURL(textoParaReproducir, true);
		String miIp = TextToSpeechWatson.getInstance().obtenerUrlPublicaDeAudios()+nombreDelArchivo;
		miSonido = new Sonido(miIp, textoParaReproducir);
	}

	public ArrayList<Vineta> getMisVinetas() {
		return misVinetas;
	}
	
	public Respuesta obtenerLaRespuestaDeIBM(){
		return miRespuesta;
	}
	
	public Tema getTemaActual() {
		return temaActual;
	}

	public Frase getFraseActual() {
		return fraseActual;
	}
	
	public void cambiarSeTerminoElChat(boolean seTermino){
		seTerminoElChat = seTermino;
	}
	
	public boolean seTerminoElChat(){
		return seTerminoElChat;
	}
	
	private Matcher buscarExpresionRegular(String texto){
		return Pattern.compile("\\$\\{(\\w+)}").matcher(texto);
	}
	
	private boolean existeElPlaceholder(ArrayList<Placeholder> misPlaceholders, String nombre){
		boolean resultado = false;
		if(!misPlaceholders.isEmpty()){
			for(Placeholder placeholder: misPlaceholders){
				if(placeholder.getNombreDelPlaceholder().equals(nombre))
					return true;
			}
		}
		
		return resultado;
	}
	
	public ArrayList<Placeholder> obtienePlaceholders(){
		// http://stackoverflow.com/questions/2286648/named-placeholders-in-string-formatting
		ArrayList<Placeholder> placeholders = new ArrayList<>();
		Matcher matcher = null;
		if(!miTexto.isEmpty()){
			matcher = buscarExpresionRegular(this.miTexto);
		}else if(! miSonido.getTextoUsadoParaGenerarElSonido().isEmpty()){
			matcher = buscarExpresionRegular(miSonido.getTextoUsadoParaGenerarElSonido());
		}else{
			for(Vineta vineta:misVinetas)
				 if(! vineta.getContenido().isEmpty())
					 matcher = buscarExpresionRegular(vineta.getContenido());
		}
		
		if(matcher != null){
			while (matcher.find()){
		        String key = matcher.group(1);
		        if( ! existeElPlaceholder(placeholders, key)){
		        	Variable miVariable = VariablesDeContexto.getInstance().obtenerUnaVariableDeMiContexto(key);
		        	placeholders.add(new Placeholder(key, miVariable.getTipoVariable()));
		        }
		    }
		}
		
		return placeholders;
	}
	
	private boolean hayExpresionRegularEnElTexto(String texto, Placeholder placeholder){
		Matcher matcher = buscarExpresionRegular(texto);
		boolean resultado = false;
	    while (matcher.find()){
	    	String key = matcher.group(1);
	    	if(key.equals(placeholder.getNombreDelPlaceholder()))
	    		return true;
	    }
	    return resultado;
	}
	
	
	public void sustituirPlaceholder(Placeholder placeholder, String valorASustituir){
		
		String formatoDelPlaceholder = String.format("${%s}", placeholder.getNombreDelPlaceholder());
		if(hayExpresionRegularEnElTexto(miTexto, placeholder)){
			miTextoConPlaceholder = miTexto;
			miTexto = miTexto.replace(formatoDelPlaceholder, valorASustituir);	
		}
		
		if(hayExpresionRegularEnElTexto(miSonido.getTextoUsadoParaGenerarElSonido(), placeholder)){
			miSonido.setTextoUsadoParaGenerarElSonido(miSonido.getTextoUsadoParaGenerarElSonido().replace(formatoDelPlaceholder, valorASustituir));
		}
		
		for(Vineta vineta:misVinetas)
			if(vineta != null){
				if(hayExpresionRegularEnElTexto(vineta.getContenido(), placeholder)){
					String miVineta = vineta.getContenido();
					miVineta = miVineta.replace(formatoDelPlaceholder, valorASustituir);
					vineta.cambiarElContenido(miVineta);
				}
			}
	}
	
	public boolean soloTieneEnum(ArrayList<Placeholder> placeholders){
		boolean tieneSoloEnum = false;
			for (int i = 0; i < placeholders.size();i++){
				String tipo = VariablesDeContexto.getInstance().obtenerUnaVariableDeMiContexto(placeholders.get(i).getNombreDelPlaceholder()).getTipoVariable().name();
				if(!tipo.equals(TiposDeVariables.ENUM.name()))
					return tieneSoloEnum = false;								
			}
		return tieneSoloEnum;
	}
}
