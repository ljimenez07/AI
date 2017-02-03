package com.ncubo.chatbot.partesDeLaConversacion;

import com.ncubo.chatbot.configuracion.Constantes;

public class Vineta {

	private String contenidoDeLaVineta;
	private String tipoDeVineta;
	
	public Vineta(String contenido){
		this.contenidoDeLaVineta = contenido;
		this.tipoDeVineta = Constantes.TIPO_VINETA_ILUSTRATIVA;
	}
	
	public Vineta(String contenido, String tipo){
		this.contenidoDeLaVineta = contenido;
		this.tipoDeVineta = tipo;
	}
	
	public String getContenido(){
		return this.contenidoDeLaVineta;
	}
	
	public void cambiarElContenido(String nuevoContenido){
		this.contenidoDeLaVineta = nuevoContenido;
	}
	
	public String getTipoDeVineta(){
		return this.tipoDeVineta;
	}
}
