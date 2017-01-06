package com.ncubo.evaluador.interprete;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.StringTokenizer;

import com.ncubo.evaluador.interprete.Token.TokenType;
import com.ncubo.evaluador.interprete.libraries.LanguageException;
import com.ncubo.evaluador.libraries.Meses;
import com.ncubo.evaluador.libraries.Monedas;

public class Lexer 
{
	public Token tokenActual;
	private Entrada entrada;
	
	public Lexer(String contexto)
	{
		contexto += "\f";
		entrada = new Entrada();
		entrada.establecer(contexto);
		obtenerSiguiente();
	}
	
	public Lexer()
	{
	}
	
	public void setComando(String comando)
	{
		comando += " \f";
		entrada.establecer(comando);
		obtenerSiguiente();
	}
	
	private void obtenerSiguiente() 
	{
		while(true)
		{
			try
			{
				entrada.limpiarParaLeerOtroToken();
				eliminarEspacios();
				if(esNumero())
				{
					entrada.avanceGuardandoCaracter();
					if(esNumero())//00..
					{
						entrada.avanceGuardandoCaracter();
						if(entrada.caracterActual == ':') //hora HH:
						{
							entrada.avanceGuardandoCaracter();
							if(esNumero())
							{
								entrada.avanceGuardandoCaracter();
								if(esNumero())
								{
									entrada.avanceGuardandoCaracter();
									if(entrada.caracterActual == ':') //HH:MM:
									{
										entrada.avanceGuardandoCaracter();
										if(esNumero())
										{
											entrada.avanceGuardandoCaracter();
											if(esNumero())
											{
												entrada.avanceGuardandoCaracter();
												if(esUnFinalDeNumero()) //HH:MM:SS<eol>
												{
													tokenActual = new Token(TokenType.hora, entrada.cadenaActual());
													break;
												}

												entrada.devolverCaracter();
											}
											else if(esUnFinalDeNumero()) //HH:MM:S<eol>
											{
												tokenActual = new Token(TokenType.hora, entrada.cadenaActual());
												break;
											}
											entrada.devolverCaracter();
										}
										entrada.devolverCaracter();
									}
									entrada.devolverCaracter();
								}
								else if(entrada.caracterActual == ':') // HH:M:
								{
									entrada.avanceGuardandoCaracter();
									if(esNumero())//HH:M:S
									{
										entrada.avanceGuardandoCaracter();
										if(esNumero()) //HH:M:SS
										{
											entrada.avanceGuardandoCaracter();
											if(esUnFinalDeNumero())
											{
												tokenActual = new Token(TokenType.hora, entrada.cadenaActual());
												break;
											}
										}
										else if(esUnFinalDeNumero()) //HH:M:S<eol>
										{
											tokenActual = new Token(TokenType.hora, entrada.cadenaActual());
											break;
										}
									}
									entrada.devolverCaracter();
								}
								entrada.devolverCaracter();
							}
							entrada.devolverCaracter();
						}
						else if(esUnDividir()) // DD/
						{
							entrada.avanceGuardandoCaracter();
							if(esNumero())
							{
								entrada.avanceGuardandoCaracter();
								if(esNumero())
								{
									entrada.avanceGuardandoCaracter();
									if(esUnDividir()) //DD/MM/
									{
										entrada.avanceGuardandoCaracter();
										if(esNumero())
										{
											entrada.avanceGuardandoCaracter();
											if(esNumero())
											{
												entrada.avanceGuardandoCaracter();
												if(esNumero())
												{
													entrada.avanceGuardandoCaracter();
													if(esNumero())
													{
														entrada.avanceGuardandoCaracter();
														if(esUnFinalDeNumero())
														{
															tokenActual = new Token(TokenType.fecha, entrada.cadenaActual());
															break;
														}
														entrada.devolverCaracter();
													}
													entrada.devolverCaracter();
												}
												entrada.devolverCaracter();
											}
											entrada.devolverCaracter();
										}
										entrada.devolverCaracter();
									}
									entrada.devolverCaracter();
								}
								else if(esUnDividir()) //DD/M/
								{
									entrada.avanceGuardandoCaracter();
									if(esNumero())
									{
										entrada.avanceGuardandoCaracter();
										if(esNumero())
										{
											entrada.avanceGuardandoCaracter();
											if(esNumero())
											{
												entrada.avanceGuardandoCaracter();
												if(esNumero())
												{
													entrada.avanceGuardandoCaracter();
													if(esUnFinalDeNumero())
													{
														tokenActual = new Token(TokenType.fecha, entrada.cadenaActual());
														break;
													}
													entrada.devolverCaracter();
												}
												entrada.devolverCaracter();
											}
											entrada.devolverCaracter();
										}
										entrada.devolverCaracter();
									}
									entrada.devolverCaracter();	
								}
								entrada.devolverCaracter();
							}
							entrada.devolverCaracter();
						}
						entrada.devolverCaracter();
					}
					else if(esUnDividir())//0...
					{
						entrada.avanceGuardandoCaracter();
						if(esNumero())
						{
							entrada.avanceGuardandoCaracter();
							if(esNumero())//D/MM
							{
								entrada.avanceGuardandoCaracter();
								if(esUnDividir())
								{
									entrada.avanceGuardandoCaracter();
									if(esNumero())
									{
										entrada.avanceGuardandoCaracter();
										if(esNumero())
										{
											entrada.avanceGuardandoCaracter();
											if(esNumero())
											{
												entrada.avanceGuardandoCaracter();
												if(esNumero())
												{
													entrada.avanceGuardandoCaracter();
													if(esUnFinalDeNumero())
													{
														tokenActual = new Token(TokenType.fecha, entrada.cadenaActual());
														break;
													}
													entrada.devolverCaracter();
												}
												entrada.devolverCaracter();
											}
											entrada.devolverCaracter();
										}
										entrada.devolverCaracter();
									}
									entrada.devolverCaracter();									
								}
								entrada.devolverCaracter();
							}
							else if(esUnDividir()) //D/M/..
							{
								entrada.avanceGuardandoCaracter();
								if(esNumero())
								{
									entrada.avanceGuardandoCaracter();
									if(esNumero())
									{
										entrada.avanceGuardandoCaracter();
										if(esNumero())
										{
											entrada.avanceGuardandoCaracter();
											if(esNumero())
											{
												entrada.avanceGuardandoCaracter();
												if(esUnFinalDeNumero())
												{
													tokenActual = new Token(TokenType.fecha, entrada.cadenaActual());
													break;
												}
												entrada.devolverCaracter();
											}
											entrada.devolverCaracter();
										}
										entrada.devolverCaracter();
									}
									entrada.devolverCaracter();
								}
								entrada.devolverCaracter();
							}
							entrada.devolverCaracter();
						}
						entrada.devolverCaracter();
					}
					else if(entrada.caracterActual == ':')// H:
					{
						entrada.avanceGuardandoCaracter();
						if(esNumero())
						{
							entrada.avanceGuardandoCaracter();
							if(esNumero()) //H:MM
							{
								entrada.avanceGuardandoCaracter();
								if(entrada.caracterActual == ':') //H:MM:
								{
									entrada.avanceGuardandoCaracter();
									if(esNumero())//H:MM:S
									{
										entrada.avanceGuardandoCaracter();
										if(esNumero()) //H:MM:SS
										{
											entrada.avanceGuardandoCaracter();
											if(esUnFinalDeNumero())
											{
												tokenActual = new Token(TokenType.hora, entrada.cadenaActual());
												break;
											}
										}
										else if(esUnFinalDeNumero()) //H:MM:S
										{
											tokenActual = new Token(TokenType.hora, entrada.cadenaActual());
											break;
										}
									}
									entrada.devolverCaracter();
								}
								entrada.devolverCaracter();
							}
							else if(entrada.caracterActual == ':') //H:N:
							{
								entrada.avanceGuardandoCaracter();
								if(esNumero())//H:M:S
								{
									entrada.avanceGuardandoCaracter();
									if(esNumero()) //H:MM:SS
									{
										entrada.avanceGuardandoCaracter();
										if(esUnFinalDeNumero())
										{
											tokenActual = new Token(TokenType.hora, entrada.cadenaActual());
											break;
										}
									}
									else if(esUnFinalDeNumero()) //H:M:S
									{
										tokenActual = new Token(TokenType.hora, entrada.cadenaActual());
										break;
									}
								}
								entrada.devolverCaracter();
							}
						}
						entrada.devolverCaracter();
					}
					
					boolean esDecimal = procesarNumero();
					if(esDecimal)
					{
						tokenActual = new Token(TokenType.decimal, entrada.cadenaActual());
						break;
					}
					tokenActual = new Token(TokenType.numero, entrada.cadenaActual());
					break;
				}
				else if(esUnCaracterDeId())
				{
					entrada.avanceGuardandoCaracter();
					entrada.avanceGuardandoCaracter();
					entrada.avanceGuardandoCaracter();
					
					if(esAlgunaMoneda())
					{
						if(esEspacio())
						{
							entrada.avanceGuardandoCaracter();
							if(esNumero())
							{
								procesarMonto();
								if(esUnFinalDeNumero())
								{
									tokenActual = new Token(TokenType.monto, entrada.cadenaActual());
									break;
								}
								entrada.devolverCaracter();
							}
							entrada.devolverCaracter();
						}
						else if(esNumero())
						{
							procesarMonto();
							if(esUnFinalDeNumero())
							{
								tokenActual = new Token(TokenType.monto, entrada.cadenaActual());
								break;
							}
							entrada.devolverCaracter();
						}						
					}
					entrada.devolverCaracter();
					entrada.devolverCaracter();
					entrada.devolverCaracter();

					procesarIdentificador();
					
					if(esMultiplicacion())
					{
						entrada.avanceGuardandoCaracter();
						if(esPunto())
						{
							procesarWildCard();
							tokenActual = new Token(TokenType.wildcard, entrada.cadenaActual());
							break;
						}
						entrada.devolverCaracter();
					}
					
					String cadenaActualOriginal = entrada.cadenaActual();
					String cadenaActualUnsensitive = cadenaActualOriginal.toUpperCase();
					if( cadenaActualUnsensitive.equals("SHOW") )
					{
						tokenActual = new Token(TokenType.show, cadenaActualOriginal );
					}
					else if(Meses.contieneElMes(cadenaActualUnsensitive))
					{
						if(esUnDividir())
						{
							entrada.avanceGuardandoCaracter();
							if(esNumero())
							{
								entrada.avanceGuardandoCaracter();
								if(esNumero())
								{
									entrada.avanceGuardandoCaracter();
									if(esNumero())
									{
										entrada.avanceGuardandoCaracter();
										if(esNumero())
										{
											entrada.avanceGuardandoCaracter();
											if(esUnFinalDeNumero())
											{
												tokenActual = new Token(TokenType.mes, entrada.cadenaActual());
												break;
											}
										}
									}
								}
							}
						}

						throw new LanguageException(entrada.cadenaActual() +" es una palabra reservada del lenguaje", cadenaActualOriginal, entrada.fila, entrada.columna);
					}
					else if( cadenaActualUnsensitive.equals("ASSERT") )
					{
						tokenActual = new Token(TokenType.asert, cadenaActualOriginal);
					}
					else if( cadenaActualUnsensitive.equals("TRUE") )
					{
						tokenActual = new Token(TokenType.boolTrue, cadenaActualOriginal);
					}
					else if( cadenaActualUnsensitive.equals("FALSE") )
					{
						tokenActual = new Token(TokenType.boolFalse, cadenaActualOriginal);
					}
					else if( cadenaActualUnsensitive.equals("IF") )
					{
						tokenActual = new Token(TokenType.IF, cadenaActualOriginal);
					}
					else if( cadenaActualUnsensitive.equals("ELSE") )
					{
						tokenActual = new Token(TokenType.ELSE, cadenaActualOriginal);
					}
					else if( cadenaActualUnsensitive.equals("NULL") )
					{
						tokenActual = new Token(TokenType.nulo, cadenaActualOriginal);
					}
					else if( cadenaActualUnsensitive.equals("PROCEDURE") )
					{
						tokenActual = new Token(TokenType.procedure, cadenaActualOriginal);
					}
					else if( cadenaActualUnsensitive.equals("AS") )
					{
						tokenActual = new Token(TokenType.as, cadenaActualOriginal);
					}
					else if( cadenaActualUnsensitive.equals("LIST") )
					{
						tokenActual = new Token(TokenType.list, cadenaActualOriginal);
					}
					else if( cadenaActualUnsensitive.equals("EXIT") )
					{
						tokenActual = new Token(TokenType.exit, cadenaActualOriginal);
					}
					else
					{
						tokenActual = new Token(TokenType.id, cadenaActualOriginal);
					}					
					break;
				}
				else switch(entrada.caracterActual)
				{
				case '\'' :
				case '\"' :
					procesarLiteralString();
					tokenActual = new Token(TokenType.hilera, entrada.cadenaActual());
					return;
				case '.' :
					entrada.avanceCaracterSinGuardar();
					tokenActual = new Token(TokenType.punto, ".");
					return;
				case '+' :
					entrada.avanceCaracterSinGuardar();
					tokenActual = new Token(TokenType.suma, "+");
					return;
				case '-' :
					entrada.avanceCaracterSinGuardar();
					tokenActual = new Token(TokenType.resta, "-");
					return;
				case '/':
					entrada.avanceGuardandoCaracter();
					boolean esComentarioDeLinea = entrada.caracterActual == '/';
					if( esComentarioDeLinea )
					{
						entrada.avanceGuardandoCaracter();
						procesarComentario();
						tokenActual = new Token(TokenType.comentarioDeLinea, entrada.cadenaActual());
					}
					else
					{
						tokenActual = new Token(TokenType.division, "/");
					}
					return;
				case '>' :
					entrada.avanceCaracterSinGuardar();
					if(entrada.caracterActual == '=')
					{
						entrada.avanceCaracterSinGuardar();
						tokenActual = new Token(TokenType.mayorIgual, ">=");
						return;
					}					
					tokenActual = new Token(TokenType.mayor, ">");
					return;
				case '<' :
					entrada.avanceCaracterSinGuardar();
					if(entrada.caracterActual == '=')
					{
						entrada.avanceCaracterSinGuardar();
						tokenActual = new Token(TokenType.menorIgual, "<=");
						return;
					}
					
					tokenActual = new Token(TokenType.menor, "<");
					return;
				case '=' :
					entrada.avanceCaracterSinGuardar();
					if(entrada.caracterActual == '=')
					{
						entrada.avanceCaracterSinGuardar();
						tokenActual = new Token(TokenType.igualdad, "==");
						return;
					}

					tokenActual = new Token(TokenType.igual, "=");
					return;
				case '&' :
					entrada.avanceCaracterSinGuardar();
					if(entrada.caracterActual == '&')
					{
						entrada.avanceCaracterSinGuardar();
						tokenActual = new Token(TokenType.yLogico, "&&");
						return;
					}
					throw new LanguageException("Error de sintaxis en el caracter &", entrada.caracterActual+"", entrada.fila, entrada.columna);
				case '|' :
					entrada.avanceCaracterSinGuardar();
					if(entrada.caracterActual == '|')
					{
						entrada.avanceCaracterSinGuardar();
						tokenActual = new Token(TokenType.oLogico, "&&");
						return;
					}
					throw new LanguageException("Error de sintaxis en el caracter |", entrada.caracterActual+"", entrada.fila, entrada.columna);
				case '!' :
					entrada.avanceCaracterSinGuardar();
					if(entrada.caracterActual == '=')
					{
						entrada.avanceCaracterSinGuardar();
						tokenActual = new Token(TokenType.desigualdad, "!=");
						return;
					}
					tokenActual = new Token(TokenType.negacionLogica, "!");
					return;
				case '*' :
					entrada.avanceGuardandoCaracter();
					if(entrada.caracterActual == '.')
					{
						procesarWildCard();
						tokenActual = new Token(TokenType.wildcard, entrada.cadenaActual());
						return;
					}
					tokenActual = new Token(TokenType.multiplicacion, "*");
					return;
				case '}' :
					entrada.avanceCaracterSinGuardar();
					tokenActual = new Token(TokenType.end, "}");
					return;
				case '{' :
					entrada.avanceCaracterSinGuardar();
					tokenActual = new Token(TokenType.begin, "{");
					return;
				case '(' :
					entrada.avanceCaracterSinGuardar();
					tokenActual = new Token(TokenType.lParentesis, "(");
					return;
				case ')' :
					entrada.avanceCaracterSinGuardar();
					tokenActual = new Token(TokenType.rParentesis, ")");
					return;					
				case ',' :
					entrada.avanceCaracterSinGuardar();
					tokenActual = new Token(TokenType.coma, ",");
					return;
				case ';' :
					entrada.avanceCaracterSinGuardar();
					tokenActual = new Token(TokenType.puntoComa, ";");
					return;
				case '\f' :
					tokenActual = new Token(TokenType.eof, "<eof>");
					return;
				case '\n' :
					entrada.avanceCaracterSinGuardar();
					continue;
				case '\r' :
					entrada.avanceCaracterSinGuardar();
					if (entrada.caracterActual =='\n') entrada.avanceCaracterSinGuardar();
					continue;
				}
			}
			catch(Exception e)
			{
				if(e instanceof LanguageException)
				{
					throw (LanguageException)e;
				}
				throw new LanguageException(" El caracter " + entrada.caracterActual + " es inválido.", entrada.cadenaActual(), entrada.fila, entrada.columna);
			}
			
			throw new LanguageException("La linea presenta un caracter inválido.", entrada.bytesConflictivos(), entrada.fila, entrada.columna);
		}
	}
	
