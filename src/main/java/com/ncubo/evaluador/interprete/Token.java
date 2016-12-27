package com.ncubo.evaluador.interprete;


public class Token 
{

	private TokenType type;
	private String valor;

	public static enum TokenType 
	{
		list,
		nulo,
		as,
		procedure,
		ELSE,
		IF,
		begin,
		end,
		comentarioDeLinea,
		exit, 
		boolTrue, 
		boolFalse, 
		monto,
		fecha, 
		mes,
		hora, 
		decimal, 
		numero, 
		igual,
		igualdad,
		desigualdad,
		suma, 
		resta,
		division, 
		coma, 
		punto, 
		puntoComa, 
		rParentesis, 
		lParentesis, 
		show, 
		hilera,
		mayor,
		menor,
		mayorIgual,
		menorIgual,
		asert,
		eof,
		wildcard,
		multiplicacion,
		eol,
		id;
		
		public String pattern;
		

		private TokenType(String pattern) 
		{
			this.pattern = pattern;
		}

		private TokenType() 
		{
			this.pattern = this.name();
		}
		
		@Override
		public String toString() 
		{
			return "Pattern: " + this.pattern ;
		}
	}

	public Token(TokenType type, String valor) 
	{
		super();
		this.type = type;

		if (this.type == TokenType.hilera)
		{
			this.valor = valor.substring(1, valor.length() - 1);
		} 
		else 
		{
			this.valor = valor;
		}
	}

	public TokenType getType()
	{
		return type;
	}

	public void setType(TokenType type) 
	{
		this.type = type;
	}

	public String getValor() 
	{
		return valor;
	}

	public void setValor(String signo) 
	{
		this.valor = signo;
	}

	@Override
	public String toString() 
	{
		return "Valor: " + valor + " Tipo: " + type.name();
	}
}
