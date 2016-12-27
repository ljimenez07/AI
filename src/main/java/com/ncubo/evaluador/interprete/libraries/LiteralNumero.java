package com.ncubo.evaluador.interprete.libraries;

import com.ncubo.evaluador.libraries.Numero;
import com.ncubo.evaluador.libraries.Objeto;

public class LiteralNumero extends Expresion {

	private int valor;
	
	public LiteralNumero (int valor)
	{
		this.valor = valor;
	}
	
	@Override
	Class<? extends Objeto> calcularTipo()
	{
		return Numero.class;
	}
	
	@Override
	public Objeto ejecutar()
	{
		return new Numero (valor);
	}
	
	@Override
	void write(StringBuilder resultado)
	{
		resultado.append(valor);	
	}
}
