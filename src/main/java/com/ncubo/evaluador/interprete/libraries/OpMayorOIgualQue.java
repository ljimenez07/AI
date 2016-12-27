package com.ncubo.evaluador.interprete.libraries;

import com.ncubo.evaluador.libraries.Objeto;

public class OpMayorOIgualQue extends Expresion {

	private Expresion e1;
	private Expresion e2;
	
	public OpMayorOIgualQue(Expresion e1, Expresion e2)
	{
		this.e1 = e1;
		this.e2 = e2;
	}
	
	@Override
	Class<? extends Objeto> calcularTipo()
	{
		return com.ncubo.evaluador.libraries.Boolean.class;
	}

	
	@Override
	public Objeto ejecutar() throws Exception
	{
		Objeto objeto1 = e1.ejecutar();
		Objeto objeto2 = e2.ejecutar();
		return new com.ncubo.evaluador.libraries.Boolean (objeto1.esMayorOIgualQue(objeto2));
	}
	
	@Override
	void write(StringBuilder resultado) 
	{
		e1.write(resultado);
		resultado.append(" >= ");
		e2.write(resultado);
	}
}
