package com.ncubo.chatbot.main;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.consola.TemarioDePruebas;
import com.ncubo.chatbot.partesDeLaConversacion.Temario;

public class CargarFrases {

	private static Temario temarioDelBancoAtlantida;
	
	@Test
	public void cargarFrases( ) throws Exception {
		
		temarioDelBancoAtlantida = new TemarioDePruebas(Constantes.PATH_ARCHIVO_DE_CONFIGURACION_BA);
		
		Assert.assertEquals("test", "test");
	}
}
