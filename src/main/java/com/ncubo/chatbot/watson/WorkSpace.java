package com.ncubo.chatbot.watson;

public class WorkSpace {

	private String usuarioIBM;
	private String contrasenaIBM;
	private String idIBM;
	private String nombreDelTopico;
	private String nombre;
	private String[] listaDeIntencionesUsadasParaReferenciarAlWorkspace;
	
	public WorkSpace(String usuarioIBM, String contrasenaIBM, String idIBM, String nombreDelTopico, String nombre, String... intenciones) {
		this.usuarioIBM = usuarioIBM;
		this.contrasenaIBM = contrasenaIBM;
		this.idIBM = idIBM;
		this.nombreDelTopico = nombreDelTopico;
		this.nombre = nombre;
		this.listaDeIntencionesUsadasParaReferenciarAlWorkspace = intenciones;
	}

	public boolean tieneLaIntencion(String nombreDeLaIntencion){
		for(int index = 0; index < listaDeIntencionesUsadasParaReferenciarAlWorkspace.length; index ++){
			if(listaDeIntencionesUsadasParaReferenciarAlWorkspace[index].equals(nombreDeLaIntencion)){
				return true;
			}
		}
		return false;
	}
	
	public String getUsuarioIBM() {
		return usuarioIBM;
	}

	public void setUsuarioIBM(String usuarioIBM) {
		this.usuarioIBM = usuarioIBM;
	}

	public String getContrasenaIBM() {
		return contrasenaIBM;
	}

	public void setContrasenaIBM(String contrasenaIBM) {
		this.contrasenaIBM = contrasenaIBM;
	}

	public String getIdIBM() {
		return idIBM;
	}

	public void setIdIBM(String idIBM) {
		this.idIBM = idIBM;
	}

	public String getNombreDelTopico() {
		return nombreDelTopico;
	}

	public void setNombreDelTopico(String nombreDelTopico) {
		this.nombreDelTopico = nombreDelTopico;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
}
