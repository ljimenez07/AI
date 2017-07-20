package com.ncubo.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.ncubo.chatbot.partesDeLaConversacion.ArchivoAdjunto;
import com.ncubo.chatbot.partesDeLaConversacion.Imagen;
import com.ncubo.chatbot.partesDeLaConversacion.Salida;
import com.ncubo.chatbot.partesDeLaConversacion.Sonido;
import com.ncubo.chatbot.partesDeLaConversacion.Vineta;
import com.ncubo.logDeLasConversaciones.Chat;
import com.ncubo.logDeLasConversaciones.Mensaje;
import com.ncubo.logDeLasConversaciones.UsuarioDelChat;

public class ChatDao {

	private final String NOMBRE_TABLA_CHAT = "chat";
	private final String NOMBRE_TABLA_MENSAJE = "mensaje";

	public enum atributosDelChatDao{
		
		ID("idchat"), ID_CONVERSACION("idDeLaConversacion"), ID_USUARIO("idUsuarioQueLoCreo"), 
		FECHA("fechaDeCreacion"), ID_CLIENTE("idClienteCompania"), ACTIVO("activo"), ID_DESTINO("idUsuarioDestino"), ESTADO("estadoDeConexion");
		
		private String nombre;
		
		atributosDelChatDao(String nombre){
			this.nombre = nombre;
		}
		
		public String toString(){
			return this.nombre;
		}
		
	}
	
	public enum atributosDelMensajeDao{
		
		ID("idMensaje"), ID_MENSAJE("idDelMensaje"), ID_USUARIO("idDelUsuario"), ID_CONVERSACION("idDeLaConversacion"), FECHA("fechaDeCreacion"),
		TEXTO("textoDelMensaje"), AUDIO("urlDelAudio"), IMAGEN("urlDeLaImagen"), ARCHIVO("urlDelArchivo"), VINETA("vineta");
		
		private String nombre;
		
		atributosDelMensajeDao(String nombre){
			this.nombre = nombre;
		}
		
		public String toString(){
			return this.nombre;
		}
		
	}

	public ChatDao(){}
	
	public void ingresarUnChat(String idCliente, Chat chat){
		
		String query = "INSERT INTO " + NOMBRE_TABLA_CHAT + "(" + atributosDelChatDao.ID_CONVERSACION+", "+
				atributosDelChatDao.ID_USUARIO+", "+ atributosDelChatDao.FECHA+", "+ atributosDelChatDao.ID_CLIENTE+ ") VALUES (?,?,?,?);";
		
		try {
			Connection con = ConexionALaDB.getInstance().openConBD();
			PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			
			stmt.setString(1, chat.getIdDeLaConversacion());
			stmt.setString(2, chat.getIdUsuarioQuienLoCreo());
			stmt.setTimestamp(3, new Timestamp(chat.getFechaDeCreacion().getTime()));
			stmt.setString(4, idCliente);
			
			stmt.executeUpdate();
				
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			// e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			// e1.printStackTrace();
		}
		
		ConexionALaDB.getInstance().closeConBD();
	}
	
	public ArrayList<Chat> buscarChats(String idUsuario, String idDeLaConversacion){
		
		ArrayList<Chat> resultados = new ArrayList<>();
		String query = "select * from "+NOMBRE_TABLA_CHAT;
		
		if( ! idUsuario.isEmpty() || ! idDeLaConversacion.isEmpty()){
		
			query += " where ";
			if(! idUsuario.isEmpty() && idDeLaConversacion.isEmpty()){
				query += atributosDelChatDao.ID_USUARIO +" = '"+idUsuario+"'";
			}else if(idUsuario.isEmpty() && ! idDeLaConversacion.isEmpty()){
				query += atributosDelChatDao.ID_CONVERSACION +" = '"+idDeLaConversacion+"'";
			}else{
				query += atributosDelChatDao.ID_CONVERSACION +" = '"+idDeLaConversacion+"' and "+atributosDelChatDao.ID_USUARIO +" = '"+idUsuario+"'";
			}
		}
		
		query += ";";
		Connection con = null;
		try {
			con = ConexionALaDB.getInstance().openConBD();
			PreparedStatement stmt = con.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()){
				//System.out.println(rs.getString(atributosDelChatDao.ID_CONVERSACION.toString()) + " "+rs.getTimestamp(atributosDelChatDao.FECHA.toString()));
				Chat chat = new Chat(rs.getString(atributosDelChatDao.ID_CONVERSACION.toString()), 
						rs.getString(atributosDelChatDao.ID_USUARIO.toString()), rs.getString(atributosDelChatDao.ID_CLIENTE.toString()));
				
				Timestamp date = rs.getTimestamp(atributosDelChatDao.FECHA.toString());
				chat.setFechaDeCreacion(new Date(date.getTime()));
				
				//SimpleDateFormat formato = new SimpleDateFormat("yy-MM-dd hh:mm:ss:Ms");
				//System.out.println(formato.format(chat.getFechaDeCreacion()));
				resultados.add(chat);
			}
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		ConexionALaDB.getInstance().closeConBD();
		
		return resultados;
	}
	
	public boolean existeElChat(String idDeLaConversacion, String idUsuario){
		ArrayList<Chat> respuesta = buscarChats(idUsuario, idDeLaConversacion);
		if(respuesta.isEmpty()){
			return false;
		}else{
			return true;
		}
	}
	
