package com.ncubo.regresion;

import java.io.File;
import com.ncubo.chatbot.partesDeLaConversacion.Contenido;

public class ContenidoDeLaRegresion extends Contenido{
	
	protected ContenidoDeLaRegresion(String path) {
		super(path);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected File archivoDeConfiguracion(String path){
		return new File(path);
	}
}
