package com.ncubo.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.ncubo.chatbot.partesDeLaConversacion.Temario;
import com.ncubo.data.Consulta;
import com.ncubo.logicaDeLasConversaciones.TemariosDeUnCliente;

public class ConsultaDao
{
	private final String NOMBRE_TABLA = "estadistica_tema";
	private TemariosDeUnCliente temarios;
	
	private enum atributo
	{
		ID_TEMA("idTema"),
		FECHA("fecha"),
		TEMA("nombreTema"),
		VECES_CONSULTADO("vecesConsultado"),
		TOTAL_CONSULTADO("TotalConsultado"),
		ID_CLIENTE("idCliente");
		
		private String nombre;
		atributo(String nombre)
		{
			this.nombre = nombre;
		}
		
		public String toString()
		{
			return this.nombre;
		}
	}
	
	public ArrayList<Consulta> obtener(String fechaDesde, String fechaHasta, String idCliente) throws ClassNotFoundException, SQLException
	{
		ArrayList<Consulta> consultas = new ArrayList<Consulta>();
		String query = "SELECT " 
				+ NOMBRE_TABLA + "." + atributo.ID_TEMA + ", "
				+ "SUM(" + atributo.VECES_CONSULTADO + ") as '" + atributo.TOTAL_CONSULTADO + "'"
				+ " FROM " + NOMBRE_TABLA
				+ " WHERE " + atributo.FECHA + " BETWEEN '" + fechaDesde + "' AND '" + fechaHasta + "'" 
				+ " AND " + atributo.ID_CLIENTE + " = '" + idCliente +"'"
				+ " group by " + atributo.ID_TEMA + " ;";

		Connection con = ConexionALaDB.getInstance().openConBD();
		ResultSet rs = con.createStatement().executeQuery(query);
		
		while (rs.next())
		{
			consultas.add(new Consulta(
					temarios.buscarTemaPorId(rs.getString(atributo.ID_TEMA.toString())),
					null,
					rs.getInt(atributo.TOTAL_CONSULTADO.toString()),
					idCliente
				));
		}
		
		ConexionALaDB.getInstance().closeConBD();
		return consultas;
	}
	
	public void insertar(Consulta consulta) throws ClassNotFoundException, SQLException
	{
		String queryDatos = "'" + consulta.getTema().getIdTema()+ "'"
				+ ",'" + consulta.getFecha() + "'"
				+ ",'" + consulta.getVecesConsultado() + "'"
				+ ",'" + consulta.getIdCliente() + "'";
		
		String query = "INSERT INTO " + NOMBRE_TABLA
				 + "(" + atributo.ID_TEMA + ","
				 + atributo.FECHA + ","
				 + atributo.VECES_CONSULTADO + ","
				 + atributo.ID_CLIENTE + ")"
				 + " VALUES (" + queryDatos + ") "
				 + " ON DUPLICATE KEY UPDATE " + atributo.VECES_CONSULTADO + " = " +atributo.VECES_CONSULTADO + " + " + consulta.getVecesConsultado();
		
		Connection con = ConexionALaDB.getInstance().openConBD();
		con.createStatement().execute(query);
		ConexionALaDB.getInstance().closeConBD();
	}

	public void establecerTemario(TemariosDeUnCliente temarios) {
		this.temarios = temarios;
	}
	
}