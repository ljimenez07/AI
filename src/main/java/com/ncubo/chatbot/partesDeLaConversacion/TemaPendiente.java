package com.ncubo.chatbot.partesDeLaConversacion;

public class TemaPendiente {

	private final Tema temaActual;
	private final Frase fraseActual;
	private final String contextoCognitivo;
	
	public TemaPendiente(Tema tema, Frase frase, String contexto){
		temaActual = tema;
		fraseActual = frase;
		contextoCognitivo = contexto;
	}
	
	public Tema getTemaActual() {
		return temaActual;
	}

	public Frase getFraseActual() {
		return fraseActual;
	}

	public String getContextoCognitivo() {
		return contextoCognitivo;
	}
}
