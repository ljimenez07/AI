package com.ncubo.evaluador.interprete.libraries;

import com.ncubo.evaluador.libraries.Fecha;
import com.ncubo.evaluador.libraries.Objeto;

public class LiteralFecha extends Expresion 
{
	private final Fecha valor;
	
	public LiteralFecha (Fecha valor)
	{
		this.valor = valor;
	}
	
	@Override
	Class<? extends Objeto> calcularTipo()
	{
		return Fecha.class;
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
