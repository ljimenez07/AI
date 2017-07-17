package com.ncubo.logDeLasConversaciones;

import java.util.ArrayList;
import java.util.Date;

import com.ncubo.chatbot.partesDeLaConversacion.Salida;

public class Chat {

	private final String idDeLaConversacion;
	private ArrayList<Mensaje> misMensajes;
	private final Date fechaDeCreacion;
	private final String idUsuarioQuienLoCreo;
	
	public Chat(String idConversacion, String idUsuario){
		this.fechaDeCreacion = new Date();
		this.idDeLaConversacion = idConversacion;
		this.misMensajes = new ArrayList<>();
		this.idUsuarioQuienLoCreo = idUsuario;
	}
	
	private boolean hayMensajes(){
		return ! misMensajes.isEmpty();
	}
	
	public void agregarUnMensaje(String idUsuario, String idDeMensaje, Salida mensaje, Date fecha, String nombreUsuario){
		misMensajes.add(new Mensaje(idUsuario, idDeMensaje, mensaje, fecha, nombreUsuario));
	}
	
	public String obtenerElIdDelUltimoMensaje(){
		if(hayMensajes()){
			return misMensajes.get(misMensajes.size() - 1).getIdDeMensaje();
		}else{
			return "0";
		}
	}
	
	public ArrayList<Mensaje> obtenerLosUltimosMensaajesApartirDeUnIndex(String mensajeId){
		
		ArrayList<Mensaje> respuesta = new ArrayList<>();
		boolean loEncontro = false;
		
		for(Mensaje mensaje: misMensajes){
			if(loEncontro){
				respuesta.add(mensaje);
			}else{
				if(mensaje.getIdDeMensaje().equals(mensajeId)){
					loEncontro = true;
				}
			}
		}
		
		return respuesta;
	}
	
	public ArrayList<Mensaje> getMisMensajes() {
		return misMensajes;
	}

	public void setMisMensajes(ArrayList<Mensaje> misMensajes) {
		this.misMensajes = misMensajes;
	}

	public String getIdDeLaConversacion() {
		return idDeLaConversacion;
	}

	public Date getFechaDeCreacion() {
		return fechaDeCreacion;
	}

	public String getIdUsuarioQuienLoCreo() {
		return idUsuarioQuienLoCreo;
	}
	
}
