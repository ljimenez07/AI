package com.ncubo.chatbot.partesDeLaConversacion;

public class Sonido {

	private String miURLPublica;
	
	public Sonido(String url){
		this.miURLPublica = url;
	}
	
	public String url(){
		return this.miURLPublica;
	}

}
