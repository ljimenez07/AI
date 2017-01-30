package com.ncubo.controller;

import java.util.Iterator;

import com.ncubo.chatbot.bitacora.LogDeLaConversacion;
import com.ncubo.db.DetalleDeConversacionDao;

public class FiltroDeConversaciones {

	
	public Iterator<LogDeLaConversacion> obtenerConversacionesEntreFechas(String fechaInicial, String fechaFinal) throws ClassNotFoundException{

		DetalleDeConversacionDao detalle = new DetalleDeConversacionDao();
		
		return detalle.buscarConversacionesEntreFechas(fechaInicial, fechaFinal);
	}

	public Iterator<LogDeLaConversacion> obntenerLasConversacionesDeHoy() throws ClassNotFoundException{

		DetalleDeConversacionDao detalle = new DetalleDeConversacionDao();
		
		return detalle.buscarConversacionesDeHoy();
	}

	public Iterator<LogDeLaConversacion> obtenerConversacionesPorUsuario(String idUsuario) throws ClassNotFoundException{

		DetalleDeConversacionDao detalle = new DetalleDeConversacionDao();

		return detalle.buscarConversacionesPorUsuario(idUsuario);
	}

	public Iterator<LogDeLaConversacion> obtenerConversacionesPorUsuarioAnonimo() throws ClassNotFoundException{

		DetalleDeConversacionDao detalle = new DetalleDeConversacionDao();
		
		return detalle.buscarConversacionesPorUsuarioAnonimo();
	}
	
	public Iterator<LogDeLaConversacion> obtenerConversacionesPorUsuarioEntreFechas(String idUsuario ,String fechaInicial, String fechaFinal) throws ClassNotFoundException{

		DetalleDeConversacionDao detalle = new DetalleDeConversacionDao();

		return detalle.buscarConversacionesDeUsuariosEspecificosEntreFechas(idUsuario, fechaInicial, fechaFinal);
	}
	
	public Iterator<LogDeLaConversacion> obtenerConversacionesDeUsusariosAnonimosEntreFechas(String fechaInicial, String fechaFinal) throws ClassNotFoundException{

		DetalleDeConversacionDao detalle = new DetalleDeConversacionDao();

		return detalle.buscarConversacionesDeUsuariosAnonimosEntreFechas(fechaInicial, fechaFinal);
	}
	
	public Iterator<LogDeLaConversacion> obtenerLasConversacionesDeTodosLosUsuariosNoAnonimos() throws ClassNotFoundException{

		DetalleDeConversacionDao detalle = new DetalleDeConversacionDao();

		return detalle.buscarLasConversacionesDeTodosLosUsuariosNoAnonimos();
	}
	
	public Iterator<LogDeLaConversacion> obtenerTodasLasConversaciones() throws ClassNotFoundException{

		DetalleDeConversacionDao detalle = new DetalleDeConversacionDao();

		return detalle.buscarTodasLasConversaciones();
	}
	
}
