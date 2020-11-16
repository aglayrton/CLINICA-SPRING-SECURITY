package com.mballem.curso.security.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mballem.curso.security.domain.Medico;
import com.mballem.curso.security.domain.Usuario;
import com.mballem.curso.security.service.MedicoService;
import com.mballem.curso.security.service.UsuarioService;

@Controller
@RequestMapping("/medicos")
public class MedicoController {
	
	@Autowired
	private MedicoService service;
	@Autowired
	private UsuarioService usuarioService;
	
	//abre a pagina de dados pessoas de medicos pelo MEDICO
	@GetMapping("/dados")
	public String abrirPorMedico(Medico medico, ModelMap model,
			@AuthenticationPrincipal User user) {
		//
		if(medico.hasNotId()) {
			medico = service.buscarPorEmail(user.getUsername());
			model.addAttribute("medico", medico);
		}
		return "medico/cadastro";
	}
	
	@PostMapping("/salvar")
	public String salvar(Medico medico, RedirectAttributes attr,
			@AuthenticationPrincipal User user 
	) {
		if(medico.hasNotId() && medico.getUsuario().hasNotId()) {
			Usuario usuario = usuarioService.buscarPorEmail(user.getUsername());
			medico.setUsuario(usuario);
		}
			
		service.salvar(medico);
		attr.addFlashAttribute("sucesso", "Operação realizada com sucesso");
		attr.addFlashAttribute("medico", medico);
		return "redirect:/medicos/dados";
	}
	
	@PostMapping({"/editar"})
	public String editar(Medico medico, RedirectAttributes attr) {
		service.editar(medico);
		attr.addFlashAttribute("sucesso", "Operação realizada com sucesso");
		attr.addFlashAttribute("medico", medico);
		return "redirect:/medicos/dados";
	}
	
	//excluir especialidades
	@GetMapping({"/id/{idMed}/excluir/especializacao/{idEsp}"})
	public String excluirEspecialidadePorMedico(@PathVariable("idMed") Long idMed, @PathVariable("idEsp") Long idEsp, RedirectAttributes attr) {
		
		if (service.existeEspecialidadeAgendada(idMed, idEsp)) {
			attr.addFlashAttribute("falha", "Tem consultas agendadas, exclusão negada!");
		} else {
			service.deletarPorMedico(idMed, idEsp);
			attr.addFlashAttribute("sucesso", "Operação realizada com sucesso");
		}

		return "redirect:/medicos/dados";
	}
	
	//buscar medicos por especialidades via ajax
	@GetMapping("/especialidade/titulo/{titulo}")
	public ResponseEntity<?> getMedicosPorEspecialidades(@PathVariable("titulo") String titulo) {
		return ResponseEntity.ok(service.buscarMedicoPorEspecialidade(titulo));
	}
}
