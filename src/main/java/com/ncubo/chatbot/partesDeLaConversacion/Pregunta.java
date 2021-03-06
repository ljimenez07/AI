package com.ncubo.chatbot.partesDeLaConversacion;

import java.util.ArrayList;
import java.util.Enumeration;

import com.ncubo.chatbot.contexto.Variable;
import com.ncubo.chatbot.parser.Operador.TipoDeOperador;
import com.ncubo.chatbot.watson.Entidades;
import com.ncubo.chatbot.watson.Intenciones;

public class Pregunta extends Frase 
{
	private Entidades misEntidades;
	private Intenciones misIntenciones;
	
	protected Pregunta(int version, String idFrase, String nombreDeLaFrase, ArrayList<ComponentesDeLaFrase> misSinonimosDeLaFrase, String[] vinetasDeLaFrase,
			CaracteristicaDeLaFrase[] caracteristicas, Entidades entidades, Intenciones intenciones, int intentosFallidos, ArrayList<Variable> misVariables)
	{
		super (version, idFrase, nombreDeLaFrase, misSinonimosDeLaFrase, vinetasDeLaFrase, intentosFallidos, misVariables, caracteristicas);
		this.misEntidades = entidades;
		this.misIntenciones = intenciones;
	}
	
	public Entidades entidades(){
		return misEntidades;
	}
	
	public Intenciones intenciones(){
		return misIntenciones;
	}
	
	public boolean verificarSiLasIntencionesExistenYSonDeConfianza(Intenciones intenciones){
		boolean resultado = true;
		Enumeration<String> keys = misIntenciones.obtenerTodasLasIntenciones().keys();
		
		if(verSiTodasLasIntencionesQueExistenSonOr()){
			int contador = 1;
			while(keys.hasMoreElements()){
				String key = keys.nextElement();
				if( ! intenciones.obtenerTodasLasIntenciones().containsKey(key)){
					contador ++;
				}
			}
			if(contador == misIntenciones.obtenerTodasLasIntenciones().size()){
				resultado = false;
			}
		}else{
			while(keys.hasMoreElements()){
				String key = keys.nextElement();
				if(intenciones.obtenerTodasLasIntenciones().containsKey(key)){
					if( ! intenciones.obtenerTodasLasIntenciones().get(key).esReal() && 
							misIntenciones.obtenerTodasLasIntenciones().get(key).obtenerMiTipoDeOperador().equals(TipoDeOperador.AND)){
						resultado = false;
					}
				}else{
					if(misIntenciones.obtenerTodasLasIntenciones().get(key).obtenerMiTipoDeOperador().equals(TipoDeOperador.AND))
						resultado = false;
				}
			}
		}
		
		return resultado;
	}
	
	private boolean verSiTodasLasIntencionesQueExistenSonOr(){
		boolean resultado = true;
		Enumeration<String> keys = misIntenciones.obtenerTodasLasIntenciones().keys();
		
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			if(misIntenciones.obtenerTodasLasIntenciones().get(key).obtenerMiTipoDeOperador().equals(TipoDeOperador.AND)){
				resultado = false;
			}
		}
		
		return resultado;
	}
	
	public boolean verificarSiTodasLasEntidadesExisten(Entidades entidades){
		boolean resultado = true;
		Enumeration<String> keys = misEntidades.obtenerTodasLasEntidades().keys();
		
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			if(entidades.obtenerTodasLasEntidades().containsKey(key)){
				resultado = misEntidades.obtenerTodasLasEntidades().get(key).buscarSiTodosLosValoresExisten(entidades.obtenerTodasLasEntidades().get(key).obtenerMisValores());
			}else{
				resultado = false;
			}
		}
		
		return resultado;
	}
	
}
