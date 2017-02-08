package com.ncubo.logicaDeLasConversaciones;

public class InformacionDelCliente {

	private final String nombreDelCliente;
	private final String idDelCliente;
	private final String xmlDelCliente;
	
	public InformacionDelCliente(String nombre, String id, String xml){
		this.nombreDelCliente = nombre;
		this.idDelCliente = id;
		this.xmlDelCliente = xml;
	}
	
	public String getNombreDelCliente() {
		return nombreDelCliente;
	}

	public String getIdDelCliente() {
		return idDelCliente;
	}

	public String getXmlDelCliente() {
		return xmlDelCliente;
	}
}
