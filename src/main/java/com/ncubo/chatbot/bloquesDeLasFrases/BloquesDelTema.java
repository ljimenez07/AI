package com.ncubo.chatbot.bloquesDeLasFrases;

import java.util.ArrayList;

public class BloquesDelTema {

	private final ArrayList<FrasesDelBloque> misBloques;
	
	public BloquesDelTema(){
		misBloques = new ArrayList<>();
	}
	
	private boolean existeElBloque(FrasesDelBloque miBloque){
		for(FrasesDelBloque bloque: misBloques){
			if(bloque.getIdDelBloque().equals(miBloque.getIdDelBloque()))
				return true;
		}
		return false;
	}
	
	public void agregarBloque(FrasesDelBloque bloque){
		if( ! existeElBloque(bloque))
			misBloques.add(bloque);
	}
	
	public FrasesDelBloque buscarUnBloque(String idDelBloque){
		for(FrasesDelBloque bloque: misBloques){
			if(bloque.getIdDelBloque().equals(idDelBloque))
				return bloque;
		}
		return null;
	}
	
}
