package com.ncubo.chatbot.audiosXML;

import java.util.ArrayList;
import com.ncubo.chatbot.partesDeLaConversacion.ComponentesDeLaFrase;

public class ContenidoDeAudios {

	private final String nombreDeLaFrase;

	private ArrayList<ComponentesDeLaFrase> misSinonimosDeLaFrase;
	
	/*private final String[] textosDeLaFrase;
	private final String[] sonidosDeLosTextosDeLaFrase;
	
	private final String[] textosImpertinetesDeLaFrase;
	private final String[] sonidosDeLosTextosImpertinentesDeLaFrase;
	
	private final String[] textosDeLaFraseMeRindo;
	private final String[] sonidosDeLosTextosDeLaFraseMeRindo;*/
	
	public ContenidoDeAudios(String nombreDeLaFrase, ArrayList<ComponentesDeLaFrase> misSinonimosDeLaFrase){
		this.nombreDeLaFrase = nombreDeLaFrase;
		this.misSinonimosDeLaFrase = misSinonimosDeLaFrase;
		/*this.textosDeLaFrase = textosDeLaFrase;
		this.sonidosDeLosTextosDeLaFrase = sonidosDeLosTextosDeLaFrase;
		this.textosImpertinetesDeLaFrase = textosImpertinetesDeLaFrase;
		this.sonidosDeLosTextosImpertinentesDeLaFrase = sonidosDeLosTextosImpertinentesDeLaFrase;
		this.textosDeLaFraseMeRindo = textosDeLaFraseMeRindo;
		this.sonidosDeLosTextosDeLaFraseMeRindo = sonidosDeLosTextosDeLaFraseMeRindo;*/
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
	
	/*public String[] getTextosDeLaFrase() {
		return textosDeLaFrase;
	}

	public String[] getSonidosDeLosTextosDeLaFrase() {
		return sonidosDeLosTextosDeLaFrase;
	}

	public String[] getTextosImpertinetesDeLaFrase() {
		return textosImpertinetesDeLaFrase;
	}

	public String[] getSonidosDeLosTextosImpertinentesDeLaFrase() {
		return sonidosDeLosTextosImpertinentesDeLaFrase;
	}
	
	public String[] getTextosDeLaFraseMeRindo() {
		return textosDeLaFraseMeRindo;
	}

	public String[] getSonidosDeLosTextosDeLaFraseMeRindo() {
		return sonidosDeLosTextosDeLaFraseMeRindo;
	}*/
}
