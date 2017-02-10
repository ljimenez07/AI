package com.ncubo.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.ncubo.db.BitacoraDao.atributosDeLaBitacoraDao;

public class EstadisticasPorConversacionDao {

	private final String NOMBRE_TABLA_ESTADISTICAS_CONVERSACION = "estadisticas_por_conversacion";
	private final String NOMBRE_TABLA_BITACORA = "bitacora_de_conversaciones";
	
	public enum atributosDeLasEstadisticasPorConversacionDao
	{
		NOMBRE_TEMA("nombreTema"),
		ID_SESION("idSesion"),
		ID_CLIENTE("idCliente");
		private String nombre;
		atributosDeLasEstadisticasPorConversacionDao(String nombre)
		{
			this.nombre = nombre;
		}
		
		public String toString()
		{
			return this.nombre;
		}
	}
	
	public void insertar(String idTema, String idConversacion, String idCliente) throws ClassNotFoundException, SQLException{
		
		String query = "INSERT INTO "+NOMBRE_TABLA_ESTADISTICAS_CONVERSACION
				 + "("+atributosDeLasEstadisticasPorConversacionDao.NOMBRE_TEMA+", "+atributosDeLasEstadisticasPorConversacionDao.ID_SESION+", "+atributosDeLasEstadisticasPorConversacionDao.ID_CLIENTE+") VALUES (?,?,?);";

		Connection con = ConexionALaDB.getInstance().openConBD();

		PreparedStatement stmt = con.prepareStatement(query);
		stmt.setString(1, idTema);
		stmt.setString(2, idConversacion);
		stmt.setString(3, idCliente);
		
		stmt.executeUpdate();
		
		ConexionALaDB.getInstance().closeConBD();
	}
	
	public ArrayList<String> buscarConversacionesQueNoHanSidoVerificadasPorTema(String nombreTema, String idCliente) throws ClassNotFoundException, SQLException{
		
		// select bitacora_de_conversaciones.idSesion from estadisticas_por_conversacion join bitacora_de_conversaciones 
		// where estadisticas_por_conversacion.idSesion = bitacora_de_conversaciones.idSesion and 
		// estadisticas_por_conversacion.nombreTema = 'quiereTasaDeCambio' and bitacora_de_conversaciones.haSidoVerificado = 0 and estadisticas_por_conversacion.idCliente = 'muni1';
		ArrayList<String> consultas = new ArrayList<String>();
		String query = String.format("select %s.%s from %s join %s where %s.%s = %s.%s and %s.%s = ? and %s.%s = 0 and %s.%s = ?;", 
				NOMBRE_TABLA_BITACORA, atributosDeLaBitacoraDao.ID_SESION, NOMBRE_TABLA_ESTADISTICAS_CONVERSACION, NOMBRE_TABLA_BITACORA,
				NOMBRE_TABLA_ESTADISTICAS_CONVERSACION, atributosDeLasEstadisticasPorConversacionDao.ID_SESION, NOMBRE_TABLA_BITACORA,
				atributosDeLaBitacoraDao.ID_SESION, NOMBRE_TABLA_ESTADISTICAS_CONVERSACION, atributosDeLasEstadisticasPorConversacionDao.NOMBRE_TEMA,
				NOMBRE_TABLA_BITACORA, atributosDeLaBitacoraDao.HA_SIDO_VERIFICADO, NOMBRE_TABLA_ESTADISTICAS_CONVERSACION, atributosDeLasEstadisticasPorConversacionDao.ID_CLIENTE);
		
		Connection con = ConexionALaDB.getInstance().openConBD();
		
		PreparedStatement stmt = con.prepareStatement(query);
		stmt.setString(1, nombreTema);
		stmt.setString(2, idCliente);
		
		ResultSet rs = stmt.executeQuery();
		
		while (rs.next()){
			consultas.add(rs.getString(atributosDeLaBitacoraDao.ID_SESION.toString()));
		}
		
		ConexionALaDB.getInstance().closeConBD();
		return consultas;
	}
	
}
