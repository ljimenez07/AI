package com.ncubo.evaluador.interprete.libraries;

import com.ncubo.evaluador.libraries.Decimal;
import com.ncubo.evaluador.libraries.Moneda;
import com.ncubo.evaluador.libraries.Numero;
import com.ncubo.evaluador.libraries.Objeto;

public class OpMultiplicar extends Expresion {
	
	private Expresion e1;
	private Expresion e2;
	
	public OpMultiplicar(Expresion e1, Expresion e2)
	{
		this.e1 = e1;
		this.e2 = e2;
	}
	
	@Override
	Class<? extends Objeto> calcularTipo()
	{
		Class<? extends Objeto> tipoE1 = e1.calcularTipo();
		Class<? extends Objeto> tipoE2 = e2.calcularTipo();
		if (tipoE1.equals(Numero.class) && tipoE2.equals(Numero.class))
		{
			return Numero.class;
		}
		else if (tipoE1.equals(Decimal.class) && tipoE2.equals(Decimal.class))
		{
			return Decimal.class;
		}
		else if (tipoE1.isAssignableFrom(Moneda.class) || tipoE2.equals(Decimal.class))
		{
			return tipoE1;
		}
		else if (tipoE2.isAssignableFrom(Moneda.class) || tipoE1.equals(Decimal.class))
		{
			return tipoE2;
		}
		else if (tipoE1.isAssignableFrom(Moneda.class) || tipoE2.equals(Numero.class))
		{
			return tipoE1;
		}
		else if (tipoE2.isAssignableFrom(Moneda.class) || tipoE1.equals(Numero.class))
		{
			return tipoE2;
		}
		return Objeto.class;
	}
	
	@Override
	void validarEstaticamente()
	{
		if (calcularTipo().equals(Objeto.class))
		{
			throw new LanguageException(
				String.format("No se puede multiplicar un valor tipo %s por un valor tipo %s.", e1.getClass().getSimpleName(), e2.getClass().getSimpleName())
			);
		}
	}
	
	@Override
	public Objeto ejecutar()
	{
		Objeto objeto1 = e1.ejecutar();
		Objeto objeto2 = e2.ejecutar();
		return objeto1.multiplicar(objeto2);
	}
	
	@Override
	void write(StringBuilder resultado) 
	{
		e1.write(resultado);
		resultado.append(" * ");
		e2.write(resultado);
	}

	@Override
	public void buscarVariablesEstaticas() {
		e1.buscarVariablesEstaticas();
		e2.buscarVariablesEstaticas();
	}

}
