package com.ncubo.logicaDeLasConversaciones;

import java.util.ArrayList;
import java.util.Iterator;

public class TemariosDeUnCliente extends ArrayList<TemarioDelCliente>{
	
	public TemariosDeUnCliente(){}
	
	private boolean existeElTemario(TemarioDelCliente temario){
		return this.contains(temario);
	}
	
	public void agregarTemario(TemarioDelCliente temario){
		if(existeElTemario(temario)){
			this.add(temario);
		}
	}
	
	public TemarioDelCliente extraerUnTemario(int posicion){
		try{
			return this.get(posicion);
		}catch(Exception e){
			return null;
		}
		
	}
	
	public Iterator<TemarioDelCliente> obtenerLosTemariosDelCliente() { 
		return iterator();
	}
}
