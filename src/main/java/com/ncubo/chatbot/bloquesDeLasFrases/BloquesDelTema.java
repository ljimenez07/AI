package com.ncubo.chatbot.bloquesDeLasFrases;

import java.util.ArrayList;
import java.util.Collections;

import com.ncubo.chatbot.partesDeLaConversacion.Tema;

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
	
	public void borrarMisBloques(){
		misBloques.clear();
	}
	
	public boolean agregarBloque(FrasesDelBloque bloque){
		if( ! existeElBloque(bloque)){
			misBloques.add(bloque);
			return true;
		}
		return false;
	}
	
	public FrasesDelBloque buscarUnBloque(String idDelBloque){
		for(FrasesDelBloque bloque: misBloques){
			if(bloque.getIdDelBloque().equals(idDelBloque))
				return bloque;
		}
		return null;
	}
	
	public boolean elTemaTieneBloques(){
		return ! misBloques.isEmpty();
	}
	
	public FrasesDelBloque buscarSiguienteBloqueADecir(BloquesDelTema bloquesYaConcluidos, FrasesDelBloque bloqueActual){
		Collections.shuffle(misBloques); // Desordenar el array
		for(FrasesDelBloque bloque: misBloques){
			if( bloqueActual != null){
				if( ! bloque.getIdDelBloque().equals(bloqueActual.getIdDelBloque())){
					if(bloque.tieneDependencias()){
						if(bloque.todasLasDependenciasFueronConcluidas(bloquesYaConcluidos))
							return bloque;
					}else{
						FrasesDelBloque miBloque = bloquesYaConcluidos.buscarUnBloque(bloque.getIdDelBloque());
						boolean elBloqueYaFueDicho = miBloque != null;
						if(! elBloqueYaFueDicho)
							return bloque;
					}
				}
			}else{
				if(bloque.tieneDependencias()){
					if(bloque.todasLasDependenciasFueronConcluidas(bloquesYaConcluidos))
						return bloque;
				}else{
					FrasesDelBloque miBloque = bloquesYaConcluidos.buscarUnBloque(bloque.getIdDelBloque());
					boolean elBloqueYaFueDicho = miBloque != null;
					if(! elBloqueYaFueDicho)
						return bloque;
				}
			}
			
		}
		return null;
	}
	
}
