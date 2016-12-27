package com.ncubo.chatbot.partesDeLaConversacion;

import java.util.Enumeration;

import com.ncubo.chatbot.parser.Operador.TipoDeOperador;
import com.ncubo.chatbot.watson.Entidades;
import com.ncubo.chatbot.watson.Intenciones;

public class Pregunta extends Frase 
{
	private Entidades misEntidades;
	private Intenciones misIntenciones;
	
	protected Pregunta(String idFrase, String[] textosDeLaFrase, String[] textosImpertinetesDeLaFrase, String[] vinetasDeLaFrase, String[] textosDeLaFraseMeRindo,
			CaracteristicaDeLaFrase[] caracteristicas, Entidades entidades, Intenciones intenciones)
	{
		super (idFrase, textosDeLaFrase, textosImpertinetesDeLaFrase, vinetasDeLaFrase, textosDeLaFraseMeRindo, caracteristicas);
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
