package com.ncubo.chatbot.contexto;

import java.util.Enumeration;
import java.util.Hashtable;

import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.configuracion.Constantes.TiposDesVariables;
import com.ncubo.evaluador.main.Evaluador;

public class AdministradorDeVariablesDeContexto {

	private final Evaluador miEvaluador;
	
	public AdministradorDeVariablesDeContexto(){
		miEvaluador = new Evaluador();
		inicializarVariables();
	}
	
	private void inicializarVariables(){
		System.out.println("Inicializar variables del cliente ...");
		
		Hashtable<String, Variable> variables = VariablesDeContexto.getInstance().obtenerTodasLasVariablesDeMiContexto();
		Enumeration<String> keys = variables.keys();
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			Variable variable = variables.get(key);
			if( ! variable.getValorDeLaVariable().equals("") && variable.getTipoVariable().equals(TiposDesVariables.CONTEXTO)){
				agregarVariableDeContexto(variable.getNombre(), variable.getValorDeLaVariable()[0]);
			}
		}
	}
	
	private void agregarVariableDeContexto(String nombreDeLaVariable, String valorDeLaVariable){
		if( ! nombreDeLaVariable.equals("") && ! valorDeLaVariable.equals("")){
			String comando = nombreDeLaVariable+"="+valorDeLaVariable+";";
			System.out.println("Agregado variable con el comando: "+comando);
			miEvaluador.crearContexto(comando);
		}
	}
	
	private String obtenerElValorDeUnaVariable(String nombreDeLaVariable) throws Exception{
		String resultado = "";
		if( ! nombreDeLaVariable.equals("")){
			String comando = "show "+nombreDeLaVariable+";";
			resultado = miEvaluador.ejecutaComando(comando).trim();
		}
		return resultado;
	}

	public String ejecutar(String comando) throws Exception {
		System.out.println("Ejecutando el comando: "+comando);
		return miEvaluador.ejecutaComando(comando);
	}
	
	public String obtenerVariable(String nombreDeLaVariable) throws Exception{
		return obtenerElValorDeUnaVariable(nombreDeLaVariable);
	}
}
