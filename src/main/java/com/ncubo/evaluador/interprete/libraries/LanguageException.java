package com.ncubo.evaluador.interprete.libraries;

public class LanguageException extends RuntimeException {

	private static final long serialVersionUID = 2081427811811732501L;
	int fila;
	int columna;
	
	public LanguageException(String mensaje, String lineaConError, int fila, int columna)
	{
		super(mensaje + "\r" + lineaConError);
		this.fila = fila;
		this.columna = columna;
	}
	
	public LanguageException(String mensaje)
	{
		super(mensaje);
		this.fila = 0;
		this.columna = 0;
	}

	public String lineaConError()
	{
		return super.getMessage();
	}
	
	public int fila()
	{
		return fila;
	}
	
	public int columna()
	{
		return columna;
	}
}
