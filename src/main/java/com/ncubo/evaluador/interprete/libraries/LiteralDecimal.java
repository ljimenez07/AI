package com.ncubo.evaluador.interprete.libraries;

import com.ncubo.evaluador.libraries.Objeto;

public class LiteralDecimal extends Expresion 
{
	private final double valor;
	
	public LiteralDecimal (double valor)
	{
		this.valor = valor;
	}
	
	@Override
	Class<? extends Objeto> calcularTipo()
	{
		return com.ncubo.evaluador.libraries.Decimal.class;
	}
	
	@Override
	public Objeto ejecutar()
	{
		return new com.ncubo.evaluador.libraries.Decimal(valor);
	}

	@Override
	void write(StringBuilder resultado)
	{
		resultado.append(valor);	
	}
	
	

}
