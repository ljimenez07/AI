package com.ncubo.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.ncubo.chatbot.bitacora.Dialogo;
import com.ncubo.chatbot.bitacora.LogDeLaConversacion;
import com.ncubo.chatbot.configuracion.Constantes;


public class DetalleDeConversacionDao {

	private final String ULTIMA_HORA_DEL_DIA = " 23:59:59";

	public void insertarDetalledeLaConversacion(String idCliente, Dialogo conversacion, int idDeLaConversacion, int idDeLaFraseGuardada) throws ClassNotFoundException
	{
		try{
			String queryParaTablaDetalle = "INSERT IGNORE INTO detalle_de_la_conversacion (fechaHora, usuario, frase, fraseId, intencion, entidad, idConversacion, idCliente) VALUES (?,?,?,?,?,?,?,?);";		
			Connection con = ConexionALaDB.getInstance().openConBD();

			Timestamp miFechaActual = new Timestamp(conversacion.getLaFechaEnQueSeCreo().getTime());

			PreparedStatement stmt = con.prepareStatement(queryParaTablaDetalle);

			boolean elClienteDijoAlgo = conversacion.getLoQueDijoElParticipante() !="";
			if (elClienteDijoAlgo && !conversacion.getIntencion().equals(Constantes.INTENCION_PREGUNTAR_POR_OTRA_CONSULTA)) 
			{
				stmt = con.prepareStatement(queryParaTablaDetalle);
				stmt.setTimestamp(1, miFechaActual);
				stmt.setString(2, "Cliente");
				stmt.setString(3, conversacion.getLoQueDijoElParticipante());
				stmt.setString(4, null);
				stmt.setString(5, conversacion.getIntencion());
				stmt.setString(6, conversacion.getEntidades());
				stmt.setInt(7, idDeLaConversacion);
				stmt.setString(8, idCliente);
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
			stmt.setString(8, idCliente);
			
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{

		}



	}

	public Iterator<LogDeLaConversacion> buscarConversacionesEntreFechas(String fechaInicial, String fechaFinal, String idCliente) throws ClassNotFoundException{

		String queryParaObtenerIndices = "SELECT id FROM bitacora_de_conversaciones WHERE FECHA BETWEEN ? and ? and idCliente = '?';";

		try{
			Connection con = ConexionALaDB.getInstance().openConBD();
			PreparedStatement stmt = con.prepareStatement(queryParaObtenerIndices);

			stmt.setString(1, fechaInicial);
			stmt.setString(2, fechaFinal+ULTIMA_HORA_DEL_DIA);
			stmt.setString(3, idCliente);
			
			return recorrerResultadoDelQuery(stmt, con);

		} catch (SQLException e) {
			e.printStackTrace();

		}finally{

		}
		return null;
	}

	public Iterator<LogDeLaConversacion> buscarConversacionesDeHoy() throws ClassNotFoundException{
		Date fecha = new Date( );
		SimpleDateFormat formato = new SimpleDateFormat ("yyyy-MM-dd");

		try{
			String queryParaObtenerIndices = "SELECT id FROM bitacora_de_conversaciones WHERE FECHA BETWEEN ? and ?;";

			Connection con = ConexionALaDB.getInstance().openConBD();
			PreparedStatement stmt = con.prepareStatement(queryParaObtenerIndices);

			stmt.setString(1, formato.format(fecha));
			stmt.setString(2, formato.format(fecha)+ULTIMA_HORA_DEL_DIA);

			return recorrerResultadoDelQuery(stmt, con);
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public  Iterator<LogDeLaConversacion> buscarConversacionesPorUsuario(String idUsuario) throws ClassNotFoundException{

		String queryParaObtenerIndices = "SELECT id FROM bitacora_de_conversaciones WHERE id_usuario = ?;";

		try{
			Connection con = ConexionALaDB.getInstance().openConBD();
			PreparedStatement stmt = con.prepareStatement(queryParaObtenerIndices);

			stmt.setString(1, idUsuario);

			return recorrerResultadoDelQuery(stmt, con);
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public  Iterator<LogDeLaConversacion> buscarConversacionesPorUsuarioAnonimo() throws ClassNotFoundException{

		String queryParaObtenerIndices = "SELECT id FROM bitacora_de_conversaciones WHERE id_usuario = '';";

		try{
			Connection con = ConexionALaDB.getInstance().openConBD();
			PreparedStatement stmt = con.prepareStatement(queryParaObtenerIndices);


			return recorrerResultadoDelQuery(stmt, con);
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Iterator<LogDeLaConversacion> buscarConversacionesDeUsuariosEspecificosEntreFechas(String idUsuario, String fechaInicial, String fechaFinal) throws ClassNotFoundException{

		String queryParaObtenerIndices = "SELECT id FROM bitacora_de_conversaciones WHERE id_usuario = ? and FECHA between ? and ?;";
		try{
			Connection con = ConexionALaDB.getInstance().openConBD();
			PreparedStatement stmt = con.prepareStatement(queryParaObtenerIndices);

			stmt.setString(1, idUsuario);
			stmt.setString(2, fechaInicial);
			stmt.setString(3, fechaFinal+ULTIMA_HORA_DEL_DIA);

			return recorrerResultadoDelQuery(stmt, con);
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Iterator<LogDeLaConversacion> buscarConversacionesDeUsuariosAnonimosEntreFechas(String fechaInicial, String fechaFinal) throws ClassNotFoundException{

		String query = "SELECT id FROM bitacora_de_conversaciones WHERE id_usuario = '' and FECHA between ? and ?;";
		try{
			Connection con = ConexionALaDB.getInstance().openConBD();
			PreparedStatement stmt = con.prepareStatement(query);

			stmt.setString(1, fechaInicial);
			stmt.setString(2, fechaFinal+ULTIMA_HORA_DEL_DIA);

			return recorrerResultadoDelQuery(stmt, con);
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Iterator<LogDeLaConversacion> buscarLasConversacionesDeTodosLosUsuariosNoAnonimos() throws ClassNotFoundException{

		String query = "SELECT id FROM bitacora_de_conversaciones WHERE id_usuario != '';";
		try{
			Connection con = ConexionALaDB.getInstance().openConBD();
			PreparedStatement stmt = con.prepareStatement(query);

			return recorrerResultadoDelQuery(stmt, con);
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Iterator<LogDeLaConversacion> buscarTodasLasConversaciones() throws ClassNotFoundException {

		String query = "SELECT id FROM bitacora_de_conversaciones;";

		Connection con;

		try {
			con = ConexionALaDB.getInstance().openConBD();
			PreparedStatement stmt;
			stmt = con.prepareStatement(query);

			return recorrerResultadoDelQuery(stmt, con);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public LogDeLaConversacion buscarUnaConversacionPorId(int idConversacion) throws ClassNotFoundException{

		String queryParaObtenerEldetalleDeLaConversacion = "SELECT DISTINCT a.fechaHora, a.usuario, a.frase, b.version, a.intencion, a.entidad, (select frase from frases where id = a.fraseId) as FraseFromFrases, (select idfrase from frases where id = a.fraseId) as IdFraseFromFrases FROM detalle_de_la_conversacion a, frases b WHERE idConversacion = ?;";
		
		LogDeLaConversacion conversacion = new LogDeLaConversacion();
		Dialogo dialogo;
		
		try{
			Connection con = ConexionALaDB.getInstance().openConBD();
			PreparedStatement stmt = con.prepareStatement(queryParaObtenerEldetalleDeLaConversacion);

			stmt.setInt(1, idConversacion);

			ResultSet rs = stmt.executeQuery();

			boolean hayDetalle = rs.next();
			if(hayDetalle){
				while (hayDetalle) {
					dialogo =  new Dialogo();
					boolean habloElcliente = rs.getString("usuario").equals("Cliente");

					dialogo.setLaFechaEnQueSeCreo(rs.getTimestamp("fechaHora"));

					if (habloElcliente) {
						dialogo.setLoQueDijoElParticipante(rs.getString("frase"));
						dialogo.setEntidades(rs.getString("entidad"));
					}else{
						boolean laFraseDelFrameworkVieneNula = rs.getString("frase") == null;
						if (laFraseDelFrameworkVieneNula)
							dialogo.setElTextoQueDijoElFramework(rs.getString("FraseFromFrases"));
						else dialogo.setElTextoQueDijoElFramework(rs.getString("frase"));


					}
					dialogo.setIdFraseQueUso(rs.getString("IdFraseFromFrases"));
					dialogo.setVersion(rs.getInt("version"));
					dialogo.setIntencion(rs.getString("intencion"));
					hayDetalle = rs.next();

					conversacion.historico.add(dialogo);
				}

			}

			return conversacion;
		} catch (SQLException e) {
			e.printStackTrace();

		}finally{

		}
		return null;
	}
	private Iterator<LogDeLaConversacion> recorrerResultadoDelQuery(PreparedStatement stmt, Connection con){

		ArrayList<LogDeLaConversacion> conversaciones = new ArrayList<LogDeLaConversacion>();
		LogDeLaConversacion conversacion;
		Dialogo dialogo;
		String queryParaObtenerEldetalleDeLaConversacion = "SELECT DISTINCT a.fechaHora, a.usuario, a.frase, b.version, a.intencion, a.entidad, (select frase from frases where id = a.fraseId) as FraseFromFrases, (select idfrase from frases where id = a.fraseId) as IdFraseFromFrases FROM detalle_de_la_conversacion a, frases b WHERE idConversacion = ?;";

		try{
			ResultSet rs = stmt.executeQuery();

			boolean hayDatos=rs.next();
			while (hayDatos) {

				conversacion = new LogDeLaConversacion();
				stmt = con.prepareStatement(queryParaObtenerEldetalleDeLaConversacion);
				stmt.setInt(1, rs.getInt("id"));
				ResultSet res = stmt.executeQuery();
				boolean hayDetalle = res.next();
				if(hayDetalle){
					while (hayDetalle) {
						dialogo =  new Dialogo();
						boolean habloElcliente = res.getString("usuario").equals("Cliente");

						dialogo.setLaFechaEnQueSeCreo(res.getTimestamp("fechaHora"));

						if (habloElcliente) {
							dialogo.setLoQueDijoElParticipante(res.getString("frase"));
							dialogo.setEntidades(res.getString("entidad"));
						}else{
							boolean laFraseDelFrameworkVieneNula = res.getString("frase") == null;
							if (laFraseDelFrameworkVieneNula)
								dialogo.setElTextoQueDijoElFramework(res.getString("FraseFromFrases"));
							else dialogo.setElTextoQueDijoElFramework(res.getString("frase"));


						}
						dialogo.setIdFraseQueUso(res.getString("IdFraseFromFrases"));
						dialogo.setVersion(res.getInt("version"));
						dialogo.setIntencion(res.getString("intencion"));
						hayDetalle = res.next();

						conversacion.historico.add(dialogo);
					}

					conversaciones.add(conversacion);
				}
				hayDatos=rs.next();
			}

			return conversaciones.listIterator();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return conversaciones.listIterator();
	}

}
