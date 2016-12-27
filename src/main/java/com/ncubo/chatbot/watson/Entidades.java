package com.ncubo.chatbot.watson;

import java.util.Hashtable;
import com.ncubo.chatbot.exceptiones.ChatException;
import com.ncubo.chatbot.parser.Operador;
import com.ncubo.chatbot.parser.Operador.TipoDeOperador;

public class Entidades { // implements Iterable<Entidad>

	private Hashtable<String, Entidad> entidades = new Hashtable<String, Entidad>();

	public Entidades(){}
	
	public void agregar(Entidad entidad){
		if( ! existeLaEntidad(entidad.getNombre()))
			entidades.put(entidad.getNombre(), entidad);
		else{
			Entidad miEntidad = obtener(entidad.getNombre());
			miEntidad.agregarValores(entidad.obtenerMisValores());
			entidades.put(entidad.getNombre(), miEntidad);
		}
	}
	
	public void agregarValorALaEntidad(String nombre, String valor){
		if (existeLaEntidad(nombre)){
			Entidad entidad = obtener(nombre);
			entidad.agregarValor(valor);
			entidades.put(nombre, entidad);
		}else{
			throw new ChatException(String.format("La entidad %s no existe", nombre));
		}
	}
	
	public void agregarValorALaEntidadConOperador(String nombre, String valor, TipoDeOperador operador){
		if (existeLaEntidad(nombre)){
			Entidad entidad = obtener(nombre);
			entidad.agregarValorConOperador(valor, new Operador(operador));
			entidades.put(nombre, entidad);
		}else{
			throw new ChatException(String.format("La entidad %s no existe", nombre));
		}
	}
	
	public boolean existeLaEntidad(String nombre){
		return entidades.containsKey(nombre);
	}
	
	public Entidad obtener(String key){
		return entidades.get(key);
	}
	
	public Hashtable<String, Entidad> obtenerTodasLasEntidades(){
		return entidades;
	}
	
	/*@Override
	public Iterator<Entidad> iterator() {
		return entidades.iterator();
	}*/
	
	public void setEntidades(Hashtable<String, Entidad> entidades) {
		this.entidades = entidades;
	}
	
}
