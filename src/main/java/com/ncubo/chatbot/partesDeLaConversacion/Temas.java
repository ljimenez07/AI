package com.ncubo.chatbot.partesDeLaConversacion;

import java.util.ArrayList;
import java.util.Iterator;

public class Temas extends ArrayList<Tema>
{
	public Tema buscarTemaEnMisTemas(String idTema){
		Iterator<Tema> misFrases = this.iterator();
	    Tema resultado = null;
		while(misFrases.hasNext()) {
			Tema tema = (Tema) misFrases.next();
			if (tema.obtenerIdTema().equals(idTema))
				return tema;
		}
		return resultado;
	}
}