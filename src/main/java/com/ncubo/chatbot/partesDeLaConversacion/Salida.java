package com.ncubo.chatbot.partesDeLaConversacion;

import java.util.ArrayList;

public class Salida {

	private String miTexto;
	private Sonido miSonido;
	private ArrayList<Vineta> misVinetas;
	private Respuesta miRespuesta;
	private Tema temaActual;
	private Frase fraseActual;
	private boolean seTerminoElChat;
	
	public Salida(){
		miTexto = "";
		miSonido = null;
		misVinetas = new ArrayList<Vineta>();
		miRespuesta = null;
		temaActual = null;
		seTerminoElChat = false;
	}
	
	public void escribir(String texto, Respuesta respuesta, Tema tema, Frase frase){
		this.miTexto = texto;
		this.miRespuesta = respuesta;
		this.temaActual = tema;
		this.fraseActual = frase;
		//System.out.println(texto);
	}
	
	public void escribir(Sonido sonido, Respuesta respuesta, Tema tema, Frase frase){
		this.miSonido = sonido;
		this.miRespuesta = respuesta;
		this.temaActual = tema;
		this.fraseActual = frase;
		//System.out.println(sonido.url());
	} 
	
	public void escribir(Vineta vineta, Respuesta respuesta, Tema tema, Frase frase){
		if(vineta != null && ! existeLaVineta(vineta))
			this.misVinetas.add(vineta);
		this.miRespuesta = respuesta;
		this.temaActual = tema;
		this.fraseActual = frase;
		//System.out.println(vineta.url());
	} 
	
	public void escribir(String texto, Sonido sonido, Respuesta respuesta, Tema tema, Frase frase){
		this.miTexto = texto;
		this.miSonido = sonido;
		this.miRespuesta = respuesta;
		this.temaActual = tema;
		this.fraseActual = frase;
	}
	
	public void escribir(ComponentesDeLaFrase miFrase, Respuesta respuesta, Tema tema, Frase frase){
		this.miTexto = miFrase.getTextoDeLaFrase();
		this.miSonido = miFrase.getAudio("audio");
		if(miFrase.getVineta() != null && ! existeLaVineta(miFrase.getVineta()))
			this.misVinetas.add(miFrase.getVineta());
		this.miRespuesta = respuesta;
		this.temaActual = tema;
		this.fraseActual = frase;
	}
	
	private boolean existeLaVineta(Vineta vineta){
		for(Vineta miVineta: misVinetas){
			if (miVineta.obtenerContenido().trim().equals(vineta.obtenerContenido().trim()))
				return true;
		}
		return false;
	}
	public String getMiTexto() {
		return miTexto;
	}

	public void setMiTexto(String texto) {
		miTexto = texto;
	}
	
	public Sonido getMiSonido() {
		if(miSonido == null){
			return new Sonido("", "");
		}
		return miSonido;
	}

	public ArrayList<Vineta> getMisVinetas() {
		return misVinetas;
	}
	
	public Respuesta obtenerLaRespuestaDeIBM(){
		return miRespuesta;
	}
	
	public Tema getTemaActual() {
		return temaActual;
	}

	public Frase getFraseActual() {
		return fraseActual;
	}
	
	public void cambiarSeTerminoElChat(boolean seTermino){
		seTerminoElChat = seTermino;
	}
	
	public boolean seTerminoElChat(){
		return seTerminoElChat;
	}
	
}
