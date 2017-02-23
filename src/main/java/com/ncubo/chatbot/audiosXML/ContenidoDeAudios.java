package com.ncubo.chatbot.audiosXML;

import java.util.ArrayList;
import com.ncubo.chatbot.partesDeLaConversacion.ComponentesDeLaFrase;

public class ContenidoDeAudios {

	private final String nombreDeLaFrase;

	private ArrayList<ComponentesDeLaFrase> misSinonimosDeLaFrase;
	
	public ContenidoDeAudios(String nombreDeLaFrase, ArrayList<ComponentesDeLaFrase> misSinonimosDeLaFrase){
		this.nombreDeLaFrase = nombreDeLaFrase;
		this.misSinonimosDeLaFrase = misSinonimosDeLaFrase;
	}
	
	public String obtenerNombreDeLaFrase() {
		return nombreDeLaFrase;
	}

	private ArrayList<ComponentesDeLaFrase> buscarFrasesSinonimoPorTipo(String tipoFrase){
		ArrayList<ComponentesDeLaFrase> resultado = new ArrayList<ComponentesDeLaFrase>();
		
		for(ComponentesDeLaFrase miFrase: misSinonimosDeLaFrase){
			if(miFrase.getTipoDeFrase().equals(tipoFrase))
				resultado.add(miFrase);
		}
		
		return resultado;
	}
	
	public ArrayList<ComponentesDeLaFrase> obtenerMisSinonimosDeLaFrase(){
		return misSinonimosDeLaFrase;
	}
	
}
