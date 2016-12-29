package com.ncubo.evaluador.interprete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ncubo.evaluador.db.TablaDeSimbolos;
import com.ncubo.evaluador.interprete.Token.TokenType;
import com.ncubo.evaluador.interprete.libraries.Comando;
import com.ncubo.evaluador.interprete.libraries.ComandoNuevaInstancia;
import com.ncubo.evaluador.interprete.libraries.ComandoNulo;
import com.ncubo.evaluador.interprete.libraries.ComandoShow;
import com.ncubo.evaluador.interprete.libraries.Expresion;
import com.ncubo.evaluador.interprete.libraries.Id;
import com.ncubo.evaluador.interprete.libraries.IdConPunto;
import com.ncubo.evaluador.interprete.libraries.LanguageException;
import com.ncubo.evaluador.interprete.libraries.Linea;
import com.ncubo.evaluador.interprete.libraries.LineaDePrograma;
import com.ncubo.evaluador.interprete.libraries.LiteralBoolean;
import com.ncubo.evaluador.interprete.libraries.LiteralDecimal;
import com.ncubo.evaluador.interprete.libraries.LiteralFecha;
import com.ncubo.evaluador.interprete.libraries.LiteralFechaHora;
import com.ncubo.evaluador.interprete.libraries.LiteralHilera;
import com.ncubo.evaluador.interprete.libraries.LiteralMes;
import com.ncubo.evaluador.interprete.libraries.LiteralMoneda;
import com.ncubo.evaluador.interprete.libraries.LiteralNull;
import com.ncubo.evaluador.interprete.libraries.LiteralNumero;
import com.ncubo.evaluador.interprete.libraries.NuevaInstancia;
import com.ncubo.evaluador.interprete.libraries.OpDividir;
import com.ncubo.evaluador.interprete.libraries.OpIgualQue;
import com.ncubo.evaluador.interprete.libraries.OpMayorOIgualQue;
import com.ncubo.evaluador.interprete.libraries.OpMayorQue;
import com.ncubo.evaluador.interprete.libraries.OpMenorOIgualQue;
import com.ncubo.evaluador.interprete.libraries.OpMenorQue;
import com.ncubo.evaluador.interprete.libraries.OpMenos;
import com.ncubo.evaluador.interprete.libraries.OpMultiplicar;
import com.ncubo.evaluador.interprete.libraries.OpNoIgualQue;
import com.ncubo.evaluador.interprete.libraries.OpRestar;
import com.ncubo.evaluador.interprete.libraries.OpSumar;
import com.ncubo.evaluador.interprete.libraries.ParserValidation;
import com.ncubo.evaluador.interprete.libraries.Programa;
import com.ncubo.evaluador.interprete.libraries.Punto;
import com.ncubo.evaluador.interprete.libraries.PuntoConPunto;
import com.ncubo.evaluador.libraries.Fecha;
import com.ncubo.evaluador.libraries.FechaHora;
import com.ncubo.evaluador.libraries.Hilera;
import com.ncubo.evaluador.libraries.Meses;
import com.ncubo.evaluador.libraries.Monedas;
import com.ncubo.evaluador.libraries.Objeto;

public class Parser 
{
	private Comando ultimoComandoValido = new ComandoNulo("Es el inicio del archivo y no hay un comando anterior.");
	private final TablaDeSimbolos tablaDeSimbolos;
	private final Lexer lexer;
	private final Salida salida;
	private static final HashMap<String, Class<?>> tiposPrimitivos = new HashMap<String, Class<?>>();
	
	static
	{
		for (Class<?> tipoPrimitivo : NuevaInstancia.tiposPrimitivos())
		{
			String nombre = tipoPrimitivo.getSimpleName().toLowerCase();
			tiposPrimitivos.put(nombre, tipoPrimitivo);
		}
	}

	public Parser(TablaDeSimbolos tablaDeSimbolos, Salida salida, String contexto) 
	{
		this.tablaDeSimbolos = tablaDeSimbolos;
		this.salida = salida;
		lexer = new Lexer(contexto);
	}
	
	public Programa procesar()
	{
		Programa result = parsearPrograma();
		return result;
	}

	public void establecerComando(String comando) throws Exception
	{
		this.lexer.setComando(comando);
	}
	
