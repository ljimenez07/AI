package com.ncubo.regresion;

import java.util.ArrayList;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class RegresionTest {
 
	@Test(dataProvider = "obtenerStatusYObservaciones")
	 public void regresion(Boolean status, String observaciones) throws Exception {
			Assert.assertTrue(status, observaciones);
	 }

	 @DataProvider(name = "obtenerStatusYObservaciones")	
	 public static Object[][] obtenerStatusYObservaciones() throws Exception {
		
		 EjecucionCasosDePrueba comparador = new EjecucionCasosDePrueba();
	     ArrayList<Resultado> resultados = comparador.getResultados();
		 Object[][] data = new Object[resultados.size()][2];

	     for (int i = 0; i < resultados.size(); i++) {
	    	 data[i][0] =resultados.get(i).getStatus();
	    	 data[i][1] =resultados.get(i).getObservaciones().toString();
	    }
	    return data; 
	 }

}
