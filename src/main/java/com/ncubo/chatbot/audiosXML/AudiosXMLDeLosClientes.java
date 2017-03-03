package com.ncubo.chatbot.audiosXML;

import java.io.File;
import java.util.Hashtable;
import com.ncubo.logicaDeLasConversaciones.TemariosDeUnCliente;

public class AudiosXMLDeLosClientes {
	
	private static Hashtable<String, AudiosXML> audiosDeLosClientes;
	
	private static AudiosXMLDeLosClientes audiosXML = null;
	
	private AudiosXMLDeLosClientes(){
		audiosDeLosClientes = new Hashtable<String, AudiosXML>();
	}
	
	public static AudiosXMLDeLosClientes getInstance(){
		if(audiosXML == null)
			audiosXML = new AudiosXMLDeLosClientes();
		return audiosXML;
	}
	
	private boolean existeElCliente(String idCliente){
		return audiosDeLosClientes.containsKey(idCliente);
	}
	
	public void cargarLosNombresDeLosAudios(String idCliente, String archivoDeAudios){
		AudiosXML audios = new AudiosXML(archivoDeAudios);
		audios.cargarLosNombresDeLosAudios();
		audiosDeLosClientes.put(idCliente, audios);
	}
	
	public void guardarLosAudiosDeUnaFrase(String idCliente, TemariosDeUnCliente temarios, String archivoDeAudios){
		
		if(existeElCliente(idCliente)){
			audiosDeLosClientes.get(idCliente).guardarLosAudiosDeUnaFrase(temarios);
		}else{
			AudiosXML audios = new AudiosXML(archivoDeAudios);
			audios.guardarLosAudiosDeUnaFrase(temarios);
			audiosDeLosClientes.put(idCliente, audios);
		}
	}
	
	public boolean exiteElArchivoXMLDeAudios(String pathArchivo){
		try{
			File file = new File(pathArchivo);
			if(file.exists() && ! file.isDirectory()) {
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			return false;
		}
	}
	
	public boolean hayQueGenerarAudios(String idCliente, String nombreDeLaFrase, String textoDeLaFrase, String idTemario){
		if(existeElCliente(idCliente)){
			return audiosDeLosClientes.get(idCliente).hayQueGenerarAudios(nombreDeLaFrase, textoDeLaFrase, idTemario);
		}else{
			return true;
		}
	}
	
	public String obtenerUnAudioDeLaFrase(String idCliente, String nombreDeLaFrase, String idAudio, int posicion, String idTemario){
		if(existeElCliente(idCliente)){
			return audiosDeLosClientes.get(idCliente).obtenerUnAudioDeLaFrase(nombreDeLaFrase, idAudio, posicion, idTemario);
		}
		return "";
	}
	
	public String obtenerUnAudioDeLaFrase(String idCliente, String nombreDeLaFrase, String idAudio, String idTemario){
		if(existeElCliente(idCliente)){
			return audiosDeLosClientes.get(idCliente).obtenerUnAudioDeLaFrase(nombreDeLaFrase, idAudio, idTemario);
		}
		return "";
	}
	
}
