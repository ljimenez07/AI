package com.ncubo.chatbot.participantes;

import java.util.ArrayList;
import com.ncubo.chatbot.contexto.AdministradorDeVariablesDeContexto;

public class Cliente extends Participante{

	private String miNombre;
	private String miId;
	private ArrayList<String> misIdsDeSesiones = new ArrayList<String>();
	private AdministradorDeVariablesDeContexto administradorDeVariablesDeContexto;
	
	public Cliente(){
		miNombre = "";
		miId = "";
		//administradorDeVariablesDeContexto = new AdministradorDeVariablesDeContexto();
		/*administradorDeVariablesDeContexto.agregarVariableDeContexto("leGustaLosHoteles", "0");
		administradorDeVariablesDeContexto.agregarVariableDeContexto("leGustaComerAfuera", "0");
		administradorDeVariablesDeContexto.agregarVariableDeContexto("sePreocupaPorLaSalud", "0");*/
		administradorDeVariablesDeContexto = new AdministradorDeVariablesDeContexto();
	}
	
	public Cliente(String nombre, String id) throws Exception{
		miNombre = nombre;
		miId = id;
		administradorDeVariablesDeContexto = new AdministradorDeVariablesDeContexto();
		guardarNombreDelCliente(nombre);
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
	
	public void actualizarValoresDeConocerte(String leGustaLosHoteles, String leGustaComerAfuera, String sePreocupaPorLaSalud) throws Exception{
		double valoresDeHoteles = (obtenerValorDeGustosDeHoteles() + Double.parseDouble(leGustaLosHoteles)) / 2;
		actualizarGustosDeHoteles(valoresDeHoteles+"");
		
		double valoresDeRestaurantes = (obtenerValorDeGustosDeRestaurantes() + Double.parseDouble(leGustaComerAfuera)) / 2;
		actualizarGustosDeRestaurantes(valoresDeRestaurantes+"");
		
		double valoresDeBelleza = (obtenerValorDeGustosDeBelleza() + Double.parseDouble(sePreocupaPorLaSalud)) / 2;
		actualizarGustosDeBelleza(valoresDeBelleza+"");
		
	}
	
	// Hoteles
	public void actualizarGustosDeHoteles(String valor) throws Exception
	{
		administradorDeVariablesDeContexto.ejecutar(String.format("leGustaLosHoteles = %s; show leGustaLosHoteles;", valor));
	}
	
	public double obtenerValorDeGustosDeHoteles() throws Exception
	{
		return Double.parseDouble(administradorDeVariablesDeContexto.obtenerVariable("leGustaLosHoteles"));
	}
	
	// Restaurantes
	public void actualizarGustosDeRestaurantes(String valor) throws Exception
	{
		administradorDeVariablesDeContexto.ejecutar(String.format("leGustaComerAfuera = %s; show leGustaComerAfuera;", valor));
	}
	
	public double obtenerValorDeGustosDeRestaurantes() throws Exception
	{
		return Double.parseDouble(administradorDeVariablesDeContexto.obtenerVariable("leGustaComerAfuera"));
	}
	
	// Belleza	
	public void actualizarGustosDeBelleza(String valor) throws Exception
	{
		administradorDeVariablesDeContexto.ejecutar(String.format("sePreocupaPorLaSalud = %s; show sePreocupaPorLaSalud;", valor));
	}
	
	public double obtenerValorDeGustosDeBelleza() throws Exception
	{
		return Double.parseDouble(administradorDeVariablesDeContexto.obtenerVariable("sePreocupaPorLaSalud"));
	}
	
	// Esta logueado
	public void cambiarEstadoDeLogeo(boolean estado) throws Exception{
		administradorDeVariablesDeContexto.ejecutar(String.format("estaLogueado = %s; show estaLogueado;", String.valueOf(estado)));
	}
	
	public boolean obtenerEstadoDeLogeo() throws Exception
	{
		return Boolean.parseBoolean(administradorDeVariablesDeContexto.obtenerVariable("estaLogueado").toString().trim().replace("\"", ""));
	}
	
	// Nombre del cliente
	public void guardarNombreDelCliente(String nombre) throws Exception{
		administradorDeVariablesDeContexto.ejecutar(String.format("nombreCliente = '%s'; show nombreCliente;", nombre));
	}
	
	public String obtenerNombreDelCliente() throws Exception{
		return administradorDeVariablesDeContexto.obtenerVariable("nombreCliente").toString();
	}
	
	// tiene tarjetas de credito
	public void guardarSiTieneTarjetaCredito(boolean tieneTarjeta) throws Exception{
		administradorDeVariablesDeContexto.ejecutar(String.format("tieneTarjetaCredito = %s; show tieneTarjetaCredito;", tieneTarjeta));
	}
	
	public boolean obtenerSiTieneTarjetaCredito() throws Exception{
		return Boolean.parseBoolean(administradorDeVariablesDeContexto.obtenerVariable("tieneTarjetaCredito").toString().trim().replace("\"", ""));
	}
	
	// tiene tarjetas de credito
	public void guardarSiTieneCuentaAhorros(boolean tieneCuenta) throws Exception{
		administradorDeVariablesDeContexto.ejecutar(String.format("tieneCuentaAhorros = %s; show tieneCuentaAhorros;", tieneCuenta));
	}
		
	public boolean obtenerSiTieneCuentaAhorros() throws Exception{
		return Boolean.parseBoolean(administradorDeVariablesDeContexto.obtenerVariable("tieneCuentaAhorros").toString().trim().replace("\"", ""));

	}
}

