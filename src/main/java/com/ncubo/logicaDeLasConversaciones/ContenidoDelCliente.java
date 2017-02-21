package com.ncubo.logicaDeLasConversaciones;

import java.io.File;

import com.ncubo.chatbot.partesDeLaConversacion.CargadorDeContenido;

public class ContenidoDelCliente extends CargadorDeContenido{

	protected ContenidoDelCliente(String path) {
		super(path);
	}

	@Override
	protected File archivoDeConfiguracion(String path) {
		return new File(path);
	}

}
