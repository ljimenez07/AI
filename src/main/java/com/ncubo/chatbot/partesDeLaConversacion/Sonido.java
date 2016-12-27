package com.ncubo.chatbot.partesDeLaConversacion;

public class Sonido {

	private String miURLPublica;
	private String miPathInterno;
	
	public Sonido(String url, String path){
		this.miURLPublica = url;
		this.miPathInterno = path;
	}
	
	public String url(){
		return this.miURLPublica;
	}
	
	public String obtenerPath(){
		return this.miPathInterno;
	}
}
