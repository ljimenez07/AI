package com.ncubo.evaluador.interprete.libraries;

import com.ncubo.evaluador.libraries.Objeto;

public class ComandoIf extends Comando 
{
	private final Expresion expresion;
	private final Comando comandosDelIf;
	private final Comando comandosDelElse;
	
	public ComandoIf(Expresion expresion, Comando comandosDelIf)
	{
		this.expresion = expresion;
		this.comandosDelIf = comandosDelIf;
		this.comandosDelElse = null;
	}
	
	public ComandoIf(Expresion expresion, Comando comandoDeIf, Comando comandoDeElse)
	{
		this.expresion = expresion;
		this.comandosDelIf = comandoDeIf;
		this.comandosDelElse = comandoDeElse;
	}
	@Override
	public void ejecutar()
	{
		Objeto valorDeLaExpresion = expresion.ejecutar();
		boolean cumpleCondicion = ((com.ncubo.evaluador.libraries.Boolean) valorDeLaExpresion ).getValor();
		if( cumpleCondicion )
		{
			//comandosDelIf.cargarListenerDeEventos(listenerDeEventos);
			//comandosDelIf.ejecutar();
		}
		else if(comandosDelElse != null)
		{
			//comandosDelElse.ejecutar();
		}
	}
	
	@Override
	public void validarEstaticamente()
	{
		expresion.validarEstaticamente();
		comandosDelIf.validarEstaticamente();
		if (comandosDelElse != null)
		{
			comandosDelElse.validarEstaticamente();
		}
	}
	
	void write(StringBuilder resultado, int tabs)
	{
		resultado.append(generarTabs(tabs));
		resultado.append("If (");
		expresion.write(resultado);
		resultado.append(")");

		if ( !(comandosDelIf instanceof ComandoBloque) ) tabs++;
		comandosDelIf.write(resultado, tabs);
		if ( !(comandosDelIf instanceof ComandoBloque) ) tabs--;

		if (comandosDelElse != null) 
		{
			resultado.append(generarTabs(tabs));
			resultado.append("\rElse\r");
			if ( !(comandosDelElse instanceof ComandoBloque) ) tabs++;
			comandosDelElse.write(resultado, tabs);
			if ( !(comandosDelElse instanceof ComandoBloque) ) tabs--;
		}
	}
}