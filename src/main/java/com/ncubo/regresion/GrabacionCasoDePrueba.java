package com.ncubo.regresion;

import java.util.ArrayList;

import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.partesDeLaConversacion.Salida;
import com.ncubo.chatbot.participantes.Cliente;
import com.ncubo.db.ConexionALaDB;
import com.ncubo.db.ConsultaDao;
import com.ncubo.logicaDeLasConversaciones.Conversacion;
import com.ncubo.logicaDeLasConversaciones.InformacionDelCliente;

public class GrabacionCasoDePrueba {

	ArrayList<Salida> salidasParaElCliente = new ArrayList<>();
	TemarioDeLaRegresion temario = null;
	Conversacion miConversacion = null;
	
	public ArrayList<Salida> iniciarGrabacion(String xmlFrases){
		temario = new TemarioDeLaRegresion(xmlFrases);
		ConsultaDao consultaDao = new ConsultaDao();
		
		Cliente cliente = null ;
		try {
			cliente = new Cliente("Ricky", "123456");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Problemas al iniciar el cliente");
		}
		
		InformacionDelCliente informacionDelCliente = new InformacionDelCliente("test", "test", "");
		miConversacion = new Conversacion(temario, cliente, consultaDao, new AgenteDeLaRegresion(temario.contenido().getMiWorkSpaces()), informacionDelCliente);
		
		return salidasParaElCliente = miConversacion.inicializarLaConversacion();
	}
	
	
	public ArrayList<Salida> enviarTexto(String texto){
		try {
			 salidasParaElCliente = miConversacion.analizarLaRespuestaConWatson(texto, true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Problemas al enviar texto a Watson");
			salidasParaElCliente = null;
		}
		return salidasParaElCliente;
	}
	
	public int guardarConversacion(){
		ConexionALaDB.getInstance(Constantes.DB_HOST, Constantes.DB_NAME, Constantes.DB_USER, Constantes.DB_PASSWORD);
		return miConversacion.obtenerAgente().guardarUnaConversacionEnLaDB("123", "Lis-Regresion");
	}
}
