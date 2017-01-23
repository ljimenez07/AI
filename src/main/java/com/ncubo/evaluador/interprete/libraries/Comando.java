
package com.ncubo.evaluador.interprete.libraries;

public abstract class Comando extends AST
{
	public abstract void ejecutar() throws Exception;

	public abstract void validarEstaticamente();

	abstract void write(StringBuilder resultado, int tabs);
	
	public abstract void buscarVariablesEstaticas();
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		write(builder, 0);
		return builder.toString();
	}
}
