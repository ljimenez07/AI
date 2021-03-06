package com.ncubo.chatbot.bloquesDeLasFrases;

import java.util.ArrayList;

import com.ncubo.chatbot.contexto.VariablesDeContexto;
import com.ncubo.chatbot.participantes.Cliente;
import com.ncubo.evaluador.main.Evaluador;

public class Bloque {

	private final String idDelBloque;
	private final String condicion;
	
	public Bloque(String idDelBloque, String condicion){
		this.idDelBloque = idDelBloque;
		this.condicion = condicion;
		verificarExistenciaDeLasVariables();
	}
	
	public String getIdDelBloque() {
		return idDelBloque;
	}
	
	public String getCondicion() {
		return condicion;
	}

	private void verificarExistenciaDeLasVariables(){
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
				for(String key: variables){
					if(! VariablesDeContexto.getInstance().verificarSiUnaVariableDeContextoExiste(key)){
						System.err.println(String.format("La variable %s, en el bloque %s, no existe en el sistema.", key, this.idDelBloque));
						System.exit(0);
					}
				}
			}
		}
	}
	
	public boolean sePuedeDecirElBloque(Cliente cliente){
		if(this.tieneCondicion()){
			String comando = "show "+this.getCondicion()+";";
			try {
				if(cliente.evaluarCondicion(comando).contains("true")){
					return true;
				}else{
					return false;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	
	public boolean tieneCondicion(){
		return ! condicion.isEmpty();
	}
	
}
