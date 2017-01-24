package com.ncubo.chatbot.participantes;

import java.util.ArrayList;

import com.ncubo.chatbot.configuracion.Constantes.ModoDeLaVariable;
import com.ncubo.chatbot.partesDeLaConversacion.Frase;
import com.ncubo.chatbot.partesDeLaConversacion.Respuesta;
import com.ncubo.chatbot.partesDeLaConversacion.Salida;
import com.ncubo.chatbot.partesDeLaConversacion.Tema;
import com.ncubo.chatbot.watson.WorkSpace;

public class AgenteDeLaConversacion extends Agente{

	public AgenteDeLaConversacion(){}
	
	public AgenteDeLaConversacion(ArrayList<WorkSpace> miWorkSpaces){
		super(miWorkSpaces);
	}
	
	@Override
	public Salida decirUnaFrase(Frase frase, Respuesta respuesta, Tema tema, Cliente cliente, ModoDeLaVariable modoDeResolucionDeResultadosFinales) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Salida volverAPreguntarUnaFrase(Frase pregunta, Respuesta respuesta, Tema tema, Cliente cliente, ModoDeLaVariable modoDeResolucionDeResultadosFinales) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Salida volverAPreguntarUnaFraseConMeRindo(Frase pregunta, Respuesta respuesta, Tema tema, boolean meRindo, Cliente cliente, ModoDeLaVariable modoDeResolucionDeResultadosFinales) {
		// TODO Auto-generated method stub
		return null;
	}

}
