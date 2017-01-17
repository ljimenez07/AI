package com.ncubo.chatbot.bitacora;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.json.JSONArray;
import com.ncubo.db.BitacoraDao;
import com.ncubo.db.EstadisticasPorConversacionDao;

public class HistoricosDeConversaciones {

	private final static Hashtable<String, HistoricosDeConversacion> historicoDeMisConversaciones = new Hashtable<String, HistoricosDeConversacion>();
	
	private BitacoraDao miBitacora;
	private EstadisticasPorConversacionDao estadisticasPorConversacion;
	
	public HistoricosDeConversaciones(){
		miBitacora = new BitacoraDao();
		estadisticasPorConversacion = new EstadisticasPorConversacionDao();
	}
	
	public boolean existeElHistoricoDeLaConversacion(String idSesion){
		return historicoDeMisConversaciones.containsKey(idSesion);
	}
	
	private void agregarUnHistoricoALaConversacion(String idSesion, HistoricosDeConversacion historico){
		synchronized(historicoDeMisConversaciones){
			historicoDeMisConversaciones.put(idSesion, historico);
		}
	}
	
	public void agregarHistorialALaConversacion(String idSesion, HistoricosDeConversacion historico){
		if(! existeElHistoricoDeLaConversacion(idSesion)){
			agregarUnHistoricoALaConversacion(idSesion, historico);
		}
	}
	
	public HistoricosDeConversacion verElHistoricoDeUnaConversacion(String idSesion){
		HistoricosDeConversacion miHistorico = null;
	
		if(existeElHistoricoDeLaConversacion(idSesion)){
			miHistorico = historicoDeMisConversaciones.get(idSesion);
		}else{
			// TODO Sino de la db
		}
		
		return miHistorico;
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
			borrarElHistoricoDeUnaConversacionGeneral(idSesion);
		}
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
