package com.ncubo.evaluador.libraries;

import java.util.GregorianCalendar;

import com.ncubo.evaluador.interprete.libraries.LanguageException;

public class Mes extends Objeto implements Comparable<Mes>
{
	private Meses mes;
	private int ano;

	public Meses getMes() 
	{
		return mes;
	}

	public int getAnno() 
	{
		return ano;
	}

	public Mes(Meses mes, int anno)
	{
		this.ano = anno;
		this.mes = mes;

	}

	public Numero getFormatoAnnoMes() 
	{
		String temp = "" + ano;
		temp += mes.numero();

		return new Numero(Integer.parseInt(temp));
	}

	public Mes mesSiguiente()
	{
		if( mes.numero() == 12 )
		{
			return new Mes(Meses.ENE, ano + 1);
		}
		else
		{
			return new Mes( mes.siguienteMes(), ano );	
		}
	}
	
	Mes mesTrimestreSiguiente()
	{
		if( mes.numero() == 10 )
		{
			return new Mes(Meses.ENE, ano + 1);
		}
		else if( mes.numero() == 11 )
		{
			return new Mes(Meses.FEB, ano + 1);
		}
		else if( mes.numero() == 12 )
		{
			return new Mes(Meses.MAR, ano + 1);
		}
		else
		{
			return new Mes( mes.siguienteTrimestre(), ano );	
		}
	}
	
	public Mes mesAnterior()
	{
		if( mes.numero() == 1 )
		{
			return new Mes(Meses.DIC, ano - 1);
		}
		else
		{
			return new Mes(mes.anteriorMes() , ano );	
		}
	}

	@Override
	public String toString() 
	{
		return "" + mes.name() + "/" + ano;
	}
	
	private static final String SIMPLE_NAME = Mes.class.getSimpleName();
	
	@Override
	public String show() 
	{
		String result = "" + mes + "/" + ano;
		return "{\""+ SIMPLE_NAME + "\":\""+ result +"\"}";
	}
	
	private static final GregorianCalendar calendar = new GregorianCalendar();
	private boolean esAnoBisiesto()
	{
		return calendar.isLeapYear(ano) ;
	}

	public int diasDelMes()
	{
		boolean esFebrero = mes.numero() == 2;
		if (esFebrero)
		{
			boolean esBisiesto = esAnoBisiesto();
			if (esBisiesto) return 29;
		}
		final int[] DIAS_POR_MES = new int []{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31}; 		
		return DIAS_POR_MES[mes.numero() - 1];
	}

	public Fecha ultimoDiaDelMes()
	{
		return new Fecha(diasDelMes(), mes.numero(), ano);
	}
	
	public Fecha primerDiaDelMes()
	{
		return new Fecha(1, mes.numero(), ano);
	}

	@Override
	public boolean esIgualQue(Objeto objeto) 
	{
		Mes argumento = null;
		try
		{
			argumento = (Mes)objeto;				
		}
		catch(ClassCastException p)
		{
			throw new LanguageException(
					String.format("En la comparación se esperaba el valor de tipo [%s] pero se encontro un valor de tipo [%s]", Mes.class.getSimpleName(), objeto.getClass().getSimpleName())
					);
		}
		return mes == argumento.mes && ano == argumento.ano;
	}

