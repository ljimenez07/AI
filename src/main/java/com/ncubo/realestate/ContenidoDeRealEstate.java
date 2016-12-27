package com.ncubo.realestate;

import java.io.File;

import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.partesDeLaConversacion.Contenido;

public class ContenidoDeRealEstate extends Contenido{
	
	protected ContenidoDeRealEstate(String path) {
		super(path);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected File archivoDeConfiguracion(String path){
		return new File(path);
	}
}