	private void procesarWildCard() throws Exception 
	{
		entrada.avanceGuardandoCaracter();
		while(true)
		{
			if(esUnCaracterDeId() || esMultiplicacion() || esPunto() || esNumero())
			{				
				if(esPunto())
				{
					StringTokenizer wildcard = new StringTokenizer(entrada.cadenaActual(), ".");
					boolean yaTieneUnPunto = wildcard.countTokens() == 2;
					if(yaTieneUnPunto)
					{
						break;
					}
				}
				entrada.avanceGuardandoCaracter();
			}	
			else if(esUnFinalDeNumero())
			{
				break;
			}
		}
	}
	
	private void procesarComentario()
	{
		while(! esFinalDeComando())
		{
			entrada.avanceGuardandoCaracter();
		}
	}

	private boolean esAlgunaMoneda()
	{
		boolean esAlgunaMoneda = Monedas.contieneLaMoneda(entrada.cadenaActual());
		return esAlgunaMoneda;
	}

	public void aceptar() 
	{
		obtenerSiguiente();
	}
	
	public void aceptar(TokenType tipo)
	{
		TokenType currentType = tokenActual.getType();
		if (currentType != tipo)
		{
			throw new LanguageException(String.format("Se esperaba un '%s' y se encontró el valor '%s' de tipo '%s'.", tipo.name(), tokenActual.getValor(), currentType.name() ), entrada.cadenaActual(), entrada.fila, entrada.columna);
		}
		aceptar();
	}
	
