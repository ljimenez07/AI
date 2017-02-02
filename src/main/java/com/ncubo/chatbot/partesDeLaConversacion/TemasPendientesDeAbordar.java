package com.ncubo.chatbot.partesDeLaConversacion;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

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
		TemaPendiente miTema = buscarUnTemaPendiente(tema.getTemaActual());
		boolean elTemaNoHaSidoAgregado = miTema != null;
		if ( ! existeElTema(tema) && elTemaNoHaSidoAgregado){
			temasPendientes.push(tema); // Agregar al top de la pila
		}
		else{
			if(! elTemaNoHaSidoAgregado)
				temasPendientes.remove(miTema); // Eliminar el tema y moverlo al top
			else
				temasPendientes.remove(tema); 
			temasPendientes.push(tema);
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
	
	public void borrarUnTemaPendiente(TemaPendiente tema){
		for(Iterator<TemaPendiente> itr = temasPendientes.iterator(); itr.hasNext();)  {
			TemaPendiente miTema = (TemaPendiente) itr.next();
			if(miTema.getTemaActual().equals(tema))
				temasPendientes.remove(tema);
		}
	}
	
	public TemaPendiente buscarUnTemaPendiente(Tema tema){
		for(Iterator<TemaPendiente> itr = temasPendientes.iterator(); itr.hasNext();)  {
			TemaPendiente miTema = (TemaPendiente) itr.next();
			if(miTema.getTemaActual().equals(tema))
				return miTema;
		}
		return null;
	}
}
