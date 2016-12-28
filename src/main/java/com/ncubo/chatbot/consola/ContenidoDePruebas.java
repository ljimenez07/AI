package com.ncubo.chatbot.consola;

import java.io.File;
import com.ncubo.chatbot.partesDeLaConversacion.Contenido;

public class ContenidoDePruebas extends Contenido{
	
	protected ContenidoDePruebas(String path) {
		super(path);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected File archivoDeConfiguracion(String path){
		return new File(path);
	}
}