	private boolean procesarNumero() throws Exception
	{
		boolean esDecimal = false;
		while(esNumero() || esPunto())
		{
			
			if(esPunto())
			{
				if(esDecimal)
				{
					throw new LanguageException("se encontro mas de 1 punto en el numero", entrada.cadenaActual(), entrada.fila, entrada.columna);
				}
				else
				{
					esDecimal = true;
				}
			}
			entrada.avanceGuardandoCaracter();
			
		}		
		if(esPorcentaje())
		{
			entrada.avanceCaracterSinGuardar();
		}
		if(entrada.caracterActual =='.')
		{
			throw new LanguageException("se encontro un punto al final del numero", entrada.cadenaActual(), entrada.fila, entrada.columna);
		}
		
		return esDecimal;
	}
	
	private boolean procesarMonto() throws Exception
	{
		boolean esDecimal = false;
		int cantidadDeDecimales = 0;
		while(esNumero() || esPunto())
		{
			if(esPunto())
			{
				if(esDecimal)
				{
					throw new LanguageException("se encontro mas de 1 punto en el numero", entrada.cadenaActual(), entrada.fila, entrada.columna);
				}
				else
				{
					esDecimal = true;
				}
			}
			else
			{
				if(esDecimal)
				{
					cantidadDeDecimales++;
				}
			}
			entrada.avanceGuardandoCaracter();
		}		
		if(esPorcentaje())
		{
			entrada.avanceCaracterSinGuardar();
		}
		if(entrada.caracterActual == '.')
		{
			throw new LanguageException("se encontro un punto al final del numero", entrada.cadenaActual(), entrada.fila, entrada.columna);
		}
		if(cantidadDeDecimales>0 && cantidadDeDecimales!=2)
		{
			throw new LanguageException("Los montos de dinero solo pueden tener 0 o 2 decimales", entrada.cadenaActual(), entrada.fila, entrada.columna);
		}
		
		return esDecimal;
	}
	
