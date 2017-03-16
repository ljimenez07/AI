package com.ncubo.chatbot.bitacora;

import java.io.Serializable;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ncubo.chatbot.partesDeLaConversacion.Salida;

@SuppressWarnings("serial")
public class LogDeLaConversacion implements Serializable{

	public ArrayList<Dialogo> historico = new ArrayList<Dialogo>();
	
	public LogDeLaConversacion(){}
	
	public void agregarHistorialALaConversacion(Salida miSalida){
		try{
			historico.add(new Dialogo(miSalida));
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	public void limpiarHistorialALaConversacion(){
		historico.clear();
	}
	
	public ArrayList<Dialogo> verHistorialDeLaConversacion(){
		return historico;
	}

	public String verMiHistorialDeLaConversacion(){
		JSONArray respuesta = new JSONArray();
		int contador = 0;
		for(Dialogo historico: this.verHistorialDeLaConversacion()){
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
