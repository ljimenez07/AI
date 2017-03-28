package com.ncubo.chatbot.bloquesDeLasFrases;

import java.util.ArrayList;

import com.ncubo.chatbot.partesDeLaConversacion.Frase;

public class FrasesDelBloque extends Bloque{

	private final ArrayList<Frase> misFrases;
	private final ArrayList<Bloque> misDependencias;
	
	public FrasesDelBloque(String idDelBloque){
		super(idDelBloque);
		misFrases = new ArrayList<>();
		misDependencias = new ArrayList<>();
	}
	
	public void agregarFrase(Frase frase){
		misFrases.add(frase);
	}
	
	public void agregarDependencia(FrasesDelBloque bloque){
		misDependencias.add(bloque);
	}
	
	public boolean tieneDependencias(){
		return ! misDependencias.isEmpty();
	}
	
	public Frase buscarUnaFrase(String nombreDeLaFrase){
		Frase resultado = null;
		for(int index = 0; index < misFrases.size(); index ++){
			if(misFrases.get(index).obtenerNombreDeLaFrase().equals(nombreDeLaFrase.trim())){
				resultado = misFrases.get(index);
				break;
			}
		}
		return resultado;
	}
	
	public boolean todasLasDependenciasFueronConcluidas(BloquesDelTema bloquesYaConcluidos){
		for(Bloque bloque: misDependencias){
			FrasesDelBloque bloqueDependiente = bloquesYaConcluidos.buscarUnBloque(bloque.getIdDelBloque());
			boolean elBloqueYaFueConcluido = bloqueDependiente != null;
			if( ! elBloqueYaFueConcluido){
				return false;
			}
		}
		return true;
	}
	
}
