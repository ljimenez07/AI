package com.ncubo.chatbot.partesDeLaConversacion;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.configuracion.Constantes.ModoDeLaVariable;
import com.ncubo.chatbot.configuracion.Constantes.TiposDeVariables;
import com.ncubo.chatbot.contexto.Variable;
import com.ncubo.chatbot.contexto.VariablesDeContexto;
import com.ncubo.chatbot.exceptiones.ChatException;
import com.ncubo.chatbot.parser.Operador;
import com.ncubo.chatbot.parser.Operador.TipoDeOperador;
import com.ncubo.chatbot.watson.Entidad;
import com.ncubo.chatbot.watson.Entidades;
import com.ncubo.chatbot.watson.Intencion;
import com.ncubo.chatbot.watson.Intenciones;
import com.ncubo.chatbot.watson.WorkSpace;

public abstract class CargadorDeContenido {

	private ModoDeLaVariable modoDeTrabajo;
	private ArrayList<Contenido> misContenidos;
	private String pathFileXML;
	
	protected CargadorDeContenido(String path) {
		pathFileXML = path;
		misContenidos = new ArrayList<>();
		modoDeTrabajo = Constantes.ModoDeLaVariable.REAL;
		File archivoDeConfiguracion = archivoDeConfiguracion(path);
		cargarContenidoDelArchivoDeConfiguracion(archivoDeConfiguracion);
	}
	
	protected abstract File archivoDeConfiguracion(String path);
	
