package com.ncubo.evaluador.libraries;

import com.ncubo.evaluador.interprete.libraries.Primitiva;

public final class Nulo extends Objeto implements Primitiva
{
	public final static Nulo NULO = new Nulo();

	private Nulo()
	{

	}
	
	@Override
	public String show() 
	{
		return "{\""+ this.getClass().getSimpleName()+"\":"+ null +"}";
	}
	
	@Override
	public boolean esIgualQue(Objeto objeto)
	{
		boolean esIgual;
		if( objeto.getClass() != this.getClass() )
		{
			esIgual = false;
		}	
		else 
		{
			esIgual = true;
		}
		return esIgual;
	}

	@Override
	public boolean noEsIgualQue(Objeto objeto) 
	{
		return ! esIgualQue(objeto);
	}
}
