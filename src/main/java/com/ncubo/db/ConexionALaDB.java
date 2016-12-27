package com.ncubo.db;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;

public class ConexionALaDB {

	private static ConexionALaDB miConexion = null;
	private Connection conector;
	private String urlDeLaDB;
	private String nombreDeLaBD;
	private String usuarioDeLaBD;
	private String claveDeLaBD;
	
	private ConexionALaDB(String url, String nombre, String usuario, String clave){
		this.urlDeLaDB = url;
		this.nombreDeLaBD = nombre;
		this.usuarioDeLaBD = usuario;
		this.claveDeLaBD = clave;
	}
	
	public static ConexionALaDB getInstance(String url, String nombre, String usuario, String clave){
		if(miConexion == null){
			miConexion = new ConexionALaDB(url, nombre, usuario, clave);
		}
		return miConexion;
	}
	
	public static ConexionALaDB getInstance(){
		return miConexion;
	}
	
	public Connection openConBD() throws SQLException, ClassNotFoundException{
		Class.forName("com.mysql.jdbc.Driver");
		conector = (Connection) DriverManager.getConnection
				("jdbc:mysql://"+ urlDeLaDB +"/" + nombreDeLaBD, usuarioDeLaBD, claveDeLaBD);
		return conector;
	}
	
	public void closeConBD() throws SQLException{
		conector.close();
	}
}
