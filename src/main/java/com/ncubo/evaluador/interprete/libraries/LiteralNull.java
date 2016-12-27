package com.ncubo.evaluador.interprete.libraries;

import com.ncubo.evaluador.libraries.Nulo;
import com.ncubo.evaluador.libraries.Objeto;

public class LiteralNull extends Expresion 
{
	public LiteralNull(){}
	
	@Override
	public Objeto ejecutar() 
	{
		return Nulo.NULO;
	}

	@Override
	Class<? extends Objeto> calcularTipo() 
	{
		return com.ncubo.evaluador.libraries.Nulo.class;
	}
	
	@Override
	void write(StringBuilder resultado)
	{
		resultado.append("Null");	
	}
}
