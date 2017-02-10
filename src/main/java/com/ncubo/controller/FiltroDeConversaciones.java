package com.ncubo.controller;

import java.util.Iterator;

import com.ncubo.chatbot.bitacora.LogDeLaConversacion;
import com.ncubo.db.DetalleDeConversacionDao;

public class FiltroDeConversaciones {

	
	public Iterator<LogDeLaConversacion> obtenerConversacionesEntreFechas(String fechaInicial, String fechaFinal, String idCliente) throws ClassNotFoundException{

		DetalleDeConversacionDao detalle = new DetalleDeConversacionDao();
		
		return detalle.buscarConversacionesEntreFechas(fechaInicial, fechaFinal, idCliente);
	}

	public Iterator<LogDeLaConversacion> obntenerLasConversacionesDeHoy(String idCliente) throws ClassNotFoundException{

		DetalleDeConversacionDao detalle = new DetalleDeConversacionDao();
		
		return detalle.buscarConversacionesDeHoy(idCliente);
	}

	public Iterator<LogDeLaConversacion> obtenerConversacionesPorUsuario(String idUsuario, String idCliente) throws ClassNotFoundException{

		DetalleDeConversacionDao detalle = new DetalleDeConversacionDao();

		return detalle.buscarConversacionesPorUsuario(idUsuario, idCliente);
	}

	public Iterator<LogDeLaConversacion> obtenerConversacionesPorUsuarioAnonimo(String idCliente) throws ClassNotFoundException{

		DetalleDeConversacionDao detalle = new DetalleDeConversacionDao();
		
		return detalle.buscarConversacionesPorUsuarioAnonimo(idCliente);
	}
	
	public Iterator<LogDeLaConversacion> obtenerConversacionesPorUsuarioEntreFechas(String idUsuario ,String fechaInicial, String fechaFinal, String idCliente) throws ClassNotFoundException{

		DetalleDeConversacionDao detalle = new DetalleDeConversacionDao();

		return detalle.buscarConversacionesDeUsuariosEspecificosEntreFechas(idUsuario, fechaInicial, fechaFinal, idCliente);
	}
	
	public Iterator<LogDeLaConversacion> obtenerConversacionesDeUsusariosAnonimosEntreFechas(String fechaInicial, String fechaFinal, String idCliente) throws ClassNotFoundException{

		DetalleDeConversacionDao detalle = new DetalleDeConversacionDao();

		return detalle.buscarConversacionesDeUsuariosAnonimosEntreFechas(fechaInicial, fechaFinal, idCliente);
	}
	
	public Iterator<LogDeLaConversacion> obtenerLasConversacionesDeTodosLosUsuariosNoAnonimos(String idCliente) throws ClassNotFoundException{

		DetalleDeConversacionDao detalle = new DetalleDeConversacionDao();

		return detalle.buscarLasConversacionesDeTodosLosUsuariosNoAnonimos(idCliente);
	}
	
	public Iterator<LogDeLaConversacion> obtenerTodasLasConversaciones(String idCliente) throws ClassNotFoundException{

		DetalleDeConversacionDao detalle = new DetalleDeConversacionDao();

		return detalle.buscarTodasLasConversaciones(idCliente);
	}
	
	public LogDeLaConversacion obtenerUnaConversacionPorMedioDelId(int idConversacion) throws ClassNotFoundException{

		DetalleDeConversacionDao detalle = new DetalleDeConversacionDao();

		return detalle.buscarUnaConversacionPorId(idConversacion);
	}
	
}
