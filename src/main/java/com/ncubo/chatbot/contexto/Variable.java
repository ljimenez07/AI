package com.ncubo.chatbot.contexto;

public class Variable {

	private final String nombre;
	private String valorDeLaVariable;
	private final String tipoVariable;

	public Variable(String nombre, String valorPorDefecto, String tipoVariable){
		this.nombre = nombre;
		this.valorDeLaVariable = valorPorDefecto;
		this.tipoVariable = tipoVariable;
	}
	
	public String getTipoVariable() {
		return tipoVariable;
	}
	
	public String getNombre() {
		return nombre;
	}

	public String getValorDeLaVariable() {
		return valorDeLaVariable;
	}

	public void setValorDeLaVariable(String valorPorDefecto) {
		this.valorDeLaVariable = valorPorDefecto;
	}
	
}
