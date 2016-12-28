package com.ncubo.chatbot.partesDeLaConversacion;

import com.ncubo.chatbot.configuracion.Constantes;

public class ComponentesDeLaFrase {

	private String tipoDeFrase;
	private String textoDeLaFrase;
	private String textoAUsarParaGenerarElAudio;
	private Sonido audio;
	private Vineta vineta;
	private String condicion;
	
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
		if(this.condicion.isEmpty()){
			this.condicion = Constantes.CONDICION_POR_DEFECTO;
		}
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
}
