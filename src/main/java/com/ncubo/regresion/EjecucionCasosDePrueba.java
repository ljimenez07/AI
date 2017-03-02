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

import com.ibm.watson.developer_cloud.conversation.v1.model.Entity;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
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
import com.ncubo.logicaDeLasConversaciones.InformacionDelCliente;
import com.ncubo.logicaDeLasConversaciones.TemariosDeUnCliente;

public class EjecucionCasosDePrueba {

	private static TemariosDeUnCliente temario;

	private static ConversacionesDeLaRegresion misConversaciones = new ConversacionesDeLaRegresion();
	
	private static ArrayList<Resultado> resultados = new ArrayList<Resultado>();
	
	public ArrayList<Resultado> correrCasosDesdeXML(String xmlFrases, String xmlCasos, String xmlTestNG, String nombreSuite, String user, String password, String cluster, String collection, String ranker) throws Exception{
		
		resultados.clear();
		temario = new TemariosDeUnCliente(xmlFrases); //xml de conversaciones
		
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
			
			correrUnCaso(idConversacion, descripcion, user, password, cluster, collection, ranker);
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

	public boolean correrUnCaso(int idConversacion, String descripcion, String user, String password, String cluster, String collection, String ranker){
		
		ConsultaDao consultaDao = new ConsultaDao();
		FiltroDeConversaciones filtro = new FiltroDeConversaciones();

		Cliente cliente = null;
		try {
			cliente = new Cliente("Regresion", "123456789");
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		
		InformacionDelCliente informacionDelCliente = new InformacionDelCliente("test", "test", "");
		Conversacion miconversacion = new Conversacion(cliente, consultaDao,new AgenteDeLaRegresion(temario), informacionDelCliente, user, password, cluster, collection, ranker);

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
		for(Dialogo dialogo: conversacion.verHistorialDeLaConversacion()){
			if(!dialogo.getLoQueDijoElParticipante().equals(""))
			{
				try {
					salidasParaElCliente = miconversacion.analizarLaRespuestaConWatson(dialogo.getLoQueDijoElParticipante(), true);
				} catch (Exception e) {
					reintentarRespuestaAWatson(dialogo.getLoQueDijoElParticipante(), 3, miconversacion);
				}
				if(salidasParaElCliente.equals(null)){
					status = false;
					observaciones.add("Problemas de comunicación con Watson. El caso es interrumpido");
					break;
				}
				else{
					contadorSalidas = 0;
	
					MessageResponse response = null;
					try{
						response = salidasParaElCliente.get(contadorSalidas).obtenerLaRespuestaDeIBM().messageResponse();
					}catch(Exception e){
						observaciones.add("Problemas de comunicación con Watson. El caso es interrumpido");
						break;
					}
					List<Entity> listaDeEntidadesDeWatson = response.getEntities();
					String laEntidadQueTrajoLaBD = dialogo.getEntidades();
					
					
					boolean laListaDeEntidadesNoEstaVacia = listaDeEntidadesDeWatson.size() > 0;
					boolean lasEntidadesCoinciden = false;
	
					if (laListaDeEntidadesNoEstaVacia) 
					{
	
						String lasEntidadesDeLaBD[] = laEntidadQueTrajoLaBD.split(",");
						boolean hayLaMismaCantidadDeEntidades = lasEntidadesDeLaBD.length == listaDeEntidadesDeWatson.size();
						
						if (hayLaMismaCantidadDeEntidades)
						{
	
							for (int i = 0; i < listaDeEntidadesDeWatson.size(); i++) 
							{
	
								String laEntidadQueTrajoWatson = listaDeEntidadesDeWatson.get(i).getEntity();
								boolean seTrataDeUnSys = laEntidadQueTrajoWatson.startsWith(Constantes.ENTIDAD_SYS);
	
								if (seTrataDeUnSys) 
								{
									lasEntidadesCoinciden = laEntidadQueTrajoWatson.equals(lasEntidadesDeLaBD[i]);
									if (lasEntidadesCoinciden) 
									{
										observaciones.add("Las entidades coinciden correctamente. La entidad evaluada fue: "+lasEntidadesDeLaBD[i] );
									}
									else
									{
										observaciones.add("Error las entidades no coinciden correctamente. Se esperaba la entidad: "+lasEntidadesDeLaBD[i] );
									}
								}
								else
								{
									laEntidadQueTrajoLaBD = lasEntidadesDeLaBD[i];
									laEntidadQueTrajoWatson = laEntidadQueTrajoWatson+ ":" + listaDeEntidadesDeWatson.get(i).getValue();
									lasEntidadesCoinciden = laEntidadQueTrajoWatson.equals(laEntidadQueTrajoLaBD);
	
									if (lasEntidadesCoinciden) 
									{
										observaciones.add("Las entidades coinciden correctamente. La entidad evaluada fue: "+lasEntidadesDeLaBD[i] );
									}
									else
									{
										observaciones.add("Error las entidades no coinciden correctamente. Se esperaba la entidad: : "+lasEntidadesDeLaBD[i] );
									}
								}
							}
						}else{
							observaciones.add("La cantidad de entidades no coinciden, la BD parece tener "+lasEntidadesDeLaBD.length+" entidades mientras que Watson responde que tiene "+listaDeEntidadesDeWatson.size()+" entidades" );
						}
					}
				}
			}
			if(!dialogo.getElTextoQueDijoElFramework().equals("")){
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
		return status;
	}
	
	public ArrayList<Resultado> getResultados() {
		return resultados;
	}

	public void setResultados(ArrayList<Resultado> resultados) {
		EjecucionCasosDePrueba.resultados = resultados;
	}
	
	public ArrayList<Salida> reintentarRespuestaAWatson(String texto, int maximo, Conversacion miConversacion){
		
		ArrayList<Salida> salida = null;
		if(maximo == 0)
			return salida;
		else{
			try {
				salida = miConversacion.analizarLaRespuestaConWatson(texto, true);
			} catch (Exception e) {
				// TODO Auto-generated catch block	
				reintentarRespuestaAWatson(texto, maximo--, miConversacion);
			}			
		}
		return salida;
	}
	
	public EjecucionCasosDePrueba(){}
}
