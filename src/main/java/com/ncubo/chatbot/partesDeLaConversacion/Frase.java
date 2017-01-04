package com.ncubo.chatbot.partesDeLaConversacion;

import java.util.ArrayList;
import java.util.List;

import com.ncubo.chatbot.audiosXML.AudiosXML;
import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.exceptiones.ChatException;
import com.ncubo.chatbot.watson.TextToSpeechWatson;

public abstract class Frase
{
	private final String idFrase;
	
	private final ArrayList<ComponentesDeLaFrase> misSinonimosDeLaFrase;
	
	private ArrayList<Vineta> vinetasDeLosTextosDeLaFrase = new ArrayList<Vineta>();
	
	private final CaracteristicaDeLaFrase[] caracteristicas;
	//private Intencion intencion;
	private boolean esEstatica = true;
	
	private String pathAGuardarLosAudiosTTS;
	private String ipPublicaAMostrarLosAudioTTS;
	
	private int intentosFallidos = 0;
	
	protected Frase (String idFrase, ArrayList<ComponentesDeLaFrase> misSinonimosDeLaFrase, String[] vinetasDeLaFrase, int intentosFallidos,
			CaracteristicaDeLaFrase... caracteristicas)
	{
		this.caracteristicas = caracteristicas;
		this.idFrase = idFrase;
		this.misSinonimosDeLaFrase = misSinonimosDeLaFrase;
		this.intentosFallidos = intentosFallidos;
		cargarLaFrase();
		cargarVinetas(vinetasDeLaFrase);
		if(esEstatica()){
			System.out.println("Es estaticaaaaaaaa");
		}
		if(esDinamica()){
			System.out.println("Es dinamicaaaaa");
		}
		
	}
	
	private void cargarVinetas(String[] vinetasDeLaFrase){
		if (vinetasDeLaFrase != null){
			for(int index = 0; index < vinetasDeLaFrase.length; index ++){
				String vineta = vinetasDeLaFrase[index];
				vineta = vineta.replace("@@", "<").replace("##", ">");
				vinetasDeLosTextosDeLaFrase.add(new Vineta(vineta));
			}
		}
	}
	
	private void verSiLaFraseTienePlaceHolders(){
		boolean tieneUnoOVariosPlaceHolders = false;

		for(ComponentesDeLaFrase miFrase: misSinonimosDeLaFrase){
			// textoTag = texto.substring(texto.indexOf("{")+1, texto.indexOf("}"));
			tieneUnoOVariosPlaceHolders = ! (miFrase.getTextoDeLaFrase().indexOf("$") == -1);
			if(tieneUnoOVariosPlaceHolders) 
				break;
		}
		
		esEstatica = ! tieneUnoOVariosPlaceHolders;
	}
	
	public boolean hayFrasesConCondicion(){
		for(ComponentesDeLaFrase miFrase: misSinonimosDeLaFrase){
			if(miFrase.tieneUnaCondicion())
				return true;
		}
		return false;
	}
	
	public ArrayList<ComponentesDeLaFrase> extraerFrasesConCondicion(){
		ArrayList<ComponentesDeLaFrase> resultado = new ArrayList<>();
		for(ComponentesDeLaFrase miFrase: misSinonimosDeLaFrase){
			if(miFrase.tieneUnaCondicion())
				resultado.add(miFrase);
		}
		return resultado;
	}
	
	private ArrayList<ComponentesDeLaFrase> buscarFrasesSinonimoPorTipo(String tipoFrase){
		ArrayList<ComponentesDeLaFrase> resultado = new ArrayList<ComponentesDeLaFrase>();
		
		for(ComponentesDeLaFrase miFrase: misSinonimosDeLaFrase){
			if(miFrase.getTipoDeFrase().equals(tipoFrase))
				resultado.add(miFrase);
		}
		
		return resultado;
	}
	
	private ArrayList<ComponentesDeLaFrase> buscarFrasesGenerales(){
		return buscarFrasesSinonimoPorTipo(Constantes.TIPO_FRASE_GERERAL);
	}
	
	private ArrayList<ComponentesDeLaFrase> buscarFrasesImpertinentes(){
		return buscarFrasesSinonimoPorTipo(Constantes.TIPO_FRASE_IMPERTINENTE);
	}
	
