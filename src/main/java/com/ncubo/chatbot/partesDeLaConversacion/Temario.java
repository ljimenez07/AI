package com.ncubo.chatbot.partesDeLaConversacion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.exceptiones.ChatException;
import com.ncubo.chatbot.watson.Entidades;
import com.ncubo.chatbot.watson.Intenciones;

public abstract class Temario 
{
	private Temas temasDelDiscurso;
	private final Contenido contenido;
	private ArrayList<Intenciones> intenciones = new ArrayList<Intenciones>();
	private ArrayList<Entidades> entidades = new ArrayList<Entidades>();
	
	protected Temario(String pathXML)
	{
		temasDelDiscurso = new Temas();
		contenido = cargarContenido(pathXML);
		cargarTemario(temasDelDiscurso);
		valirQueLasDependenciasEstenEnLosTemas();
		cargarDependencias(temasDelDiscurso);
		//cargarEntidades(entidades);
		//cargarIntenciones(intenciones);
		
	}
	
	protected abstract void cargarTemario(Temas temasDelDiscurso);
	
	protected abstract void cargarDependencias(Temas temasDelDiscurso);
	
	protected abstract void cargarEntidades(List<Entidades> entidades);
		
	protected abstract void cargarIntenciones(List<Intenciones> intenciones);
	
	protected abstract Contenido cargarContenido(String path);
	
	public Contenido contenido(){
		return contenido;
	}
	
	public Frase frase(String idDeLaFrase){
		if(contenido == null){
			// TODO Error
			throw new ChatException("No se ha cargado el contenido del archivo de configuracion");
		}
		return contenido.frase(idDeLaFrase);
	}
	
	private void valirQueLasDependenciasEstenEnLosTemas()
	{
		/*for (Tema dependencia : dependenciasEntreLosTemas)
			if (! temasDelDiscurso.contains(dependencia))
				throw new ChatException(
					String.format("Hay una dependencia %s que no es parte de los temas del discurso", 1)
				);*/
	}
	
	public Tema buscarTema(String nombre){
		for(Tema tema: temasDelDiscurso){
			if(tema.obtenerIdTema().equals(nombre)){
				return tema;
			}
		}
		return null;
	}
	
	public Tema buscarTema(String nombreDelWorkspace, String nombreIntencionGeneral){
		for(Tema tema: temasDelDiscurso){
			if(tema.obtenerElNombreDelWorkspaceAlQuePertenece().equals(nombreDelWorkspace) && tema.obtenerIntencionGeneralAlQuePertenece().equals(nombreIntencionGeneral)){
				return tema;
			}
		}
		return null;
	}
	
	public Frase extraerFraseDeSaludoInicial(CaracteristicaDeLaFrase caracteristica){
		Tema miSaludo = buscarTema(Constantes.FRASE_SALUDO);
		return miSaludo.buscarUnaFraseCon(caracteristica);
	}
	
	public Tema proximoTemaATratar(Tema temaActual, Temas temasYaTratados, String nombreDelWorkspace, String nombreIntencionGeneral){
		Collections.shuffle(temasDelDiscurso); // Desordenar el array
		for(Tema tema: temasDelDiscurso){
			if(tema.obtenerElNombreDelWorkspaceAlQuePertenece().equals(nombreDelWorkspace) && tema.obtenerIntencionGeneralAlQuePertenece().equals(nombreIntencionGeneral)){
				if(temaActual != null){
					if(! tema.obtenerIdTema().equals(temaActual.obtenerIdTema())){
						if(temasYaTratados.size() > 0){
							if( ! temasYaTratados.contains(tema)){
								if(tema.buscarSiTodasLasDependenciasYaFueronTratadas(temasYaTratados)){
									return tema;
								}
							}
						}else{
							if( ! tema.tieneDependencias()){
								return tema;
							}
						}
					}
				}else{
					if(temasYaTratados != null){
						if( ! temasYaTratados.contains(tema)){
							if(tema.buscarSiTodasLasDependenciasYaFueronTratadas(temasYaTratados)){
								return tema;
							}
						}
					}else{
						if( ! tema.tieneDependencias()){
							return tema;
						}
					}
				}
				
			}
		}
		if(temaActual.obtenerElNombreDelWorkspaceAlQuePertenece().equals(nombreDelWorkspace) && temaActual.obtenerIntencionGeneralAlQuePertenece().equals(nombreIntencionGeneral))
			return temaActual;
		else
			return null;
	}
	
	public void generarAudioEstaticosDeTodasLasFrases(String pathAGuardar, String ipPublica){
		/*for(Tema tema: temasDelDiscurso){
			System.out.println("Generando audios al TEMA: "+tema.obtenerIdTema());
			tema.generarAudiosEstaticos(pathAGuardar, ipPublica);
		}*/
		
		ArrayList<Frase> misFrase = contenido.obtenerMiFrases();
		for(int index = 0; index < misFrase.size(); index ++){
			System.out.println("Generando audios de la frase: "+misFrase.get(index).getIdFrase());
			misFrase.get(index).generarAudiosEstaticos(pathAGuardar, ipPublica);
		}
	}
	
	public void generarAudioEstaticosDeUnTema(String pathAGuardar, String ipPublica, int index){
		temasDelDiscurso.get(index).generarAudiosEstaticos(pathAGuardar, ipPublica);
	}
	
	public void cargarElNombreDeUnSonidoEstaticoEnMemoria(String pathAGuardar, String ipPublica, int indexTema, int indexFrase, String nombreTema, String nombreDelArchivo){
		try{
			temasDelDiscurso.buscarTemaEnMisTemas(nombreTema).obtenerMisFrases()[indexTema].cargarElNombreDeUnSonidoEstaticoEnMemoria(indexFrase, nombreDelArchivo, pathAGuardar, ipPublica);
		}catch(Exception e){
			System.out.println("Error al generar la fase del tema "+nombreTema);
		}
	}
	
	public Temas obtenerMisTemas(){
		return temasDelDiscurso;
	}
	
	public String verMiTemario(){
		String resultado = "";
		int contador = 0;
		
		for(Tema tema: temasDelDiscurso){
			resultado += tema.obtenerTodasMisFrases(contador);
			contador ++;
		}
		
		return resultado;
	}
	
}