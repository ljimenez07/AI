package com.ncubo.regresion;

import java.util.ArrayList;
import java.util.Hashtable;
import com.ncubo.chatbot.configuracion.Constantes.ModoDeLaVariable;
import com.ncubo.chatbot.partesDeLaConversacion.Frase;
import com.ncubo.chatbot.partesDeLaConversacion.Respuesta;
import com.ncubo.chatbot.partesDeLaConversacion.Salida;
import com.ncubo.chatbot.partesDeLaConversacion.Tema;
import com.ncubo.chatbot.participantes.AgenteDeLaConversacion;
import com.ncubo.chatbot.participantes.Cliente;
import com.ncubo.chatbot.watson.WorkSpace;

public class AgenteDeLaRegresion extends AgenteDeLaConversacion{

	private Hashtable<String, String> misUltimosResultados = new Hashtable<>();
	
 	public AgenteDeLaRegresion(){}
	
	public AgenteDeLaRegresion(ArrayList<WorkSpace> miWorkSpaces){
		super(miWorkSpaces);
	}
	
	@Override
	public Salida decirUnaFrase(Frase frase, Respuesta respuesta, Tema tema, Cliente cliente, ModoDeLaVariable modoDeResolucionDeResultadosFinales){
		misUltimosResultados.clear();
		
		Salida salida = null;
		salida = this.decir(frase, respuesta, tema);
		
		this.agregarHistorico(salida);
		
		return salida;
	}
	
	@Override
	public Salida volverAPreguntarUnaFrase(Frase pregunta, Respuesta respuesta, Tema tema, Cliente cliente, ModoDeLaVariable modoDeResolucionDeResultadosFinales) {
		return volverAPreguntarUnaFraseConMeRindo(pregunta, respuesta, tema, false, cliente, modoDeResolucionDeResultadosFinales);
	}
	
	@Override
	public Salida volverAPreguntarUnaFraseConMeRindo(Frase pregunta, Respuesta respuesta, Tema tema, boolean meRindo, Cliente cliente, ModoDeLaVariable modoDeResolucionDeResultadosFinales){
		Salida salida = volverAPreguntarConMeRindo(pregunta, respuesta, tema, meRindo, false);
	
		salida = this.volverAPreguntarConMeRindo(salida.getFraseActual(), respuesta, tema, meRindo, true);
		
		this.agregarHistorico(salida);
				
		return salida;
	}
}
