package com.ncubo.chatbot.partesDeLaConversacion;

import com.ncubo.niveles.Topico;

public class TemaPendiente {

	private final Tema temaActual;
	private final Frase fraseActual;
	private final String contextoCognitivo;
	private final Topico miTopico;

	public TemaPendiente(Tema tema, Frase frase, String contexto, Topico topico){
		temaActual = tema;
		fraseActual = frase;
		contextoCognitivo = contexto;
		miTopico = topico;
	}
	
	public TemaPendiente(Tema tema, Frase frase, Topico topico){
		temaActual = tema;
		fraseActual = frase;
		contextoCognitivo = topico.obtenerMiUltimoContexto();
		miTopico = topico;
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
	
	public Topico getMiTopico() {
		return miTopico;
	}
}
