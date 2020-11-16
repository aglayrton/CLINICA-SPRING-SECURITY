package com.mballem.curso.security.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mballem.curso.security.domain.Paciente;
import com.mballem.curso.security.domain.Usuario;
import com.mballem.curso.security.service.PacienteService;
import com.mballem.curso.security.service.UsuarioService;

@Controller
@RequestMapping("pacientes")
public class PacientesController {
	
	@Autowired
	private PacienteService service;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@GetMapping("/dados")
	public String cadastrar(Paciente paciente, ModelMap model, @AuthenticationPrincipal User user) {
		//pegando a sessão
		paciente = service.buscarPorUsuarioEmail(user.getUsername());
		
		//verifica se tem ou nao o id
		if(paciente.hasNotId()) {//caso não tenha será feito um insert
			paciente.setUsuario(new Usuario(user.getUsername()));//envia para pagina o email 
		}
		
		model.addAttribute("paciente", paciente);
		
		return "paciente/cadastro";
	}
	
	@PostMapping("/salvar")
	public String salvar(Paciente paciente, ModelMap model, @AuthenticationPrincipal User user) {
		
		//pegando a sessão
		Usuario u = usuarioService.buscarPorEmail(user.getUsername());
		
		//Caso a senha que vem do paciente seja igual a senha armazenada na sessão, entra no if
		if(UsuarioService.isSenhaCorreta(paciente.getUsuario().getSenha(), u.getSenha())) {
			paciente.setUsuario(u); //vincula o usuario ao paciente
			service.salvar(paciente);
			model.addAttribute("sucesso", "Operação bem sucedida");
		}else {
			model.addAttribute("falha", "sua senha não confere");
		}
		
		return "paciente/cadastro";
	}
	
	@PostMapping("/editar")
	public String editar(Paciente paciente, ModelMap model, @AuthenticationPrincipal User user) {
		
		Usuario u = usuarioService.buscarPorEmail(user.getUsername());
		
		if(UsuarioService.isSenhaCorreta(paciente.getUsuario().getSenha(), u.getSenha())) {
			service.editar(paciente);
			model.addAttribute("sucesso", "Alteração bem sucedida");
		}else {
			model.addAttribute("falha", "sua senha não confere");
		}
		
		return "paciente/cadastro";
	}
}
