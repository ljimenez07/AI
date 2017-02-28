package com.ncubo.niveles;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;

import com.ibm.watson.developer_cloud.conversation.v1.model.Intent;
import com.ncubo.chatbot.partesDeLaConversacion.Frase;
import com.ncubo.chatbot.partesDeLaConversacion.Respuesta;
import com.ncubo.chatbot.watson.WorkSpace;

public class Topicos {

	private Deque<Topico> misTopicos;
	
	public Topicos(){
		misTopicos = new ArrayDeque<Topico>();
	}
	
	private boolean existeElTopico(Topico topico){
		for(Topico miTopico: misTopicos){
			if(miTopico.getMiTemario().contenido().getIdContenido().equals(topico.getMiTemario().contenido().getIdContenido()))
				return true;
		}
		return false;
	}
	
	public boolean hayTopicos(){
		return ! misTopicos.isEmpty();
	}
	
	public void agregarUnTopicoEnElTop(Topico topico){
		if ( ! existeElTopico(topico)){
			misTopicos.push(topico); // Agregar al top de la pila
		}
		else{
			misTopicos.remove(topico); // Eliminar el tema y moverlo al top
			misTopicos.push(topico);
		}
	}
	
	public void agregarUnTopicoAlInicio(Topico topico){
		if ( ! existeElTopico(topico)){
			misTopicos.addFirst(topico); // Agregar al top de la pila
		}
		else{
			misTopicos.remove(topico); // Eliminar el tema y moverlo al top
			misTopicos.addFirst(topico);
		}
	}
	
	public void agregarUnTopicoAlFinal(Topico topico){
		if ( ! existeElTopico(topico)){
			misTopicos.addLast(topico); // Agregar al top de la pila
		}
		else{
			misTopicos.remove(topico); // Eliminar el tema y moverlo al top
			misTopicos.addLast(topico);
		}
	}
	
	public Topico extraerElSiquienteTopico(){
		Topico siguienteTopico = null;
		if (hayTopicos())
			siguienteTopico = misTopicos.poll(); // Extraer el ultimo que se agrego (lo borra)
		return siguienteTopico;
	}
	
	public Topico extraerElUltimoTopicoAgregado(boolean hayQueBorrar){
		Topico siguienteTopico = null;
		if (hayTopicos()){
			siguienteTopico = misTopicos.getFirst(); // Extraer el ultimo que se agrego
			if(hayQueBorrar)
				misTopicos.remove(siguienteTopico);
		}
		
		return siguienteTopico;
	}
	
	public Topico extraerElPrimerTopicoAgregado(boolean hayQueBorrar){
		Topico siguienteTopico = null;
		if (hayTopicos())
			siguienteTopico = misTopicos.getLast(); // Extraer el primer que se agrego
			if(hayQueBorrar)
				misTopicos.remove(siguienteTopico);
		return siguienteTopico;
	}
	
	public Topico obtenerElTopicoPorDefecto(){
		int cantidadTeTopicos = misTopicos.size();
		
		for(int index = 0; index < cantidadTeTopicos; index ++){
			Topico topico = extraerElSiquienteTopico();
			if(topico.getMiTemario().contenido().getNombreDelContenido().equals("Temario General"))
				return topico;
			else{
				agregarUnTopicoAlFinal(topico);
			}
		}
		
		return null;
	}
	
	public Topico buscarElTopicoDeMayorConfienza(Frase frase, String textoDelUsuario){ // TODO Se hace mas complicado cuando quiero realizar mas de una intencion que estan en diferentes workspaces (quiero pagar el agua y hacer una declaracion)
		int cantidadTeTopicos = misTopicos.size();
		Topico resultado = null;
		Double laConfianzaMasAlta = 0.0;
		
		for(int index = 0; index < cantidadTeTopicos; index ++){
			Topico topico = extraerElSiquienteTopico();
			Respuesta respuesta = null;
			List<Intent> intenciones = null;
			
			try{
				respuesta = topico.hablarConWatsonEnElNivelSuperior(frase, textoDelUsuario);
				intenciones = respuesta.messageResponse().getIntents();
			}catch(Exception e){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {}
				try{
					respuesta = topico.hablarConWatsonEnElNivelSuperior(frase, textoDelUsuario);
					intenciones = respuesta.messageResponse().getIntents();
				}catch(Exception e2){
					try{
						respuesta = topico.hablarConWatsonEnElNivelSuperior(frase, textoDelUsuario);
						intenciones = respuesta.messageResponse().getIntents();
					}catch(Exception e3){}
				}
			}
			
			if(intenciones != null){
				Collections.sort(intenciones, new Comparator<Intent>() {

			        public int compare(Intent laPrimeraIntencion, Intent laSegundaIntencion) {
			            return laSegundaIntencion.getConfidence().compareTo(laPrimeraIntencion.getConfidence());
			        }
			    });
				
				Intent intencionMasAlta = respuesta.messageResponse().getIntents().get(0);
				Double confianza = intencionMasAlta.getConfidence();
				WorkSpace workspase = extraerUnWorkspaceConLaIntencion(intencionMasAlta.getIntent(), topico);
				if(confianza >= 0.85 && workspase != null){
					return topico;
				}else{
					if(confianza > laConfianzaMasAlta && workspase != null){
						laConfianzaMasAlta = confianza;
						resultado = topico;
					}
					agregarUnTopicoAlFinal(topico);
				}
			}
			
		}
		
		if(laConfianzaMasAlta >= 0.55 ){
			return resultado;
		}
		
		return null;
	}
	
	private WorkSpace extraerUnWorkspaceConLaIntencion(String nombreDeLaIntencion, Topico miTopico){
		for(WorkSpace workspace: miTopico.getMiTemario().contenido().getMiWorkSpaces()){
			if(workspace.tieneLaIntencion(nombreDeLaIntencion)){
				return workspace;
			}
		}
		return null;
	}
	
}
