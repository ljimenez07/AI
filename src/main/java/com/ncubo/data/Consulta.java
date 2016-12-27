package com.ncubo.data;

import java.sql.Timestamp;

import com.ncubo.chatbot.partesDeLaConversacion.Tema;

public class Consulta
{
	private Tema tema;
	private Timestamp fecha;
	private int vecesConsultado;
	
	public Consulta(){}
	
	public Consulta(Tema tema, Timestamp fecha)
	{
		this.tema = tema;
		this.fecha = fecha;
		this.vecesConsultado = 1;
	}
	
	public Consulta(Tema tema, Timestamp fecha, int vecesConsultado)
	{
		this.tema = tema;
		this.fecha = fecha;
		this.vecesConsultado = vecesConsultado;
	}

	public Tema getTema()
	{
		return tema;
	}
	
	public void setTema(Tema tema)
	{
		this.tema = tema;
	}
	
	public Timestamp getFecha()
	{
		return fecha;
	}
	
	public void setFecha(Timestamp fecha)
	{
		this.fecha = fecha;
	}
	
	public int getVecesConsultado()
	{
		return vecesConsultado;
	}
	
	public void setVecesConsultado(int vecesConsultado)
	{
		this.vecesConsultado = vecesConsultado;
	}

}
