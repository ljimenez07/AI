package com.ncubo.evaluador.interprete.libraries;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import com.ncubo.evaluador.libraries.Nulo;
import com.ncubo.evaluador.libraries.Objeto;

public abstract class Punto extends Expresion 
{
	private String metodo;
	private String propiedad;
	private Expresion[] argumentos;
	
	protected Punto (String metodo, Expresion[] argumentos)
	{
		this.metodo = metodo;
		this.argumentos = argumentos;
	}
	
	protected Punto (String propiedad) 
	{
		this.propiedad = propiedad;
	}
	
	String metodo ()
	{
		return metodo;
	}
	
	String propiedad ()
	{
		return propiedad;
	}
	
	Expresion[] argumentos()
	{
		return argumentos;
	}
	
	@Override
	public Objeto ejecutar() throws Exception
	{
		boolean esUnMetodoYNoUnaPropiedad = metodo != null;
		if( esUnMetodoYNoUnaPropiedad )
		{
			return invocarElMetodo();
		}
		
		
		
		throw new LanguageException("Metodo desconocido: "+metodo);
	}
	
	@SuppressWarnings("unchecked")
	protected Class<? extends Objeto> calcularElTipoDeUnCallExpresion(Class<?> classDeLaInstancia) throws Exception
	{
		Method methodABuscar = null;
		
		boolean esUnMetodoYNoUnaPropiedad = metodo != null;
		if( esUnMetodoYNoUnaPropiedad )
		{
			methodABuscar = obtenerElMetodoDelObjetoSiExiste(classDeLaInstancia);
		}
		
		
		boolean seEncontroELMetodoRespectivo = methodABuscar != null;
		if( seEncontroELMetodoRespectivo )
		{
			if ( methodABuscar.getReturnType().equals(Void.TYPE) )
			{
				return com.ncubo.evaluador.libraries.Void.class;
			}
			return (Class<? extends Objeto>) methodABuscar.getReturnType();
		}
		
		boolean elMetodoNoSeEncontroSobreLaClaseEspecificiada = methodABuscar == null;
		if(elMetodoNoSeEncontroSobreLaClaseEspecificiada)
		{
			ParserValidation.validacionDeMetodo(classDeLaInstancia, metodo, obtenerFirmaDeArgumentos());
		}
		
		return null;
	}
	
	private Class<?>[] obtenerFirmaDeArgumentos() throws Exception
	{
		Class<?>[] firmas = new Class<?>[argumentos.length];
		for(int i = 0; i < argumentos.length; i++)
		{
			Expresion e = argumentos[i];
			firmas[i] = e.calcularTipo();
		}
		return firmas;
	}

	protected Objeto invocarElMetodo() throws Exception
	{
		Object object = obtenerElObjeto();
		Method methodABuscar = obtenerElMetodoDelObjetoSiExiste(object.getClass());		
		
		boolean seEncontroELMetodoRespectivo = methodABuscar != null;
		
		if( ! seEncontroELMetodoRespectivo )
		{
			ParserValidation.validacionDeMetodo(object.getClass(), metodo, obtenerFirmaDeArgumentos());
		}		
		try 
		{
			object = methodABuscar.invoke(object, obtenerFirmaDeValoresBasadosEnElMetodo(methodABuscar));
		} 
		catch (IllegalAccessException e)
		{
			throw new LanguageException(e.getMessage());
		}
		catch (IllegalArgumentException e) 
		{
			throw new LanguageException(e.getMessage());
		} 
		catch (InvocationTargetException e) 
		{
			boolean esBussinessException = e.getTargetException().toString().contains("com.ts.interprete.libraries.BusinessLogicalException:");
			if(esBussinessException)
			{
				LanguageException be = (LanguageException) e.getTargetException();
				StringWriter errors = new StringWriter();
				be.printStackTrace(new PrintWriter(errors));
				throw new LanguageException(e.getTargetException().getMessage()+" \r "+errors.toString().trim());
			}
			else
			{
				throw e;
			}
		}
		return (Objeto) object;
	}
	
