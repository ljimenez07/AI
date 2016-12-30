package com.ncubo.chatbot.variablesDeAmbiente;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ncubo.evaluador.libraries.Numero;
import com.ncubo.evaluador.libraries.Objeto;

public class Sistema extends Objeto{

	private Date fechaActual;
	private final static DateFormat formato = new SimpleDateFormat("yyyy/MM/dd");
	private final static DateFormat formatoDeHora = new SimpleDateFormat("HH:mm:ss");
	
	public Sistema(){}
	
	public Numero annoActual(){
		fechaActual = new Date();
		String hora[] = formato.format(fechaActual).split("/");
		return new Numero(Integer.parseInt(hora[0]));
	}
	
	public Numero mesActual(){
		fechaActual = new Date();
		String hora[] = formato.format(fechaActual).split("/");
		return new Numero(Integer.parseInt(hora[1]));
	}
	
	public Numero diaActual(){
		fechaActual = new Date();
		String hora[] = formato.format(fechaActual).split("/");
		return new Numero(Integer.parseInt(hora[2]));
	}
	
	public Numero horaActual(){
		fechaActual = new Date();
		String hora[] = formatoDeHora.format(fechaActual).split(":");
		return new Numero(Integer.parseInt(hora[0]));
	}
	
	public Numero minutoActual(){
		fechaActual = new Date();
		String hora[] = formatoDeHora.format(fechaActual).split(":");
		return new Numero(Integer.parseInt(hora[1]));
	}
	
	public static void main(String argv[]) throws Exception {
		Sistema sistema = new Sistema();
		System.out.println(sistema.horaActual());
	}
}
