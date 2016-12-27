package com.ncubo.chatbot.partesDeLaConversacion;

import java.util.ArrayList;

// Guarda todo el historial de la conversacion de un cliente X
public class MemoriaDeLaConversacion {

	private final ArrayList<HiloDeLaConversacion> misHilosDeLaConversacion = new ArrayList<HiloDeLaConversacion>();
	private HiloDeLaConversacion miHiloDeLaConversacionActual;
	
	// Lo general que ya se del Cliente (miContexto)
	public MemoriaDeLaConversacion(){
		
	}
	
}
