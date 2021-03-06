package com.ncubo.estadisticas;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map.Entry;
import com.ncubo.chatbot.partesDeLaConversacion.Tema;
import com.ncubo.data.Consulta;
import com.ncubo.db.ConsultaDao;
import com.ncubo.db.EstadisticasPorConversacionDao;

public class Estadisticas
{
	private Hashtable<Tema, Integer> detalles;
	
	private ConsultaDao consultaDao;
	private EstadisticasPorConversacionDao estadisticasPorConversacionDao;
	
	public Estadisticas(ConsultaDao consultaDao)
	{
		detalles = new Hashtable<>();
		this.consultaDao = consultaDao;
		this.estadisticasPorConversacionDao = new EstadisticasPorConversacionDao();
	}
	
	public void darSeguimiento(Tema tema)
	{
		if(tema != null){
			Integer valorDelKey = detalles.get(tema);
			detalles.put( tema, (valorDelKey == null) ? 1 : ++valorDelKey);
		}
	}
	
	public Hashtable<Tema, Integer> obtenerDetalles()
	{
		return detalles;
	}
	
	public void guardarEstadisticasEnBaseDeDatos(String idCliente, String idSesion) throws ClassNotFoundException, SQLException
	{
		for(Entry<Tema, Integer> estadistica : detalles.entrySet()){
			consultaDao.insertar( new Consulta(estadistica.getKey(), new Timestamp(new Date().getTime()), estadistica.getValue(), idCliente));
			estadisticasPorConversacionDao.insertar(estadistica.getKey().getNombre(), idSesion, idCliente);
		}
		detalles = new Hashtable<>();
	}

}
