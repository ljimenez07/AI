package com.ncubo.realestate;

import java.util.List;

import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.partesDeLaConversacion.Contenido;
import com.ncubo.chatbot.partesDeLaConversacion.Tema;
import com.ncubo.chatbot.partesDeLaConversacion.Temario;
import com.ncubo.chatbot.partesDeLaConversacion.Temas;
import com.ncubo.chatbot.watson.Entidades;
import com.ncubo.chatbot.watson.Intenciones;

//TODO: aunque ya veo 1/2 claro... me huele mal que parezca plantilla.
public class TemarioDeRealEstate extends Temario
{
	
	protected TemarioDeRealEstate(String pathXML) {
		super(pathXML);
		// TODO Auto-generated constructor stub
	}

	private Tema saludo()
	{
		Tema resultado = new Tema
		(
			"saludo",
			"Saludo",
			"deam_house_universe",
			true,
			"deam_house_universe",
			frase("saludoGeneral"),
			frase("queBusca"),
			frase("saludar")
		);
		return resultado;
	}
	
	private Tema cualPais()
	{
		Tema resultado = new Tema
		(
			"cualPais",
			"Cual pais",
			"want_house",
			false,
			"want_house",
			frase("describirCostaRica"),
			frase("quiereCostaRica"),
			frase("noQuiereCostaRica")
		);
		return resultado;
	}
	
	private Tema conFamiliaOSolo()
	{
		Tema resultado = new Tema
		(
			"conFamiliaOSolo",
			"Con familia o solo",
			"want_house",
			false,
			"want_house",
			frase("vieneSoloOConLaFamilia"),
			frase("describaLaFamilia")
		);
		return resultado;
	}		
	
	private Tema determinarLocalizacion()
	{
		Tema resultado = new Tema
		(
			"determinarLocalizacion",
			"Determinar localizacion",
			"want_house",
			false,
			"want_house",
			frase("determinarLaLocalizacion"),
			frase("quierePiscina")
		);
		return resultado;
	}
	
	private Tema quierePatio()
	{
		Tema resultado = new Tema
		(
			"quierePatio",
			"patio",
			"want_house",
			false,
			"want_house",
			frase("describirPatio"),
			frase("quierePatio")
		);
		return resultado;
	}
	
	private Tema quiereEnCondominio()
	{
		Tema resultado = new Tema
		(
			"quiereEnCondominio",
			"Condominio",
			"want_house",
			false,
			"want_house",
			frase("quiereEnCondominio")
		);
		return resultado;
	}
	
	private Tema mostrarResultados()
	{
		Tema resultado = new Tema
		(
			"mostrarResultados",
			"Mostrar resultados",
			"want_house",
			false,
			"want_house",
			frase("mostrarResultados")
		);
		return resultado;
	}
	
	private Tema despedida()
	{
		Tema resultado = new Tema
		(
			"despedida",
			"Despedida",
			"deam_house_universe",
			true,
			"deam_house_universe",
			frase("despedida")
		);
		return resultado;
	}		
	
	@Override
	protected void cargarTemario(Temas temasDelDiscurso){
		
		System.out.println("Cargando temario ...");
		temasDelDiscurso.add(saludo());
		temasDelDiscurso.add(cualPais());
		temasDelDiscurso.add(conFamiliaOSolo());
		temasDelDiscurso.add(determinarLocalizacion());
		temasDelDiscurso.add(quierePatio());
		temasDelDiscurso.add(quiereEnCondominio());
		temasDelDiscurso.add(mostrarResultados());
		temasDelDiscurso.add(despedida());
	}
	
	public void cargarIntenciones(List<Intenciones> intenciones)
	{
		//intenciones.add (Intencion.get("WANT_HOUSE"));
		//intenciones.add (Intencion.get("GOODBYE"));
	}

	@Override
	protected void cargarEntidades(List<Entidades> entidades) {
		// TODO Auto-generated method stub
		//entidades.add (Entidad.get("INTEREST"));
		//entidades.add (Entidad.get("AFFIRMATIONS"));
		//entidades.add (Entidad.get("COUNTRIES"));
		//entidades.add (Entidad.get("LOCATIONS"));
		//entidades.add (Entidad.get("PLEASURES"));
	}
	
	@Override
	protected Contenido cargarContenido(String path){
		return new ContenidoDeRealEstate(path);
	}

	@Override
	protected void cargarDependencias(Temas temasDelDiscurso){
		temasDelDiscurso.get(2).dependeDe(temasDelDiscurso.get(1));
		temasDelDiscurso.get(6).dependeDe(temasDelDiscurso.get(2)).dependeDe(temasDelDiscurso.get(3))
							   .dependeDe(temasDelDiscurso.get(4)).dependeDe(temasDelDiscurso.get(5));
	}
	
	public static void main(String argv[]) {
		TemarioDeRealEstate temario = new TemarioDeRealEstate(Constantes.PATH_ARCHIVO_DE_CONFIGURACION_RS);
		temario.buscarTema(Constantes.FRASE_SALUDO);
	}
}
