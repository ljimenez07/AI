package com.ncubo.chatbot.contexto;

public class Variable {

	private String nombre;
	private String valorDeLaVariable;
	private String tipoValor;
	
	public Variable(String nombre, String valorPorDefecto, String tipoValor){
		this.nombre = nombre;
		this.valorDeLaVariable = valorPorDefecto;
		this.tipoValor = tipoValor;
	}
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getValorDeLaVariable() {
		return valorDeLaVariable;
	}

	public void setValorDeLaVariable(String valorPorDefecto) {
		this.valorDeLaVariable = valorPorDefecto;
	}

	public String getTipoValor() {
		return tipoValor;
	}

	public void setTipoValor(String tipoValor) {
		this.tipoValor = tipoValor;
	}
}
