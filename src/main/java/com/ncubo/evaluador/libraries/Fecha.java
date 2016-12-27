package com.ncubo.evaluador.libraries;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import com.ncubo.evaluador.interprete.libraries.LanguageException;

public class Fecha  extends Objeto implements Comparable<Fecha>
{
	private int dia;
	private int mes;
	private int anno;

	public Fecha(int dia, int mes, int anno)
	{
		this.dia = dia;
		this.mes = mes;
		this.anno = anno;
		validaCreacionDeFecha(dia, mes, anno);
	}
	
	public Fecha(int dia, Meses mes, int anno)
	{
		this(dia, mes.numero(), anno);
	}
	
	public static Fecha minFecha()
	{
		return new Fecha(1, 1, 1950);
	}

	private static final int[] diasDelMes = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	
	private void validaCreacionDeFecha(int dia, int mes, int anno)
	{
		if(mes < 1 || mes > 12)
		{
			throw new LanguageException("El mes ingresado no es valido debe estar contenido entre 1-12");
		}
		boolean esAnoBisiesto = (anno % 4 == 0) && ((anno % 100 != 0) || (anno % 400 == 0));
		if (dia == 29 && mes == 2 && esAnoBisiesto)
		{
			//Esta Ok.
		}
		else if(dia < 1 || dia > diasDelMes[mes-1])
		{
			throw new LanguageException(String.format("El dia ingresado no es valido debe estar contenido entre 1 y %s", diasDelMes[mes-1]));
		}
		if(anno < 1950 || anno > 2050)
		{
			throw new LanguageException("El anno ingresado no es valido debe poseer 4 digitos");	
		}
	}
		
	public Date getDate()
	{
		return covertirFechaACalendar().getTime();
	}

	public int getDia() 
	{
		return dia;
	}

	public int getMes() 
	{
		return mes;
	}

	public int getAnno()
	{
		return anno;
	}

	public Mes mesDeLaFecha()
	{
		return new Mes(Meses.obtenerMes(mes), anno); 
	}
	
	public FechaHora toFechaHora()
	{
		return new FechaHora(dia, mes, anno, 0, 0, 0);
	}

	Fecha siguienteDia()
	{
		int sgteDia = dia;
		int sgteMes = mes;
		int sgteAno = anno;
		if (dia == mesDeLaFecha().diasDelMes() )
		{
			sgteDia = 1;
			sgteMes ++;
		}
		else
		{
			sgteDia++;
		}
		if (sgteMes == 13)
		{
			sgteMes = 1;
			sgteAno ++;
		}
		return new Fecha(sgteDia, sgteMes, sgteAno);
	}
	
	Fecha restarMeses(int mesesPorRestar)
	{
		Mes mes = this.mesDeLaFecha();
		for (int i = 0; i < mesesPorRestar; i++) mes = mes.mesAnterior();
		int diaAnterior = dia > mes.diasDelMes() ? mes.diasDelMes() : dia;
		return new Fecha(diaAnterior, mes.getMes(), mes.getAnno());
	}
	
	Fecha diaAnterior()
	{
		int diaAnterior = dia;
		int mesAnterior = mes;
		int anoAnterior = anno;
		if( dia == 1 & mes == 1 )
		{
			mesAnterior = 12;
			anoAnterior --;
			
			diaAnterior = new Mes(Meses.obtenerMes(mesAnterior), anoAnterior).diasDelMes();
		}
		if( dia == 1 & mes != 1 )
		{
			mesAnterior--;
			diaAnterior = new Mes(Meses.obtenerMes(mesAnterior), anoAnterior).diasDelMes();
		}
		if(dia != 1)
		{
			diaAnterior--;
		}
		return new Fecha(diaAnterior, mesAnterior, anoAnterior);
	}
	
	Fecha sumarDia(int dia)
	{
		Calendar fechaASumar = GregorianCalendar.getInstance();
		fechaASumar.set(this.anno, this.mes, this.dia);
		fechaASumar.add(Calendar.DATE, dia);
		return new Fecha(fechaASumar.get(Calendar.DAY_OF_MONTH), fechaASumar.get(Calendar.MONTH), fechaASumar.get(Calendar.YEAR));
	}
	
