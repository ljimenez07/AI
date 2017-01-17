package com.ncubo.conectores;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.configuracion.Constantes.TiposDeVariables;
import com.ncubo.evaluador.interprete.libraries.LanguageException;
import com.ncubo.evaluador.libraries.Objeto;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;

public class Conectores {

	private final static String BIBLIOTECA = Constantes.PATH_VARIABLES;
	private final static ClassFinder finder = new ClassFinder(BIBLIOTECA);
	private final static Class<?>[] libraries = finder.find();
	
	public Conectores(){}
	
	public boolean existeLaVariable(String valorDeLaVariable, TiposDeVariables tipoDeLaVariable){
		boolean resultado = false;
		String nombreDeLaVariable = Constantes.NOMBRE_VARIABLE;
		String tipo = Constantes.TIPO_VARIABLE;
		for (Class<?> unaClase : libraries){
			String nombreDeLaClase = unaClase.getSimpleName();
			System.out.println("Nombre de la clase: "+nombreDeLaClase);
			if( ! nombreDeLaClase.contains(Constantes.CLASE_PARAMETROS)){
				Field variableValor = obtenerVariable(nombreDeLaVariable, unaClase);
				Field variableTipo = obtenerVariable(tipo, unaClase);
				if(variableValor != null && variableTipo != null){
					if(obtenerValorDeUnaVariable(unaClase, variableValor).equals(valorDeLaVariable) && obtenerValorDeUnaVariable(unaClase, variableTipo).equals(tipoDeLaVariable))
						return true;
				}
			}
		}
		return resultado;
	}
	
	public String obtenerElNombreDeLaClase(String valorDeLaVariable, TiposDeVariables tipoDeLaVariable){
		String nombreDeLaVariable = Constantes.NOMBRE_VARIABLE;
		String tipo = Constantes.TIPO_VARIABLE;
		String resultado = "";
		for (Class<?> unaClase : libraries){
			String nombreDeLaClase = unaClase.getSimpleName();
			if( ! nombreDeLaClase.contains(Constantes.CLASE_PARAMETROS)){
				Field variableValor = obtenerVariable(nombreDeLaVariable, unaClase);
				Field variableTipo = obtenerVariable(tipo, unaClase);
				if(variableValor != null && variableTipo != null){
					if(obtenerValorDeUnaVariable(unaClase, variableValor).equals(valorDeLaVariable) && obtenerValorDeUnaVariable(unaClase, variableTipo).equals(tipoDeLaVariable)){
						resultado = nombreDeLaClase;
						break;
					}
						
				}
			}
		}
		return resultado;
	}
	
	private Field obtenerVariable(String nombreDeLaVariable, Class<?> objetoDeClassConInfoDeMiClase){
		try {
			return objetoDeClassConInfoDeMiClase.getDeclaredField(nombreDeLaVariable);
		} catch (NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Objeto ejecutarElMetodoEvaluar(String valorDeLaVariable){
		Objeto resultado = null;
		String metodo = "evaluar";
		String nombreDeLaVariable = "nombreDeLaVariable";
		for (Class<?> unaClase : libraries){
			String nombreDeLaClase = unaClase.getSimpleName();
			System.out.println("Nombre de la clase: "+nombreDeLaClase);
			Field variableValor = obtenerVariable(nombreDeLaVariable, unaClase);;
			if(variableValor != null){
				if(obtenerValorDeUnaVariable(unaClase, variableValor).equals(valorDeLaVariable)){
					Method[] todosLosMetodosDeclarados = unaClase.getDeclaredMethods();
					for(int index = 0; index < todosLosMetodosDeclarados.length; index++){
						System.out.println(todosLosMetodosDeclarados[index].getName()+" - "+todosLosMetodosDeclarados[index].getReturnType());
						if(todosLosMetodosDeclarados[index].getName().equals(metodo)){
							resultado = (Objeto) invocarUnMetodo(todosLosMetodosDeclarados[index], crearUnaInstancia(unaClase));
						}
					}
				}
			}
		}
		return resultado;
	}
	
	private Object obtenerValorDeUnaVariable(Class<?> unaClase, Field variable){
		String nombreVariable = variable.getName();

		Object valorVariable = null;
		try {
			variable.setAccessible(true);
			valorVariable = variable.get(crearUnaInstancia(unaClase));
			return valorVariable;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		System.out.println("\nValor de la variable " + nombreVariable + " es: " + valorVariable);
		return null;
	}
	
	private VariableParaChat crearUnaInstancia(Class<?> objetoDeClassConInfoDeMiClase){
		VariableParaChat nuevoObjetoDeMiClase = null;
		Constructor constructorSinParametros;
		try {
			constructorSinParametros = objetoDeClassConInfoDeMiClase.getConstructor();
			nuevoObjetoDeMiClase = (VariableParaChat) constructorSinParametros.newInstance();
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return nuevoObjetoDeMiClase;
	}
	
	private Object invocarUnMetodo(Method metodo, VariableParaChat objetoDeMiClase){
		System.out.println("Nombre del MeTODO a invocar: " + metodo.getName());
		try {
			if (Modifier.isPrivate(metodo.getModifiers())) {
				metodo.setAccessible(true);
			}
			Object valorRetornoMetodoInvocado = metodo.invoke(objetoDeMiClase);
			System.out.println("  Valor del método invocado: " + valorRetornoMetodoInvocado);
			return valorRetornoMetodoInvocado;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			//e.printStackTrace();
			return null;
		}
	}
	
	private final static class ClassFinder{
		
		private String scannedPackage;
		private static char DOT = '.';
		private static char SLASH = '/';
		private static String CLASS_SUFFIX = ".class";
		private static String BAD_PACKAGE_ERROR = "Unable to get resources from path %s. Are you sure the given %s package exists?";
	    
		ClassFinder (final String scannedPackage){
			this.scannedPackage = scannedPackage;
		}
		
		private final Class<?>[] find(){
			final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			final String scannedPath = scannedPackage.replace(DOT, SLASH);
			final Enumeration<URL> resources;
			try {
				resources = classLoader.getResources(scannedPath);
			} 
			catch (IOException e) {
				throw new IllegalArgumentException(String.format(BAD_PACKAGE_ERROR, scannedPath, scannedPackage), e);
			}
			List<Class<?>> classes = new LinkedList<Class<?>>();
			while (resources.hasMoreElements()) {
				final File file = new File(resources.nextElement().getFile());
				classes.addAll(find(file, new String()));
			}
			Class<?>[] resultado = classes.toArray(new Class<?>[classes.size()]);
			return resultado;
		}

	    private final  List<Class<?>> find(final File file, final String scannedPackage){
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

	public static void main(String argv[]){
		Conectores conectores = new Conectores();
		//conectores.obtenerTodasLasVariables();
		//conectores.obtenerFunciones();
		//conectores.obtenerConstructores();
		//conectores.obtenerModificadoresDeLasVariables();
		//conectores.obtenerTodosLosValoresDeLasVariables();
		System.out.println(conectores.existeLaVariable("saldoAgua", TiposDeVariables.NEGOCIO));
		String[] parametros = new String[1];
		parametros[0] = "1234";
		System.out.println("El resultado es: "+conectores.ejecutarElMetodoEvaluar("saldoAgua"));
	}
}
