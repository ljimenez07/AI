package com.ncubo.chatbot.google;

import java.io.File;
import java.io.IOException;

public class STTGoogle {

	private Recognizer recognizer;
	private static String API_KEY = "AIzaSyAokCeQz5sIPaCwsqk6IUKQpfU_tshQJ-o";
	private int maxNumOfResponses = 4;
	
	public STTGoogle(){
	    recognizer = new Recognizer(Recognizer.Languages.SPANISH_SPAIN, API_KEY);
	}
	
	public STTGoogle(String apiKey){
	    recognizer = new Recognizer(Recognizer.Languages.SPANISH_SPAIN, apiKey);
	}
	
	public String convertirDeAudioATexto(File file){
		
		GoogleResponse response;
		String resultado = "";
		
		File archivoDeAudio = new File (file.getAbsolutePath().replaceAll("wav", "flac"));
		
		FlacEncoder flacEncoder = new FlacEncoder();
	    flacEncoder.convertWaveToFlac(file, archivoDeAudio);
	    
		try {
			response = recognizer.getRecognizedDataForFlac(archivoDeAudio, maxNumOfResponses, 24000);
			resultado = response.getResponse ();
			System.out.println ("Google Response: " + resultado);
			System.out.println ("Google is " + Double.parseDouble (response.getConfidence ()) * 100 + "% confident in" + " the reply");
			System.out.println ("Other Possible responses are: ");
			for (String s:response.getOtherPossibleResponses ()) {
				System.out.println ("\t" + s);
			}
			
		} catch (IOException e) {
			try {
				response = recognizer.getRecognizedDataForFlac(archivoDeAudio, maxNumOfResponses, 24000);
				resultado = response.getResponse ();
				System.out.println ("Google Response: " + resultado);
				System.out.println ("Google is " + Double.parseDouble (response.getConfidence ()) * 100 + "% confident in" + " the reply");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		archivoDeAudio.delete();
		
		return resultado;
	}
	
	public static void main (String[]args) {
		
	    File file = new File ("src/main/resources/test_es.wav");
		STTGoogle stt = new STTGoogle();
		stt.convertirDeAudioATexto(file);
	}
	
}