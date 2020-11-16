package com.mballem.curso.security.web.controller;


import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mballem.curso.security.domain.Especialidade;
import com.mballem.curso.security.service.EspecialidadeService;

@Controller
@RequestMapping("especialidades")
public class EspecialidadeController {

	@Autowired
	private EspecialidadeService service;
	
	@GetMapping({"", "/"})
	public String abrir(Especialidade especialidade) {
		return "especialidade/especialidade";
	}
	
	@PostMapping("/salvar")
	public String salvar(Especialidade especialidade, RedirectAttributes attr) {
		service.salvar(especialidade);
		attr.addFlashAttribute("sucesso", "Operação realizada com sucesso");
		return "redirect:/especialidades";
	}
	
	@GetMapping("/datatables/server")
	public ResponseEntity<?> getEspecialidades(HttpServletRequest request) {
		return ResponseEntity.ok(service.buscarEspecialidades(request));
	}
	
	@GetMapping("/datatables/server/medico/{id}")
	public ResponseEntity<?> getEspecialidadesPorMedico(@PathVariable("id") Long id, HttpServletRequest request) {
		return ResponseEntity.ok(service.buscarEspecialidadesPorMedico(id, request));
	}
	
	@GetMapping("/titulo")
	public ResponseEntity<?> getEspecialidadesPorTermo(@RequestParam("termo") String termo) {
		List<String> especialidades = service.buscarEspecialidadeByTermo(termo);
		return ResponseEntity.ok(especialidades);
	}
	
	@GetMapping("/editar/{id}")
	public String preEditar(@PathVariable("id") Long id, ModelMap model) {
		model.addAttribute("especialidade", service.buscarPorId(id));
		return "especialidade/especialidade";
	}
	
	@GetMapping("/exlcuir/{id}")
	public String preEditar(@PathVariable("id") Long id, RedirectAttributes attr) {
		service.deletarPorId(id);
		attr.addFlashAttribute("sucesso", "Operação realizada com sucesso");
		return "redirect:/especialidades";
	}
}
