package com.ncubo.evaluador.libraries;

public final class Nulo extends Objeto
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
