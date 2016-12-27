package com.ncubo.chatbot.audiosXML;

public class ContenidoDeAudios {

	private final String idFrase;

	private final String[] textosDeLaFrase;
	private final String[] sonidosDeLosTextosDeLaFrase;
	
	private final String[] textosImpertinetesDeLaFrase;
	private final String[] sonidosDeLosTextosImpertinentesDeLaFrase;
	
	private final String[] textosDeLaFraseMeRindo;
	private final String[] sonidosDeLosTextosDeLaFraseMeRindo;
	
	public ContenidoDeAudios(String idFrase, String[] textosDeLaFrase, String[] sonidosDeLosTextosDeLaFrase, 
			String[] textosImpertinetesDeLaFrase, String[] sonidosDeLosTextosImpertinentesDeLaFrase, 
			String[] textosDeLaFraseMeRindo, String[] sonidosDeLosTextosDeLaFraseMeRindo){
		this.idFrase = idFrase;
		this.textosDeLaFrase = textosDeLaFrase;
		this.sonidosDeLosTextosDeLaFrase = sonidosDeLosTextosDeLaFrase;
		this.textosImpertinetesDeLaFrase = textosImpertinetesDeLaFrase;
		this.sonidosDeLosTextosImpertinentesDeLaFrase = sonidosDeLosTextosImpertinentesDeLaFrase;
		this.textosDeLaFraseMeRindo = textosDeLaFraseMeRindo;
		this.sonidosDeLosTextosDeLaFraseMeRindo = sonidosDeLosTextosDeLaFraseMeRindo;
	}
	
	public String getIdFrase() {
		return idFrase;
	}

	public String[] getTextosDeLaFrase() {
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
	}
}
