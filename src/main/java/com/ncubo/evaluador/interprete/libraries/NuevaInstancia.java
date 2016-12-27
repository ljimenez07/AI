package com.ncubo.evaluador.interprete.libraries;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import com.ncubo.evaluador.libraries.Objeto;

public class NuevaInstancia extends Expresion
{
	private String clase;
	private Expresion[] argumentos;
	private Objeto instancia;
	
	private final static String BIBLIOTECA = "com.ncubo.evaluador.libraries";
	private final static ClassFinder finder = new ClassFinder(BIBLIOTECA);
	private final static Class<?>[] libraries = finder.find();
	
	public NuevaInstancia (Id clase)
	{
		this.clase = clase.getValor();
		this.argumentos = new Expresion[]{};
	}
		
	@Override
	Class<? extends Objeto> calcularTipo() throws Exception
	{
		if (instancia == null) instancia = instanciarElObjeto();
		return instancia.getClass();
	}

	@Override
	public  Objeto ejecutar() throws Exception
	{	
		if (instancia == null) instancia = instanciarElObjeto();
		return instancia;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object[] obtenerFirmaDeValoresBasadosEnElConstructor(Constructor<?> constructor) throws Exception
	{
		Class<?>[] firmaDelConstructor = constructor.getParameterTypes();
		Object[] resultado = new Object[argumentos.length];
		for(int i = 0; i < constructor.getGenericParameterTypes().length; i++)
		{	
			Class<?> tipoDelParametro = firmaDelConstructor[i];

			boolean deboTambienTratarDeProbarSiPuedeSerUnEnum = tipoDelParametro.isEnum() && argumentos[i].getClass() == Id.class;
			if ( deboTambienTratarDeProbarSiPuedeSerUnEnum )
			{	
				try
				{
					Class tipoDelArgumentoEnum = tipoDelParametro; 
					String nombreDelPosibleValorEnumerado = ((Id) argumentos[i]).getValor().toUpperCase();
					
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
					throw new LanguageException("" + ex.getMessage());
				}
			}
			else
			{
				resultado[i] = argumentos[i].ejecutar();
			}
		}
		return resultado;
	}

	private Objeto instanciarElObjeto() throws Exception
	{
		Constructor<?> constructor = obtenerConstructorDeLaClase();
		Object[] firmaValoresConstructor = obtenerFirmaDeValoresBasadosEnElConstructor(constructor);
		
		try
		{
			return  (Objeto) constructor.newInstance(firmaValoresConstructor);
		}   
		catch (InstantiationException e) 
        {
            throw new LanguageException("" + e.getMessage());
        }
		catch (IllegalAccessException e)
		{
			throw new LanguageException("" + e.getMessage());
		}
		catch (IllegalArgumentException e) 
		{
			throw new LanguageException("" + e.getMessage());
		}
		catch (InvocationTargetException e) 
		{
			throw new LanguageException("" + e.getTargetException().getMessage());
		}
		catch (Exception e) 
		{
			throw new LanguageException(
					String.format("Hubo un error al instanciar la clase %s. Ver detalles: %s", clase, e.getMessage())
			);			
		}
	}
	

	public static Class<?>[] tiposPrimitivos()
	{
		ArrayList<Class<?>> primitivos = new ArrayList<Class<?>>();
		for (Class<?> unaClase : libraries)
		{
			if ( Objeto.class.isAssignableFrom(unaClase))
			{
				primitivos.add((Class<?>) unaClase);
			} 
		}
		Class<?>[] primitivosArr = new Class<?>[primitivos.size()];
		primitivosArr = primitivos.toArray(primitivosArr);
		return primitivosArr;
	}
	
	@SuppressWarnings({ "rawtypes" })
	private Constructor<?> obtenerConstructorDeLaClase () throws Exception
	{
		String claseUnsensitive = clase.toLowerCase();
		for (int index = 0; index < libraries.length; index++)
		{
			Class<?> unaClase = libraries[index];
			if (Objeto.class.isAssignableFrom(unaClase) && unaClase.getSimpleName().toLowerCase().equals(claseUnsensitive) )
			{
				for (Constructor<?> constructor : unaClase.getConstructors()) 
				{
					boolean elConstructorEsPublico = Modifier.isPublic(constructor.getModifiers());

					if(elConstructorEsPublico)
					{
						int cantidadDeParametros = constructor.getGenericParameterTypes().length;
						boolean tieneElMismoNumeroDeArgumentos = cantidadDeParametros == argumentos.length;
						Class<?>[] firmaDelConstructor = constructor.getParameterTypes();
						if (tieneElMismoNumeroDeArgumentos )
						{
							boolean sonCompatibles = true;
							for(int i = 0; sonCompatibles && i < cantidadDeParametros; i++)
							{
								Class<?> tipoDelParametro = firmaDelConstructor[i];
								boolean deboTambienTratarDeProbarSiPuedeSerUnEnum = tipoDelParametro.isEnum() && argumentos[i].getClass() == Id.class;
								if ( deboTambienTratarDeProbarSiPuedeSerUnEnum )
								{
									boolean siSonCompatiblesCambiandoAEnum = false;
									try
									{
										Class tipoDelParametroEnum = tipoDelParametro; 
										String nombreDelPosibleValorEnumerado = ((Id) argumentos[i]).getValor().toUpperCase();

										Object[] Enums = tipoDelParametroEnum.getEnumConstants();
										for( Object e : Enums)
										{
											String valorEnElEnum = e.toString().toUpperCase();
											if( valorEnElEnum.equals(nombreDelPosibleValorEnumerado))
											{
												siSonCompatiblesCambiandoAEnum = true;
												break;
											}
										}
									}
									catch(IllegalArgumentException ex)
									{
										siSonCompatiblesCambiandoAEnum = false;
									}
									sonCompatibles = siSonCompatiblesCambiandoAEnum;
								}
								else
								{
									Class<?> tipoDelArgumento = argumentos[i].calcularTipo();
									sonCompatibles = tipoDelArgumento.isAssignableFrom(tipoDelParametro);
								}
							}
							if (sonCompatibles)
							{
								if (index > 0) 
								{
									libraries[index]   = libraries[index-1]; 
									libraries[index-1] = unaClase;
								}
								return constructor;
							}
						}	
					}
				}
			}
		}
		throw new LanguageException(obtieneErrorEnElConstructorDeLaClase());			
	}

	private String obtieneErrorEnElConstructorDeLaClase() throws Exception
	{
		String mensajeDeError = "";
		ClassFinder finder = new ClassFinder(BIBLIOTECA);
		String claseUnsensitive = clase.toUpperCase();
		for (Class<?> unaClase : finder.find())
		{
			if (Objeto.class.isAssignableFrom(unaClase) && unaClase.getSimpleName().toUpperCase().equals(claseUnsensitive) )
			{
				for (Constructor<?> constructor : unaClase.getConstructors()) 
				{
					boolean elConstructorEsPublico = Modifier.isPublic(constructor.getModifiers());

					if(elConstructorEsPublico)
					{
						boolean tieneElMismoNumeroDeArgumentos = constructor.getGenericParameterTypes().length == argumentos.length;
						Class<?>[] firmaDelConstructor = constructor.getParameterTypes();
						if (tieneElMismoNumeroDeArgumentos )
						{
							boolean sonCompatibles = true;
							for(int i = 0; sonCompatibles && i < constructor.getGenericParameterTypes().length; i++)
							{
								Class<?> tipoDelParametro = firmaDelConstructor[i];
								boolean deboTambienTratarDeProbarSiPuedeSerUnEnum = tipoDelParametro.isEnum() && argumentos[i].getClass() == Id.class;
								if ( deboTambienTratarDeProbarSiPuedeSerUnEnum )
								{
									boolean siSonCompatiblesCambiandoAEnum = false;
									try
									{
										Class<?> tipoDelParametroEnum = tipoDelParametro; 
										String nombreDelPosibleValorEnumerado = ((Id) argumentos[i]).getValor().toUpperCase();

										Object[] Enums = tipoDelParametroEnum.getEnumConstants();
										for( Object e : Enums)
										{
											String valorEnElEnum = e.toString().toUpperCase();
											if( valorEnElEnum.equals(nombreDelPosibleValorEnumerado))
											{
												siSonCompatiblesCambiandoAEnum = true;
												break;
											}
										}
									}
									catch(IllegalArgumentException ex)
									{
										siSonCompatiblesCambiandoAEnum = false;
									}
									sonCompatibles = siSonCompatiblesCambiandoAEnum;
								}
								else
								{
									Class<?> tipoDelArgumento = argumentos[i].calcularTipo();
									sonCompatibles = tipoDelArgumento.isAssignableFrom(tipoDelParametro);
									
									if( !sonCompatibles )
									{
										mensajeDeError = String.format("Esta trantando de hacer el llamado al Contructor de '%s' con un valor de tipo '%s' en el parametro #%s, donde el esperado debe ser un valor de tipo '%s', por favor corrijalo.", clase, tipoDelArgumento.getSimpleName(), i+1, tipoDelParametro.getSimpleName());
									}
								}
							}
						}	
					}
				}
			}
		}
		
		if(mensajeDeError.isEmpty())
		{
			mensajeDeError = String.format("Esta trantando de hacer el llamado al Contructor de '%s' pero en la biblioteca '%s' no se reconoce ninguna clase con ese nombre, por favor verifique si existe.", clase, BIBLIOTECA);
		}
		return mensajeDeError;
	}

	private final static class ClassFinder
	{
		private String scannedPackage;
		private static char DOT = '.';
		private static char SLASH = '/';
		private static String CLASS_SUFFIX = ".class";
		private static String BAD_PACKAGE_ERROR = "Unable to get resources from path %s. Are you sure the given %s package exists?";
	    
		ClassFinder (final String scannedPackage)
		{
			this.scannedPackage = scannedPackage;
		}
		
		private final Class<?>[] find() 
		{
			final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			final String scannedPath = scannedPackage.replace(DOT, SLASH);
			final Enumeration<URL> resources;
			try 
			{
				resources = classLoader.getResources(scannedPath);
			} 
			catch (IOException e) 
			{
				throw new IllegalArgumentException(String.format(BAD_PACKAGE_ERROR, scannedPath, scannedPackage), e);
			}
			List<Class<?>> classes = new LinkedList<Class<?>>();
			while (resources.hasMoreElements()) 
			{
				final File file = new File(resources.nextElement().getFile());
				classes.addAll(find(file, new String()));
			}
			Class<?>[] resultado = classes.toArray(new Class<?>[classes.size()]);
			return resultado;
		}

	    private final  List<Class<?>> find(final File file, final String scannedPackage)
	    {
	    	List<Class<?>> classes = new LinkedList<Class<?>>();
	        final String resource = scannedPackage + DOT + file.getName();
	        if (file.isDirectory()) 
	        {
	            for (File nestedFile : file.listFiles()) 
	            {
	                String nombreArchivo = nestedFile.getName();
	            	if (nombreArchivo.endsWith(CLASS_SUFFIX)) 
	            	{
	    	            int beginIndex = 0;
	    	            int endIndex = nombreArchivo.indexOf(CLASS_SUFFIX);
	    	            String className = nombreArchivo.substring(beginIndex, endIndex);
	    	            try 
	    	            {
	    	                classes.add(Class.forName(BIBLIOTECA + DOT + className));
	    	            }
	    	            catch (ClassNotFoundException ignore) 
	    	            {
	    	            	throw new LanguageException(ignore.getMessage());
	    	            }
	    	        }
	            }
	        } 
	        else if (resource.endsWith(CLASS_SUFFIX)) 
	        {
	            int beginIndex = 1;
	            int endIndex = resource.length() - CLASS_SUFFIX.length();
	            String className = resource.substring(beginIndex, endIndex);
	            try 
	            {
	                classes.add(Class.forName(className));
	            } 
	            catch (ClassNotFoundException ignore) 
	            {
	            	throw new LanguageException(ignore.getMessage());
	            }
	        }
	        return classes;
	    }

	}
	
	public String clase() 
	{
		return clase;
	}

	@Override
	void write(StringBuilder resultado) 
	{
		resultado.append(clase);
		resultado.append('(');
		for(int i = 0; i< argumentos.length; i++)
		{
			if  (i > 0) resultado.append(", ");
			argumentos[i].write(resultado);
		}
		resultado.append(')');
	}
}
