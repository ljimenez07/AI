package com.ncubo.chatbot.partesDeLaConversacion;

import java.util.ArrayList;
import java.util.List;

import com.ncubo.chatbot.bloquesDeLasFrases.BloquesDelTema;
import com.ncubo.chatbot.exceptiones.ChatException;
import com.ncubo.chatbot.watson.Intencion;

public class Tema
{
	private final Frase frases[];
	private final Temas dependencias;
	private final String idDelTema;
	private final String nombre;
	private final String descripcion;
	private final String nombreDelWorkspaceAlQuePertenece;
	private final Intencion intencionGeneralAlQuePertenece;
	private final boolean sePuedeRepetir;
	private final List<String> variablesDeContextoQueElTemaOcupa;
	private final List<Intencion> intencionesDelTema;
	private final BloquesDelTema bloquesDelTema;
	
	public Tema (String idDelTema, String nombre, String descripcion, String nombreWorkspace, boolean sePuedeRepetir, Intencion idDeLaIntencionGeneral, List<Intencion> intencionesDelTema, BloquesDelTema bloquesDelTema, Frase... frases){
		this.idDelTema = idDelTema;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.nombreDelWorkspaceAlQuePertenece = nombreWorkspace;
		this.sePuedeRepetir = sePuedeRepetir;
		this.intencionGeneralAlQuePertenece = idDeLaIntencionGeneral;
		this.frases = frases;
		this.dependencias = new Temas();
		this.variablesDeContextoQueElTemaOcupa = new ArrayList<>();
		this.intencionesDelTema = intencionesDelTema;
		this.bloquesDelTema = bloquesDelTema;
	}
	
	public Tema (String idDelTema, String nombre, String descripcion, String nombreWorkspace, boolean sePuedeRepetir, Intencion idDeLaIntencionGeneral, List<String> variables, List<Intencion> intencionesDelTema, BloquesDelTema bloquesDelTema, Frase... frases){
		this.idDelTema = idDelTema;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.nombreDelWorkspaceAlQuePertenece = nombreWorkspace;
		this.sePuedeRepetir = sePuedeRepetir;
		this.intencionGeneralAlQuePertenece = idDeLaIntencionGeneral;
		this.frases = frases;
		this.dependencias = new Temas();
		this.variablesDeContextoQueElTemaOcupa = variables;
		this.intencionesDelTema = intencionesDelTema;
		this.bloquesDelTema = bloquesDelTema;
	}
	
	public Tema dependeDe(Tema otroTema){
		dependencias.add(otroTema);
		return this;
	}
	
	public String getIntencionGeneralAlQuePertenece() {
		return intencionGeneralAlQuePertenece.getNombre();
	}

	public String getIdTema(){
		return idDelTema;
	}
	
	public String getNombre(){
		return nombre;
	}
	
	public String getDescripcion(){
		return descripcion;
	}
	
	public String getElNombreDelWorkspaceAlQuePertenece(){
		return nombreDelWorkspaceAlQuePertenece;
	}
	
	public Frase buscarUnaFrase(String nombreDeLaFrase){
		Frase resultado = null;
		for(int index = 0; index < frases.length; index ++){
			if(frases[index].obtenerNombreDeLaFrase().equals(nombreDeLaFrase.trim())){
				resultado = frases[index];
				break;
			}
		}
		if(resultado != null){
			return resultado;
		}
		throw new ChatException(String.format("No existe una frase con id %s en el tema %s", nombreDeLaFrase, this.nombre));
	}

	public Frase buscarUnaFraseCon(CaracteristicaDeLaFrase caracteristica){
		Frase resultado = null;
		for(int index = 0; index < frases.length; index ++){
			if(frases[index].buscarCaracteristica(caracteristica)){
				resultado = frases[index];
				break;
			}
		}
		if(resultado != null){
			return resultado;
		}
		throw new ChatException(String.format("No existe una frase con tipo %s", caracteristica.toString()));
	}

	public boolean tieneDependencias(){
		return dependencias.size() > 0;
	}
	
	public Temas getTodasLasDependencias(){
		return dependencias;
	}
	
	public boolean buscarSiTodasLasDependenciasYaFueronTratadas(Temas temasYaTratados){
		boolean resultado = true;
		for(Tema dependencia: dependencias){
			if( ! temasYaTratados.contains(dependencia)){
				resultado = false;
				break;
			}
		}
		return resultado;
	}

	public boolean sePuedeRepetir(){
		return sePuedeRepetir;
	}
	
	public void generarAudiosEstaticos(String idCliente, String pathAGuardar, String ipPublica, String idTemario){
		for(int index = 0; index < frases.length; index ++){
			System.out.println("Generando audios a la frase: "+frases[index].obtenerNombreDeLaFrase());
			frases[index].generarAudiosEstaticos(idCliente, pathAGuardar, ipPublica, idTemario);
		}
	}
	
	public Frase[] getMisFrases(){
		return frases;
	}
	
	public String getTodasMisFrases(int id){
		String resultado = "";
		resultado += "IdTema: "+id+" - "+getIdTema()+" => \n"; 
		for(int index = 0; index < frases.length; index ++){
			resultado += frases[index].obtenerLaInformacionDeLaFrase()+"\n";
		}
		return resultado;
	}
	
	public List<String> getVariablesDeContextoQueElTemaOcupa(){
		return variablesDeContextoQueElTemaOcupa;
	}
	
}