package com.ncubo.logDeLasConversaciones;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import com.ncubo.chatbot.partesDeLaConversacion.Salida;
import com.ncubo.db.ChatDao;

public class Chat {

	private final String idDeLaConversacion;
	private ArrayList<Mensaje> misMensajes;
	private Date fechaDeCreacion;
	private final String idUsuarioQuienLoCreo;
	private Hashtable<String, UsuarioDelChat> usuariosDelChat;
	private final String idDelClienteCompania;
	private ChatDao chatDao;
	private Date fechaDelUltimoMensajeIngresado;
	private int idDeLaDB;

	public Chat(String idConversacion, String idUsuario, String idDelClienteCompania){
		this.fechaDeCreacion = new Date();
		this.idDeLaConversacion = idConversacion;
		this.misMensajes = new ArrayList<>();
		this.idUsuarioQuienLoCreo = idUsuario;
		this.usuariosDelChat = new Hashtable<>();
		this.idDelClienteCompania = idDelClienteCompania;
		this.chatDao = new ChatDao();
		this.fechaDelUltimoMensajeIngresado = new Date();
		this.idDeLaDB = 0;
	}
	
	public Chat(String idConversacion, String idUsuario, String idDelClienteCompania, int idDB){
		this.fechaDeCreacion = new Date();
		this.idDeLaConversacion = idConversacion;
		this.misMensajes = new ArrayList<>();
		this.idUsuarioQuienLoCreo = idUsuario;
		this.usuariosDelChat = new Hashtable<>();
		this.idDelClienteCompania = idDelClienteCompania;
		this.chatDao = new ChatDao();
		this.fechaDelUltimoMensajeIngresado = new Date();
		this.idDeLaDB = idDB;
	}
	
	public boolean existeElUsuarioEnElChat(String idDelUsuario){
		if(! idDelUsuario.isEmpty()){
			return usuariosDelChat.containsKey(idDelUsuario);
		}else{
			return false;
		}
	}
	
	public boolean existeElUusarioYEsAnonimo(String idDelUsuario){
		if(existeElUsuarioEnElChat(idDelUsuario)){
			return usuariosDelChat.get(idDelUsuario).isEsUsuarioAnonimo();
		}else{
			return false;
		}
	}
	
	private boolean hayMensajes(){
		return ! misMensajes.isEmpty();
	}
	
	public void agregarUsuarioAlChat(String idUsuario, String nombre){
		usuariosDelChat.put(idUsuario, new UsuarioDelChat(idUsuario, nombre));
	}
	
	private void agregarMensajeComoVistoAUsuario(String idUsuario, Mensaje mensaje){
		
		if (existeElUsuarioEnElChat(idUsuario)){
			usuariosDelChat.get(idUsuario).agregarUnNuevoMensajeComoVisto(mensaje);
		}
	}
	
	private boolean elMensajeExiste(Mensaje miMensaje){
		
		for(Mensaje mensaje: misMensajes){
			if(mensaje.getIdDeMensaje().equals(miMensaje.getIdDeMensaje())){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean agregarUnMensaje(String idUsuario, String idDeMensaje, Salida mensaje, Date fecha){
		
		if (existeElUsuarioEnElChat(idUsuario)){
			Mensaje mensajeAEnviar = new Mensaje(idDeMensaje, mensaje, fecha, usuariosDelChat.get(idUsuario), idDeLaConversacion);
			
			if( ! elMensajeExiste(mensajeAEnviar)){
				
				this.chatDao.insertarUnMesajeALaConversacion(this.idDeLaConversacion, mensajeAEnviar);
				misMensajes.add(mensajeAEnviar);
				agregarMensajeComoVistoAUsuario(idUsuario, mensajeAEnviar);
				
				this.fechaDelUltimoMensajeIngresado = new Date();
				
				return true;
			}
			
		}
		
		return false;
	}
	
	public String obtenerElIdDelUltimoMensaje(){
		
		if(hayMensajes()){
			return misMensajes.get(misMensajes.size() - 1).getIdDeMensaje();
		}else{
			return "0";
		}
	}
	
	public ArrayList<Mensaje> obtenerLosUltimosMensaajesApartirDeUnIndex(String idUltimoMensajeVisto, String idDelUsuario){
		
		ArrayList<Mensaje> respuesta = new ArrayList<>();
		boolean loEncontro = false;
		
		for(Mensaje mensaje: misMensajes){
			if(loEncontro){
				respuesta.add(mensaje);
				agregarMensajeComoVistoAUsuario(idDelUsuario, mensaje);
			}else{
				if(mensaje.getIdDeMensaje().equals(idUltimoMensajeVisto)){
					loEncontro = true;
				}
			}
		}
		
		return respuesta;
	}
	
	private boolean tieneMensajesNuevos(String idDelUsuario){
		
		if (existeElUsuarioEnElChat(idDelUsuario)){
			if(usuariosDelChat.get(idDelUsuario).obtenerLaCantidadDeMensajesVistos() == misMensajes.size()){
				return false;
			}else{
				return true;
			}
		}
		
		return false;
	}
	
	public String obtenerLaCantidadDeMensajeNoVistosPorElUsuario(String idDelUltimoMensajeVisto, String idDelUsuario){
		
		String respuesta = "0";
		int cantidadDeMensajes = 0;
		
		if(tieneMensajesNuevos(idDelUsuario)){
			cantidadDeMensajes = Math.abs(misMensajes.size() - usuariosDelChat.get(idDelUsuario).obtenerLaCantidadDeMensajesVistos());
		}
		
		if(cantidadDeMensajes > 99){
			respuesta = "99+";
		}else{
			respuesta = cantidadDeMensajes + "";
		}
		
		return respuesta;
	}
	
	public ArrayList<Mensaje> getMisMensajes(String idUsuario) {
		
		for(Mensaje mensaje: misMensajes){
			agregarMensajeComoVistoAUsuario(idUsuario, mensaje);
		}
		
		return misMensajes;
	}

	public void setMisMensajes(ArrayList<Mensaje> misMensajes) {
		this.misMensajes = misMensajes;
	}

	public String getIdDeLaConversacion() {
		return idDeLaConversacion;
	}

	public Date getFechaDeCreacion() {
		return fechaDeCreacion;
	}

	public void setFechaDeCreacion(Date fecha) {
		fechaDeCreacion = fecha;
	}
	
	public String getIdUsuarioQuienLoCreo() {
		return idUsuarioQuienLoCreo;
	}
	
	public String getIdDelClienteCompania() {
		return idDelClienteCompania;
	}
	
	public void actualizarUsuarios(Hashtable<String, UsuarioDelChat> usuarios){
		this.usuariosDelChat = usuarios;
	}
	
	public Hashtable<String, UsuarioDelChat> obtenerUsuarios(){
		return usuariosDelChat;
	}
	
	public Date getFechaDelUltimoMensajeIngresado() {
		return fechaDelUltimoMensajeIngresado;
	}

	public void setFechaDelUltimoMensajeIngresado(Date fechaDelUltimoMensajeIngresado) {
		this.fechaDelUltimoMensajeIngresado = fechaDelUltimoMensajeIngresado;
	}
	
	public int getIdDeLaDB() {
		return idDeLaDB;
	}

	public void setIdDeLaDB(int idDeLaDB) {
		this.idDeLaDB = idDeLaDB;
	}
	
}
