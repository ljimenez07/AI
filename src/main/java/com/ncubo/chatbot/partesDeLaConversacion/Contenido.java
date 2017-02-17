package com.ncubo.chatbot.partesDeLaConversacion;

import java.util.ArrayList;
import java.util.Hashtable;
import com.ncubo.chatbot.configuracion.Constantes.ModoDeLaVariable;
import com.ncubo.chatbot.exceptiones.ChatException;
import com.ncubo.chatbot.watson.WorkSpace;

public class Contenido 
{
	private final ModoDeLaVariable modoDeTrabajo;
	private final ArrayList<Tema> misTemas;
	private final ArrayList<Frase> frases;
	//private ArrayList<Intencion> intenciones = new ArrayList<Intencion>();
	private final ArrayList<WorkSpace> miWorkSpaces;
	private final Hashtable<String, DependenciasDeLaFrase> misDependencias;
	
	public Contenido(ModoDeLaVariable modoDeTrabajo, ArrayList<Tema> misTemas, ArrayList<Frase> frases, ArrayList<WorkSpace> miWorkSpaces, Hashtable<String, DependenciasDeLaFrase> misDependencias){
		this.modoDeTrabajo = modoDeTrabajo;
		this.misTemas = misTemas;
		this.frases = frases;
		this.miWorkSpaces = miWorkSpaces;
		this.misDependencias = misDependencias;
	}
	
	public Frase frase(String nombreDeLaFrase){
		
		for(Frase frase: frases){
			if(frase.obtenerNombreDeLaFrase().equalsIgnoreCase(nombreDeLaFrase)){
				return frase;
			}
		}
		
		throw new ChatException(
			String.format("En este contenido no hay ninguna frase cuyo id sea '%s'", nombreDeLaFrase)
		);
	}
	
	public ArrayList<Tema> obtenerMisTemas(){
		return misTemas;
	}
	
	public Hashtable<String, DependenciasDeLaFrase> obtenerMisDependencias(){
		return misDependencias;
	}
	
	public ModoDeLaVariable obtenerModoDeTrabajo(){
		return modoDeTrabajo;
	}
	
	public ArrayList<WorkSpace> getMiWorkSpaces() {
		return miWorkSpaces;
	}
	
	public ArrayList<Frase> obtenerMiFrases(){
		return frases;
	}
	
	public static void main(String argv[]) {
		/*Contenido contenido = new Contenido(Constantes.PATH_ARCHIVO_DE_CONFIGURACION){
		@Override
		protected File archivoDeConfiguracion(String path) {
			// TODO Auto-generated method stub
			return new File(path);
			//return new File(Constantes.PATH_ARCHIVO_DE_CONFIGURACION);
		}};*/
		//contenido.generarAudioEstatico();
	}
}