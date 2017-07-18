package com.ncubo.logDeLasConversaciones;

public class UsuarioDelChat {

	private final String idDelUsuario;
	private final String nombreDelUsuario;
	private final boolean esUsuarioAnonimo;

	public static final String USUARIO_ANONIMO = "Anónimo";
	
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

	public String getNombreDelUsuario() {
		return nombreDelUsuario;
	}
	
	public boolean isEsUsuarioAnonimo() {
		return esUsuarioAnonimo;
	}
	
}
