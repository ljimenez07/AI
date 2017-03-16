package com.ncubo.logicaDeLasConversaciones;

import java.util.ArrayList;
import java.util.Iterator;

import com.ncubo.chatbot.partesDeLaConversacion.Contenido;
import com.ncubo.chatbot.partesDeLaConversacion.IntencionesNoReferenciadas;
import com.ncubo.chatbot.partesDeLaConversacion.Tema;
import com.ncubo.chatbot.watson.WorkSpace;

public class TemariosDeUnCliente extends ArrayList<TemarioDelCliente>{
	
	private ContenidoDelCliente contenidoDelCliente;
	private IntencionesNoReferenciadas intencionesNoReferenciadas;
	
	public TemariosDeUnCliente(String pathXML){
		contenidoDelCliente = new ContenidoDelCliente(pathXML);
		cargarLosTemariosDelCliente();
	}
	
	private void cargarLosTemariosDelCliente(){
		if(contenidoDelCliente != null){
			ArrayList<Contenido> contenidos = contenidoDelCliente.obtenerMisContenidos();
			for(Contenido contenido: contenidos){
				agregarTemario(new TemarioDelCliente(contenido));
			}
			intencionesNoReferenciadas = contenidoDelCliente.obtenerIntencionesNoReferenciadas();
		}
	}
	
	public IntencionesNoReferenciadas obtenerIntenciones(){
		return intencionesNoReferenciadas;
	}
	
	private boolean existeElTemario(TemarioDelCliente temario){
		return this.contains(temario);
	}
	
	private void agregarTemario(TemarioDelCliente temario){
		if(!existeElTemario(temario)){
			this.add(temario);
		}
	}
	
	public TemarioDelCliente extraerUnTemario(int posicion){
		try{
			return this.get(posicion);
		}catch(Exception e){
			return null;
		}
		
	}
	
	public ArrayList<WorkSpace> getMiWorkSpaces(){
		ArrayList<WorkSpace> resultado = new ArrayList<>();
		
		Iterator<TemarioDelCliente> temarios = this.iterator();
		while(temarios.hasNext()){
			TemarioDelCliente temario = temarios.next();
			for(WorkSpace workspace:temario.contenido().getMiWorkSpaces()){
				resultado.add(workspace);
			}
		}
		return resultado;
	}
	
	public void generarAudioEstaticosDeTodasLasFrases(String idCliente, String pathAGuardar, String ipPublica){
		Iterator<TemarioDelCliente> temarios = this.iterator();
		while(temarios.hasNext()){
			TemarioDelCliente temario = temarios.next();
			temario.generarAudioEstaticosDeTodasLasFrases(idCliente, pathAGuardar, ipPublica);
		}
	}
	
	public Tema buscarTemaPorId(String id){
		Iterator<TemarioDelCliente> temarios = this.iterator();
		while(temarios.hasNext()){
			TemarioDelCliente temario = temarios.next();
			Tema resultado = temario.buscarTemaPorId(id);
			if(resultado != null)
				return resultado;
		}
		return null;
	}
	
	public Iterator<TemarioDelCliente> obtenerLosTemariosDelCliente() { 
		return iterator();
	}
}
