package com.ncubo.chatbot.bloquesDeLasFrases;

import java.util.ArrayList;
import java.util.Collections;

import com.ncubo.chatbot.participantes.Cliente;

public class BloquesDelTema {

	private final ArrayList<FrasesDelBloque> misBloques;
	
	public BloquesDelTema(){
		misBloques = new ArrayList<>();
	}
	
	private boolean existeElBloque(FrasesDelBloque miBloque){
		if(miBloque != null){
			for(FrasesDelBloque bloque: misBloques){
				if(bloque.getIdDelBloque().equals(miBloque.getIdDelBloque()))
					return true;
			}
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
	
	public FrasesDelBloque buscarSiguienteBloqueADecir(BloquesDelTema bloquesYaConcluidos, FrasesDelBloque bloqueActual, Cliente cliente){
		Collections.shuffle(misBloques); // Desordenar el array
		for(FrasesDelBloque bloque: misBloques){
			
			FrasesDelBloque miBloque = bloquesYaConcluidos.buscarUnBloque(bloque.getIdDelBloque());
			boolean elBloqueYaFueDicho = miBloque != null;
			
			if( bloqueActual != null){
				if( ! bloque.getIdDelBloque().equals(bloqueActual.getIdDelBloque())){
					if(bloque.tieneDependencias()){
						if(bloque.todasLasDependenciasFueronConcluidas(bloquesYaConcluidos)){
							if(bloque.sePuedeDecirElBloque(cliente) && ! elBloqueYaFueDicho)
								return bloque;
						}
					}else{
						if(! elBloqueYaFueDicho){
							if(bloque.sePuedeDecirElBloque(cliente))
								return bloque;
						}
					}
				}
			}else{
				if(bloque.tieneDependencias()){
					if(bloque.todasLasDependenciasFueronConcluidas(bloquesYaConcluidos)){
						if(bloque.sePuedeDecirElBloque(cliente) && ! elBloqueYaFueDicho)
							return bloque;
					}
				}else{
					
					if(! elBloqueYaFueDicho){
						if(bloque.sePuedeDecirElBloque(cliente))
							return bloque;
					}
				}
			}
			
		}
		
		// TODO Verificar si alguno de los bloques que quedan por decir tiene dependencias de bloques que no se pueden decir porque no cumplen la condicion
		for(FrasesDelBloque bloque: misBloques){
			
			FrasesDelBloque miBloque = bloquesYaConcluidos.buscarUnBloque(bloque.getIdDelBloque());
			boolean elBloqueYaFueDicho = miBloque != null;
			
			if( bloqueActual != null){
				if( ! bloque.getIdDelBloque().equals(bloqueActual.getIdDelBloque())){
					if(bloque.tieneDependencias() && ! elBloqueYaFueDicho && bloque.sePuedeDecirElBloque(cliente)){
						if( ! bloque.todasLasDependenciasFueronConcluidas(bloquesYaConcluidos)){
							if(bloque.puedoDecirElBloquePorqueLasDependenciasNoDichasNoSePuedenDecirPorqueNoCumplenLaCondicion(bloquesYaConcluidos, cliente)){
								return bloque;
							}
						}
					}
				}
			}
		}
								
		return null;
	}
	
}
