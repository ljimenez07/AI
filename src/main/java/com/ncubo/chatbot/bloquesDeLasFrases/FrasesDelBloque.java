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
	
}