	private void procesarIdentificador() throws Exception
	{
		entrada.avanceGuardandoCaracter();
		while(true)
		{
			if(esUnCaracterDeId() || esNumero())
			{
				entrada.avanceGuardandoCaracter();
			}
			else
			{
				break;
			}
		}
	}
	
	private void procesarLiteralString() throws Exception
	{
		char comillaInicial = entrada.caracterActual; 
		while(true)
		{
			entrada.avanceGuardandoCaracter();

			if(esBackSlash())
			{
				entrada.avanceCaracterSinGuardar();
			}
			else if(entrada.caracterActual == comillaInicial)
			{
				entrada.avanceGuardandoCaracter();
				break;
			}
		}					
	}
	
	private static final String CARACTERES_VALIDOS = new String(new char[]{'"', ';', '=', ':', ',', '(', ')', '+', '\'', '/', '*', '-', '>', '<', '!', '{', '}', '%', '.', '&', '|'});
	private void eliminarEspacios() throws Exception
	{
		while(true)
		{
			boolean esFinDeArchivo_o_noEsUnEspacio = Character.isLetterOrDigit(entrada.caracterActual) || CARACTERES_VALIDOS.indexOf(entrada.caracterActual) >= 0 || entrada.caracterActual == '\f';
			if (esFinDeArchivo_o_noEsUnEspacio)
			{
				break;
			}
			else
			{
				entrada.avanceCaracterSinGuardar();
			}				
		}
	}
	
