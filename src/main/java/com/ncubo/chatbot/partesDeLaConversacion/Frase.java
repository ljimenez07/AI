package com.ncubo.chatbot.partesDeLaConversacion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.ncubo.chatbot.audiosXML.AudiosXML;
import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.configuracion.Constantes.TiposDeVariables;
import com.ncubo.chatbot.contexto.VariablesDeContexto;
import com.ncubo.chatbot.exceptiones.ChatException;
import com.ncubo.chatbot.watson.TextToSpeechWatson;

public abstract class Frase
{
	private final String nombreDeLaFrase;
	private final int idFrase;
	private final ArrayList<ComponentesDeLaFrase> misSinonimosDeLaFrase;
	private ArrayList<Vineta> vinetasDeLosTextosDeLaFrase = new ArrayList<Vineta>();
	private final CaracteristicaDeLaFrase[] caracteristicas;
	//private Intencion intencion;
	private String pathAGuardarLosAudiosTTS;
	private String ipPublicaAMostrarLosAudioTTS;
	private int intentosFallidos = 0;
	
	private int indiceValoresActual = 0;
	private int subIndiceValoresActual = 0;
	
	protected Frase (int idFrase, String nombreDeLaFrase, ArrayList<ComponentesDeLaFrase> misSinonimosDeLaFrase, String[] vinetasDeLaFrase, int intentosFallidos,
			CaracteristicaDeLaFrase... caracteristicas)
	{
		this.idFrase = idFrase;
		this.caracteristicas = caracteristicas;
		this.nombreDeLaFrase = nombreDeLaFrase;
		this.misSinonimosDeLaFrase = misSinonimosDeLaFrase;
		this.intentosFallidos = intentosFallidos;
		//cargarLaFrase();
		cargarVinetas(vinetasDeLaFrase);
		/*if(esEstatica()){
			System.out.println("Es estaticaaaaaaaa");
		}
		if(esDinamica()){
			System.out.println("Es dinamicaaaaa");
		}*/
	}
	
