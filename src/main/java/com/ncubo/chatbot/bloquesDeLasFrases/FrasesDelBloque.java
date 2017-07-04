package com.ncubo.chatbot.bloquesDeLasFrases;

import java.util.ArrayList;

import com.ncubo.chatbot.partesDeLaConversacion.Frase;
import com.ncubo.chatbot.participantes.Cliente;

public class FrasesDelBloque extends Bloque{

	private final ArrayList<Frase> misFrases;
	private final ArrayList<FrasesDelBloque> misDependencias;
	
	public FrasesDelBloque(String idDelBloque, String condicion){
		super(idDelBloque, condicion);
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
	
	public boolean puedoDecirElBloquePorqueLasDependenciasNoDichasNoSePuedenDecirPorqueNoCumplenLaCondicion(BloquesDelTema bloquesYaConcluidos, Cliente cliente){
		
		for(FrasesDelBloque bloque: misDependencias){
			FrasesDelBloque bloqueDependiente = bloquesYaConcluidos.buscarUnBloque(bloque.getIdDelBloque());
			boolean elBloqueYaFueConcluido = bloqueDependiente != null;
			if( ! elBloqueYaFueConcluido && bloque.sePuedeDecirElBloque(cliente)){
				if(bloque.todasLasDependenciasFueronConcluidas(bloquesYaConcluidos)){
					return false;
				}
			}
		}
		
		return true;
	}
	
}
