package com.ncubo.evaluador.interprete.libraries;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.ncubo.evaluador.interprete.Lexer;
import com.ncubo.evaluador.interprete.Token.TokenType;
import com.ncubo.evaluador.libraries.Decimal;
import com.ncubo.evaluador.libraries.Denominacion;
import com.ncubo.evaluador.libraries.Fecha;
import com.ncubo.evaluador.libraries.FechaHora;
import com.ncubo.evaluador.libraries.Hilera;
import com.ncubo.evaluador.libraries.Moneda;
import com.ncubo.evaluador.libraries.Monedas;

public class ParserValidation
{
	
	public static Moneda validacionMoneda(Hilera unidadMonetaria, Double cantidad, Lexer lexer)
	{	
		Moneda moneda;		
		boolean esUnaMonedaNuestra = Monedas.contieneLaMoneda(unidadMonetaria.getValor());
		if (esUnaMonedaNuestra) 
		{
			Monedas tipo = Monedas.valueOf(unidadMonetaria.getValor().toUpperCase());
			moneda = new Denominacion(new Decimal(cantidad),tipo);
		}
		else 
		{
			throw new LanguageException(
					String.format("Se esta tratando de ingresar un monto en %s economia, la cual el sistema no soporta.", unidadMonetaria)
					);
		}
		return moneda;
	}

	public static Fecha parseFechaValidacion(Lexer lexer)
	{
		if (lexer.tokenActual.getType() != TokenType.fecha) 
		{
			throw new LanguageException("Se esperaba una fecha en el comando, por favor verificar formato.");
		}
		Fecha resultado = Fecha.fromString(lexer.tokenActual.getValor());
		lexer.aceptar(TokenType.fecha);

		return resultado;
	}
	
	public static FechaHora parseFechaHoraValidacion(Fecha fecha, Lexer lexer)
	{
		boolean tieneHora = lexer.tokenActual != null && lexer.tokenActual.getType() == TokenType.hora;
		if ( ! tieneHora )
		{
			throw new LanguageException("Se esperaba una hora en el comando, por favor verificar formato");
		}
		FechaHora resultado = FechaHora.fromString(fecha, lexer.tokenActual.getValor());
		lexer.aceptar(TokenType.hora);
		return resultado;
	}


	public static void validacionDeMetodo(Class<?> clase, String methodName, Class<?>[] firma)
	{
		boolean existeElNombreDelMetodoEnLaClase = existeELNombreDelMetodoEnLaClase(clase, methodName);
		if( !existeElNombreDelMetodoEnLaClase )
		{
			throw new LanguageException(
					String.format("La función '%s' no se encuentra definida en los valores de tipo '%s', por favor verifique si la funcion corresponde al tipo de valor.", methodName, clase.getSimpleName()), "", 1, 1
					);
		}
		else
		{
			boolean existeAlMenosUnMetodoConEseNombreYConLaMisMaCantidadDeArgumentos = existeAlMenosUnMetodoConLaMismaCantidadArgumentos(clase, methodName, firma);
			if(existeAlMenosUnMetodoConEseNombreYConLaMisMaCantidadDeArgumentos)
			{
				validaErrorEnMetodoConMismaCantidadDeArgumentos(clase, methodName, firma);
			}
			else
			{
				validaErrorEnMetodoConDiferenteCantidadDeArgumentos(clase, methodName, firma);
			}
		}
	}

	private static void validaErrorEnMetodoConDiferenteCantidadDeArgumentos(Class<?> clase, String methodName, Class<?>[] firma) 
	{
		ArrayList<Method> metodosEncontrados = obtenerMetodosDiferenteTamanno(clase, methodName);

		throw new LanguageException(
				String.format("Esta trantando de hacer el llamado a la funcion '%s' con una cantidad erronea de argumentos para los valores de tipo '%s'. %s", methodName, clase.getSimpleName(), obtenerEncabezadosDeMetodosSugeridos(metodosEncontrados)), "", 1, 1
				);
	}

	private static String obtenerEncabezadosDeMetodosSugeridos( ArrayList<Method> metodosEncontrados) 
	{
		StringBuilder encabezados = new StringBuilder();
		encabezados.append("Funciones Sugeridas:");
		
		for(Method metodo : metodosEncontrados )
		{
			String argumentos = "";
			for(Type type: metodo.getParameterTypes())
			{
				argumentos+= "" + ((Class<?>) type).getSimpleName() + ", ";
			}
			argumentos = argumentos.substring(0, argumentos.length() - 2);
			encabezados.append( String.format(" %s(%s); ", metodo.getName(), argumentos) );
		}
		return encabezados.toString();
	}

