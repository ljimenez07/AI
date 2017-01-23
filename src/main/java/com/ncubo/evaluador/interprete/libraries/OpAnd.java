package com.ncubo.evaluador.interprete.libraries;

import com.ncubo.evaluador.libraries.Objeto;

public class OpAnd extends Expresion {

	private Expresion e1;
	private Expresion e2;
	
	public OpAnd(Expresion e1, Expresion e2)
	{
		this.e1 = e1;
		this.e2 = e2;
	}
	
	@Override
	Class<? extends Objeto> calcularTipo() {
		return com.ncubo.evaluador.libraries.Boolean.class;
	}
	
	@Override
	void validarEstaticamente()
	{
		if (! e1.calcularTipo().equals(com.ncubo.evaluador.libraries.Boolean.class))
		{
			throw new LanguageException(
				String.format("La expresi�n %s al lado izquierdo del AND debe retornar un valor true o false.", e1.getClass().getSimpleName())
			);
		}
		if (! e2.calcularTipo().equals(com.ncubo.evaluador.libraries.Boolean.class))
		{
			throw new LanguageException(
				String.format("La expresi�n %s al lado derecho del AND debe retornar un valor true o false.", e2.getClass().getSimpleName())
			);
		}
	}
	
	@Override
	public Objeto ejecutar()
	{
		com.ncubo.evaluador.libraries.Boolean objeto1 = (com.ncubo.evaluador.libraries.Boolean) e1.ejecutar();
		boolean cortoCircuito = ! objeto1.getValor();
		if ( cortoCircuito ) return new com.ncubo.evaluador.libraries.Boolean(false);
		com.ncubo.evaluador.libraries.Boolean objeto2 = (com.ncubo.evaluador.libraries.Boolean) e2.ejecutar();
		return new com.ncubo.evaluador.libraries.Boolean(objeto2.getValor());
	}
	
	@Override
	void write(StringBuilder resultado) 
	{
		e1.write(resultado);
		resultado.append(" && ");
		e2.write(resultado);
	}

	@Override
	public void buscarVariablesEstaticas() {
		e1.buscarVariablesEstaticas();
		e2.buscarVariablesEstaticas();
	}



}
