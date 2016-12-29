package com.ncubo.evaluador.interprete.libraries;

import com.ncubo.evaluador.libraries.Objeto;

public abstract class Expresion extends AST
{
	public abstract Objeto ejecutar() throws RuntimeException;
	
	abstract Class<? extends Objeto> calcularTipo() throws RuntimeException;
	
	void validarEstaticamente() throws RuntimeException{}

	abstract void write(StringBuilder resultado);
	
}

