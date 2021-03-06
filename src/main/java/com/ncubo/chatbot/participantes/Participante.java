package com.ncubo.chatbot.participantes;

import com.ncubo.chatbot.partesDeLaConversacion.Salida;
import com.ncubo.chatbot.partesDeLaConversacion.Sonido;
import com.ncubo.chatbot.partesDeLaConversacion.Tema;
import com.ncubo.chatbot.partesDeLaConversacion.Vineta;
import com.ncubo.chatbot.watson.TextToSpeechWatson;

import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.partesDeLaConversacion.ComponentesDeLaFrase;
import com.ncubo.chatbot.partesDeLaConversacion.Frase;
import com.ncubo.chatbot.partesDeLaConversacion.Respuesta;

public class Participante{ 
	
	public enum Manifestarse{ // https://howtoprogramwithjava.com/enums/
		
		EnFormaEscrita(0b001), 
		EnFormaOral(0b010), 
		EnFormaVisual(0b100);
		
		private int valorDeManifestarse;
		 
		private Manifestarse (int valor){
			this.valorDeManifestarse = this.valorDeManifestarse | valor;
		}

		public boolean esEnFormaEscrita() {
			return (valorDeManifestarse & 0b001) != 0;
		}
		
		public boolean esEnFormaOral() {
			return (valorDeManifestarse & 0b010) != 0;
		}
		
		public boolean esFormaVisual() {
			return (valorDeManifestarse & 0b100) != 0;
		}
		
	}
	
	private Manifestarse formaDeManifestarseEscrita = Manifestarse.EnFormaEscrita;
	private Manifestarse formaDeManifestarseOral = Manifestarse.EnFormaEscrita;
	private Manifestarse formaDeManifestarseVisual = Manifestarse.EnFormaEscrita;
	
	public Participante(){}
	
	public void manifestarseEnFormaOral(){
		formaDeManifestarseOral = Manifestarse.EnFormaOral;
	}
	
	public void manifestarseEnFormaVisual(){
		formaDeManifestarseVisual = Manifestarse.EnFormaVisual;
	}
	
	public Salida decir(Frase frase, Respuesta respuesta, Tema tema, String intencionDespedida){
		Salida salida = new Salida();
		ComponentesDeLaFrase fraseADecir = null;
		
		if (formaDeManifestarseEscrita.esEnFormaEscrita()){
			fraseADecir = frase.texto();
			if (fraseADecir != null){
				salida.escribir(fraseADecir.getTextoDeLaFrase().toString(), respuesta, tema, frase);
			}
		}
		
		if (formaDeManifestarseOral.esEnFormaOral()){
			Sonido sonido = fraseADecir.getAudio("audio");
			if (sonido != null)
				salida.escribir(sonido, respuesta, tema, frase);
		}
		
		if (formaDeManifestarseVisual.esFormaVisual()){
			Vineta vineta = fraseADecir.getVineta();
			if(vineta != null){
				salida.escribir(vineta, respuesta, tema, frase);
			}
			Vineta vinetaSeleccion = frase.vineta();
			if(vinetaSeleccion != null)
				salida.escribir(vinetaSeleccion, respuesta, tema, frase);
		}
		
		try{
			if(respuesta.obtenerLaIntencionDeConfianzaDeLaRespuesta().getNombre().equals(intencionDespedida)){
				salida.cambiarSeTerminoElChat(true);
			}
		}catch(Exception e){
			
		}
		
		return salida;
	}
	
