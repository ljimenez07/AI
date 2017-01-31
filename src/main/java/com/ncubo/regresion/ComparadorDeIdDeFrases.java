package com.ncubo.regresion;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ncubo.chatbot.bitacora.Dialogo;
import com.ncubo.chatbot.bitacora.LogDeLaConversacion;
import com.ncubo.chatbot.partesDeLaConversacion.Salida;
import com.ncubo.chatbot.partesDeLaConversacion.Temario;
import com.ncubo.chatbot.participantes.Cliente;
import com.ncubo.controller.FiltroDeConversaciones;
import com.ncubo.db.ConexionALaDB;
import com.ncubo.db.ConsultaDao;
import com.ncubo.logicaDeLasConversaciones.Conversacion;

public class ComparadorDeIdDeFrases {

	private static Temario temario;
	
	public static ArrayList<Resultado> compararConversaciones(String xmlFrases, String xmlCasos) throws Exception{
		
		ArrayList<Resultado> resultados = new ArrayList<Resultado>();
		temario = new TemarioDeLaRegresion(xmlFrases); //xml de conversaciones
		ConsultaDao consultaDao = new ConsultaDao();
		
		Cliente cliente = new Cliente("Ricky", "123456");
		Conversacion miconversacion = new Conversacion(temario, cliente, consultaDao,new AgenteDeLaRegresion(temario.contenido().getMiWorkSpaces()));

		ConexionALaDB.getInstance("172.16.60.2", "dmuni", "root", "123456");
		
		

		FiltroDeConversaciones filtro = new FiltroDeConversaciones();

		
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
			
			Vector <String> observaciones = new Vector <String>();
			observaciones.add("\nEjecución del caso: " + descripcion);
			
			int contadorSalidas = 0;
			boolean status = true;
			ArrayList<Salida> salidasParaElCliente = miconversacion.inicializarLaConversacion();
			LogDeLaConversacion conversacion = filtro.obtenerUnaConversacionPorMedioDelId(idConversacion);
			ArrayList<Dialogo> dialogos = conversacion.verHistorialDeLaConversacion();
			for(Dialogo dialogo:dialogos){
				if(!dialogo.getLoQueDijoElParticipante().equals(""))
				{
					salidasParaElCliente = miconversacion.analizarLaRespuestaConWatson(dialogo.getLoQueDijoElParticipante(), true);
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
			Resultado resultado = new Resultado(idConversacion, conversacion, status, miconversacion.obtenerAgenteDeLaMuni().verMiHistorico(), observaciones);
			resultados.add(resultado);
		}
		
		return resultados;
	}

	public static void main(String argv[]) throws Exception {

		ArrayList<Resultado> nuevasConversaciones = compararConversaciones("src/main/resources/conversacionesMuni.xml", "C:/Users/lisjm_000/casosDMuni.xml");

		for (int i = 0; i < nuevasConversaciones.size(); i++) {

			Vector <String> observaciones = nuevasConversaciones.get(i).getObservaciones();
			for (int j = 0; j < observaciones.size(); j++) {
				System.out.println(observaciones.elementAt(j));
			}
		}
		
	/*	temario = new TemarioDeLaRegresion("src/main/resources/conversacionesBanco.xml"); //xml de conversaciones
		ConsultaDao consultaDao = new ConsultaDao();

		Conectores conectores = new Conectores();
		
		Cliente cliente = new Cliente("Ricky", "123456", conectores);
		Conversacion miconversacion = new Conversacion(temario, cliente, consultaDao,new AgenteDeLaRegresion(temario.contenido().getMiWorkSpaces()));

		ArrayList<Salida> salidasParaElCliente;
		String folderDeCasos = argv[0];
		Vector <String> imprimirEvidencias = new Vector<String>();

		File f = new File(folderDeCasos);
		File[] ficheros = f.listFiles();

		for (int x=0; x<ficheros.length; x++)
		{
			if( ! ficheros[x].getName().endsWith(".txt") )
			{
				continue;
			}

			imprimirEvidencias.add("================================================================================================================================================="
					+ "\nProcesando >> "+ficheros[x].getName()+"\n");

			FileInputStream fstream = new FileInputStream(ficheros[x]);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

			String strLine;
			String idObtenidoDeWatson = "";
			boolean elIdDeLaFraseCoincide = false;

			while ((strLine = br.readLine()) != null)
			{

				String mensajeDelUsuario = strLine.split("/")[0].trim();
				String idEsperado = (strLine.split("/")[1].trim()).substring(1);

				salidasParaElCliente = miconversacion.analizarLaRespuestaConWatson(mensajeDelUsuario);

				idObtenidoDeWatson = salidasParaElCliente.get(0).getFraseActual().obtenerIdDeLaFrase();
				elIdDeLaFraseCoincide = idEsperado.equals(idObtenidoDeWatson);

				if (elIdDeLaFraseCoincide) {
					imprimirEvidencias.add("El id del mensaje del usuario '"+mensajeDelUsuario+"' coincide con del xml actual, el cual es '"+idEsperado+"'");
				}else{
					imprimirEvidencias.add("ERROR POSIBLE REGRESION.\n\tSe esperaba el id '"+idEsperado+"' como respuesta a la frase '"+mensajeDelUsuario+"' pero en su lugar se obtuvo '"+idObtenidoDeWatson+"'");
				}
			}
		}
		boolean esUnError = false;
		for (int i = 0; i < imprimirEvidencias.size(); i++) {

			String laFrase = imprimirEvidencias.elementAt(i).substring(0,5);
			esUnError = laFrase.equals("ERROR");
			
			if (esUnError) {
				System.err.println(imprimirEvidencias.elementAt(i));
			}else{
				System.out.println(imprimirEvidencias.elementAt(i));
			}
		}*/
	}
}
