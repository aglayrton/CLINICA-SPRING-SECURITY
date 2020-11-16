package com.mballem.curso.security.web.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.mballem.curso.security.exception.AcessoNegado;

@ControllerAdvice
public class ExceptionController {
	
	//captura a exceção e trata
	@ExceptionHandler(UsernameNotFoundException.class)
	public ModelAndView usuarioNaoEncontradoException(UsernameNotFoundException ex) {
		ModelAndView model = new ModelAndView("error");
		model.addObject("status", 404);
		model.addObject("error", "Operação não pode ser realizada");
		model.addObject("message", ex.getMessage());
		return model;
	}
	
	//captura a exceção e trata
		@ExceptionHandler(AcessoNegado.class)
		public ModelAndView acessoNegado(AcessoNegado ex) {
			ModelAndView model = new ModelAndView("error");
			model.addObject("status", 403);
			model.addObject("error", "Operação não pode ser realizada");
			model.addObject("message", ex.getMessage());
			return model;
		}
}
