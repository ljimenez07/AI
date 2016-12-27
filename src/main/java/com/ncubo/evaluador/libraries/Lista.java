package com.ncubo.evaluador.libraries;

import java.util.ArrayList;

import com.ncubo.evaluador.interprete.libraries.LanguageException;

public class Lista extends Objeto
{
	private ArrayList<Objeto> lista;

	public Lista() 
	{
		lista = new ArrayList<Objeto>();
	}

	public int size()
	{
		return lista.size();
	}

	public ArrayList<Objeto> getLista() 
	{
		return lista;
	}

	public Objeto getObjeto(int puntero) 
	{
		return lista.get(puntero);
	}

	public boolean contains(Objeto objeto) 
	{
		if (lista.contains(objeto))
		{
			return true;
		} 
		else
		{
			return false;
		}
	}
	public void remover(Objeto objeto) 
	{
		lista.remove(objeto);
	}

	public void guardarObjeto(Objeto objeto) 
	{
		lista.add(objeto);
	}

	public void guardarLista(ArrayList<Objeto> lista)
	{
		lista.addAll(lista);	
	}
	
	public void guardarTodo(Lista objeto)
	{
		for (Objeto lista : objeto.getLista())
		{
			this.lista.add(lista);
		}
	}
	public void clear() 
	{
		lista.clear();
	}

	public boolean isEmpty()
	{		
		if(lista.isEmpty())
		{
			return true;
		}
		else if(lista.size() == 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public Hilera demeElTipoObjetoAlQuePertenece()
	{
		Hilera valor = new Hilera(lista.get(0).getClass().getSimpleName());
		return valor;
	}

	@Override
	public Objeto sumar( Objeto objeto )
	{
		Lista miLista =  new Lista();
		miLista.guardarLista( lista );

		boolean elObjetoDelParametroEsInstanciaDeLista = objeto instanceof Lista;
		if ( ! elObjetoDelParametroEsInstanciaDeLista ) 
		{
			throw new LanguageException(
					String.format("No se puede sumar un %s a un %s, ya que ambos de tipos diferentes.", miLista.demeElTipoObjetoAlQuePertenece(), objeto.getClass().getSimpleName())
					);
		}

		Lista nuevaLista = new Lista();
		nuevaLista.guardarTodo((Lista) objeto);

		boolean noEsElMismoTipoDeObjeto = ! miLista.demeElTipoObjetoAlQuePertenece().esIgual(nuevaLista.demeElTipoObjetoAlQuePertenece());
		if( noEsElMismoTipoDeObjeto )
		{
			throw new LanguageException(
					String.format("No se puede sumar un %s a un %s, ya que ambos de tipos diferentes.", miLista.demeElTipoObjetoAlQuePertenece(), nuevaLista.demeElTipoObjetoAlQuePertenece())
					);
		}
		miLista.guardarTodo( nuevaLista );
		return miLista;
	}

	@Override
	public Objeto restar(Objeto objeto)  
	{
		Lista miLista = new Lista();
		miLista.guardarLista( lista );

		boolean elObjetoDelParametroEsInstanciaDeLista = objeto instanceof Lista;
		if ( elObjetoDelParametroEsInstanciaDeLista ) 
		{
			throw new LanguageException(
					String.format("No se puede restar un %s a un %s, ya que ambos de tipos diferentes.", miLista.demeElTipoObjetoAlQuePertenece(), objeto.getClass().getSimpleName())
					);
		}
		Lista nuevaLista = new Lista();
		nuevaLista.guardarTodo((Lista) objeto);

		boolean noEsElMismoTipoDeObjeto = ! miLista.demeElTipoObjetoAlQuePertenece().esIgual(nuevaLista.demeElTipoObjetoAlQuePertenece());
		if( noEsElMismoTipoDeObjeto )
		{
			throw new LanguageException(
					String.format("No se puede restar un %s a un '%2', ya que ambos de tipos diferentes.", miLista.demeElTipoObjetoAlQuePertenece(), nuevaLista.demeElTipoObjetoAlQuePertenece())
					);
		}

		Lista copiaDeMiLista = new Lista();
		copiaDeMiLista.guardarTodo( miLista );
		for(Objeto listaRestar : nuevaLista.getLista())
		{
			for(Objeto lista : copiaDeMiLista.getLista())
			{
				if(lista.equals( listaRestar ))
				{
					miLista.remover( listaRestar );
				}
			}
		}
		return miLista;
	}

	@Override
	public String show() 
	{
		String salida = "{\""+ this.getClass().getSimpleName()+"\":[";
		for (int i = 0; i < lista.size(); i++) 
		{
			Objeto obj = lista.get(i);
			if (obj != null)
			{
				salida += obj.show();
			}
			if(i+1 < lista.size())
			{
				salida+=",";
			}
		}
		salida += "]}";
		return salida;
	}
}
