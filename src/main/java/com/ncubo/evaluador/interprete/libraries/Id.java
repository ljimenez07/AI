package com.ncubo.evaluador.interprete.libraries;

import com.ncubo.evaluador.db.TablaDeSimbolos;
import com.ncubo.evaluador.libraries.Objeto;

public class Id extends Expresion 
{
	private String id;
	private final TablaDeSimbolos tablaDeSimbolos;
	
	public Id (TablaDeSimbolos tablaDeSimbolos, String id)
	{
		this.tablaDeSimbolos = tablaDeSimbolos;
		this.id = id;
	}
	
	public String getValor()
	{
		return id;
	}
	
	@Override
	Class<? extends Objeto> calcularTipo()
	{
		return ejecutar().getClass();
	}
	
	@Override
	public Objeto ejecutar() 
	{
		if ( ! tablaDeSimbolos.existeLaVariable(id) )
		{
			throw new LanguageException(
					String.format("La variable %s no ha sido definida. Verifique si ya creó la variable y que no cometió ningún error en la escritura.", id)
					);
		}
		Objeto valor = tablaDeSimbolos.valor(id);
		return valor;
	}

	@Override
	void write(StringBuilder resultado) 
	{
		resultado.append(id);
	}

	
}
