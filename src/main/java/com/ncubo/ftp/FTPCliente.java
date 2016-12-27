package com.ncubo.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import com.ncubo.chatbot.watson.TextToSpeechWatson;

public class FTPCliente
{
	private String usuario;
	private String password;
	private String host;
	private int puerto;
	private String carpeta;

	public FTPCliente(String usuario, String contrasena, String host, int puerto, String carpeta){
		this.usuario = usuario;
		this.password = contrasena;
		this.host = host;
		this.puerto = puerto;
		this.carpeta = carpeta;
	}
	
	private boolean subirUnArchivo(FTPClient ftpClient, String localFilePath, String remoteFilePath) throws IOException
	{
		Path path = Paths.get(localFilePath);
		String finalPath = remoteFilePath.equals("") ? path.getFileName().toString() : remoteFilePath;

		InputStream inputStream = new FileInputStream(localFilePath);
		try
		{
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			return ftpClient.storeFile(finalPath, inputStream);
		} 
		finally
		{
			inputStream.close();
		}
	}
	
	private String subirDirectorio(FTPClient ftpClient, String remoteDirPath, String localParentDir, String remoteParentDir) throws IOException
	{
		String directorioFinalArchivo = "";
		File localDir = new File(localParentDir);
		File[] subFiles = localDir.listFiles();
		if (subFiles != null && subFiles.length > 0)
		{
			for (File item : subFiles)
			{
				String remoteFilePath = remoteDirPath + "/" + remoteParentDir + "/" + item.getName();
				if (remoteParentDir.equals(""))
				{
					remoteFilePath = remoteDirPath + "/" + item.getName();
				}

				if (item.isFile())
				{
					String extensionArchivo = FilenameUtils.getExtension(item.getAbsolutePath()); 
					if (extensionArchivo.equalsIgnoreCase("html"))
					{
						directorioFinalArchivo = remoteFilePath;
						
					}
					String localFilePath = item.getAbsolutePath();
					subirUnArchivo(ftpClient, localFilePath, remoteFilePath);
				}
				else
				{
					ftpClient.makeDirectory(remoteFilePath);
					String parent = remoteParentDir + "/" + item.getName();
					if (remoteParentDir.equals(""))
					{
						parent = item.getName();
					}

					localParentDir = item.getAbsolutePath();
					String stringSubirDirectorio = subirDirectorio(ftpClient, remoteDirPath, localParentDir, parent);
					
					if ( ! stringSubirDirectorio.equals(""))
					{
						directorioFinalArchivo = stringSubirDirectorio;
					}
				}
			}
		}
		
		return directorioFinalArchivo;
	}
	
	public String subirArchivo(String path) throws SocketException, IOException
	{
		String pathArchivoGuardado = "";
		FTPClient ftpClient = new FTPClient();
		ftpClient.connect(host, puerto);
		ftpClient.login(usuario, password);
		ftpClient.enterLocalPassiveMode();
		
		Path file = new File(path).toPath();
		
		ftpClient.changeWorkingDirectory(carpeta);
		int returnCode = ftpClient.getReplyCode();
		if (returnCode == 550)
		{
			ftpClient.makeDirectory(carpeta);
			ftpClient.changeWorkingDirectory(carpeta);
		}
		
		if(Files.isRegularFile(file))
		{
			subirUnArchivo(ftpClient, path, "");
			pathArchivoGuardado = file.getFileName().toString();
		}
		else
		{
			ftpClient.makeDirectory(file.getFileName().toString());
			pathArchivoGuardado = subirDirectorio(ftpClient, file.getFileName().toString(), path, "");
		}
		
		ftpClient.logout();
		ftpClient.disconnect();
		
		return pathArchivoGuardado.replaceAll("-", "_").replaceAll("/", "-");
	}
	
	public void subirUnArchivo(InputStream archivo, String pathDondeGuardar) throws IOException
	{
		FTPClient ftpClient = new FTPClient();
		ftpClient.connect(host, puerto);
		ftpClient.login(usuario, password);
		ftpClient.changeWorkingDirectory(carpeta);
		
		String[] directorio = pathDondeGuardar.split("/");
		String nombreArchivo = directorio[directorio.length-1];
		
		for(int i = 0 ; i < directorio.length - 1 ; i++)
		{
			ftpClient.changeWorkingDirectory(directorio[i]);
			int returnCode = ftpClient.getReplyCode();
			if (returnCode == 550)
			{
				ftpClient.makeDirectory(directorio[i]);
				ftpClient.changeWorkingDirectory(directorio[i]);
			}
		}
		
		try
		{
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			ftpClient.enterLocalPassiveMode();
			ftpClient.storeFile(nombreArchivo, archivo);
		}catch (Exception e){
			System.out.println("Error al transferir al FTP: "+e.getMessage());
			ftpClient.storeFile(nombreArchivo, archivo);
		}
		finally
		{
			archivo.close();
		}
	}
	
	public void subirUnArchivoPorHilo(InputStream archivo, String pathDondeGuardar)
	{
		HiloParaSubirArchivosAlFTP hilo = new HiloParaSubirArchivosAlFTP(archivo, pathDondeGuardar);
		hilo.start();
	}
	
	private class HiloParaSubirArchivosAlFTP extends Thread{
		
		private InputStream archivo;
		private String pathDondeGuardar;
		
		public HiloParaSubirArchivosAlFTP(InputStream archivo, String pathDondeGuardar){
			this.archivo = archivo;
			this.pathDondeGuardar = pathDondeGuardar;
		}
		
		public void run(){
			try
			{
				subirUnArchivo(archivo, pathDondeGuardar);
			}
			catch(Exception ex)
			{
				try
				{
					subirUnArchivo(archivo, pathDondeGuardar);
				}
				catch(Exception ex1)
				{
					ex1.printStackTrace();
				}
			}
			finally
			{
				try {
					archivo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Termin� de subir el archivo");
		}
	}
	
	public InputStream descargarArchivo(String nombreArchivo) throws SocketException, IOException
	{
		FTPClient ftpClient = new FTPClient();
		ftpClient.connect(host, puerto);
		ftpClient.login(usuario, password);
		ftpClient.enterLocalPassiveMode();
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		ftpClient.changeWorkingDirectory(carpeta);
		
		InputStream stream = ftpClient.retrieveFileStream(nombreArchivo);
		
		return stream;
	}
	
	public String getUsuario()
	{
		return usuario;
	}

	public void setUsuario(String usuario)
	{
		this.usuario = usuario;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getHost()
	{
		return host;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public int getPuerto()
	{
		return puerto;
	}

	public void setPuerto(int puerto)
	{
		this.puerto = puerto;
	}

}
