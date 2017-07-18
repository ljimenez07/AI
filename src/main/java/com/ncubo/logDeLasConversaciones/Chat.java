package com.ncubo.logDeLasConversaciones;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import com.ncubo.chatbot.partesDeLaConversacion.Salida;

public class Chat {

	private final String idDeLaConversacion;
	private ArrayList<Mensaje> misMensajes;
	private final Date fechaDeCreacion;
	private final String idUsuarioQuienLoCreo;
	private Hashtable<String, UsuarioDelChat> usuariosDelChat;
	
	public Chat(String idConversacion, String idUsuario){
		this.fechaDeCreacion = new Date();
		this.idDeLaConversacion = idConversacion;
		this.misMensajes = new ArrayList<>();
		this.idUsuarioQuienLoCreo = idUsuario;
		this.usuariosDelChat = new Hashtable<>();
	}
	
	public boolean existeElUsuarioEnElChat(String idDelUsuario){
		if(! idDelUsuario.isEmpty()){
			return usuariosDelChat.containsKey(idDelUsuario);
		}else{
			return false;
		}
	}
	
	public boolean existeElUusarioYEsAnonimo(String idDelUsuario){
		if(existeElUsuarioEnElChat(idDelUsuario)){
			return usuariosDelChat.get(idDelUsuario).isEsUsuarioAnonimo();
		}else{
			return false;
		}
	}
	
	private boolean hayMensajes(){
		return ! misMensajes.isEmpty();
	}
	
	public void agregarUsuarioAlChat(String idUsuario, String nombre){
		usuariosDelChat.put(idUsuario, new UsuarioDelChat(idUsuario, nombre));
	}
	
	public boolean agregarUnMensaje(String idUsuario, String idDeMensaje, Salida mensaje, Date fecha){
		if (existeElUsuarioEnElChat(idUsuario)){
			misMensajes.add(new Mensaje(idDeMensaje, mensaje, fecha, usuariosDelChat.get(idUsuario)));
			return true;
		}
		return false;
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
