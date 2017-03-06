package com.ncubo.chatbot.watson;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.exceptiones.ChatException;
import com.ncubo.ftp.FTPCliente;

public class SpeechToTextWatson{
	
	private SpeechToText service;
	private static SpeechToTextWatson speechToTextWatson = null;
	private String usuarioTTS = Constantes.WATSON_USER_TEXT_SPEECH;
	private String contrasenaTTS = Constantes.WATSON_PASS_TEXT_SPEECH;
	private String vozSTT = Constantes.WATSON_MODEL_SPEECH_TEXT;
	private FTPCliente ftp;
	private String pathAudios;
	private String urlPublicaAudios;
	
	private SpeechToTextWatson(String usuario, String contrasena, String voz, String usuarioFTP, String contrasenaFTP, String hostFTP, int puetoFTP, String carpeta, String path, String urlPublicaAudios){
		service = new SpeechToText();
		service.setUsernameAndPassword(usuario, contrasena);
		
		this.usuarioTTS = usuario;
		this.contrasenaTTS = contrasena;
		this.vozSTT = voz;
		this.pathAudios = path;
		this.urlPublicaAudios = urlPublicaAudios;
		this.ftp = new FTPCliente(usuarioFTP, contrasenaFTP, hostFTP, puetoFTP, carpeta);
		
		System.out.println(String.format("Los datos del TTS  son: %s / %s / %s. Y los datos del FTP son: %s / %s / %s / %s", usuarioTTS, contrasenaTTS, vozSTT, usuarioFTP, contrasenaFTP, hostFTP, puetoFTP));
		
	}
	
	public static SpeechToTextWatson getInstance(String usuario, String contrasena, String voz, String usuarioFTP, String contrasenaFTP, String hostFTP, int puetoFTP, String carpeta, String pathAudios, String urlPublicaAudios)
	{
		if(speechToTextWatson == null)
		{
			speechToTextWatson = new SpeechToTextWatson(usuario, contrasena, voz, usuarioFTP, contrasenaFTP, hostFTP, puetoFTP, carpeta, pathAudios, urlPublicaAudios);
		}
		return speechToTextWatson;
	}
	
	public static SpeechToTextWatson getInstance()
	{
		if(speechToTextWatson == null)
		{
			throw new ChatException("No se a inicializado esta clase. Debe instanciar esta clase primero.");
		}
		return speechToTextWatson;
	}
	
	public String transformAudio(File inputstream){
		String resultado = "";
		
		RecognizeOptions options = new RecognizeOptions.Builder()
				.continuous(true)
				.contentType(HttpMediaType.AUDIO_WAV)
				.model(vozSTT)
				.build();
		
		try{
			SpeechResults results = service.recognize(inputstream, options).execute();
			resultado = results.getResults().get(0).getAlternatives().get(0).getTranscript();
		}
		catch (Exception e) {
			resultado = "ERROR STT: "+e.getMessage();
			try{
				SpeechResults results = service.recognize(inputstream, options).execute();
				resultado = results.getResults().get(0).getAlternatives().get(0).getTranscript();
			}
			catch (Exception e1) {
				resultado = "ERROR STT: "+e.getMessage();
			}
		}
		
		inputstream.delete();
		
		return resultado;
	}
	
}