	@Override
	public boolean noEsIgualQue(Objeto objeto) 
	{
		return ! esIgualQue(objeto);
	}

	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int resultado = 1;
		resultado = prime * resultado + ano;
		resultado = prime * resultado + mes.numero();
		return resultado;
	}

	@Override
	public boolean equals(Object objeto) 
	{
		Mes otroMes = (Mes) objeto;		
		return ano == otroMes.ano && mes == otroMes.mes;
	}

	public boolean comparteMismoTrimestreCon(Mes mesAcomparar) 
	{
		boolean resultado = false;
		boolean elMesActualEsPrimerTrimestre = mes.numero()== 1 || mes.numero() == 2 || mes.numero() == 3;
		boolean elMesActualEsSegundoTrimestre = mes.numero() == 4 || mes.numero() == 5 || mes.numero() == 6;
		boolean elMesActualEsTercerTrimestre = mes.numero() == 7 || mes.numero() == 8 || mes.numero() == 9;
		boolean elMesActualEsCuartoTrimestre = mes.numero() == 10 || mes.numero() == 11 || mes.numero() == 12;
			
		if(elMesActualEsPrimerTrimestre)
		{
			resultado = mesAcomparar.getMes().numero() >= 1 && mesAcomparar.getMes().numero() <= 3;				
		}
		else if(elMesActualEsSegundoTrimestre)
		{
			resultado = mesAcomparar.getMes().numero() >= 4 && mesAcomparar.getMes().numero() <= 6;
		}
		else if(elMesActualEsTercerTrimestre)
		{
			resultado = mesAcomparar.getMes().numero() >= 7 && mesAcomparar.getMes().numero() <= 9;
		}
		else if(elMesActualEsCuartoTrimestre)
		{
			resultado = mesAcomparar.getMes().numero() >= 10 && mesAcomparar.getMes().numero() <= 12;
		}

		return resultado;
	}

	boolean estaDentroDelRangoDeUnAnno(Mes mesFinal) 
	{	
		int diferencia = mesFinal.getAnno() * 12 + mesFinal.getMes().numero() - (this.getAnno() * 12 + this.getMes().numero());
		return diferencia >= 0 && diferencia < 12;
	}

	public Mes obtenerElMismoMesPeroUnAnoPosterior()
	{
		return new Mes(mes, ano + 1);
	}
	
	public int compareTo( Mes mes )
	{
		int id = hashCode();
		int idArgumento = mes.hashCode();
		
		return id - idArgumento;
	}
	
	@Override
	public boolean esMenorOIgualQue(Objeto objeto)  
	{
		Mes argumento = null;
		try
		{
			argumento = (Mes)objeto;				
		}
		catch(ClassCastException p)
		{
			throw new LanguageException(
					String.format("En la comparación se esperaba el valor de tipo [%s] pero se encontro un valor de tipo [%s]", Mes.class.getSimpleName(), objeto.getClass().getSimpleName())
					);
		}
		return ano < argumento.ano || (ano == argumento.ano && mes.numero() <= argumento.mes.numero());
	}
	
	@Override
	public boolean esMayorQue(Objeto objeto)  
	{
		Mes argumento = null;
		try
		{
			argumento = (Mes)objeto;				
		}
		catch(ClassCastException p)
		{
			throw new LanguageException(
					String.format("En la comparación se esperaba el valor de tipo [%s] pero se encontro un valor de tipo [%s]", Mes.class.getSimpleName(), objeto.getClass().getSimpleName())
					);
		}
		return ano > argumento.ano || (ano == argumento.ano && mes.numero() > argumento.mes.numero());
	}
	
	@Override
	public boolean esMenorQue(Objeto objeto)  
	{
		Mes argumento = null;
		try
		{
			argumento = (Mes)objeto;				
		}
		catch(ClassCastException p)
		{
			throw new LanguageException(
					String.format("En la comparación se esperaba el valor de tipo [%s] pero se encontro un valor de tipo [%s]", Mes.class.getSimpleName(), objeto.getClass().getSimpleName())
					);
		}
		return ano < argumento.ano || (ano == argumento.ano && mes.numero() < argumento.mes.numero());
	}
	
	@Override
	public boolean esMayorOIgualQue(Objeto objeto)  
	{
		Mes argumento = null;
		try
		{
			argumento = (Mes)objeto;				
		}
		catch(ClassCastException p)
		{
			throw new LanguageException(
					String.format("En la comparación se esperaba el valor de tipo [%s] pero se encontro un valor de tipo [%s]", Mes.class.getSimpleName(), objeto.getClass().getSimpleName())
					);
		}
		return ano > argumento.ano || (ano == argumento.ano && mes.numero() >= argumento.mes.numero());
	}

	public String nombre() 
	{
		return mes.nombreCompleto();
	}		
}
