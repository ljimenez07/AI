package com.ncubo.chatbot.partesDeLaConversacion;

import java.util.ArrayList;

public class Afirmacion extends Frase 
{
	protected Afirmacion(String idFrase, ArrayList<ComponentesDeLaFrase> misSinonimosDeLaFrase, String[] vinetasDeLaFrase, CaracteristicaDeLaFrase[] caracteristicas)
	{
		super (idFrase, misSinonimosDeLaFrase, vinetasDeLaFrase, caracteristicas);
	}
}