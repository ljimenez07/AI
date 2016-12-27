package com.ncubo.evaluador.libraries;

import java.text.DecimalFormat;
import java.util.Locale;
import com.ncubo.evaluador.interprete.libraries.LanguageException;

public abstract class Moneda extends Objeto
{
	protected Decimal monto;
	protected Numero montoEntero;
	protected Locale localizacion;
	private static final DecimalFormat FORMATO_DECIMAL_DE_SALIDA_CON_DECIMALES = new DecimalFormat("0.00");
	private static final DecimalFormat FORMATO_DECIMAL_DE_SALIDA_SIN_DECIMALES = new DecimalFormat("0");
	
	protected Monedas tipoDeMoneda;
	
	protected Moneda(Decimal monto, Monedas tipo) 
	{
		tipoDeMoneda = tipo;
		double cantidadOriginal = monto.getValor();
		double cantidadRedondeada = Math.rint( cantidadOriginal * 100.0 ) / 100.0;
		
		if (cantidadOriginal - cantidadRedondeada > 0)
		{
			throw new LanguageException(
					String.format("La cantidad monetaria %s contiene m찼s de dos decimales", cantidadOriginal)
					);
		}
		this.monto = monto;
	}
	
	protected Moneda(Numero monto, Monedas tipo) 
	{
		this.tipoDeMoneda = tipo;
		this.montoEntero = monto;
	}
	
	public Decimal getMonto() 
	{
		return monto;
	}

	public Numero getMontoEntero()
	{
		return montoEntero;
	}
	
	public boolean esCero()
	{
		return monto.esCero();
	}

	@Override
	public Moneda sumar(Objeto objeto)  
	{
		double resultado = 0.0;
		if (objeto instanceof Moneda) 
		{
			Moneda estaMoneda = (Moneda) objeto;

			if(estaEnLaMismaMonedaQue(estaMoneda))
			{	
				resultado = (convertirADouble() + estaMoneda.convertirADouble());
			}
			else
			{
				throw new LanguageException(
						String.format("No se puede sumar un %s y un %s.", ((Moneda) objeto).tipoDeMoneda, tipoDeMoneda));
			}
		}
		else 
		{
			throw new LanguageException(
					String.format("No se puede sumar un %s y un %s.", objeto.getClass().getSimpleName(), getClass().getSimpleName()));
		}
		Moneda nuevoMonto = obtenerNuevaMonedaEnLaMismaEconomia( resultado );
		
		return nuevoMonto;
	}

	@Override
	public Moneda restar(Objeto objeto)  
	{
		double resultado = 0.0;
		if (objeto instanceof Moneda) 
		{
			Moneda estaMoneda = (Moneda) objeto;
			if(estaEnLaMismaMonedaQue(estaMoneda))
			{	
				resultado = (convertirADouble() - estaMoneda.convertirADouble());
			}
			else
			{
				throw new LanguageException(
						String.format("No se puede restar un %s y un %s.", objeto.getClass().getSimpleName(), getClass().getSimpleName()));
			}
		}
		else 
		{
			throw new LanguageException(
					String.format("No se puede restar un %s y un %s.", objeto.getClass().getSimpleName(), getClass().getSimpleName()));
		}
		Moneda nuevoMonto = obtenerNuevaMonedaEnLaMismaEconomia( resultado );
		
		return nuevoMonto;
	}
	
	@Override
	public Moneda multiplicar(Objeto objeto)  
	{
		double resultado = 0.0;
		if (objeto instanceof Decimal) 
		{
			Decimal numero = (Decimal) objeto;
			resultado = convertirADouble() * numero.getValor();
		}
		else if (objeto instanceof Numero) 
		{
			Numero numero = (Numero) objeto;
			resultado = convertirADouble() * numero.getValor();
		}		
		else 
		{
			throw new LanguageException(
					String.format("No se puede multiplicar un %s y un %s.", objeto.getClass().getSimpleName(), getClass().getSimpleName()));
		}
		Moneda nuevoMonto = obtenerNuevaMonedaEnLaMismaEconomia( Moneda.convertirADouble(resultado) );
		return nuevoMonto;
	}

	@Override
	public Moneda dividir(Objeto objeto)  
	{
		double resultado = 0.0;
		if (objeto instanceof Decimal) 
		{
			Decimal numero = (Decimal) objeto;
			resultado = convertirADouble() / numero.getValor();
		}
		else if (objeto instanceof Numero) 
		{
			Numero numero = (Numero) objeto;
			resultado = convertirADouble() / (double) numero.getValor();
		}		
		else 
		{
			throw new LanguageException(
					String.format("No se puede dividir un %s y un %s.", objeto.getClass().getSimpleName(), getClass().getSimpleName()));
		}
		Moneda nuevoMonto = obtenerNuevaMonedaEnLaMismaEconomia( Moneda.convertirADouble(resultado) );
		return nuevoMonto;
	}
	
	public static double convertirADouble(double cantidadOriginal)
	{
		double cantidadRedondeada = Math.rint( cantidadOriginal * 100.0 ) / 100.0;
		return cantidadRedondeada;
	}	
	
	@Override
	public String show()
	{
		String simbolo = simboloMonetario();
		if (simbolo.equals("CRC")) simbolo = "�";
		if (simbolo.equals("USD")) simbolo = "$";
		String montoConFormato;
		if( montoEntero != null)
		{
			montoConFormato = simbolo + FORMATO_DECIMAL_DE_SALIDA_SIN_DECIMALES.format(montoEntero.getValor());
		}
		else
		{
			montoConFormato = simbolo + FORMATO_DECIMAL_DE_SALIDA_CON_DECIMALES.format(monto.getValor());
		}
		String resultado = montoConFormato.substring(0, Integer.min(18, montoConFormato.length()));
		while (resultado.length() < 18) resultado = ' ' + resultado;
		return resultado;
	}
	
