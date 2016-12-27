package com.ncubo.evaluador.interprete.libraries;

import com.ncubo.evaluador.libraries.Decimal;
import com.ncubo.evaluador.libraries.Moneda;
import com.ncubo.evaluador.libraries.Numero;
import com.ncubo.evaluador.libraries.Objeto;

public class OpMenos extends Expresion {

	private Expresion e;

	public OpMenos(Expresion e)
	{
		this.e = e;
	}

	@Override
	Class<? extends Objeto> calcularTipo() throws Exception
	{
		if (e.calcularTipo().equals(Decimal.class) || e.calcularTipo().equals(Numero.class) || e.calcularTipo().isAssignableFrom(Moneda.class))
		{
			return e.calcularTipo();
		}
		return Objeto.class;
	}

	@Override
	public Objeto ejecutar() throws Exception
	{
		Objeto objeto1 = e.ejecutar();
		if(objeto1 instanceof Numero)
		{
			return objeto1.multiplicar(new Numero(-1));
		}
		else if(objeto1 instanceof Decimal)
		{
			return objeto1.multiplicar(new Decimal(-1));	
		}
		else
		{
			throw new LanguageException(
					String.format("No se reconoce la estructura %s", objeto1.getClass().getSimpleName())
					);
		}
	}

	@Override
	void write(StringBuilder resultado) 
	{
		resultado.append('-');
		e.write(resultado);
	}
}