	private Programa parsearPrograma()
	{
		List<Linea> lineas = new ArrayList<Linea>();
		while ( lexer.tokenActual.getType() != TokenType.eof )
		{
			List<Comando> comandosDeLaLinea = new ArrayList<Comando>();
			while (lexer.tokenActual.getType() != TokenType.eof && lexer.tokenActual.getType() != TokenType.eol)
			{
				comandosDeLaLinea.add(parsearComando());
			}
			LineaDePrograma linea = new LineaDePrograma(comandosDeLaLinea);
			lineas.add(linea);
			if (lexer.tokenActual.getType() == TokenType.eol) 
			{
				lexer.aceptar(TokenType.eol);
			}
		}
		lexer.aceptar(TokenType.eof);
		Linea[] lineasArr = new Linea[lineas.size()];
		lineasArr = lineas.toArray(lineasArr);
		return new Programa (salida, lineasArr);
	}

	private Comando parsearComando()
	{
		Comando result = null;
		
		TokenType tipo = lexer.tokenActual.getType();
		switch ( tipo )
		{
			case show:
				result = parsearComandoShow();
				break;
			case id:
				result = parserComandoCreateOCall();
				break;
			case comentarioDeLinea:
				result = parserComentarioDeLinea();
				break;
			default:
				String hileraConProblemas = lexer.tokenActual.getValor();
				throw new LanguageException("Se encontró con un '" + hileraConProblemas + "' donde se esperaba que inicie un comando.");
		}
		ultimoComandoValido = result;
		return result;
	}
	
	
	
	@SuppressWarnings("unchecked")
	private Class<? extends Objeto> parsearTipo()
	{
		String nombreDelTipo = lexer.tokenActual.getValor().toLowerCase();
		Class<?> tipo = tiposPrimitivos.get(nombreDelTipo);
		if (tipo == null)
		{
			throw new LanguageException("Se encontró un tipo no válido en los parámetros del procedimiento: '" + nombreDelTipo + "'", nombreDelTipo, 1, 1);
		}
		lexer.aceptar();
		return (Class<? extends Objeto>) tipo;
	} 
		
	private Comando parserComentarioDeLinea()
	{
		String comentario = lexer.tokenActual.getValor();
		lexer.aceptar(TokenType.comentarioDeLinea);
		return new ComandoNulo(comentario);
	}

	private Comando parserComandoCreateOCall()
	{
		Comando resultado;
		Expresion punto = parsearPunto();
		resultado = parsearComandoCreate (punto);
		lexer.aceptar(TokenType.puntoComa);
		return resultado;
	}
	
	
	private Comando parsearComandoCreate(Expresion lValue)
	{
		lexer.aceptar(TokenType.igual);
		Expresion rValue = parsearExpresion();
		return new ComandoNuevaInstancia(tablaDeSimbolos, lValue, rValue);
	}

	private Comando parsearComandoShow()
	{
		lexer.aceptar();
		Expresion exp  = parsearExpresion();
		lexer.aceptar(TokenType.puntoComa);
		return new ComandoShow(salida, exp);
	}


	private Expresion parsearPunto()
	{
		Expresion resultado = parsearId();
		boolean salir = false;
		while ( ! salir ) 
		{
			TokenType tipo = lexer.tokenActual.getType();
			switch (tipo)
			{
				case punto:
					lexer.aceptar();
					String metodo = lexer.tokenActual.getValor();
					lexer.aceptar(TokenType.id);
					
					if( lexer.tokenActual.getType() != TokenType.lParentesis )
					{
						if (resultado instanceof Id) 
						{
							resultado = new IdConPunto(tablaDeSimbolos, (Id) resultado, metodo );
						}
						else
						{
							resultado = new IdConPunto(tablaDeSimbolos, (NuevaInstancia) resultado, metodo );
						}
					}
					else
					{
						lexer.aceptar(TokenType.lParentesis);
						//TODO aca va un if preguntado si es id o nuevaInstancia
						if (resultado instanceof Id) 
						{
							resultado = new IdConPunto(tablaDeSimbolos, (Id) resultado, metodo, parsearArgumentos() );
						}
						else
						{
							resultado = new IdConPunto(tablaDeSimbolos, (NuevaInstancia) resultado, metodo, parsearArgumentos() );
						}
						
						lexer.aceptar(TokenType.rParentesis);
						
						boolean siguienteEsUnPunto = lexer.tokenActual.getType() == TokenType.punto;
						while(siguienteEsUnPunto )
						{
							lexer.aceptar();
							metodo = lexer.tokenActual.getValor();
							lexer.aceptar(TokenType.id);
							
							if( lexer.tokenActual.getType() != TokenType.lParentesis )
							{
								resultado = new PuntoConPunto( (Punto) resultado, metodo);
							}
							else
							{
								lexer.aceptar(TokenType.lParentesis);
								resultado = new PuntoConPunto( (Punto) resultado, metodo, parsearArgumentos() );
								lexer.aceptar(TokenType.rParentesis);
							}
							siguienteEsUnPunto = lexer.tokenActual.getType() == TokenType.punto;
						}
					}
					break;
				case lParentesis:
					lexer.aceptar();
					resultado = new NuevaInstancia (tablaDeSimbolos, (Id) resultado, parsearArgumentos() );		
					lexer.aceptar(TokenType.rParentesis);
					break;
				default:
					salir = true;
					break;
			}
		}		
		return resultado;
	}

