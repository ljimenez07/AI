package com.ncubo.chatbot.partesDeLaConversacion;

import java.util.ArrayList;

public class Afirmacion extends Frase 
{
	public Afirmacion(int version, String idFrase, String nombreDeLaFrase, ArrayList<ComponentesDeLaFrase> misSinonimosDeLaFrase, String[] vinetasDeLaFrase, int intentosFallidos, CaracteristicaDeLaFrase[] caracteristicas)
	{
		super (version ,idFrase, nombreDeLaFrase, misSinonimosDeLaFrase, vinetasDeLaFrase, intentosFallidos, caracteristicas);
	}
}