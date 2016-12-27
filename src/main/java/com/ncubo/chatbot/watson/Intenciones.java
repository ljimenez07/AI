package com.ncubo.chatbot.watson;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.exceptiones.ChatException;

public class Intenciones { //implements Iterable<Intencion>

	private Hashtable<String, Intencion> intenciones = new Hashtable<String, Intencion>();
	
	public Intenciones(){}
	
	public void agregar(Intencion intencion){
		intenciones.put(intencion.getNombre(), intencion);
	}
	
	public void agregarConfianzaAUnaIntencion(String nombre, double confiance){
		if (existeLaIntencion(nombre)){
			Intencion intencion = obtener(nombre);
			intencion.setConfidence(confiance);
			intenciones.put(nombre, intencion);
		}else{
			throw new ChatException(String.format("La intencion %s no existe", nombre));
		}
	}
	
	public boolean existeLaIntencion(String key){
		return intenciones.containsKey(key);
	}
	
	public Intencion obtener(String key){
		return intenciones.get(key);
	}
	
	public Hashtable<String, Intencion> obtenerTodasLasIntenciones(){
		return intenciones;
	}
	
	public Intencion obtenerLaDeMayorConfianza(double confidence){
		Intencion intencion = new Intencion("");
		Enumeration<String> misInteciones = intenciones.keys();
		while(misInteciones.hasMoreElements()){
			String key = (String) misInteciones.nextElement();
			Intencion intent = intenciones.get(key);
			if(intent.getConfidence() > confidence)
			{
				confidence = intent.getConfidence();
				intencion = intent;
			}
			
		}
		
		return intencion;
	}

	/*@Override
	public Iterator<Intencion> iterator() {
		return intenciones.iterator();
	}*/
}