	private ArrayList<ComponentesDeLaFrase> buscarFrasesMeRindo(){
		return buscarFrasesSinonimoPorTipo(Constantes.TIPO_FRASE_ME_RINDO);
	}
	
	private ArrayList<ComponentesDeLaFrase> buscarFrasesConjunciones(){
		return buscarFrasesSinonimoPorTipo(Constantes.TIPO_FRASE_CONJUNCION);
	}
	
	public String getIdFrase() {
		return idFrase;
	}
	
	public ComponentesDeLaFrase textosDeConjunciones(){
		
		ComponentesDeLaFrase resultado = null;
		ArrayList<ComponentesDeLaFrase> textos = buscarFrasesConjunciones();
		if(textos.size() > 0){
			int unIndiceAlAzar = (int)Math.floor(Math.random()*textos.size());
			resultado = textos.get(unIndiceAlAzar);
		}
		
		return resultado;
	}

	public ComponentesDeLaFrase texto(){
		
		ComponentesDeLaFrase resultado = null;
		ArrayList<ComponentesDeLaFrase> textos = buscarFrasesGenerales();
		if(textos.size() > 0){
			int unIndiceAlAzar = (int)Math.floor(Math.random()*textos.size());
			resultado = textos.get(unIndiceAlAzar);
			if(resultado.tieneUnaCondicion())
				return texto();
		}
		
		return resultado;
	}
	
	/*public Sonido obtenerSonidoAUsar(int idDelSonidoAUsar){
		Sonido resultado = null;
		if(esEstatica() && misSinonimosDeLaFrase.size() > 0){
			if (idDelSonidoAUsar == -1){
				idDelSonidoAUsar = (int)Math.floor(Math.random()*misSinonimosDeLaFrase.size());
			}
			resultado = misSinonimosDeLaFrase.get(idDelSonidoAUsar).getAudio();
		}
		return resultado;
	}*/
	
	public ComponentesDeLaFrase textoImpertinente(){
		ComponentesDeLaFrase resultado = null;
		ArrayList<ComponentesDeLaFrase> textosImpertinetesDeLaFrase = buscarFrasesImpertinentes();
		if(textosImpertinetesDeLaFrase.size() > 0){
			int unIndiceAlAzar = (int)Math.floor(Math.random()*textosImpertinetesDeLaFrase.size());
			resultado = textosImpertinetesDeLaFrase.get(unIndiceAlAzar);
			return resultado;
		}else{
			return texto();
		}
	}
	
	/*public Sonido obtenerSonidoImpertinenteAUsar(int idDelSonidoImpertinenteAUsar){
		Sonido resultado = null;
		ArrayList<ComponentesDeLaFrase> sonidosDeLosTextosImpertinentesDeLaFrase = buscarFrasesImpertinentes();
		if(hayTextosImpertinetes() && sonidosDeLosTextosImpertinentesDeLaFrase.size() > 0){
			if (idDelSonidoImpertinenteAUsar == -1){
				idDelSonidoImpertinenteAUsar = (int)Math.floor(Math.random()*sonidosDeLosTextosImpertinentesDeLaFrase.size());
			}resultado = sonidosDeLosTextosImpertinentesDeLaFrase.get(idDelSonidoImpertinenteAUsar).getAudio();
		}
		return resultado;
	}*/
	
	public boolean hayTextosImpertinetes(){
		try{
			return (buscarFrasesImpertinentes().size() > 0);
		}catch(Exception e){
			return false;
		}
	}
	
	public ComponentesDeLaFrase textoMeRindo(){
		ComponentesDeLaFrase resultado = null;
		ArrayList<ComponentesDeLaFrase> textosDeLaFraseMeRindo = buscarFrasesMeRindo();
		if(textosDeLaFraseMeRindo.size() > 0){
			int unIndiceAlAzar = (int)Math.floor(Math.random()*textosDeLaFraseMeRindo.size());
			resultado = textosDeLaFraseMeRindo.get(unIndiceAlAzar);
		}
		
		return resultado;
	}
	
