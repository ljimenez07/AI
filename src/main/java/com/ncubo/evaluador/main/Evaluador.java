package com.ncubo.evaluador.main;

import java.io.PrintStream;
import com.ncubo.evaluador.db.TablaDeSimbolos;
import com.ncubo.evaluador.interprete.Parser;
import com.ncubo.evaluador.interprete.Salida;
import com.ncubo.evaluador.interprete.libraries.Programa;

public class Evaluador {

	private Parser parser;
	private PrintStream output;
	private final TablaDeSimbolos tablaDeSimbolos = new TablaDeSimbolos();
	private final Salida salida = new Salida();
	
	public void crearContexto(String contexto)
	{
		Programa programa = null;
		this.parser = new Parser(tablaDeSimbolos, salida, contexto);
		salida.sinSalida();
		programa = parser.procesar();
		programa.ejecutar();
	}

	public String ejecutaComando(String dato) throws Exception
	{ 
		String resultado = "";
		if(! dato.isEmpty())
		{
			parser.establecerComando(dato);
			Programa programa = parser.procesar();
			salida.conSalida();
			resultado = programa.ejecutar();
			salida.limpiar();
		}
		return resultado;
	}

	public static void main(String[] args) throws Exception
    {
		Evaluador evaluador = new Evaluador();
		/*evaluador.crearContexto("a = 10;b = 5; f = 23/11/2016;");
		System.out.println(evaluador.ejecutaComando("a = (a+1)/2; show a;").trim());*/
		//System.out.println(evaluador.ejecutaComando("show f.getDia();"));
		
		evaluador.crearContexto("leGustaLosHoteles = 0.0;");
		System.out.println(evaluador.ejecutaComando("leGustaLosHoteles = 'Oscar Orlando Pagoaca Argueta'; show leGustaLosHoteles;").trim());
		
    }
}
