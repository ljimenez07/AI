package com.ncubo.chatbot.consola;

import java.util.ArrayList;

import com.ncubo.chatbot.participantes.AgenteDeLaConversacion;
import com.ncubo.chatbot.watson.WorkSpace;

public class AgenteDePrueba extends AgenteDeLaConversacion{

	public AgenteDePrueba(){}
	
	public AgenteDePrueba(ArrayList<WorkSpace> miWorkSpaces){
		super(miWorkSpaces);
	}
}
