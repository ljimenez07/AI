package com.ncubo.chatbot.watson;

import java.util.Enumeration;
import java.util.Hashtable;
import com.ncubo.chatbot.parser.Operador;
import com.ncubo.chatbot.parser.Operador.TipoDeOperador;

public class Entidad //extends WatsonUnderstand
{
	private String nombre; // Example: appliance
	//private ArrayList<String> valores; // Example: radio, lights
	private Hashtable<String, Operador> valores = new Hashtable<String, Operador>();
	
	public Entidad(String nombre, Hashtable<String, Operador> valores){
		System.out.println("Agregando entidad: "+nombre);
		this.nombre = nombre;
		this.valores = valores;
	}
	
	public Entidad(String nombre){
		System.out.println("Agregando entidad: "+nombre);
		this.nombre = nombre;
	}
	
	/*public static Entidad newInstance(String nombre, String... valores)
	{
		if (universoDeEntidades.containsKey(nombre))
		{
			return universoDeEntidades.get(nombre);
		}
		Entidad entidad = new Entidad(nombre, valores);
		universoDeEntidades.put(nombre, entidad);
		
		return entidad;
	}*/
	
	public void agregarValorConOperador(String miValor, Operador miOperador){
		if( ! existeElValor(miValor)){
			System.out.println("Valor de la entidad a agregar: "+miValor);
			valores.put(miValor, miOperador);
		} 
	}
	
	public void agregarValor(String miValor){
		if( ! existeElValor(miValor)){
			System.out.println("Valor de la entidad a agregar: "+miValor);
			valores.put(miValor, null);
		} 
	}
	
	public void agregarValores(Hashtable<String, Operador> misValores){
		Enumeration<String> valores = misValores.keys();
		while(valores.hasMoreElements()){
			String valor = (String) valores.nextElement();
			agregarValorConOperador(valor, misValores.get(valor));
		}
	}
	
	public Hashtable<String, Operador> obtenerMisValores(){
		return valores;
	}
	
	public boolean existeElValor(String miValor){
		return valores.containsKey(miValor);
	}
	
	public String getNombre(){
		return nombre;
	}
	
	public boolean buscarSiTodosLosValoresExisten(Hashtable<String, Operador> misValores){
		boolean resultado = true;
		Enumeration<String> keys = valores.keys();
		System.out.println("Mis valores: "+valoresAStrings());
		while (keys.hasMoreElements()){
			String key = keys.nextElement();
			if( ! misValores.containsKey(key) && valores.get(key).getTipoDeOperador().equals(TipoDeOperador.AND)){
				resultado = false;
				break;
			}
		}
		return resultado;
	}
	
	public String valoresAStrings(){
		String resultado = "[ ";
		Enumeration<String> keys = valores.keys();
		while (keys.hasMoreElements()){
			resultado += keys.nextElement()+" ";
		}
		return resultado+"]";
	}
}

