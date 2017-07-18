package com.ncubo.logDeLasConversaciones;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.ncubo.chatbot.partesDeLaConversacion.Salida;
import java.util.UUID;

public class Mensaje {

	private final String idDeMensaje;
	private final Salida detalleDelMensaje;
	private final Date fechaDelCreacionDelMensaje;
	private final UsuarioDelChat informacionDelUsuario;

	public Mensaje(String idDeMensaje, Salida mensaje, Date fecha, UsuarioDelChat usuario){
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
		
		this.informacionDelUsuario = usuario;
		this.detalleDelMensaje = mensaje;
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
	
	public UsuarioDelChat getInformacionDelUsuario() {
		return informacionDelUsuario;
	}
	
}
