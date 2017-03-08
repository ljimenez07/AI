package com.ncubo.chatbot.participantes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ibm.watson.developer_cloud.conversation.v1.model.Intent;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ncubo.chatbot.bitacora.Dialogo;
import com.ncubo.chatbot.bitacora.LogDeLaConversacion;
import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.configuracion.Constantes.ModoDeLaVariable;
import com.ncubo.chatbot.exceptiones.ChatException;
import com.ncubo.chatbot.partesDeLaConversacion.Frase;
import com.ncubo.chatbot.partesDeLaConversacion.Respuesta;
import com.ncubo.chatbot.partesDeLaConversacion.Salida;
import com.ncubo.chatbot.partesDeLaConversacion.Tema;
import com.ncubo.chatbot.watson.WorkSpace;
import com.ncubo.db.BitacoraDao;
import com.ncubo.db.DetalleDeConversacionDao;
import com.ncubo.db.FrasesDao;
import com.ncubo.logicaDeLasConversaciones.TemarioDelCliente;
import com.ncubo.logicaDeLasConversaciones.TemariosDeUnCliente;
import com.ncubo.niveles.Topico;
import com.ncubo.niveles.Topicos;

public abstract class Agente extends Participante{

	//private final ArrayList<WorkSpace> misWorkSpaces;
	private Topico miTopico;
	private Topico miUltimoTopico;
	private TemariosDeUnCliente misTemarios;
	private Topicos misTopicos;
	private String nombreDeWorkspaceActual;
	private String nombreDeLaIntencionGeneralActiva;
	private boolean hayQueEvaluarEnNivelSuperior;
	private boolean noEntendiLaUltimaRespuesta;
	private int numeroDeIntentosActualesEnRepetirUnaPregunta;
	private boolean cambiarDeTema = false;
	private boolean cambiarDeTemaForzosamente = false;
	private boolean pareceQueQuiereCambiarDeTemaForzosamente = false;
	private boolean abordarElTemaPorNOLoEntendi = false;
	private boolean hayIntencionNoAsociadaANingunWorkspace;
	private LogDeLaConversacion miHistorico = new LogDeLaConversacion();
	private BitacoraDao miBitacora;
	private DetalleDeConversacionDao detalleDeLaConversacion;
	private FrasesDao frasesDelFramework;
	private ArrayList<Intent> lasDosUltimasIntencionesDeConfianza;
	
	public Agente(TemariosDeUnCliente temarios){
		this.noEntendiLaUltimaRespuesta = true;
		this.hayQueEvaluarEnNivelSuperior = true;
		//this.misWorkSpaces = miWorkSpaces;
		this.misTemarios = temarios;
		this.nombreDeLaIntencionGeneralActiva = "";
		this.numeroDeIntentosActualesEnRepetirUnaPregunta = 0;
		this.hayIntencionNoAsociadaANingunWorkspace = false;
		this.inicializarContextos();
		miBitacora = new BitacoraDao();
		detalleDeLaConversacion = new DetalleDeConversacionDao();
		frasesDelFramework = new FrasesDao();
		lasDosUltimasIntencionesDeConfianza = new ArrayList<>();
	}
	
	public Agente(){
		//misWorkSpaces = null;
		miBitacora = new BitacoraDao();
		detalleDeLaConversacion = new DetalleDeConversacionDao();
		frasesDelFramework = new FrasesDao();
		lasDosUltimasIntencionesDeConfianza = new ArrayList<>();
	}
	
	private void inicializarContextos(){
		
		misTopicos = new Topicos();
		Iterator<TemarioDelCliente> temarios = this.misTemarios.obtenerLosTemariosDelCliente();
		while(temarios.hasNext()){
			TemarioDelCliente temario = temarios.next();
			misTopicos.agregarUnTopicoEnElTop(new Topico(temario));
		}
		miTopico = misTopicos.obtenerElTopicoPorDefecto();
		miUltimoTopico = miTopico.clone();
		if(miTopico == null)
			throw new ChatException("No existe un topico por defecto");
		
		nombreDeWorkspaceActual = miTopico.getMiTemario().contenido().getMiWorkSpaces().get(0).getNombre();
	}
	
	public TemarioDelCliente obtenerTemario(){
		return miTopico.getMiTemario();
	}
	
	public MessageResponse llamarAWatson(String mensaje){
		return miTopico.hablarConWatsonEnElNivelSuperior(null, mensaje).messageResponse();
	}
	
