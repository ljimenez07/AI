package com.ncubo.regresion;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.partesDeLaConversacion.Salida;
import com.ncubo.chatbot.participantes.Cliente;
import com.ncubo.db.ConexionALaDB;
import com.ncubo.db.ConsultaDao;
import com.ncubo.logicaDeLasConversaciones.Conversacion;
import com.ncubo.logicaDeLasConversaciones.InformacionDelCliente;
import com.ncubo.logicaDeLasConversaciones.TemariosDeUnCliente;

public class GrabacionCasoDePrueba {

	ArrayList<Salida> salidasParaElCliente = new ArrayList<>();
	TemariosDeUnCliente temario = null;
	Conversacion miConversacion = null;
	
	public ArrayList<Salida> iniciarGrabacion(String xmlFrases){
		temario = new TemariosDeUnCliente(xmlFrases);
		ConsultaDao consultaDao = new ConsultaDao();
		
		Cliente cliente = null ;
		try {
			cliente = new Cliente("Ricky", "123456");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Problemas al iniciar el cliente");
		}
		
		InformacionDelCliente informacionDelCliente = new InformacionDelCliente("test", "test", "");
		miConversacion = new Conversacion(temario, cliente, consultaDao, new AgenteDeLaRegresion(temario.getMiWorkSpaces()), informacionDelCliente);
		
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
	
	public int guardarConversacion(String xmlCasos, String descripcionDelCaso){
		ConexionALaDB.getInstance(Constantes.DB_HOST, Constantes.DB_NAME, Constantes.DB_USER, Constantes.DB_PASSWORD);
		int id = miConversacion.obtenerAgente().guardarUnaConversacionEnLaDB("regresion", "123", "Regresion");
		
		File file = new File(xmlCasos);

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Document doc = null;
		try
		{
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(file);
		} catch(Exception e)
		{
			e.printStackTrace();
			return id;
		}
		
		Element root = doc.getDocumentElement();
		root.normalize();
		Element nodoCasosDePrueba = (Element)doc.getElementsByTagName("casosDePrueba").item(0);
		Element casoOriginal = (Element)nodoCasosDePrueba.getElementsByTagName("caso").item(0);
		Element caso = (Element) doc.importNode(casoOriginal, true);
		caso.setAttribute("descripcion", descripcionDelCaso);
		caso.setAttribute("idConversacion", String.valueOf(id));
		nodoCasosDePrueba.appendChild(caso);
		
		try
		{
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(xmlCasos));
			transformer.transform(source, result);
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return id;
	}
}
