package com.ncubo.logicaDeLasConversaciones;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.configuracion.Constantes.ModoDeLaVariable;
import com.ncubo.chatbot.configuracion.Constantes.TiposDeVariables;
import com.ncubo.chatbot.contexto.Variable;
import com.ncubo.chatbot.contexto.VariablesDeContexto;
import com.ncubo.chatbot.partesDeLaConversacion.ComponentesDeLaFrase;
import com.ncubo.chatbot.partesDeLaConversacion.Frase;
import com.ncubo.chatbot.partesDeLaConversacion.Placeholder;
import com.ncubo.chatbot.partesDeLaConversacion.Respuesta;
import com.ncubo.chatbot.partesDeLaConversacion.Salida;
import com.ncubo.chatbot.partesDeLaConversacion.Tema;
import com.ncubo.chatbot.participantes.AgenteDeLaConversacion;
import com.ncubo.chatbot.participantes.Cliente;
import com.ncubo.chatbot.watson.WorkSpace;

public class AgenteDelCliente extends AgenteDeLaConversacion{

	private Hashtable<String, String> misUltimosResultados = new Hashtable<>();
	
 	public AgenteDelCliente(){}
	
	public AgenteDelCliente(ArrayList<WorkSpace> miWorkSpaces){
		super(miWorkSpaces);
	}
	