	private Expresion[] parsearArgumentos()
	{
		ArrayList<Expresion> argumentos = new ArrayList<Expresion>();
		boolean salir = false;
		boolean siguienteCierraParentesis = lexer.tokenActual.getType() == TokenType.rParentesis;
		if (siguienteCierraParentesis)
		{
			salir = true;
		}
		while ( ! salir )
		{
			Expresion argumento = parsearExpresion();
			argumentos.add(argumento);
			switch (lexer.tokenActual.getType())
			{
				case coma:
					lexer.aceptar();
					break;
				case rParentesis:
					salir = true;
					break;
				default:
					String hileraConProblemas = lexer.tokenActual.getValor();
					throw new LanguageException("Se esperaba un argumento o un paréntesis para abrir la función pero se encontró, '" + hileraConProblemas + "'",hileraConProblemas,1,1);
			}
		}
		Expresion[] argumentosArr = new Expresion[argumentos.size()];
		argumentosArr = argumentos.toArray(argumentosArr);
		return argumentosArr; 
	}

	private Expresion parsearId()
	{
		String id = lexer.tokenActual.getValor();
		lexer.aceptar(TokenType.id);
		return new Id(tablaDeSimbolos, id);
	}

	private Expresion parsearExpresion()
	{
		Expresion resultado = parseExpresionRelacional();
		return resultado;
	}

	private Expresion parseFecha()  
	{
		Fecha fecha = ParserValidation.parseFechaValidacion(lexer);
		Expresion resultado = new LiteralFecha ( fecha );
		if (lexer.tokenActual.getType() == Token.TokenType.hora )
		{
			resultado = parseFechaHora(fecha);
		}
		return resultado;
	}

	private Expresion parseFechaHora()
	{
		Fecha fecha = ParserValidation.parseFechaValidacion(lexer);

		FechaHora fechaHora = ParserValidation.parseFechaHoraValidacion(fecha, lexer);

		Expresion resultado = new LiteralFechaHora( fechaHora );
		return resultado;
	}
	
	private Expresion parseFechaHora(Fecha fecha)
	{
		FechaHora fechaHora = ParserValidation.parseFechaHoraValidacion(fecha, lexer);

		Expresion resultado = new LiteralFechaHora( fechaHora );
		return resultado;
	}


	private boolean esOperadorRelacional()
	{
		TokenType tipo = lexer.tokenActual.getType();
		
		return 	tipo == TokenType.igualdad || tipo ==  TokenType.desigualdad || tipo == TokenType.menorIgual || 
				tipo ==  TokenType.mayorIgual || tipo == TokenType.menor || tipo ==  TokenType.mayor;
	}

	private Expresion parseExpresionRelacional()
	{
		Expresion resultado = parseExpresionMultiplicativa();

		boolean siguienteOperadorRelacional = esOperadorRelacional();
		if(siguienteOperadorRelacional)
		{
			TokenType tipo = lexer.tokenActual.getType();
			lexer.aceptar();
			Expresion segundoObjeto = parsearExpresion();

			switch( tipo )
			{
				case igualdad:
					resultado = new OpIgualQue(resultado, segundoObjeto);
					break;
	
				case desigualdad:
					resultado = new OpNoIgualQue(resultado, segundoObjeto);
					break;
	
				case menor:
					resultado = new OpMenorQue(resultado,  segundoObjeto);
					break;
	
				case mayor:
					resultado = new OpMayorQue(resultado,  segundoObjeto);
					break;
	
				case menorIgual:
					resultado = new OpMenorOIgualQue(resultado,  segundoObjeto);
					break;
	
				case mayorIgual:
					resultado = new OpMayorOIgualQue(resultado,  segundoObjeto);
					break;
				default:
					break;
			}	
		}
		return resultado;
	}

	private Expresion parseExpresionMultiplicativa() 
	{
		Expresion resultado = parseExpresionAditiva();
		TokenType tipo = lexer.tokenActual.getType();
		Expresion segundoObjeto;
		switch (tipo)
		{
			case multiplicacion:
				lexer.aceptar();
				segundoObjeto = parseExpresionMultiplicativa();
				resultado = new OpMultiplicar(resultado, segundoObjeto);
				break;
			case division:
				lexer.aceptar();
				segundoObjeto = parseExpresionMultiplicativa();
				resultado = new OpDividir(resultado, segundoObjeto);
				break;
			default:
				break;
		}
		return resultado;
	}

