package com.ncubo.chatbot.bitacora;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.ibm.watson.developer_cloud.conversation.v1.model.Entity;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ncubo.chatbot.partesDeLaConversacion.Salida;

// http://www.easywayserver.com/java/save-serializable-object-in-java/
public class Dialogo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3713915913976165952L;
	
	private String elTextoQueDijoElFramework = "";
	private String elAudioQueDijoElFramework = "";
	private String laVinetaQueMostroElFramework = "";
	private String idTemaQueUso = "" ;
	private String idFraseQueUso;
	private String loQueDijoElParticipante = "";
	private Date laFechaEnQueSeCreo;
	private int version;
	private String intencion = "";
	private String entidades = "";
	
	public Dialogo(Salida miSalida){
		this.elTextoQueDijoElFramework = miSalida.getMiTexto();
		this.elAudioQueDijoElFramework = miSalida.getMiSonido().url();
		this.laVinetaQueMostroElFramework = miSalida.getMisVinetas().get(0).obtenerContenido();
		this.idTemaQueUso = miSalida.getTemaActual().obtenerNombre();
		this.idFraseQueUso = miSalida.getFraseActual().obtenerIdDeLaFrase();
		if(miSalida.obtenerLaRespuestaDeIBM() != null){
			this.loQueDijoElParticipante = miSalida.obtenerLaRespuestaDeIBM().loQueElClienteDijoFue();
			MessageResponse response = miSalida.obtenerLaRespuestaDeIBM().messageResponse();
			List<Entity> listaEntidades = response.getEntities();
		  if(!listaEntidades.isEmpty())
		   this.entidades = procesarEntidades(listaEntidades);
		  }
		this.laFechaEnQueSeCreo = Calendar.getInstance().getTime();
		this.intencion = miSalida.getTemaActual().obtenerIntencionGeneralAlQuePertenece();
		this.version = miSalida.getFraseActual().getVersion();
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

	public String getIdFraseQueUso() {
		return idFraseQueUso;
	}

	public void setIdFraseQueUso(String idFraseQueUso) {
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
	
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getIntencion() {
		return intencion;
	}

	public void setIntencion(String intension) {
		this.intencion = intension;
	}
	public String getEntidades() {
		return entidades;
	}

	public void setEntidades(String entidades) {
		this.entidades = entidades;
	}
	
	private String procesarEntidades (List<Entity> lista){
		  StringBuilder buffer = new StringBuilder();
		  boolean processedFirst = false;
		  String entidades = "";

		  try{
		      for(Entity record: lista){
		          if(processedFirst)
		              buffer.append(",");
		          buffer.append(record.getEntity());
		          processedFirst = true;
		      }
		      entidades = buffer.toString();
		  }finally{
		      buffer = null;
		  }
		  return entidades;
		 }

	/*public static void main(String argv[]) {
		HistoricoDeLaConversacion historico = new HistoricoDeLaConversacion();
		historico.agregarHistorico("Hola!", "{\"textos\":[{\"texto\":\"�Hola!\",\"audio\":\"\"},{\"texto\":\"�En qu� puedo ayudarte?\",\"audio\":\"\"}]}");
		historico.agregarHistorico("como estas", "{\"textos\":[{\"texto\":\"�Hola!, soy tu asesor del Banco Atl�ntida.\",\"audio\":\"\"},{\"texto\":\"�En qu� puedo ayudarte?\",\"audio\":\"\"}]}");
		
		System.out.println(historico.verMiHistorico());
	}*/
}