	private void cargarContenidoDelArchivoDeConfiguracion(File file){
		
		try {
			Frase miFrase = null;
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);

			doc.getDocumentElement().normalize();

			/*// Entidades
			NodeList lasEntidades = doc.getElementsByTagName("entidades");
			
			Entidades misEntidades = new Entidades();
			try{
				Element entidades = (Element) eElement.getElementsByTagName("entidades").item(0);
				NodeList entidad = entidades.getElementsByTagName("entidad");
				for (int temp1 = 0; temp1 < entidad.getLength(); temp1++) {
					String nombre = entidades.getElementsByTagName("entidad").item(temp1).getTextContent();
					Entidad miEntidad = Entidad.newInstance(nombre, valores);
					misEntidades.agregar(miEntidad);
					System.out.println("Entidad: " +miEntidad.getNombre());
				}
			}catch(Exception e){
				
			}*/
			
			// Modo
			try{
				Element root = doc.getDocumentElement();
				String miModo = root.getAttribute("modo");
				if(miModo.equals("REAL"))
					modoDeTrabajo = ModoDeLaVariable.REAL;
				else
					modoDeTrabajo = ModoDeLaVariable.PRUEBA;
				
				System.out.println("Modo de trabajo: "+modoDeTrabajo);
				if(! modoDeTrabajo.equals(Constantes.ModoDeLaVariable.PRUEBA) && ! modoDeTrabajo.equals(Constantes.ModoDeLaVariable.REAL))
					throw new ChatException("Verifique que el modo de trabajo sea valido (Fake/Real/Test)");
			}catch(Exception e){
				throw new ChatException("Error cargando el modo de trabajo. "+e.getMessage());
			}
			
			// Conjunciones
			try{
				System.out.println("\nCargando las conjunciones ...\n");
				NodeList conjunciones = doc.getElementsByTagName("conjunciones");
				Node conjuncionesNode = conjunciones.item(0);
				Element conjuncionesElement = (Element) conjuncionesNode;
				NodeList conjuncion = conjuncionesElement.getElementsByTagName("conjuncion");
				for (int temp = 0; temp < conjuncion.getLength(); temp++) {
					Node nNode = conjuncion.item(temp);
					Element eElement = (Element) nNode;
					String id = eElement.getAttribute("id");
					String nombre = eElement.getAttribute("nombre");
					String frase = nNode.getTextContent();
					System.out.println("Conjuncion: "+frase);
					ArrayList<ComponentesDeLaFrase> misSinonimosDeLasConjunciones = new ArrayList<ComponentesDeLaFrase>();
					misSinonimosDeLasConjunciones.add(new ComponentesDeLaFrase(Constantes.TIPO_FRASE_CONJUNCION, frase, "", "", ""));
					Conjunciones.getInstance().agregarConjuncion(new Conjuncion(id, nombre, misSinonimosDeLasConjunciones));
				}
			}catch(Exception e){
				throw new ChatException("Error cargando las conjunciones "+e.getMessage());
			}
			
			// Intenciones No Referenciadas
			try{
				System.out.println("\nCargando las intenciones No Referenciadas ...\n");
				NodeList variables = doc.getElementsByTagName("intencionesNoReferenciadas");
				Node variablesNode = variables.item(0);
				Element variablesElement = (Element) variablesNode;
				NodeList variable = variablesElement.getElementsByTagName("intencion");
				for (int temp = 0; temp < variable.getLength(); temp++) {
					Node nNode = variable.item(temp);
					Element eElement = (Element) nNode;
					String id = eElement.getAttribute("id");
					String tipoValor = eElement.getAttribute("tipo");
					String[] frasesDeIntenciones = new String[]{};
					
					NodeList nodoFrases = eElement.getElementsByTagName("frases");
					if(nodoFrases.getLength()>0){
						Node valorNode = nodoFrases.item(0);
						Element valorElement = (Element) valorNode;
						NodeList frase = valorElement.getElementsByTagName("frase");
						frasesDeIntenciones = new String [frase.getLength()];
		
						for (int i = 0; i < frase.getLength(); i++) {
							Node nodoValor = frase.item(i);
							Element elementoValor = (Element) nodoValor;
							String valorPorDefecto = elementoValor.getTextContent();
							frasesDeIntenciones[i] = valorPorDefecto;
						}
					}
					
					if(tipoValor.equals(Constantes.TIPO_INTENCION_SALUDAR)){
						Constantes.INTENCION_SALUDAR = id;
						Constantes.FRASES_INTENCION_SALUDAR = frasesDeIntenciones;
					}
					if(tipoValor.equals(Constantes.TIPO_INTENCION_DESPEDIDA)){
						Constantes.INTENCION_DESPEDIDA = id;
						Constantes.FRASES_INTENCION_DESPEDIDA = frasesDeIntenciones;
					}
					if(tipoValor.equals(Constantes.TIPO_INTENCION_DESPISTADOR)){
						Constantes.INTENCION_DESPISTADOR = id;
						Constantes.FRASES_INTENCION_DESPISTADOR = frasesDeIntenciones;
					}
					if(tipoValor.equals(Constantes.TIPO_INTENCION_ERROR_CON_WATSON)){
						Constantes.INTENCION_ERROR_CON_WATSON = id;
						Constantes.FRASES_INTENCION_ERROR_CON_WATSON = frasesDeIntenciones;
					}
					if(tipoValor.equals(Constantes.TIPO_INTENCION_FUERA_DE_CONTEXTO)){
						Constantes.INTENCION_FUERA_DE_CONTEXTO = id;
						Constantes.FRASES_INTENCION_FUERA_DE_CONTEXTO = frasesDeIntenciones;
					}
					if(tipoValor.equals(Constantes.TIPO_INTENCION_NO_ENTIENDO)){
						Constantes.INTENCION_NO_ENTIENDO = id;
						Constantes.FRASES_INTENCION_NO_ENTIENDO = frasesDeIntenciones;
					}
					if(tipoValor.equals(Constantes.TIPO_INTENCION_REPETIR_ULTIMA_FRASE)){
						Constantes.INTENCION_REPETIR_ULTIMA_FRASE = id;
						Constantes.FRASES_INTENCION_REPETIR = frasesDeIntenciones;
					}
					if(tipoValor.equals(Constantes.TIPO_INTENCION_AGRADECIMIENTO)){
						Constantes.INTENCION_AGRADECIMIENTO = id;
						Constantes.FRASES_INTENCION_AGRADECIMIENTO = frasesDeIntenciones;
					}
					if(tipoValor.equals(Constantes.TIPO_INTENCION_QUE_PUEDEN_PREGUNTAR)){
						Constantes.INTENCION_QUE_PUEDEN_PREGUNTAR = id;
						Constantes.FRASES_INTENCION_QUE_PUEDEN_PREGUNTAR = frasesDeIntenciones;
					}
					if(tipoValor.equals(Constantes.TIPO_INTENCION_PREGUNTAR_POR_OTRA_CONSULTA)){
						Constantes.INTENCION_PREGUNTAR_POR_OTRA_CONSULTA = id;
						Constantes.FRASES_INTENCION_PREGUNTAR_POR_OTRA_CONSULTA = frasesDeIntenciones;
					}
					if(tipoValor.equals(Constantes.TIPO_INTENCION_RECORDAR_TEMA)){
						Constantes.FRASES_INTENCION_RECORDAR_TEMAS = frasesDeIntenciones;
					}
				}
			}catch(Exception e){
				System.out.println("Error cargando las intenciones no referenciadas"+e.getMessage());
			}
						
			// Variables de ambiente
			String[] valores = new String[] {""};
			try{
				System.out.println("\nCargando las variablesDeAmbiente ...\n");
				NodeList variables = doc.getElementsByTagName("variablesDeAmbiente");
				Node variablesNode = variables.item(0);
				Element variablesElement = (Element) variablesNode;
				NodeList variable = variablesElement.getElementsByTagName("variable");
				for (int temp = 0; temp < variable.getLength(); temp++) {
					Node nNode = variable.item(temp);
					Element eElement = (Element) nNode;
					String nombre = eElement.getAttribute("nombre");
					String tipoValor = eElement.getAttribute("tipo");

					NodeList nodoValores = eElement.getElementsByTagName("valores");
					if(nodoValores.getLength()>0){
						Node valorNode = nodoValores.item(0);
						Element valorElement = (Element) valorNode;
						NodeList valor = valorElement.getElementsByTagName("valor");
						valores = new String [valor.getLength()];
		
						for (int i = 0; i < valor.getLength(); i++) {
							Node nodoValor = valor.item(i);
							Element elementoValor = (Element) nodoValor;
							String valorPorDefecto = elementoValor.getTextContent();
							valores[i] = valorPorDefecto;
						}
					}
					VariablesDeContexto.getInstance().agregarVariableAMiContexto(new Variable(nombre, valores, obtenerTipoDeVariable(tipoValor)));
				}
			}catch(Exception e){
				System.out.println("Error cargando las variables de ambiente "+e.getMessage());
			}
						
			NodeList temarios = doc.getElementsByTagName("temario");
			System.out.println("\nCargando los temarios ...\n");
			for (int contadorDeTemarios = 0; contadorDeTemarios < temarios.getLength(); contadorDeTemarios++) {
				Node nodoDelTemario = temarios.item(contadorDeTemarios);
				System.out.println("\nCurrent Element :" + nodoDelTemario.getNodeName());
				
				// WorkSpaces
				ArrayList<WorkSpace> miWorkSpaces = new ArrayList<>();
				try{
					System.out.println("\nCargando los workspaces ...\n");
					NodeList workspaces = doc.getElementsByTagName("workspaces");
					Node workspacesNode = workspaces.item(0);
					Element workspacesElement = (Element) workspacesNode;
					String user = workspacesElement.getAttribute("user");
					String pass = workspacesElement.getAttribute("pass");
					NodeList workspace = workspacesElement.getElementsByTagName("workspace");
					for (int temp = 0; temp < workspace.getLength(); temp++) {
						Node nNode = workspace.item(temp);
						Element eElement = (Element) nNode;
						String nombreDelTopico = eElement.getAttribute("nombreDelTopico");
						String idIBM = eElement.getAttribute("idIBM");
						String intencionesParaSerReferenciado = eElement.getAttribute("intencionesParaSerReferenciado");
						String nombre = nNode.getTextContent();
						System.out.println("NOMBRE: " + nombre);
						System.out.println("Tipo: " + nombreDelTopico);
						miWorkSpaces.add(new WorkSpace(user, pass, idIBM, nombreDelTopico, nombre, intencionesParaSerReferenciado.split(",")));
					}
				}catch(Exception e){
					throw new ChatException("Error cargando los workspaces "+e.getMessage());
				}
				
				// Conversaciones
				ArrayList<Frase> lasFrases = new ArrayList<>();
				NodeList conversaciones = doc.getElementsByTagName("conversacion");
				System.out.println("\nCargando las frases ...\n");
				for (int temp = 0; temp < conversaciones.getLength(); temp++) {

					Node nNode = conversaciones.item(temp);
					System.out.println("\nCurrent Element :" + nNode.getNodeName());
					
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {

						Element eElement = (Element) nNode;
						CaracteristicaDeLaFrase[] caracteristicasDeLaFrase = new CaracteristicaDeLaFrase[3];
						
						String idDeLaFrase = eElement.getAttribute("id");
						int version = Integer.parseInt(eElement.getElementsByTagName("version").item(0).getTextContent());
						System.out.println("Conversacion Id : " + idDeLaFrase);
						
						String nombreDeLaFrase = eElement.getAttribute("nombre");
						System.out.println("Nombre : " + nombreDeLaFrase);
						
						String elTipoEs = eElement.getElementsByTagName("tipo").item(0).getTextContent();
						System.out.println("Tipo : " + elTipoEs);
						
						String esMandatorio = eElement.getElementsByTagName("mandatoria").item(0).getTextContent();
						System.out.println("Mandatorio : " + esMandatorio);
						if(esMandatorio.equals("true")){
							caracteristicasDeLaFrase[0] = CaracteristicaDeLaFrase.esUnaPreguntaMandatoria;
						}else{
							caracteristicasDeLaFrase[0] = CaracteristicaDeLaFrase.noUnaPreguntaMandatoria;
						}
						
						String enVozAlta = eElement.getElementsByTagName("enVozAlta").item(0).getTextContent();
						System.out.println("enVozAlta : " + enVozAlta);
						if(enVozAlta.equals("false")){
							caracteristicasDeLaFrase[1] = CaracteristicaDeLaFrase.noPuedeDecirEnVozAlta;
						}else{
							caracteristicasDeLaFrase[1] = CaracteristicaDeLaFrase.sePuedeDecirEnVozAlta;
						}
						
						int intentosFallidos = Constantes.MAXIMO_DE_INTENTOS_OPCIONALES;
						try {
							intentosFallidos = Integer.parseInt(eElement.getElementsByTagName("intentosFallidos").item(0).getTextContent());
						} catch (Exception e) {
							// TODO Auto-generated catch block
						}
						System.out.println("intentosFallidos : " + intentosFallidos);
					
						// Variables de frase
						try{
							System.out.println("\nCargando las variables de la frase ...\n");
							NodeList variables = eElement.getElementsByTagName("variables");
							Node variablesNode = variables.item(0);
							Element variablesElement = (Element) variablesNode;
							NodeList variable = variablesElement.getElementsByTagName("variable");
							for (int temporal = 0; temporal < variable.getLength(); temporal++) {
								Node nodo = variable.item(temporal);
								Element element = (Element) nodo;
								String nombre = element.getAttribute("nombre");
								String tipoValor = element.getAttribute("tipo");
								
								NodeList nodoValores = eElement.getElementsByTagName("valores");
								Node valorNode = nodoValores.item(0);
								Element valorElement = (Element) valorNode;
								NodeList valor = valorElement.getElementsByTagName("valor");
								valores = new String [valor.getLength()];

								for (int i = 0; i < valor.getLength(); i++) {
									Node nodoValor = valor.item(i);
									Element elementoValor = (Element) nodoValor;
									String valorPorDefecto = elementoValor.getTextContent();
									valores[i] = valorPorDefecto;
								}
								VariablesDeContexto.getInstance().agregarVariableAMiContexto(new Variable(nombre, valores, obtenerTipoDeVariable(tipoValor)));

							}
						}catch(Exception e){}
							 
						Element frases = (Element) eElement.getElementsByTagName("frases").item(0);
						
						Element vinetas = null;
						String[] vinetasDeLaFrase = null;
						try{
							vinetas = (Element) eElement.getElementsByTagName("vinetas").item(0);
							vinetasDeLaFrase = obtenerFrasesPorTipo(vinetas, "vineta");
						}catch(Exception e){}
						
						ArrayList<ComponentesDeLaFrase> misSinonimosDeLasConjunciones = new ArrayList<ComponentesDeLaFrase>();
						obtenerFrasesPorTipo(misSinonimosDeLasConjunciones, frases);
						
						if(elTipoEs.equals("saludo")){
							caracteristicasDeLaFrase[2] = CaracteristicaDeLaFrase.esUnSaludo;
							miFrase = new Saludo(version,idDeLaFrase, nombreDeLaFrase, misSinonimosDeLasConjunciones, vinetasDeLaFrase, intentosFallidos, caracteristicasDeLaFrase);
						}else if(elTipoEs.equals("pregunta")){
							caracteristicasDeLaFrase[2] = CaracteristicaDeLaFrase.esUnaPregunta;
							miFrase = new Pregunta(version ,idDeLaFrase, nombreDeLaFrase, misSinonimosDeLasConjunciones, vinetasDeLaFrase, caracteristicasDeLaFrase, 
									obtenerEntidades((Element) eElement.getElementsByTagName("condiciones").item(0)), 
									obtenerIntenciones((Element) eElement.getElementsByTagName("condiciones").item(0)),intentosFallidos);
						}else if(elTipoEs.equals("afirmativa")){
							caracteristicasDeLaFrase[2] = CaracteristicaDeLaFrase.esUnaOracionAfirmativa;
							miFrase = new Afirmacion(version, idDeLaFrase, nombreDeLaFrase, misSinonimosDeLasConjunciones, vinetasDeLaFrase, intentosFallidos, caracteristicasDeLaFrase);
						}else if(elTipoEs.equals("despedida")){
							caracteristicasDeLaFrase[2] = CaracteristicaDeLaFrase.esUnaDespedida;
							miFrase = new Despedida(version,idDeLaFrase, nombreDeLaFrase, misSinonimosDeLasConjunciones, vinetasDeLaFrase, intentosFallidos, caracteristicasDeLaFrase);
						}

						System.out.println("Agregando frase: " +miFrase.obtenerNombreDeLaFrase());
						lasFrases.add(miFrase);
					}
				}
				
				// Temas
				Temas temasDelDiscurso = new Temas();
				Hashtable<String, DependenciasDeLaFrase> misDependencias = new Hashtable<>();
				try{
					System.out.println("\nCargando los temas ...\n");
					NodeList temas = doc.getElementsByTagName("temas");
					Node temasNode = temas.item(0);
					Element temasElement = (Element) temasNode;
					NodeList tema = temasElement.getElementsByTagName("tema");
					for (int temp = 0; temp < tema.getLength(); temp++) {
						Node nNode = tema.item(temp);
						Element eElement = (Element) nNode;
						
						String idDelTema = eElement.getElementsByTagName("idDelTema").item(0).getTextContent();
						System.out.println("idDelTema : " + idDelTema);
						
						String nombreDelTema = eElement.getElementsByTagName("nombreDelTema").item(0).getTextContent();
						System.out.println("nombreDelTema : " + nombreDelTema);
						
						String descripcionDelTema = eElement.getElementsByTagName("descripcionDelTema").item(0).getTextContent();
						System.out.println("descripcionDelTema : " + descripcionDelTema);
						
						String nombreWorkspace = eElement.getElementsByTagName("nombreWorkspace").item(0).getTextContent();
						System.out.println("nombreWorkspace : " + nombreWorkspace);
						
						String sePuedeRepetir = eElement.getElementsByTagName("sePuedeRepetir").item(0).getTextContent();
						System.out.println("sePuedeRepetir : " + sePuedeRepetir);
						
						String idDeLaIntencionGeneral = eElement.getElementsByTagName("idDeLaIntencionGeneral").item(0).getTextContent();
						System.out.println("idDeLaIntencionGeneral : " + idDeLaIntencionGeneral);
						
						// Frases
						NodeList frases = eElement.getElementsByTagName("frases");
						Node frasesNode = frases.item(0);
						Element frasesElement = (Element) frasesNode;
						NodeList frase = frasesElement.getElementsByTagName("frase");
						Frase frasesACargar[] = new Frase[frase.getLength()];
						for (int index = 0; index < frase.getLength(); index++) {
							System.out.println(frase.item(index).getTextContent());
							frasesACargar[index] = this.frase(lasFrases, frase.item(index).getTextContent());
						}
						
						Tema temaACargar = new Tema(idDelTema, nombreDelTema, descripcionDelTema, nombreWorkspace, 
								Boolean.parseBoolean(sePuedeRepetir), idDeLaIntencionGeneral, frasesACargar);
						temasDelDiscurso.add(temaACargar);
						
						// Dependencias
						try{
							NodeList dependencias = eElement.getElementsByTagName("dependencias");
							Node dependenciasNode = dependencias.item(0);
							Element dependenciasElement = (Element) dependenciasNode;
							NodeList dependencia = dependenciasElement.getElementsByTagName("dependencia");
							DependenciasDeLaFrase lasDependencias = new DependenciasDeLaFrase();
							for (int index = 0; index < dependencia.getLength(); index++) {
								System.out.println(dependencia.item(index).getTextContent());
								lasDependencias.agregarDependencia(dependencia.item(index).getTextContent());
							}
							if(! lasDependencias.obtenerMisDependencias().isEmpty())
								misDependencias.put(nombreDelTema, lasDependencias);
						}catch(Exception e){}
					}
				}catch(Exception e){
					throw new ChatException("Error cargando los temas "+e.getMessage());
				}
				
				Contenido contenido = new Contenido(modoDeTrabajo, temasDelDiscurso, lasFrases, miWorkSpaces, misDependencias);
				misContenidos.add(contenido);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		end();
		
	}

	public ArrayList<Contenido> obtenerMisContenidos(){
		return misContenidos;
	}
	
	public Frase frase(ArrayList<Frase> frases, String nombreDeLaFrase){
		
		for(Frase frase: frases){
			if(frase.obtenerNombreDeLaFrase().equalsIgnoreCase(nombreDeLaFrase)){
				return frase;
			}
		}
		
		throw new ChatException(
			String.format("En el archivo de contenido '%s' no hay ninguna frase cuyo id sea '%s'", archivoDeConfiguracion(pathFileXML).getAbsoluteFile(), nombreDeLaFrase)
		);
	}

	protected CargadorDeContenido end()
	{
		//Warning si hay cosas en el XML que no se estan usando en el contenido
		// Validar que solo exista un saludo y una despedida en el xml
		// Validar que un id no se repita
		// Validar que al menos haya una pregunta o frase en el xml
		// Validar si una frase afirmativa tiene entidades o intenciones REVENTAR
		// Validar que la preguntas tengan por lo menos una intencion o entidad
		return this;
	}
	
	private Entidades obtenerEntidades(Element condition){
		Entidades misEntidades = new Entidades();
		Hashtable<String, Operador> valores;
		
		try{
			NodeList frase = condition.getElementsByTagName("condicion");
			
			for (int temp = 0; temp < frase.getLength(); temp++) {
				Node nNode = frase.item(temp);
				Element eElement = (Element) nNode;
				String tipo = eElement.getAttribute("tipo");
				valores = new Hashtable<String, Operador>();
				
				if(tipo.equals("entidad")){
					String entidadValor = condition.getElementsByTagName("condicion").item(temp).getTextContent();
					String entidadValores[] = entidadValor.split("@");
					String entidad = "";
					String valor = "";
					if(entidadValores.length > 1){
						entidad = entidadValores[0];
						valor = entidadValores[1];
					}else{
						entidad = entidadValores[0];
					}
					valores.put(valor, new Operador(obtenerTipoDeOperador(tipo)));
					System.out.println("Entidad : " + entidad);
					//System.out.println("Entidad tipo: " + tipo);
					//System.out.println("Entidad operador: " + operador);
					misEntidades.agregar(new Entidad(entidad, valores));
				}
			}
		}catch(Exception e){
			
		}
		return misEntidades;
	}
	
	private TiposDeVariables obtenerTipoDeVariable(String tipo){
		if(tipo.equals(Constantes.VARIABLE_TIPO_CONTEXTO)){
			return TiposDeVariables.CONTEXTO;
		}else if(tipo.equals(Constantes.VARIABLE_TIPO_NEGOCIO)){
			return TiposDeVariables.NEGOCIO;
		}else if(tipo.equals(Constantes.VARIABLE_TIPO_USUARIO)){
			return TiposDeVariables.USUARIO;
		}else if(tipo.equals(Constantes.VARIABLE_TIPO_SISTEMA)){
			return TiposDeVariables.SISTEMA;
		}else if(tipo.equals(Constantes.VARIABLE_TIPO_ENUM)){
			return TiposDeVariables.ENUM;
		}
		
		throw new ChatException(String.format("El tipo de variable '%s' no se encuentra definida en el sistema.", tipo));
	}
	
	private Intenciones obtenerIntenciones(Element condition){
		Intenciones misIntenciones = new Intenciones();
		Hashtable<String, Operador> valores;
		
		try{
			NodeList frase = condition.getElementsByTagName("condicion");
			
			for (int temp = 0; temp < frase.getLength(); temp++) {
				Node nNode = frase.item(temp);
				Element eElement = (Element) nNode;
				String tipo = eElement.getAttribute("tipo");
				valores = new Hashtable<String, Operador>();
				
				if(tipo.equals("intencion")){
					String operador = eElement.getAttribute("operador");
					String intencion = condition.getElementsByTagName("condicion").item(temp).getTextContent();
					valores.put(intencion, new Operador(obtenerTipoDeOperador(tipo)));
					System.out.println("Intencion : " + intencion);
					System.out.println("Entidad tipo: " + tipo);
					System.out.println("Entidad operador: " + operador);
					misIntenciones.agregar(new Intencion(intencion, new Operador(obtenerTipoDeOperador(operador))));
				}
			}
		}catch(Exception e){}
		
		return misIntenciones;
	}
	
	private TipoDeOperador obtenerTipoDeOperador(String tipo){
		if(tipo.toUpperCase().equals("AND")) return TipoDeOperador.AND;
		else return TipoDeOperador.OR;
	}
	
	private String[] obtenerFrasesPorTipo(Element frases, String tipoDeFraseACargar){
		NodeList frase = frases.getElementsByTagName(tipoDeFraseACargar);
		String[] textosDeLaFrase = new String[frase.getLength()];
		for (int temp1 = 0; temp1 < frase.getLength(); temp1++) {
			textosDeLaFrase[temp1] = frases.getElementsByTagName(tipoDeFraseACargar).item(temp1).getTextContent();
			System.out.println("Frase : " + textosDeLaFrase[temp1]);
		}
		return textosDeLaFrase;
	}
	
	private void obtenerFrasesPorTipo(ArrayList<ComponentesDeLaFrase> misSinonimosDeLasConjunciones, Element frases){
		
		NodeList misFrase = frases.getChildNodes();
		// NodeList misFrase = frases.getElementsByTagName("*");
		
		for (int index = 0; index < misFrase.getLength(); index++) {
			NodeList misComponentesDeLaFrase = misFrase.item(index).getChildNodes();
			String tipo = misFrase.item(index).getNodeName();
			
			if(tipo.contains(Constantes.TIPO_FRASE_GERERAL)){
				System.out.println(Constantes.TIPO_FRASE_GERERAL);
				crearComponentesDeLaFrase(misSinonimosDeLasConjunciones, Constantes.TIPO_FRASE_GERERAL, misComponentesDeLaFrase);
			}else if (tipo.contains(Constantes.TIPO_FRASE_IMPERTINENTE)){
				System.out.println(Constantes.TIPO_FRASE_IMPERTINENTE);
				crearComponentesDeLaFrase(misSinonimosDeLasConjunciones, Constantes.TIPO_FRASE_IMPERTINENTE, misComponentesDeLaFrase);
			}else if (tipo.contains(Constantes.TIPO_FRASE_ME_RINDO)){
				System.out.println(Constantes.TIPO_FRASE_ME_RINDO);
				crearComponentesDeLaFrase(misSinonimosDeLasConjunciones, Constantes.TIPO_FRASE_ME_RINDO, misComponentesDeLaFrase);
			}
		}
	}
	
	private void crearComponentesDeLaFrase(ArrayList<ComponentesDeLaFrase> misSinonimosDeLasConjunciones, String tipoFrase, NodeList misComponentesDeLaFrase){
		
		String textoDeLaFrase = "";
		String textoAUsarParaGenerarElAudio = "";
		String vineta = "";
		String condicion = "";
		
		for (int index = 0; index < misComponentesDeLaFrase.getLength(); index++) {

			String tipo = misComponentesDeLaFrase.item(index).getNodeName();
			String frase = misComponentesDeLaFrase.item(index).getTextContent();
			//frase = frase.replace("@@", "<").replace("##", ">").replace("@@!", "&nbsp;").replace("#and@", "&&").replace("#or@", "||");
			
			if(! frase.isEmpty() && ! tipo.contains("#text")){
				if(tipo.contains("texto")){
					textoDeLaFrase = frase;
				}else if (tipo.contains("sonido")){
					textoAUsarParaGenerarElAudio = frase;
				}else if (tipo.contains("vineta")){
					vineta = frase;
				}else if (tipo.contains("soloSi")){
					condicion = frase;
				}
				System.out.println("Frase: "+frase);
			}
		}
		
		misSinonimosDeLasConjunciones.add(new ComponentesDeLaFrase(tipoFrase, textoDeLaFrase, textoAUsarParaGenerarElAudio, vineta, condicion));
	}
	
	public static void main(String argv[]) {
		CargadorDeContenido contenido = new CargadorDeContenido("src/main/resources/conversacionesMuni1.xml"){
			@Override
			protected File archivoDeConfiguracion(String path) {
				// TODO Auto-generated method stub
				return new File(path);
				//return new File(Constantes.PATH_ARCHIVO_DE_CONFIGURACION);
			}
		};
		//contenido.generarAudioEstatico();
	}
}
