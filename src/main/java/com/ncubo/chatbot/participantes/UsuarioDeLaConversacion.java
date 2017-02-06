package com.ncubo.chatbot.participantes;

public class UsuarioDeLaConversacion
{

	private String usuarioId = "";
	private String usuarioNombre = "";
	private String llaveSession = "";
	private boolean estaLogueado = false;
	private String idSesion = "";
	
	public UsuarioDeLaConversacion(String usuario, String nombre, String llave, boolean estaLogueado, String idSesion){
		this.usuarioId = usuario;
		this.usuarioNombre = nombre;
		this.llaveSession = llave;
		this.estaLogueado = estaLogueado;
		this.idSesion = idSesion;
	}
	
	public UsuarioDeLaConversacion(String idSesion)
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
}

