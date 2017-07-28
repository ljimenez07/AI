package com.ncubo.logDeLasConversaciones;

import java.util.Date;

public class MensajeVisto {

	private Date horaVista;
	private Mensaje mensaje;
	
	public MensajeVisto(Mensaje mensaje){
		this.mensaje = mensaje;
		this.horaVista = new Date();
	}
	
	public MensajeVisto(Mensaje mensaje, Date fechaVisto){
		this.mensaje = mensaje;
		this.horaVista = fechaVisto;
	}
	
	public Mensaje getMensaje() {
		return mensaje;
	}
	
	public Date getHoraVista() {
		return horaVista;
	}

	public void setHoraVista(Date horaVista) {
		this.horaVista = horaVista;
	}
	
	public void setMensaje(Mensaje mensaje) {
		this.mensaje = mensaje;
	}
	
}
