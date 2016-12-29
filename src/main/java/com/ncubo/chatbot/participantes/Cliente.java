package com.ncubo.chatbot.participantes;

import java.util.ArrayList;
import com.ncubo.chatbot.contexto.AdministradorDeVariablesDeContexto;

public class Cliente extends Participante{

	private String miNombre;
	private String miId;
	private ArrayList<String> misIdsDeSesiones = new ArrayList<String>();
	protected AdministradorDeVariablesDeContexto administradorDeVariablesDeContexto;
	
	public Cliente(){
		miNombre = "";
		miId = "";
		administradorDeVariablesDeContexto = new AdministradorDeVariablesDeContexto();
	}
	
	public Cliente(String nombre, String id) throws Exception{
		miNombre = nombre;
		miId = id;
		administradorDeVariablesDeContexto = new AdministradorDeVariablesDeContexto();
	}
	
	public String getMiNombre() {
		return miNombre;
	}

	public void setMiNombre(String miNombre) {
		this.miNombre = miNombre;
	}

	public String getMiId() {
		return miId;
	}

	public void setMiId(String miId) {
		this.miId = miId;
	}
	
	public ArrayList<String> getMisIdsDeSesiones() {
		return misIdsDeSesiones;
	}

	public void borrarTodosLosIdsDeSesiones() {
		this.misIdsDeSesiones.clear();
	}
	
	public void agregarIdsDeSesiones(String idDeSesion) {
		if( ! contieneElIdSesion(idDeSesion)){
			this.misIdsDeSesiones.add(idDeSesion);
		}
	}
	
	public boolean contieneElIdSesion(String idSesion){
		return misIdsDeSesiones.contains(idSesion);
	}
}

