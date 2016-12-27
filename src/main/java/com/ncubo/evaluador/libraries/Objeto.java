package com.ncubo.evaluador.libraries;

import java.util.ArrayList;

import com.ncubo.evaluador.interprete.libraries.LanguageException;

public abstract class Objeto 
{
	private Hilera nombreDeVariable;
	
	
	
	
	 
	
	
	public final void setNombreDeVariable(String nombreDeVariable)
	{
		this.nombreDeVariable = new Hilera(nombreDeVariable);
	}
	
	public Hilera nombreDeVariable()
	{
		return this.nombreDeVariable;
	}
	 
	public String show()  
	{
		throw new LanguageException("El método show no ha sido implementado para "+ this.getClass().getName());
	}
	
	@Override
	public String toString()
	{
		throw new LanguageException(
				String.format("El método toSring no ha sido implementado para %s", this.getClass().getName())
				);
	}

	public Objeto sumar(Objeto objeto)
	{
		throw new LanguageException(
				String.format("El método sumar no ha sido implementado para ", this.getClass().getName())
				);
	}

	public Objeto restar(Objeto objeto) 
	{
		throw new LanguageException(
				String.format("El método restar no ha sido implementado para ", this.getClass().getName())
				);
	}

	public Objeto multiplicar(Objeto objeto) 
	{
		throw new LanguageException(
				String.format("El método multiplicar no ha sido implementado para ", this.getClass().getName())
				);
	}

	public Objeto dividir(Objeto objeto) 
	{
		throw new LanguageException(
				String.format("El método dividir no ha sido implementado para ", this.getClass().getName())
				);
	}
	
	protected boolean igual(Objeto objeto)
	{
		throw new LanguageException(
				String.format("El objeto de tipo [%s] no ha implementado el método 'igual'", this.getClass().getName())
				);
	}
	
	public boolean esIgualQue(Objeto objeto) 
	{
		return this.igual(objeto);
	}

	public boolean noEsIgualQue(Objeto objeto) 
	{
		return ! this.igual(objeto);
	}
	
	public boolean esMayorQue(Objeto objeto) 
	{
		throw new LanguageException(
				String.format("El método esMayorQue no ha sido implementado para ", this.getClass().getName())
				);
	}
	
	public boolean esMenorQue(Objeto objeto) 
	{
		throw new LanguageException(
				String.format("El método esMenorQue no ha sido implementado para ", this.getClass().getName())
				);
	}
	
	public boolean esMayorOIgualQue(Objeto objeto) 
	{
		throw new LanguageException(
				String.format("El método esMayorOIgualQue no ha sido implementado para ", this.getClass().getName())
				);
	}
	
	public boolean esMenorOIgualQue(Objeto objeto)  
	{
		throw new LanguageException(
				String.format("El método esMenorOIgualQue no ha sido implementado para ", this.getClass().getName())
				);
	}

}