	public Salida decirUnaFraseDinamica(Frase frase, Respuesta respuesta, Tema tema, String datoAActualizar, String idCliente, String intencionDespedida){
		Salida salida = new Salida();
		String texto = "";
		ComponentesDeLaFrase fraseADecir = null;
		
		if (formaDeManifestarseEscrita.esEnFormaEscrita()){
			fraseADecir = frase.texto();
			texto = fraseADecir.getTextoDeLaFrase().replace("$", datoAActualizar);
			salida.escribir(texto, respuesta, tema, frase);
		}
		
		if (formaDeManifestarseOral.esEnFormaOral()){
			Sonido sonido = null;
			
			String nombreDelArchivo = TextToSpeechWatson.getInstance().getAudioToURL(texto, false, idCliente);
			String miIp = TextToSpeechWatson.getInstance().obtenerUrlPublicaDeAudios()+idCliente+"-"+nombreDelArchivo;
			sonido = new Sonido(miIp, texto);
			
			if (sonido != null)
				salida.escribir(sonido, respuesta, tema, frase);
		}
		
		if (formaDeManifestarseVisual.esFormaVisual()){
			Vineta vineta = fraseADecir.getVineta();
			if(vineta != null){
				salida.escribir(vineta, respuesta, tema, frase);
			}
			Vineta vinetaSeleccion = frase.vineta();
			if(vinetaSeleccion != null)
				salida.escribir(vinetaSeleccion, respuesta, tema, frase);
		}
		
		try{
			if(respuesta.obtenerLaIntencionDeConfianzaDeLaRespuesta().getNombre().equals(intencionDespedida)){
				salida.cambiarSeTerminoElChat(true);
			}
		}catch(Exception e){
			
		}
		
		return salida;
	}
	
	public Salida volverAPreguntar(Frase pregunta, Respuesta respuesta, Tema tema, String idCliente){
		return volverAPreguntarConMeRindo(pregunta, respuesta, tema, false, true, idCliente);
	}
	
	public Salida volverAPreguntarConMeRindo(Frase pregunta, Respuesta respuesta, Tema tema, boolean meRindo, boolean generarAudio, String idCliente){
		
		Salida salida = new Salida();
		ComponentesDeLaFrase resultado = null;
		String texto = "";
		if (formaDeManifestarseEscrita.esEnFormaEscrita()){
			if(meRindo && pregunta.hayTextosMeRindo()){
				resultado = pregunta.textoMeRindo();
				texto = resultado.getTextoDeLaFrase();
			}else{
				if(pregunta.hayTextosImpertinetes()){
					resultado = pregunta.textoImpertinente();
					texto = resultado.getTextoDeLaFrase();
				}else{
					resultado = pregunta.texto();
					String laPreguntaEs = resultado.getTextoDeLaFrase();
					if(laPreguntaEs.isEmpty())
						laPreguntaEs = resultado.getTextoAUsarParaGenerarElAudio();
					texto = pregunta.conjuncionParaRepreguntar().getTextoDeLaFrase()+" "+laPreguntaEs;
				}
			}		
			salida.escribir(texto, respuesta, tema, pregunta);
		}
		
		if (formaDeManifestarseOral.esEnFormaOral()){
			Sonido sonido = null;
			if(meRindo && pregunta.hayTextosMeRindo()){
				sonido = resultado.getAudio("audio");
			}else{
				if(pregunta.hayTextosImpertinetes()){
					sonido = resultado.getAudio("audio");
				}else{
					if(! texto.equals("")){
						try{
							
							String textoParaReproducir = texto;
							textoParaReproducir = textoParaReproducir.replace("&nbsp;", " ");
							textoParaReproducir = textoParaReproducir.replace("<br/>", " ");
							String miIp = "";
							if(generarAudio){
								String nombreDelArchivo = TextToSpeechWatson.getInstance().getAudioToURL(textoParaReproducir, true, idCliente);
								miIp = TextToSpeechWatson.getInstance().obtenerUrlPublicaDeAudios()+idCliente+"-"+nombreDelArchivo;
								
							}
							sonido = new Sonido(miIp, textoParaReproducir);
						}catch(Exception e){
							System.out.println("Error al generar el audio dinamico de: "+texto);
						}
						
					}
				}
			}
			if(sonido != null)
				salida.escribir(sonido, respuesta, tema, pregunta);
		}
		
		if (formaDeManifestarseVisual.esFormaVisual()){
			Vineta vineta = resultado.getVineta();
			if(vineta != null){
				salida.escribir(vineta, respuesta, tema, pregunta);
			}
			
			Vineta vinetaSeleccion = pregunta.vineta();
			if(vinetaSeleccion != null)
				salida.escribir(vinetaSeleccion, respuesta, tema, pregunta);
		}
		
		return salida;
	}

}