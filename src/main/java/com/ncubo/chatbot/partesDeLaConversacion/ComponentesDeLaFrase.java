package com.ncubo.chatbot.partesDeLaConversacion;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ncubo.chatbot.contexto.VariablesDeContexto;
import com.ncubo.chatbot.exceptiones.ChatException;
import com.ncubo.chatbot.watson.TextToSpeechWatson;

public class ComponentesDeLaFrase {

	private String tipoDeFrase;
	private String textoDeLaFrase;
	private String textoAUsarParaGenerarElAudio;
	private Hashtable<String, Sonido> audios = new Hashtable<String, Sonido>();
	private Vineta vineta = null;
	private String condicion;
	private ArrayList<Placeholder> placeholders;
	
	public ComponentesDeLaFrase(String tipoDeFrase, String textoDeLaFrase, String textoAUsarParaGenerarElAudio, String vineta, String condicion){
		this.tipoDeFrase = tipoDeFrase;
		this.textoDeLaFrase = textoDeLaFrase;
		this.textoAUsarParaGenerarElAudio = textoAUsarParaGenerarElAudio;
		if(this.textoAUsarParaGenerarElAudio.isEmpty()){
			this.textoAUsarParaGenerarElAudio = this.textoDeLaFrase;
		}
		if(! vineta.isEmpty())
			this.vineta = new Vineta(vineta);
		else
			this.vineta = new Vineta("");
		this.condicion = condicion;
		this.placeholders = new ArrayList<>();
		buscarPlaceholders();
		verificarExistenciaDeLasVariables();
	}
	
	private void verificarExistenciaDeLasVariables(){
		System.out.println("Verificar variables ...");
		
		for(Placeholder placeholder: placeholders){
			if(! VariablesDeContexto.getInstance().verificarSiUnaVariableDeContextoExiste(placeholder.getNombreDelPlaceholder()))
				throw new ChatException(String.format("La variable %s no existe en el sistema.", placeholder.getNombreDelPlaceholder()));
		}
		
	}
	
	private void buscarPlaceholders(){
		// http://stackoverflow.com/questions/2286648/named-placeholders-in-string-formatting
		Matcher matcher = buscarExpresionRegular(this.textoDeLaFrase);
	    while (matcher.find()){
	        String key = matcher.group(1);
	        if( ! existeElPlaceholder(key)){
	        	System.out.println("Agregando placeholder: "+key);
	        	placeholders.add(new Placeholder(key));
	        }
	    }
	}
	
	private Matcher buscarExpresionRegular(String texto){
		return Pattern.compile("\\$\\{(\\w+)}").matcher(texto);
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
	
	private boolean existeElPlaceholder(String nombre){
		boolean resultado = false;
		if(tienePlaceholders()){
			for(Placeholder placeholder: placeholders){
				if(placeholder.getNombreDelPlaceholder().equals(nombre))
					return true;
			}
		}
		
		return resultado;
	}
	
	public ComponentesDeLaFrase sustituirPlaceholder(Placeholder plaseholder, String valorASustituir){
		
		String formatoDelPlaceholder = String.format("${%s}", plaseholder.getNombreDelPlaceholder());
		if(hayExpresionRegularEnElTexto(textoDeLaFrase, plaseholder)){
			textoDeLaFrase = textoDeLaFrase.replace(formatoDelPlaceholder, valorASustituir);
		}
		
		if(hayExpresionRegularEnElTexto(textoAUsarParaGenerarElAudio, plaseholder)){
			textoAUsarParaGenerarElAudio = textoAUsarParaGenerarElAudio.replace(formatoDelPlaceholder, valorASustituir);
		}
		
		if(vineta != null){
			if(hayExpresionRegularEnElTexto(vineta.obtenerContenido(), plaseholder)){
				String miVineta = vineta.obtenerContenido();
				miVineta = miVineta.replace(formatoDelPlaceholder, valorASustituir);
				vineta.cambiarElContenido(miVineta);
			}
		}
		return this;
	}
	
	public ComponentesDeLaFrase generarAudio(){
		String nombreDelArchivo = TextToSpeechWatson.getInstance().getAudioToURL(textoAUsarParaGenerarElAudio, false);
		String miIp = TextToSpeechWatson.getInstance().obtenerUrlPublicaDeAudios()+nombreDelArchivo;
		this.setAudio("audio",new Sonido(miIp, textoAUsarParaGenerarElAudio));
		return this;
	}
	
	public ArrayList<Placeholder> obtenerLosPlaceholders(){
		return placeholders;
	}
	
	public boolean tienePlaceholders(){
		return ! placeholders.isEmpty();
	}
	
	public String getTextoDeLaFrase() {
		return textoDeLaFrase;
	}

	public Sonido getAudio(String key) {
		return audios.get(key);
	}

	public void setAudio(String key, Sonido audio) {
		if(audios != null && audios.containsKey(key))
			this.audios.replace(key, audio);
		else this.audios.put(key, audio);
	}

	public String getTipoDeFrase() {
		return tipoDeFrase;
	}

	public String getTextoAUsarParaGenerarElAudio() {
		return textoAUsarParaGenerarElAudio;
	}

	public Vineta getVineta() {
		return vineta;
	}

	public String getCondicion() {
		return condicion;
	}
	
	public boolean tieneUnaCondicion(){
		return ! condicion.isEmpty();
	}
	
	public static void main(String argv[]) {
		ComponentesDeLaFrase componente = new ComponentesDeLaFrase("frase", "Tu saldo de la cuenta ${cuenta} es de $${saldo} en la cuenta ${cuenta}", "Tu saldo es de", "", "");
		System.out.println(componente.getTextoDeLaFrase());
		componente.sustituirPlaceholder(new Placeholder("saldo"), "100");
		componente.sustituirPlaceholder(new Placeholder("cuenta"), "de ahorros");
		System.out.println(componente.getTextoDeLaFrase());
	}
}
