package com.ncubo.chatbot.partesDeLaConversacion;

import java.util.ArrayList;

import com.ncubo.chatbot.contexto.Variable;

public class Conjuncion extends Frase{

	public Conjuncion(String idFrase, String nombreDeLaFrase, ArrayList<ComponentesDeLaFrase> misSinonimosDeLaFrase) {
		super(0, idFrase, nombreDeLaFrase, misSinonimosDeLaFrase, null, 0, new ArrayList<Variable>(), CaracteristicaDeLaFrase.esUnaConjuncion);
		// TODO Auto-generated constructor stub
	}

		
}
