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
import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.partesDeLaConversacion.ComponentesDeLaFrase;
import com.ncubo.chatbot.partesDeLaConversacion.Contenido;
import com.ncubo.chatbot.partesDeLaConversacion.Frase;
import com.ncubo.chatbot.partesDeLaConversacion.Sonido;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
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
			
			rootElement = doc.createElement("conversaciones");
			doc.appendChild(rootElement);
			
		}catch(Exception e){
			System.out.println("Error al crear el archivo");
		}
	}
	
	public boolean exiteLaFraseEnElArchivo(String nombreFrase){
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
					String nombreDeLaFrase = eElement.getAttribute("nombre");
					//System.out.println("\nCurrent Element :" + idDeLaFrase);
					if(nombreDeLaFrase.equals(nombreFrase)){
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
	
	private boolean existeLaFrase(String nombreDeLaFrase){
		return misFrases.containsKey(nombreDeLaFrase);
	}
	
	
	public boolean hayQueGenerarAudios(String nombreDeLaFrase, String textoDeLaFrase){
		try{
			if(existeLaFrase(nombreDeLaFrase)){
				textoDeLaFrase = textoDeLaFrase.trim();
				for(ComponentesDeLaFrase miFrase: misFrases.get(nombreDeLaFrase).obtenerMisSinonimosDeLaFrase()){
					String miTexto = miFrase.getTextoAUsarParaGenerarElAudio().trim();
					if(textoDeLaFrase.equals(miTexto))
						return false;	
				}
				return true;
			}else{
				return true;
			}
		}catch(Exception e){
			return true;
		}
		
	}
	/*public boolean hayQueGenerarAudiosImpertinetes(String nombreDeLaFrase, String textoDeLaFrase, int posicionDeLaFrase){
		try{
			if(exiteLaFrase(nombreDeLaFrase)){
				textoDeLaFrase = textoDeLaFrase.trim();
				String miTexto = "";
				miTexto = misFrases.get(nombreDeLaFrase).getTextosImpertinetesDeLaFrase()[posicionDeLaFrase].trim();
				
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
	
	public boolean hayQueGenerarAudiosMeRindo(String nombreDeLaFrase, String textoDeLaFrase, int posicionDeLaFrase){
		try{
			if(exiteLaFrase(nombreDeLaFrase)){
				textoDeLaFrase = textoDeLaFrase.trim();
				String miTexto = "";
				miTexto = misFrases.get(nombreDeLaFrase).getTextosDeLaFraseMeRindo()[posicionDeLaFrase].trim();
				
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
	}*/

	
	public String obtenerUnAudioDeLaFrase(String nombreDeLaFrase, String idAudio){
		String resultado = "";
		try{
			if(existeLaFrase(nombreDeLaFrase)){
				for(ComponentesDeLaFrase miFrase: misFrases.get(nombreDeLaFrase).obtenerMisSinonimosDeLaFrase())
					if(miFrase.getAudios().containsKey(idAudio))
						resultado = miFrase.getAudio(idAudio).url();
			}
		}catch(Exception e){
			resultado = "";
		}
		return resultado;
	}
	
	/*public String obtenerUnAudioDeLaFraseImpertinete(String nombreDeLaFrase, int posicionDeLaFrase){
		String resultado = "";
		try{
			if(exiteLaFrase(nombreDeLaFrase)){
				resultado = misFrases.get(nombreDeLaFrase).getSonidosDeLosTextosImpertinentesDeLaFrase()[posicionDeLaFrase];
			}
		}catch(Exception e){
			resultado = "";
		}
		return resultado;
	}
	
	public String obtenerUnAudioDeLaFraseMeRindo(String nombreDeLaFrase, int posicionDeLaFrase){
		String resultado = "";
		try{
			if(exiteLaFrase(nombreDeLaFrase)){
				resultado = misFrases.get(nombreDeLaFrase).getSonidosDeLosTextosDeLaFraseMeRindo()[posicionDeLaFrase];
			}
		}catch(Exception e){
			resultado = "";
		}
		return resultado;
	}*/
	
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
					
					String nombreDeLaFrase = eElement.getAttribute("nombre");
					System.out.println("Nombre : " + nombreDeLaFrase);
					
					Element frases = (Element) eElement.getElementsByTagName("frases").item(0);
					/*
					String tipoDeFraseACargar = "frase";
					if(esUnaPregunta.equals("true")){
						tipoDeFraseACargar = "curioso";
					}
					
					ArrayList<String[]> textosDeLaFrase = obtenerFrasesPorTipo(frases, tipoDeFraseACargar);
					ArrayList<String[]> textosImpertinetesDeLaFrase = obtenerFrasesPorTipo(frases, "impertinente");
					ArrayList<String[]> textosMeRindoDeLaFrase = obtenerFrasesPorTipo(frases, "meRindo");*/
					
					ArrayList<ComponentesDeLaFrase> misSinonimosDeLasConjunciones = new ArrayList<ComponentesDeLaFrase>();
					obtenerFrasesPorTipo(misSinonimosDeLasConjunciones, frases);
					
					ContenidoDeAudios miFrase = new ContenidoDeAudios(nombreDeLaFrase, misSinonimosDeLasConjunciones);
					
					misFrases.put(nombreDeLaFrase, miFrase);
				}
			}
		}catch(Exception e){
			System.out.println("Error en el archivo de audios: "+e.getMessage());
		}
		
	}
	
	/*private ArrayList<String[]> obtenerFrasesPorTipo(Element frases, String tipoDeFraseACargar){
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
	}*/
	
	private void obtenerFrasesPorTipo(ArrayList<ComponentesDeLaFrase> misSinonimosDeLasConjunciones, Element frases){
		
		NodeList misFrase = frases.getChildNodes();
		// NodeList misFrase = frases.getElementsByTagName("*");
		
		for (int index = 0; index < misFrase.getLength(); index++) {
			String tipo = misFrase.item(index).getNodeName();
			if(tipo.contains(Constantes.TIPO_FRASE_GERERAL)){
				System.out.println(Constantes.TIPO_FRASE_GERERAL);
				crearComponentesDeLaFrase(misSinonimosDeLasConjunciones, Constantes.TIPO_FRASE_GERERAL, 
						misFrase.item(index).getAttributes().getNamedItem("audio").toString(), misFrase.item(index).getTextContent().toString());
			}else if (tipo.contains(Constantes.TIPO_FRASE_IMPERTINENTE)){
				System.out.println(Constantes.TIPO_FRASE_IMPERTINENTE);
				crearComponentesDeLaFrase(misSinonimosDeLasConjunciones, Constantes.TIPO_FRASE_IMPERTINENTE, 
						misFrase.item(index).getAttributes().getNamedItem("audio").toString(), misFrase.item(index).getTextContent().toString());
			}else if (tipo.contains(Constantes.TIPO_FRASE_ME_RINDO)){
				System.out.println(Constantes.TIPO_FRASE_ME_RINDO);
				crearComponentesDeLaFrase(misSinonimosDeLasConjunciones, Constantes.TIPO_FRASE_ME_RINDO, 
						misFrase.item(index).getAttributes().getNamedItem("audio").toString(), misFrase.item(index).getTextContent().toString());
			}
		}
	}

	private void crearComponentesDeLaFrase(ArrayList<ComponentesDeLaFrase> misSinonimosDeLasConjunciones, String tipoFrase, String audio, String textoDelAudio){
		
		audio = audio.replace("audio=", "").replace("\"", "");
		System.out.println("Frase del xml: " + textoDelAudio);
		System.out.println("Audios de la frase del xml: " + audio);
		ComponentesDeLaFrase miSinonimoDeLaFrase = new ComponentesDeLaFrase(tipoFrase, "", textoDelAudio, "", "");
		miSinonimoDeLaFrase.setAudio("audio",new Sonido(audio, textoDelAudio));
		misSinonimosDeLasConjunciones.add(miSinonimoDeLaFrase);
	}

	public void escribirUnaFrase(Frase miFrase){
		
		Element conversacion = doc.createElement("conversacion");
		conversacion.setAttribute("nombre", miFrase.obtenerNombreDeLaFrase());
		
		//Element esUnaPregunta = doc.createElement("esUnaPregunta");
		//esUnaPregunta.appendChild(doc.createTextNode(miFrase.esUnaPregunta()+""));
		//conversacion.appendChild(esUnaPregunta);
		
		Element frases = doc.createElement("frases");
		
		for(ComponentesDeLaFrase sinonimoDeFrase: miFrase.obtenerMisSinonimosDeLaFrase()){
		
			if(sinonimoDeFrase.tienePlaceholders() && miFrase.soloTieneEnum(sinonimoDeFrase))
			{
				Hashtable<String, Sonido> audios = sinonimoDeFrase.getAudios();
				
				Enumeration<String> llaves = audios.keys();
				
				while (llaves.hasMoreElements()) {
					String llave = llaves.nextElement();
					
					Element empName = doc.createElement(sinonimoDeFrase.getTipoDeFrase());
					
					empName.appendChild(doc.createTextNode(audios.get(llave).getTextoUsadoParaGenerarElSonido()));
					try{
						empName.setAttribute("audio", sinonimoDeFrase.getAudio(llave).url());
	
					}catch(Exception e){
						empName.setAttribute("audio", "test.mp3");
					}
					frases.appendChild(empName);
				}
			}
			
			else
			{
				Element empName = doc.createElement(sinonimoDeFrase.getTipoDeFrase());
				empName.appendChild(doc.createTextNode(sinonimoDeFrase.getTextoAUsarParaGenerarElAudio()));
				
				try{
					empName.setAttribute("audio", sinonimoDeFrase.getAudio("audio").url());

				}catch(Exception e){
					empName.setAttribute("audio", "test.mp3");
				}
				frases.appendChild(empName);

			}
			
			
			
		}
		
		/*if (miFrase.esUnaPregunta()){
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
		}*/
		
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
