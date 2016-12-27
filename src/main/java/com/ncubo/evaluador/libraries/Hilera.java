package com.ncubo.evaluador.libraries;
import com.ncubo.evaluador.interprete.libraries.LanguageException;

public class Hilera extends Objeto
{
	private String valor;

	public Hilera(String valor)
	{
		this.valor = valor;
	}
	
	@Override
	public String show() 
	{
		String resultado = "\"" + valor.substring(0, Integer.min(33, valor.length())) + "\"";
		while (resultado.length() < 35) resultado += ' ';
		return resultado;
	}

	public String json() 
	{
		return valor.replace("\\", "\\\\");
	}

	
	public boolean esIgual(Hilera literal) 
	{
		return valor.equals(literal.valor);
	}

	@Override
	public String toString() 
	{
		return valor;
	}

	public String getValor() 
	{
		return valor;
	}
	
	String enMinuscula()
	{
		return valor.toLowerCase().replaceAll(" ", "");
	}

	@Override
	public Objeto sumar(Objeto objeto) 
	{
		Objeto resultado;
		if (objeto instanceof Hilera) 
		{
			resultado = new Hilera(valor + ((Hilera) objeto).getValor());
		} 
		else if (objeto instanceof Decimal) 
		{
			resultado = new Hilera(valor + ((Decimal) objeto).getValor());
		} 
		else if (objeto instanceof Numero) 
		{
			resultado = new Hilera(valor + ((Numero) objeto).getValor());
		} 
		else 
		{
			throw new LanguageException(
					String.format("No se puede sumar un %s a un %s.", objeto.getClass().getSimpleName(), getClass().getSimpleName())
					);
		}
		return resultado;
	}

	@Override
	public boolean esIgualQue(Objeto objeto) 
	{
		Hilera cadena = null;
		try
		{
			cadena = (Hilera)objeto;
		}
		catch(ClassCastException e)
		{
			throw new LanguageException(
					String.format("En la comparación se esperaba el valor de tipo [%s] pero se encontro un valor de tipo [%s]", Hilera.class.getSimpleName(), objeto.getClass().getSimpleName())
					);
		}
		return this.valor.equalsIgnoreCase(cadena.valor);
	}

	@Override
	public boolean noEsIgualQue(Objeto objeto)
	{
		return ! esIgualQue(objeto);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((valor == null) ? 0 : valor.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Hilera other = (Hilera) obj;
		if (valor == null) {
			if (other.valor != null)
				return false;
		} else if (!valor.equals(other.valor))
			return false;
		return true;
	}
	
	
	
}
