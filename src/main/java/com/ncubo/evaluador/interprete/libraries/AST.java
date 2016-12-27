package com.ncubo.evaluador.interprete.libraries;

abstract class AST 
{
	private static String tabsGenerados = "";
	private static int anteriorTamano = 0;
	
	static protected final String generarTabs(int cantidad)
	{
		if (anteriorTamano != cantidad)
		{
			tabsGenerados = new String(new char[cantidad]).replace('\0', '\t');
			anteriorTamano = cantidad;
		}
		return tabsGenerados;
	}
}
