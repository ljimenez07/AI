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
		ID_TEMA("idTema"),
		ID_CONVERSACION("idConversacion");
		
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
	
	public void insertar(String idTema, String idConversacion) throws ClassNotFoundException, SQLException{
		
		String query = "INSERT INTO "+NOMBRE_TABLA_ESTADISTICAS_CONVERSACION
				 + "("+atributosDeLasEstadisticasPorConversacionDao.ID_TEMA+", "+atributosDeLasEstadisticasPorConversacionDao.ID_CONVERSACION+") VALUES (?,?);";

		Connection con = ConexionALaDB.getInstance().openConBD();

		PreparedStatement stmt = con.prepareStatement(query);
		stmt.setString(1, idTema);
		stmt.setString(2, idConversacion);
		
		stmt.executeUpdate();
		
		ConexionALaDB.getInstance().closeConBD();
	}
	
	public ArrayList<String> buscarConversacionesQueNoHanSidoVerificadasPorTema(String idTema) throws ClassNotFoundException, SQLException{
		
		// select bitacora_de_conversaciones.id_sesion from estadisticas_por_conversacion join bitacora_de_conversaciones 
		// where estadisticas_por_conversacion.idConversacion = bitacora_de_conversaciones.id_sesion and 
		// estadisticas_por_conversacion.idTema = 'quiereTasaDeCambio' and bitacora_de_conversaciones.haSidoVerificado = 0;
		ArrayList<String> consultas = new ArrayList<String>();
		String query = String.format("select %s.%s from %s join %s where %s.%s = %s.%s and %s.%s = ? and %s.%s = 0;", 
				NOMBRE_TABLA_BITACORA, atributosDeLaBitacoraDao.ID_SESION, NOMBRE_TABLA_ESTADISTICAS_CONVERSACION, NOMBRE_TABLA_BITACORA,
				NOMBRE_TABLA_ESTADISTICAS_CONVERSACION, atributosDeLasEstadisticasPorConversacionDao.ID_CONVERSACION, NOMBRE_TABLA_BITACORA,
				atributosDeLaBitacoraDao.ID_SESION, NOMBRE_TABLA_ESTADISTICAS_CONVERSACION, atributosDeLasEstadisticasPorConversacionDao.ID_TEMA,
				NOMBRE_TABLA_BITACORA, atributosDeLaBitacoraDao.HA_SIDO_VERIFICADO);
		
		Connection con = ConexionALaDB.getInstance().openConBD();
		
		PreparedStatement stmt = con.prepareStatement(query);
		stmt.setString(1, idTema);
		
		ResultSet rs = stmt.executeQuery();
		
		while (rs.next()){
			consultas.add(rs.getString(atributosDeLaBitacoraDao.ID_SESION.toString()));
		}
		
		ConexionALaDB.getInstance().closeConBD();
		return consultas;
	}
	
}
