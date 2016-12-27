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
	private char[] arregloCaracteres;
	private String cadenaActual="";
	public Token tokenActual;
	private int fila=1;//contiene el indice de la linea actual por la cual se esta realizando el analisis
	private int columna=1;//contiene el caracter de la linea en la que se realiza el analisis
	private int indiceActual=0;
	private Posiciones posiciones = new Posiciones();
	
	public Lexer(String contexto)
	{
		contexto += "\f";
		arregloCaracteres = contexto.toCharArray();
		obtenerSiguiente();
	}
	
	public void setComando(String comando) throws Exception
	{
		comando += "\f";
		arregloCaracteres = comando.toCharArray();
		indiceActual = 0;
		obtenerSiguiente();
	}	
	
	private char obtenerSiguienteChar()
	{
		return arregloCaracteres[indiceActual];
	}
	
	private char obtenerCharAnterior()
	{
		return arregloCaracteres[indiceActual-1];
	}
	
	private void avanceGuardandoCaracter()
	{	
		char caracterActual = obtenerSiguienteChar();
		switch (caracterActual)
		{
			case '\n':
				fila++;
				columna=0;
				break;
			default: 
				columna++;
		}
		posiciones.guardarPosicion(fila, columna, indiceActual);
		cadenaActual += caracterActual;
		indiceActual++;
	}
	
	private void avanceCaracterSinGuardar()
	{
		char caracterActual = obtenerSiguienteChar();
		switch (caracterActual)
		{
			case '\n':
				fila++;
				columna=0;
				break;
			default: 
				columna++;
		}
		posiciones.limpiar();
		indiceActual++;		
	}
	
	private void devolverCaracter()
	{
		indiceActual = posiciones.getIndiceActual();
		columna = posiciones.getColumna();
		fila = posiciones.getFila();
		posiciones.quitarLaUltimaPosicion();
		cadenaActual=cadenaActual.substring(0, cadenaActual.length()-1);
	}
	
	private void limpiar()
	{
		cadenaActual="";
		posiciones.limpiar();
	}
	
	String getPalabraActual()
	{
		return cadenaActual;
	}
	
	private void devolverPalabra()
	{
		final int longitudDePalabraActual = cadenaActual.length();
		for(int i=0; i < longitudDePalabraActual; i++)
		{
			devolverCaracter();
		}
			
	}
	
	public boolean elSiguienteEsUn(TokenType tipoAEvaluar) throws Exception
	{
		int indiceParaElSigiente = indiceActual;
		devolverPalabra();
		int indiceReal = indiceActual;
		indiceActual = indiceParaElSigiente;
		obtenerSiguiente();
		boolean resultado = tokenActual.getType() == tipoAEvaluar;		 
		indiceActual = indiceReal;
		obtenerSiguiente();
		return resultado;
	}
	
	protected void obtenerSiguiente() 
	{
		limpiar() ;
		while(true)
		{
			try
			{
				eliminaEspacios();
				
				if(esNumero())
				{
					avanceGuardandoCaracter();
					if(esNumero())//00..
					{
						avanceGuardandoCaracter();
						if(sonDosPuntos()) //hora HH:
						{
							avanceGuardandoCaracter();
							if(esNumero())
							{
								avanceGuardandoCaracter();
								if(esNumero())
								{
									avanceGuardandoCaracter();
									if(sonDosPuntos()) //HH:MM:
									{
										avanceGuardandoCaracter();
										if(esNumero())
										{
											avanceGuardandoCaracter();
											if(esNumero())
											{
												avanceGuardandoCaracter();
												if(esUnFinalDeNumero()) //HH:MM:SS<eol>
												{
													tokenActual = new Token(TokenType.hora,cadenaActual);
													break;
												}

												devolverCaracter();
											}
											else if(esUnFinalDeNumero()) //HH:MM:S<eol>
											{
												tokenActual = new Token(TokenType.hora,cadenaActual);
												break;
											}
											devolverCaracter();
										}
										devolverCaracter();
									}
									devolverCaracter();
								}
								else if(sonDosPuntos()) // HH:M:
								{
									avanceGuardandoCaracter();
									if(esNumero())//HH:M:S
									{
										avanceGuardandoCaracter();
										if(esNumero()) //HH:M:SS
										{
											avanceGuardandoCaracter();
											if(esUnFinalDeNumero())
											{
												tokenActual = new Token(TokenType.hora,cadenaActual);
												break;
											}
										}
										else if(esUnFinalDeNumero()) //HH:M:S<eol>
										{
											tokenActual = new Token(TokenType.hora,cadenaActual);
											break;
										}
									}
									devolverCaracter();
								}
								devolverCaracter();
							}
							devolverCaracter();
						}
						else if(esUnDividir()) // DD/
						{
							avanceGuardandoCaracter();
							if(esNumero())
							{
								avanceGuardandoCaracter();
								if(esNumero())
								{
									avanceGuardandoCaracter();
									if(esUnDividir()) //DD/MM/
									{
										avanceGuardandoCaracter();
										if(esNumero())
										{
											avanceGuardandoCaracter();
											if(esNumero())
											{
												avanceGuardandoCaracter();
												if(esNumero())
												{
													avanceGuardandoCaracter();
													if(esNumero())
													{
														avanceGuardandoCaracter();
														if(esUnFinalDeNumero())
														{
															tokenActual = new Token(TokenType.fecha,cadenaActual);
															break;
														}
														devolverCaracter();
													}
													devolverCaracter();
												}
												devolverCaracter();
											}
											devolverCaracter();
										}
										devolverCaracter();
									}
									devolverCaracter();
								}
								else if(esUnDividir()) //DD/M/
								{
									avanceGuardandoCaracter();
									if(esNumero())
									{
										avanceGuardandoCaracter();
										if(esNumero())
										{
											avanceGuardandoCaracter();
											if(esNumero())
											{
												avanceGuardandoCaracter();
												if(esNumero())
												{
													avanceGuardandoCaracter();
													if(esUnFinalDeNumero())
													{
														tokenActual = new Token(TokenType.fecha,cadenaActual);
														break;
													}
													devolverCaracter();
												}
												devolverCaracter();
											}
											devolverCaracter();
										}
										devolverCaracter();
									}
									devolverCaracter();	
								}
								devolverCaracter();
							}
							devolverCaracter();
						}
						devolverCaracter();
					}
					else if(esUnDividir())//0...
					{
						avanceGuardandoCaracter();
						if(esNumero())
						{
							avanceGuardandoCaracter();
							if(esNumero())//D/MM
							{
								avanceGuardandoCaracter();
								if(esUnDividir())
								{
									avanceGuardandoCaracter();
									if(esNumero())
									{
										avanceGuardandoCaracter();
										if(esNumero())
										{
											avanceGuardandoCaracter();
											if(esNumero())
											{
												avanceGuardandoCaracter();
												if(esNumero())
												{
													avanceGuardandoCaracter();
													if(esUnFinalDeNumero())
													{
														tokenActual = new Token(TokenType.fecha,cadenaActual);
														break;
													}
													devolverCaracter();
												}
												devolverCaracter();
											}
											devolverCaracter();
										}
										devolverCaracter();
									}
									devolverCaracter();									
								}
								devolverCaracter();
							}
							else if(esUnDividir()) //D/M/..
							{
								avanceGuardandoCaracter();
								if(esNumero())
								{
									avanceGuardandoCaracter();
									if(esNumero())
									{
										avanceGuardandoCaracter();
										if(esNumero())
										{
											avanceGuardandoCaracter();
											if(esNumero())
											{
												avanceGuardandoCaracter();
												if(esUnFinalDeNumero())
												{
													tokenActual = new Token(TokenType.fecha,cadenaActual);
													break;
												}
												devolverCaracter();
											}
											devolverCaracter();
										}
										devolverCaracter();
									}
									devolverCaracter();
								}
								devolverCaracter();
							}
							devolverCaracter();
						}
						devolverCaracter();
					}
					else if(sonDosPuntos())// H:
					{
						avanceGuardandoCaracter();
						if(esNumero())
						{
							avanceGuardandoCaracter();
							if(esNumero()) //H:MM
							{
								avanceGuardandoCaracter();
								if(sonDosPuntos()) //H:MM:
								{
									avanceGuardandoCaracter();
									if(esNumero())//H:MM:S
									{
										avanceGuardandoCaracter();
										if(esNumero()) //H:MM:SS
										{
											avanceGuardandoCaracter();
											if(esUnFinalDeNumero())
											{
												tokenActual = new Token(TokenType.hora,cadenaActual);
												break;
											}
										}
										else if(esUnFinalDeNumero()) //H:MM:S
										{
											tokenActual = new Token(TokenType.hora,cadenaActual);
											break;
										}
									}
									devolverCaracter();
								}
								devolverCaracter();
							}
							else if(sonDosPuntos()) //H:N:
							{
								avanceGuardandoCaracter();
								if(esNumero())//H:M:S
								{
									avanceGuardandoCaracter();
									if(esNumero()) //H:MM:SS
									{
										avanceGuardandoCaracter();
										if(esUnFinalDeNumero())
										{
											tokenActual = new Token(TokenType.hora,cadenaActual);
											break;
										}
									}
									else if(esUnFinalDeNumero()) //H:M:S
									{
										tokenActual = new Token(TokenType.hora,cadenaActual);
										break;
									}
								}
								devolverCaracter();
							}
						}
						devolverCaracter();
					}
					
					boolean esDecimal = procesarNumero();
					if(esDecimal)
					{
						tokenActual = new Token(TokenType.decimal,cadenaActual);
						break;
					}
					tokenActual = new Token(TokenType.numero,cadenaActual);
					break;
				}
				else if(esUnCaracterDeId())
				{
					avanceGuardandoCaracter();
					avanceGuardandoCaracter();
					avanceGuardandoCaracter();
					
					if(esAlgunaMoneda())
					{
						if(esEspacio())
						{
							avanceGuardandoCaracter();
							if(esNumero())
							{
								procesarMonto();
								if(esUnFinalDeNumero())
								{
									tokenActual = new Token(TokenType.monto,cadenaActual);
									break;
								}
								devolverCaracter();
							}
							devolverCaracter();
						}
						else if(esNumero())
						{
							procesarMonto();
							if(esUnFinalDeNumero())
							{
								tokenActual = new Token(TokenType.monto,cadenaActual);
								break;
							}
							devolverCaracter();
						}						
					}
					devolverCaracter();
					devolverCaracter();
					devolverCaracter();

					procesarIdentificador();
					
					if(esMultiplicacion())
					{
						avanceGuardandoCaracter();
						if(esPunto())
						{
							procesarWildCard();
							tokenActual = new Token(TokenType.wildcard,cadenaActual);
							break;
						}
						devolverCaracter();
					}
					
					String cadenaActualUnsensitive = cadenaActual.toLowerCase(); 
					if( cadenaActualUnsensitive.equals("show") )
					{
						tokenActual = new Token(TokenType.show,cadenaActual);
					}
					else if(esAlgunMes())
					{
						if(esUnDividir())
						{
							avanceGuardandoCaracter();
							if(esNumero())
							{
								avanceGuardandoCaracter();
								if(esNumero())
								{
									avanceGuardandoCaracter();
									if(esNumero())
									{
										avanceGuardandoCaracter();
										if(esNumero())
										{
											avanceGuardandoCaracter();
											if(esUnFinalDeNumero())
											{
												tokenActual = new Token(TokenType.mes,cadenaActual);
												break;
											}
										}
									}
								}
							}
						}

						throw new LanguageException(cadenaActual+" es una palabra reservada del lenguaje",cadenaActual.trim(),fila,columna);
					}
					else if( cadenaActualUnsensitive.equals("assert") )
					{
						tokenActual = new Token(TokenType.asert, cadenaActual);
					}
					else if( cadenaActualUnsensitive.equals("true") )
					{
						tokenActual = new Token(TokenType.boolTrue, cadenaActual);
					}
					else if( cadenaActualUnsensitive.equals("false") )
					{
						tokenActual = new Token(TokenType.boolFalse, cadenaActual);
					}
					else if( cadenaActualUnsensitive.equals("if") )
					{
						tokenActual = new Token(TokenType.IF, cadenaActual);
					}
					else if( cadenaActualUnsensitive.equals("else") )
					{
						tokenActual = new Token(TokenType.ELSE, cadenaActual);
					}
					else if( cadenaActualUnsensitive.equals("null") )
					{
						tokenActual = new Token(TokenType.nulo, cadenaActual);
					}
					else if( cadenaActualUnsensitive.equals("procedure") )
					{
						tokenActual = new Token(TokenType.procedure, cadenaActual);
					}
					else if( cadenaActualUnsensitive.equals("as") )
					{
						tokenActual = new Token(TokenType.as, cadenaActual);
					}
					else if( cadenaActualUnsensitive.equals("list") )
					{
						tokenActual = new Token(TokenType.list, cadenaActual);
					}
					else if( cadenaActualUnsensitive.equals("exit") )
					{
						tokenActual = new Token(TokenType.exit, cadenaActual);
					}
					else
					{
						tokenActual = new Token(TokenType.id,cadenaActual);
					}					
					break;
				}
				else if(esLiteral())
				{
					procesarLiteralString(obtenerSiguienteChar());
					tokenActual = new Token(TokenType.hilera,cadenaActual);
					break;
				}
				else if(esPunto())
				{
					avanceGuardandoCaracter();
					tokenActual = new Token(TokenType.punto,cadenaActual);
					break;
				}
				else if(esUnMas())
				{
					avanceGuardandoCaracter();
					tokenActual = new Token(TokenType.suma,cadenaActual);
					break;
				}
				else if(esUnMenos())
				{
					avanceGuardandoCaracter();
					tokenActual = new Token(TokenType.resta,cadenaActual);
					break;
				}
				else if(esUnDividir())  
				{
					avanceGuardandoCaracter();
					boolean esComentarioDeLinea = obtenerSiguienteChar() == '/';
					if( esComentarioDeLinea )
					{
						avanceGuardandoCaracter();
						procesarComentario();
						tokenActual = new Token(TokenType.comentarioDeLinea, cadenaActual);
						break;
					}
					else
					{
						tokenActual = new Token(TokenType.division,cadenaActual);
						break;
					}
				}
				else if(esUnMayorQue())
				{
					avanceGuardandoCaracter();
					if(esUnIgual())
					{
						avanceGuardandoCaracter();
						tokenActual = new Token(TokenType.mayorIgual,cadenaActual);
						break;
					}					
					tokenActual = new Token(TokenType.mayor,cadenaActual);
					break;
				}
				else if(esUnMenorQue())
				{
					avanceGuardandoCaracter();
					if(esUnIgual())
					{
						avanceGuardandoCaracter();
						tokenActual = new Token(TokenType.menorIgual,cadenaActual);
						break;
					}
					
					tokenActual = new Token(TokenType.menor,cadenaActual);
					break;
				}
				else if(esUnIgual())
				{
					avanceGuardandoCaracter();
					if(esUnIgual())
					{
						avanceGuardandoCaracter();
						tokenActual = new Token(TokenType.igualdad,cadenaActual);
						break;
					}

					tokenActual = new Token(TokenType.igual,cadenaActual);
					break;
				}
				else if(esUnNegacion())
				{
					avanceGuardandoCaracter();
					if(esUnIgual())
					{
						avanceGuardandoCaracter();
						tokenActual = new Token(TokenType.desigualdad,cadenaActual);
						break;
					}

					tokenActual = new Token(TokenType.desigualdad, cadenaActual);
					break;
				}
				else if(esMultiplicacion())
				{
					avanceGuardandoCaracter();
					if(esPunto())
					{
						
						procesarWildCard();
						tokenActual = new Token(TokenType.wildcard,cadenaActual);
						break;
					}
					tokenActual = new Token(TokenType.multiplicacion, cadenaActual);
					break;
				}
				else if( esFinDeBloque() )
				{
					avanceGuardandoCaracter();
					tokenActual = new Token(TokenType.end, cadenaActual);
					break;
				}
				else if( esInicioDeBloque() )
				{
					avanceGuardandoCaracter();
					tokenActual = new Token(TokenType.begin, cadenaActual);
					break;
				}
				else if(esParentesisI())
				{
					avanceGuardandoCaracter();
					tokenActual = new Token(TokenType.lParentesis,cadenaActual);
					break;
				}
				else if(esParentesisD())
				{
					avanceGuardandoCaracter();
					tokenActual = new Token(TokenType.rParentesis,cadenaActual);
					break;
				}
				else if( esComa() )
				{
					avanceGuardandoCaracter();
					tokenActual = new Token(TokenType.coma,cadenaActual);
					break;
				}
				else if(esPuntoYComa())
				{
					avanceGuardandoCaracter();
					tokenActual = new Token(TokenType.puntoComa,cadenaActual);
					break;
				}
				else if(obtenerSiguienteChar() =='\f')
				{
					tokenActual = new Token(TokenType.eof, "<eof>");
					break;
				}
				else if(obtenerSiguienteChar() =='\n')
				{
					avanceGuardandoCaracter();
					tokenActual = new Token(TokenType.eol, "<eol>");
					break;
				}
				else if(obtenerSiguienteChar() =='\r')
				{
					avanceCaracterSinGuardar();
					if (obtenerSiguienteChar() =='\n') avanceGuardandoCaracter();
					tokenActual = new Token(TokenType.eol, "<eol>");
					break;
				}
			}
			catch(Exception e)
			{
				if(e instanceof LanguageException)
				{
					throw (LanguageException)e;
				}
				throw new LanguageException(" El caracter " + obtenerSiguienteChar() + " es inválido.",cadenaActual.trim(),fila,columna);
			}
			
			throw new LanguageException("La linea presenta un caracter inválido.", bytesConflictivos(), fila, columna);
		}
	}
	
	private String bytesConflictivos() 
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

	private void procesarWildCard() throws Exception 
	{
		avanceGuardandoCaracter();
		while(true)
		{
			if(esUnCaracterDeId() || esMultiplicacion() || esPunto() || esNumero())
			{				
				if(esPunto())
				{
					StringTokenizer wildcard = new StringTokenizer(cadenaActual.toString(), ".");
					boolean yaTieneUnPunto = wildcard.countTokens() == 2;
					if(yaTieneUnPunto)
					{
						break;
					}
				}
				avanceGuardandoCaracter();
			}	
			else if(esUnFinalDeNumero())
			{
				break;
			}
		}
	}
	
	private void procesarComentario() throws Exception 
	{
		while(! esFinalDeComando())
		{
			avanceGuardandoCaracter();
		}
	}

	private boolean esAlgunMes() 
	{
		boolean esList = Meses.contieneElMes(cadenaActual);
		return esList;
	}
	
	private boolean esAlgunaMoneda()
	{
		boolean esAlgunaMoneda = Monedas.contieneLaMoneda(cadenaActual);
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
			throw new LanguageException(String.format("Se esperaba un '%s' y se encontró el valor '%s' de tipo '%s'.", tipo.name(), tokenActual.getValor(), currentType.name() ), cadenaActual.trim(), fila, columna);
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
					throw new LanguageException("se encontro mas de 1 punto en el numero",cadenaActual.trim(),fila,columna);
				}
				else
				{
					esDecimal = true;
				}
			}
			avanceGuardandoCaracter();
			
		}		
		if(esPorcentaje())
		{
			avanceCaracterSinGuardar();
		}
		if(obtenerCharAnterior()=='.')
		{
			throw new LanguageException("se encontro un punto al final del numero",cadenaActual.trim(),fila,columna);
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
					throw new LanguageException("se encontro mas de 1 punto en el numero",cadenaActual.trim(),fila,columna);
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
			avanceGuardandoCaracter();
			
		}		
		if(esPorcentaje())
		{
			avanceCaracterSinGuardar();
		}
		if(obtenerCharAnterior()=='.')
		{
			throw new LanguageException("se encontro un punto al final del numero",cadenaActual.trim(),fila,columna);
		}
		if(cantidadDeDecimales>0 && cantidadDeDecimales!=2)
		{
			throw new LanguageException("Los montos de dinero solo pueden tener 0 o 2 decimales",cadenaActual.trim(),fila,columna);
		}
		
		return esDecimal;
	}
	
	private void procesarIdentificador() throws Exception
	{
		avanceGuardandoCaracter();
		while(true)
		{
			if(esUnCaracterDeId() || esNumero())
			{
				avanceGuardandoCaracter();
			}
			else
			{
				break;
			}
		}
	}
	
	private void procesarLiteralString(char comillaInicial) throws Exception
	{
		while(true)
		{
			avanceGuardandoCaracter();

			if(esBackSlash())
			{
				avanceCaracterSinGuardar();
			}
			else if(obtenerSiguienteChar()==comillaInicial)
			{
				avanceGuardandoCaracter();
				break;
			}
		}					
	}
	
	private void eliminaEspacios() throws Exception
	{
		while(true)
		{
			char sgteCaracter = obtenerSiguienteChar();
			boolean esFinDeArchivo_o_noEsUnEspacio = sgteCaracter == '\f' || ! (sgteCaracter == ' ' || sgteCaracter == '\t');
			if (esFinDeArchivo_o_noEsUnEspacio)
			{
				break;
			}
			else
			{
				avanceCaracterSinGuardar();
			}				
		}
	}
	
	private boolean esMultiplicacion() 
	{
		boolean esUnMultiplicacion = obtenerSiguienteChar()=='*';
		return esUnMultiplicacion;
	}

	private boolean esUnDividir()
	{
		boolean esUnDividir = obtenerSiguienteChar()=='/';
		return esUnDividir;
	}
	
	private boolean esUnMayorQue()
	{
		boolean esUnMayorQue = obtenerSiguienteChar()=='>';
		return esUnMayorQue;
	}
	private boolean esUnMenorQue()
	{
		boolean esUnMenorQue = obtenerSiguienteChar()=='<';
		return esUnMenorQue;
	}
	
	private boolean esUnMas()
	{
		boolean esUnMas = obtenerSiguienteChar()=='+';
		return esUnMas;
	}
	
	private boolean esUnMenos()
	{
		boolean esUnMenos = obtenerSiguienteChar()=='-';
		return esUnMenos;
	}
	
	private boolean esUnIgual()
	{
		boolean esUnIgual = obtenerSiguienteChar()=='=';
		return esUnIgual;
	}
	
	private boolean esUnNegacion()
	{
		boolean esNegacion = obtenerSiguienteChar()=='!';
		return esNegacion;
	}
	
	private boolean esPunto()
	{
		boolean esUnPunto =  obtenerSiguienteChar()=='.';
		return esUnPunto;
	}
	
	private boolean esNumero()
	{
		boolean esUnNumero = Character.isDigit(obtenerSiguienteChar());
		return esUnNumero;
	}
	
	private boolean sonDosPuntos()
	{
		boolean sonDosPuntos = obtenerSiguienteChar()==':';
		
		return sonDosPuntos;
	}

	boolean esEspacio()
	{
		char sgteCaracter = obtenerSiguienteChar();
		boolean esUnEspacio = sgteCaracter == ' ' || sgteCaracter =='\t';
		return esUnEspacio;
	}
	
	private boolean esUnFinalDeNumero()
	{
		return esEspacio() || esComa() || esFinDeBloque() || esParentesisD() || esUnOperador() || esPuntoYComa() || esFinalDeComando() ||  esPorcentaje();
	}
	
	private boolean esUnOperador() 
	{
		return esUnMas() || esUnMenos() || esUnDividir() || esUnMayorQue() || esUnMenorQue() || esUnIgual() || esUnNegacion() || esMultiplicacion();
	}
	
	private boolean esFinalDeComando()
	{
		char sgteCaracter = obtenerSiguienteChar();
		boolean esElFinalDelComando = sgteCaracter =='\n' || sgteCaracter =='\r' || sgteCaracter =='\f';
		return esElFinalDelComando;
	}
	
	private boolean esPorcentaje()
	{
		boolean esUnPorcentaje = obtenerSiguienteChar()=='%';
		return esUnPorcentaje;
	}
	
	private boolean esBackSlash()
	{
		boolean esBackSlash = obtenerSiguienteChar()=='\\';
		return esBackSlash;
	}

	private boolean esPuntoYComa()
	{
		boolean esPuntoYComa = obtenerSiguienteChar()==';';
		return esPuntoYComa;
	}
	private boolean esParentesisD()
	{
		boolean esParentesisD = obtenerSiguienteChar()==')';
		return esParentesisD;
	}
	
	private boolean esComa()
	{
		boolean esUnaComa = obtenerSiguienteChar()==',';
		return esUnaComa;
	}
	
	private boolean esFinDeBloque()
	{
		boolean esUnaComa = obtenerSiguienteChar()=='}';
		return esUnaComa;
	}
	
	private boolean esInicioDeBloque()
	{
		boolean esUnaComa = obtenerSiguienteChar()=='{';
		return esUnaComa;
	}
	
	private boolean esLiteral()
	{
		char sgteCaracter = obtenerSiguienteChar();	
		boolean esTexto = sgteCaracter == '"' || sgteCaracter == '\'';
		return esTexto;
	}
	
	private boolean esUnCaracterDeId()
	{
		char character = obtenerSiguienteChar();
		boolean esLetra = Character.isLetter(character);
		if (esLetra) return true;
		boolean esGuionBajo = character=='_';
		boolean esNumeral = character=='#';
		boolean esArroba = character=='@';
		return esGuionBajo || esNumeral || esArroba;
	}
	
	private boolean esParentesisI()
	{
		boolean esParentesisI = obtenerSiguienteChar()=='(';
		return esParentesisI;
	}
	
	public int fila()
	{
		return fila;
	}

	public int columna()
	{
		return columna;
	}
	
	private class Posiciones
	{
		private static final int MAX_TAMANO_DE_UN_LEXEMA = 1024;
		private int fila[] = new int[MAX_TAMANO_DE_UN_LEXEMA];
		private int columna[] = new int[MAX_TAMANO_DE_UN_LEXEMA];
		private int indices[] = new int[MAX_TAMANO_DE_UN_LEXEMA];
		private int indice = -1;
		
		void guardarPosicion (int fila, int columna, int indiceActual)
		{
			indice++;
			this.fila[indice] = fila;
			this.columna[indice] = columna;
			this.indices[indice] = indiceActual;
		}
		
		void limpiar()
		{
			indice = -1;
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
