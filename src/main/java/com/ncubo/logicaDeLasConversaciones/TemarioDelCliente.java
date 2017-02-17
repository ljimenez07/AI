package com.ncubo.logicaDeLasConversaciones;

import java.util.List;

import com.ncubo.chatbot.partesDeLaConversacion.Contenido;
import com.ncubo.chatbot.partesDeLaConversacion.Tema;
import com.ncubo.chatbot.partesDeLaConversacion.Temario;
import com.ncubo.chatbot.partesDeLaConversacion.Temas;
import com.ncubo.chatbot.watson.Entidades;
import com.ncubo.chatbot.watson.Intenciones;

public class TemarioDelCliente extends Temario{
	
	public TemarioDelCliente(Contenido contenido) {
		super(contenido);
	}
	
	@Override
	protected void cargarTemario(Temas temasDelDiscurso){
		System.out.println("Cargando temario ...");
		for(Tema tema: this.contenido().obtenerMisTemas()){
			temasDelDiscurso.add(tema);
		}
	}
	
	public void cargarIntenciones(List<Intenciones> intenciones){
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
	
	/*@Override
	protected Contenido cargarContenido(String path){
		return new ContenidoDelCliente(path);
	}*/

	@Override
	protected void cargarDependencias(Temas temasDelDiscurso){
		//temasDelDiscurso.get(1).dependeDe(temasDelDiscurso.get(0));
	}
	
	public static void main(String argv[]) {
		//TemarioDelCliente temario = new TemarioDelCliente(Constantes.PATH_ARCHIVO_DE_CONFIGURACION_BA);
	}
}
