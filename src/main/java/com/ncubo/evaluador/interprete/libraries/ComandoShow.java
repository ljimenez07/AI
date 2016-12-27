package com.ncubo.evaluador.interprete.libraries;

import com.ncubo.evaluador.interprete.Salida;
import com.ncubo.evaluador.libraries.Objeto;

public class ComandoShow extends Comando
{
	private final Expresion expression; 
	private final Salida salida;
	
	public ComandoShow(Salida salida, Expresion expression) 
	{
		this.salida = salida;
		this.expression = expression;
	}
	
	@Override
	public void ejecutar() throws Exception
	{
		if ( ! salida.estaEscribiendo() ) return;
		Objeto resultado = expression.ejecutar();
		String mensaje = resultado.show();
		if ( ! salida.vacio() ) salida.append("\r\n");
		salida.append(mensaje);
	}

	@Override
	public void validarEstaticamente() throws Exception
	{
		Class<? extends Objeto> tipoExpresion = expression.calcularTipo();
		if ( tipoExpresion.equals(Void.class) )
		{
			throw new LanguageException(
					String.format("Al parecer intenta mostar un procedimiento o accion, pero que el comando show solo permite mostrar valores")
			);
		}
		expression.validarEstaticamente();	
	}

	@Override
	void write(StringBuilder resultado, int tabs) 
	{
		resultado.append(generarTabs(tabs));
		resultado.append("Show ");
		expression.write(resultado);
		resultado.append(";\r");
	}

}
