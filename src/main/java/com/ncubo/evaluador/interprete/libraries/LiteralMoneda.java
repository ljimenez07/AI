package com.ncubo.evaluador.interprete.libraries;

import com.ncubo.evaluador.libraries.Moneda;
import com.ncubo.evaluador.libraries.Objeto;

public class LiteralMoneda extends Expresion 
{
	private Moneda valor;
	
	public LiteralMoneda (Moneda valor)
	{
		this.valor = valor;
	}
	
	@Override
	Class<? extends Objeto> calcularTipo()
	{
		return valor.getClass();
	}
	
	@Override
	public Objeto ejecutar()
	{
		return valor;
	}
	
	@Override
	void validarEstaticamente() 
	{
		String caracteresDelMonto = "" + valor.convertirADouble();
		int posicionDelPunto = caracteresDelMonto.indexOf(".");
		
		boolean existeElPuntoEnElMonto = ! (posicionDelPunto == -1);
		
		if(existeElPuntoEnElMonto)
		{
			int caracteresDespuesDelaPunto = caracteresDelMonto.substring(posicionDelPunto + 1,caracteresDelMonto.length()).length();
			boolean laMonedaPoseeMasDeDosDecimales = caracteresDespuesDelaPunto > 2;
			
			if(laMonedaPoseeMasDeDosDecimales)
			{
				throw new LanguageException(
						String.format("Las cantidades de Tipo %s deben tener a sumo solo 2 decimales. La cifra: %s tiene %s.", valor.nombreDeLaMoneda(), valor,  caracteresDespuesDelaPunto)
						);
			}	
		}	
	}
	
	@Override
	void write(StringBuilder resultado)
	{
		resultado.append(valor);	
	}
}
