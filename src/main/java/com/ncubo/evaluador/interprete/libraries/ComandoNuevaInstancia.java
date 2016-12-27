package com.ncubo.evaluador.interprete.libraries;

import com.ncubo.evaluador.db.TablaDeSimbolos;
import com.ncubo.evaluador.libraries.Objeto;

public class ComandoNuevaInstancia extends Comando 
{
	private Expresion lValue;
	private Expresion rValue;
	private final TablaDeSimbolos tablaDeSimbolos;

	public ComandoNuevaInstancia(TablaDeSimbolos tablaDeSimbolos, Expresion lValue, Expresion rValue) 
	{
		this.lValue = lValue;
		this.rValue = rValue;
		this.tablaDeSimbolos = tablaDeSimbolos;
	}
	
	@Override
	public void ejecutar() throws Exception
	{
		String nuevaVariable = ((Id) lValue).getValor();
		Objeto valorDeLaExpresionDerecha = rValue.ejecutar();
		tablaDeSimbolos.guardarVariable(nuevaVariable, valorDeLaExpresionDerecha);
	}

	@Override
	public void validarEstaticamente() throws Exception
	{
		lValue.validarEstaticamente();
		rValue.validarEstaticamente();
	}

	@Override
	void write(StringBuilder resultado, int tabs) 
	{
		if(lValue != null && rValue != null)
		{
			resultado.append(generarTabs(tabs));
			lValue.write(resultado);
			resultado.append(" = ");
			rValue.write(resultado);
			resultado.append(";\r");
		}
	}
}
