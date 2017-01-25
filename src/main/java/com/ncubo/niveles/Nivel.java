package com.ncubo.niveles;

import com.ncubo.chatbot.partesDeLaConversacion.Frase;
import com.ncubo.chatbot.partesDeLaConversacion.Respuesta;
import com.ncubo.chatbot.watson.ConversacionConWatson;
import com.ncubo.chatbot.watson.WorkSpace;

public abstract class Nivel extends Thread{

	protected ConversacionConWatson miConversacionConElAgenteCognitivo = null;
	protected WorkSpace miWorkSpace = null;
	
	private volatile boolean corriendo = true;
    private volatile int segundosADormir = 86400;
    private final Object objetoABloquear = new Object();
    private boolean hayMesajeNuevo = false;
    private String mensajeAEnviar;
    private Respuesta respuestaDelAgenteCognitivo = null;
    
	public Nivel(WorkSpace miWorkSpace){
		this.miWorkSpace = miWorkSpace;
		mensajeAEnviar = "";
	}
	
	public void run() {
		System.out.println("Corriendo hilo: "+this.getName()+ " ...");
		while(corriendo){
			enviarTextoAlAgenteCognitivo(mensajeAEnviar);
			synchronized (objetoABloquear) {
                try{
                	objetoABloquear.wait(segundosADormir * 1000);
                } catch(InterruptedException e){
                    //Handle Exception
                }
            }
		}
	}

	private void enviarTextoAlAgenteCognitivo(String texto){
		if(hayMesajeNuevo && ! texto.isEmpty()){
			respuestaDelAgenteCognitivo.llamarAWatson(texto);
		}
	}
	
	protected Respuesta hablarConElAgenteCognitivo(Frase frase, String texto){
		
		int contador = 0;
		mensajeAEnviar = texto;
		respuestaDelAgenteCognitivo = new Respuesta(frase, miConversacionConElAgenteCognitivo, miConversacionConElAgenteCognitivo.getElContextoConWatson());
		hayMesajeNuevo = true;
		depertarNivel();
		
		while(! respuestaDelAgenteCognitivo.teminoDeProcesarLaRespuestaDelAgente()  && contador < 10){
			try {
				Thread.sleep (1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			contador ++;
		}
		
		hayMesajeNuevo = false;
		mensajeAEnviar = "";
		
		return respuestaDelAgenteCognitivo;
	}
	
	private void depertarNivel(){
		synchronized (objetoABloquear) {
			objetoABloquear.notify();
		}
	}
	
	public void detenerNivel(){
		corriendo = false;
	}
	
	protected abstract void cargarWorkSpace();
	public abstract void reiniciarContexto();
	public abstract Respuesta hablarConWatson(Frase frase, String texto);
	public abstract void actualizarContexto(String contexto);
	public abstract String obtenerElContexto();
}
