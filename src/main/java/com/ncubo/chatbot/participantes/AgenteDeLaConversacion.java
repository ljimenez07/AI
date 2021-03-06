package com.ncubo.chatbot.participantes;

import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.configuracion.Constantes.ModoDeLaVariable;
import com.ncubo.chatbot.partesDeLaConversacion.Frase;
import com.ncubo.chatbot.partesDeLaConversacion.Respuesta;
import com.ncubo.chatbot.partesDeLaConversacion.Salida;
import com.ncubo.chatbot.partesDeLaConversacion.Tema;
import com.ncubo.logicaDeLasConversaciones.TemariosDeUnCliente;

public class AgenteDeLaConversacion extends Agente{

	public AgenteDeLaConversacion(){}
	
	public AgenteDeLaConversacion(TemariosDeUnCliente temarios){
		super(temarios);
	}
	
	@Override
	public Salida decirUnaFrase(Frase frase, Respuesta respuesta, Tema tema, Cliente cliente, ModoDeLaVariable modoDeResolucionDeResultadosFinales, String idCliente, boolean generarAudio) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Salida volverAPreguntarUnaFrase(Frase pregunta, Respuesta respuesta, Tema tema, Cliente cliente, ModoDeLaVariable modoDeResolucionDeResultadosFinales, String idCliente) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Salida volverAPreguntarUnaFraseConMeRindo(Frase pregunta, Respuesta respuesta, Tema tema, boolean meRindo, Cliente cliente, ModoDeLaVariable modoDeResolucionDeResultadosFinales, String idCliente) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void actualizarTodasLasVariablesDeContexto(Respuesta respuesta, Cliente cliente) {
		// TODO Auto-generated method stub	
	}
	
	protected void ejecutarParametroEnElParser(Cliente cliente, String nombreKey, String parametro){
		if(parametro != null && nombreKey != null && !parametro.isEmpty()){
			String comando = "x = "+Constantes.INSTANCEA_PARAMETROS+".agregarParametro('"+nombreKey+"',"+parametro+");";
			try {
				cliente.evaluarCondicion(comando);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void ejecutarParametroEnElParser(Cliente cliente, String comando){
		try {
			cliente.evaluarCondicion(comando);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
