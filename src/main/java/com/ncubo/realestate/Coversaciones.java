package com.ncubo.realestate;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.partesDeLaConversacion.Salida;
import com.ncubo.chatbot.partesDeLaConversacion.Temario;
import com.ncubo.chatbot.participantes.Cliente;

public class Coversaciones {

	private final static Hashtable<String, Conversacion> misConversaciones = new Hashtable<String, Conversacion>();
	private final static Hashtable<String, Cliente> misClientes = new Hashtable<String, Cliente>();
	private static Temario temarioDelRealEstate;
	
	public Coversaciones(){
		temarioDelRealEstate = new TemarioDeRealEstate(Constantes.PATH_ARCHIVO_DE_CONFIGURACION_RS);
	}
	
	public String agregarUnNuevoCliente(String nombreDelCliente, String idDelCliente){
		String resultado = "El usuario no se pudo agregar";
		try{
			if( ! existeElCliente(idDelCliente)){
				Cliente cliente = new Cliente(nombreDelCliente, idDelCliente);
				misClientes.put(cliente.getMiId(), cliente);
				Conversacion coversacion = new Conversacion(temarioDelRealEstate, cliente);
				misConversaciones.put(cliente.getMiId(), coversacion);
				resultado = "El usuario se creo exitosamente";
				System.out.println(resultado);
			}else{
				resultado = "El usuario ya existe";
				System.out.println(resultado);
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		return resultado;
	}
	
	private boolean existeElCliente(String idDelCliente){
		return misClientes.containsKey(idDelCliente);
	}
	
	public ArrayList<Salida> inicializarConversacionConElAgente(String idDelCliente){
		ArrayList<Salida> resultado = null;
		
		if(existeElCliente(idDelCliente)){
			resultado = misConversaciones.get(idDelCliente).inicializarLaConversacion();
		}else{
			System.out.println("No existe el usuario");
		}
		return resultado;
	}
	
	public ArrayList<Salida> converzarConElAgente(String idDelCliente, String textoDelCliente){
		ArrayList<Salida> resultado = null;
		
		if(existeElCliente(idDelCliente)){
			resultado = misConversaciones.get(idDelCliente).analizarLaRespuestaConWatson(textoDelCliente);
		}else{
			System.out.println("No existe el usuario");
		}
		return resultado;
	}
	
}
