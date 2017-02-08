package com.ncubo.chatbot.consola;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.partesDeLaConversacion.Salida;
import com.ncubo.chatbot.partesDeLaConversacion.Temario;
import com.ncubo.chatbot.participantes.Cliente;
import com.ncubo.conectores.Conectores;
import com.ncubo.db.ConsultaDao;
import com.ncubo.logicaDeLasConversaciones.Conversacion;
import com.ncubo.logicaDeLasConversaciones.InformacionDelCliente;

public class Consola {

	private static Temario temarioDePrueba;
	
	public Consola(){}
	
	private void imprimirSalidas(ArrayList<Salida> salidas){
		
		for(Salida salida: salidas){
			try{
				System.out.println("Contexto: "+salida.obtenerLaRespuestaDeIBM().messageResponse().getContext());
			}catch(Exception e){
				
			}
		}
		
		System.out.println("");
		for(Salida salida: salidas){
			String audio = "";
			if( ! salida.getMiSonido().url().equals(""))
				audio = " ("+salida.getMiSonido().url()+")";
			
			System.out.println("- "+salida.getMiTexto()+audio);
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
	
	public static void main(String argv[]) throws Exception {
		Consola main = new Consola();
		
		temarioDePrueba = new TemarioDePruebas(Constantes.PATH_ARCHIVO_DE_CONFIGURACION_BA);
		ConsultaDao consultaDao = new ConsultaDao();
		
		Cliente cliente = new Cliente("Ricky", "123456", new Conectores());
		InformacionDelCliente informacionDelCliente = new InformacionDelCliente("test", "test", "");
		Conversacion miconversacion = new Conversacion(temarioDePrueba, cliente, consultaDao, new AgenteDePrueba(temarioDePrueba.contenido().getMiWorkSpaces()), informacionDelCliente);
		
		String respuesta = "";
		
		ArrayList<Salida> salidasParaElCliente = miconversacion.inicializarLaConversacion();
		main.imprimirSalidas(salidasParaElCliente);
		
		while(true){
			respuesta = main.leerTexto();
			
			salidasParaElCliente = miconversacion.analizarLaRespuestaConWatson(respuesta, true);
			main.imprimirSalidas(salidasParaElCliente);
		}
	}
	
}
