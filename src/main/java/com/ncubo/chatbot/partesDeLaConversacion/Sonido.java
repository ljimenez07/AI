package com.ncubo.chatbot.partesDeLaConversacion;

public class Sonido {

	private String miURLPublica;
	private String textoUsadoParaGenerarElSonido;
	
	public Sonido(String url, String textoDelAudio){
		this.miURLPublica = url;
		this.textoUsadoParaGenerarElSonido = textoDelAudio;
	}
	
	public String url(){
		return this.miURLPublica;
	}

	public void cambiarURL(String nuevaURL){
		this.miURLPublica = nuevaURL;
	}
	
	public String getTextoUsadoParaGenerarElSonido() {
		return textoUsadoParaGenerarElSonido;
	}

	public void setTextoUsadoParaGenerarElSonido(String textoUsadoParaGenerarElSonido) {
		this.textoUsadoParaGenerarElSonido = textoUsadoParaGenerarElSonido;
	}

}
