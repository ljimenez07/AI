package com.ncubo.evaluador.interprete.libraries;

import com.ncubo.evaluador.interprete.Salida;

public class Programa extends AST 
{
	private final Linea[] lineasDePrograma;
	private LineaDePrograma lineaEnEjecucion;
	private final Salida salida;
	
	public Programa(Salida salida, Linea[] lineasDePrograma)
	{
		this.salida = salida;
		this.lineasDePrograma = lineasDePrograma;
	}
	
	public String ejecutar()
	{
		/// Limpiar mi array en tabla de simbolos
		if (! salida.vacio()) salida.limpiar();
		for (Linea linea : lineasDePrograma)
		{
			if (linea instanceof LineaDePrograma) lineaEnEjecucion = (LineaDePrograma) linea;
			linea.ejecutar();
		}
		String resultado = salida.toString();
		salida.limpiar();
		// obtener la variables estaticas
		return resultado;
	}
	
	public String lineaEnEjecucion()
	{
		return lineaEnEjecucion.ultimoComandoEjecutado();
	}
	
	public Linea[] lineasDelPrograma()
	{
		return lineasDePrograma;
	}

	public String write()
	{
		StringBuilder builder = new StringBuilder();
		for (Linea linea : lineasDePrograma)
		{
			linea.write(builder);
		}
		return builder.toString();
	}
	
}
