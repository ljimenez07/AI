package com.ncubo.evaluador.interprete.libraries;

public abstract class Linea extends AST 
{
	abstract void ejecutar();
	
	abstract void write(StringBuilder resultado);
	
}
