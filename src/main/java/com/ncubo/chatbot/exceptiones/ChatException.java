package com.ncubo.chatbot.exceptiones;

public class ChatException extends RuntimeException
{
	public ChatException(String mensaje)
	{
		super (mensaje);
	}
}
