package com.ncubo.chatbot.partesDeLaConversacion;

import java.io.Serializable;
import java.util.ArrayList;

import com.ncubo.chatbot.bloquesDeLasFrases.BloquesDelTema;
import com.ncubo.chatbot.bloquesDeLasFrases.FrasesDelBloque;

// Hilo de la converzacion actual - Se puede borrar cuando la seccion caduca
public class HiloDeLaConversacion implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2467563711251769111L;
	private Temas temasYaDichos = new Temas();
	private Temas temasYaDichosQueNoPuedoRepetir = new Temas();
	private Temas temasYaDichosQueNoPuedoRepetirParaWorkspaceEspecifico = new Temas();
	private ArrayList<Frase> loQueYaSeHaDicho = new ArrayList<Frase>();
	private ArrayList<Respuesta> respuestas = new ArrayList<Respuesta>();
	private ArrayList<Salida> misSalidas = new ArrayList<Salida>();
	private BloquesDelTema bloquesConcluidosDelTemaActual = new BloquesDelTema();
	
	public HiloDeLaConversacion(){}
	
	public boolean agregarBloqueConcluido(FrasesDelBloque bloque){
		return bloquesConcluidosDelTemaActual.agregarBloque(bloque);
	}
	
	public BloquesDelTema obtenerBloquesConcluidos(){
		return bloquesConcluidosDelTemaActual;
	}
	
	public void limpiarLosBloquesConcluidosDelTemaActual(){
		bloquesConcluidosDelTemaActual.borrarMisBloques();
	}
	
	public void ponerComoDichoEsta(Frase frase){
		loQueYaSeHaDicho.add(frase);
	}

	public void noPuedoRepetir(Tema tema){
		temasYaDichosQueNoPuedoRepetir.add(tema);
		ponerComoDichoEste(tema);
	}
	
	public void ponerComoDichoEste(Tema tema){
		temasYaDichos.add(tema);
	}
	
	public Temas verTemasYaTratados(){
		return temasYaDichos;
	}
	
	public Temas verTemasYaTratadosYQueNoPuedoRepetir(){
		return temasYaDichosQueNoPuedoRepetir;
	}
	
	public void agregarUnaRespuesta(Respuesta miRespuesta){
		respuestas.add(miRespuesta);
	}
	
	public void agregarSalidaAlHilo(Salida salida){
		misSalidas.add(salida);
	}
	
	public boolean existeTema(Tema tema)
	{
		return temasYaDichos.contains(tema);
	}
	
	public boolean existeTemaEspecifico(Tema tema)
	{
		return temasYaDichosQueNoPuedoRepetirParaWorkspaceEspecifico.contains(tema);
	}
	
	public void noPuedoRepetirTemaEspecifico(Tema tema){
		temasYaDichosQueNoPuedoRepetirParaWorkspaceEspecifico.add(tema);
	}
	
	public Temas verTemasYaTratadosYQueNoPuedoRepetirParaTemaEspecifico(){
		return temasYaDichosQueNoPuedoRepetirParaWorkspaceEspecifico;
	}
	
	public void borrarTemasEspecificosYaDichos(){
		temasYaDichosQueNoPuedoRepetirParaWorkspaceEspecifico.clear();;
	}
}
