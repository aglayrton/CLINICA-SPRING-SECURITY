package com.mballem.curso.security.web.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	// abrir pagina home
	@GetMapping({"/", "/home"})
	public String home() {
		return "home";
	}	
	
	//acesso a página de login
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	//login invalido
	@GetMapping("/login-error")
	public String loginError(ModelMap model) {
		model.addAttribute("alerta", "erro");
		model.addAttribute("titulo", "Credenciais inválidas");
		model.addAttribute("texto","Login e senha incorretos, tente novamente");
		model.addAttribute("subtexto", "Acesso permitido apenas para cadastros ativados");
		return "login";
	}
	
	//acesso negado
		@GetMapping("/acesso-negado")
		public String acessoNegado(ModelMap model, HttpServletResponse response) {
			model.addAttribute("status", response.getStatus());
			model.addAttribute("error", "Acesso negado");
			model.addAttribute("message","Você não tem permissão para acesso ou ação");
			return "error";
		}
}
