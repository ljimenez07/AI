package com.ncubo.chatbot.partesDeLaConversacion;

import java.util.ArrayList;

import com.ncubo.chatbot.contexto.Variable;

public class Saludo extends Frase
{  	
	// Id de xml = class.getName()
	protected Saludo(int version, String idFrase, String nombreDeLaFrase, ArrayList<ComponentesDeLaFrase> misSinonimosDeLaFrase, 
			String[] vinetasDeLaFrase, int intentosFallidos, ArrayList<Variable> misVariables, CaracteristicaDeLaFrase[] caracteristicas){
		super(version, idFrase, nombreDeLaFrase, misSinonimosDeLaFrase, vinetasDeLaFrase, intentosFallidos, misVariables, caracteristicas);
	}
}
