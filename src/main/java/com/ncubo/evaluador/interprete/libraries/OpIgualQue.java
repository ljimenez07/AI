package com.ncubo.evaluador.interprete.libraries;

import com.ncubo.evaluador.libraries.Nulo;
import com.ncubo.evaluador.libraries.Objeto;

public class OpIgualQue extends Expresion {

	private Expresion e1;
	private Expresion e2;
	
	public OpIgualQue(Expresion e1, Expresion e2)
	{
		this.e1 = e1;
		this.e2 = e2;
	}
	
	@Override
	Class<? extends Objeto> calcularTipo() {
		return com.ncubo.evaluador.libraries.Boolean.class;
	}
	
	@Override
	public Objeto ejecutar()
	{
		Objeto objeto1 = e1.ejecutar();
		Objeto objeto2 = e2.ejecutar();
		if( objeto2.getClass() == Nulo.class )
		{
			return new com.ncubo.evaluador.libraries.Boolean (objeto2.esIgualQue(objeto1));
		}
		else
		{
			return new com.ncubo.evaluador.libraries.Boolean (objeto1.esIgualQue(objeto2));
		}
	}
	
	@Override
	void write(StringBuilder resultado) 
	{
		e1.write(resultado);
		resultado.append(" == ");
		e2.write(resultado);
	}

	@Override
	public void buscarVariablesEstaticas() {
		e1.buscarVariablesEstaticas();
		e2.buscarVariablesEstaticas();
	}



}
