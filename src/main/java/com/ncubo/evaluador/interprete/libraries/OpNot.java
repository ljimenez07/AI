package com.ncubo.evaluador.interprete.libraries;

import com.ncubo.evaluador.libraries.Objeto;

public class OpNot extends Expresion {

	private Expresion e;
	
	public OpNot(Expresion e)
	{
		this.e = e;
	}
	
	@Override
	Class<? extends Objeto> calcularTipo() {
		return com.ncubo.evaluador.libraries.Boolean.class;
	}
	
	@Override
	void validarEstaticamente()
	{
		if (! e.calcularTipo().equals(com.ncubo.evaluador.libraries.Boolean.class))
		{
			throw new LanguageException(
				String.format("La expresi�n %s al lado derecho del NOT debe retornar un valor true o false.", e.getClass().getSimpleName())
			);
		}
	}
	
	@Override
	public Objeto ejecutar()
	{
		com.ncubo.evaluador.libraries.Boolean objeto1 = (com.ncubo.evaluador.libraries.Boolean) e.ejecutar();
		return new com.ncubo.evaluador.libraries.Boolean( ! objeto1.getValor() );
	}
	
	@Override
	void write(StringBuilder resultado) 
	{
		resultado.append(" ! ");
		e.write(resultado);
	}

	@Override
	public void buscarVariablesEstaticas() {
		e.buscarVariablesEstaticas();
	}

}
