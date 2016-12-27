package com.ncubo.evaluador.libraries;

import com.ncubo.evaluador.interprete.libraries.LanguageException;

public class Numero extends Objeto implements Comparable<Numero>
{
	public int valor;

	public Numero(int valor)
	{
		this.valor = valor;
	}

	@Override
	public String show() 
	{
		String resultado = toString().substring(0, Integer.min(12, toString().length()));
		while (resultado.length() < 12) resultado = ' ' + resultado;
		return resultado;
	}

	@Override
	public String toString()
	{ 
		return "" + valor;
	}

	public int getValor() 
	{
		return valor;
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
			resultado = new Numero(valor + ((Numero) objeto).getValor());
		}
		else if (objeto instanceof Decimal) 
		{
			resultado = new Decimal(valor + ((Decimal) objeto).getValor());
		} 
		else 
		{
			throw new LanguageException(
					String.format("No se puede sumar un %s a un %s.", getClass().getSimpleName(), objeto.getClass().getSimpleName()));
		}
		return resultado;
	}

	@Override
	public Objeto restar(Objeto objeto)
	{
		Objeto resultado;
		if (objeto instanceof Numero) 
		{
			resultado = new Numero(valor - ((Numero) objeto).getValor());
		} 
		else if (objeto instanceof Decimal) 
		{
			resultado = new Decimal(valor - ((Decimal) objeto).getValor());
		} 
		else 
		{
			throw new LanguageException(
					String.format("No se puede restar un %s a un %s.", getClass().getSimpleName(), objeto.getClass().getSimpleName())
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
			resultado = new Numero(valor * ((Numero) objeto).getValor());
		}
		else if (objeto instanceof Decimal) 
		{
			resultado = new Decimal(valor * ((Decimal) objeto).getValor());
		}
		else 
		{
			throw new LanguageException(
					String.format("No se puede multiplicar un %s a un %s.", getClass().getSimpleName(), objeto.getClass().getSimpleName())
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
			resultado = new Numero(valor / ((Numero) objeto).getValor());
		} 
		else if (objeto instanceof Decimal) 
		{
			resultado = new Decimal(valor / ((Decimal) objeto).getValor());
		} 
		else 
		{
			throw new LanguageException(
					String.format("No se puede dividir un %s a un %s.", getClass().getSimpleName(), objeto.getClass().getSimpleName())
					);
		}
		return resultado;
	}

	@Override
	public boolean esIgualQue(Objeto objeto)
	{
		double otroValor;
		try
		{
			otroValor = objeto instanceof Numero ? ((Numero) objeto).valor : ((Decimal)objeto).getValor();		
		}
		catch(ClassCastException p)
		{
			throw new LanguageException(
					String.format("En la comparación se esperaba el valor de tipo [%s] pero se encontro un valor de tipo [%s]", Numero.class.getSimpleName(),  objeto.getClass().getSimpleName())
					);
		}
		return valor == otroValor;
	}
	
	@Override
	public boolean esMayorQue(Objeto objeto)
	{
		double otroValor;
		try
		{
			otroValor = objeto instanceof Numero ? ((Numero) objeto).valor : ((Decimal)objeto).getValor();	
		}
		catch(ClassCastException p)
		{
			throw new LanguageException(
					String.format("En la comparación se esperaba el valor de tipo [%s] pero se encontro un valor de tipo [%s]", Numero.class.getSimpleName(),  objeto.getClass().getSimpleName())
					);
		}
		return valor > otroValor;
	}
	
	@Override
	public boolean esMenorQue(Objeto objeto)
	{
		double otroValor;
		try
		{
			otroValor = objeto instanceof Numero ? ((Numero) objeto).valor : ((Decimal)objeto).getValor();				
		}
		catch(ClassCastException p)
		{
			throw new LanguageException(
					String.format("En la comparación se esperaba el valor de tipo [%s] pero se encontro un valor de tipo [%s]", Numero.class.getSimpleName(),  objeto.getClass().getSimpleName())
					);
		}
		return valor < otroValor;
	}
	
	@Override
	public boolean esMenorOIgualQue(Objeto objeto)
	{
		double otroValor;
		try
		{
			otroValor = objeto instanceof Numero ? ((Numero) objeto).valor : ((Decimal)objeto).getValor();
		}
		catch(ClassCastException p)
		{
			throw new LanguageException(
					String.format("En la comparación se esperaba el valor de tipo [%s] pero se encontro un valor de tipo [%s]", Numero.class.getSimpleName(),  objeto.getClass().getSimpleName())
					);
		}
		return valor <= otroValor;
	}
	
	@Override
	public boolean esMayorOIgualQue(Objeto objeto)
	{
		double otroValor;
		try
		{
			otroValor = objeto instanceof Numero ? ((Numero) objeto).valor : ((Decimal)objeto).getValor();		
		}
		catch(ClassCastException p)
		{
			throw new LanguageException(
					String.format("En la comparación se esperaba el valor de tipo [%s] pero se encontro un valor de tipo [%s]", Numero.class.getSimpleName(),  objeto.getClass().getSimpleName())
					);
		}
		return valor >= otroValor;
	}

	@Override
	public boolean noEsIgualQue(Objeto objeto) 
	{
		return ! esIgualQue(objeto);
	}

	public Numero siguiente() 
	{
		return new Numero(valor + 1);
	}
	
	public Numero anterior() 
	{
		if(valor == 0) return this;
		return new Numero(valor - 1);
	}
	
	@Override
	public int hashCode()
	{
		return valor;
	}

	@Override
	public boolean equals(Object objeto) 
	{
		Numero otroNumero = (Numero) objeto;
		return valor == otroNumero.getValor();
	}
	
	
	public int compareTo( Numero numero )
	{
		return valor - numero.getValor();
	}

}
