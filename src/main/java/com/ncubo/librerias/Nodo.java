package com.ncubo.librerias;

class Nodo
{
	private String key;
	private byte[] valor;
	private Nodo previo;
	private Nodo siguiente;

	Nodo(String key, byte[] valor)
	{
		this.key = key;
		this.valor = valor;
	}

	String getKey()
	{
		return key;
	}

	void setKey(String key)
	{
		this.key = key;
	}

	byte[] getValor()
	{
		return valor;
	}

	void setValor(byte[] valor)
	{
		this.valor = valor;
	}

	Nodo getPrevio()
	{
		return previo;
	}

	void setPrevio(Nodo previo)
	{
		this.previo = previo;
	}

	Nodo getSiguiente()
	{
		return siguiente;
	}

	void setSiguiente(Nodo siguiente)
	{
		this.siguiente = siguiente;
	}
}