	private boolean esMultiplicacion() 
	{
		boolean esUnMultiplicacion = entrada.caracterActual == '*';
		return esUnMultiplicacion;
	}

	private boolean esUnDividir()
	{
		boolean esUnDividir = entrada.caracterActual == '/';
		return esUnDividir;
	}
	
	private boolean esPunto()
	{
		boolean esUnPunto =  entrada.caracterActual == '.';
		return esUnPunto;
	}
	
	private boolean esNumero()
	{
		boolean esUnNumero = Character.isDigit(entrada.caracterActual);
		return esUnNumero;
	}
	
	boolean esEspacio()
	{
		boolean esUnEspacio = entrada.caracterActual == ' ' || entrada.caracterActual =='\t';
		return esUnEspacio;
	}
	
	private static final String OPERADORES = new String(new char[]{'=', '+', '-', '*', '<', '>', '!', '/'});
	private static final String FIN_DE_NUMERO = new String(new char[]{',', ')', '}', ';', '%'});
	private boolean esUnFinalDeNumero()
	{
		boolean esUnFinalDeNumero = esEspacio() || OPERADORES.indexOf(entrada.caracterActual) >= 0 || FIN_DE_NUMERO.indexOf(entrada.caracterActual) >= 0 || esFinalDeComando();
		return esUnFinalDeNumero;
	}
	