	protected Method obtenerElMetodoDelObjetoSiExiste(Class<?> classDelObjeto) throws Exception
	{
		Method metodoEncontrado = null;
		String metodoUnsensitivo = metodo.toUpperCase();
		for(Method method : classDelObjeto.getMethods())
		{
			String methodName = method.getName().toUpperCase();
			if( methodName.equals(metodoUnsensitivo) )
			{
				boolean elMetodoEsPublico =  Modifier.isPublic(method.getModifiers());
				if(elMetodoEsPublico)
				{
					Class<?>[] variables = method.getParameterTypes();
					
					boolean tienenLaMismaCantidadDeArgumentos = variables.length == argumentos.length;
					
					if( tienenLaMismaCantidadDeArgumentos )
					{
						boolean sonValidasLasFirmas = validarLaIntegridadDeLosArgumentosDelMetodo(variables);
						if(sonValidasLasFirmas)
						{
							metodoEncontrado = method;
							break;
						}
					}
				}
			}
		}
		return metodoEncontrado;	
	}
	
	@SuppressWarnings("unchecked")
	private Object[] obtenerFirmaDeValoresBasadosEnElMetodo(Method metodo) throws Exception
	{
		Class<?>[] firmaDelValoresDelMetodo = metodo.getParameterTypes();
		Object[] resultado = new Object[argumentos.length];
		for(int i = 0; i < firmaDelValoresDelMetodo.length; i++)
		{
			Class<?> tipoDelParametro = firmaDelValoresDelMetodo[i];
			boolean deboTambienTratarDeProbarSiPuedeSerUnEnum = tipoDelParametro.isEnum() && argumentos[i].getClass() == Id.class;
			if ( deboTambienTratarDeProbarSiPuedeSerUnEnum )
			{	
				try
				{
					@SuppressWarnings("rawtypes")
					Class tipoDelArgumentoEnum = tipoDelParametro; 
					String nombreDelPosibleValorEnumerado = ((Id) argumentos[i]).getValor().toUpperCase();// DolaR
					
					Object[] Enums = tipoDelArgumentoEnum.getEnumConstants();
					for( Object e : Enums)
					{
						String valorEnElEnum = e.toString().toUpperCase();
						if( valorEnElEnum.equals(nombreDelPosibleValorEnumerado))
						{
							resultado[i] = Enum.valueOf( tipoDelArgumentoEnum, e.toString());
							break;
						}
					}
				}
				catch(IllegalArgumentException ex)
				{
				}
			}
			else
			{
				resultado[i] = argumentos[i].ejecutar();
			}
		}

		return resultado;
	}
	
	private boolean validarLaIntegridadDeLosArgumentosDelMetodo(Class<?>[] variables) throws Exception
	{
		boolean sonCompatibles = argumentos.length == variables.length;
		for(int i = 0; /*===>>>*/ sonCompatibles /*<<<===*/ && i < variables.length; i++)
		{
			Class<?> laClaseOriginal = variables[i];
			
			boolean deboTambienTratarDeProbarSiPuedeSerUnEnum = laClaseOriginal.isEnum() && argumentos[i].getClass() == Id.class;
			
			if ( deboTambienTratarDeProbarSiPuedeSerUnEnum )
			{	
				boolean siSonCompatiblesCambiandoAEnum = true;
				try
				{					
					Class<?> tipoDelArgumentoEnum = laClaseOriginal;
					String nombreDelPosibleValorEnumerado = ((Id) argumentos[i]).getValor().toUpperCase();
					boolean sonCompatiblesLosEnums = false;
					Object[] Enums = tipoDelArgumentoEnum.getEnumConstants();
					for( Object e : Enums)
					{
						String valorEnElEnum = e.toString().toUpperCase();
						if( valorEnElEnum.equals(nombreDelPosibleValorEnumerado))
						{
							sonCompatiblesLosEnums = true;
							break;
						}
					}
					
					if( ! sonCompatiblesLosEnums)
					{
						String valoresDelEnum = "";
						for( Object e : Enums)
						{
							valoresDelEnum = valoresDelEnum + e.toString().concat(" o ");
						}
						
						valoresDelEnum = valoresDelEnum.substring(0, valoresDelEnum.length() - 2);
						
						throw new LanguageException(
								String.format("Esta trantando de poner un valor de tipo %s pero escribio %s, cuando en realidad debio haber escrito %s, por favor corrija el valor.", tipoDelArgumentoEnum.getSimpleName(), nombreDelPosibleValorEnumerado, valoresDelEnum)
								);
					}
				}
				catch (IllegalArgumentException e) 
				{
					throw new LanguageException(e.getMessage());
				} 
				sonCompatibles = siSonCompatiblesCambiandoAEnum;
			}
			else
			{
				Class<?> miClase = argumentos[i].calcularTipo();
				sonCompatibles = laClaseOriginal.isAssignableFrom( miClase );
			}
		}
		return sonCompatibles;
	}

	protected abstract Object obtenerElObjeto() throws Exception;
	
}
