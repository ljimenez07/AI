package com.ncubo.regresion;

import java.util.Vector;

import com.ncubo.chatbot.bitacora.LogDeLaConversacion;

public class Resultado {

	public int idCaso = 0;
	public LogDeLaConversacion logCasoDePrueba = new LogDeLaConversacion();
	public boolean status;
	public LogDeLaConversacion logResultado = new LogDeLaConversacion();
	public Vector <String> observaciones = new Vector<String>();
	
	public Resultado(int idCaso, LogDeLaConversacion logCaso, boolean status, LogDeLaConversacion logResultado, Vector<String> observaciones){
		this.idCaso = idCaso;
		this.logCasoDePrueba = logCaso;
		this.status = status;
		this.logResultado = logResultado;
		this.observaciones = observaciones;
	}

	public int getIdCaso() {
		return idCaso;
	}

	public void setIdCaso(int idCaso) {
		this.idCaso = idCaso;
	}

	public LogDeLaConversacion getLogCasoDePrueba() {
		return logCasoDePrueba;
	}

	public void setLogCasoDePrueba(LogDeLaConversacion logCasoDePrueba) {
		this.logCasoDePrueba = logCasoDePrueba;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public LogDeLaConversacion getLogResultado() {
		return logResultado;
	}

	public void setLogResultado(LogDeLaConversacion logResultado) {
		this.logResultado = logResultado;
	}

	public Vector<String> getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(Vector<String> observaciones) {
		this.observaciones = observaciones;
	}
	
	
}