	private boolean esFinalDeComando()
	{
		boolean esElFinalDelComando = entrada.caracterActual =='\n' || entrada.caracterActual =='\r' || entrada.caracterActual =='\f';
		return esElFinalDelComando;
	}
	
	private boolean esPorcentaje()
	{
		boolean esUnPorcentaje = entrada.caracterActual == '%';
		return esUnPorcentaje;
	}
	
	private boolean esBackSlash()
	{
		boolean esBackSlash = entrada.caracterActual == '\\';
		return esBackSlash;
	}
	
	private boolean esUnCaracterDeId()
	{
		char character = entrada.caracterActual;
		boolean esLetra = Character.isLetter(character);
		if (esLetra) return true;
		boolean esGuionBajo = character=='_';
		boolean esNumeral = character=='#';
		boolean esArroba = character=='@';
		return esGuionBajo || esNumeral || esArroba;
	}
	
	public int fila()
	{
		return entrada.fila;
	}

	public int columna()
	{
		return entrada.columna;
	}
	
	private class Entrada
	{
		public char caracterActual;
		
		private char[] arregloCaracteres;
		private int indiceActual=0;
		private int fila;
		private int columna;
		private StringBuilder cadenaActual = new StringBuilder();
		
		private Posiciones posiciones = new Posiciones(this); 

		void establecer(String entrada)
		{
			arregloCaracteres = entrada.toCharArray();
			limpiar();
		}
		
