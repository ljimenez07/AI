package com.ncubo.chatbot.partesDeLaConversacion;

import java.util.ArrayList;

public class Saludo extends Frase
{  	
	// Id de xml = class.getName()
	protected Saludo(String idFrase, ArrayList<ComponentesDeLaFrase> misSinonimosDeLaFrase, String[] vinetasDeLaFrase, int intentosFallidos, Boolean tieneEnum, CaracteristicaDeLaFrase[] caracteristicas){
		super(idFrase, misSinonimosDeLaFrase, vinetasDeLaFrase, intentosFallidos, tieneEnum, caracteristicas);
	}
}
