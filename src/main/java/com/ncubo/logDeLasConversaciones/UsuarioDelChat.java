package com.ncubo.logDeLasConversaciones;

import java.util.ArrayList;

import com.ncubo.db.ChatDao;

public class UsuarioDelChat{

	private String idDelUsuario;
	private String nombreDelUsuario;
	private boolean esUsuarioAnonimo;
	private ArrayList<MensajeVisto> mensajesVistosPorElUsuario;
	private ChatDao chatDao;
	
	public static final String USUARIO_ANONIMO = "An&oacute;nimo";
	
	public UsuarioDelChat(String idUsuario, String nombre){
		this.idDelUsuario = idUsuario;
		this.mensajesVistosPorElUsuario = new ArrayList<>();
		this.chatDao = new ChatDao();
		
		if(idUsuario.isEmpty() || nombre.isEmpty()){
			esUsuarioAnonimo = true;
			this.nombreDelUsuario = USUARIO_ANONIMO;
		}else{
			esUsuarioAnonimo = false;
			this.nombreDelUsuario = nombre;
		}
	}
	
	public String getIdDelUsuario() {
		return idDelUsuario;
	}

	public void setIdDelUsuario(String idDelUsuario) {
		this.idDelUsuario = idDelUsuario;
	}

	public String getNombreDelUsuario() {
		return nombreDelUsuario;
	}

	public void setNombreDelUsuario(String nombreDelUsuario) {
		this.nombreDelUsuario = nombreDelUsuario;
	}

	public boolean isEsUsuarioAnonimo() {
		return esUsuarioAnonimo;
	}

	public void setEsUsuarioAnonimo(boolean esUsuarioAnonimo) {
		this.esUsuarioAnonimo = esUsuarioAnonimo;
	}

	public static String getUsuarioAnonimo() {
		return USUARIO_ANONIMO;
	}
	
	public boolean elMensajeYaFueVisto(String idMensaje){
		
		for(MensajeVisto mensaje: mensajesVistosPorElUsuario){
			if(mensaje.getMensaje().getIdDeMensaje().equals(idMensaje)){
				return true;
			}
		}
		return false;
	}
	
	public void agregarUnNuevoMensajeComoVisto(Mensaje mensaje){
		if( ! elMensajeYaFueVisto(mensaje.getIdDeMensaje())){
			MensajeVisto mensajeVisto = new MensajeVisto(mensaje);
			mensajesVistosPorElUsuario.add(mensajeVisto);
			chatDao.marcarUnMensajeComoVisto(mensajeVisto, idDelUsuario);
		}
	}
	
	public ArrayList<MensajeVisto> getMensajesVistosPorElUsuario() {
		return mensajesVistosPorElUsuario;
	}

	public void setMensajesVistosPorElUsuario(ArrayList<MensajeVisto> mensajesVistosPorElUsuario) {
		this.mensajesVistosPorElUsuario = mensajesVistosPorElUsuario;
	}
	
	public int obtenerLaCantidadDeMensajesVistos(){
		return mensajesVistosPorElUsuario.size();
	}
	
}