	/*public Sonido obtenerSonidoMeRindoAUsar(int idDelSonidoMeRindoAUsar){
		Sonido resultado = null;
		ArrayList<ComponentesDeLaFrase> sonidosDeLosTextosDeLaFraseMeRindo = buscarFrasesMeRindo();
		if(hayTextosMeRindo() && sonidosDeLosTextosDeLaFraseMeRindo.size() > 0){
			if (idDelSonidoMeRindoAUsar == -1){
				idDelSonidoMeRindoAUsar = (int)Math.floor(Math.random()*sonidosDeLosTextosDeLaFraseMeRindo.size());
			}resultado = sonidosDeLosTextosDeLaFraseMeRindo.get(idDelSonidoMeRindoAUsar).getAudio();
		}
		return resultado;
	}*/
	
	public boolean hayTextosMeRindo(){
		try{
			return (buscarFrasesMeRindo().size() > 0);
		}catch(Exception e){
			return false;
		}
	}
	
	public boolean esEstatica(){
		return esEstatica;
	}
	
	public Vineta vineta(){
		Vineta resultado = null;
		
		if(vinetasDeLosTextosDeLaFrase.size() > 0){
			int unIndiceAlAzar = (int)Math.floor(Math.random()*vinetasDeLosTextosDeLaFrase.size());
			resultado = vinetasDeLosTextosDeLaFrase.get(unIndiceAlAzar);
		}
		
		return resultado;
	}
	
	public boolean esDinamica(){
		return ! esEstatica;
	}
	
	protected void cargarLaFrase() 
	{
		if (idFrase == null) 
			new ChatException("No has inicializado el id de la frase");
		
		// Validar que en el archivo o repositorio existe ese ID.
		//textoDeLaFrase = contenido.buscarLaFrase(this.idFrase);
		verSiLaFraseTienePlaceHolders();
		// "Validar inconsistencias como una frase no puede ser de saludo y despedida a la vez, pregunta y afirmativa a la vez"
		// Validar que ese ID al menos tenga un texto
		
	}
	
	public boolean buscarCaracteristica(CaracteristicaDeLaFrase caracteristica){
		boolean resultado = false;
		for (CaracteristicaDeLaFrase miCaracteristica: caracteristicas){
			if(miCaracteristica.equals(caracteristica)){
				resultado = true;
				break;
			}
		}
		return resultado;
	}
	
