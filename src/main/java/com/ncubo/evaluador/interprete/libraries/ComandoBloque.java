package com.ncubo.evaluador.interprete.libraries;

import com.ncubo.evaluador.db.TablaDeSimbolos;

public class ComandoBloque extends Comando
{
	private Comando[] comandos;
	private final TablaDeSimbolos tablaDeSimbolos;
	
	public ComandoBloque(TablaDeSimbolos tablaDeSimbolos, Comando[] comandos) 
	{
		this.comandos = comandos;
		this.tablaDeSimbolos = tablaDeSimbolos;
	}
	
	public Comando[] getComandos()
	{
		return comandos;
	}
	
	@Override
	public void ejecutar()
	{ 
		tablaDeSimbolos.abrirBloque();
		for (Comando comando : comandos)  
		{
			if (comando instanceof ComandoNuevaInstancia) 
			{
				//((ComandoNuevaInstancia) comando).estoyEnPrimerNivel(false);
			}
			//comando.cargarListenerDeEventos(listenerDeEventos);
			//comando.ejecutar();
		 }
		 tablaDeSimbolos.cerrarBloque();
	}

	@Override
	public void validarEstaticamente()
	{
		tablaDeSimbolos.abrirBloque();
		for (Comando comando : comandos)  
		{
			comando.validarEstaticamente();
		}
		tablaDeSimbolos.cerrarBloque();
	}

	@Override
	void write(StringBuilder resultado, int tabs) 
	{
		resultado.append(generarTabs(tabs));
		resultado.append("{\r");
		tabs++;
		for (Comando comando : comandos)  
		{
		    comando.write(resultado, tabs);
		}
		tabs--;
		resultado.append(generarTabs(tabs));
		resultado.append("}\r");
	}

	@Override
	public void buscarVariablesEstaticas() {
		tablaDeSimbolos.abrirBloque();
		for (Comando comando : comandos)  
		{
			if (comando instanceof ComandoNuevaInstancia) 
			{
				//((ComandoNuevaInstancia) comando).estoyEnPrimerNivel(false);
			}
			//comando.cargarListenerDeEventos(listenerDeEventos);
			//comando.ejecutar();
		 }
		 tablaDeSimbolos.cerrarBloque();
	}
}
