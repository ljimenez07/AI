package com.ncubo.chatbot.bloquesDeLasFrases;

import com.ncubo.chatbot.partesDeLaConversacion.Frase;
import com.ncubo.chatbot.partesDeLaConversacion.Tema;

public class BloquePendiente {

	private final Tema temaActual;
	private final FrasesDelBloque bloqueActual;
	private final Frase fraseActual;
	private final String contextoCognitivo;
	
	public BloquePendiente(Tema tema, FrasesDelBloque bloque, Frase frase, String contexto){
		this.temaActual = tema;
		this.bloqueActual = bloque;
		this.fraseActual = frase;
		this.contextoCognitivo = contexto;
	}
	
	public Tema getTemaActual() {
		return temaActual;
	}

	public FrasesDelBloque getBloqueActual() {
		return bloqueActual;
	}

	public Frase getFraseActual() {
		return fraseActual;
	}

	public String getContextoCognitivo() {
		return contextoCognitivo;
	}
}
