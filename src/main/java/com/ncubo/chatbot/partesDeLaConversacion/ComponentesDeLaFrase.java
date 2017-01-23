package com.ncubo.chatbot.partesDeLaConversacion;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ncubo.chatbot.contexto.Variable;
import com.ncubo.chatbot.contexto.VariablesDeContexto;
import com.ncubo.chatbot.exceptiones.ChatException;
import com.ncubo.chatbot.watson.TextToSpeechWatson;
import com.ncubo.evaluador.main.Evaluador;

public class ComponentesDeLaFrase implements Cloneable{

	private String tipoDeFrase;
	private String textoDeLaFrase;
	private String textoAUsarParaGenerarElAudio;
	private Hashtable<String, Sonido> audios = new Hashtable<>();
	private Vineta vineta = null;
	private String condicion;
	private ArrayList<Placeholder> placeholders;
	private boolean esEstatica = true;
	
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
			this.vineta = new Vineta(textoDeLaFrase);
		this.condicion = condicion;
		this.placeholders = new ArrayList<>();
		buscarPlaceholders();
		verificarExistenciaDeLasVariables();
	}
	
	private void verificarExistenciaDeLasVariables(){
		System.out.println("Verificar variables ...");
		
		for(Placeholder placeholder: placeholders){
			if(! VariablesDeContexto.getInstance().verificarSiUnaVariableDeContextoExiste(placeholder.getNombreDelPlaceholder()))
				throw new ChatException(String.format("La variable '%s' no existe en el sistema.", placeholder.getNombreDelPlaceholder()));
		}
		
		if( ! condicion.isEmpty()){
			Evaluador evaluador = new Evaluador();
			ArrayList<String> variables = new ArrayList<>();
			try {
				String comando = "";
				comando = "show "+condicion+";";
				variables = evaluador.buscarVariablesEstaticasEnElComando(comando);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(variables != null && ! variables.isEmpty()){
				ArrayList<Placeholder> misPlaceholdersEnLaCondicion = buscarPlaceholdersEnLaCondicion(variables);
				for(Placeholder placeholder: misPlaceholdersEnLaCondicion){
					if(! VariablesDeContexto.getInstance().verificarSiUnaVariableDeContextoExiste(placeholder.getNombreDelPlaceholder()))
						throw new ChatException(String.format("La variable %s no existe en el sistema.", placeholder.getNombreDelPlaceholder()));
				}
			}
		}
	}
	
	private void buscarPlaceholders(){
		// http://stackoverflow.com/questions/2286648/named-placeholders-in-string-formatting
		Matcher matcher = null;
		if(!textoDeLaFrase.isEmpty()){
			matcher = buscarExpresionRegular(this.textoDeLaFrase);
		}else if(! textoAUsarParaGenerarElAudio.isEmpty()){
			matcher = buscarExpresionRegular(this.textoAUsarParaGenerarElAudio);
		}else if(! vineta.obtenerContenido().isEmpty()){
			matcher = buscarExpresionRegular(vineta.obtenerContenido());
		}
		
		if(matcher != null){
			while (matcher.find()){
		        String key = matcher.group(1);
		        if( ! existeElPlaceholder(key)){
		        	System.out.println("Agregando placeholder: "+key);
		        	Variable miVariable = VariablesDeContexto.getInstance().obtenerUnaVariableDeMiContexto(key);
		        	placeholders.add(new Placeholder(key, miVariable.getTipoVariable()));
		        	esEstatica = false;
		        }
		    }
		}
	}
	
	public boolean esEstatica(){
		return esEstatica;
	}
	
	public boolean esDinamica(){
		return ! esEstatica;
	}
	
	public ArrayList<Placeholder> buscarPlaceholdersEnElTextoADecir(){
		ArrayList<Placeholder> misPlaceholders = new ArrayList<>();
		Matcher matcher = buscarExpresionRegular(this.textoAUsarParaGenerarElAudio);	
		while (matcher.find()){
	        String key = matcher.group(1);
	        if( ! existeElPlaceholder(misPlaceholders, key)){
	        	Variable miVariable = VariablesDeContexto.getInstance().obtenerUnaVariableDeMiContexto(key);
	        	misPlaceholders.add(new Placeholder(key, miVariable.getTipoVariable()));
	        }
	    }
		return misPlaceholders;
	}
	
	public ArrayList<Placeholder> buscarPlaceholdersEnLaCondicion(ArrayList<String> variablesEstaticas){
		ArrayList<Placeholder> misPlaceholders = new ArrayList<>();
		//Matcher matcher = buscarExpresionRegular(this.condicion);	
		//while (matcher.find()){
	    //    String key = matcher.group(1);
		for(String key: variablesEstaticas){
	        if( ! existeElPlaceholder(misPlaceholders, key)){
	        	Variable miVariable = VariablesDeContexto.getInstance().obtenerUnaVariableDeMiContexto(key);
	        	misPlaceholders.add(new Placeholder(key, miVariable.getTipoVariable()));
	        }
	     }
		return misPlaceholders;
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
		return existeElPlaceholder(placeholders, nombre);
	}
	
	private boolean existeElPlaceholder(ArrayList<Placeholder> misPlaceholders, String nombre){
		boolean resultado = false;
		if(tienePlaceholders()){
			for(Placeholder placeholder: misPlaceholders){
				if(placeholder.getNombreDelPlaceholder().equals(nombre))
					return true;
			}
		}
		
		return resultado;
	}
	
	public void sustituirPlaceholder(Placeholder placeholder, String valorASustituir){
		
		String formatoDelPlaceholder = String.format("${%s}", placeholder.getNombreDelPlaceholder());
		if(hayExpresionRegularEnElTexto(textoDeLaFrase, placeholder)){
			textoDeLaFrase = textoDeLaFrase.replace(formatoDelPlaceholder, valorASustituir);
		}
		
		if(hayExpresionRegularEnElTexto(textoAUsarParaGenerarElAudio, placeholder)){
			textoAUsarParaGenerarElAudio = textoAUsarParaGenerarElAudio.replace(formatoDelPlaceholder, valorASustituir);
		}
		
		if(vineta != null){
			if(hayExpresionRegularEnElTexto(vineta.obtenerContenido(), placeholder)){
				String miVineta = vineta.obtenerContenido();
				miVineta = miVineta.replace(formatoDelPlaceholder, valorASustituir);
				vineta.cambiarElContenido(miVineta);
			}
		}
	}
	
	public Sonido generarAudio(String texto){
		String nombreDelArchivo = TextToSpeechWatson.getInstance().getAudioToURL(texto, false);
		String miIp = TextToSpeechWatson.getInstance().obtenerUrlPublicaDeAudios()+nombreDelArchivo;
		return new Sonido(miIp, texto);
	}
	public Sonido generarAudio(){
		String nombreDelArchivo = TextToSpeechWatson.getInstance().getAudioToURL(this.textoAUsarParaGenerarElAudio, false);
		String miIp = TextToSpeechWatson.getInstance().obtenerUrlPublicaDeAudios()+nombreDelArchivo;
		return new Sonido(miIp, this.textoAUsarParaGenerarElAudio);
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
		this.audios.put(key, audio);
	}

	public String getTipoDeFrase() {
		return tipoDeFrase;
	}

	public String getTextoAUsarParaGenerarElAudio() {
		return textoAUsarParaGenerarElAudio;
	}

	public void setTextoAUsarParaGenerarElAudio(String texto) {
		textoAUsarParaGenerarElAudio = texto;
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
	
	public Object clonar(){
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this;
	}
	
	public Hashtable<String, Sonido> getAudios(){
		return audios;
	}
	
	public static void main(String argv[]) {
		ComponentesDeLaFrase componente = new ComponentesDeLaFrase("frase", "Tu saldo de la cuenta ${cuenta} es de $${saldo} en la cuenta ${cuenta}", "Tu saldo es de", "", "");
	}
}
