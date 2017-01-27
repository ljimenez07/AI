package com.ncubo.chatbot.partesDeLaConversacion;

import java.util.ArrayDeque;
import java.util.Deque;

public class TemasPendientesDeAbordar {

	private Deque<TemaPendiente> temasPendientes;
	
	public TemasPendientesDeAbordar(){
		temasPendientes = new ArrayDeque<TemaPendiente>();
	}
	
	private boolean existeElTema(TemaPendiente tema){
		return temasPendientes.contains(tema);
	}
	
	public boolean hayTemasPendientes(){
		return ! temasPendientes.isEmpty();
	}
	
	public void agregarUnTema(TemaPendiente tema){
		if ( ! existeElTema(tema))
			temasPendientes.push(tema); // Agregar al top de la pila
		else{
			temasPendientes.remove(tema); // Eliminar el tema y moverlo al top
			agregarUnTema(tema);
		}
	}
	
	public TemaPendiente extraerElSiquienteTema(){
		TemaPendiente siguienteTema = null;
		if (hayTemasPendientes())
			siguienteTema = temasPendientes.poll(); // Extraer el ultimo que se agrego
		return siguienteTema;
	}
	
	public void borrarLosTemasPendientes(){
		temasPendientes.clear();
	}
}