	public void generarAudiosEstaticos(String pathAGuardar, String ipPublica){
		this.pathAGuardarLosAudiosTTS = pathAGuardar;
		this.ipPublicaAMostrarLosAudioTTS = ipPublica;
		//sonidosDeLosTextosDeLaFrase.clear();
		
		if (sePuedeDecirEnVozAlta()){
			if(esEstatica()){
				int contadorDeSinonimos = 0;
				for(ComponentesDeLaFrase miFrase: misSinonimosDeLaFrase){
					String testoParaAudio = miFrase.getTextoAUsarParaGenerarElAudio();
					String nombreDelArchivo = "";
					if(AudiosXML.getInstance().hayQueGenerarAudios(this.idFrase, testoParaAudio, contadorDeSinonimos)){
						nombreDelArchivo = TextToSpeechWatson.getInstance().getAudioToURL(testoParaAudio, false);
					}else{
						nombreDelArchivo = AudiosXML.getInstance().obtenerUnAudioDeLaFrase(this.idFrase, contadorDeSinonimos);
						nombreDelArchivo = nombreDelArchivo.replace(ipPublica, "");
					}
					String miIp = ipPublica+nombreDelArchivo;
					miFrase.setAudio(new Sonido(miIp, testoParaAudio));
					contadorDeSinonimos ++;
				}
				
				/*for(int index = 0; index < textosDeLaFrase.length; index ++){
					String texto = textosDeLaFrase[index];
					String textoParaReproducir = texto;
					String textoTag = ""; 
					while(texto.contains("@@"))
					{
						texto = texto.replace("@@!", "&nbsp;");
						textoParaReproducir = textoParaReproducir.replace("@@!", " ");
						textoTag = texto.substring(texto.indexOf("@@")+2, texto.indexOf("@@@"));
					
						textoParaReproducir = textoParaReproducir.replace("@@"+textoTag+"@@@", "");
						texto = texto.replace("@@"+textoTag+"@@@", "<"+textoTag+">");

					}
					String nombreDelArchivo = "";
					if(AudiosXML.getInstance().hayQueGenerarAudios(this.idFrase, texto, index)){
						textoParaReproducir = textoParaReproducir.replace("<br/>", " ");
						textoParaReproducir = textoParaReproducir.replace("&nbsp;"," ");
						nombreDelArchivo = TextToSpeechWatson.getInstance().getAudioToURL(textoParaReproducir, false);
					}else{
						nombreDelArchivo = AudiosXML.getInstance().obtenerUnAudioDeLaFrase(this.idFrase, index);
						nombreDelArchivo = nombreDelArchivo.replace(ipPublica, "");
					}
					
					String path = pathAGuardar+File.separator+nombreDelArchivo;
					String miIp = ipPublica+nombreDelArchivo;
					sonidosDeLosTextosDeLaFrase.add(new Sonido(miIp));
					textosDeLaFrase[index] = texto;
				}
			}
			
			if(hayTextosImpertinetes()){
				//sonidosDeLosTextosImpertinentesDeLaFrase.clear();
				
				for(int index = 0; index < textosImpertinetesDeLaFrase.length; index ++){
					String texto = textosImpertinetesDeLaFrase[index];
					
					String textoParaReproducir = texto;
					String textoTag = ""; 
					while(texto.contains("@@"))
					{
						texto = texto.replace("@@!", "&nbsp;");
						textoParaReproducir = textoParaReproducir.replace("@@!", " ");
						textoTag = texto.substring(texto.indexOf("@@")+2, texto.indexOf("@@@"));
					
						textoParaReproducir = textoParaReproducir.replace("@@"+textoTag+"@@@", "");
						texto = texto.replace("@@"+textoTag+"@@@", "<"+textoTag+">");

					}
					String nombreDelArchivo = "";
					if(AudiosXML.getInstance().hayQueGenerarAudiosImpertinetes(this.idFrase, texto, index)){
						textoParaReproducir = textoParaReproducir.replace("<br/>", " ");
						textoParaReproducir = textoParaReproducir.replace("&nbsp;"," ");
						nombreDelArchivo = TextToSpeechWatson.getInstance().getAudioToURL(textoParaReproducir, false);
					}else{
						nombreDelArchivo = AudiosXML.getInstance().obtenerUnAudioDeLaFraseImpertinete(this.idFrase, index);
						nombreDelArchivo = nombreDelArchivo.replace(ipPublica, "");
					}
					
					String path = pathAGuardar+File.separator+nombreDelArchivo;
					String miIp = ipPublica+nombreDelArchivo;
					sonidosDeLosTextosImpertinentesDeLaFrase.add(new Sonido(miIp, path));
				}
				
			}
			
			if(hayTextosMeRindo()){
				sonidosDeLosTextosDeLaFraseMeRindo.clear();
				
				for(int index = 0; index < textosDeLaFraseMeRindo.length; index ++){
					String texto = textosDeLaFraseMeRindo[index];
					
					String textoParaReproducir = texto;
					String textoTag = ""; 
					while(texto.contains("@@"))
					{
						texto = texto.replace("@@!", "&nbsp;");
						textoParaReproducir = textoParaReproducir.replace("@@!", " ");
						textoTag = texto.substring(texto.indexOf("@@")+2, texto.indexOf("@@@"));
					
						textoParaReproducir = textoParaReproducir.replace("@@"+textoTag+"@@@", "");
						texto = texto.replace("@@"+textoTag+"@@@", "<"+textoTag+">");
					}
					
					String nombreDelArchivo = "";
					if(AudiosXML.getInstance().hayQueGenerarAudiosMeRindo(this.idFrase, texto, index)){
						textoParaReproducir = textoParaReproducir.replace("<br/>", " ");
						textoParaReproducir = textoParaReproducir.replace("&nbsp;"," ");
						nombreDelArchivo = TextToSpeechWatson.getInstance().getAudioToURL(textoParaReproducir, false);
					}else{
						nombreDelArchivo = AudiosXML.getInstance().obtenerUnAudioDeLaFraseMeRindo(this.idFrase, index);
						nombreDelArchivo = nombreDelArchivo.replace(ipPublica, "");
					}
					
					String path = pathAGuardar+File.separator+nombreDelArchivo;
					String miIp = ipPublica+nombreDelArchivo;
					sonidosDeLosTextosDeLaFraseMeRindo.add(new Sonido(miIp, path));
					textosDeLaFraseMeRindo[index] = texto;
				}*/
				
			}
		}
	}
	