	private Expresion parseExpresionAditiva()
	{
		Expresion resultado = parseExpresionAtomica();
		TokenType tipo = lexer.tokenActual.getType();
		Expresion segundoObjeto;
		switch (tipo)
		{
			case suma:
				lexer.aceptar();
				segundoObjeto = parseExpresionMultiplicativa();
				resultado = new OpSumar(resultado, segundoObjeto);
				break;
			case resta:
				lexer.aceptar();
				segundoObjeto = parseExpresionMultiplicativa();
				resultado = new OpRestar (resultado, segundoObjeto);
				break;
			default:
				break;
		}
		return resultado;
	}

	private Expresion parseExpresionAtomica()
	{
		Expresion resultado;
		TokenType tipo = lexer.tokenActual.getType();
		switch (tipo)
		{
			case lParentesis:
				lexer.aceptar();
				resultado = parsearExpresion();
				lexer.aceptar(Token.TokenType.rParentesis);
				break;
			case id:
				resultado = parsearPunto();
				break;
			case resta:
				lexer.aceptar();
				resultado = parseExpresionMultiplicativa();
				resultado = new OpMenos( resultado );
				break;
			case suma:
				lexer.aceptar();
				resultado = parseExpresionMultiplicativa();
				break;
			default : 
				resultado = parseLiteral();
				break;
		}
		return resultado;
	}

	private Expresion parseLiteral() 
	{	
		Expresion resultado = null;
		TokenType tipoDelLiteral = lexer.tokenActual.getType();
		switch( tipoDelLiteral )
		{
			case numero:
				resultado = parseNumero();
				break;
	
			case hilera:
				resultado = parseHilera();
				break;
			
			case decimal:
				resultado = parseDecimal();
				break;
				
			case nulo:
				resultado = parseNull();
				break;
	
			case fecha:
				resultado = parseFecha();
				break;
	
			case mes:
				resultado = parseMes();
				break;
	
			case monto:
				resultado = parseMonto();
				break;
				
			case boolFalse: case boolTrue:
				resultado = parseBoolean();
				break;
			
			default:	
				String hileraConProblemas = lexer.tokenActual.getValor();
				throw new LanguageException("El símbolo '" + hileraConProblemas + "' es desconocido", hileraConProblemas, 1, 1);
		}
		return resultado;
	}
	
	private Expresion parseMes()
	{
		String valor = lexer.tokenActual.getValor();
		int primerSlash = valor.indexOf('/');


		Meses mes = Meses.valueOf(valor.substring(0, primerSlash).toUpperCase());
		int anno = Integer.parseInt(valor.substring(primerSlash+1));

		lexer.aceptar(TokenType.mes);

		return new LiteralMes(mes, anno);
	}

	private Expresion parseBoolean() 
	{
		Expresion resultado = new LiteralBoolean ( java.lang.Boolean.parseBoolean(lexer.tokenActual.getValor().toLowerCase()) );
		lexer.aceptar();
		return resultado;
	}

	private Expresion parseHilera()
	{		
		String literal = lexer.tokenActual.getValor();
		lexer.aceptar();
		return new LiteralHilera(literal);
	}

	private Expresion parseDecimal()
	{
		Expresion decimalLiteral = new LiteralDecimal( Double.parseDouble(lexer.tokenActual.getValor()) );
		lexer.aceptar();
		return decimalLiteral;
	}
	
	private Expresion parseNull()
	{
		Expresion resultado = new LiteralNull();
		lexer.aceptar();
		return resultado;
	}

	private Expresion parseNumero()
	{
		Expresion resultado = new LiteralNumero(Integer.parseInt(lexer.tokenActual.getValor()));
		lexer.aceptar();
		return resultado;
	}

	private Expresion parseMonto() 
	{	
		Monedas tipoActual = Monedas.obtenerTipoDeMonto(new Hilera(lexer.tokenActual.getValor()));
		
		Hilera unidadMonetaria;
		Double cantidad ;
		String simbolo = tipoActual.name();
		unidadMonetaria = new Hilera(simbolo);
		cantidad = Double.parseDouble(lexer.tokenActual.getValor().substring(simbolo.length()));

		Expresion resultado = new LiteralMoneda ( ParserValidation.validacionMoneda(unidadMonetaria, cantidad, lexer) );

		lexer.aceptar();
		return resultado;
	}

	public String comandoActual()
	{
		return ultimoComandoValido.toString();
	}
	
}
