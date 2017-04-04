package com.ncubo.chatbot.partesDeLaConversacion;

import com.ncubo.chatbot.bloquesDeLasFrases.BloquePendiente;
import com.ncubo.chatbot.bloquesDeLasFrases.FrasesDelBloque;
import com.ncubo.niveles.Topico;

public class TemaPendiente {

	private final Tema temaActual;
	private final Frase fraseActual;
	private final String contextoCognitivo;
	private final Topico miTopico;
	private final FrasesDelBloque fraseDelBloqueActual;
	private final BloquePendiente bloqueActual;

	public TemaPendiente(Tema tema, Frase frase, String contexto, Topico topico, FrasesDelBloque fraseDelBloqueActual, BloquePendiente bloqueActual){
		this.temaActual = tema;
		this.fraseActual = frase;
		this.contextoCognitivo = contexto;
		this.miTopico = topico;
		this.fraseDelBloqueActual = fraseDelBloqueActual;
		this.bloqueActual = bloqueActual;
	}
	
	public TemaPendiente(Tema tema, Frase frase, Topico topico, FrasesDelBloque fraseDelBloqueActual, BloquePendiente bloqueActual){
		this.temaActual = tema;
		this.fraseActual = frase;
		this.contextoCognitivo = topico.obtenerMiUltimoContexto();
		this.miTopico = topico;
		this.fraseDelBloqueActual = fraseDelBloqueActual;
		this.bloqueActual = bloqueActual;
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
	
	public BloquePendiente getBloqueActual(){
		return bloqueActual;
	}
	
	public FrasesDelBloque getFraseDelBloqueActual(){
		return fraseDelBloqueActual;
	}
}
