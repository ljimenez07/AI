package com.ncubo.regresion;

public class Regresion {

	public Regresion(){
	}
	
	public void run() throws Exception{
		EjecucionCasosDePrueba casos = new EjecucionCasosDePrueba();
		casos.correrCasosDesdeXML("src/main/resources/conversacionesMuni1.xml", "src/main/resources/casosDMuni.xml","src/main/resources/testng.xml", "casos DMuni");
	}
	
	public static void main(String[] args) throws Exception{
		Regresion regresion = new Regresion();
		regresion.run();
	}
}
