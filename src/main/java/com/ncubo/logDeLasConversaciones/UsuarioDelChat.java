package com.ncubo.logDeLasConversaciones;


public class UsuarioDelChat{

	private String idDelUsuario;
	private String nombreDelUsuario;
	private boolean esUsuarioAnonimo;

	public static final String USUARIO_ANONIMO = "An&oacute;nimo";
	
	public UsuarioDelChat(String idUsuario, String nombre){
		this.idDelUsuario = idUsuario;
		
		if(idUsuario.isEmpty() || nombre.isEmpty()){
			esUsuarioAnonimo = true;
			this.nombreDelUsuario = USUARIO_ANONIMO;
		}else{
			esUsuarioAnonimo = false;
			this.nombreDelUsuario = nombre;
		}
	}
	
	
	public String getIdDelUsuario() {
		return idDelUsuario;
	}

	public void setIdDelUsuario(String idDelUsuario) {
		this.idDelUsuario = idDelUsuario;
	}

	public String getNombreDelUsuario() {
		return nombreDelUsuario;
	}

	public void setNombreDelUsuario(String nombreDelUsuario) {
		this.nombreDelUsuario = nombreDelUsuario;
	}

	public boolean isEsUsuarioAnonimo() {
		return esUsuarioAnonimo;
	}

	public void setEsUsuarioAnonimo(boolean esUsuarioAnonimo) {
		this.esUsuarioAnonimo = esUsuarioAnonimo;
	}

	public static String getUsuarioAnonimo() {
		return USUARIO_ANONIMO;
	}
	
}
