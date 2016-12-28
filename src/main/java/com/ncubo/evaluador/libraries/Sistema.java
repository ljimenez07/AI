package com.ncubo.evaluador.libraries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Sistema extends Objeto{

	private Date fechaActual;
	private final static DateFormat formato = new SimpleDateFormat("yyyy/MM/dd");
	private final static DateFormat formatoDeHora = new SimpleDateFormat("HH:mm:ss");
	
	public Sistema(){}
	
	public Hilera obtenerLaHoraActual(){
		fechaActual = new Date();
		String hora[] = formatoDeHora.format(fechaActual).split(":");
		return new Hilera(hora[0]);
	}
	
	public String show(){
		return "Hola: "+ obtenerLaHoraActual();
	}
	
	public static void main(String argv[]) throws Exception {
		Sistema sistema = new Sistema();
		System.out.println(sistema.obtenerLaHoraActual());
	}
}
