package com.ncubo.regresion;

import java.util.List;

import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.partesDeLaConversacion.Contenido;
import com.ncubo.chatbot.partesDeLaConversacion.Tema;
import com.ncubo.chatbot.partesDeLaConversacion.Temario;
import com.ncubo.chatbot.partesDeLaConversacion.Temas;
import com.ncubo.chatbot.watson.Entidades;
import com.ncubo.chatbot.watson.Intenciones;

public class TemarioDeLaRegresion extends Temario{
	
	protected TemarioDeLaRegresion(String pathXML) {
		super(pathXML);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void cargarTemario(Temas temasDelDiscurso){
		
		System.out.println("Cargando temario ...");
		
		for(Tema tema: this.contenido().obtenerMisTemas()){
			temasDelDiscurso.add(tema);
		}
	}
	
	public void cargarIntenciones(List<Intenciones> intenciones)
	{
	}

	@Override
	protected void cargarEntidades(List<Entidades> entidades) {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected Contenido cargarContenido(String path){
		return new ContenidoDeLaRegresion(path);
	}

	@Override
	protected void cargarDependencias(Temas temasDelDiscurso){
	}
	
	public static void main(String argv[]) {
		TemarioDeLaRegresion temario = new TemarioDeLaRegresion(Constantes.PATH_ARCHIVO_DE_CONFIGURACION_BA);
		temario.buscarTemaPorLaIntencion(Constantes.INTENCION_SALUDAR);
	}
}
