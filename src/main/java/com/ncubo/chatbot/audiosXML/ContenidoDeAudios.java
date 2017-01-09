package com.ncubo.chatbot.audiosXML;

import java.util.ArrayList;
import com.ncubo.chatbot.partesDeLaConversacion.ComponentesDeLaFrase;

public class ContenidoDeAudios {

	private final String idFrase;

	private ArrayList<ComponentesDeLaFrase> misSinonimosDeLaFrase;
	
	/*private final String[] textosDeLaFrase;
	private final String[] sonidosDeLosTextosDeLaFrase;
	
	private final String[] textosImpertinetesDeLaFrase;
	private final String[] sonidosDeLosTextosImpertinentesDeLaFrase;
	
	private final String[] textosDeLaFraseMeRindo;
	private final String[] sonidosDeLosTextosDeLaFraseMeRindo;*/
	
	public ContenidoDeAudios(String idFrase, ArrayList<ComponentesDeLaFrase> misSinonimosDeLaFrase){
		this.idFrase = idFrase;
		this.misSinonimosDeLaFrase = misSinonimosDeLaFrase;
		/*this.textosDeLaFrase = textosDeLaFrase;
		this.sonidosDeLosTextosDeLaFrase = sonidosDeLosTextosDeLaFrase;
		this.textosImpertinetesDeLaFrase = textosImpertinetesDeLaFrase;
		this.sonidosDeLosTextosImpertinentesDeLaFrase = sonidosDeLosTextosImpertinentesDeLaFrase;
		this.textosDeLaFraseMeRindo = textosDeLaFraseMeRindo;
		this.sonidosDeLosTextosDeLaFraseMeRindo = sonidosDeLosTextosDeLaFraseMeRindo;*/
	}
	
	public String getIdFrase() {
		return idFrase;
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
