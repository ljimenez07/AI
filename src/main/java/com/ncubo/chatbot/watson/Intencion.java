package com.ncubo.chatbot.watson;

import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.parser.Operador;
import com.ncubo.chatbot.parser.Operador.TipoDeOperador;

public class Intencion //extends WatsonUnderstand
{
	private String nombre;
	private double umbral;
	private double confidence;
	private Operador miOperador;
	
	public Intencion(String nombre, double confidence){
		this.nombre = nombre;
		this.confidence = confidence;
		this.umbral = Constantes.WATSON_CONVERSATION_CONFIDENCE;
		this.miOperador = null;
		System.out.println("Agregando intencion "+nombre+" con confianza "+confidence);
	}
	
	public Intencion(String nombre){
		this.nombre = nombre;
		this.confidence = 0;
		this.umbral = Constantes.WATSON_CONVERSATION_CONFIDENCE;
		this.miOperador = null;
		System.out.println("Agregando intencion "+nombre);
	}
	
	public Intencion(String nombre, Operador operador){
		this.nombre = nombre;
		this.confidence = 0;
		this.umbral = Constantes.WATSON_CONVERSATION_CONFIDENCE;
		this.miOperador = operador;
		System.out.println("Agregando intencion "+nombre);
	}
	
	/*Intencion get (String nombre)
	{
		if (valores.contains(nombre))
		{
			return valores.get(nombre);
		}
		//Intencion intencion = new Intencion(nombre);
		valores.put(nombre, this);
		return intencion;
	}*/
	
	public TipoDeOperador obtenerMiTipoDeOperador(){
		return miOperador.getTipoDeOperador();
	}
	
	public boolean esReal(){
		return (confidence >= umbral);
	}
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public double getUmbral() {
		return umbral;
	}

	public void setUmbral(double umbral) {
		this.umbral = umbral;
	}
	
	public double getConfidence() {
		return confidence;
	}

	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}
}
