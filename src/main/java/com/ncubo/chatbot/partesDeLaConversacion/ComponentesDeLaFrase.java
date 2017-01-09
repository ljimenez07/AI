package com.ncubo.chatbot.partesDeLaConversacion;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		this.condicion = condicion;
		this.placeholders = new ArrayList<>();
		buscarPlaceholders();
	}
	
	private void buscarPlaceholders(){
		// http://stackoverflow.com/questions/2286648/named-placeholders-in-string-formatting
		Matcher matcher = Pattern.compile("\\$\\{(\\w+)}").matcher(this.textoDeLaFrase);
	    while (matcher.find()){
	        String key = matcher.group(1);
	        if( ! existeElPlaceholder(key)){
	        	System.out.println("Agregando placeholder: "+key);
	        	placeholders.add(new Placeholder(key));
	        }
	    }
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
		ComponentesDeLaFrase componente = new ComponentesDeLaFrase("frase", "Tu saldo de la cuenta ${cuenta} es de $${saldo} de la cuenta ${cuenta}", "Tu saldo es de", "", "");
	}
}
