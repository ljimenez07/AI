package com.ncubo.regresion;

import java.util.ArrayList;

import com.ncubo.chatbot.partesDeLaConversacion.Salida;
import com.ncubo.chatbot.participantes.UsuarioDeLaConversacion;
import com.ncubo.conectores.Conectores;
import com.ncubo.logicaDeLasConversaciones.Conversaciones;
import com.ncubo.logicaDeLasConversaciones.InformacionDelCliente;
import com.ncubo.logicaDeLasConversaciones.TemariosDeUnCliente;

public class ConversacionesDeLaRegresion extends Conversaciones{

	private TemariosDeUnCliente temario;
	
	public ConversacionesDeLaRegresion(){
		super(new Conectores(), new InformacionDelCliente("test", "test", ""));
	}
	
	public void inicializarConversaciones(String rutaDelTemario){
		temario = new TemariosDeUnCliente(rutaDelTemario);
		this.inicializar(rutaDelTemario, temario);
	}
	
	public ArrayList<Salida> conversarConElAgente(UsuarioDeLaConversacion cliente, String textoDelCliente, String user, String password, String cluster, String collection, String ranker) throws Exception{
		if(this.existeLaConversacion(cliente.getIdSesion())){
			return this.conversarConElAgenteCognitivo(cliente, textoDelCliente);
		}else{
			AgenteDeLaRegresion agente = new AgenteDeLaRegresion(temario);
			return this.conversarConElAgenteCognitivo(cliente, textoDelCliente, agente, user, password, cluster, collection, ranker);
		}
	}
}
