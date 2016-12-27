package com.ncubo.evaluador.interprete.libraries;

public class ComandoNulo extends Comando 
{
	private String lineaComentada;
	private Declaracion declaracion;
	
	public ComandoNulo(Declaracion declaracion)
	{
		this.declaracion = declaracion;
	}
	
	public ComandoNulo(String lineaComentada)
	{
		this.lineaComentada = lineaComentada;
	}

	public ComandoNulo()
	{
	}

	@Override
	public void ejecutar() 
	{
		
	}

	@Override
	public void validarEstaticamente() {}

	@Override
	void write(StringBuilder resultado, int tabs)
	{
		if(lineaComentada == null && declaracion == null)
		{
			resultado.append('\r');
		}
		else if(lineaComentada != null)
		{
			resultado.append(lineaComentada);
			resultado.append('\r');
		}
		else if(declaracion != null)
		{
			tabs++;
			declaracion.write(resultado, tabs);
			tabs--;
		}
	}

}
