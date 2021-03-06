package com.ncubo.chatbot.contexto;

import com.ncubo.chatbot.configuracion.Constantes.TiposDeVariables;

public class Variable {

	private final String nombre;
	private String[] valorDeLaVariable;
	private final TiposDeVariables tipoVariable;

	public Variable(String nombre, String[] valorPorDefecto, TiposDeVariables tipoVariable){
		this.nombre = nombre;
		this.valorDeLaVariable = valorPorDefecto;
		this.tipoVariable = tipoVariable;
	}
	
	public TiposDeVariables getTipoVariable() {
		return tipoVariable;
	}
	
	public String getNombre() {
		return nombre;
	}

	public String[] getValorDeLaVariable() {
		return valorDeLaVariable;
	}

	public void setValorDeLaVariable(String[] valorPorDefecto) {
		this.valorDeLaVariable = valorPorDefecto;
	}
	
}
