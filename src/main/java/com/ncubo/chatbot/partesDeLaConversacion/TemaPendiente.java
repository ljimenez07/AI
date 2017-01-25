package com.ncubo.chatbot.partesDeLaConversacion;

import com.ncubo.chatbot.watson.ConversacionConWatson;

public class TemaPendiente {

	private final Tema temaActual;
	private final ConversacionConWatson miConversacion;
	
	public TemaPendiente(Tema tema, ConversacionConWatson conversacion){
		temaActual = tema;
		miConversacion = conversacion;
	}
	
	public Tema getTemaActual() {
		return temaActual;
	}

	public ConversacionConWatson getMiConversacion() {
		return miConversacion;
	}
}
