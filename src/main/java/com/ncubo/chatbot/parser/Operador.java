package com.ncubo.chatbot.parser;

public class Operador {

	public enum TipoDeOperador{
		AND, OR
	}
	
	private TipoDeOperador miTipoDeOperador;
	
	public Operador(TipoDeOperador tipo){
		this.miTipoDeOperador = tipo;
	}
	
	public TipoDeOperador getTipoDeOperador() {
		return miTipoDeOperador;
	}
	
}
