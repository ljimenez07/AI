package com.ncubo.chatbot.partesDeLaConversacion;

import java.util.ArrayList;
import java.util.List;

import com.ncubo.chatbot.exceptiones.ChatException;

public class Tema
{
	private final Frase frases[];
	private final Temas dependencias;
	private final String idDelTema;
	private final String nombre;
	private final String nombreDelWorkspaceAlQuePertenece;
	private final String intencionGeneralAlQuePertenece;
	private final boolean sePuedeRepetir;
	private final List<String> variablesDeContextoQueElTemaOcupa;
	
	public Tema (String idDelTema, String nombre, String nombreWorkspace, boolean sePuedeRepetir, String idDeLaIntencionGeneral, Frase... frases){
		this.idDelTema = idDelTema;
		this.nombre = nombre;
		this.nombreDelWorkspaceAlQuePertenece = nombreWorkspace;
		this.sePuedeRepetir = sePuedeRepetir;
		this.intencionGeneralAlQuePertenece = idDeLaIntencionGeneral;
		this.frases = frases;
		this.dependencias = new Temas();
		this.variablesDeContextoQueElTemaOcupa = new ArrayList<>();
	}
	
	public Tema (String idDelTema, String nombre,String nombreWorkspace, boolean sePuedeRepetir, String idDeLaIntencionGeneral, List<String> variables, Frase... frases){
		this.idDelTema = idDelTema;
		this.nombre = nombre;
		this.nombreDelWorkspaceAlQuePertenece = nombreWorkspace;
		this.sePuedeRepetir = sePuedeRepetir;
		this.intencionGeneralAlQuePertenece = idDeLaIntencionGeneral;
		this.frases = frases;
		this.dependencias = new Temas();
		this.variablesDeContextoQueElTemaOcupa = variables;
	}
	
	public Tema dependeDe(Tema otroTema)
	{
		dependencias.add(otroTema);
		return this;
	}
	
	public String obtenerIntencionGeneralAlQuePertenece() {
		return intencionGeneralAlQuePertenece;
	}

	public String obtenerIdTema(){
		return idDelTema;
	}
	
	public String obtenerNombre(){
		return nombre;
	}
	
	public String obtenerElNombreDelWorkspaceAlQuePertenece(){
		return nombreDelWorkspaceAlQuePertenece;
	}
	
	public Frase buscarUnaFrase(String nombreDeLaFrase){
		
		for(int index = 0; index < frases.length; index ++){
			if(frases[index].getIdFrase().equals(nombreDeLaFrase.trim()))
				return frases[index];
		}
		
		throw new ChatException(String.format("No existe una frase con id %s en el tema %s", nombreDeLaFrase, this.idDelTema));
	}
	
	public Frase buscarUnaFraseCon(CaracteristicaDeLaFrase caracteristica){
		for(int index = 0; index < frases.length; index ++){
			if(frases[index].buscarCaracteristica(caracteristica))
				return frases[index];
		}
		
		throw new ChatException(String.format("No existe una frase con tipo %s", caracteristica.toString()));
	}

	public boolean tieneDependencias(){
		return dependencias.size() > 0;
	}
	
	public Temas obtenerTodasLasDependencias(){
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
	
	public void generarAudiosEstaticos(String pathAGuardar, String ipPublica){
		for(int index = 0; index < frases.length; index ++){
			System.out.println("Generando audios a la frase: "+frases[index].getIdFrase());
			frases[index].generarAudiosEstaticos(pathAGuardar, ipPublica);
		}
	}
	
	public Frase[] obtenerMisFrases(){
		return frases;
	}
	
	public String obtenerTodasMisFrases(int id){
		String resultado = "";
		resultado += "IdTema: "+id+" - "+obtenerIdTema()+" => \n"; 
		for(int index = 0; index < frases.length; index ++){
			resultado += frases[index].obtenerLaInformacionDeLaFrase()+"\n";
		}
		return resultado;
	}
	
	public List<String> obtenerVariablesDeContextoQueElTemaOcupa(){
		return variablesDeContextoQueElTemaOcupa;
	}
	
}