		void limpiar()
		{
			caracterActual = ' ';
			indiceActual = 0;
			fila = 1;
			columna = 0;
			cadenaActual.setLength(0);
		}
		
		void limpiarParaLeerOtroToken()
		{
			cadenaActual.setLength(0);
			posiciones.limpiarParaLeerOtroToken();
		}
		
		private void anotarElDesplazamientoDeLaLinea(boolean guardando)
		{
			fila++;
			columna=0;
			avanzar(guardando);
		}
		
		private void anotarElDesplazamientoDeLaColumna(boolean guardando)
		{
			columna++;
			avanzar(guardando);
		}
		
		private void avanzar(boolean guardando)
		{
			if (guardando) 
			{
				posiciones.guardarPosicion();
				cadenaActual.append(caracterActual);
			}
			caracterActual = arregloCaracteres[indiceActual];
			indiceActual++;
		}
		
		void avanceGuardandoCaracter()
		{	
			switch (caracterActual)
			{
				case '\n':
					entrada.anotarElDesplazamientoDeLaLinea(true);
					break;
				case '\f':
					throw new LanguageException("EOF inesperado", entrada.cadenaActual.toString(), entrada.fila, entrada.columna);
				default:
					entrada.anotarElDesplazamientoDeLaColumna(true);
			}
		}
		
		void avanceCaracterSinGuardar()
		{
			switch (caracterActual)
			{
				case '\n':
					entrada.anotarElDesplazamientoDeLaLinea(false);
					break;
				case '\f':
					throw new LanguageException("EOF inesperado", entrada.cadenaActual.toString(), entrada.fila, entrada.columna);
				default: 
					entrada.anotarElDesplazamientoDeLaColumna(false);
			}
		}
		
		void devolverCaracter()
		{
			indiceActual = posiciones.getIndiceActual();
			columna = posiciones.getColumna();
			fila = posiciones.getFila();
			posiciones.quitarLaUltimaPosicion();
			cadenaActual.setLength(cadenaActual.length()-1);
			caracterActual = arregloCaracteres[indiceActual-1];
		}
				
		String cadenaActual()
		{
			return cadenaActual.toString().trim();
		}
		
		String bytesConflictivos() 
		{
			char[] chars = chartSetConflictivo();
		    CharBuffer charBuffer = CharBuffer.wrap(chars);
		    ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
		    byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
		            byteBuffer.position(), byteBuffer.limit());
		    Arrays.fill(charBuffer.array(), '\u0000'); // clear sensitive data
		    Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
		    return new String(bytes);
		}

		private char[] chartSetConflictivo() 
		{
			int punteroDelCaracterAIncluir = new String(arregloCaracteres).lastIndexOf(";") + 1;
			
			int tamanno = arregloCaracteres.length - punteroDelCaracterAIncluir;
			char[] chars = new char[tamanno];
			
			for(int i = 0; i < tamanno; i++)
			{
				chars[i] = arregloCaracteres[punteroDelCaracterAIncluir];
				punteroDelCaracterAIncluir++;
			}
			return chars;
		}
	}
	
	private class Posiciones
	{
		private Entrada entradaActual;
		
		private static final int MAX_TAMANO_DE_UN_LEXEMA = 1024;
		private int fila[] = new int[MAX_TAMANO_DE_UN_LEXEMA];
		private int columna[] = new int[MAX_TAMANO_DE_UN_LEXEMA];
		private int indices[] = new int[MAX_TAMANO_DE_UN_LEXEMA];
		private int indice = -1;
		
		Posiciones(Entrada entrada)
		{
			this.entradaActual = entrada;
			guardarPosicion ();
		}
		
		void limpiarParaLeerOtroToken()
		{
			indice = -1;
			guardarPosicion ();
		}
		
		void guardarPosicion ()
		{
			indice++;
			this.fila[indice] = entradaActual.fila;
			this.columna[indice] = entradaActual.columna;
			this.indices[indice] = entradaActual.indiceActual;
		}
		
		void quitarLaUltimaPosicion()
		{
			indice--;
		}
		
		int getFila()
		{
			return this.fila[indice];
		}
		
		int getColumna()
		{
			return this.columna[indice];
		}
		
		int getIndiceActual()
		{
			return this.indices[indice];
		}
	}

}