	private static void validaErrorEnMetodoConMismaCantidadDeArgumentos(Class<?> claseDelObjeto, String methodName, Class<?>[] firma)
	{
		HashMap<Integer, Method> pesosDeMetodosPorErrores = new HashMap<Integer, Method>();
		
		ArrayList<Method> metodosEncontrados = obtenerMetodosMismoTamanno(claseDelObjeto, methodName, firma);
		
		for(Method metodo : metodosEncontrados)
		{
			Class<?>[] firmaEsperadaTemp = obtenerFirmaEsperada(metodo);

			int cantidadErrores = pesosDeMetodosPorErrores.size();
			for(int i=0; i < firma.length; i++)
			{
				Class<?> miClase = firma[i];
				Class<?> claseEsperada = firmaEsperadaTemp[i];
				
				boolean sonCompatibles = claseEsperada.isAssignableFrom( miClase );
				if( !sonCompatibles )
				{
					cantidadErrores++;
				}
			}
			pesosDeMetodosPorErrores.put(cantidadErrores, metodo);
		}
		
		ArrayList<Integer> keys = new ArrayList<>(pesosDeMetodosPorErrores.keySet());
		Collections.sort(keys);
		int metodoConMenosCantidadDeErrores = obtenerKeyMenor(keys);
		
		StringBuilder mensajeDeError = new StringBuilder();
		for(Integer key : keys)
		{
			Method metodo = pesosDeMetodosPorErrores.get(key);
			Class<?>[] firmaEsperadaTemp = obtenerFirmaEsperada(metodo);
			
			if(key == metodoConMenosCantidadDeErrores) 
			{
				for(int i=0; i < firma.length; i++)
				{
					Class<?> miClase = firma[i];
					Class<?> claseEsperada = firmaEsperadaTemp[i];
					
					boolean sonCompatibles = claseEsperada.isAssignableFrom( miClase );
					if( !sonCompatibles )
					{
						mensajeDeError.append(
								String.format("Esta trantando de hacer el llamado a la funcion '%s' con un valor de tipo '%s' en el parametro #%s, donde el esperado debe ser un valor de tipo '%s', por favor corrijalo.", methodName, miClase.getSimpleName(), i+1, claseEsperada.getSimpleName())
								);
					}
				}
			}
			else
			{
				mensajeDeError.append("\n").append(
						String.format("Funciones Sugeridas: %s", obtenerEncabezadoMetodoSugerido(metodo))
						);
			}
		}
		throw new LanguageException(mensajeDeError.toString(),"", 1, 1);
	}
	
	private static int obtenerKeyMenor(ArrayList<Integer> keys) 
	{
		int menor = 0;
		for(int k: keys)
		{
			if(menor == 0 || k < menor)
			{
				menor = k;
			}
		}
		return menor;
	}

	private static String obtenerEncabezadoMetodoSugerido(Method metodo)
	{
		String argumentos = "";
		for(Type type: metodo.getParameterTypes())
		{
			argumentos+= "" + ((Class<?>) type).getSimpleName() + ", ";
		}
		argumentos = argumentos.substring(0, argumentos.length() - 2);
		
		return String.format(" %s(%s)", metodo.getName(), argumentos);
	}

	private static Class<?>[] obtenerFirmaEsperada(Method metodo) 
	{
		Class<?>[] firma = new Class<?>[metodo.getParameterTypes().length];
		int punteroDelArgumento = 0;
		
		for(Type claseDelArgumento : metodo.getParameterTypes() )
		{
			firma[punteroDelArgumento] = (Class<?>) claseDelArgumento;
			punteroDelArgumento++;
		}
		return firma;
	}

	private static ArrayList<Method> obtenerMetodosMismoTamanno(Class<?> clase, String methodName, Class<?>[] firma)
	{
		ArrayList<Method> metodosEncontrados = new ArrayList<Method>();
		String methodUnsensitive = methodName.toUpperCase();
		for (Method method : clase.getMethods()) 
		{
			boolean esElMismoNombre = method.getName().toUpperCase().equals(methodUnsensitive);
			if(esElMismoNombre)
			{
				boolean poseenLaMismaCantidadDeArgumentos = method.getGenericParameterTypes().length == firma.length;
				if(poseenLaMismaCantidadDeArgumentos)
				{
					metodosEncontrados.add(method);
				}
			}
		}
		return metodosEncontrados;
	}
	
	private static ArrayList<Method> obtenerMetodosDiferenteTamanno(Class<?> clase, String methodName)
	{
		String methodUnsensitive = methodName.toUpperCase();
		ArrayList<Method> metodosEncontrados = new ArrayList<Method>();
		for (Method method : clase.getMethods()) 
		{
			boolean esElMismoNombre = method.getName().toUpperCase().equals(methodUnsensitive);
			if(esElMismoNombre)
			{
				metodosEncontrados.add(method);
			}
		}
		return metodosEncontrados;
	}

	private static boolean existeAlMenosUnMetodoConLaMismaCantidadArgumentos(Class<?> clase, String methodName, Class<?>[] firma)
	{
		String methodUnsensitive = methodName.toUpperCase();
		for (Method method : clase.getMethods()) 
		{
			boolean esElMismoNombre = method.getName().toUpperCase().equals(methodUnsensitive);
			if(esElMismoNombre)
			{
				boolean poseenLaMismaCantidadDeArgumentos = method.getGenericParameterTypes().length == firma.length;
				if(poseenLaMismaCantidadDeArgumentos)
				{
					return true;
				}
			}
		}
		return false;
	}

	private static boolean existeELNombreDelMetodoEnLaClase(Class<?> clase, String methodName)
	{
		String methodUnsensitive = methodName.toUpperCase();
		for (Method method : clase.getMethods()) 
		{
			boolean esElMismoNombre = method.getName().toUpperCase().equals(methodUnsensitive);
			if(esElMismoNombre)
			{
				return true;
			}
		}
		return false;
	}

}
