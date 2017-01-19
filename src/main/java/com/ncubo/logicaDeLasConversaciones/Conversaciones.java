package com.ncubo.logicaDeLasConversaciones;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import com.ncubo.chatbot.audiosXML.AudiosXML;
import com.ncubo.chatbot.bitacora.HistoricosDeConversaciones;
import com.ncubo.chatbot.bitacora.LaConversacion;
import com.ncubo.chatbot.exceptiones.ChatException;
import com.ncubo.chatbot.partesDeLaConversacion.Salida;
import com.ncubo.chatbot.partesDeLaConversacion.Temario;
import com.ncubo.chatbot.participantes.AgenteDeLaConversacion;
import com.ncubo.chatbot.participantes.Cliente;
import com.ncubo.chatbot.participantes.Usuario;
import com.ncubo.chatbot.watson.TextToSpeechWatson;
import com.ncubo.conectores.Conectores;
import com.ncubo.db.ConsultaDao;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Conversaciones {

	private final static Hashtable<String, Conversacion> misConversaciones = new Hashtable<String, Conversacion>();
	private final static Hashtable<String, Cliente> misClientes = new Hashtable<String, Cliente>();
	private static Temario miTemario;
	private final Semaphore semaphore = new Semaphore(1);
	private ConsultaDao consultaDao;
	private final Conectores misConectores = new Conectores();
	private HistoricosDeConversaciones historicoDeConversaciones;
	private HiloParaBorrarConversacionesInactivas hiloParaBorrarConversacionesInactivas;
	
	public Conversaciones(){
		historicoDeConversaciones = new HistoricosDeConversaciones();
		hiloParaBorrarConversacionesInactivas = new HiloParaBorrarConversacionesInactivas();
		hiloParaBorrarConversacionesInactivas.start();
	}
	
	private String crearUnaNuevoConversacion(Usuario usuario, AgenteDeLaConversacion agente) throws Exception{
		
		String resultado = "";
		Cliente cliente = null;
		
		if(usuario.getEstaLogueado()){
			if(existeElCliente(usuario.getUsuarioId())){
				cliente = misClientes.get(usuario.getUsuarioId());
			}else{
				cliente = new Cliente(usuario.getUsuarioNombre(), usuario.getUsuarioId(), misConectores);
				synchronized(misClientes){
					misClientes.put(cliente.getMiId(), cliente);
				}
			}
			cliente.agregarIdsDeSesiones(usuario.getIdSesion());
			
			synchronized(misConversaciones){
				if(existeLaConversacion(usuario.getIdSesion())){
					Conversacion coversacion = misConversaciones.get(usuario.getIdSesion());
					coversacion.cambiarParticipante(cliente);
					misConversaciones.put(usuario.getIdSesion(), coversacion);
				}else{
					Conversacion coversacion = new Conversacion(miTemario, cliente, consultaDao, agente);
					misConversaciones.put(usuario.getIdSesion(), coversacion);
				}
			}
			
			resultado = "La nueva conversacion se creo exitosamente.";
			System.out.println(resultado);
		}else{
			if (! usuario.getIdSesion().equals("")){
				if( ! existeLaConversacion(usuario.getIdSesion())){
					cliente = new Cliente(misConectores);
					Conversacion coversacion = new Conversacion(miTemario, cliente, consultaDao, agente);
					synchronized(misConversaciones){
						misConversaciones.put(usuario.getIdSesion(), coversacion);
					}
					resultado = "La nueva conversacion se creo exitosamente.";
					System.out.println(resultado);
				}
			}else{
				resultado = "La conversacion NO pudo ser creada";
				throw new ChatException(resultado);
			}
		}
		
		return resultado;
	}
	
	public ArrayList<Salida> conversarConElAgente(Usuario cliente, String textoDelCliente, AgenteDeLaConversacion agente) throws Exception{
		ArrayList<Salida> resultado = null;
		System.out.println("Coversar con "+cliente.getIdSesion());
		
		if( cliente.getUsuarioId().isEmpty() && cliente.getIdSesion().isEmpty() || (cliente.getUsuarioId().isEmpty() && cliente.getEstaLogueado())){ // Esta logueado
			throw new ChatException("No se puede chatear porque no existe usuario ni id de sesion");
		}
		
		if(cliente.getEstaLogueado()){ // Esta logueado
			// Verificar si ya el usuario existe
			if(existeElCliente(cliente.getUsuarioId()) && existeLaConversacion(cliente.getIdSesion())){
				// TODO Verificar si cambio el id de sesion, si es asi agregarla al cliente y hacerlo saber a conversacion
				misClientes.get(cliente.getUsuarioId()).agregarIdsDeSesiones(cliente.getIdSesion());
				misConversaciones.get(cliente.getIdSesion()).cambiarParticipante(misClientes.get(cliente.getUsuarioId())); // Actualizar cliente en la conversacion
				resultado = hablarConElAjente(cliente, textoDelCliente);
				
				synchronized (misClientes) {
					misClientes.put(cliente.getUsuarioId(), misConversaciones.get(cliente.getIdSesion()).obtenerElParticipante());
				}
			}else{ // Crear un nuevo Cliente y asociarle una conversacion

				semaphore.acquire(); //Sección crítica a proteger
				crearUnaNuevoConversacion(cliente, agente);
				semaphore.release();
				
				resultado = hablarConElAjente(cliente, textoDelCliente);
				/*if(existeLaConversacion(cliente.getIdSesion())){ // Es porque ya se cliente esta conversando y no se habia logueado, eso quiere decir que se tiene que mantener el contexto y NO saludar de nuevo
					resultado = hablarConElAjente(cliente, textoDelCliente, esConocerte);
				}else{
					resultado = inicializarConversacionConElAgente(cliente.getIdSesion());
				}*/
			}
		}else{
			if(! cliente.getIdSesion().equals("")){
				if(existeLaConversacion(cliente.getIdSesion())){
					resultado = hablarConElAjente(cliente, textoDelCliente);
				}else{ // Crear una nueva conversacion
					crearUnaNuevoConversacion(cliente, agente);
					resultado = inicializarConversacionConElAgente(cliente, textoDelCliente);
				}
			}else{
				throw new ChatException("No se puede chatear porque no existe id de sesion");
			}
		}
		
		return resultado;
	}
	
	public boolean existeElCliente(String idDelCliente){
		return misClientes.containsKey(idDelCliente);
	}
	
	private boolean existeLaConversacion(String idSesion){
		return misConversaciones.containsKey(idSesion);
	}
	
	public ArrayList<Salida> inicializarConversacionConElAgente(Usuario cliente, String textoDelCliente) throws Exception{
		if(textoDelCliente.isEmpty())
			return misConversaciones.get(cliente.getIdSesion()).inicializarLaConversacion();
		else
			return hablarConElAjente(cliente, textoDelCliente);
	}
	
	public ArrayList<Salida> hablarConElAjente(Usuario cliente, String textoDelCliente) throws Exception{
		ArrayList<Salida> resultado = null;
		resultado = misConversaciones.get(cliente.getIdSesion()).analizarLaRespuestaConWatson(textoDelCliente);
		return resultado;
	}
	
	public void generarAudiosEstaticos(String usuarioTTS, String contrasenaTTS, String vozTTS, String pathAGuardar, String usuarioFTP, 
			String contrasenaFTP, String hostFTP, int puetoFTP, String carpetaFTP, String url, String pathXMLAudios){
		HiloParaGenerarAudiosEstaticos hilo = new HiloParaGenerarAudiosEstaticos(usuarioTTS, contrasenaTTS, vozTTS, pathAGuardar, 
				usuarioFTP, contrasenaFTP, hostFTP, puetoFTP, carpetaFTP, url, pathXMLAudios);
		hilo.start();
	}
	
	public void generarAudiosEstaticosDeUnTema(String usuarioTTS, String contrasenaTTS, String vozTTS, String pathAGuardar, String usuarioFTP, String contrasenaFTP, String hostFTP, int puetoFTP, String carpeta, int index, String url){
		TextToSpeechWatson.getInstance(usuarioTTS, contrasenaTTS, vozTTS, usuarioFTP, contrasenaFTP, hostFTP, puetoFTP, carpeta, pathAGuardar, url);
		System.out.println(String.format("El path a guardar los audios es %s y la url publica es %s", pathAGuardar, url));
		miTemario.generarAudioEstaticosDeUnTema(pathAGuardar, url, index);
		System.out.println("Se termino de generar audios estaticos de tema.");
	}
	
	public void cargarElNombreDeUnSonidoEstaticoEnMemoria(String pathAGuardar, String url, int indexTema, int indexFrase, String idNombreTema, String nombreDelArchivo){
		System.out.println(String.format("El path a guardar los audios es %s y la url publica es %s", pathAGuardar, url));
		miTemario.cargarElNombreDeUnSonidoEstaticoEnMemoria(pathAGuardar, url, indexTema, indexFrase, idNombreTema, nombreDelArchivo);
		System.out.println("Se termino de agregar el audio a la frase en el tema "+idNombreTema);
	}
	
	public String verMiTemario(){
		return miTemario.verMiTemario();
	}
	
	private class HiloParaBorrarConversacionesInactivas extends Thread{
		
		public HiloParaBorrarConversacionesInactivas(){}
		
		public void run(){
			while(true){
				Enumeration<String> keys = misConversaciones.keys();
				Date fechaActual = Calendar.getInstance().getTime();
				while(keys.hasMoreElements()){
					String key = keys.nextElement();
					Date ultimoRegistro = misConversaciones.get(key).obtenerLaFechaDelUltimoRegistroDeLaConversacion();
					long diff = fechaActual.getTime() - ultimoRegistro.getTime();
					if(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) > 3){ // Si es mayor de 3 dias hay q borrar
						synchronized(misConversaciones){
							borrarUnaConversacion(key);
						}
					}
				}
				
				try {
					Thread.sleep(86400000); // Dormir por un dia
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private class HiloParaGenerarAudiosEstaticos extends Thread{
		private String usuarioTTS;
		private String contrasenaTTS;
		private String vozTTS;
		private String pathAGuardar;
		private String usuarioFTP;
		private String contrasenaFTP;
		private String hostFTP;
		private int puetoFTP;
		private String carpetaFTP;
		private String urlAReproducir;
		private String pathXMLAudios;
		
		public HiloParaGenerarAudiosEstaticos(String usuarioTTS, String contrasenaTTS, String vozTTS, String pathAGuardar, 
				String usuarioFTP, String contrasenaFTP, String hostFTP, int puetoFTP, String carpetaFTP, String url, String pathXMLAudios){
			this.usuarioTTS = usuarioTTS;
			this.contrasenaTTS = contrasenaTTS;
			this.vozTTS = vozTTS;
			this.pathAGuardar = pathAGuardar;
			this.usuarioFTP = usuarioFTP;
			this.contrasenaFTP = contrasenaFTP;
			this.hostFTP = hostFTP;
			this.puetoFTP = puetoFTP;
			this.carpetaFTP = carpetaFTP;
			this.urlAReproducir = url;
			this.pathXMLAudios = pathXMLAudios;
		}
		
		public void run(){
			TextToSpeechWatson.getInstance(usuarioTTS, contrasenaTTS, vozTTS, usuarioFTP, contrasenaFTP, hostFTP, puetoFTP, carpetaFTP, pathAGuardar, urlAReproducir);
			System.out.println(String.format("El path a guardar los audios es %s y la url publica es %s", pathAGuardar, urlAReproducir));
			if (AudiosXML.getInstance().exiteElArchivoXMLDeAudios(this.pathXMLAudios)){
				AudiosXML.getInstance().cargarLosNombresDeLosAudios();
			}
			miTemario.generarAudioEstaticosDeTodasLasFrases(pathAGuardar, urlAReproducir);
			AudiosXML.getInstance().guardarLosAudiosDeUnaFrase(miTemario.contenido());
			
			System.out.println("Se termino de generar audios estaticos.");
		}
	}

	public String borrarUnaConversacion(String idSesion){
		historicoDeConversaciones.borrarElHistoricoDeUnaConversacion(idSesion, this.buscarUnClienteApartirDeLaSesion(idSesion));
		
		String resultado = "La conversación con id "+idSesion+" no existe.";
		if(existeLaConversacion(idSesion)){
			synchronized(misConversaciones){
				try {
					misConversaciones.get(idSesion).guardarEstadiscitas(idSesion);
					misConversaciones.get(idSesion).obtenerAgenteDeLaMuni().guardarUnaConversacionEnLaDB(idSesion, misConversaciones.get(idSesion).obtenerElParticipante().getMiNombre());
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
				}
				misConversaciones.remove(idSesion);
				resultado = "La conversación con id "+idSesion+" se borró exitosamente.";
			}
		}
		System.out.println(resultado);
		return resultado;
	}
	
	public String verTodasLasCoversacionesActivas(){
		String resultado = "";
		
		Enumeration<String> keys = misConversaciones.keys();
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			resultado += " -> "+key+" \n";
		}
		
		return resultado;
	}
	
	public String verTodosLosClientesActivos(){
		String resultado = "";
		
		Enumeration<String> keys = misClientes.keys();
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			resultado += " -> "+key+" \n";
		}
		
		return resultado;
	}
	
	public String verLosIdsDeLasConversacionesActivasPorCliente(String idCliente){

		String resultado = "El cliente con id "+idCliente+" no existe.";
		if(existeElCliente(idCliente)){
			resultado = "";
			Cliente miCliente = misClientes.get(idCliente);
			ArrayList<String> idsSeseiones = miCliente.getMisIdsDeSesiones();
			for (String idSesion: idsSeseiones){
				resultado += " -> "+idSesion+" \n";
			}
		}
		
		return resultado;
	}
	
	public String borrarTodasLasConversacionesDeUnCliente(String idCliente){
		
		historicoDeConversaciones.borrarElHistoricoDeUnaConversacionPorCliente(this.obtenerLosIdsDeSesionDeUnCliente(idCliente), idCliente);

		String resultado = "El cliente con id "+idCliente+" no existe.";
		if(existeElCliente(idCliente)){
			synchronized(misConversaciones){
				Cliente miCliente = misClientes.get(idCliente);
				ArrayList<String> idsSeseiones = miCliente.getMisIdsDeSesiones();
				for (String idSesion: idsSeseiones){
					borrarUnaConversacion(idSesion);
				}
				miCliente.borrarTodosLosIdsDeSesiones();
				resultado = "Las conversaciones del cliente "+idCliente+" se borraron exitosamente.";
			}
		}
		return resultado;
	}
	
	public ArrayList<String> obtenerLosIdsDeSesionDeUnCliente(String idCliente){
		ArrayList<String> resultado = null;
		if(existeElCliente(idCliente)){
			Cliente miCliente = misClientes.get(idCliente);
			resultado = miCliente.getMisIdsDeSesiones();
		}
		
		return resultado;
	}

	public String buscarUnClienteApartirDeLaSesion(String idSesion){
		String resultado = "";
		
		Enumeration<String> keys = misClientes.keys();
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			if(misClientes.get(key).contieneElIdSesion(idSesion)){
				resultado = key;
				break;
			}
		}
		
		return resultado;
	}
	
	public void inicializar(String pathXML, Temario temario) {
		consultaDao = new ConsultaDao();
		System.out.println("El path xml es: "+pathXML);
		miTemario = temario;
		consultaDao.establecerTemario(miTemario);
	}
	
	public ConsultaDao obtenerConsultaDao(){
		return this.consultaDao;
	}
	
	public Cliente obtenerCliente(String idCliente)
	{
		return misClientes.get(idCliente);
	}
	
	public String verElHistoricoDeLaConversacion(String idSesion, String fecha){
		LaConversacion miHistorico = null;
		
		if(existeLaConversacion(idSesion)){
			miHistorico = misConversaciones.get(idSesion).obtenerAgenteDeLaMuni().verMiHistorico();
		}else{
			try {
				miHistorico = historicoDeConversaciones.obtenerMiBitacoraDeBD().buscarUnaConversacion(idSesion, fecha);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (miHistorico != null)
				historicoDeConversaciones.agregarHistorialALaConversacion(idSesion, miHistorico);
		}
		
		if (miHistorico != null)
			return miHistorico.verMiHistorialDeLaConversacion();
		else
			return "";
	}
	
	public String buscarConversacionesQueNoHanSidoVerificadasPorTema(String idTema) throws ClassNotFoundException, SQLException{
		return this.buscarConversacionesQueNoHanSidoVerificadasPorTema(idTema);
	}
	
	public String cambiarDeEstadoAVerificadoDeLaConversacion(String idCliente, String idSesion, String fecha) throws ClassNotFoundException, SQLException{
		return this.cambiarDeEstadoAVerificadoDeLaConversacion(idCliente, idSesion, fecha);
	}
}
