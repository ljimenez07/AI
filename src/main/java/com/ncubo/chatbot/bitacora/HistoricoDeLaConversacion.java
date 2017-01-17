package com.ncubo.chatbot.bitacora;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import com.ncubo.chatbot.partesDeLaConversacion.Salida;

// http://www.easywayserver.com/java/save-serializable-object-in-java/
public class HistoricoDeLaConversacion implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3713915913976165952L;
	
	public String elTextoQueDijoElFramework = "";
	public String elAudioQueDijoElFramework = "";
	public String laVinetaQueMostroElFramework = "";
	public String idTemaQueUso = "" ;
	public int idFraseQueUso;
	public String loQueDijoElParticipante = "";
	public Date laFechaEnQueSeCreo;
	
	public HistoricoDeLaConversacion(Salida miSalida){
		this.elTextoQueDijoElFramework = miSalida.getMiTexto();
		this.elAudioQueDijoElFramework = miSalida.getMiSonido().url();
		this.laVinetaQueMostroElFramework = miSalida.getMisVinetas().get(0).obtenerContenido();
		this.idTemaQueUso = miSalida.getTemaActual().obtenerIdTema();
		this.idFraseQueUso = miSalida.getFraseActual().obtenerIdDeLaFrase();
		if(miSalida.obtenerLaRespuestaDeIBM() != null)
			this.loQueDijoElParticipante = miSalida.obtenerLaRespuestaDeIBM().loQueElClienteDijoFue();
		this.laFechaEnQueSeCreo = Calendar.getInstance().getTime();;
	}
	
	public String getElTextoQueDijoElFramework() {
		return elTextoQueDijoElFramework;
	}

	public void setElTextoQueDijoElFramework(String elTextoQueDijoElFramework) {
		this.elTextoQueDijoElFramework = elTextoQueDijoElFramework;
	}

	public String getElAudioQueDijoElFramework() {
		return elAudioQueDijoElFramework;
	}

	public void setElAudioQueDijoElFramework(String elAudioQueDijoElFramework) {
		this.elAudioQueDijoElFramework = elAudioQueDijoElFramework;
	}

	public String getLaVinetaQueMostroElFramework() {
		return laVinetaQueMostroElFramework;
	}

	public void setLaVinetaQueMostroElFramework(String laVinetaQueMostroElFramework) {
		this.laVinetaQueMostroElFramework = laVinetaQueMostroElFramework;
	}

	public String getIdTemaQueUso() {
		return idTemaQueUso;
	}

	public void setIdTemaQueUso(String idTemaQueUso) {
		this.idTemaQueUso = idTemaQueUso;
	}

	public int getIdFraseQueUso() {
		return idFraseQueUso;
	}

	public void setIdFraseQueUso(int idFraseQueUso) {
		this.idFraseQueUso = idFraseQueUso;
	}

	public String getLoQueDijoElParticipante() {
		return loQueDijoElParticipante;
	}

	public void setLoQueDijoElParticipante(String loQueDijoElParticipante) {
		this.loQueDijoElParticipante = loQueDijoElParticipante;
	}

	public Date getLaFechaEnQueSeCreo() {
		return laFechaEnQueSeCreo;
	}

	public void setLaFechaEnQueSeCreo(Date laFechaEnQueSeCreo) {
		this.laFechaEnQueSeCreo = laFechaEnQueSeCreo;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	/*public static void main(String argv[]) {
		HistoricoDeLaConversacion historico = new HistoricoDeLaConversacion();
		historico.agregarHistorico("Hola!", "{\"textos\":[{\"texto\":\"¡Hola!\",\"audio\":\"\"},{\"texto\":\"¿En qué puedo ayudarte?\",\"audio\":\"\"}]}");
		historico.agregarHistorico("como estas", "{\"textos\":[{\"texto\":\"¡Hola!, soy tu asesor del Banco Atlántida.\",\"audio\":\"\"},{\"texto\":\"¿En qué puedo ayudarte?\",\"audio\":\"\"}]}");
		
		System.out.println(historico.verMiHistorico());
	}*/
}
