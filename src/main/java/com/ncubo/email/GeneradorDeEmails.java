package com.ncubo.email;

import java.util.ArrayList;

import com.ncubo.chatbot.bitacora.Dialogo;
import com.ncubo.chatbot.configuracion.Constantes;

public class GeneradorDeEmails {

	private String email;
	
	public GeneradorDeEmails(){
		email = Constantes.EMAIL_BASE.replace("#COLOR#", Constantes.EMAIL_COLOR_BASE).
				replace("#LOGO#", Constantes.EMAIL_IMAGE_LOGO).
				replace("#FOOTER#", Constantes.EMAIL_IMAGE_FOOTER);
	}
	
	public GeneradorDeEmails(String baseDelCorreo, String colorBase, String imagenLogo){
		email = baseDelCorreo.replace("#COLOR#", colorBase).
				replace("#LOGO#", imagenLogo).
				replace("#FOOTER#", Constantes.EMAIL_IMAGE_FOOTER);
	}
	
	public String generarNuevoCorreo(ArrayList<Dialogo> logDeLaConversacion){
		String resultado = email;
		String conversacion = "";
		String ultimoTextoQueDijoElParticipante = "";
	
		for(Dialogo historico: logDeLaConversacion){
			
			if( ! historico.getElTextoQueDijoElFramework().isEmpty() && historico.getLoQueDijoElParticipante().isEmpty()){ // Lo dijo el agente
				
				conversacion += Constantes.EMAIL_BASE_CONVERSACION.replace("#QUIEN#", "other").
						replace("#TEXT#", historico.getElTextoQueDijoElFramework()).
						replace("#HORA#", historico.getLaFechaEnQueSeCreo().toString());
				
			}
			else if(historico.getElTextoQueDijoElFramework().isEmpty() && ! historico.getLoQueDijoElParticipante().isEmpty()){ // Lo fijo el participante
				ultimoTextoQueDijoElParticipante = historico.getLoQueDijoElParticipante();
				
				conversacion += Constantes.EMAIL_BASE_CONVERSACION.replace("#QUIEN#", "self").
						replace("#TEXT#", historico.getLoQueDijoElParticipante()).
						replace("#HORA#", historico.getLaFechaEnQueSeCreo().toString());
				
			}else if(! historico.getElTextoQueDijoElFramework().isEmpty() && ! historico.getLoQueDijoElParticipante().isEmpty()){
				if( ! ultimoTextoQueDijoElParticipante.equals(historico.getLoQueDijoElParticipante())){ // Primero hablo en participante
					ultimoTextoQueDijoElParticipante = historico.getLoQueDijoElParticipante();
					
					conversacion += Constantes.EMAIL_BASE_CONVERSACION.replace("#QUIEN#", "self").
							replace("#TEXT#", historico.getLoQueDijoElParticipante()).
							replace("#HORA#", historico.getLaFechaEnQueSeCreo().toString());
					
					conversacion += Constantes.EMAIL_BASE_CONVERSACION.replace("#QUIEN#", "other").
							replace("#TEXT#", historico.getElTextoQueDijoElFramework()).
							replace("#HORA#", historico.getLaFechaEnQueSeCreo().toString());
					
				}else{
				
					conversacion += Constantes.EMAIL_BASE_CONVERSACION.replace("#QUIEN#", "other").
							replace("#TEXT#", historico.getElTextoQueDijoElFramework()).
							replace("#HORA#", historico.getLaFechaEnQueSeCreo().toString());
					
				}
			}

		}
		
		resultado = resultado.replace("#CONVERSACION#", conversacion);
		return resultado;
	}
}
