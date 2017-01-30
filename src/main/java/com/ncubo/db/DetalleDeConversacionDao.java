package com.ncubo.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.ncubo.chatbot.bitacora.Dialogo;

public class DetalleDeConversacionDao {

	
	public void insertarDetalledeLaConversacion(Dialogo conversacion, int idDeLaConversacion, int idDeLaFraseGuardada) throws ClassNotFoundException
	{
		try{
		String queryParaTablaDetalle = "INSERT IGNORE INTO detalle_de_la_conversacion (fechaHora, usuario, frase, fraseId, intencion, entidad, idConversacion) VALUES (?,?,?,?,?,?,?);";		
		Connection con = ConexionALaDB.getInstance().openConBD();
	
		Timestamp miFechaActual = new Timestamp(conversacion.getLaFechaEnQueSeCreo().getTime());

		PreparedStatement stmt = con.prepareStatement(queryParaTablaDetalle);
		
		boolean elClienteDijoAlgo = conversacion.getLoQueDijoElParticipante() !="";
		if (elClienteDijoAlgo) 
		{
			stmt = con.prepareStatement(queryParaTablaDetalle);
			stmt.setTimestamp(1, miFechaActual);
			stmt.setString(2, "Cliente");
			stmt.setString(3, conversacion.getLoQueDijoElParticipante());
			stmt.setString(4, null);
			stmt.setString(5, conversacion.getIntencion());
			stmt.setString(6, conversacion.getEntidades());
			stmt.setInt(7, idDeLaConversacion);
			stmt.executeUpdate();
		}
		
		stmt = con.prepareStatement(queryParaTablaDetalle);
		stmt.setTimestamp(1, miFechaActual);
		stmt.setString(2, "Chatbot");
		
		if(conversacion.getElTextoConPlaceholders().isEmpty())
			stmt.setString(3, null);
		else stmt.setString(3, conversacion.getElTextoQueDijoElFramework());
		stmt.setInt(4, idDeLaFraseGuardada);
		stmt.setString(5, null);
		stmt.setString(6, null);
		stmt.setInt(7, idDeLaConversacion);
		
		stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			
		}	
	}
}
