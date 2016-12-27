package com.ncubo.evaluador.libraries;

import java.util.HashSet;
import java.util.Set;

public enum Monedas  
{
	CRC("es_CR","Colon","CRC"),
	MXN("es_MX","Peso","$"),
	SVC("es_SV","Colon","¢"),
	HNL("es_HN","Lempira","(L)"),
	NIO("es_NI","Cordoba","(C$)"),
	PAB("es_PA","Balboa","(฿)"),
	GTQ("es_GT","Quetzal","(Q)"),
	COP("es_CO","Peso","($)"),
	ECS("es_EC","Sucre","(S/.)"),
	VEF("es_VE","Bolivar","(Bs.)"),	
	PEN("es_PE","Nuevo sol","(S/.)"),
	BOB("es_BO","Boliviano","(Bs)"),	
	USD("en_US","Dollar","$"),	
	CLP("es_CL","Peso","($)"),
	ARS("es_AR","Peso","($)"),
	PYG("es_PY","Guarani","(₲)"),
	BRL("pt_BR","Real","(R$)"),
	DOP("es_DO","Peso","(RD$)"),
	PRUSD("es_PR","Dollar","($)"),	
	CUP("es_CU","Peso","($)"),
	UYU("es_UY","Peso","($)");
	
	private String localizacion;
	private String simbolo;
	private String nombre;
	
	private static Set<String> monedas = new HashSet<String>();

	private Monedas(String localizacion,String nombre,String simbolo) 
	{
		this.localizacion = localizacion;
		this.simbolo = simbolo;
		this.nombre = nombre;
	}
		
	public String simbolo()
	{
		return simbolo;
	}
	
	public String localizacion()
	{
		return localizacion;
	}
	
	public String nombre()
	{
		return nombre;
	}
	
	public static boolean contieneLaMoneda(String monedaAEvaluar) 
	{
		String aEvaluarUnSensitive = monedaAEvaluar.toUpperCase();
		boolean resultado = monedas.contains(aEvaluarUnSensitive);
		return resultado;
	}

	public static Monedas obtenerTipoDeMonto(Hilera hilera) 
	{
		//TODO El PRUSD esta mal.. pues tiene 5 letras y aqui se usan fijos 3.
		String tipo = hilera.getValor().trim().substring(0,3);
		return Monedas.valueOf(tipo.toUpperCase());
	}

	static
	{
		for (Monedas moneda : Monedas.values()) 
		{
			monedas.add(moneda.name().toUpperCase());
		}
	}
}
