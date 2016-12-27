package com.ncubo.evaluador.interprete.libraries;

import com.ncubo.evaluador.db.TablaDeSimbolos;
import com.ncubo.evaluador.libraries.Objeto;

public class IdConPunto extends Punto 
{
	private String instancia;
	private final TablaDeSimbolos tablaDeSimbolos;
	
	public IdConPunto (TablaDeSimbolos tablaDeSimbolos, Id instancia, String propiedad)
	{
		super(propiedad);
		this.instancia = instancia.getValor();//.toLowerCase();
		this.tablaDeSimbolos = tablaDeSimbolos;
	}
	
	String instancia ()
	{
		return instancia;
	}
	 
	Class<?>[] firmaNecesariaParaElMetodo() throws Exception
	{
		Expresion[] argumentos = argumentos();
		Class<?>[] resultado = new Class[argumentos.length];
		for (int i = 0; i < argumentos.length; i++ ) 
		{
			resultado[i] = argumentos[i].calcularTipo();
		}
		return resultado;
	}
	
	@Override
	Class<? extends Objeto> calcularTipo() throws Exception
	{
		Class<? extends Objeto> resultado = calcularElTipoDeUnCallExpresion( obtenerElObjeto().getClass() );
		return resultado;
	}
	
	@Override
	protected Object obtenerElObjeto()
	{
		if ( ! tablaDeSimbolos.existeLaVariable(instancia) )
		{
			throw new LanguageException("La variable '" + instancia + "' es desconocida");
		}
		Objeto objetoInstancia = tablaDeSimbolos.valor(instancia); 
		
		return (Object) objetoInstancia;
	}

	@Override
	void write(StringBuilder resultado) 
	{
		resultado.append(instancia);
		resultado.append('.');
		
		if(propiedad() != null)
		{
			resultado.append(propiedad());
		}
		else
		{
			Expresion[] argumentos = argumentos();
			resultado.append(metodo());
			resultado.append('(');			
			for(int i = 0; i< argumentos.length; i++)
			{
				if (i > 0) resultado.append(", ");
				argumentos[i].write(resultado);
			}
			resultado.append(')');
			
		}
	}
}
