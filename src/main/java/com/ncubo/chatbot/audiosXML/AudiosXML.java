package com.ncubo.chatbot.audiosXML;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
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
import com.ncubo.logicaDeLasConversaciones.TemarioDelCliente;
import com.ncubo.logicaDeLasConversaciones.TemariosDeUnCliente;

public class AudiosXML {

	private String archivoDeAudios;
	private DocumentBuilderFactory docFactory = null;
	private DocumentBuilder docBuilder = null;
	private Document doc = null;
	private Element rootElement = null;
	private Hashtable<String, ContenidoDeAudios> misFrases;
	
	public AudiosXML(String archivoDeAudios){
		this.archivoDeAudios = archivoDeAudios;
		misFrases = new Hashtable<String, ContenidoDeAudios>();
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
	
	public void guardarLosAudiosDeUnaFrase(TemariosDeUnCliente temarios){
		
		crearElArchivo();
		
		Iterator<TemarioDelCliente> misTemarios = temarios.obtenerLosTemariosDelCliente();
		while(misTemarios.hasNext()){
			TemarioDelCliente temario = misTemarios.next();
			Contenido miContenido = temario.contenido();
			
			Element temarioXML = doc.createElement("temario");
			temarioXML.setAttribute("id", miContenido.getIdContenido());
			temarioXML.setAttribute("nombre", miContenido.getNombreDelContenido());
			
			ArrayList<Frase> misFrases = miContenido.obtenerMiFrases();
			for(int index = 0; index < misFrases.size(); index ++){
				escribirUnaFrase(misFrases.get(index), temarioXML);
			}
			
			rootElement.appendChild(temarioXML);
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

	public String obtenerUnAudioDeLaFrase(String nombreDeLaFrase, String idAudio, int posicion){
		String resultado = "";
		try{
			if(existeLaFrase(nombreDeLaFrase)){
				ArrayList<ComponentesDeLaFrase> misSinonimos = misFrases.get(nombreDeLaFrase).obtenerMisSinonimosDeLaFrase();
				resultado = misSinonimos.get(posicion).getAudio(idAudio).url();
			}
		}catch(Exception e){
			resultado = "";
		}
		return resultado;
	}
	
	public String obtenerUnAudioDeLaFrase(String nombreDeLaFrase, String textoAudio){
		String resultado = "";
		try{
			if(existeLaFrase(nombreDeLaFrase)){
				for(ComponentesDeLaFrase miFrase: misFrases.get(nombreDeLaFrase).obtenerMisSinonimosDeLaFrase()){
					Hashtable<String, Sonido> audios = miFrase.getAudios();
					Enumeration<String> llaves = audios.keys();
					while (llaves.hasMoreElements()) {
						String llave = llaves.nextElement();
						if(audios.get(llave).getTextoUsadoParaGenerarElSonido().equals(textoAudio))
							resultado = audios.get(llave).url();	
					}
				}
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

	private void obtenerFrasesPorTipo(ArrayList<ComponentesDeLaFrase> misSinonimosDeLasConjunciones, Element frases){
		NodeList misFrase = frases.getChildNodes();
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
		miSinonimoDeLaFrase.setAudio("audio", new Sonido(audio, textoDelAudio));
		misSinonimosDeLasConjunciones.add(miSinonimoDeLaFrase);
	}

	public void escribirUnaFrase(Frase miFrase, Element temarioXML){
		
		Element conversacion = doc.createElement("conversacion");
		conversacion.setAttribute("nombre", miFrase.obtenerNombreDeLaFrase());
		
		Element frases = doc.createElement("frases");
		for(ComponentesDeLaFrase sinonimoDeFrase: miFrase.obtenerMisSinonimosDeLaFrase()){
			if(sinonimoDeFrase.tienePlaceholders() && miFrase.soloTieneEnum(sinonimoDeFrase)){
				Hashtable<String, Sonido> audios = sinonimoDeFrase.getAudios();
				
				Enumeration<String> llaves = audios.keys();
				while (llaves.hasMoreElements()) {
					String llave = llaves.nextElement();
					
					Element empName = doc.createElement(sinonimoDeFrase.getTipoDeFrase());
					empName.appendChild(doc.createTextNode(audios.get(llave).getTextoUsadoParaGenerarElSonido()));
					try{
						empName.setAttribute("audio", audios.get(llave).url());
					}catch(Exception e){
						empName.setAttribute("audio", "audioDinamico.mp3");
					}
					frases.appendChild(empName);
				}
			}
			else{
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
		conversacion.appendChild(frases);
		temarioXML.appendChild(conversacion);
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
	
	public static void main(String argv[]) {
		AudiosXML file = new AudiosXML("src/main/resources/conversaciones.xml");
		System.out.println(file.exiteLaFraseEnElArchivo("quiereEnCondominio"));
	}
}
