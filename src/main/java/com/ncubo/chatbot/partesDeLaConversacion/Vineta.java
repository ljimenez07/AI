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
	
	public String obtenerContenido(){
		return this.contenidoDeLaVineta;
	}
	
	public String obtenerTipoDeVineta(){
		return this.tipoDeVineta;
	}
}
