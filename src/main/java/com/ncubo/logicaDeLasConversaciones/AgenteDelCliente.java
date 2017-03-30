package com.ncubo.logicaDeLasConversaciones;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import com.ibm.watson.developer_cloud.conversation.v1.model.Entity;
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
import com.ncubo.chatbot.partesDeLaConversacion.Sonido;
import com.ncubo.chatbot.partesDeLaConversacion.Tema;
import com.ncubo.chatbot.partesDeLaConversacion.Vineta;
import com.ncubo.chatbot.participantes.AgenteDeLaConversacion;
import com.ncubo.chatbot.participantes.Cliente;

public class AgenteDelCliente extends AgenteDeLaConversacion{

	private Hashtable<String, String> misUltimosResultados = new Hashtable<>();
	private TemariosDeUnCliente temariosDelCliente;
 	public AgenteDelCliente(){}
	
	public AgenteDelCliente(TemariosDeUnCliente temarios){
		super(temarios);
		temariosDelCliente = temarios;
	}
	
	@Override
	public Salida decirUnaFrase(Frase frase, Respuesta respuesta, Tema tema, Cliente cliente, ModoDeLaVariable modoDeResolucionDeResultadosFinales, String idCliente){
		misUltimosResultados.clear();
		ComponentesDeLaFrase miFraseADecir = null;
		Salida salida = null;
		if(!frase.hayFrasesConCondicion()&&!frase.hayFrasesConPlaceholders())
			actualizarTodasLasVariablesDeContexto(respuesta,cliente);
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
						miFraseADecir = miFrase;
						break; // Para que ya no siga evaluando mas
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		// TODO Hay frases con plaseholders
		if(frase.hayFrasesConPlaceholders()){
			salida = new Salida();
			String idAudio = "";
			
			if(miFraseADecir == null)
				miFraseADecir = frase.extraerFraseSinonimoConPlaceholders();
			String texto = miFraseADecir.getTextoDeLaFrase();
			String audio = miFraseADecir.getTextoAUsarParaGenerarElAudio();
			Vineta vineta = null;
			String fraseConPlaceholder = miFraseADecir.getTextoDeLaFrase();
			for(Placeholder placeholder: miFraseADecir.obtenerLosPlaceholders()){
				String valorARetornar = evaluarUnPlaceholder(respuesta, cliente, placeholder, miFraseADecir.obtenerLosPlaceholders(), modoDeResolucionDeResultadosFinales);
				if( ! valorARetornar.isEmpty()){
					
					String formatoDelPlaceholder = String.format("${%s}", placeholder.getNombreDelPlaceholder());
					if(miFraseADecir.hayExpresionRegularEnElTexto(texto, placeholder)){
						texto =  texto.replace(formatoDelPlaceholder, valorARetornar);
					}
					
					if(miFraseADecir.hayExpresionRegularEnElTexto(audio, placeholder)){
						audio = audio.replace(formatoDelPlaceholder, valorARetornar);
					}
					
					if(vineta != null){
						if(miFraseADecir.hayExpresionRegularEnElTexto(miFraseADecir.getVineta().getContenido(), placeholder)){
							vineta = miFraseADecir.getVineta();
							String contenido = vineta.getContenido().replace(formatoDelPlaceholder, valorARetornar);
							vineta.cambiarElContenido(contenido);
						
						}
					}
					idAudio = idAudio + "-" + valorARetornar;
				}
			}
			if(frase.soloTieneEnum(miFraseADecir) && miFraseADecir.getAudio(idAudio) !=  null)
				salida.escribir(miFraseADecir, respuesta, tema, frase, idAudio,texto, vineta);
			else{ 
				Sonido sonido = miFraseADecir.generarAudio(audio, idCliente);
				salida.escribir(texto, sonido, respuesta, tema, frase,vineta);
			}
			salida.setMiTextoConPlaceholder(fraseConPlaceholder);
		}
		
		if(salida == null){
			salida = this.decir(frase, respuesta, tema, temariosDelCliente.obtenerIntenciones().getINTENCION_DESPEDIDA());
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
		
		return valorARetornar;
	}
	
	private String procesarEntidadSys (List<Entity> lista, String entidad){
		  String entidades = "";

		 for(Entity record: lista){
		          if(record.getEntity().equals("sys-number"))
		        	return record.getValue();
		      }
		 
		 return entidades;
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
					if(valor.startsWith("sys-"))
						valor = procesarEntidadSys(respuesta.messageResponse().getEntities(),valor);
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
					
					if(nis.startsWith("sys-")&&variable.getValorDeLaVariable()[0].equals("") || nis.equals("actualizarSysNumber"))
					{
						nis = procesarEntidadSys(respuesta.messageResponse().getEntities(), nis);
					}
					if(nis.equals("")&&!variable.getValorDeLaVariable()[0].equals("")){
						String comando = String.format("%s = Lista();", "lista");
						ejecutarParametroEnElParser(cliente, comando);
						comando = String.format("xx = %s.guardarObjeto(Hilera('%s'));", "lista", variable.getValorDeLaVariable()[0]);
						ejecutarParametroEnElParser(cliente, comando);
						
						ejecutarParametroEnElParser(cliente, variable.getNombre(), "lista");
					}
					if(!nis.equals("")&&!nis.startsWith("sys-")){
						String comando = String.format("%s = Lista();", "lista");
						ejecutarParametroEnElParser(cliente, comando);
						comando = String.format("xx = %s.guardarObjeto(Hilera('%s'));", "lista", nis);
						ejecutarParametroEnElParser(cliente, comando);
						
						ejecutarParametroEnElParser(cliente, variable.getNombre(), "lista");
						variable.setValorDeLaVariable(new String[]{nis}); 
					}	
				}catch(Exception e){}
			}
		}
	}
}