	public String toString()
	{
		if( montoEntero != null)
		{
			return simboloMonetario() + FORMATO_DECIMAL_DE_SALIDA_SIN_DECIMALES.format(montoEntero.getValor());
		}
		else
		{
			return simboloMonetario() + FORMATO_DECIMAL_DE_SALIDA_CON_DECIMALES.format(monto.getValor());
		}
	}

	abstract String simboloMonetario();

	abstract boolean estaEnLaMismaMonedaQue(Moneda otroMonto);

	public abstract String nombreDeLaMoneda();

	public abstract Moneda ceroEnEstaMoneda();

	public abstract Moneda obtenerNuevaMonedaEnLaMismaEconomia(double cantidadDePlata);

	public abstract double convertirADouble();

	public abstract Objeto sinDecimales();
	
	@Override
	public boolean esIgualQue(Objeto objeto) 
	{
		
		Moneda otraMoneda = null;
		try
		{
			otraMoneda = (Moneda)objeto;
			if (tipoDeMoneda != otraMoneda.tipoDeMoneda)
				throw new LanguageException(
						String.format("En la comparaci처n se esperaba el valor de tipo [%s] pero se encontro un valor de tipo [%s]", tipoDeMoneda.name(), otraMoneda.tipoDeMoneda.name())
						);
		}
		catch(ClassCastException p)
		{
			throw new LanguageException(
					String.format("En la comparaci처n se esperaba el valor de tipo [%s] pero se encontro un valor de tipo [%s]", Denominacion.class.getSimpleName(), objeto.getClass().getSimpleName())
					);
		}
		return convertirADouble() == otraMoneda.convertirADouble() && tipoDeMoneda == otraMoneda.tipoDeMoneda;
	}
	
	@Override
	public boolean noEsIgualQue(Objeto objeto) 
	{
		return ! esIgualQue(objeto);
	}
	
	@Override
	public boolean esMenorQue(Objeto objeto) 
	{	
		boolean resultado = false;
		if (objeto instanceof Moneda) 
		{
			Moneda estaMoneda = (Moneda) objeto;
			if (tipoDeMoneda != estaMoneda.tipoDeMoneda)
				throw new LanguageException(
						String.format("En la comparaci처n se esperaba el valor de tipo [%s] pero se encontro un valor de tipo [%s]", tipoDeMoneda.name(), estaMoneda.tipoDeMoneda.name())
						);
			resultado = convertirADouble() < estaMoneda.convertirADouble();
		}
		else 
		{
			throw new LanguageException(
					String.format("No se puede comparar un %s con un %s.", objeto.getClass().getSimpleName(), getClass().getSimpleName()));
		}
		
		return resultado;
	}
	
	@Override
	public boolean esMayorQue(Objeto objeto) 
	{	
		boolean resultado = false;
		if (objeto instanceof Moneda) 
		{
			Moneda estaMoneda = (Moneda) objeto;
			if (tipoDeMoneda != estaMoneda.tipoDeMoneda)
				throw new LanguageException(
						String.format("En la comparaci처n se esperaba el valor de tipo [%s] pero se encontro un valor de tipo [%s]", tipoDeMoneda.name(), estaMoneda.tipoDeMoneda.name())
						);
			resultado = convertirADouble() > estaMoneda.convertirADouble();
		}
		else 
		{
			throw new LanguageException(
					String.format("No se puede comparar un %s con un %s.", objeto.getClass().getSimpleName(), getClass().getSimpleName()));
		}
		
		return resultado;
	}
	
	@Override
	public boolean esMayorOIgualQue(Objeto objeto) 
	{	
		boolean resultado = false;
		if (objeto instanceof Moneda) 
		{
			Moneda estaMoneda = (Moneda) objeto;
			if (tipoDeMoneda != estaMoneda.tipoDeMoneda)
				throw new LanguageException(
						String.format("En la comparaci처n se esperaba el valor de tipo [%s] pero se encontro un valor de tipo [%s]", tipoDeMoneda.name(), estaMoneda.tipoDeMoneda.name())
						);
			resultado = convertirADouble() >= estaMoneda.convertirADouble();
		}
		else 
		{
			throw new LanguageException(
					String.format("No se puede comparar un %s con un %s.", objeto.getClass().getSimpleName(), getClass().getSimpleName()));
		}
		
		return resultado;
	}
	
	@Override
	public boolean esMenorOIgualQue(Objeto objeto) 
	{	
		boolean resultado = false;
		if (objeto instanceof Moneda) 
		{
			Moneda estaMoneda = (Moneda) objeto;
			if (tipoDeMoneda != estaMoneda.tipoDeMoneda)
				throw new LanguageException(
						String.format("En la comparaci처n se esperaba el valor de tipo [%s] pero se encontro un valor de tipo [%s]", tipoDeMoneda.name(), estaMoneda.tipoDeMoneda.name())
						);
			resultado = convertirADouble() <= estaMoneda.convertirADouble();
		}
		else 
		{
			throw new LanguageException(
					String.format("No se puede comparar un %s con un %s.", objeto.getClass().getSimpleName(), getClass().getSimpleName()));
		}
		
		return resultado;
	}

	public boolean esDeTipo(Monedas tipo) 
	{
		return tipoDeMoneda == tipo;
	}

	public Monedas tipoDeMoneda() 
	{
		return tipoDeMoneda;
	}

}