	private void cargarVinetas(String[] vinetasDeLaFrase){
		if (vinetasDeLaFrase != null){
			for(int index = 0; index < vinetasDeLaFrase.length; index ++){
				String vineta = vinetasDeLaFrase[index];
				vineta = vineta.replace("@@", "<").replace("##", ">");
				vinetasDeLosTextosDeLaFrase.add(new Vineta(vineta, Constantes.TIPO_VINETA_SELECTIVA));
			}
		}
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
	
	public boolean hayFrasesConPlaceholders(){
		for(ComponentesDeLaFrase miFrase: misSinonimosDeLaFrase){
			if(miFrase.tienePlaceholders())
				return true;
		}
		return false;
	}
	
	public ComponentesDeLaFrase extraerFraseSinonimoConPlaceholders(){
		ComponentesDeLaFrase resultado = null;
		Collections.shuffle(misSinonimosDeLaFrase); // Desordenar el array
		for(ComponentesDeLaFrase miFrase: misSinonimosDeLaFrase){
			if(miFrase.tienePlaceholders()){
				resultado = miFrase;
				break;
			}
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
	
	public String obtenerNombreDeLaFrase() {
		return nombreDeLaFrase;
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

	public boolean hayTextosMeRindo(){
		try{
			return (buscarFrasesMeRindo().size() > 0);
		}catch(Exception e){
			return false;
		}
	}
	
	public Vineta vineta(){
		Vineta resultado = null;
		
		if(vinetasDeLosTextosDeLaFrase.size() > 0){
			int unIndiceAlAzar = (int)Math.floor(Math.random()*vinetasDeLosTextosDeLaFrase.size());
			resultado = vinetasDeLosTextosDeLaFrase.get(unIndiceAlAzar);
		}
		
		return resultado;
	}
	
	protected void cargarLaFrase() 
	{
		if (nombreDeLaFrase == null) 
			new ChatException("No has inicializado el nombre de la frase");
		
		// Validar que en el archivo o repositorio existe ese Nombre.
		//textoDeLaFrase = contenido.buscarLaFrase(this.nombreDeLaFrase);
		//verSiLaFraseTienePlaceHolders();
		// "Validar inconsistencias como una frase no puede ser de saludo y despedida a la vez, pregunta y afirmativa a la vez"
		// Validar que ese Nombre al menos tenga un texto
		
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
		
		if (sePuedeDecirEnVozAlta()){
			
			for(ComponentesDeLaFrase miFrase: misSinonimosDeLaFrase){
				if(miFrase.esEstatica()){
					String textoParaAudio = miFrase.getTextoAUsarParaGenerarElAudio();
					String nombreDelArchivo = "";
					if(AudiosXML.getInstance().hayQueGenerarAudios(this.nombreDeLaFrase, textoParaAudio)){
						nombreDelArchivo = TextToSpeechWatson.getInstance().getAudioToURL(textoParaAudio, false);
					}else{
						nombreDelArchivo = AudiosXML.getInstance().obtenerUnAudioDeLaFrase(this.nombreDeLaFrase, "audio");
						nombreDelArchivo = nombreDelArchivo.replace(ipPublica, "");
					}
					String miIp = ipPublica+nombreDelArchivo;
					miFrase.setAudio("audio",new Sonido(miIp, textoParaAudio));
				}
				if(soloTieneEnum(miFrase)){
					generarAudioEnums(miFrase, ipPublica);	
				}
			}
		}
	}
	
	
	public boolean generarAudioEnums(ComponentesDeLaFrase miFrase, String ipPublica){
		
		int totalCombinaciones = 1;
		boolean generados = false; 
			
		ArrayList<Placeholder> placeholders = miFrase.obtenerLosPlaceholders();
		final List<List<Object>> c = new ArrayList<>(placeholders.size());
		for(Placeholder placeholder: placeholders){
			final List<Object> lista = new ArrayList<Object>();
			String[] valores = VariablesDeContexto.getInstance().obtenerUnaVariableDeMiContexto(placeholder.getNombreDelPlaceholder()).getValorDeLaVariable();
			totalCombinaciones = totalCombinaciones * valores.length;
		for(String valor: valores){	
					lista.add(valor);
			}
			c.add(lista);
		}
		
		if(totalCombinaciones <= 50){
			generados = true; 
			final List<List<Object>> m = this.combine(c);
		   
		    for (List<Object> valores:m){
		    	String textoAUsarParaGenerarAudio = miFrase.getTextoAUsarParaGenerarElAudio();
		    	 int contadorValores = 0;	
		    	 String idAudio = "";
		    	for(Placeholder placeholder: miFrase.obtenerLosPlaceholders()){
					textoAUsarParaGenerarAudio = textoAUsarParaGenerarAudio.replace("${"+placeholder.getNombreDelPlaceholder()+"}",  valores.get(contadorValores).toString());
					idAudio = idAudio + "-" + valores.get(contadorValores);
					contadorValores++;
		    	}
		    	
		    	String nombreDelArchivo = "";
				if(AudiosXML.getInstance().hayQueGenerarAudios(this.nombreDeLaFrase, textoAUsarParaGenerarAudio)){
					nombreDelArchivo = TextToSpeechWatson.getInstance().getAudioToURL(textoAUsarParaGenerarAudio, false);
				}else{
					nombreDelArchivo = AudiosXML.getInstance().obtenerUnAudioDeLaFrase(this.nombreDeLaFrase, idAudio);
					nombreDelArchivo = nombreDelArchivo.replace(ipPublica, "");
				}
				String miIp = ipPublica+nombreDelArchivo;
				miFrase.setAudio(idAudio,new Sonido(miIp, textoAUsarParaGenerarAudio));
		    }
		}
		return generados;
	}
	
	public int[] actualizarIndicesValores(int[] indicesValores, String[][]valores)
	{
		if(disminuirIndiceValoresActual(indicesValores, valores)){
			subIndiceValoresActual = indiceValoresActual;
			indiceValoresActual--;
			indicesValores[indiceValoresActual] =  indicesValores[indiceValoresActual] + 1;
			indicesValores = reiniciarIndicesALaDerecha(indiceValoresActual, indicesValores);
		}
		else if(indicesValores[subIndiceValoresActual] + 1 == valores[subIndiceValoresActual].length)
		{
			subIndiceValoresActual --;
			indicesValores[subIndiceValoresActual] =  indicesValores[subIndiceValoresActual] + 1;
			indicesValores = reiniciarIndicesALaDerecha(subIndiceValoresActual, indicesValores);
			
		}
		else{
			indicesValores = aumentarIndicesALaDerecha(subIndiceValoresActual, indicesValores,valores);
		}
		return indicesValores;
	}
	
	public int[] reiniciarIndicesALaDerecha(int posicion, int[] indicesValores)
	{
		for(int i = posicion +1; i < indicesValores.length; i++){
			indicesValores[i] = 0;
		}
		subIndiceValoresActual = indicesValores.length - 1;
		return indicesValores;
	}

	public int[]aumentarIndicesALaDerecha(int posicion, int[] indicesValores, String[][]valores)
	{	
		for (int max = indicesValores.length - 1; max >= posicion; max--){
			if(indicesValores[max] < (valores[max].length-1))
			{
				indicesValores[max] =  indicesValores[max] + 1;
				return indicesValores;
			}
		}
		
		return indicesValores;
	}
	
	
	public boolean esElUltimo(int[] indicesValores, String [] [] valores){
		boolean terminar = true;
		
		for(int i = 0; i < indicesValores.length; i++)
		{
			if(indicesValores[i] < (valores[i].length-1))
				return terminar = false;
		}
		
		return terminar;
	}
	 
	
	public boolean disminuirIndiceValoresActual(int[] indicesValores, String [] [] valores){
		boolean terminar = true;
		
		for(int i = indicesValores.length - 1 ; i >= indiceValoresActual; i--)
		{
			if(indicesValores[i] < (valores[i].length-1))
				return terminar = false;
		}
		return terminar;
	}
	
	
	public boolean esUnaPregunta(){
		return buscarCaracteristica(CaracteristicaDeLaFrase.esUnaPregunta);
	}

	public boolean esUnSaludo(){
		return buscarCaracteristica(CaracteristicaDeLaFrase.esUnSaludo);
	}
	
	public boolean esUnaOracionAfirmativa(){
		return buscarCaracteristica(CaracteristicaDeLaFrase.esUnaOracionAfirmativa);
	}
	
	public boolean esUnaDespedida(){
		return buscarCaracteristica(CaracteristicaDeLaFrase.esUnaDespedida);
	}
	
	public boolean esMandatorio(){
		return buscarCaracteristica(CaracteristicaDeLaFrase.esUnaPreguntaMandatoria);
	}
	
	public boolean sePuedeDecirEnVozAlta(){
		return buscarCaracteristica(CaracteristicaDeLaFrase.sePuedeDecirEnVozAlta);
	}
	
	public boolean soloTieneEnum(){
		boolean tieneSoloEnum = false;
		if(this.hayFrasesConPlaceholders()){
			tieneSoloEnum = true;
			for(ComponentesDeLaFrase miFrase: misSinonimosDeLaFrase){
				ArrayList<Placeholder> placeholders = miFrase.obtenerLosPlaceholders();
					for (int i = 0; i < placeholders.size();i++){
						String tipo = VariablesDeContexto.getInstance().obtenerUnaVariableDeMiContexto(placeholders.get(i).getNombreDelPlaceholder()).getTipoVariable().name();
						if(!tipo.equals(TiposDeVariables.ENUM.name()))
							return tieneSoloEnum = false;							
				}
			}
		}
		return tieneSoloEnum;
	}
	
	public boolean soloTieneEnum(ComponentesDeLaFrase miFrase){
		boolean tieneSoloEnum = false;
		if(this.hayFrasesConPlaceholders()){
			tieneSoloEnum = true;
			ArrayList<Placeholder> placeholders = miFrase.obtenerLosPlaceholders();
					for (int i = 0; i < placeholders.size();i++){
						String tipo = VariablesDeContexto.getInstance().obtenerUnaVariableDeMiContexto(placeholders.get(i).getNombreDelPlaceholder()).getTipoVariable().name();
						if(!tipo.equals(TiposDeVariables.ENUM.name()))
							return tieneSoloEnum = false;							
			}
		}
		return tieneSoloEnum;
	}
	
	public int totalPlaceholderEnum(){
		int totalEnum = 0;
		if(this.hayFrasesConPlaceholders()){
			for(ComponentesDeLaFrase miFrase: misSinonimosDeLaFrase){
				ArrayList<Placeholder> placeholders = miFrase.obtenerLosPlaceholders();
				for (int i = 0; i < placeholders.size();i++){
					String tipo = VariablesDeContexto.getInstance().obtenerUnaVariableDeMiContexto(placeholders.get(i).getNombreDelPlaceholder()).getTipoVariable().name();
					if(tipo.equals(TiposDeVariables.ENUM.name()))
						return totalEnum++;							
				}
			}
		}
		return totalEnum;
	}
	/*
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
		this.misSinonimosDeLaFrase.get(index).setAudio("audio",new Sonido(miIp, ""));
	}
	
	public ArrayList<ComponentesDeLaFrase> obtenerMisSinonimosDeLaFrase(){
		return misSinonimosDeLaFrase;
	}
	
	public int obtenerNumeroIntentosFallidos (){
		return this.intentosFallidos;
	}
	
	public int obtenerIdDeLaFrase(){
		return this.idFrase;
	}
	
	public List<List<Object>> combine(final List<List<Object>> containers) {
	    return this.combineInternal(0, containers);
	}

	private List<List<Object>> combineInternal(final int currentIndex,
	        final List<List<Object>> containers) {
	    if (currentIndex == containers.size()) {
	        // Skip the items for the last container
	        final List<List<Object>> combinations = new ArrayList<>();
	        combinations.add(Collections.emptyList());
	        return combinations;
	    }

	    final List<List<Object>> combinations = new ArrayList<>();
	    final List<Object> containerItemList = containers.get(currentIndex);
	    // Get combination from next index
	    final List<List<Object>> suffixList = this.combineInternal(
	            currentIndex + 1, containers);
	    final int totalContainers = containers.size();
	    System.out.println(totalContainers);
	    final int size = containerItemList.size();
	    for (int i = 0; i < size; i++) {
	        final Object containerItem = containerItemList.get(i);
	        if (suffixList != null) {
	            for (final List<Object> suffix : suffixList) {
	                final List<Object> nextCombination = new ArrayList<>();
	                nextCombination.add(containerItem);
	                nextCombination.addAll(suffix);
	                combinations.add(nextCombination);
	            }
	        }
	    }

	    return combinations;
	}

}