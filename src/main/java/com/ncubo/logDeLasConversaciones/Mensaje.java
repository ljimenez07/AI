package com.ncubo.logDeLasConversaciones;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.ncubo.chatbot.partesDeLaConversacion.Salida;
import java.util.UUID;

public class Mensaje{

	private String idDeMensaje;
	private Salida detalleDelMensaje;
	private Date fechaDelCreacionDelMensaje;
	private UsuarioDelChat informacionDelUsuario;

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
	
	public String getIdDeMensaje() {
		return idDeMensaje;
	}

	public void setIdDeMensaje(String idDeMensaje) {
		this.idDeMensaje = idDeMensaje;
	}

	public Salida getDetalleDelMensaje() {
		return detalleDelMensaje;
	}

	public void setDetalleDelMensaje(Salida detalleDelMensaje) {
		this.detalleDelMensaje = detalleDelMensaje;
	}

	public Date getFechaDelCreacionDelMensaje() {
		return fechaDelCreacionDelMensaje;
	}

	public void setFechaDelCreacionDelMensaje(Date fechaDelCreacionDelMensaje) {
		this.fechaDelCreacionDelMensaje = fechaDelCreacionDelMensaje;
	}

	public UsuarioDelChat getInformacionDelUsuario() {
		return informacionDelUsuario;
	}

	public void setInformacionDelUsuario(UsuarioDelChat informacionDelUsuario) {
		this.informacionDelUsuario = informacionDelUsuario;
	}
	
}