	@Override
	public String show()
	{
		String result = toString();
		return result;
	}

	@Override
	public String toString()
	{
		String resultado = (dia < 10 ? "0" + dia : "" + dia) + (mes < 10 ? "/0" + mes : "/" + mes) + "/" + anno;
		return resultado;
	}

	public int cantidadDiasQueHayAl(Fecha otraFecha)
	{
		Date miFecha = this.covertirFechaACalendar().getTime();
		Date laOtraFecha = otraFecha.covertirFechaACalendar().getTime();

		int resultado = (int) ( ( laOtraFecha.getTime() - miFecha.getTime() ) / (1000 * 60 * 60 * 24));
		return resultado; 
	}
	
	public boolean esUnFinDeSemana()
	{
		Calendar cal = covertirFechaACalendar();
		int diaSemana = cal.get( Calendar.DAY_OF_WEEK ); 
		return diaSemana == Calendar.SATURDAY || diaSemana == Calendar.SUNDAY;
	}
	
	public boolean esUnDomingo()
	{
		Calendar cal = covertirFechaACalendar();
		int diaSemana = cal.get( Calendar.DAY_OF_WEEK );
		return ( diaSemana == Calendar.SUNDAY);
	}

	
	private static final Calendar cal = new GregorianCalendar();
	private Calendar covertirFechaACalendar()
	{
		cal.clear();
		cal.set(anno, mes-1, dia);
		return cal;
	}

	public boolean esIgualQue(Fecha literal) 
	{
		return dia == literal.getDia() && mes == literal.getMes() && anno == literal.getAnno();
	}

	@Override
	public boolean esMayorQue(Objeto otraFecha)
	{
		try
		{
			return otraFecha instanceof Fecha ? esMayorQue((Fecha) otraFecha) : new FechaHora(dia, mes, anno, 0, 0 , 0).esMayorQue(otraFecha);
		}
		catch(ClassCastException e)
		{
			throw new LanguageException(
					String.format("En la comparación se esperaba el valor de tipo [%s] pero se encontro un valor de tipo [%s]", Fecha.class.getSimpleName(), otraFecha.getClass().getSimpleName())
					);
		}
	}
	
	@Override
	public boolean esMenorQue(Objeto otraFecha)
	{
		try
		{
			return otraFecha instanceof Fecha ? esMenorQue((Fecha) otraFecha) : new FechaHora(dia, mes, anno, 0, 0 , 0).esMenorQue(otraFecha);
		}
		catch(ClassCastException e)
		{
			throw new LanguageException(
					String.format("En la comparación se esperaba el valor de tipo [%s] pero se encontro un valor de tipo [%s]", Fecha.class.getSimpleName(), otraFecha.getClass().getSimpleName())
					);
		}
	}
	
	@Override
	public boolean esMayorOIgualQue(Objeto otraFecha)
	{
		try
		{
			return otraFecha instanceof Fecha ? esMayorOIgualQue((Fecha) otraFecha) : new FechaHora(dia, mes, anno, 0, 0 , 0).esMayorOIgualQue(otraFecha);
		}
		catch(ClassCastException e)
		{
			throw new LanguageException(
					String.format("En la comparación se esperaba el valor de tipo [%s] pero se encontro un valor de tipo [%s]", Fecha.class.getSimpleName(), otraFecha.getClass().getSimpleName())
					);
		}
	}
	
	@Override
	public boolean esMenorOIgualQue(Objeto otraFecha)
	{
		try
		{
			return otraFecha instanceof Fecha ? esMenorOIgualQue((Fecha) otraFecha) : new FechaHora(dia, mes, anno, 0, 0 , 0).esMenorOIgualQue(otraFecha);
		}
		catch(ClassCastException e)
		{
			throw new LanguageException(
					String.format("En la comparación se esperaba el valor de tipo [%s] pero se encontro un valor de tipo [%s]", Fecha.class.getSimpleName(), otraFecha.getClass().getSimpleName())
					);
		}
	}
	
