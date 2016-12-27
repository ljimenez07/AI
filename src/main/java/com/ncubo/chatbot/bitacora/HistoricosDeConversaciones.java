package com.ncubo.chatbot.bitacora;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ncubo.db.BitacoraDao;
import com.ncubo.db.ConexionALaDB;
import com.ncubo.db.EstadisticasPorConversacionDao;

public class HistoricosDeConversaciones {

	private final static Hashtable<String, HistoricoDeLaConversacion> historicoDeMisConversaciones = new Hashtable<String, HistoricoDeLaConversacion>();
	private final static Hashtable<String, HistoricoDeLaConversacion> historicoDeMisConversacionesEspecificas = new Hashtable<String, HistoricoDeLaConversacion>();
	private BitacoraDao miBitacora;
	private EstadisticasPorConversacionDao estadisticasPorConversacion;
	
	public HistoricosDeConversaciones(){
		miBitacora = new BitacoraDao();
		estadisticasPorConversacion = new EstadisticasPorConversacionDao();
	}
	
	public boolean existeElHistoricoDeLaConversacion(String idSesion){
		return historicoDeMisConversaciones.containsKey(idSesion);
	}
	
	private void agregarUnHistoricoALaConversacion(String idSesion){
		synchronized(historicoDeMisConversaciones){
			historicoDeMisConversaciones.put(idSesion, new HistoricoDeLaConversacion());
		}
	}
	
	public void agregarHistorialALaConversacion(String idSesion, String loQueDijoElCliente, String loQueDijoElAgente){
		if(! existeElHistoricoDeLaConversacion(idSesion)){
			agregarUnHistoricoALaConversacion(idSesion);
		}
		historicoDeMisConversaciones.get(idSesion).agregarHistorico(loQueDijoElCliente, loQueDijoElAgente);
	}
	
	public String verElHistoricoDeUnaConversacion(String idSesion){
		String miHistorico = "";
		JSONObject jsonObject = new JSONObject();
		
		if(existeElHistoricoDeLaConversacion(idSesion)){
			miHistorico = historicoDeMisConversaciones.get(idSesion).verMiHistorico();
		}
		
		String miHistoricoEspecifico = verElHistoricoDeUnaConversacionEspecifica(idSesion);
		try {
			jsonObject.put("Chat General", miHistorico);
			jsonObject.put("Conocerte", miHistoricoEspecifico);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jsonObject.toString();
	}
	
	private String borrarElHistoricoDeUnaConversacionGeneral(String idSesion){
		String resultado = "El historico para la conversacion "+idSesion+" no existe";
		if(existeElHistoricoDeLaConversacion(idSesion)){
			synchronized(historicoDeMisConversaciones){
				historicoDeMisConversaciones.remove(idSesion);
				resultado = "Se borro exitosamente el historico de la conversacion: "+idSesion;
			}
		}
		return resultado;
	}
	
	public void borrarElHistoricoDeUnaConversacionPorCliente(ArrayList<String> idsSesiones, String idCliente){
		for (String idSesion: idsSesiones){
			borrarElHistoricoDeUnaConversacion(idSesion, idCliente);
		}
	}
	
	public void borrarElHistoricoDeUnaConversacion(String idSesion, String idCliente){
		
		if(! idSesion.isEmpty()){
			String miHistorico = verElHistoricoDeUnaConversacion(idSesion);
			String miHistoricoEspecifica = verElHistoricoDeUnaConversacionEspecifica(idSesion);
			
			JSONObject jsonObject = new JSONObject();
			
			try {
				jsonObject.put("Chat General", miHistorico);
				jsonObject.put("Conocerte", miHistoricoEspecifica);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if(guardarUnaConversacionEnLaDB(idSesion, idCliente, jsonObject.toString())){
				borrarElHistoricoDeUnaConversacionGeneral(idSesion);
				borrarElHistoricoDeUnaConversacionEspecifica(idSesion);
			}
		}
	}
	
	private boolean guardarUnaConversacionEnLaDB(String idSesion, String idCliente, String miHistorico){
		try {
			miBitacora.insertar(idSesion, idCliente, miHistorico);
			return true;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	// Conversacion Especifica
	private boolean existeElHistoricoDeLaConversacionEspecifica(String idSesion){
		return historicoDeMisConversacionesEspecificas.containsKey(idSesion);
	}
	
	private void agregarUnHistoricoALaConversacionEspecifica(String idSesion){
		synchronized(historicoDeMisConversacionesEspecificas){
			historicoDeMisConversacionesEspecificas.put(idSesion, new HistoricoDeLaConversacion());
		}
	}
	
	public void agregarHistorialALaConversacionEspecifica(String idSesion, String loQueDijoElCliente, String loQueDijoElAgente){
		if(! existeElHistoricoDeLaConversacionEspecifica(idSesion)){
			agregarUnHistoricoALaConversacionEspecifica(idSesion);
		}
		historicoDeMisConversacionesEspecificas.get(idSesion).agregarHistorico(loQueDijoElCliente, loQueDijoElAgente);
	}
	
	public String verElHistoricoDeUnaConversacionEspecifica(String idSesion){
		String resultado = "";
		if(existeElHistoricoDeLaConversacionEspecifica(idSesion)){
			resultado = historicoDeMisConversacionesEspecificas.get(idSesion).verMiHistorico();
		}
		return resultado;
	}
	
	private String borrarElHistoricoDeUnaConversacionEspecifica(String idSesion){
		String resultado = "El historico para la conversacion especifica "+idSesion+" no existe";
		if(existeElHistoricoDeLaConversacionEspecifica(idSesion)){
			synchronized(historicoDeMisConversacionesEspecificas){
				historicoDeMisConversacionesEspecificas.remove(idSesion);
				resultado = "Se borro exitosamente el historico de la conversacion especifica: "+idSesion;
			}
		}
		return resultado;
	}
	
	public BitacoraDao obtenerMiBitacoraDeBD(){
		return miBitacora;
	}
	
	public String buscarConversacionesQueNoHanSidoVerificadasPorTema(String idTema) throws ClassNotFoundException, SQLException{
		JSONArray resultado = new JSONArray();

		ArrayList<String> idSesiones = estadisticasPorConversacion.buscarConversacionesQueNoHanSidoVerificadasPorTema(idTema);
		for (String idSesion: idSesiones){
			resultado.put(idSesion);
		}
		
		return resultado.toString();
	}
	
	public String cambiarDeEstadoAVerificadoDeLaConversacion(String idCliente, String idSesion, String fecha) throws ClassNotFoundException, SQLException{
		return miBitacora.cambiarDeEstadoAVerificadoDeLaConversacion(idCliente, idSesion, fecha);
	}
}
