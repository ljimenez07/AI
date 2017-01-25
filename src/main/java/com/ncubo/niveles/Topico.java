package com.ncubo.niveles;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.partesDeLaConversacion.Frase;
import com.ncubo.chatbot.partesDeLaConversacion.Respuesta;
import com.ncubo.chatbot.partesDeLaConversacion.TemasPendientesDeAbordar;
import com.ncubo.chatbot.watson.WorkSpace;

public class Topico {

	private NivelSuperior hiloDelNivelSuperior;
	private NivelDetallado hiloDelNivelDetallado;
	private final TemasPendientesDeAbordar temasPendientes;
	
	public Topico(WorkSpace misWorkSpaces){
		crearHilosDeLosNiveles(misWorkSpaces);
		temasPendientes = new TemasPendientesDeAbordar();
	}
	
	private void crearHilosDeLosNiveles(WorkSpace workspace){
		
		hiloDelNivelSuperior = new NivelSuperior(workspace);
		hiloDelNivelSuperior.start();
		
		hiloDelNivelDetallado = new NivelDetallado(workspace);
		hiloDelNivelDetallado.start();
		
	}
	
	public void detenerTodosLosNiveles(){
		System.out.println("Deteniendo hilos ...");
		hiloDelNivelSuperior.stop();
		hiloDelNivelDetallado.stop();
	}
	
	public Respuesta hablarConWatsonEnElNivelSuperior(Frase frase, String texto){
		return hiloDelNivelSuperior.hablarConWatson(frase, texto);
	}
	
	public Respuesta hablarConWatson(Frase frase, String texto){
		return hiloDelNivelDetallado.hablarConWatson(frase, texto);
	}
	
	public void actualizarContexto(String contexto) {
		hiloDelNivelDetallado.actualizarContexto(contexto);
	}
	
	public String obtenerElContexto() {
		return hiloDelNivelDetallado.obtenerElContexto();
	}
	
	public void reiniciarContexto(){
		hiloDelNivelDetallado.reiniciarContexto();
	}
	
	private void dormirPrueba(int tiempo){
		try {
			Thread.sleep (tiempo);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main (String[] args) throws InterruptedException {
		
		WorkSpace workSpace = new WorkSpace("37db1761-b00b-422e-a912-bca0e93d87d4", "enZ5tpEeGWUH", "99edd6c7-981b-488e-895d-0139e22c7028", "general", "DMuni", "");
		
		Topico topico = new Topico(workSpace);
		
		topico.dormirPrueba(5000);
		
		topico.hablarConWatsonEnElNivelSuperior(null, "Hola!");
		
		Respuesta respuesta = topico.hablarConWatson(null, "Hola!");
		topico.actualizarContexto(respuesta.getMiContexto());
		
		topico.dormirPrueba(3000);
		respuesta = topico.hablarConWatson(null, "Quiero pagar el agua");
		
		topico.detenerTodosLosNiveles();
	}
}
