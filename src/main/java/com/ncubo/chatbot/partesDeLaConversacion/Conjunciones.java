package com.ncubo.chatbot.partesDeLaConversacion;

import java.util.ArrayList;

public class Conjunciones {

	private final static ArrayList<Conjuncion> misConjunciones = new ArrayList<Conjuncion>();
	private static Conjunciones conjunciones = null;
	
	private Conjunciones(){}
	
	public static Conjunciones getInstance(){
		if(conjunciones == null){
			conjunciones = new Conjunciones();
		}
		return conjunciones;
	}
	
	private boolean existeLaConjuncion(Conjuncion conjuncion){
		return misConjunciones.contains(conjuncion);
	}
	
	public void agregarConjuncion(Conjuncion conjuncion){
		if(! existeLaConjuncion(conjuncion))
			misConjunciones.add(conjuncion);
	}
	
	public Conjuncion obtenerUnaConjuncion(){
		int unIndiceAlAzar = (int)Math.floor(Math.random()*misConjunciones.size());
		return misConjunciones.get(unIndiceAlAzar);
	}
	
}
