package com.ncubo.chatbot.partesDeLaConversacion;

import java.util.ArrayList;

public class Despedida extends Frase 
{
	protected Despedida(String idFrase, ArrayList<ComponentesDeLaFrase> misSinonimosDeLaFrase, String[] vinetasDeLaFrase, int intentosFallidos, Boolean tieneEnum, CaracteristicaDeLaFrase[] caracteristicas)
	{
		super (idFrase, misSinonimosDeLaFrase, vinetasDeLaFrase, intentosFallidos, tieneEnum, caracteristicas);
	}
}