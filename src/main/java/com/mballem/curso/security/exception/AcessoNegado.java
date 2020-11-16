package com.mballem.curso.security.exception;

public class AcessoNegado extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AcessoNegado(String message) {
		super(message);
	}
	
}
