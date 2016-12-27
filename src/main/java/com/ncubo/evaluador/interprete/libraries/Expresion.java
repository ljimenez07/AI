package com.ncubo.evaluador.interprete.libraries;

import com.ncubo.evaluador.libraries.Objeto;

public abstract class Expresion extends AST
{
	public abstract Objeto ejecutar() throws Exception;
	
	abstract Class<? extends Objeto> calcularTipo() throws Exception;
	
	void validarEstaticamente() throws Exception{}

	abstract void write(StringBuilder resultado);
	
}

