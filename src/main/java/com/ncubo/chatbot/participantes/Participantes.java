package com.ncubo.chatbot.participantes;

import java.util.ArrayList;
import java.util.Iterator;

public class Participantes implements Iterable<Participante>{

	private ArrayList<Participante> participantes = new ArrayList<Participante>();
	
	public Participantes agregar(Participante participante){
		participantes.add(participante);
		return this;
	}

	@Override
	public Iterator<Participante> iterator() {
		// TODO Auto-generated method stub
		return participantes.iterator();
	}
}
