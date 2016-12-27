package com.ncubo.evaluador.libraries;

import com.ncubo.evaluador.interprete.libraries.LanguageException;

public class Denominacion extends Moneda 
{
	
	public Denominacion(Decimal monto,Monedas tipoDeMoneda) 
	{
		super(monto, tipoDeMoneda);
	}

	public Denominacion(Numero numero, Monedas tipoDeMoneda) 
	{
		super(numero, tipoDeMoneda);
	}

	@Override
	String simboloMonetario() 
	{
		return tipoDeMoneda.name();
	}

	@Override
	boolean estaEnLaMismaMonedaQue(Moneda otroMonto) 
	{
		return this.tipoDeMoneda == otroMonto.tipoDeMoneda;
	}

	@Override
	public String nombreDeLaMoneda() 
	{
		return tipoDeMoneda.nombre();
	}

	@Override
	public Moneda ceroEnEstaMoneda() 
	{
		return new Denominacion(new Decimal(0), tipoDeMoneda);
	}

	@Override
	public Moneda obtenerNuevaMonedaEnLaMismaEconomia(double cantidadDePlata) 
	{
		double cantidadRedondeada = Math.rint( cantidadDePlata * 100.0 ) / 100.0;
		return new Denominacion(new Decimal( cantidadRedondeada ) ,tipoDeMoneda);
	}

	@Override
	public double convertirADouble() 
	{		
		return monto.getValor();
	}

	@Override
	public Objeto sinDecimales() 
	{
		return new Denominacion( new Numero( (int) this.monto.getValor()),tipoDeMoneda);
	}
}
