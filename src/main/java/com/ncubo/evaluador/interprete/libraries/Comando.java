
package com.ncubo.evaluador.interprete.libraries;

public abstract class Comando extends AST
{
	public abstract void ejecutar() throws Exception;

	public abstract void validarEstaticamente() throws Exception;

	abstract void write(StringBuilder resultado, int tabs);
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		write(builder, 0);
		return builder.toString();
	}
}
