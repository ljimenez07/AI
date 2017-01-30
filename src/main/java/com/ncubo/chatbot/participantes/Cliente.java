package com.ncubo.chatbot.participantes;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import com.ncubo.chatbot.configuracion.Constantes;
import com.ncubo.chatbot.contexto.AdministradorDeVariablesDeContexto;
import com.ncubo.chatbot.contexto.Variable;
import com.ncubo.chatbot.contexto.VariablesDeContexto;
import com.ncubo.chatbot.exceptiones.ChatException;
import com.ncubo.conectores.Conectores;

public class Cliente extends Participante{

	private String miNombre;
	private String miId;
	private ArrayList<String> misIdsDeSesiones = new ArrayList<String>();
	protected AdministradorDeVariablesDeContexto administradorDeVariablesDeContexto;
	private Conectores misConectores;
	
	public Cliente(Conectores conectores){
		miNombre = "";
		miId = "";
		administradorDeVariablesDeContexto = new AdministradorDeVariablesDeContexto();
		misConectores = conectores;
		verificarExistenciaDeLasVariables();
	}
	
	public Cliente(String nombre, String id, Conectores conectores) throws Exception{
		miNombre = nombre;
		miId = id;
		administradorDeVariablesDeContexto = new AdministradorDeVariablesDeContexto();
		misConectores = conectores;
		verificarExistenciaDeLasVariables();
	}
	
	public Cliente(String nombre, String id) throws Exception{
		miNombre = nombre;
		miId = id;
	}
	
	public String getMiNombre() {
		return miNombre;
	}

	public void setMiNombre(String miNombre) {
		this.miNombre = miNombre;
	}

	public String getMiId() {
		return miId;
	}

	public void setMiId(String miId) {
		this.miId = miId;
	}
	
	public ArrayList<String> getMisIdsDeSesiones() {
		return misIdsDeSesiones;
	}

	public void borrarTodosLosIdsDeSesiones() {
		this.misIdsDeSesiones.clear();
	}
	
	public void agregarIdsDeSesiones(String idDeSesion) {
		if( ! contieneElIdSesion(idDeSesion)){
			this.misIdsDeSesiones.add(idDeSesion);
		}
	}
	
	public boolean contieneElIdSesion(String idSesion){
		return misIdsDeSesiones.contains(idSesion);
	}
	
	public String evaluarCondicion(String comando) throws Exception{
		return administradorDeVariablesDeContexto.ejecutar(comando);
	}
	
	public ArrayList<String> buscarVariablesEstaticasEnElComando(String comando) throws Exception{
		return administradorDeVariablesDeContexto.buscarVariablesEstaticasEnElComando(comando);
	}
	
	// Nombre del cliente
	public void guardarNombreDelCliente(String nombre) throws Exception{
		String nombreVariable = VariablesDeContexto.getInstance().obtenerUnaVariableDeMiContexto("nombreCliente").getNombre();
		administradorDeVariablesDeContexto.ejecutar(String.format("%s = '%s'; show %s;", nombreVariable, nombre, nombreVariable));
	}
	
	public String obtenerNombreDelCliente() throws Exception{
		String nombreVariable = VariablesDeContexto.getInstance().obtenerUnaVariableDeMiContexto("nombreCliente").getNombre();
		return administradorDeVariablesDeContexto.obtenerVariable(nombreVariable).toString();
	}
	
	private void verificarExistenciaDeLasVariables(){
		System.out.println("Verificar variables del cliente ...");
		
		Hashtable<String, Variable> variables = VariablesDeContexto.getInstance().obtenerTodasLasVariablesDeMiContexto();
		Enumeration<String> keys = variables.keys();
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			Variable variable = variables.get(key);
			if(variable.getTipoVariable().equals(Constantes.TiposDeVariables.NEGOCIO) || variable.getTipoVariable().equals(Constantes.TiposDeVariables.CONTEXTO) || variable.getTipoVariable().equals(Constantes.TiposDeVariables.ENUM)){
				if(! misConectores.existeLaVariable(variable.getNombre(), variable.getTipoVariable())){ // TODO Obtener el nombre de la clase para instansear en el parser
					throw new ChatException(String.format("La variable %s no existe en el sistema. Esta clase debe ser creada previamente.", variable.getNombre()));
				}else{
					String nombreDeLaClase = misConectores.obtenerElNombreDeLaClase(variable.getNombre(), variable.getTipoVariable());
					String comando = variable.getNombre()+Constantes.VARIABLE+" = "+ nombreDeLaClase+"();";
					try {
						this.evaluarCondicion(comando);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		String comando = Constantes.INSTANCEA_PARAMETROS +" = "+Constantes.CLASE_PARAMETROS+"();";
		try {
			this.evaluarCondicion(comando);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Conectores obtenerMiConector(){
		return misConectores;
	}
}

