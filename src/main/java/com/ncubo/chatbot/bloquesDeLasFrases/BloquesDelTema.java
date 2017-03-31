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
	
	public boolean sePuedeDecirElBloque(FrasesDelBloque bloque, Cliente cliente){
		if(bloque.tieneCondicion()){
			String comando = "show "+bloque.getCondicion()+";";
			try {
				if(cliente.evaluarCondicion(comando).contains("true")){
					return true;
				}else{
					return false;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	public FrasesDelBloque buscarSiguienteBloqueADecir(BloquesDelTema bloquesYaConcluidos, FrasesDelBloque bloqueActual, Cliente cliente){
		Collections.shuffle(misBloques); // Desordenar el array
		for(FrasesDelBloque bloque: misBloques){
			if( bloqueActual != null){
				if( ! bloque.getIdDelBloque().equals(bloqueActual.getIdDelBloque())){
					if(bloque.tieneDependencias()){
						if(bloque.todasLasDependenciasFueronConcluidas(bloquesYaConcluidos)){
							if(sePuedeDecirElBloque(bloque, cliente))
								return bloque;
						}
					}else{
						FrasesDelBloque miBloque = bloquesYaConcluidos.buscarUnBloque(bloque.getIdDelBloque());
						boolean elBloqueYaFueDicho = miBloque != null;
						if(! elBloqueYaFueDicho){
							if(sePuedeDecirElBloque(bloque, cliente))
								return bloque;
						}
					}
				}
			}else{
				if(bloque.tieneDependencias()){
					if(bloque.todasLasDependenciasFueronConcluidas(bloquesYaConcluidos)){
						if(sePuedeDecirElBloque(bloque, cliente))
							return bloque;
					}
				}else{
					FrasesDelBloque miBloque = bloquesYaConcluidos.buscarUnBloque(bloque.getIdDelBloque());
					boolean elBloqueYaFueDicho = miBloque != null;
					if(! elBloqueYaFueDicho){
						if(sePuedeDecirElBloque(bloque, cliente))
							return bloque;
					}
				}
			}
			
		}
		return null;
	}
	
}
