package com.ncubo.regresion;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.testng.TestNG;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ncubo.chatbot.bitacora.Dialogo;
import com.ncubo.chatbot.bitacora.LogDeLaConversacion;
import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.partesDeLaConversacion.Salida;
import com.ncubo.chatbot.partesDeLaConversacion.Temario;
import com.ncubo.chatbot.participantes.Cliente;
import com.ncubo.controller.FiltroDeConversaciones;
import com.ncubo.db.ConexionALaDB;
import com.ncubo.db.ConsultaDao;
import com.ncubo.logicaDeLasConversaciones.Conversacion;

public class ComparadorDeIdDeFrases {

	private static Temario temario;
	
	private static ConversacionesDeLaRegresion misConversaciones = new ConversacionesDeLaRegresion();
	
	private static ArrayList<Resultado> resultados = new ArrayList<Resultado>();
	
	public ArrayList<Resultado> correrCasosDesdeXML(String xmlFrases, String xmlCasos, String xmlTestNG, String nombreSuite) throws Exception{
		
		temario = new TemarioDeLaRegresion(xmlFrases); //xml de conversaciones
		
		ConexionALaDB.getInstance(Constantes.DB_HOST, Constantes.DB_NAME , Constantes.DB_USER,Constantes.DB_PASSWORD);
		
		misConversaciones.inicializarConversaciones(xmlFrases);

		File file = new File(xmlCasos);

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);

		doc.getDocumentElement().normalize();
		
		Element nodoCasosDePrueba = (Element)doc.getElementsByTagName("casosDePrueba").item(0);
		NodeList listaDeCasos = nodoCasosDePrueba.getElementsByTagName("caso");
		for (int x=0; x<listaDeCasos.getLength(); x++)
		{
			
			Element casoDePrueba = (Element)listaDeCasos.item(x);
			int idConversacion = Integer.parseInt(casoDePrueba.getAttribute("idConversacion"));
			String descripcion = casoDePrueba.getAttribute("descripcion");
			
			correrUnCaso(idConversacion, descripcion);
		}
		
		correrTestNG(xmlTestNG, nombreSuite);
				
		return resultados;
	}
	
	
	private void correrTestNG(String pathXML, String nombreSuite){
		File inputFile = new File(pathXML);  
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();   
		DocumentBuilder dBuilder = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			
		}   
		Document document = null;
		try {
			document = dBuilder.parse(inputFile);
		} catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			
		}  
		Element suite = (Element)document.getElementsByTagName("suite").item(0);
		suite.setAttribute("name", nombreSuite);
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance(); 
		Transformer transformer = null;
		try {
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
		
		} 
		DOMSource source = new DOMSource(document); 
		StreamResult result=new StreamResult(new File(pathXML)); 
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			
		} 
		
		TestNG runner=new TestNG();
		List<String> suitefiles=new ArrayList<String>();
		suitefiles.add(pathXML);
		runner.setTestSuites(suitefiles);
		runner.run();
	}

	public void correrUnCaso(int idConversacion, String descripcion){
		
		ConsultaDao consultaDao = new ConsultaDao();
		FiltroDeConversaciones filtro = new FiltroDeConversaciones();

		Cliente cliente = null ;
		try {
			cliente = new Cliente("Regresion", "123456789");
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		Conversacion miconversacion = new Conversacion(temario, cliente, consultaDao,new AgenteDeLaRegresion(temario.contenido().getMiWorkSpaces()));

		Vector <String> observaciones = new Vector <String>();
		observaciones.add("\nEjecución del caso: " + descripcion);
		
		int contadorSalidas = 0;
		boolean status = true;
		ArrayList<Salida> salidasParaElCliente = miconversacion.inicializarLaConversacion();
		LogDeLaConversacion conversacion = new LogDeLaConversacion();
		try {
			conversacion = filtro.obtenerUnaConversacionPorMedioDelId(idConversacion);
		} catch (ClassNotFoundException e) {
			System.out.println("Problema al traer la conversacion de la base de datos");
		}
		ArrayList<Dialogo> dialogos = conversacion.verHistorialDeLaConversacion();
		for(Dialogo dialogo:dialogos){
			if(!dialogo.getLoQueDijoElParticipante().equals(""))
			{
				try {
					salidasParaElCliente = miconversacion.analizarLaRespuestaConWatson(dialogo.getLoQueDijoElParticipante(), true);
				} catch (Exception e) {
					System.out.println("Problema al enviar la respuesta a Watson");
				}
				contadorSalidas = 0;
			}
			else{
				if(salidasParaElCliente.size()<=contadorSalidas){
					observaciones.add("El caso tiene esta respuesta adicional: "+ dialogo.getElTextoQueDijoElFramework());
					status = false;
				}
				else if(salidasParaElCliente.get(contadorSalidas).getFraseActual().obtenerIdDeLaFrase().equals(dialogo.getIdFraseQueUso()))
					observaciones.add("FRASE ID IGUALES: " +  dialogo.getIdFraseQueUso());
				else {
					observaciones.add("FRASE ID DIFERENTES \n Se esperaba \n\t ID: " + dialogo.getIdFraseQueUso() +"\n\t Texto: "+ dialogo.getElTextoQueDijoElFramework()+ "\n Se obtuvo: \n\t ID: "+ salidasParaElCliente.get(contadorSalidas).getFraseActual().obtenerIdDeLaFrase() +"\n\t Texto: "+salidasParaElCliente.get(contadorSalidas).getMiTexto());
					status = false;
				}
				contadorSalidas++;				
			}
		}
		
		LogDeLaConversacion logResultado = new LogDeLaConversacion();
		
		if(status)
		{
			logResultado = null;
			conversacion = null;
		}
		else logResultado = miconversacion.obtenerAgente().verMiHistorico();
		
		Resultado resultado = new Resultado(idConversacion, conversacion, status, logResultado, observaciones);
		resultados.add(resultado);
	}
	
	public ArrayList<Resultado> getResultados() {
		return resultados;
	}

	public static void setResultados(ArrayList<Resultado> resultados) {
		ComparadorDeIdDeFrases.resultados = resultados;
	}
}
