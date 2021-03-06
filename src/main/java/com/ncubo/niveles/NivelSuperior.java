package com.ncubo.niveles;

import com.ncubo.chatbot.partesDeLaConversacion.Frase;
import com.ncubo.chatbot.partesDeLaConversacion.Respuesta;
import com.ncubo.chatbot.partesDeLaConversacion.Tema;
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
		String contexto = "";
		try{
			contexto = miConversacionConElAgenteCognitivo.enviarMSG("", null).getContext().toString();
		}catch (Exception e){
			try{
				try {
					Thread.sleep(500);
				} catch (InterruptedException exception) {}
				contexto = miConversacionConElAgenteCognitivo.enviarMSG("", null).getContext().toString();
			}catch (Exception e1){
				try{
					try {
						Thread.sleep(500);
					} catch (InterruptedException exception1) {}
					contexto = miConversacionConElAgenteCognitivo.enviarMSG("", null).getContext().toString();
				}catch (Exception e2){}
			}
		}
		miConversacionConElAgenteCognitivo.setElContextoConWatson(contexto);
		
		this.setName(miWorkSpace.getNombre());
	}

	@Override
	public Respuesta hablarConWatson(Frase frase, Tema tema, String texto) {
		return this.hablarConElAgenteCognitivo(frase, tema, texto);
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