	public void insertarUnMesajeALaConversacion(String idDeLaConversacion, Mensaje mensaje){
		
		String query = "INSERT INTO " + NOMBRE_TABLA_MENSAJE + "(" + atributosDelMensajeDao.ID_MENSAJE+", "+
				atributosDelMensajeDao.ID_USUARIO+", "+ atributosDelMensajeDao.ID_CONVERSACION+", "+ atributosDelMensajeDao.FECHA+", "+
				atributosDelMensajeDao.TEXTO+", "+atributosDelMensajeDao.AUDIO+", "+atributosDelMensajeDao.IMAGEN+", "+
				atributosDelMensajeDao.ARCHIVO+ ", "+atributosDelMensajeDao.VINETA+") VALUES (?,?,?,?,?,?,?,?,?);";
		
		Connection con = null;
		try {
			con = ConexionALaDB.getInstance().openConBD();
			PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			
			stmt.setString(1, mensaje.getIdDeMensaje());
			stmt.setString(2, mensaje.getInformacionDelUsuario().getIdDelUsuario());
			stmt.setString(3, idDeLaConversacion);
			stmt.setTimestamp(4, new Timestamp(mensaje.getFechaDelCreacionDelMensaje().getTime()));
			stmt.setString(5, mensaje.getDetalleDelMensaje().getMiTexto());
			stmt.setString(6, mensaje.getDetalleDelMensaje().getMiSonido().url());
			stmt.setString(7, mensaje.getDetalleDelMensaje().obtenerImagen().getURL());
			stmt.setString(8, mensaje.getDetalleDelMensaje().obtenerArchivo().getURL());
			
			String vinetas = " ";
			for(Vineta vineta: mensaje.getDetalleDelMensaje().getMisVinetas()){
				vinetas += vineta.getContenido() +" ";
			}
			
			stmt.setString(9, vinetas);
			
			stmt.executeUpdate();
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ConexionALaDB.getInstance().closeConBD();
	}
	
	public ArrayList<Mensaje> buscarMensajes(String idUsuario, String idDeLaConversacion){
		
		ArrayList<Mensaje> respuesta = new ArrayList<>();
		
		String query = "select * from "+NOMBRE_TABLA_MENSAJE;
		
		if( ! idUsuario.isEmpty() || ! idDeLaConversacion.isEmpty()){
		
			query += " where ";
			if(! idUsuario.isEmpty() && idDeLaConversacion.isEmpty()){
				query += atributosDelMensajeDao.ID_USUARIO +" = '"+idUsuario+"'";
			}else if(idUsuario.isEmpty() && ! idDeLaConversacion.isEmpty()){
				query += atributosDelMensajeDao.ID_CONVERSACION +" = '"+idDeLaConversacion+"'";
			}else{
				query += atributosDelMensajeDao.ID_CONVERSACION +" = '"+idDeLaConversacion+"' and "+atributosDelMensajeDao.ID_USUARIO +" = '"+idUsuario+"'";
			}
		}
		
		query += ";";
		Connection con = null;
		try {
			con = ConexionALaDB.getInstance().openConBD();
			PreparedStatement stmt = con.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();

			while(rs.next()){		
				String idDeMensaje = rs.getString(atributosDelMensajeDao.ID_MENSAJE.toString());
				
				String audio = rs.getString(atributosDelMensajeDao.AUDIO.toString());
				String imagen = rs.getString(atributosDelMensajeDao.IMAGEN.toString());
				String archivo = rs.getString(atributosDelMensajeDao.ARCHIVO.toString());
				String texto = rs.getString(atributosDelMensajeDao.TEXTO.toString());
				String vineta = rs.getString(atributosDelMensajeDao.VINETA.toString());
				
				Salida salida = new Salida();
				salida.escibirUnaImagen(new Imagen(imagen));
				salida.escibirUnArchivo(new ArchivoAdjunto(archivo));
				salida.escribir(texto, new Sonido(audio, ""), null, null, null, new Vineta(vineta));
				
				Timestamp date = rs.getTimestamp(atributosDelMensajeDao.FECHA.toString());
				String usuario = rs.getString(atributosDelMensajeDao.ID_USUARIO.toString());
				Mensaje mensaje = new Mensaje(idDeMensaje, salida, new Date(date.getTime()), new UsuarioDelChat(usuario, ""));

				respuesta.add(mensaje);
			}
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ConexionALaDB.getInstance().closeConBD();
		return respuesta;
	}
	
	public static void main(String[] args) {
		
		ConexionALaDB.getInstance("172.16.60.2", "agentecognitivo", "root", "123456");
		
		ChatDao chatDao = new ChatDao();
		//Chat chat = new Chat("bb4a0ba9_606d_439c_aded_04d3a7e9b202_ncubo_170714043612712", "6c62f6ab-e202-4a30-9a50-56af45755833", "asecnar");
		//Salida salida = new Salida();
		//Mensaje mensaje = new Mensaje("bb4a0ba9_606d_439c_aded_04d3a7e9b202", salida, null, new UsuarioDelChat("6c62f6ab-e202-4a30-9a50-56af45755833", ""));
		
		//chatDao.ingresarUnChat("asecnar", chat);
		//chatDao.buscarChats("", "bb4a0ba9_606d_439c_aded_04d3a7e9b202_ncubo_170714043612712");
		//chatDao.insertarUnMesajeALaConversacion("bb4a0ba9_606d_439c_aded_04d3a7e9b202_ncubo_170714043612712", mensaje);
		chatDao.buscarMensajes("", "caded40b_4daa_49bc_b9ae_58a18cbd63db_ncubo_17072009060676");
	}
	
}
