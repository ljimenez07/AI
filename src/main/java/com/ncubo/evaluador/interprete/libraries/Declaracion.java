package com.ncubo.evaluador.interprete.libraries;

public abstract class Declaracion extends AST 
{
	public abstract void guardar();

	public abstract void write(StringBuilder resultado, int tabs) ;
	
}
