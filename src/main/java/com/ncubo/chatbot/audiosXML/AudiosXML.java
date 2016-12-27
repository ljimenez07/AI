package com.ncubo.chatbot.audiosXML;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ncubo.chatbot.partesDeLaConversacion.Contenido;
import com.ncubo.chatbot.partesDeLaConversacion.Frase;
import com.ncubo.chatbot.partesDeLaConversacion.Tema;
import com.ncubo.chatbot.partesDeLaConversacion.Temario;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;

public class AudiosXML {
	
	private String archivoDeAudios;
	private DocumentBuilderFactory docFactory = null;
	private DocumentBuilder docBuilder = null;
	private Document doc = null;
	private Element rootElement = null;
	private static Hashtable<String, ContenidoDeAudios> misFrases;
	
	private static AudiosXML audiosXML = null;
	
	private AudiosXML(){
		archivoDeAudios = "";
		misFrases = new Hashtable<String, ContenidoDeAudios>();
	}
	
	public static AudiosXML getInstance(){
		if(audiosXML == null)
			audiosXML = new AudiosXML();
		return audiosXML;
	}
	
	private void crearElArchivo(){
		try{
			docFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();
			
			rootElement = doc.createElement("conversacions");
			doc.appendChild(rootElement);
			
		}catch(Exception e){
			System.out.println("Error al crear el archivo");
		}
	}
	
	public boolean exiteLaFraseEnElArchivo(String idFrase){
		boolean resultado = false;

		try{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(archivoDeAudios);

			doc.getDocumentElement().normalize();
			
			NodeList conversaciones = doc.getElementsByTagName("conversacion");
			System.out.println("\nCargando las frases ...\n");

			for (int temp = 0; temp < conversaciones.getLength(); temp++) {

				Node nNode = conversaciones.item(temp);
				
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String idDeLaFrase = eElement.getAttribute("id");
					//System.out.println("\nCurrent Element :" + idDeLaFrase);
					if(idDeLaFrase.equals(idFrase)){
						return true;
					}
				}
			}
		}catch(Exception e){
			System.out.println("Error en el archivo de audios: "+e.getMessage());
		}

