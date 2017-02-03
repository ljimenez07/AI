package com.ncubo.db;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.ncubo.chatbot.bitacora.LogDeLaConversacion;

public class BitacoraDao
{
	
	private final String NOMBRE_TABLA_BITACORA = "bitacora_de_conversaciones";
	
	public enum atributosDeLaBitacoraDao
	{
		ID("id"), ID_SESION("id_sesion"), ID_USARIO("id_usuario"), FECHA("fecha"), CONVERSACION("conversacion"), HA_SIDO_VERIFICADO("haSidoVerificado");
		
		private String nombre;
		
		atributosDeLaBitacoraDao(String nombre)
		{
			this.nombre = nombre;
		}
		
		public String toString()
		{
			return this.nombre;
		}
	}
	
	public int insertar(String idSesion, String idUsuarioenBA, LogDeLaConversacion historicoDeLaConversacion) throws ClassNotFoundException
	{
		
		String query = "INSERT INTO " + NOMBRE_TABLA_BITACORA + "(" + atributosDeLaBitacoraDao.ID_SESION + ", " + atributosDeLaBitacoraDao.ID_USARIO + ", " + atributosDeLaBitacoraDao.FECHA + ", " + atributosDeLaBitacoraDao.CONVERSACION + ") VALUES (?,?,?,?);";
		
		int idConversacion = 0;
		
		try
		{
			Connection con = ConexionALaDB.getInstance().openConBD();
			Calendar calendar = Calendar.getInstance();
			Timestamp miFechaActual = new Timestamp(calendar.getTime().getTime());
			
			PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, idSesion);
			stmt.setString(2, idUsuarioenBA);
			stmt.setTimestamp(3, miFechaActual);
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try
			{
				ObjectOutputStream oos = new ObjectOutputStream(bos);
				oos.writeObject(historicoDeLaConversacion);
				oos.flush();
				oos.close();
				bos.close();
			} catch(IOException e)
			{
				e.printStackTrace();
			}
			byte[] data = bos.toByteArray();
			stmt.setObject(4, data);
			stmt.executeUpdate();
			
			ResultSet rs = stmt.getGeneratedKeys(); // obtengo las ultimas
													// llaves generadas
			while(rs.next())
			{
				idConversacion = rs.getInt(1);
			}
			
			ConexionALaDB.getInstance().closeConBD();
		} catch(SQLException e)
		{
			e.printStackTrace();
		} finally
		{
			
		}
		return idConversacion;
	}
	
	public LogDeLaConversacion buscarUnaConversacion(String idSesion, String fecha) throws ClassNotFoundException, SQLException
	{
		LogDeLaConversacion resultado = null;
		
		String query = "select " + atributosDeLaBitacoraDao.CONVERSACION + " from " + NOMBRE_TABLA_BITACORA + " where " + atributosDeLaBitacoraDao.ID_SESION + " = ? and " + atributosDeLaBitacoraDao.FECHA + " = ?;";
		
		Connection con = ConexionALaDB.getInstance().openConBD();
		PreparedStatement stmt = con.prepareStatement(query);
		
		stmt.setString(1, idSesion);
		stmt.setString(2, fecha);
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next())
		{
			ByteArrayInputStream bais;
			ObjectInputStream ins;
			try
			{
				bais = new ByteArrayInputStream(rs.getBytes(atributosDeLaBitacoraDao.CONVERSACION.toString()));
				ins = new ObjectInputStream(bais);
				resultado = (LogDeLaConversacion) ins.readObject();
				ins.close();
			} catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return resultado;
	}

	public ArrayList<String> obtenerIdsDeSesionDeBitacoras() throws SQLException, ClassNotFoundException
	{
		String query = "SELECT " + atributosDeLaBitacoraDao.ID_SESION + " FROM " + NOMBRE_TABLA_BITACORA + ";";
		
		Connection con = ConexionALaDB.getInstance().openConBD();
		PreparedStatement stmt = con.prepareStatement(query);
		
		ResultSet rs = stmt.executeQuery();
		
		ArrayList<String> respuesta = new ArrayList<>();
		while(rs.next())
		{
			String idSesion = rs.getString(atributosDeLaBitacoraDao.ID_SESION.toString());
			if(!respuesta.contains(idSesion))
			{
				respuesta.add(idSesion);
			}
		}
		return respuesta;
	}
	
	public Map<String, ArrayList<String>> obtenerFechasDeBitacorasPorId() throws ClassNotFoundException, SQLException
	{
		String query = "SELECT " + atributosDeLaBitacoraDao.ID_SESION + ", " + atributosDeLaBitacoraDao.FECHA + " FROM " + NOMBRE_TABLA_BITACORA + ";";
		
		Connection con = ConexionALaDB.getInstance().openConBD();
		PreparedStatement stmt = con.prepareStatement(query);
		
		ResultSet rs = stmt.executeQuery();
		
		Map<String, ArrayList<String>> respuesta = new HashMap<>();
		while(rs.next())
		{
			String idSesion = rs.getString(atributosDeLaBitacoraDao.ID_SESION.toString());
			String fechaHora = rs.getString(atributosDeLaBitacoraDao.FECHA.toString());
			if(respuesta.containsKey(idSesion))
			{
				respuesta.get(idSesion).add(fechaHora);
			}
			else
			{
				ArrayList<String> arreglo = new ArrayList<>();
				arreglo.add(fechaHora);
				respuesta.put(idSesion, arreglo);
			}
		}
		return respuesta;
	}
	
	private int obtenerIdDeLaBitacoraDeUnaConversacion(String idCliente, String idSesion, String fecha) throws ClassNotFoundException, SQLException
	{
		int resultado = 0;
		
		// SELECT id FROM cognitiveagent2.bitacora_de_conversaciones where
		// id_sesion = '5cf8066d-f023-44e6-9000-99138a2fab6e' and fecha =
		// '2016-12-19 14:36:59';
		
		/*
		 * String query = String.
		 * format("select %s.%s from %s join %s where %s.%s = %s.%s and %s.%s = ? and %s.%s = ? and %s.%s = ? and %s.%s = 0;"
		 * , NOMBRE_TABLA_BITACORA, atributosDeLaBitacoraDao.ID,
		 * NOMBRE_TABLA_ESTADISTICAS_CONVERSACION, NOMBRE_TABLA_BITACORA,
		 * NOMBRE_TABLA_ESTADISTICAS_CONVERSACION, "idConversacion",
		 * NOMBRE_TABLA_BITACORA, atributosDeLaBitacoraDao.ID_SESION,
		 * NOMBRE_TABLA_ESTADISTICAS_CONVERSACION,
		 * atributosDeLasEstadisticasPorConversacionDao.ID_TEMA,
		 * NOMBRE_TABLA_BITACORA, atributosDeLaBitacoraDao.FECHA,
		 * NOMBRE_TABLA_BITACORA, atributosDeLaBitacoraDao.HA_SIDO_VERIFICADO);
		 */
		String query = "";
		if(idCliente.isEmpty())
		{
			query = String.format("select %s FROM %s where %s = ? and %s = ?;", atributosDeLaBitacoraDao.ID, NOMBRE_TABLA_BITACORA, atributosDeLaBitacoraDao.ID_SESION, atributosDeLaBitacoraDao.FECHA);
		} else
		{
			query = String.format("select %s FROM %s where %s = ? and %s = ? and %s = ?;", atributosDeLaBitacoraDao.ID, NOMBRE_TABLA_BITACORA, atributosDeLaBitacoraDao.ID_SESION, atributosDeLaBitacoraDao.FECHA, atributosDeLaBitacoraDao.ID_USARIO);
		}
		
		Connection con = ConexionALaDB.getInstance().openConBD();
		PreparedStatement stmt = con.prepareStatement(query);
		
		stmt.setString(1, idSesion);
		stmt.setString(2, fecha);
		if(!idCliente.isEmpty())
		{
			stmt.setString(3, idCliente);
		}
		
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next())
		{
			resultado = rs.getInt(atributosDeLaBitacoraDao.ID.toString());
		}
		
		return resultado;
	}
	
	public String cambiarDeEstadoAVerificadoDeLaConversacion(String idCliente, String idSesion, String fecha) throws ClassNotFoundException, SQLException
	{
		// update bitacora_de_conversaciones set haSidoVerificado = 1 where id =
		// 126;
		
		try
		{
			int idBitacoraConversacion = obtenerIdDeLaBitacoraDeUnaConversacion(idCliente, idSesion, fecha);
			String query = String.format("update %s set %s = 1 where id = ?;", NOMBRE_TABLA_BITACORA, atributosDeLaBitacoraDao.HA_SIDO_VERIFICADO);
			
			Connection con = ConexionALaDB.getInstance().openConBD();
			
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, idBitacoraConversacion);
			stmt.executeUpdate();
			
			ConexionALaDB.getInstance().closeConBD();
			
			return "La actualización se hizo exitosamente.";
		} catch(Exception e)
		{
			return "Error al actualizar: " + e.getMessage();
		}
	}
}
