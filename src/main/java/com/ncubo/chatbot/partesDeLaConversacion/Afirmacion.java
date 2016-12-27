package com.ncubo.chatbot.partesDeLaConversacion;


public class Afirmacion extends Frase 
{
	protected Afirmacion(String idFrase, String[] textosDeLaFrase, String[] vinetasDeLaFrase, String[] textosDeLaFraseMeRindo, CaracteristicaDeLaFrase[] caracteristicas)
	{
		super (idFrase, textosDeLaFrase, null, vinetasDeLaFrase, textosDeLaFraseMeRindo, caracteristicas);
	}
}