package com.ncubo.evaluador.interprete.libraries;

import com.ncubo.evaluador.libraries.Hilera;
import com.ncubo.evaluador.libraries.Objeto;

public class LiteralHilera extends Expresion 
{
	private final String valor;
	
	public LiteralHilera (String valor)
	{
		this.valor = valor;
	}
	
	@Override
	Class<? extends Objeto> calcularTipo()
	{
		return Hilera.class;
	}
	
	@Override
	public Objeto ejecutar() 
	{
		return new Hilera(valor);
	}
	
	@Override
	void write(StringBuilder resultado)
	{
		resultado.append('\'');
		resultado.append( valor.replace("\'", "\\\'") );
		resultado.append('\'');
	}
}
