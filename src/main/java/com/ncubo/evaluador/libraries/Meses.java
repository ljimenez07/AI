package com.ncubo.evaluador.libraries;

import com.ncubo.evaluador.interprete.libraries.LanguageException;

public enum Meses 
{
	ENE("ENERO",1), 
	FEB("FEBRERO",2), 
	MAR("MARZO",3), 
	ABR("ABRIL",4), 
	MAY("MAYO",5), 
	JUN("JUNIO",6),
	JUL("JULIO",7),
	AGO("AGOSTO",8),
	SET("SETIEMBRE",9),
	OCT("OCTUBRE",10),
	NOV("NOVIEMBRE",11),
	DIC("DICIEMBRE",12);
	
	private String nombreCompleto;
	private int numero;

	private Meses(String nombreCompleto, int numero) 
	{
		this.nombreCompleto = nombreCompleto;
		this.numero = numero;
	}
	
	public String nombreCompleto()
	{
		return this.nombreCompleto;
	}
	
	public int numero()
	{
		return this.numero;
	}
	
	public static Meses obtenerMes(int numeroBuscado)
	{
		for(Meses mes :  Meses.values())
		{
			if(mes.numero==numeroBuscado)
			{
				return mes;
			}
		}
		throw new LanguageException("numero de mes no encontrado "+numeroBuscado);
	}
	
	public Meses siguienteMes()
	{

		return obtenerMes(this.numero+1);
	}
	
	public Meses siguienteTrimestre()
	{
		if(this.numero==10)
		{
			return ENE;
		}
		else if(this.numero==11)
		{
			return FEB;
		}
		else if(this.numero==12)
		{
			return MAR;
		}
		else
		{
			return obtenerMes(this.numero+3);
		}
	}
	
	private static final String[] NOMBRES = new String[]{Meses.ENE.name(), Meses.FEB.name(), Meses.MAR.name(), Meses.ABR.name(), Meses.MAY.name(), Meses.JUN.name(), Meses.JUL.name(), Meses.AGO.name(), Meses.SET.name(), Meses.OCT.name(), Meses.NOV.name(), Meses.DIC.name()};
	public static boolean contieneElMes(String mesAEvaluar) 
	{
		String mesUnsensitive = mesAEvaluar.toUpperCase();
		return 
			NOMBRES[0].equals(mesUnsensitive) || 
			NOMBRES[1].equals(mesUnsensitive) ||
			NOMBRES[2].equals(mesUnsensitive) ||
			NOMBRES[3].equals(mesUnsensitive) ||
			NOMBRES[4].equals(mesUnsensitive) ||
			NOMBRES[5].equals(mesUnsensitive) ||
			NOMBRES[6].equals(mesUnsensitive) ||
			NOMBRES[7].equals(mesUnsensitive) ||
			NOMBRES[8].equals(mesUnsensitive) ||
			NOMBRES[9].equals(mesUnsensitive) ||
			NOMBRES[10].equals(mesUnsensitive) ||
			NOMBRES[11].equals(mesUnsensitive)
		;
	     
	   
	}
	
	@Override
	public String toString() 
	{
		return "Nombre: " + this.name()+" Nombre Completo: "+this.nombreCompleto+" Numero: "+this.numero;
	}

	public Meses anteriorMes() 
	{
		return obtenerMes(this.numero-1);
	}

}
