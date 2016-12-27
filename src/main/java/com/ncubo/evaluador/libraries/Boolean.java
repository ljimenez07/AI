
package com.ncubo.evaluador.libraries;

import com.ncubo.evaluador.interprete.libraries.LanguageException;

public class Boolean extends Objeto
{
	boolean valor;

	public boolean getValor() 
	{
		return valor;
	}

	public void setValor(boolean valor) 
	{
		this.valor = valor;
	}

	public Boolean(boolean valor) 
	{
		this.valor = valor;
	}
		
	@Override
	public String show()
	{
		String resultado = "\"" + this.valor + "\"";
		resultado = resultado.substring(0, Integer.min(7, resultado.length()));
		while (resultado.length() < 7) resultado = ' ' + resultado;
		return resultado;
	}
	
	@Override
	public String toString()
	{
		return "" + valor;
	}

	@Override
	public boolean esIgualQue(Objeto objeto) 
	{
		Boolean argumento = null;
		try
		{
			argumento = (Boolean) objeto;				
		}
		catch(ClassCastException p)
		{
			throw new LanguageException(
				String.format("En la comparación se esperaba el valor de tipo [%s] pero se encontro un valor de tipo [%s]", Boolean.class.getSimpleName(), objeto.getClass().getSimpleName())
			);
		}
		return valor == argumento.valor;
	}

	@Override
	public boolean noEsIgualQue(Objeto objeto) 
	{
		return ! esIgualQue(objeto);
	}
	
}

