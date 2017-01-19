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
import java.util.Calendar;

import com.ncubo.chatbot.bitacora.HistoricoDeLaConversacion;
import com.ncubo.chatbot.bitacora.LaConversacion;

public class BitacoraDao {

	private final String NOMBRE_TABLA_BITACORA = "bitacora_de_conversaciones";
	
	public enum atributosDeLaBitacoraDao
	{
		ID("id"),
		ID_SESION("id_sesion"),
		ID_USARIO("id_usuario"),
		FECHA("fecha"),
		CONVERSACION("conversacion"),
		HA_SIDO_VERIFICADO("haSidoVerificado");
		
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
	
	public void insertar(String idSesion, String idUsuarioenBA, LaConversacion historicoDeLaConversacion) throws ClassNotFoundException, SQLException{
		
		// insert into bitacora_de_conversaciones (id_sesion, id_usuario, fecha, conversacion, esConversacionEspecifica) values ("1234", "1234", now(), "", 0);
		String query = "INSERT INTO "+NOMBRE_TABLA_BITACORA
				 + "("+atributosDeLaBitacoraDao.ID_SESION+", "+atributosDeLaBitacoraDao.ID_USARIO+", "+atributosDeLaBitacoraDao.FECHA+", "+
				atributosDeLaBitacoraDao.CONVERSACION+") VALUES (?,?,?,?);";

		Connection con = ConexionALaDB.getInstance().openConBD();
		Calendar calendar = Calendar.getInstance();
	    java.sql.Timestamp miFechaActual = new java.sql.Timestamp(calendar.getTime().getTime());

		PreparedStatement stmt = con.prepareStatement(query);
		stmt.setString(1, idSesion);
		stmt.setString(2, idUsuarioenBA);
		stmt.setTimestamp(3, miFechaActual);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
        	ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(historicoDeLaConversacion);
			oos.flush();
	        oos.close();
	        bos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        byte[] data = bos.toByteArray();
		stmt.setObject(4, data);
		
		stmt.executeUpdate();
		
		ConexionALaDB.getInstance().closeConBD();
	}
	
	public LaConversacion buscarUnaConversacion(String idSesion, String fecha) throws ClassNotFoundException, SQLException{
		LaConversacion resultado = null;
		
		String query = "select "+atributosDeLaBitacoraDao.CONVERSACION+" from "+NOMBRE_TABLA_BITACORA+" where "+
				atributosDeLaBitacoraDao.ID_SESION+" = ? and "+atributosDeLaBitacoraDao.FECHA+" = ?;";
		
		Connection con = ConexionALaDB.getInstance().openConBD();
		PreparedStatement stmt = con.prepareStatement(query);
		
		stmt.setString(1, idSesion);
		stmt.setString(2, fecha);
		
		ResultSet rs = stmt.executeQuery();
		
		if( rs.next() )
		{			
			ByteArrayInputStream bais;
            ObjectInputStream ins;
            try {
	            bais = new ByteArrayInputStream(rs.getBytes(atributosDeLaBitacoraDao.CONVERSACION.toString()));
	            ins = new ObjectInputStream(bais);
	            resultado = (LaConversacion)ins.readObject();
	            System.out.println("Object in value :"+resultado.verHistorialDeLaConversacion().get(0).getIdFraseQueUso());
	            ins.close();
            }
            catch (Exception e) {
            	e.printStackTrace();
            }
		}
		
		return resultado;
	}
	
	private int obtenerIdDeLaBitacoraDeUnaConversacion(String idCliente, String idSesion, String fecha) throws ClassNotFoundException, SQLException{
		int resultado = 0;
		
		// SELECT id FROM cognitiveagent2.bitacora_de_conversaciones where id_sesion = '5cf8066d-f023-44e6-9000-99138a2fab6e' and fecha = '2016-12-19 14:36:59';
		
		/*String query = String.format("select %s.%s from %s join %s where %s.%s = %s.%s and %s.%s = ? and %s.%s = ? and %s.%s = ? and %s.%s = 0;", 
				NOMBRE_TABLA_BITACORA, atributosDeLaBitacoraDao.ID, NOMBRE_TABLA_ESTADISTICAS_CONVERSACION, NOMBRE_TABLA_BITACORA, NOMBRE_TABLA_ESTADISTICAS_CONVERSACION, 
				"idConversacion", NOMBRE_TABLA_BITACORA, atributosDeLaBitacoraDao.ID_SESION, NOMBRE_TABLA_ESTADISTICAS_CONVERSACION, atributosDeLasEstadisticasPorConversacionDao.ID_TEMA, NOMBRE_TABLA_BITACORA, 
				atributosDeLaBitacoraDao.FECHA, NOMBRE_TABLA_BITACORA, atributosDeLaBitacoraDao.HA_SIDO_VERIFICADO);*/
		String query = "";
		if(idCliente.isEmpty()){
			query = String.format("select %s FROM %s where %s = ? and %s = ?;", atributosDeLaBitacoraDao.ID, 
					NOMBRE_TABLA_BITACORA, atributosDeLaBitacoraDao.ID_SESION, atributosDeLaBitacoraDao.FECHA);
		}else{
			query = String.format("select %s FROM %s where %s = ? and %s = ? and %s = ?;", atributosDeLaBitacoraDao.ID, 
					NOMBRE_TABLA_BITACORA, atributosDeLaBitacoraDao.ID_SESION, atributosDeLaBitacoraDao.FECHA, atributosDeLaBitacoraDao.ID_USARIO);
		}
		
		Connection con = ConexionALaDB.getInstance().openConBD();
		PreparedStatement stmt = con.prepareStatement(query);
		
		stmt.setString(1, idSesion);
		stmt.setString(2, fecha);
		if( ! idCliente.isEmpty()){
			stmt.setString(3, idCliente);
		}
		
		ResultSet rs = stmt.executeQuery();
		
		if( rs.next() ){
			resultado = rs.getInt(atributosDeLaBitacoraDao.ID.toString());
		}
		
		return resultado;
	}
	
	public String cambiarDeEstadoAVerificadoDeLaConversacion(String idCliente, String idSesion, String fecha) throws ClassNotFoundException, SQLException{
		// update bitacora_de_conversaciones set haSidoVerificado = 1 where id = 126;
		
		try{
			int idBitacoraConversacion = obtenerIdDeLaBitacoraDeUnaConversacion(idCliente, idSesion, fecha);
			String query = String.format("update %s set %s = 1 where id = ?;", NOMBRE_TABLA_BITACORA, atributosDeLaBitacoraDao.HA_SIDO_VERIFICADO);
			
			Connection con = ConexionALaDB.getInstance().openConBD();
			
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, idBitacoraConversacion);
			stmt.executeUpdate();
			
			ConexionALaDB.getInstance().closeConBD();
			
			return "La actualización se hizo exitosamente.";
		}catch(Exception e){
			return "Error al actualizar: "+e.getMessage();
		}
	}
}
