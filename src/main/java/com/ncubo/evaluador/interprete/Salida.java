package com.ncubo.evaluador.interprete;

public class Salida 
{
	private boolean conSalida = false;
	private StringBuilder salida = new StringBuilder();
	
	public void limpiar()
	{
		salida = new StringBuilder();
	}
	
	public void conSalida()
	{
		conSalida = true;
		limpiar();
	}

	public void sinSalida()
	{
		conSalida = false;
		limpiar();
	}
	
	public boolean estaEscribiendo()
	{
		return conSalida;
	}

	public boolean vacio()
	{
		return salida.length() <= 1;
	}
	
	public void append(String hilera)
	{
		if (conSalida) salida.append(hilera);
	}
	
	public void append(char hilera)
	{
		if (conSalida) salida.append(hilera);
	}

	@Override
	public String toString()
	{
		if (conSalida) 
			return salida.toString();
		return null;
	}

}
