package com.ncubo.evaluador.interprete.libraries;

import java.util.List;

public class LineaDePrograma extends Linea 
{
	private final List<Comando> comandos;
	private StringBuilder ultimoComandoEjecutado;
	
	public LineaDePrograma(List<Comando> comandos)
	{
		ultimoComandoEjecutado = new StringBuilder();
		this.comandos = comandos;
	}
	
	public String ultimoComandoEjecutado()
	{
		return ultimoComandoEjecutado.toString();
	}

	void ejecutar()
	{
		for(Comando comando : comandos)
		{
			try
			{
				comando.ejecutar();
			}
			catch (Exception e)
			{
				ultimoComandoEjecutado = new StringBuilder();
				comando.write(ultimoComandoEjecutado, 0);
				throw new LanguageException(" "+e.getMessage()+" "+ultimoComandoEjecutado);
			}
		}
	}
	
	public List<Comando> comandos()
	{
		return comandos;
	}
	
	@Override
	void write(StringBuilder resultado)
	{
		for (Comando comando : comandos)
		{
			comando.write(resultado, 0);
		}
	}
	
}
