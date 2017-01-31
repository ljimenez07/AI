package com.ncubo.regresion;

import java.util.ArrayList;

import com.ncubo.chatbot.partesDeLaConversacion.Salida;
import com.ncubo.chatbot.participantes.Usuario;
import com.ncubo.logicaDeLasConversaciones.Conversaciones;

public class ConversacionesDeLaRegresion extends Conversaciones{

	private TemarioDeLaRegresion temario;
	
	public ConversacionesDeLaRegresion(){}
	
	public void inicializarConversaciones(String rutaDelTemario){
		temario = new TemarioDeLaRegresion(rutaDelTemario);
		this.inicializar(rutaDelTemario, temario);
	}
	
	public ArrayList<Salida> conversarConElAgente(Usuario cliente, String textoDelCliente) throws Exception{
		if(this.existeLaConversacion(cliente.getIdSesion())){
			return this.conversarConElAgenteCognitivo(cliente, textoDelCliente);
		}else{
			AgenteDeLaRegresion agente = new AgenteDeLaRegresion(temario.contenido().getMiWorkSpaces());
			return this.conversarConElAgenteCognitivo(cliente, textoDelCliente, agente);
		}
	}
}
