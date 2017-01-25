package com.ncubo.niveles;

import com.ncubo.chatbot.partesDeLaConversacion.Frase;
import com.ncubo.chatbot.partesDeLaConversacion.Respuesta;
import com.ncubo.chatbot.watson.ConversacionConWatson;
import com.ncubo.chatbot.watson.WorkSpace;

public class NivelSuperior extends Nivel{

	public NivelSuperior(WorkSpace miWorkSpace){
		super(miWorkSpace);
		cargarWorkSpace();
	}
	
	@Override
	protected void cargarWorkSpace() {
		miConversacionConElAgenteCognitivo = new ConversacionConWatson(miWorkSpace.getUsuarioIBM(), miWorkSpace.getContrasenaIBM(), miWorkSpace.getIdIBM());
		String contexto = miConversacionConElAgenteCognitivo.enviarMSG("", null).getContext().toString();
		miConversacionConElAgenteCognitivo.setElContextoConWatson(contexto);
		
		this.setName(miWorkSpace.getNombre());
	}

	@Override
	public Respuesta hablarConWatson(Frase frase, String texto) {
		return this.hablarConElAgenteCognitivo(frase, texto);
	}

	@Override
	public void actualizarContexto(String contexto){
		miConversacionConElAgenteCognitivo.setElContextoConWatson(contexto);
	}

	@Override
	public String obtenerElContexto() {
		return miConversacionConElAgenteCognitivo.getElContextoConWatson();
	}

	@Override
	public void reiniciarContexto() {
		cargarWorkSpace();
	}
}
