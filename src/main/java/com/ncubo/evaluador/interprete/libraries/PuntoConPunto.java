package com.ncubo.evaluador.interprete.libraries;

import com.ncubo.evaluador.libraries.Objeto;

public class PuntoConPunto extends Punto 
{
	private Punto instancia;
	
	public PuntoConPunto (Punto instancia, String metodo, Expresion[] argumentos)
	{
		super(metodo, argumentos);
		this.instancia = instancia;
	}
	
	public PuntoConPunto (Punto instancia, String propiedad)
	{
		super(propiedad);
		this.instancia = instancia;
	}
	
	@Override
	protected Object obtenerElObjeto()
	{
		Objeto resultado = instancia.ejecutar();
		return resultado;
	}
	
	@Override
	Class<? extends Objeto> calcularTipo()
	{
		Class<?> classDeLaInstancia = instancia.calcularTipo();
		Class<? extends Objeto> resultado = calcularElTipoDeUnCallExpresion(classDeLaInstancia);
		return resultado;
	}

	@Override
	void write(StringBuilder resultado)
	{
		instancia.write(resultado);
		
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
			for(int i = 0; i < argumentos.length; i++)
			{
				if (i > 0) resultado.append(", ");
				argumentos[i].write(resultado);
			}
			resultado.append(')');
		}
	}

	@Override
	public void buscarVariablesEstaticas() {
		// TODO Auto-generated method stub
		
	}

}
