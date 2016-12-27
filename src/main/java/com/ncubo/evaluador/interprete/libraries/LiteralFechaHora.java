package com.ncubo.evaluador.interprete.libraries;

import com.ncubo.evaluador.libraries.FechaHora;
import com.ncubo.evaluador.libraries.Objeto;

public class LiteralFechaHora extends Expresion 
{
	private final FechaHora valor;
	
	public LiteralFechaHora (FechaHora valor)
	{
		this.valor = valor;
	}
	
	@Override
	Class<? extends Objeto> calcularTipo()
	{
		return FechaHora.class;
	}
	
	@Override
	public Objeto ejecutar()
	{
		return valor;
	}
	
	@Override
	void write(StringBuilder resultado)
	{
		resultado.append(valor);	
	}
	
}
