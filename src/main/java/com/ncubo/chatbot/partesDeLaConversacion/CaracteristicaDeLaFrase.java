package com.ncubo.chatbot.partesDeLaConversacion;

public final class CaracteristicaDeLaFrase
{
	public static final CaracteristicaDeLaFrase esUnaPregunta = new CaracteristicaDeLaFrase();
	public static final CaracteristicaDeLaFrase esUnSaludo = new CaracteristicaDeLaFrase();
	public static final CaracteristicaDeLaFrase esUnaOracionAfirmativa = new CaracteristicaDeLaFrase();
	public static final CaracteristicaDeLaFrase esUnaDespedida = new CaracteristicaDeLaFrase();
	public static final CaracteristicaDeLaFrase esUnaConjuncion = new CaracteristicaDeLaFrase();
	
	public static final CaracteristicaDeLaFrase noPuedeDecirEnVozAlta = new CaracteristicaDeLaFrase();
	public static final CaracteristicaDeLaFrase sePuedeDecirEnVozAlta = new CaracteristicaDeLaFrase();
	
	public static final CaracteristicaDeLaFrase esUnaPreguntaMandatoria = new CaracteristicaDeLaFrase();
	public static final CaracteristicaDeLaFrase noUnaPreguntaMandatoria = new CaracteristicaDeLaFrase();
	
	private CaracteristicaDeLaFrase(){}
	
}