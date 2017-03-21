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
		TemaPendiente miTema = buscarUnTemaPendiente(tema.getTemaActual());
		return miTema != null;
	}
	
	public boolean hayTemasPendientes(){
		return ! temasPendientes.isEmpty();
	}
	
	public void agregarUnTema(TemaPendiente tema){
		if ( ! existeElTema(tema)){
			temasPendientes.push(tema); // Agregar al top de la pila
		}
		else{
			TemaPendiente miTema = buscarUnTemaPendiente(tema.getTemaActual());
			temasPendientes.remove(miTema); // Eliminar el tema y moverlo al top
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
			if(miTema.getTemaActual().getIdTema().equals(tema.getTemaActual().getIdTema()))
				temasPendientes.remove(tema);
		}
	}
	
	public TemaPendiente buscarUnTemaPendiente(Tema tema){
		for(Iterator<TemaPendiente> itr = temasPendientes.iterator(); itr.hasNext();)  {
			TemaPendiente miTema = (TemaPendiente) itr.next();
			if(miTema.getTemaActual().getIdTema().equals(tema.getIdTema()))
				return miTema;
		}
		return null;
	}
}
