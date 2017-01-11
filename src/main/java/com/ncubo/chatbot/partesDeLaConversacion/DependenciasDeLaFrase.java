package com.ncubo.chatbot.partesDeLaConversacion;

import java.util.ArrayList;

public class DependenciasDeLaFrase {

	private final ArrayList<String> misDependencias;
	
	public DependenciasDeLaFrase(){
		misDependencias = new ArrayList<>();
	}
	
	public void agregarDependencia(String nombreDeLaFrase){
		misDependencias.add(nombreDeLaFrase);
	}
	
	public ArrayList<String> obtenerMisDependencias(){
		return misDependencias;
	}
	
}
