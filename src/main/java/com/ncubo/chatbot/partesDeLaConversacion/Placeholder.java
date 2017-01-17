package com.ncubo.chatbot.partesDeLaConversacion;

import com.ncubo.chatbot.configuracion.Constantes.TiposDeVariables;

public class Placeholder {

	private final String nombreDelPlaceholder;
	private final TiposDeVariables tipoDePlaceholder;

	public Placeholder(String nombre, TiposDeVariables tipo){
		this.nombreDelPlaceholder = nombre;
		this.tipoDePlaceholder = tipo;
	}
	
	public String getNombreDelPlaceholder() {
		return nombreDelPlaceholder;
	}
	
	public TiposDeVariables getTipoDePlaceholder() {
		return tipoDePlaceholder;
	}
}