		return resultado;
	}
	
	public boolean exiteElArchivoXMLDeAudios(String pathArchivo){
		try{
			this.archivoDeAudios = pathArchivo;
			File file = new File(pathArchivo);
			if(file.exists() && ! file.isDirectory()) {
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			return false;
		}
	}
	
	public void guardarLosAudiosDeUnaFrase(Contenido miContenido){
		
		crearElArchivo();
		
		/*for(int index = 0; index < miTemario.obtenerMisTemas().size(); index ++){
			Tema miTema = miTemario.obtenerMisTemas().get(index);
			Frase[] frases = miTema.obtenerMisFrases();
			for(int indexTema = 0; indexTema < frases.length; indexTema ++){
				if(frases[indexTema].esEstatica())
					escribirUnaFrase(frases[indexTema]);
			}
		}*/
		
		ArrayList<Frase> misFrases = miContenido.obtenerMiFrases();
		for(int index = 0; index < misFrases.size(); index ++){
			escribirUnaFrase(misFrases.get(index));
		}
		
		guardarElArchivoADisco();
	}
	
	private boolean exiteLaFrase(String idFrase){
		return misFrases.containsKey(idFrase);
	}
	
	public boolean hayQueGenerarAudios(String idFrase, String textoDeLaFrase, int posicionDeLaFrase){
		try{
			if(exiteLaFrase(idFrase)){
				textoDeLaFrase = textoDeLaFrase.trim();
				String miTexto = "";
				miTexto = misFrases.get(idFrase).getTextosDeLaFrase()[posicionDeLaFrase].trim();
				
				if(textoDeLaFrase.equals(miTexto))
					return false;
				else
					return true;
			}else{
				return true;
			}
		}catch(Exception e){
			return true;
		}
	}
	
	public boolean hayQueGenerarAudiosImpertinetes(String idFrase, String textoDeLaFrase, int posicionDeLaFrase){
		try{
			if(exiteLaFrase(idFrase)){
				textoDeLaFrase = textoDeLaFrase.trim();
				String miTexto = "";
				miTexto = misFrases.get(idFrase).getTextosImpertinetesDeLaFrase()[posicionDeLaFrase].trim();
				
				if(textoDeLaFrase.equals(miTexto))
					return false;
				else
					return true;
			}else{
				return true;
			}
		}catch(Exception e){
			return true;
		}
	}
	
	public boolean hayQueGenerarAudiosMeRindo(String idFrase, String textoDeLaFrase, int posicionDeLaFrase){
		try{
			if(exiteLaFrase(idFrase)){
				textoDeLaFrase = textoDeLaFrase.trim();
				String miTexto = "";
				miTexto = misFrases.get(idFrase).getTextosDeLaFraseMeRindo()[posicionDeLaFrase].trim();
				
				if(textoDeLaFrase.equals(miTexto))
					return false;
				else
					return true;
			}else{
				return true;
			}
		}catch(Exception e){
			return true;
		}
	}
	
	public String obtenerUnAudioDeLaFrase(String idFrase, int posicionDeLaFrase){
		String resultado = "";
		try{
			if(exiteLaFrase(idFrase)){
				resultado = misFrases.get(idFrase).getSonidosDeLosTextosDeLaFrase()[posicionDeLaFrase];
			}
		}catch(Exception e){
			resultado = "";
		}
		return resultado;
	}
	
	public String obtenerUnAudioDeLaFraseImpertinete(String idFrase, int posicionDeLaFrase){
		String resultado = "";
		try{
			if(exiteLaFrase(idFrase)){
				resultado = misFrases.get(idFrase).getSonidosDeLosTextosImpertinentesDeLaFrase()[posicionDeLaFrase];
			}
		}catch(Exception e){
			resultado = "";
		}
		return resultado;
	}
	
	public String obtenerUnAudioDeLaFraseMeRindo(String idFrase, int posicionDeLaFrase){
		String resultado = "";
		try{
			if(exiteLaFrase(idFrase)){
				resultado = misFrases.get(idFrase).getSonidosDeLosTextosDeLaFraseMeRindo()[posicionDeLaFrase];
			}
		}catch(Exception e){
			resultado = "";
		}
		return resultado;
	}
	public void cargarLosNombresDeLosAudios(){
		misFrases.clear();
		
		try{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(archivoDeAudios);

			doc.getDocumentElement().normalize();
			
			//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			NodeList conversaciones = doc.getElementsByTagName("conversacion");
			System.out.println("\nCargando las frases ...\n");

			for (int temp = 0; temp < conversaciones.getLength(); temp++) {

				Node nNode = conversaciones.item(temp);
				System.out.println("\nCurrent Element :" + nNode.getNodeName());
				
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;
					
					String idDeLaFrase = eElement.getAttribute("id");
					System.out.println("Conversacion Id : " + idDeLaFrase);
					
					String esUnaPregunta = eElement.getElementsByTagName("esUnaPregunta").item(0).getTextContent();
					System.out.println("Tipo : " + esUnaPregunta);
					
					Element frases = (Element) eElement.getElementsByTagName("frases").item(0);
					
					String tipoDeFraseACargar = "frase";
					if(esUnaPregunta.equals("true")){
						tipoDeFraseACargar = "curioso";
					}
					
					ArrayList<String[]> textosDeLaFrase = obtenerFrasesPorTipo(frases, tipoDeFraseACargar);
					ArrayList<String[]> textosImpertinetesDeLaFrase = obtenerFrasesPorTipo(frases, "impertinente");
					ArrayList<String[]> textosMeRindoDeLaFrase = obtenerFrasesPorTipo(frases, "meRindo");
					
					ContenidoDeAudios miFrase = new ContenidoDeAudios(idDeLaFrase, textosDeLaFrase.get(0), textosDeLaFrase.get(1), 
							textosImpertinetesDeLaFrase.get(0), textosImpertinetesDeLaFrase.get(1), 
							textosMeRindoDeLaFrase.get(0), textosMeRindoDeLaFrase.get(1));
					
					misFrases.put(idDeLaFrase, miFrase);
				}
			}
		}catch(Exception e){
			System.out.println("Error en el archivo de audios: "+e.getMessage());
		}
		
	}
	
	private ArrayList<String[]> obtenerFrasesPorTipo(Element frases, String tipoDeFraseACargar){
		NodeList frase = frases.getElementsByTagName(tipoDeFraseACargar);
		ArrayList<String[]> resultado = new ArrayList<>();
		
		String[] textosDeLaFrase = new String[frase.getLength()];
		String[] sonidosDeLaFrase = new String[frase.getLength()];
		
		for (int temp1 = 0; temp1 < frase.getLength(); temp1++) {
			textosDeLaFrase[temp1] = frases.getElementsByTagName(tipoDeFraseACargar).item(temp1).getTextContent();
			Node nNode = frases.getElementsByTagName(tipoDeFraseACargar).item(temp1);
			Element eElement = (Element) nNode;
			sonidosDeLaFrase[temp1] = eElement.getAttribute("audio");
			System.out.println("Frase del xml: " + textosDeLaFrase[temp1]);
			System.out.println("Audios de la frase del xml: " + sonidosDeLaFrase[temp1]);
		}
		
		resultado.add(textosDeLaFrase);
		resultado.add(sonidosDeLaFrase);
		return resultado;
	}
	
	public void escribirUnaFrase(Frase miFrase){
		
		Element conversacion = doc.createElement("conversacion");
		conversacion.setAttribute("id", miFrase.getIdFrase());
		
		Element esUnaPregunta = doc.createElement("esUnaPregunta");
		esUnaPregunta.appendChild(doc.createTextNode(miFrase.esUnaPregunta()+""));
		conversacion.appendChild(esUnaPregunta);
		
		Element frases = doc.createElement("frases");
		if (miFrase.esUnaPregunta()){
			String[] frasesAGuardar = miFrase.getTextosDeLaFrase();
			for (int index = 0; index < frasesAGuardar.length; index ++){
				String frase = frasesAGuardar[index];
				
				Element empName = doc.createElement("curioso");
				empName.appendChild(doc.createTextNode(frase));
				try{
					empName.setAttribute("audio", miFrase.obtenerSonidoAUsar(index).url());
				}catch(Exception e){
					empName.setAttribute("audio", "test.mp3");
				}
				
				frases.appendChild(empName);
			}
			
			String[] frasesImpertinentesAGuardar = miFrase.getTextosImpertinetesDeLaFrase();
			for (int index = 0; index < frasesImpertinentesAGuardar.length; index ++){
				String frase = frasesImpertinentesAGuardar[index];
				
				Element empName = doc.createElement("impertinente");
				empName.appendChild(doc.createTextNode(frase));
				try{
					empName.setAttribute("audio", miFrase.obtenerSonidoImpertinenteAUsar(index).url());
				}catch(Exception e){
					empName.setAttribute("audio", "test.mp3");
				}
				frases.appendChild(empName);
			}
		}else{
			String[] frasesAGuardar = miFrase.getTextosDeLaFrase();
			for (int index = 0; index < frasesAGuardar.length; index ++){
				String frase = frasesAGuardar[index];
				
				Element empName = doc.createElement("frase");
				empName.appendChild(doc.createTextNode(frase));
				try{
					empName.setAttribute("audio", miFrase.obtenerSonidoAUsar(index).url());
				}catch(Exception e){
					empName.setAttribute("audio", "test.mp3");
				}
				frases.appendChild(empName);
			}
		}
		
		String[] frasesMeRindoAGuardar = miFrase.getTextosMeRindoDeLaFrase();
		if(frasesMeRindoAGuardar != null){
			for (int index = 0; index < frasesMeRindoAGuardar.length; index ++){
				String frase = frasesMeRindoAGuardar[index];
				
				Element empName = doc.createElement("meRindo");
				empName.appendChild(doc.createTextNode(frase));
				try{
					empName.setAttribute("audio", miFrase.obtenerSonidoMeRindoAUsar(index).url());
				}catch(Exception e){
					empName.setAttribute("audio", "test.mp3");
				}
				frases.appendChild(empName);
			}
		}
		
		conversacion.appendChild(frases);
		
		rootElement.appendChild(conversacion);
	}
	
	private void guardarElArchivoADisco(){
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		
		System.out.println(rootElement.toString());
		
		try {
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(archivoDeAudios));
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Todos los audios estaticos han sido guardados en el xml: "+archivoDeAudios);
	}
	
	/*private void escribir(String path){
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("CONFIGURATION");
			doc.appendChild(rootElement);
			
			Element browser = doc.createElement("BROWSER");
			browser.appendChild(doc.createTextNode("chrome"));
			rootElement.appendChild(browser);
			
			Element base = doc.createElement("BASE");
			base.appendChild(doc.createTextNode("http:fut"));
			rootElement.appendChild(base);
			Element employee = doc.createElement("EMPLOYEE");
			rootElement.appendChild(employee);
			Element empName = doc.createElement("EMP_NAME");
			empName.appendChild(doc.createTextNode("Anhorn, Irene"));
			employee.appendChild(empName);
			Element actDate = doc.createElement("ACT_DATE");
			actDate.appendChild(doc.createTextNode("20131201"));
			employee.appendChild(actDate);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(path));
			transformer.transform(source, result);
			System.out.println("File saved!");
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}*/
	
	public static void main(String argv[]) {
		AudiosXML file = new AudiosXML();
		System.out.println(file.exiteElArchivoXMLDeAudios("src/main/resources/conversaciones.xml"));
		System.out.println(file.exiteLaFraseEnElArchivo("quiereEnCondominio"));
		
		//file.escribir("C:/Users/SergioAlberto/conversaciones1.xml");
	}
}
