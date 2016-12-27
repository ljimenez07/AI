package com.ncubo.evaluador.interprete.libraries;

import com.ncubo.evaluador.libraries.Mes;
import com.ncubo.evaluador.libraries.Meses;
import com.ncubo.evaluador.libraries.Objeto;

public class LiteralMes extends Expresion {

	private final Mes mes;
	
	public LiteralMes (Meses mes, int ano)
	{
		this.mes = new Mes(mes, ano);
	}
	
	@Override
	Class<? extends Objeto> calcularTipo()
	{
		return Mes.class;
	}
	
	@Override
	public Objeto ejecutar()
	{
		return mes;
	}
	
	@Override
	void write(StringBuilder resultado)
	{
		resultado.append(mes);	
	}
}