	public LogDeLaConversacion verMiHistorico(){
		return miHistorico;
	}
	
	public void cambiarAlTopicoPorDefecto(){
		miUltimoTopico = miTopico.clone();
		misTopicos.agregarUnTopicoEnElTop(miTopico);
		miTopico = misTopicos.obtenerElTopicoPorDefecto();
		if(miTopico == null)
			throw new ChatException("No existe un topico por defecto");
	}
	
	public int guardarUnaConversacionEnLaDB(String idCliente, String idSesion, String idUsuario){
		try {
			int idDeLaConversacion = miBitacora.insertar(idCliente, idSesion, idUsuario, miHistorico);
			for (Dialogo conversacion : miHistorico.verHistorialDeLaConversacion()) {
				int idDeLaFraseGuardada = frasesDelFramework.insertarFrasesDevueltasPorElFramework(conversacion);
				detalleDeLaConversacion.insertarDetalledeLaConversacion(idCliente, conversacion, idDeLaConversacion, idDeLaFraseGuardada);
			}
			miHistorico.limpiarHistorialALaConversacion();
			
			return idDeLaConversacion;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	public Respuesta enviarRespuestaAWatson(String respuestaDelCliente, Frase frase){
		Respuesta respuesta = null;
		if(hayQueEvaluarEnNivelSuperior){
			respuesta = analizarRespuestaInicial(respuestaDelCliente, frase);
		}else{
			respuesta = analizarRespuesta(respuestaDelCliente, frase);
		}
		return respuesta;
	}
	
	public Respuesta analizarRespuestaInicial(String respuestaDelCliente, Frase frase){
		Respuesta respuesta = miTopico.hablarConWatsonEnElNivelSuperior(frase, respuestaDelCliente);
		
		cambiarDeTemaForzosamente = false;
		
		try{
			lasDosUltimasIntencionesDeConfianza = determinarLasDosIntencionDeConfianzaEnUnWorkspace(respuesta.messageResponse().getIntents());
		}catch(Exception e){
			lasDosUltimasIntencionesDeConfianza = new ArrayList<>();
		}
		
		try{ // TODO Buscar si hay mas de una intension de peso ALTO
			String intencionDelCliente = respuesta.obtenerLaIntencionDeConfianzaDeLaRespuesta().getNombre();
			WorkSpace workspace = extraerUnWorkspaceConLaIntencion(intencionDelCliente);
			Topico topico = null;
			
			if(workspace == null){ // && intencionDelCliente.equals("")
				topico = misTopicos.buscarElTopicoDeMayorConfienza(frase, respuestaDelCliente);
				
				if(topico != null){
					System.out.println("Cambiando al WORKSPACE: "+topico.getMiTemario().contenido().getMiWorkSpaces().get(0).getNombre());
					if(frase != null)
						if(frase.obtenerNombreDeLaFrase().contains("preguntarPorOtraConsulta"))
							inicializarTemaEnWatson(respuestaDelCliente, respuesta, false);
					
					miUltimoTopico = miTopico.clone();
					misTopicos.agregarUnTopicoEnElTop(miTopico);
					miTopico = topico;
					nombreDeWorkspaceActual = miTopico.getMiTemario().contenido().getMiWorkSpaces().get(0).getNombre();
					
					respuesta = miTopico.hablarConWatsonEnElNivelSuperior(frase, respuestaDelCliente);
					lasDosUltimasIntencionesDeConfianza = determinarLasDosIntencionDeConfianzaEnUnWorkspace(respuesta.messageResponse().getIntents());
					
					intencionDelCliente = respuesta.obtenerLaIntencionDeConfianzaDeLaRespuesta().getNombre();
					workspace = extraerUnWorkspaceConLaIntencion(intencionDelCliente);
				}
			}
			
			if(workspace != null && ! intencionDelCliente.equals("")){
				nombreDeLaIntencionGeneralActiva = intencionDelCliente;
				cambiarDeTema = true; // Buscar otro tema
				nombreDeWorkspaceActual = workspace.getNombre();
				if(pareceQueQuiereCambiarDeTemaForzosamente){
					cambiarDeTemaForzosamente = true;
					pareceQueQuiereCambiarDeTemaForzosamente = false;
					noEntendiLaUltimaRespuesta = true;
					if(topico == null && ! miUltimoTopico.getMiTemario().contenido().getIdContenido().contains(miTopico.getMiTemario().contenido().getIdContenido()))
						miUltimoTopico = miTopico.clone();
				}else{
					noEntendiLaUltimaRespuesta = false;
				}

				abordarElTemaPorNOLoEntendi = false;
				if(miTopico.getMiTemario().contenido().getNombreDelContenido().equals("Temario General")){
					this.hayIntencionNoAsociadaANingunWorkspace = true;
					hayQueEvaluarEnNivelSuperior = true;
				}
				else{
					this.hayIntencionNoAsociadaANingunWorkspace = false;
					hayQueEvaluarEnNivelSuperior = false;
				}
				numeroDeIntentosActualesEnRepetirUnaPregunta = 0;
			}else{
				if(lasDosUltimasIntencionesDeConfianza.size() >= 2){
					nombreDeLaIntencionGeneralActiva = lasDosUltimasIntencionesDeConfianza.get(0).getIntent();
					cambiarDeTema = true; // Buscar otro tema
					
					noEntendiLaUltimaRespuesta = false;
					
					abordarElTemaPorNOLoEntendi = false;
					this.hayIntencionNoAsociadaANingunWorkspace = false;
					hayQueEvaluarEnNivelSuperior = false;
				}else{
					System.out.println("Intencion no asociada a ningun workspace");
					hayIntencionNoAsociadaANingunWorkspace = false;
					if (! intencionDelCliente.equals("") && workspace != null){
						nombreDeLaIntencionGeneralActiva = intencionDelCliente;	
					}else{
						nombreDeLaIntencionGeneralActiva = Constantes.INTENCION_NO_ENTIENDO;
						hayIntencionNoAsociadaANingunWorkspace = true;
					}
					
					noEntendiLaUltimaRespuesta = true;
					hayQueEvaluarEnNivelSuperior = false;
				}
			}
			
			
			
		}catch(Exception e){
			System.out.println("No hay ninguna intencion real o de confianza");
			nombreDeLaIntencionGeneralActiva = Constantes.INTENCION_NO_ENTIENDO;
			hayIntencionNoAsociadaANingunWorkspace = true;
			noEntendiLaUltimaRespuesta = true;
		}

		return respuesta;
	}
	
	public Respuesta analizarRespuesta(String respuestaDelCliente, Frase frase){
		Respuesta respuesta = miTopico.hablarConWatson(frase, respuestaDelCliente);
		cambiarDeTemaForzosamente = false;
		hayIntencionNoAsociadaANingunWorkspace = false;
		int maximoIntentos = 4;
		if(frase != null)
			maximoIntentos = frase.obtenerNumeroIntentosFallidos();
			
		noEntendiLaUltimaRespuesta = (! (respuesta.entendiLaRespuesta() && ! respuesta.hayAlgunAnythingElse())) && 
				(numeroDeIntentosActualesEnRepetirUnaPregunta != maximoIntentos);
		if(noEntendiLaUltimaRespuesta){
			System.out.println("No entendi la respuesta ...");
			// Validar si es que el usuario cambio de intencion general
			Intent miIntencion = this.determinarLaIntencionGeneral(respuestaDelCliente);
			if(elClienteQuiereCambiarDeIntencionGeneral(miIntencion)){
				System.out.println("Se requiere cambiar a NIVER SUPERIOR ...");
				//WorkSpace workspace = extraerUnWorkspaceConLaIntencion(miIntencion.getIntent());
				//this.seTieneQueGenerarUnNuevoContextoParaWatsonEnElWorkspaceActualConRespaldo();
				cambiarANivelSuperior();
				if( ! respuesta.seTerminoElTema()){
					pareceQueQuiereCambiarDeTemaForzosamente = true;
				}
				
				respuesta = enviarRespuestaAWatson(respuestaDelCliente, frase); // General
				cambiarDeTema = true;
			}else{
				if(frase.esMandatorio()){	
					numeroDeIntentosActualesEnRepetirUnaPregunta ++;
				}
			}
		}else{
			miTopico.actualizarContexto(respuesta.getMiContexto()); // Actualizar contexto
			if(numeroDeIntentosActualesEnRepetirUnaPregunta == maximoIntentos){
				cambiarDeTema = true; // Buscar otro tema
				cambiarANivelSuperior();
				noEntendiLaUltimaRespuesta = true;
				abordarElTemaPorNOLoEntendi = true; // Decir frase me rindo
			}else{
				abordarElTemaPorNOLoEntendi = false;
				cambiarDeTema = respuesta.seTerminoElTema();
				if(cambiarDeTema){
					String laIntencion = respuesta.obtenerLaIntencionDeConfianzaDeLaRespuesta().getNombre();
					if( ! laIntencion.equals("")){
						borrarUnaVariableDelContexto(Constantes.ID_TEMA); // Solo se borra el id cuando el tema termina
						/*if(! laIntencion.equals("afirmacion")){ // TODO Esto no deberia ir aca
							nombreDeLaIntencionGeneralActiva = laIntencion;
						}*/
						seTieneQueGenerarUnNuevoContextoParaWatsonEnElWorkspaceActualConRespaldo();
					}
				}
			}
			numeroDeIntentosActualesEnRepetirUnaPregunta = 0;
		}
		
		borrarUnaVariableDelContexto(Constantes.ANYTHING_ELSE);
	    borrarUnaVariableDelContexto(Constantes.NODO_ACTIVADO);
	    borrarUnaVariableDelContexto(Constantes.ORACIONES_AFIRMATIVAS);
	    borrarUnaVariableDelContexto(Constantes.CAMBIAR_A_GENERAL);
	    borrarUnaVariableDelContexto(Constantes.TERMINO_EL_TEMA);
		borrarUnaVariableDelContexto(Constantes.CAMBIAR_INTENCION);
		
		return respuesta;
	}
	
	public void seTieneQueGenerarUnNuevoContextoParaWatsonEnElWorkspaceActualConRespaldo(){
		// Respaldar la variables de contexto que tengo
		Hashtable<String, String> misVariables = respaldarVariablesDeContexto(miTopico.obtenerElContexto());

		// Generar la nueva converzacion
		seTieneQueGenerarUnNuevoContextoParaWatsonEnElWorkspaceActual();
		
		// Agregar a la nueva converzacion el respaldo de las variables de contexto
		agregarVariablesDeContexto(misVariables);
	}
	
	private Hashtable<String, String> respaldarVariablesDeContexto(String contexto){
		Hashtable<String, String> misVariables = new Hashtable<String, String>();
		
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(contexto);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		Iterator<?> json_keys = jsonObj.keys();
		while( json_keys.hasNext() ){
			String json_key = (String) json_keys.next();
			if( ! json_key.equals("system") && ! json_key.equals("conversation_id")){
				try {
					System.out.println("Respaldando: "+json_key);
					misVariables.put(json_key, jsonObj.get(json_key).toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		
		return misVariables;
	}
	
	public void seTieneQueGenerarUnNuevoContextoParaWatsonEnElWorkspaceActual(){
		miTopico.reiniciarContexto();
	}
	
	private void agregarVariablesDeContexto(Hashtable<String, String> variables){
		Enumeration<String> misLlaves = variables.keys();
		while(misLlaves.hasMoreElements()){
			String key = misLlaves.nextElement();
			activarValiableEnElContextoDeWatson(key, variables.get(key));
		}
	}
	
	private WorkSpace extraerUnWorkspaceConLaIntencion(String nombreDeLaIntencion){
		for(WorkSpace workspace: miTopico.getMiTemario().contenido().getMiWorkSpaces()){
			if(workspace.tieneLaIntencion(nombreDeLaIntencion)){
				return workspace;
			}
		}
		return null;
	}
	
	public void agregarHistorico(Salida miSalida){
		miHistorico.agregarHistorialALaConversacion(miSalida);
	}
	
	public Intent determinarLaIntencionGeneral(String mensaje){
		Intent intencion = determinarLaIntencionDeConfianzaEnUnWorkspace(mensaje);
		return intencion;
	}
	
	private Intent determinarLaIntencionDeConfianzaEnUnWorkspace(String mensaje){
		Intent intencion = null;
		
		try{
			List<Intent> intenciones = llamarAWatson(mensaje).getIntents();
			double confidence = 0;
			
			for(int index = 0; index < intenciones.size(); index ++){
				if(intenciones.get(index).getConfidence() > confidence){
					confidence = intenciones.get(index).getConfidence();
					intencion = intenciones.get(index);
				}
			}
		}catch(Exception e){}
		
		return intencion;
	}
	
	private ArrayList<Intent> determinarLasDosIntencionDeConfianzaEnUnWorkspace(List<Intent> intenciones){
		
		ArrayList<Intent> respuesta = new ArrayList<Intent>();
		
		if(intenciones != null){
			if(! intenciones.isEmpty() && intenciones.size() > 2){
				
				Collections.sort(intenciones, new Comparator<Intent>() {

			        public int compare(Intent laPrimeraIntencion, Intent laSegundaIntencion) {
			            return laSegundaIntencion.getConfidence().compareTo(laPrimeraIntencion.getConfidence());
			        }
			    });
				
				Double valorDeAmbas = intenciones.get(0).getConfidence() + intenciones.get(1).getConfidence();
				if(valorDeAmbas >= 0.8 && intenciones.get(1).getConfidence() >= 0.3){
					WorkSpace elPrimerWorkSpace = extraerUnWorkspaceConLaIntencion(intenciones.get(0).getIntent());
					boolean seEncontroLaPrimeraIntencionAsociadaAUnWorkSpace = elPrimerWorkSpace != null;
					
					WorkSpace elSegundoWorkSpace = extraerUnWorkspaceConLaIntencion(intenciones.get(1).getIntent());
					boolean seEncontroLaSegundaIntencionAsociadaAUnWorkSpace = elSegundoWorkSpace != null;
					
					if(seEncontroLaPrimeraIntencionAsociadaAUnWorkSpace && seEncontroLaSegundaIntencionAsociadaAUnWorkSpace){
						respuesta.add(intenciones.get(0));
						respuesta.add(intenciones.get(1));
						System.out.println("HAY 2 INTENCIONES IMPORTANTES: 1- "+intenciones.get(0).getIntent()+"  2- "+intenciones.get(1).getIntent());
					}
				}
			}
		}
		
		return respuesta;
	}
	
	public void activarTemaEnElContextoDeWatson(String nombreTema){
		activarValiableEnElContextoDeWatson(Constantes.ID_TEMA, nombreTema);
		System.out.println("Contexto modificado: "+miTopico.obtenerElContexto());
	}
	
	public void activarValiableEnElContextoDeWatson(String nombre, String valor){
		String context = miTopico.obtenerElContexto();
		
		System.out.println(context);
		JSONObject obj = null;
		try {
			if(nombre.contains("dialog_node")){
				obj = new JSONObject(context);
				JSONObject obj1 = new JSONObject(obj.getString("system"));
				JSONArray obj2 = new JSONArray(obj1.getString("dialog_stack"));
				System.out.println(obj2);
				for (int contador = 0; contador < obj2.length(); contador ++){
					if(obj2.getJSONObject(contador).getString(nombre) != null)
						obj2.getJSONObject(contador).put(nombre, valor);
				}
				obj1 = obj1.put("dialog_stack", obj2);
				obj = obj.put("system", obj1);;
				System.out.println(obj);
			}else{
				obj = new JSONObject(context);
				obj.put(nombre, valor);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		miTopico.actualizarContexto(obj.toString());
	}
	
	public Respuesta inicializarTemaEnWatson(String respuestaDelCliente, Respuesta respuesta, boolean reiniciarElWorkspace){

		Respuesta miRespuesta = respuesta;
		if(reiniciarElWorkspace){
			miRespuesta = miTopico.hablarConWatson(null, respuestaDelCliente);
			
			try{
				String contexto = new JSONObject(miRespuesta.messageResponse().getContext()).toString();
				miTopico.actualizarContexto(contexto);
			}catch(Exception e){
				System.out.println(String.format("Error al extraer el contexto de watson: %s", e.getStackTrace().toString()));
			}
				
		}else{
			activarValiableEnElContextoDeWatson("dialog_node", "root");
		}
		
		borrarUnaVariableDelContexto(Constantes.ANYTHING_ELSE);
		borrarUnaVariableDelContexto(Constantes.NODO_ACTIVADO);
		borrarUnaVariableDelContexto(Constantes.ORACIONES_AFIRMATIVAS);
		borrarUnaVariableDelContexto(Constantes.CAMBIAR_A_GENERAL);
		borrarUnaVariableDelContexto(Constantes.TERMINO_EL_TEMA);
		borrarUnaVariableDelContexto(Constantes.CAMBIAR_INTENCION);
		
		return miRespuesta;
	}
	
	public void borrarUnaVariableDelContexto(String nombreDeLaVariable){
		String context = miTopico.obtenerElContexto();
		try {
			JSONObject obj = new JSONObject(context);
			obj.remove(nombreDeLaVariable);
			miTopico.actualizarContexto(obj.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String obtenerNodoActivado(MessageResponse response){
		try{
			return response.getContext().get(Constantes.NODO_ACTIVADO).toString();
		}catch(Exception e){
			System.out.println("No existe el id nodo");
			return "";
		}
	}
	
	private boolean elClienteQuiereCambiarDeIntencionGeneral(Intent intencion){
		if(intencion != null){
			return (intencion.getConfidence() >= Constantes.WATSON_CONVERSATION_CONFIDENCE); 
		}else{
			return false;
		}
	}
	
	public void cambiarANivelSuperior(){
		this.hayQueEvaluarEnNivelSuperior = true;
	}
	
	public void yaNoCambiarANivelSuperior(){
		this.hayQueEvaluarEnNivelSuperior = false;
	}
	
	public String obtenerNombreDeLaIntencionGeneralActiva() {
		return this.nombreDeLaIntencionGeneralActiva;
	}
	
	public boolean hayIntencionNoAsociadaANingunWorkspace(){
		return this.hayIntencionNoAsociadaANingunWorkspace;
	}
	
	public boolean entendiLaUltimaPregunta(){
		return ! noEntendiLaUltimaRespuesta;
	}
	
	public boolean hayQueCambiarDeTemaForzosamente(){
		return cambiarDeTemaForzosamente;
	}
	
	public boolean hayQueCambiarDeTema(){
		return cambiarDeTema;
	}
	
	public boolean seTieneQueAbordarElTema(){
		return abordarElTemaPorNOLoEntendi;
	}
	
	public void yaNoSeTieneQueAbordarElTema(){
		abordarElTemaPorNOLoEntendi = false;
	}
	
	public String obtenerNombreDelWorkspaceActual(){
		return nombreDeWorkspaceActual;
	}
	
	public void yaNoCambiarDeTema(){
		cambiarDeTema = false;
	}
	
	public String obtenerElContexto(){
		return miTopico.obtenerElContexto();
	}
	
	public String obtenerMiUltimoContexto(){
		return miTopico.obtenerMiUltimoContexto();
	}
	
	public void cambiarElContexto(String contexto){
		miTopico.actualizarContexto(contexto);
	}
	
	public ArrayList<Intent> obtenerLasDosUltimasIntencionesDeConfianza(){
		ArrayList<Intent> misIntencionesDeConfianza = (ArrayList<Intent>) lasDosUltimasIntencionesDeConfianza.clone();
		lasDosUltimasIntencionesDeConfianza.clear();
		return misIntencionesDeConfianza;
	}
	
	public Topico getMiUltimoTopico() {
		return miUltimoTopico;
	}
	
	public Topico getMiTopico() {
		return miTopico;
	}

	public void setMiTopico(Topico miTopico) {
		if( ! miTopico.getMiTemario().contenido().getIdContenido().equals(this.miTopico.getMiTemario().contenido().getIdContenido())){
			this.miUltimoTopico = this.miTopico.clone();
			misTopicos.agregarUnTopicoEnElTop(this.miTopico);
			misTopicos.agregarUnTopicoEnElTop(miTopico);
			this.miTopico = misTopicos.extraerElSiquienteTopico();
		}
	}
	
	public abstract Salida decirUnaFrase(Frase frase, Respuesta respuesta, Tema tema, Cliente cliente, ModoDeLaVariable modoDeResolucionDeResultadosFinales, String idCliente);
	
	public abstract Salida volverAPreguntarUnaFrase(Frase pregunta, Respuesta respuesta, Tema tema, Cliente cliente, ModoDeLaVariable modoDeResolucionDeResultadosFinales, String idCliente);
	
	public abstract Salida volverAPreguntarUnaFraseConMeRindo(Frase pregunta, Respuesta respuesta, Tema tema, boolean meRindo, Cliente cliente, ModoDeLaVariable modoDeResolucionDeResultadosFinales, String idCliente);
	
	public static void main(String argv[]) throws Exception {
		Agente agente = new Agente() {
			
			@Override
			public Salida volverAPreguntarUnaFraseConMeRindo(Frase pregunta, Respuesta respuesta, Tema tema, boolean meRindo,
					Cliente cliente, ModoDeLaVariable modoDeResolucionDeResultadosFinales, String idCliente) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Salida volverAPreguntarUnaFrase(Frase pregunta, Respuesta respuesta, Tema tema, Cliente cliente,
					ModoDeLaVariable modoDeResolucionDeResultadosFinales, String idCliente) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Salida decirUnaFrase(Frase frase, Respuesta respuesta, Tema tema, Cliente cliente,
					ModoDeLaVariable modoDeResolucionDeResultadosFinales, String idCliente) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		agente.activarValiableEnElContextoDeWatson("dialog_node", "root");
	} 
}
