package com.ncubo.chatbot.partesDeLaConversacion;

import java.util.ArrayList;

public class ComponentesDeLaFrase {

	private String tipoDeFrase;
	private String textoDeLaFrase;
	private String textoAUsarParaGenerarElAudio;
	private Sonido audio = null;
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
	}
	
	private void buscarPlaceholders(){
		
	}
	
	public boolean tienePlaceholders(){
		return placeholders.isEmpty();
	}
	
	public String getTextoDeLaFrase() {
		return textoDeLaFrase;
	}

	public Sonido getAudio() {
		return audio;
	}

	public void setAudio(Sonido audio) {
		this.audio = audio;
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
}
