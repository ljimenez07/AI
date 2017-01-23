package com.ncubo.controller;

import java.io.IOException;
import java.sql.SQLException;

import com.ncubo.chatbot.bitacora.LogDeLaConversacion;
import com.ncubo.db.BitacoraDao;


public class ConversacionController {

	public LogDeLaConversacion obtenerUnaConversacion(String idSession, String fechaConHora) throws ClassNotFoundException, SQLException, IOException{
		
		BitacoraDao bitacora = new BitacoraDao();
		LogDeLaConversacion conversacionEspecifica = bitacora.buscarUnaConversacion(idSession, fechaConHora);
	
		return conversacionEspecifica;
	}
}
