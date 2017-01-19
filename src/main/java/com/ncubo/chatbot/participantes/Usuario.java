package com.ncubo.chatbot.participantes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

public class Usuario implements Serializable
{
	private static final long serialVersionUID = -3968177701312019004L;
	public final static String LLAVE_EN_SESSION="user";
/*	private String contextoDeWatsonParaChats = new JSONObject().toString();
	private String contextoDeWatsonParaConocerte = new JSONObject().toString();*/
	private String usuarioId = "";
	private String usuarioNombre = "";
	private String llaveSession = "";
	private boolean estaLogueado = false;
	private boolean tieneTarjetaCredito = false;
	private boolean tieneCuentaAhorros = false;
	private static ArrayList<Usuario> usuariosLogueados = new ArrayList<Usuario>();
	private String idSesion = "";
	private Hashtable<String, String> variablesDeContexto = new Hashtable<String, String>();
	
	protected Usuario(){}
	
	public Usuario(String idSesion)
	{
		this.setIdSesion(idSesion);		
	}

	public boolean getEstaLogueado()
	{
		return this.estaLogueado;
	}
	
	public void hizologinExitosaMente()
	{
		this.estaLogueado = true;
		usuariosLogueados.add( this );
	}

	public String getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(String usuarioId) {
		this.usuarioId = usuarioId;
	}

	public String getUsuarioNombre() {
		return usuarioNombre;
	}

	public void setUsuarioNombre(String usuarioNombre) {
		this.usuarioNombre = usuarioNombre;
	}

	public String getLlaveSession() {
		return llaveSession;
	}

	public void setLlaveSession(String llaveSession) {
		this.llaveSession = llaveSession;
	}

	public String getIdSesion()
	{
		return idSesion;
	}

	public void setIdSesion(String idSesion)
	{
		this.idSesion = idSesion;
	}

	public void setVariablesDeContexto(String variable, String valor){
		this.variablesDeContexto.put(variable, valor);
	}

	public String getVariablesDeContexto(String variable){
		return variablesDeContexto.get(variable);
	}
	
	public boolean isTieneTarjetaCredito() {
		return tieneTarjetaCredito;
	}

	public void setTieneTarjetaCredito(boolean tieneTarjetaCredito) {
		this.tieneTarjetaCredito = tieneTarjetaCredito;
	}

	public boolean isTieneCuentaAhorros() {
		return tieneCuentaAhorros;
	}

	public void setTieneCuentaAhorros(boolean tieneCuentaAhorros) {
		this.tieneCuentaAhorros = tieneCuentaAhorros;
	}
}

