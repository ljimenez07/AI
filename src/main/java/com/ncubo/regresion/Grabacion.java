package com.ncubo.regresion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.ncubo.caches.CacheDeAudios;
import com.ncubo.chatbot.audiosXML.AudiosXMLDeLosClientes;
import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.partesDeLaConversacion.Salida;
import com.ncubo.chatbot.watson.TextToSpeechWatson;
import com.ncubo.regresion.GrabacionCasoDePrueba;


public class Grabacion {

	public Grabacion(){}
	
	private void imprimirSalidas(ArrayList<Salida> salidas){
		
		for(Salida salida: salidas){
			System.out.println("ID: "+salida.getFraseActual().obtenerIdDeLaFrase());
			System.out.println("Texto: "+salida.getMiTexto());
		}
	}
	
	private String leerTexto(){
		String input = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("R/: ");
		try{
			input = br.readLine();
        }catch(IOException nfe){
            System.err.println("Invalid Format!");
        }
		return input;
	}
	
	public static boolean termino(ArrayList<Salida> salidasParaElCliente){
		
		boolean terminarChat = false;
		
		try {
			for(Salida salida:salidasParaElCliente){
				if(salida.obtenerLaRespuestaDeIBM().obtenerLaIntencionDeConfianzaDeLaRespuesta().getNombre().equals("despedidas"))
						return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		
		return terminarChat;
		
	}

	public static void main(String argv[]) throws Exception {
		Grabacion main = new Grabacion();
	
		GrabacionCasoDePrueba grabacion = new GrabacionCasoDePrueba();
		
		ArrayList<Salida> salidasParaElCliente = grabacion.iniciarGrabacion("src/main/resources/conversacionesMuni1.xml", "src/main/resources/testNG.xml");
		main.imprimirSalidas(salidasParaElCliente);
		String respuesta;
		
		TextToSpeechWatson.getInstance("8f1ec844-f8ad-4303-9293-3da7192c5b59", "LHVIAi4Kfweb", 
				"es-LA_SofiaVoice", "ftp", "123456", "10.1.0.227", 21, "audiosDelAgenteCognitivo", "audios", "/archivossubidos/audios");
		if (AudiosXMLDeLosClientes.getInstance().exiteElArchivoXMLDeAudios("src/main/resources/conversacionesMuni1.xml")){
			AudiosXMLDeLosClientes.getInstance().cargarLosNombresDeLosAudios("muni1", "src/main/resources/conversacionesMuni1.xml");
		}
		
		CacheDeAudios.inicializar(170, 130, 2000000);
		
		
		while(true){
			respuesta = main.leerTexto();
			
			salidasParaElCliente = grabacion.enviarTexto(respuesta);
			main.imprimirSalidas(salidasParaElCliente);
			
			if(termino(salidasParaElCliente))
			{
				System.out.println("Escriba la descripcion del TC: ");
				respuesta = main.leerTexto();
				System.out.println(grabacion.guardarConversacion("src/main/resources/casosDMuni1.xml", respuesta));
				break;
			}
		}
		System.exit(0);
	}
	
}
