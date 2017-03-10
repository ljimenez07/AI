package com.ncubo.chatbot.audios;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFileFormat.Type;

import org.apache.commons.io.IOUtils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.ncubo.caches.CacheDeAudios;
import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.google.STTGoogle;
import com.ncubo.chatbot.watson.SpeechToTextWatson;
import com.ncubo.ftp.FTPCliente;

public class ControladorDeAudios {

	private FTPCliente ftp;
	private String pathAudios;
	
	public ControladorDeAudios(String usuarioFTP, String contrasenaFTP, String hostFTP, int puetoFTP, String carpeta, String path){
		this.ftp = new FTPCliente(usuarioFTP, contrasenaFTP, hostFTP, puetoFTP, carpeta);
		this.pathAudios = path;
	}
	
	public String transformarAudioATexto(File inputstream, String cualAPI, String idCliente){
		
		String resultado = "";
		String pathFinal = this.pathAudios + idCliente+ "/" + inputstream.getName();
		
		if(cualAPI.contains(Constantes.API_GOOGLE)){
			STTGoogle stt = new STTGoogle();
			resultado = stt.convertirDeAudioATexto(inputstream);
		}
		else{
			resultado = SpeechToTextWatson.getInstance().transformAudio(inputstream);
		}
		
		try {
			transferirAudiosAlFTP(pathFinal, new FileInputStream(inputstream));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return resultado;
	}
	
	public String transformarAudioMp3AWavYLuegoATexto(File inputStreamMp3, File inputStreamWav, String cualAPI, String idCliente){
		
		try {
			mp3ToWav(inputStreamMp3, inputStreamWav);
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(cualAPI.contains(Constantes.API_GOOGLE)){
			STTGoogle stt = new STTGoogle();
			return stt.convertirDeAudioATexto(inputStreamWav);
		}
		else{
			return SpeechToTextWatson.getInstance().transformAudio(inputStreamWav);
		}
	}
	
	public void mp3ToWav(File mp3Data, File wavData) throws UnsupportedAudioFileException, IOException {
	    // open stream
	    AudioInputStream mp3Stream = AudioSystem.getAudioInputStream(mp3Data);
	    AudioFormat sourceFormat = mp3Stream.getFormat();
	    // create audio format object for the desired stream/audio format
	    // this is *not* the same as the file format (wav)
	    AudioFormat convertFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
	        sourceFormat.getSampleRate(), 16, 
	        sourceFormat.getChannels(), 
	        sourceFormat.getChannels() * 2,
	        sourceFormat.getSampleRate(),
	        false);
	    // create stream that delivers the desired format
	    AudioInputStream converted = AudioSystem.getAudioInputStream(convertFormat, mp3Stream);
	    // write stream into a file with file format wav
	    AudioSystem.write(converted, Type.WAVE, wavData);
	}
	
	private void transferirAudiosAlFTP(String pathFinal, InputStream in) throws IOException{
		if(in != null){
			ftp.subirUnArchivoPorHilo(in, pathFinal);
		}
	}
	
	
	/*public static void main(String argv[]) throws Exception {
		ControladorDeAudios audios = new ControladorDeAudios();
		File mp3 = new File("C:/Users/SergioAlberto/Documents/SergioGQ/Ncubo/ProyectosAI/AgenteCognitivo/AgenteCognitivo/CognitiveAgent/src/main/webapp/uploads/test_file.mp3");
		File wav = new File("C:/Users/SergioAlberto/Documents/SergioGQ/Ncubo/ProyectosAI/AgenteCognitivo/AgenteCognitivo/CognitiveAgent/src/main/webapp/uploads/test_file.wav");
		audios.mp3ToWav(mp3, wav);
	}*/
}
