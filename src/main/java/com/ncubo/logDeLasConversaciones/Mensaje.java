package com.ncubo.logDeLasConversaciones;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.ncubo.chatbot.partesDeLaConversacion.Salida;
import java.util.UUID;

public class Mensaje {

	private final String idDeMensaje;
	private final String idUsuarioQuienLoCreo;
	private final String nombreDelUsuarioQuienLoCreo;
	private final Salida detalleDelMensaje;
	private final Date fechaDelCreacionDelMensaje;
	
	private static final String USUARIO_ANONIMO = "Anónimo";
	
	public Mensaje(String idUsuario, String idDeMensaje, Salida mensaje, Date fecha, String nombreUsuario){
		SimpleDateFormat formato = new SimpleDateFormat("yyMMddhhmmssMs");
		
		if( fecha == null){
			this.fechaDelCreacionDelMensaje = new Date();
		}else{
			this.fechaDelCreacionDelMensaje = fecha;
		}
		
		if(idDeMensaje.isEmpty()){
			this.idDeMensaje = UUID.randomUUID() + formato.format(fechaDelCreacionDelMensaje).toString();
		}else{
			this.idDeMensaje = idDeMensaje;
		}
		
		if(idUsuario.isEmpty() && nombreUsuario.isEmpty()){
			this.nombreDelUsuarioQuienLoCreo = USUARIO_ANONIMO;
		}else{
			this.nombreDelUsuarioQuienLoCreo = nombreUsuario;
		}
		this.idUsuarioQuienLoCreo = idUsuario;
		this.detalleDelMensaje = mensaje;
	}
	
	public String getIdUsuarioQuienLoCreo() {
		return idUsuarioQuienLoCreo;
	}

	public Salida getDetalleDelMensaje() {
		return detalleDelMensaje;
	}

	public String getIdDeMensaje() {
		return idDeMensaje;
	}

	public Date getFechaDelCreacionDelMensaje() {
		return fechaDelCreacionDelMensaje;
	}
	
	public String getNombreDelUsuarioQuienLoCreo() {
		return nombreDelUsuarioQuienLoCreo;
	}
	
}
