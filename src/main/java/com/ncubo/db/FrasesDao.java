package com.ncubo.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.ncubo.chatbot.bitacora.Dialogo;

public class FrasesDao {


	public int insertarFrasesDevueltasPorElFramework(Dialogo conversacion) throws ClassNotFoundException
	{
		
		String queryParaNoRepetirFrases = "SELECT id from frases WHERE idfrase = ? and version = ?";
		String queryParaTablaFrases = "INSERT INTO frases (idfrase, version, frase) VALUES (?,?,?);";
		int idDeLaFraseGuardada = 0;
		try{
			Connection con = ConexionALaDB.getInstance().openConBD();

			PreparedStatement stmt = con.prepareStatement(queryParaNoRepetirFrases);

			stmt.setString(1, conversacion.getIdFraseQueUso());
			stmt.setInt(2, conversacion.getVersion());

			ResultSet res = stmt.executeQuery();

			boolean yaExisteEseIdFraseYVersion = res.next();

			if (yaExisteEseIdFraseYVersion)
			{
				return res.getInt(1);
			}

			stmt = con.prepareStatement(queryParaTablaFrases,Statement.RETURN_GENERATED_KEYS);

			stmt.setString(1, conversacion.getIdFraseQueUso());
			stmt.setInt(2, conversacion.getVersion());
			if(conversacion.getElTextoConPlaceholders().isEmpty())
				stmt.setString(3, conversacion.getElTextoQueDijoElFramework());
			else stmt.setString(3, conversacion.getElTextoConPlaceholders());
			
			stmt.executeUpdate();

			ResultSet rs=stmt.getGeneratedKeys(); //obtengo las ultimas llaves generadas

			while(rs.next())
			{ 
				idDeLaFraseGuardada = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally
		{}
		return idDeLaFraseGuardada;

	}

}
