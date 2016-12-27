package com.ncubo.evaluador.interprete.libraries;

import com.ncubo.evaluador.libraries.Decimal;
import com.ncubo.evaluador.libraries.Hilera;
import com.ncubo.evaluador.libraries.Moneda;
import com.ncubo.evaluador.libraries.Numero;
import com.ncubo.evaluador.libraries.Objeto;

public class OpSumar extends Expresion 
{
	private Expresion e1;
	private Expresion e2;
	
	public OpSumar(Expresion e1, Expresion e2)
	{
		this.e1 = e1;
		this.e2 = e2;
	}
	
	@Override
	Class<? extends Objeto> calcularTipo() throws Exception
	{
		if (e1.calcularTipo().equals(Numero.class) && e2.calcularTipo().equals(Numero.class))
		{
			return Numero.class;
		}
		else if (e1.calcularTipo().equals(Decimal.class) && e2.calcularTipo().equals(Numero.class))
		{
			return Decimal.class;
		}
		else if (e1.calcularTipo().equals(Numero.class) && e2.calcularTipo().equals(Decimal.class))
		{
			return Decimal.class;
		}
		else if (e1.calcularTipo().equals(Decimal.class) && e2.calcularTipo().equals(Decimal.class))
		{
			return Decimal.class;
		}
		else if (e1.calcularTipo().isAssignableFrom(Moneda.class) && e1.calcularTipo().equals(e2.calcularTipo()))
		{
			return e1.calcularTipo();
		}
		else if (e1.calcularTipo().equals(Hilera.class) || e2.calcularTipo().equals(Hilera.class))
		{
			return Hilera.class;
		}
		else if (e1.calcularTipo().equals(Hilera.class) || e2.calcularTipo().equals(Numero.class))
		{
			return Hilera.class;
		}
		else if (e1.calcularTipo().equals(Hilera.class) || e2.calcularTipo().equals(Decimal.class))
		{
			return Hilera.class;
		}
		return Objeto.class;
	}
	
	@Override
	void validarEstaticamente() throws Exception
	{
		if (calcularTipo().equals(Objeto.class))
		{
			throw new LanguageException(
				String.format("No se puede sumar un valor tipo %s entre un denominador tipo %s.", e1.getClass().getSimpleName(), e2.getClass().getSimpleName())
			);
		}
	}
	
	@Override
	public Objeto ejecutar() throws Exception
	{
		Objeto objeto1 = e1.ejecutar();
		Objeto objeto2 = e2.ejecutar();
		return objeto1.sumar(objeto2);
	}

	@Override
	void write(StringBuilder resultado) 
	{
		e1.write(resultado);
		resultado.append(" + ");
		e2.write(resultado);
	}
}