	public boolean esUnaPregunta(){
		//boolean resultado = IntStream.of(caracteristicas).anyMatch(x -> x == CaracteristicaDeLaFrase.esUnaPregunta);
		return buscarCaracteristica(CaracteristicaDeLaFrase.esUnaPregunta);
	}

	public boolean esUnSaludo(){
		//boolean resultado = IntStream.of(caracteristicas).anyMatch(x -> x == CaracteristicaDeLaFrase.esUnSaludo);
		return buscarCaracteristica(CaracteristicaDeLaFrase.esUnSaludo);
	}
	
	public boolean esUnaOracionAfirmativa(){
		//boolean resultado = IntStream.of(caracteristicas).anyMatch(x -> x == CaracteristicaDeLaFrase.esUnaOracionAfirmativa);
		return buscarCaracteristica(CaracteristicaDeLaFrase.esUnaOracionAfirmativa);
	}
	
	public boolean esUnaDespedida(){
		//boolean resultado = IntStream.of(caracteristicas).anyMatch(x -> x == CaracteristicaDeLaFrase.esUnaDespedida);
		return buscarCaracteristica(CaracteristicaDeLaFrase.esUnaDespedida);
	}
	
	public boolean esMandatorio(){
		return buscarCaracteristica(CaracteristicaDeLaFrase.esUnaPreguntaMandatoria);
	}
	
	public boolean sePuedeDecirEnVozAlta(){
		return buscarCaracteristica(CaracteristicaDeLaFrase.sePuedeDecirEnVozAlta);
	}
	
	/*public String[] getTextosDeLaFrase() {
		return textosDeLaFrase;
	}

	public String[] getTextosImpertinetesDeLaFrase() {
		return textosImpertinetesDeLaFrase;
	}
	
	public String[] getTextosMeRindoDeLaFrase() {
		return textosDeLaFraseMeRindo;
	}*/
	
	public ComponentesDeLaFrase conjuncionParaRepreguntar(){
		return Conjunciones.getInstance().obtenerUnaConjuncion().textosDeConjunciones();
	}
	
	public String getPathAGuardarLosAudiosTTS() {
		return pathAGuardarLosAudiosTTS;
	}

	public String getIpPublicaAMostrarLosAudioTTS() {
		return ipPublicaAMostrarLosAudioTTS;
	}

	public String obtenerLaInformacionDeLaFrase(){
		String resultado = "";
		
		for(ComponentesDeLaFrase miFrase: misSinonimosDeLaFrase){
			resultado += "Frases: \n";
			resultado += "     - "+miFrase.getTextoDeLaFrase()+"\n";
		}
		
		/*if(textosDeLaFrase.length > 0){
			resultado += "Frases: \n";
			for (int index = 0; index < textosDeLaFrase.length; index ++){
				resultado += "     - "+textosDeLaFrase[index]+"\n";
			}
		}
		
		if(hayTextosImpertinetes()){
			resultado += "Frases Impertinentes: \n";
			for (int index = 0; index < textosImpertinetesDeLaFrase.length; index ++){
				resultado += "     - "+textosImpertinetesDeLaFrase[index]+"\n";
			}
		}
		
		if(hayTextosMeRindo()){
			resultado += "Frases me reindo: \n";
			for (int index = 0; index < textosDeLaFraseMeRindo.length; index ++){
				resultado += "     - "+textosDeLaFraseMeRindo[index]+"\n";
			}
		}*/
		
		return resultado;
	}
	
	public void cargarElNombreDeUnSonidoEstaticoEnMemoria(int index, String nombreDelArchivo, String pathAGuardar, String ipPublica){
		String miIp = ipPublica+nombreDelArchivo;
		// TODO Enviar el texto del audio
		this.misSinonimosDeLaFrase.get(index).setAudio(new Sonido(miIp, ""));
	}
	
	public ArrayList<ComponentesDeLaFrase> obtenerMisSinonimosDeLaFrase(){
		return misSinonimosDeLaFrase;
	}
	
	public int obtenerNumeroIntentosFallidos (){
		return this.intentosFallidos;
	}
}