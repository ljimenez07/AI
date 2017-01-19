package com.ncubo.controller;

import java.io.IOException;
import java.sql.SQLException;

import com.ncubo.chatbot.bitacora.LaConversacion;
import com.ncubo.db.BitacoraDao;


public class ConversacionController {

	public LaConversacion ConversacionEspecificada(String idSession, String fechaConHora) throws ClassNotFoundException, SQLException, IOException{
		
		BitacoraDao bitacora = new BitacoraDao();
		LaConversacion conversacionEspecifica = bitacora.buscarUnaConversacion(idSession, fechaConHora);
	
			return conversacionEspecifica;
	}
}
