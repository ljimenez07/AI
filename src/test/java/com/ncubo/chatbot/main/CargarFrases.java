package com.ncubo.chatbot.main;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.logicaDeLasConversaciones.TemariosDeUnCliente;

public class CargarFrases {

	private static TemariosDeUnCliente temarioDelBancoAtlantida;
	
	@Test
	public void cargarFrases( ) throws Exception {
		
		temarioDelBancoAtlantida = new TemariosDeUnCliente(Constantes.PATH_ARCHIVO_DE_CONFIGURACION_BA);
		
		Assert.assertEquals("test", "test");
	}
}
