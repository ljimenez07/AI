package com.ncubo.chatbot.bitacora;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HistoricoDeLaConversacion {

	private final JSONArray misHistorico = new JSONArray();
	
	public HistoricoDeLaConversacion(){}
	
	public void agregarHistorico(String loQueDijoElCliente, String loQueDijoElAgente){
		try {
			JSONObject jsonObject = new JSONObject();
			JSONArray clienteDijo = new JSONArray();
			clienteDijo.put(loQueDijoElCliente);
			
			jsonObject.put("Cliente", clienteDijo);
			jsonObject.put("Agente", loQueDijoElAgente);
			
			DateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
			Date date = new Date();
			jsonObject.put("FechaHora", dateFormat.format(date));
			
			misHistorico.put(jsonObject);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String verMiHistorico(){
		return misHistorico.toString();
	}
	
	/*public static void main(String argv[]) {
		HistoricoDeLaConversacion historico = new HistoricoDeLaConversacion();
		historico.agregarHistorico("Hola!", "{\"textos\":[{\"texto\":\"¡Hola!\",\"audio\":\"\"},{\"texto\":\"¿En qué puedo ayudarte?\",\"audio\":\"\"}]}");
		historico.agregarHistorico("como estas", "{\"textos\":[{\"texto\":\"¡Hola!, soy tu asesor del Banco Atlántida.\",\"audio\":\"\"},{\"texto\":\"¿En qué puedo ayudarte?\",\"audio\":\"\"}]}");
		
		System.out.println(historico.verMiHistorico());
	}*/
}
