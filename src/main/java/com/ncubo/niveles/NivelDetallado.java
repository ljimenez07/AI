package com.ncubo.niveles;

import com.ncubo.chatbot.partesDeLaConversacion.Frase;
import com.ncubo.chatbot.partesDeLaConversacion.Respuesta;
import com.ncubo.chatbot.watson.ConversacionConWatson;
import com.ncubo.chatbot.watson.WorkSpace;

public class NivelDetallado extends Nivel{

	public NivelDetallado(WorkSpace miWorkSpace){
		super(miWorkSpace);
		cargarWorkSpace();
		this.miUlTimoContexto = miConversacionConElAgenteCognitivo.getElContextoConWatson();
	}
	
	@Override
	protected void cargarWorkSpace() {
		miConversacionConElAgenteCognitivo = new ConversacionConWatson(miWorkSpace.getUsuarioIBM(), miWorkSpace.getContrasenaIBM(), miWorkSpace.getIdIBM());
		//String contexto = new JSONObject(miConversacionConElAgenteCognitivo.enviarMSG("", null).getContext()).toString();
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
	public Respuesta hablarConWatson(Frase frase, String texto) {
		return this.hablarConElAgenteCognitivo(frase, texto);
	}

	@Override
	public void actualizarContexto(String contexto) {
		this.miUlTimoContexto = obtenerElContexto();
		miConversacionConElAgenteCognitivo.setElContextoConWatson(contexto);
	}

	@Override
	public String obtenerElContexto() {
		return miConversacionConElAgenteCognitivo.getElContextoConWatson();
	}

	@Override
	public void reiniciarContexto() {
		this.miUlTimoContexto = obtenerElContexto();
		cargarWorkSpace();
	}

}
