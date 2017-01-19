package com.ncubo.chatbot.bitacora;

import java.io.Serializable;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ncubo.chatbot.partesDeLaConversacion.Salida;

@SuppressWarnings("serial")
public class LaConversacion implements Serializable{

	public ArrayList<HistoricoDeLaConversacion> historico = new ArrayList<HistoricoDeLaConversacion>();
	
	public LaConversacion(){}
	
	public void agregarHistorialALaConversacion(Salida miSalida){
		historico.add(new HistoricoDeLaConversacion(miSalida));
	}
	
	public ArrayList<HistoricoDeLaConversacion> verHistorialDeLaConversacion(){
		return historico;
	}

	public String verMiHistorialDeLaConversacion(){
		JSONArray respuesta = new JSONArray();
		int contador = 0;
		for(HistoricoDeLaConversacion historico: this.verHistorialDeLaConversacion()){
			JSONObject conversacion = new JSONObject();
			try {
				conversacion.put("conversacion", contador);
				conversacion.put("Fecha", historico.getLaFechaEnQueSeCreo());
				conversacion.put("idFrase", historico.getIdFraseQueUso());
				conversacion.put("texto", historico.getElTextoQueDijoElFramework());
				conversacion.put("Cliente", historico.getLoQueDijoElParticipante());
				conversacion.put("audio", historico.getElAudioQueDijoElFramework());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			contador ++;
			respuesta.put(conversacion);
		}
		return respuesta.toString();
	}
}
