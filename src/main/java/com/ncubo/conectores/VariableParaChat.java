package com.ncubo.conectores;

import com.ncubo.chatbot.configuracion.Constantes.ModoDeLaVariable;
import com.ncubo.chatbot.configuracion.Constantes.TiposDesVariables;
import com.ncubo.evaluador.libraries.Objeto;

public abstract class VariableParaChat extends Objeto{

	protected ModoDeLaVariable modoDeLaVariable = ModoDeLaVariable.PRUEBA;
	
	public abstract String nombre(); // Ej: Saldo, Cuenta
	protected abstract TiposDesVariables tipo(); 
	public abstract Objeto cambiarModoReal();
	public abstract Objeto cambiarModoPrueba();
	public abstract Objeto evaluar();
	protected abstract Objeto evaluarModoReal();
	protected abstract Objeto evaluarModoPrueba();

}
