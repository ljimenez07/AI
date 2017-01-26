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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import com.ncubo.chatbot.bitacora.Dialogo;
import com.ncubo.chatbot.bitacora.LogDeLaConversacion;

public class BitacoraDao {

	private final String NOMBRE_TABLA_BITACORA = "bitacora_de_conversaciones";
	private final String ULTIMA_HORA_DEL_DIA = " 23:59:59";
	
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
	
	public int insertar(String idSesion, String idUsuarioenBA, LogDeLaConversacion historicoDeLaConversacion) throws ClassNotFoundException
	{
		
		String query = "INSERT INTO "+NOMBRE_TABLA_BITACORA
				+ "("+atributosDeLaBitacoraDao.ID_SESION+", "+atributosDeLaBitacoraDao.ID_USARIO+", "+atributosDeLaBitacoraDao.FECHA+", "+
				atributosDeLaBitacoraDao.CONVERSACION+") VALUES (?,?,?,?);";

		int idConversacion = 0;

		try{
			Connection con = ConexionALaDB.getInstance().openConBD();
			Calendar calendar = Calendar.getInstance();
			Timestamp miFechaActual = new Timestamp(calendar.getTime().getTime());

			PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
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
				e.printStackTrace();
			}
			byte[] data = bos.toByteArray();
			stmt.setObject(4, data);
			stmt.executeUpdate();


			ResultSet rs=stmt.getGeneratedKeys(); //obtengo las ultimas llaves generadas
			while(rs.next()){ 
				idConversacion = rs.getInt(1);
			}

			ConexionALaDB.getInstance().closeConBD();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{

		}
		return idConversacion;
	}
	
