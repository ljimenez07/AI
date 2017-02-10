package com.ncubo.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import com.ncubo.chatbot.bitacora.LogDeLaConversacion;
import com.ncubo.db.BitacoraDao;


public class ConversacionController {

	public LogDeLaConversacion obtenerUnaConversacion(String idSession, String fechaConHora, String idCliente) throws ClassNotFoundException, SQLException, IOException{
		
		BitacoraDao bitacora = new BitacoraDao();
		LogDeLaConversacion conversacionEspecifica = bitacora.buscarUnaConversacion(idSession, fechaConHora, idCliente);
	
		return conversacionEspecifica;
	}

	public ArrayList<String> obtenerIdsDeSesionDeBitacoras() throws ClassNotFoundException, SQLException
	{
		BitacoraDao bitacora = new BitacoraDao();
		return bitacora.obtenerIdsDeSesionDeBitacoras();
	}

	public Map<String, ArrayList<String>> obtenerFechasDeBitacorasPorId() throws ClassNotFoundException, SQLException
	{
		BitacoraDao bitacora = new BitacoraDao();
		return bitacora.obtenerFechasDeBitacorasPorId();
	}
}
