package com.ncubo.evaluador.interprete.libraries;

import com.ncubo.evaluador.libraries.Objeto;

public class LiteralBoolean extends Expresion 
{
	private final boolean valor;
	
	public LiteralBoolean (boolean valor)
	{
		this.valor = valor;
	}
	
	@Override
	Class<? extends Objeto> calcularTipo()
	{
		return com.ncubo.evaluador.libraries.Boolean.class;
	}
	
	@Override
	public Objeto ejecutar()
	{
		return new com.ncubo.evaluador.libraries.Boolean (valor);
	}

	@Override
	void write(StringBuilder resultado) 
	{
		resultado.append(valor);
	}

}
