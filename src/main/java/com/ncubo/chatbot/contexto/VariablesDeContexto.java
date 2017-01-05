package com.ncubo.chatbot.contexto;

import java.util.Hashtable;

import com.ncubo.chatbot.exceptiones.ChatException;

public class VariablesDeContexto {

	private final static Hashtable<String, Variable> misVariables = new Hashtable<String, Variable>();
	private static VariablesDeContexto variablesDeContexto = null;
	
	private VariablesDeContexto(){}
	
	public static VariablesDeContexto getInstance(){
		if(variablesDeContexto == null){
			variablesDeContexto = new VariablesDeContexto();
		}
		return variablesDeContexto;
	}
	
	public void agregarVariableAMiContexto(Variable variable){
		misVariables.put(variable.getNombre(), variable);
	}
	
	public boolean verificarSiUnaVariableDeContextoExiste(String variable){
		return misVariables.containsKey(variable);
	}
	
	public Variable obtenerUnaVariableDeMiContexto(String variable){
		if (verificarSiUnaVariableDeContextoExiste(variable))
			return misVariables.get(variable);
		else
			throw new ChatException(String.format("La variable %s no existe en el sistema", variable));
	}
	
	public Hashtable<String, Variable> obtenerTodasLasVariablesDeMiContexto(){
		return misVariables;
	}
}