	@Override
	public Salida decirUnaFrase(Frase frase, Respuesta respuesta, Tema tema, Cliente cliente, ModoDeLaVariable modoDeResolucionDeResultadosFinales, String idCliente){
		misUltimosResultados.clear();
		
		Salida salida = null;
		if(frase.hayFrasesConCondicion()){
			ArrayList<ComponentesDeLaFrase> misFrases = frase.extraerFrasesConCondicion();
			// TODO Evaluar si tiene plaseholders
			for(ComponentesDeLaFrase miFrase: misFrases){
				String comando = "";
				comando = "show "+miFrase.getCondicion()+";";
				
				ArrayList<String> variablesEstaticas = new ArrayList<>();
				try {
					variablesEstaticas = cliente.buscarVariablesEstaticasEnElComando(comando);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				if(variablesEstaticas != null && ! variablesEstaticas.isEmpty()){
					ArrayList<Placeholder> misPlaceholderEnLaCondicion = miFrase.buscarPlaceholdersEnLaCondicion(variablesEstaticas);
					if( ! misPlaceholderEnLaCondicion.isEmpty()){
						for(Placeholder placeholder: misPlaceholderEnLaCondicion){
							evaluarUnPlaceholder(respuesta, cliente, placeholder, misPlaceholderEnLaCondicion, modoDeResolucionDeResultadosFinales);
						}
					}
				}
				
				try {
					if(cliente.evaluarCondicion(comando).contains("true")){
						salida = new Salida();
						salida.escribir(miFrase, respuesta, tema, frase);
						break; // Para que ya no siga evaluando mas
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		// TODO Hay frases con plaseholders
		if(frase.hayFrasesConPlaceholders() && salida == null){
			salida = new Salida();
			String idAudio = "";
			
			final ComponentesDeLaFrase miFraseADecir = frase.extraerFraseSinonimoConPlaceholders();
			String fraseConPlaceholder = miFraseADecir.getTextoDeLaFrase();
			
			for(Placeholder placeholder: miFraseADecir.obtenerLosPlaceholders()){
				String valorARetornar = evaluarUnPlaceholder(respuesta, cliente, placeholder, miFraseADecir.obtenerLosPlaceholders(), modoDeResolucionDeResultadosFinales);
				if( ! valorARetornar.isEmpty()){
					miFraseADecir.sustituirPlaceholder(placeholder, valorARetornar.toString());
					idAudio = idAudio + "-" + valorARetornar;
				}
			}
			if(frase.soloTieneEnum(miFraseADecir) && miFraseADecir.getAudio(idAudio) !=  null)
				salida.escribir(miFraseADecir, respuesta, tema, frase, idAudio);
			else{ 
				miFraseADecir.setAudio("audio", miFraseADecir.generarAudio(idCliente));
				salida.escribir(miFraseADecir, respuesta, tema, frase);
			}
			salida.setMiTextoConPlaceholder(fraseConPlaceholder);
		}
		
		if(salida == null){
			salida = this.decir(frase, respuesta, tema);
		}
		return salida;
	}
	
	@Override
	public Salida volverAPreguntarUnaFrase(Frase pregunta, Respuesta respuesta, Tema tema, Cliente cliente, ModoDeLaVariable modoDeResolucionDeResultadosFinales, String idCliente) {
		return volverAPreguntarUnaFraseConMeRindo(pregunta, respuesta, tema, false, cliente, modoDeResolucionDeResultadosFinales, idCliente);
	}
	
	@Override
	public Salida volverAPreguntarUnaFraseConMeRindo(Frase pregunta, Respuesta respuesta, Tema tema, boolean meRindo, Cliente cliente, ModoDeLaVariable modoDeResolucionDeResultadosFinales, String idCliente){
		Salida salida = volverAPreguntarConMeRindo(pregunta, respuesta, tema, meRindo, false, idCliente);
	
		// TODO Hay frases con plaseholders
		if(salida.getMiTexto().contains("${")){
			String idAudio = "";
			ArrayList<Placeholder> placeholders = salida.obtienePlaceholders();
			for(Placeholder placeholder: placeholders){
				String valorARetornar = evaluarUnPlaceholder(respuesta, cliente, placeholder, placeholders, modoDeResolucionDeResultadosFinales);
				if( ! valorARetornar.isEmpty()){
					salida.sustituirPlaceholder(placeholder, valorARetornar.toString());
					idAudio = idAudio + "-" + valorARetornar;
				}
			}
			salida.setMiSonido(salida.getMiSonido().getTextoUsadoParaGenerarElSonido(), idCliente);
		}else{
			salida = this.volverAPreguntarConMeRindo(salida.getFraseActual(), respuesta, tema, meRindo, true, idCliente);
		}
		return salida;
	}
	
	private String evaluarUnPlaceholder(Respuesta respuesta, Cliente cliente, Placeholder plaseholder, ArrayList<Placeholder> plaseholders, ModoDeLaVariable modoDeResolucionDeResultadosFinales){
		String valorARetornar = "";
		
		if(misUltimosResultados.containsKey(plaseholder.getNombreDelPlaceholder())){
			valorARetornar = misUltimosResultados.get(plaseholder.getNombreDelPlaceholder());
		}else{
			agregarTodosLosParametrosALasVariablesDeAmbiente(respuesta, cliente, plaseholders);
			
			// Ejecutar el valor usando el parser
			String comando = "";
			if(modoDeResolucionDeResultadosFinales.equals(ModoDeLaVariable.PRUEBA)){
				comando = String.format("x = %s%s.cambiarModoPrueba(); ", plaseholder.getNombreDelPlaceholder(), Constantes.VARIABLE);
			}else{
				comando = String.format("x = %s%s.cambiarModoReal(); ", plaseholder.getNombreDelPlaceholder(), Constantes.VARIABLE);
			}
			comando += String.format("%s = %s%s.evaluar(); show %s;", plaseholder.getNombreDelPlaceholder(),plaseholder.getNombreDelPlaceholder(), Constantes.VARIABLE ,plaseholder.getNombreDelPlaceholder());

			try {
				valorARetornar = cliente.evaluarCondicion(comando).trim().replace("\"", "");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if( ! valorARetornar.isEmpty())
				misUltimosResultados.put(plaseholder.getNombreDelPlaceholder(), valorARetornar);
		}
		
		return valorARetornar;
	}
	
	private void agregarTodosLosParametrosALasVariablesDeAmbiente(Respuesta respuesta, Cliente cliente, ArrayList<Placeholder> plaseholders){
		
		actualizarTodasLasVariablesDeContexto(respuesta, cliente);
		
		// Actualizar los en todas las variables de ambiente
		for(Placeholder placeholder: plaseholders){
			if(placeholder.getTipoDePlaceholder().equals(TiposDeVariables.ENUM)){
				String[] valores = VariablesDeContexto.getInstance().obtenerUnaVariableDeMiContexto(placeholder.getNombreDelPlaceholder()).getValorDeLaVariable();
				//Lista miListaDeSinonimos = new Lista();
				String comando = String.format("%s = Lista();", "lista");
				ejecutarParametroEnElParser(cliente, comando);
				
				for(String valor: valores){
					//miListaDeSinonimos.guardarObjeto(new Hilera(valor));
					comando = String.format("xx = %s.guardarObjeto(Hilera('%s'));", "lista", valor);
					ejecutarParametroEnElParser(cliente, comando);
				}
				
				ejecutarParametroEnElParser(cliente, placeholder.getNombreDelPlaceholder(), "lista");
			}
			
			String comando = "x = "+placeholder.getNombreDelPlaceholder()+Constantes.VARIABLE+".agregarParametros("+Constantes.INSTANCEA_PARAMETROS+");";
			try {
				cliente.evaluarCondicion(comando);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void actualizarTodasLasVariablesDeContexto(Respuesta respuesta, Cliente cliente){
		
		Hashtable<String, Variable> variables = VariablesDeContexto.getInstance().obtenerTodasLasVariablesDeMiContexto();
		Enumeration<String> keys = variables.keys();
		
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			Variable variable = variables.get(key);
			if(variable.getTipoVariable().equals(Constantes.TiposDeVariables.CONTEXTO)){
				try{
					String nis = respuesta.obtenerElementoDelContextoDeWatson(variable.getNombre());
					String comando = String.format("%s = Lista();", "lista");
					ejecutarParametroEnElParser(cliente, comando);
					comando = String.format("xx = %s.guardarObjeto(Hilera('%s'));", "lista", nis);
					ejecutarParametroEnElParser(cliente, comando);
					
					ejecutarParametroEnElParser(cliente, variable.getNombre(), "lista");
				}catch(Exception e){}
			}
		}
	}
	
}
