package com.ncubo.evaluador.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.ncubo.evaluador.interprete.libraries.LanguageException;
import com.ncubo.evaluador.libraries.Hilera;
import com.ncubo.evaluador.libraries.Nulo;
import com.ncubo.evaluador.libraries.Objeto;

public class TablaDeSimbolos {

	private Map<String, SimboloVariable> tablaDeVariables = new HashMap<String, SimboloVariable>();

	private int nivel = 0;
	private static final char SEPARADOR_NIVEL = ':';

	public void abrirBloque() {
		nivel++;
	}

	public void cerrarBloque() {
		if (nivel == 0) {
			throw new RuntimeException("Se esta intentando cerrar un bloque que no ha sido abierto");
		}
		ArrayList<String> aRemover = new ArrayList<String>();
		for (String variable : tablaDeVariables.keySet())
		{
			SimboloVariable s = tablaDeVariables.get(variable);
			if (s.nivel == nivel) 
			{
				aRemover.add(variable);
			}			
		}
		for(String variable : aRemover)
		{
			tablaDeVariables.remove(variable);
		}
		nivel--;
	}


	public void guardarVariable(String instancia, Objeto dato) 
	{
		String keyInstancia = instancia + SEPARADOR_NIVEL + nivel;
		SimboloVariable simboloAlmacenado = tablaDeVariables.get(keyInstancia);
		
		boolean estabaAlmacenado = simboloAlmacenado != null;
		if (estabaAlmacenado && dato.getClass() != Nulo.NULO.getClass()) 
		{
				boolean sonDelMismoTipo = simboloAlmacenado.objeto.getClass() == Nulo.NULO.getClass() || dato.getClass() == simboloAlmacenado.objeto.getClass();
				if ( ! sonDelMismoTipo ) {
					throw new LanguageException("A la instacia " + instancia
							+ " solo se le pueden asignar "
							+ simboloAlmacenado.objeto.getClass().getSimpleName()
							+ " y se le está tratando de asignar: "
							+ dato.toString() );
				}
				simboloAlmacenado.objeto = dato;
		}
		else
		{
			dato.setNombreDeVariable(instancia);
			String nombreDeLainstancia = instancia.toLowerCase();
			SimboloVariable s = new SimboloVariable(nivel, instancia, dato);
			tablaDeVariables.put(nombreDeLainstancia + SEPARADOR_NIVEL + nivel, s);
		}
	}

	public boolean existeLaVariable(String nombreInstancia) {
		String instancia = nombreInstancia.toLowerCase();
		for (int i = 0; i <= nivel; i++)
		{
			boolean existe = tablaDeVariables.containsKey(instancia + SEPARADOR_NIVEL + i);
			if (existe) return true;
		}
		return false;
	}

	private SimboloVariable variable(String nombreInstancia) {
		String instancia = nombreInstancia.toLowerCase();
		for (int i = nivel; i >= 0; i--)
		{
			String keyInstancia = instancia + SEPARADOR_NIVEL + i;
			SimboloVariable s = tablaDeVariables.get(keyInstancia);
			if (s != null) return s;
		}			
		return null;
	}
	
	public Objeto valor (String nombreInstancia)
	{
		SimboloVariable variable = variable(nombreInstancia);
		return variable.objeto;
	}

	private abstract class Simbolo {
		@Override
		public abstract boolean equals(Object o);

		@Override
		public abstract int hashCode();
	}

	private class SimboloVariable extends Simbolo {
		private int nivel;
		private String nombreDeVariable;
		private Objeto objeto;

		SimboloVariable(int nivel, String nombreDeVariable, Objeto objeto) {
			this.nivel = nivel;
			this.nombreDeVariable = nombreDeVariable.toLowerCase();
			this.objeto = objeto;
		}

		public boolean equals(Object o) {
			boolean esInstanciaDeLlave = o instanceof Simbolo;
			if (!esInstanciaDeLlave) return false;
			boolean esElmismoBloque = ((SimboloVariable) o).nivel == this.nivel;
			if (!esElmismoBloque) return false;
			boolean esElmismoNombre = ((SimboloVariable) o).nombreDeVariable.equals(this.nombreDeVariable);
			return esElmismoNombre;
		}

		public int hashCode() {
			int hash = 7;
			hash = 97 * hash + this.nivel + this.nombreDeVariable.hashCode();
			return hash;
		}
	}

}
