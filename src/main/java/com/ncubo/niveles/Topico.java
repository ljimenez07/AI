package com.ncubo.niveles;

import com.ncubo.chatbot.partesDeLaConversacion.Frase;
import com.ncubo.chatbot.partesDeLaConversacion.Respuesta;
import com.ncubo.chatbot.watson.WorkSpace;
import com.ncubo.logicaDeLasConversaciones.TemarioDelCliente;

public class Topico {

	private NivelSuperior hiloDelNivelSuperior;
	private NivelDetallado hiloDelNivelDetallado;
	private final TemarioDelCliente miTemario;

	public Topico(TemarioDelCliente temario){
		this.miTemario = temario;
		crearHilosDeLosNiveles(this.miTemario.contenido().getMiWorkSpaces().get(0));
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
	
	public String obtenerMiUltimoContexto() {
		return hiloDelNivelDetallado.obtenerMiUltimoContexto();
	}
	
	public void reiniciarContexto(){
		hiloDelNivelDetallado.reiniciarContexto();
	}
	
	public TemarioDelCliente getMiTemario() {
		return miTemario;
	}
	
	/*public static void main (String[] args) throws InterruptedException {
		
		WorkSpace workSpace = new WorkSpace("37db1761-b00b-422e-a912-bca0e93d87d4", "enZ5tpEeGWUH", "99edd6c7-981b-488e-895d-0139e22c7028", "general", "DMuni", "");
		
		Topico topico = new Topico(workSpace);
		
		topico.dormirPrueba(5000);
		
		topico.hablarConWatsonEnElNivelSuperior(null, "Hola!");
		
		Respuesta respuesta = topico.hablarConWatson(null, "Hola!");
		topico.actualizarContexto(respuesta.getMiContexto());
		
		topico.dormirPrueba(3000);
		respuesta = topico.hablarConWatson(null, "Quiero pagar el agua");
		
		topico.detenerTodosLosNiveles();
	}*/
}
