package com.ncubo.evaluador.libraries;

import com.ncubo.evaluador.interprete.libraries.LanguageException;

public class Decimal extends Objeto
{
	private double valor;

	public Decimal(double valor)
	{
		this.valor = valor;
	}

	public Decimal(int valor) 
	{
		this.valor = valor;
	}
	
	@Override
	public String show() 
	{
		String resultado = toString().substring(0, Integer.min(18, toString().length()));
		while (resultado.length() < 18) resultado = ' ' + resultado;
		return resultado;
	}

	@Override
	public String toString()
	{
		return "" + valor;
	}
	
	public double getValor() 
	{
		return valor;
	}

	public void setValor(double valor) 
	{
		this.valor = valor;
	}
	
	public boolean esCero()
	{
		return valor == 0;
	}

	@Override
	public Objeto sumar(Objeto objeto)  
	{
		Objeto resultado;
		if (objeto instanceof Numero) 
		{
			resultado = new Decimal(valor + ((Numero) objeto).getValor());
		} 
		else if (objeto instanceof Decimal) 
		{
			resultado = new Decimal(valor + ((Decimal) objeto).getValor());
		} 
		else 
		{
			throw new LanguageException(
					String.format("No se puede sumar un %sa un %s.", objeto.getClass().getSimpleName(), getClass().getSimpleName())
					);
		}
		return resultado;
	}

	@Override
	public Objeto restar(Objeto objeto)  
	{
		Objeto resultado;
		if (objeto instanceof Numero) 
		{
			resultado = new Decimal(valor - ((Numero) objeto).getValor());
		} 
		else if (objeto instanceof Decimal) 
		{
			resultado = new Decimal(valor - ((Decimal) objeto).getValor());
		} 
		else 
		{
			throw new LanguageException(
					String.format("No se puede restar un %sa un %s.", objeto.getClass().getSimpleName(), getClass().getSimpleName())
					);
		}
		return resultado;
	}

	@Override
	public Objeto multiplicar(Objeto objeto)  
	{
		Objeto resultado;
		if (objeto instanceof Numero)
		{
			resultado = new Decimal(valor * ((Numero) objeto).getValor());
		} 
		else if (objeto instanceof Decimal) 
		{
			resultado = new Decimal(valor * ((Decimal) objeto).getValor());
		}
		else 
		{
			throw new LanguageException(
					String.format("No se puede multiplicar un %sa un %s.", objeto.getClass().getSimpleName(), getClass().getSimpleName())
					);
		}
		return resultado;
	}

	@Override
	public Objeto dividir(Objeto objeto)  
	{
		Objeto resultado;
		if (objeto instanceof Numero) 
		{
			resultado = new Numero((int) valor	/ ((Numero) objeto).getValor());
		} 
		else if (objeto instanceof Decimal) 
		{
			resultado = new Decimal(valor / ((Decimal) objeto).getValor());
		} 
		else 
		{
			throw new LanguageException(
					String.format("No se puede dividir un %sa un %s.", objeto.getClass().getSimpleName(), getClass().getSimpleName())
					);
		}
		return resultado;
	}

	@Override
	public boolean esIgualQue(Objeto objeto) 
	{
		double otroValor = 0.0;
		try
		{
			otroValor = objeto instanceof Numero ? ((Numero) objeto).valor : ((Decimal)objeto).valor; 				
		}
		catch(ClassCastException p)
		{
			throw new LanguageException(
					String.format("En la comparación se esperaba el valor de tipo [%s] pero se encontro un valor de tipo [%s]", Decimal.class.getSimpleName(), objeto.getClass().getSimpleName())
					);
		}
		return valor == otroValor;
	}
	
	@Override
	public boolean esMayorQue(Objeto objeto) 
	{
		double otroValor = 0.0;
		try
		{
			otroValor = objeto instanceof Numero ? ((Numero) objeto).valor : ((Decimal)objeto).valor; 
		}
		catch(ClassCastException p)
		{
			throw new LanguageException(
					String.format("En la comparación se esperaba el valor de tipo [%s] pero se encontro un valor de tipo [%s]", Decimal.class.getSimpleName(), objeto.getClass().getSimpleName())
					);
		}
		return valor > otroValor;
	}
	
	@Override
	public boolean esMenorQue(Objeto objeto) 
	{
		double otroValor = 0.0;
		try
		{
			otroValor = objeto instanceof Numero ? ((Numero) objeto).valor : ((Decimal)objeto).valor; 				
		}
		catch(ClassCastException p)
		{
			throw new LanguageException(
					String.format("En la comparación se esperaba el valor de tipo [%s] pero se encontro un valor de tipo [%s]", Decimal.class.getSimpleName(), objeto.getClass().getSimpleName())
					);
		}
		return valor < otroValor;
	}
	
	@Override
	public boolean esMenorOIgualQue(Objeto objeto) 
	{
		double otroValor = 0.0;
		try
		{
			otroValor = objeto instanceof Numero ? ((Numero) objeto).valor : ((Decimal)objeto).valor;				
		}
		catch(ClassCastException p)
		{
			throw new LanguageException(
					String.format("En la comparación se esperaba el valor de tipo [%s] pero se encontro un valor de tipo [%s]", Decimal.class.getSimpleName(), objeto.getClass().getSimpleName())
					);
		}
		return valor <= otroValor;
	}
	
	@Override
	public boolean esMayorOIgualQue(Objeto objeto) 
	{
		double otroValor = 0.0;
		try
		{
			otroValor = objeto instanceof Numero ? ((Numero) objeto).valor : ((Decimal)objeto).valor;				
		}
		catch(ClassCastException p)
		{
			throw new LanguageException(
					String.format("En la comparación se esperaba el valor de tipo [%s] pero se encontro un valor de tipo [%s]", Decimal.class.getSimpleName(), objeto.getClass().getSimpleName())
					);
		}
		return valor >= otroValor;
	}
	
	@Override
	public boolean noEsIgualQue(Objeto objeto) 
	{
		return ! esIgualQue(objeto);
	}

}