package com.ncubo.chatbot.contexto;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import com.ncubo.chatbot.configuracion.Constantes.TiposDeVariables;
import com.ncubo.chatbot.exceptiones.ChatException;
import com.ncubo.chatbot.partesDeLaConversacion.Frase;

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
	
	public Hashtable<String, Variable> obtenerTodasLasVariablesDeMiContexto(Frase miFrase){
		Hashtable<String, Variable> respuesta = new Hashtable<>();
		if(miFrase != null){
			ArrayList<Variable> variables = miFrase.obtenerLasVariablesDeContextoDeLaFrase();
			if( variables != null){
				for(Variable miVariable: variables){
					if(verificarSiUnaVariableDeContextoExiste(miVariable.getNombre())){
						respuesta.put(miVariable.getNombre(), obtenerUnaVariableDeMiContexto(miVariable.getNombre()));
					}
				}
			}
		}
		return respuesta;
	}
	
	public Hashtable<String, Variable> obtenerTodasLasVariablesEstaticas(){
		Hashtable<String, Variable> respuesta = new Hashtable<>();
		Enumeration<String> keys = misVariables.keys();
		
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			Variable variable = misVariables.get(key);
			if(variable.getTipoVariable().equals(TiposDeVariables.ESTATICA))
				respuesta.put(variable.getNombre(), variable);
		}
		return respuesta;
	}
	
}
