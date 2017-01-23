package com.ncubo.controller;

import java.util.Iterator;

import com.ncubo.chatbot.bitacora.LogDeLaConversacion;
import com.ncubo.db.BitacoraDao;

public class FiltroDeConversaciones {

	public Iterator<LogDeLaConversacion> obtenerConversacionesEntreFechas(String fechaInicial, String fechaFinal) throws ClassNotFoundException{

		BitacoraDao bitacora = new BitacoraDao();
		
		return bitacora.buscarConversacionesEntreFechas(fechaInicial, fechaFinal);
	}

	public Iterator<LogDeLaConversacion> obntenerLasConversacionesDeHoy() throws ClassNotFoundException{

		BitacoraDao bitacora = new BitacoraDao();
		
		return bitacora.buscarConversacionesDeHoy();
	}

	public Iterator<LogDeLaConversacion> obtenerConversacionesPorUsuario(String idUsuario) throws ClassNotFoundException{

		BitacoraDao bitacora = new BitacoraDao();

		return bitacora.buscarConversacionesPorUsuario(idUsuario);
	}

	public Iterator<LogDeLaConversacion> obtenerConversacionesPorUsuarioAnonimo() throws ClassNotFoundException{

		BitacoraDao bitacora = new BitacoraDao();
		
		return bitacora.buscarConversacionesPorUsuarioAnonimo();
	}
	
	public Iterator<LogDeLaConversacion> obtenerConversacionesPorUsuarioEntreFechas(String idUsuario ,String fechaInicial, String fechaFinal) throws ClassNotFoundException{

		BitacoraDao bitacora = new BitacoraDao();

		return bitacora.buscarConversacionesDeUsuariosEspecificosEntreFechas(idUsuario, fechaInicial, fechaFinal);
	}
	
	public Iterator<LogDeLaConversacion> obtenerConversacionesDeUsusariosAnonimosEntreFechas(String fechaInicial, String fechaFinal) throws ClassNotFoundException{

		BitacoraDao bitacora = new BitacoraDao();

		return bitacora.buscarConversacionesDeUsuariosAnonimosEntreFechas(fechaInicial, fechaFinal);
	}
	
	public Iterator<LogDeLaConversacion> obtenerLasConversacionesDeTodosLosUsuariosNoAnonimos() throws ClassNotFoundException{

		BitacoraDao bitacora = new BitacoraDao();

		return bitacora.buscarLasConversacionesDeTodosLosUsuariosNoAnonimos();
	}
	
	public Iterator<LogDeLaConversacion> obtenerTodasLasConversaciones() throws ClassNotFoundException{

		BitacoraDao bitacora = new BitacoraDao();

		return bitacora.buscarTodasLasConversaciones();
	}
	
}
