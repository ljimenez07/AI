package com.ncubo.chatbot.consola;

import com.ncubo.chatbot.participantes.Cliente;
import com.ncubo.conectores.Conectores;

public class ClienteDePrueba extends Cliente{

	public ClienteDePrueba(Conectores conectores){
		super(conectores);
	}
	
	public ClienteDePrueba(String nombre, String id, Conectores conectores) throws Exception{
		super(nombre, id, conectores);
	}
}