	private boolean esMayorQue(Fecha otraFecha)
	{
		int miFecha = anno * 10000 + mes * 100 + dia;  //22/11/2013 => 20131122 
		int laOtraFecha = otraFecha.anno * 10000 + otraFecha.mes * 100 + otraFecha.dia;
		return miFecha > laOtraFecha;
	}

	private boolean esMenorQue(Fecha otraFecha)
	{
		int miFecha = anno * 10000 + mes * 100 + dia;  //22/11/2013 => 20131122 
		int laOtraFecha = otraFecha.anno * 10000 + otraFecha.mes * 100 + otraFecha.dia;
		return miFecha < laOtraFecha;
	}

	private boolean esMayorOIgualQue(Fecha otraFecha)
	{
		int miFecha = anno * 10000 + mes * 100 + dia;  //22/11/2013 => 20131122 
		int laOtraFecha = otraFecha.anno * 10000 + otraFecha.mes * 100 + otraFecha.dia;
		return miFecha >= laOtraFecha;
	}

	private boolean esMenorOIgualQue(Fecha otraFecha)
	{
		int miFecha = anno * 10000 + mes * 100 + dia;  //22/11/2013 => 20131122 
		int laOtraFecha = otraFecha.anno * 10000 + otraFecha.mes * 100 + otraFecha.dia;
		return miFecha <= laOtraFecha;
	}

	@Override
	public boolean esIgualQue(Objeto objeto) 
	{
		try
		{
			if (objeto instanceof Fecha)
			{
				Fecha fecha = (Fecha)objeto;
				return dia == fecha.dia && mes == fecha.mes && anno == fecha.anno;
			}
			else
			{
				FechaHora fecha = (FechaHora)objeto;
				return new FechaHora(dia, mes, anno, 0, 0 , 0).esIgualQue(fecha);
			}
		}
		catch(ClassCastException e)
		{
			throw new LanguageException(
					String.format("En la comparación se esperaba el valor de tipo [%s] pero se encontro un valor de tipo [%s]", Fecha.class.getSimpleName(), objeto.getClass().getSimpleName())
					);
		}
		
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
		resultado = prime * resultado + anno;
		resultado = prime * resultado + dia;
		resultado = prime * resultado + mes;
		return resultado;
	}

	@Override
	public boolean equals(Object objeto) 
	{
		Fecha otraFecha = (Fecha) objeto;
		return anno == otraFecha.anno && dia == otraFecha.dia && mes == otraFecha.mes;
	}

	@Override
	public int compareTo(Fecha fecha) 
	{
		int id = anno * 10000 + mes * 100 + dia;
		int idArgumento = fecha.anno * 10000 + fecha.mes * 100 + fecha.dia;
		return id - idArgumento;
	}
	
	private final static String[] DIAS_DE_LA_SEMANA = new String[]{"Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"};
	public String getDiaDeLaSemana()
	{
		int dia = covertirFechaACalendar().get(Calendar.DAY_OF_WEEK);
		return DIAS_DE_LA_SEMANA[dia-1];
	}
	
	public static Fecha fromString(String unaFecha)
	{
		int dia = dia(unaFecha);
		int mes = mes(unaFecha);
		int anno = anno(unaFecha);
		return new Fecha(dia, mes, anno);		
	}
	
	private static int dia (String unaFecha)
	{
		final char SEPARADOR = '/';
		int index = unaFecha.indexOf(SEPARADOR);
		return Integer.parseInt(unaFecha.substring(0, index));
	}
	
	private static int mes (String unaFecha)
	{
		final char SEPARADOR = '/';
		int indexIni = unaFecha.indexOf(SEPARADOR);
		int indexFin = unaFecha.lastIndexOf(SEPARADOR);
		return Integer.parseInt(unaFecha.substring(indexIni + 1, indexFin));
	}
	
	private static int anno (String unaFecha)
	{
		final char SEPARADOR = '/';
		int index = unaFecha.lastIndexOf(SEPARADOR);
		return Integer.parseInt(unaFecha.substring(index+1));
	}
}