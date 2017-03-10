package com.ncubo.chatbot.audios;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.google.STTGoogle;
import com.ncubo.chatbot.watson.SpeechToTextWatson;

public class ControladorDeAudios {

	public ControladorDeAudios(){}
	
	public String transformarAudioATexto(File inputstream, String cualAPI){
		if(cualAPI.contains(Constantes.API_GOOGLE)){
			STTGoogle stt = new STTGoogle();
			return stt.convertirDeAudioATexto(inputstream);
		}
		else{
			return SpeechToTextWatson.getInstance().transformAudio(inputstream);
		}
	}
	
	public String transformarAudioMp3AWavYLuegoATexto(File inputStreamMp3, File inputStreamWav, String cualAPI){
		
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
	
	public static void main(String argv[]) throws Exception {
		ControladorDeAudios audios = new ControladorDeAudios();
		File mp3 = new File("C:/Users/SergioAlberto/Documents/SergioGQ/Ncubo/ProyectosAI/AgenteCognitivo/AgenteCognitivo/CognitiveAgent/src/main/webapp/uploads/test_file.mp3");
		File wav = new File("C:/Users/SergioAlberto/Documents/SergioGQ/Ncubo/ProyectosAI/AgenteCognitivo/AgenteCognitivo/CognitiveAgent/src/main/webapp/uploads/test_file.wav");
		audios.mp3ToWav(mp3, wav);
	}
}
