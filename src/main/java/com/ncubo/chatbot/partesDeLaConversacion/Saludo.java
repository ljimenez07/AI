package com.ncubo.chatbot.partesDeLaConversacion;

import java.util.ArrayList;

public class Saludo extends Frase
{  	
	// Id de xml = class.getName()
	protected Saludo(String idFrase, String nombreDeLaFrase, ArrayList<ComponentesDeLaFrase> misSinonimosDeLaFrase, String[] vinetasDeLaFrase, int intentosFallidos, CaracteristicaDeLaFrase[] caracteristicas){
		super(idFrase, nombreDeLaFrase, misSinonimosDeLaFrase, vinetasDeLaFrase, intentosFallidos, caracteristicas);
	}
}
