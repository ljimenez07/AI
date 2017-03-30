package com.ncubo.chatbot.bloquesDeLasFrases;

import java.util.ArrayList;

import com.ncubo.chatbot.contexto.Variable;
import com.ncubo.chatbot.contexto.VariablesDeContexto;
import com.ncubo.chatbot.exceptiones.ChatException;
import com.ncubo.chatbot.partesDeLaConversacion.Placeholder;
import com.ncubo.evaluador.main.Evaluador;

public class Bloque {

	private final String idDelBloque;
	private final String condicion;
	
	public Bloque(String idDelBloque, String condicion){
		this.idDelBloque = idDelBloque;
		this.condicion = condicion;
	}
	
	public String getIdDelBloque() {
		return idDelBloque;
	}
	
	public String getCondicion() {
		return condicion;
	}

	/*private void verificarExistenciaDeLasVariables(){
		System.out.println("Verificar variables ...");
	
		if( ! condicion.isEmpty()){
			Evaluador evaluador = new Evaluador();
			ArrayList<String> variables = new ArrayList<>();
			try {
				String comando = "";
				comando = "show "+condicion+";";
				variables = evaluador.buscarVariablesEstaticasEnElComando(comando);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(variables != null && ! variables.isEmpty()){
				ArrayList<Placeholder> misPlaceholdersEnLaCondicion = buscarPlaceholdersEnLaCondicion(variables);
				for(Placeholder placeholder: misPlaceholdersEnLaCondicion){
					if(! VariablesDeContexto.getInstance().verificarSiUnaVariableDeContextoExiste(placeholder.getNombreDelPlaceholder()))
						throw new ChatException(String.format("La variable %s no existe en el sistema.", placeholder.getNombreDelPlaceholder()));
				}
			}
		}
	}*/
	
}
