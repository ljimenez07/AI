package com.ncubo.chatbot.partesDeLaConversacion;

import java.util.ArrayList;

public class DependenciasDeLaFrase {

	private final ArrayList<String> misDependencias;
	
	public DependenciasDeLaFrase(){
		misDependencias = new ArrayList<>();
	}
	
	public void agregarDependencia(String idFrase){
		misDependencias.add(idFrase);
	}
	
	public ArrayList<String> obtenerMisDependencias(){
		return misDependencias;
	}
	
}
