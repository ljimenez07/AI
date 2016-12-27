package com.ncubo.chatbot.participantes;

import com.ncubo.chatbot.partesDeLaConversacion.Salida;
import com.ncubo.chatbot.partesDeLaConversacion.Sonido;
import com.ncubo.chatbot.partesDeLaConversacion.Tema;
import com.ncubo.chatbot.partesDeLaConversacion.Vineta;
import com.ncubo.chatbot.watson.TextToSpeechWatson;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import com.ncubo.chatbot.configuracion.Constantes;
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
	
	public Salida decir(Frase frase, Respuesta respuesta, Tema tema){
		Salida salida = new Salida();
		int idDelSonidoAUsar = -1;
		
		if (formaDeManifestarseEscrita.esEnFormaEscrita()){
			List<String> texto = frase.texto();
			if (texto != null){
				idDelSonidoAUsar = Integer.valueOf(texto.get(0));
				salida.escribir(texto.get(1), respuesta, tema, frase);
			}
		}
		
		if (formaDeManifestarseOral.esEnFormaOral()){
			Sonido sonido = frase.obtenerSonidoAUsar(idDelSonidoAUsar);
			if (sonido != null)
				salida.escribir(sonido, respuesta, tema, frase);
		}
		
		if (formaDeManifestarseVisual.esFormaVisual()){
			List<String> vineta = frase.vineta();
			if(vineta != null)
				salida.escribir(vineta.get(1), respuesta, tema, frase);
		}
		
		try{
			if(respuesta.obtenerLaIntencionDeConfianzaDeLaRespuesta().getNombre().equals(Constantes.INTENCION_DESPEDIDA)){
				salida.cambiarSeTerminoElChat(true);
			}
		}catch(Exception e){
			
		}
		
		return salida;
	}
	
	public Salida decirUnaFraseDinamica(Frase frase, Respuesta respuesta, Tema tema, String datoAActualizar){
		Salida salida = new Salida();
		String texto = "";
		
		if (formaDeManifestarseEscrita.esEnFormaEscrita()){
			List<String> resultado = frase.texto();
			texto = resultado.get(1).replace("$", datoAActualizar);
			salida.escribir(texto, respuesta, tema, frase);
		}
		
		if (formaDeManifestarseOral.esEnFormaOral()){
			Sonido sonido = null;
			
			String nombreDelArchivo = TextToSpeechWatson.getInstance().getAudioToURL(texto, false);
			String path = frase.getPathAGuardarLosAudiosTTS()+File.separator+nombreDelArchivo;
			String miIp = TextToSpeechWatson.getInstance().obtenerUrlPublicaDeAudios()+nombreDelArchivo;
			sonido = new Sonido(miIp, path);
			
			if (sonido != null)
				salida.escribir(sonido, respuesta, tema, frase);
		}
		
		if (formaDeManifestarseVisual.esFormaVisual()){
			//Vineta vineta = frase.vineta();
			//salida.escribir(vineta, respuesta, tema, frase);
		}
		
		try{
			if(respuesta.obtenerLaIntencionDeConfianzaDeLaRespuesta().getNombre().equals(Constantes.INTENCION_DESPEDIDA)){
				salida.cambiarSeTerminoElChat(true);
			}
		}catch(Exception e){
			
		}
		
		return salida;
	}
	
	public Salida volverAPreguntar(Frase pregunta, Respuesta respuesta, Tema tema){
		return volverAPreguntarConMeRindo(pregunta, respuesta, tema, false);
	}
	
	public Salida volverAPreguntarConMeRindo(Frase pregunta, Respuesta respuesta, Tema tema, boolean meRindo){
		
		Salida salida = new Salida();
		List<String> resultado = null;
		String texto = "";
		int idDelSonidoImpertineteAUsar = -1;
		if (formaDeManifestarseEscrita.esEnFormaEscrita()){
			if(meRindo && pregunta.hayTextosMeRindo()){
				resultado = pregunta.textoMeRindo();
				texto = resultado.get(1);
				idDelSonidoImpertineteAUsar = Integer.valueOf(resultado.get(0));
			}else{
				if(pregunta.hayTextosImpertinetes()){
					resultado = pregunta.textoImpertinente();
					texto = resultado.get(1);
					idDelSonidoImpertineteAUsar = Integer.valueOf(resultado.get(0));
				}else{
					resultado = pregunta.texto();
					texto = pregunta.conjuncionParaRepreguntar().get(1)+" "+resultado.get(1);
				}
			}		
			salida.escribir(texto, respuesta, tema, pregunta);
		}
		
		if (formaDeManifestarseOral.esEnFormaOral()){
			Sonido sonido = null;
			if(meRindo && pregunta.hayTextosMeRindo()){
				sonido = pregunta.obtenerSonidoMeRindoAUsar(idDelSonidoImpertineteAUsar);
			}else{
				if(pregunta.hayTextosImpertinetes()){
					sonido = pregunta.obtenerSonidoImpertinenteAUsar(idDelSonidoImpertineteAUsar);
				}else{
					if(! texto.equals("")){
						try{
							
							String textoParaReproducir = texto;
							textoParaReproducir = textoParaReproducir.replace("&nbsp;", " ");
							textoParaReproducir = textoParaReproducir.replace("<br/>", " ");
							
							String nombreDelArchivo = TextToSpeechWatson.getInstance().getAudioToURL(textoParaReproducir, true);
							String path = pregunta.getPathAGuardarLosAudiosTTS()+File.separator+nombreDelArchivo;
							String miIp = TextToSpeechWatson.getInstance().obtenerUrlPublicaDeAudios()+nombreDelArchivo;
							sonido = new Sonido(miIp, path);
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
			//Vineta vineta = pregunta.vineta();
			//salida.escribir(vineta, respuesta, tema, pregunta);
		}
		
		return salida;
	}

}