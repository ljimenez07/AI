package com.ncubo.caches;

import com.ncubo.librerias.LRUCache;

public class CacheDeAudios
{
	private static LRUCache audiosLivianos;
	private static LRUCache audiosPesados;
	private static int limiteTamanioAudiosLivianos;
	
	private CacheDeAudios(int capacidadAudiosLivianos, int capacidadAudiosPesados, int tamanioAudiosLivianos)
	{
		if ( audiosLivianos == null && audiosPesados == null)
		{
			audiosLivianos = new LRUCache(capacidadAudiosLivianos);
			audiosPesados = new LRUCache(capacidadAudiosPesados);
			limiteTamanioAudiosLivianos = tamanioAudiosLivianos;
		}
	}
	
	public static void inicializar(int capacidadAudiosLivianos, int capacidadAudiosPesados, int limiteTamanioAudiosLivianos)
	{
		if ( audiosLivianos == null && audiosPesados == null)
		{
			new CacheDeAudios(capacidadAudiosLivianos, capacidadAudiosPesados, limiteTamanioAudiosLivianos);
		}
	}
	
	public static byte[] obtener(String key)
	{
		byte[] bytesStreamPorDevolver = audiosLivianos.obtener(key);
		if (bytesStreamPorDevolver == null)
		{
			bytesStreamPorDevolver = audiosPesados.obtener(key);
		}
		return bytesStreamPorDevolver;
	}
	
	public static void agregar(String key, byte[] valor)
	{
		if ( valor.length < limiteTamanioAudiosLivianos )
		{
			audiosLivianos.agregar(key, valor);
		}
		else
		{
			audiosPesados.agregar(key, valor);
		}
	}
}