	public LogDeLaConversacion buscarUnaConversacion(String idSesion, String fecha) throws ClassNotFoundException, SQLException{
		LogDeLaConversacion resultado = null;
		
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
	            resultado = (LogDeLaConversacion)ins.readObject();
	            System.out.println("Object in value :"+resultado.verHistorialDeLaConversacion().get(0).getIdFraseQueUso());
	            ins.close();
            }
            catch (Exception e) {
            	e.printStackTrace();
            }
		}
		
		return resultado;
	}
	
	public Iterator<LogDeLaConversacion> buscarConversacionesDeHoy() throws ClassNotFoundException{
		Date fecha = new Date( );
		SimpleDateFormat formato = new SimpleDateFormat ("yyyy-MM-dd");
		
		try{
		String query = "select "+atributosDeLaBitacoraDao.CONVERSACION+" from "+NOMBRE_TABLA_BITACORA+" where "+
				atributosDeLaBitacoraDao.FECHA+" between ? and ?;";

		Connection con = ConexionALaDB.getInstance().openConBD();
		PreparedStatement stmt = con.prepareStatement(query);

		stmt.setString(1, formato.format(fecha));
		stmt.setString(2, formato.format(fecha)+ULTIMA_HORA_DEL_DIA);

		return recorrerResultadoDelQuery(stmt);
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public Iterator<LogDeLaConversacion> buscarConversacionesEntreFechas(String fechaInicial, String fechaFinal) throws ClassNotFoundException{
		
		String query = "select "+atributosDeLaBitacoraDao.CONVERSACION+" from "+NOMBRE_TABLA_BITACORA+" where "+
				atributosDeLaBitacoraDao.FECHA+" between ? and ?;";
		
		try{
		Connection con = ConexionALaDB.getInstance().openConBD();
		PreparedStatement stmt = con.prepareStatement(query);
		
		stmt.setString(1, fechaInicial);
		stmt.setString(2, fechaFinal+ULTIMA_HORA_DEL_DIA);
		
		return recorrerResultadoDelQuery(stmt);
	}catch(SQLException e) {
		e.printStackTrace();
	}
	return null;
	}
	
	public  Iterator<LogDeLaConversacion> buscarConversacionesPorUsuario(String idUsuario) throws ClassNotFoundException{
		
		String query = "select "+atributosDeLaBitacoraDao.CONVERSACION+" from "+NOMBRE_TABLA_BITACORA+" where "+
				atributosDeLaBitacoraDao.ID_USARIO+" = ?;";
		
		try{
		Connection con = ConexionALaDB.getInstance().openConBD();
		PreparedStatement stmt = con.prepareStatement(query);
		
		stmt.setString(1, idUsuario);
		
		return recorrerResultadoDelQuery(stmt);
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public  Iterator<LogDeLaConversacion> buscarConversacionesPorUsuarioAnonimo() throws ClassNotFoundException{
		
		String query = "select "+atributosDeLaBitacoraDao.CONVERSACION+" from "+NOMBRE_TABLA_BITACORA+" where "+
				atributosDeLaBitacoraDao.ID_USARIO+" = ?;";
		
		try{
		Connection con = ConexionALaDB.getInstance().openConBD();
		PreparedStatement stmt = con.prepareStatement(query);
		
		stmt.setString(1, "");
		
		return recorrerResultadoDelQuery(stmt);
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Iterator<LogDeLaConversacion> buscarConversacionesDeUsuariosEspecificosEntreFechas(String idUsuario, String fechaInicial, String fechaFinal) throws ClassNotFoundException{
		
		String query = "select "+atributosDeLaBitacoraDao.CONVERSACION+" from "+NOMBRE_TABLA_BITACORA+" where "+
				atributosDeLaBitacoraDao.ID_USARIO+" = ?"+" and "+
				atributosDeLaBitacoraDao.FECHA+" between ? and ?;";
		try{
		Connection con = ConexionALaDB.getInstance().openConBD();
		PreparedStatement stmt = con.prepareStatement(query);
		
		stmt.setString(1, idUsuario);
		stmt.setString(2, fechaInicial);
		stmt.setString(3, fechaFinal+ULTIMA_HORA_DEL_DIA);
		
		return recorrerResultadoDelQuery(stmt);
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Iterator<LogDeLaConversacion> buscarConversacionesDeUsuariosAnonimosEntreFechas(String fechaInicial, String fechaFinal) throws ClassNotFoundException{
		
		String query = "select "+atributosDeLaBitacoraDao.CONVERSACION+" from "+NOMBRE_TABLA_BITACORA+" where "+
				atributosDeLaBitacoraDao.ID_USARIO+" = ''"+" and "+
				atributosDeLaBitacoraDao.FECHA+" between ? and ?;";
		try{
		Connection con = ConexionALaDB.getInstance().openConBD();
		PreparedStatement stmt = con.prepareStatement(query);
		
		stmt.setString(1, fechaInicial);
		stmt.setString(2, fechaFinal+ULTIMA_HORA_DEL_DIA);
		
		return recorrerResultadoDelQuery(stmt);
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Iterator<LogDeLaConversacion> buscarLasConversacionesDeTodosLosUsuariosNoAnonimos() throws ClassNotFoundException{
		
		String query = "select "+atributosDeLaBitacoraDao.CONVERSACION+" from "+NOMBRE_TABLA_BITACORA+" where "+
				atributosDeLaBitacoraDao.ID_USARIO+" != '';";
		try{
		Connection con = ConexionALaDB.getInstance().openConBD();
		PreparedStatement stmt = con.prepareStatement(query);
				
		return recorrerResultadoDelQuery(stmt);
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Iterator<LogDeLaConversacion> buscarTodasLasConversaciones() throws ClassNotFoundException {

		String query = "select "+atributosDeLaBitacoraDao.CONVERSACION+" from "+NOMBRE_TABLA_BITACORA+" ;";

		Connection con;
		
		try {
			con = ConexionALaDB.getInstance().openConBD();
			PreparedStatement stmt;
			stmt = con.prepareStatement(query);

			return recorrerResultadoDelQuery(stmt);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Iterator<LogDeLaConversacion> recorrerResultadoDelQuery(PreparedStatement stmt) throws SQLException{
		LogDeLaConversacion resultado = null;
		ArrayList<LogDeLaConversacion> conversaciones = new ArrayList<LogDeLaConversacion>();
		
		ResultSet rs = stmt.executeQuery();
		
		boolean hayDatos=rs.next();
		
		while( hayDatos )
		{
			ByteArrayInputStream bais;
			ObjectInputStream ins;
			try {
				bais = new ByteArrayInputStream(rs.getBytes(atributosDeLaBitacoraDao.CONVERSACION.toString()));
				ins = new ObjectInputStream(bais);
				resultado = (LogDeLaConversacion)ins.readObject();
				conversaciones.add(resultado);
				ins.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			hayDatos=rs.next();
		}
		return conversaciones.listIterator();
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
