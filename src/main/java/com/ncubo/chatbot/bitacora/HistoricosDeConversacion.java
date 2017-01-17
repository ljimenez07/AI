package com.ncubo.chatbot.bitacora;

import java.io.Serializable;
import java.util.ArrayList;

import com.ncubo.chatbot.partesDeLaConversacion.Salida;

@SuppressWarnings("serial")
public class HistoricosDeConversacion implements Serializable{

	public final ArrayList<HistoricoDeLaConversacion> historico;
	
	public HistoricosDeConversacion(){
		historico = new ArrayList<HistoricoDeLaConversacion>();
	}
	
	public void agregarHistorialALaConversacion(Salida miSalida){
		historico.add(new HistoricoDeLaConversacion(miSalida));
	}
	
	public ArrayList<HistoricoDeLaConversacion> verHistorialDeLaConversacion(){
		return historico;
	}

}
