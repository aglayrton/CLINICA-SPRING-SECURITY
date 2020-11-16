package com.mballem.curso.security.web.controller;

import java.time.LocalDate;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mballem.curso.security.domain.Agendamento;
import com.mballem.curso.security.domain.Especialidade;
import com.mballem.curso.security.domain.Paciente;
import com.mballem.curso.security.domain.PerfilTipo;
import com.mballem.curso.security.service.AgendamentoService;
import com.mballem.curso.security.service.EspecialidadeService;
import com.mballem.curso.security.service.PacienteService;

@Controller
@RequestMapping("agendamentos")
public class AgendamentosController {
	
	@Autowired
	private AgendamentoService service;
	@Autowired
	private PacienteService pacienteService;
	@Autowired
	private EspecialidadeService especialidadeService;
	
	@PreAuthorize("hasAnyAuthority('PACIENTE', 'MEDICO')")
	@GetMapping({"/agendar"})
	public String agendar(Agendamento agendamento, ModelMap model) {
		model.addAttribute("agendamento", new Agendamento());
		return "agendamento/cadastro";
	}
	
	@PreAuthorize("hasAnyAuthority('PACIENTE', 'MEDICO')")
	@GetMapping("/horario/medico/{medico}/data/{data}")
	public ResponseEntity<?> getHorarios(
			@PathVariable("medico") Long id,
			@PathVariable("data") @DateTimeFormat(iso = ISO.DATE)LocalDate data
	) {
		return ResponseEntity.ok(service.buscarPorMedicos(id, data));	
	}
	
	@PreAuthorize("hasAuthority('PACIENTE')")
	@PostMapping({"/salvar"})
	public String salvar(Agendamento agendamento, RedirectAttributes attr, @AuthenticationPrincipal User user) {
		//captura do paciente que esta usando o sistema para fazer a consulta
		Paciente paciente = pacienteService.buscarPorUsuarioEmail(user.getUsername()); 
		//Pega somente o titulo e por ela pega a especialidade
		String titulo = agendamento.getEspecialidade().getTitulo();
		//consulta no banco para retorna a especialidade
		Especialidade especialidade = especialidadeService
				.buscarPorTitulos(new String[] {titulo}) //array de string passando a variavel titulo como valor
				.stream().findFirst()//procura o primeiro elemento da lista
				.get();//retorna o objeto optional especialidade
		agendamento.setEspecialidade(especialidade);
		agendamento.setPaciente(paciente);
		service.salvar(agendamento);
		attr.addFlashAttribute("sucesso", "Sua consulta foi agendada com sucesso");
		return "redirect:/agendamentos/agendar";
	}
	
	@PreAuthorize("hasAnyAuthority('PACIENTE', 'MEDICO')")
	@GetMapping({"/historico/paciente", "/historico/consultas"})
	public String historico() {
		return "agendamento/historico-paciente";
	}
	
	@PreAuthorize("hasAnyAuthority('PACIENTE', 'MEDICO')")
	//localiza o historico de agendamentos por usuario logado
	@GetMapping("/datatables/server/historico")
	public ResponseEntity<?> historicoAgendamentoPorPaciente(
			HttpServletRequest request,
			@AuthenticationPrincipal User user
	) {
		
		//veririfica no array se tem o tipo de perfil
		if(user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.PACIENTE.getDesc()))) {
			return ResponseEntity.ok(service.buscarHistoricoPorPacienteEmail(user.getUsername(), request));//
		}
		
		if(user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.MEDICO.getDesc()))) {
			return ResponseEntity.ok(service.buscarHistoricoPorMedicoEmail(user.getUsername(), request));//
		}
		
		return ResponseEntity.notFound().build();//
	}
	
	//Localizar agendamento pelo id e envia-lo para a pagina de cadastro
	@GetMapping("/editar/consulta/{id}")
	public String preEditarConsultaPaciente(
			@PathVariable("id") Long id,
			ModelMap model,
			@AuthenticationPrincipal User user
	) {
		Agendamento agendamento = service.buscarPorIdEUsuario(id, user.getUsername());
		model.addAttribute("agendamento", agendamento);
		return "agendamento/cadastro";
	}
	
	@PreAuthorize("hasAuthority('PACIENTE')")
	@PostMapping("/editar")
	public String preEditarConsultaPaciente(
			Agendamento agendamento, 
			RedirectAttributes attr,
			@AuthenticationPrincipal User user
	) {
		
		//pega o titulo que vem pelo objeto
		String titulo = agendamento.getEspecialidade().getTitulo();
		//passa o primeiro valor do titulo
		Especialidade especialidade = especialidadeService
				.buscarPorTitulos(new String[] {titulo}) 
				.stream().findFirst()
				.get();
		agendamento.setEspecialidade(especialidade);
		
		service.editar(agendamento, user.getUsername());
		attr.addFlashAttribute("sucesso", "Sua consulta foi alterada com sucesso.");
		return "redirect:/agendamentos/agendar";
	}
	
	@PreAuthorize("hasAuthority('PACIENTE')")
	@GetMapping("/excluir/consulta/{id}")
	public String excluir(@PathVariable("id") Long id, RedirectAttributes attr) {
		service.remove(id);
		attr.addFlashAttribute("sucesso", "Consulta removida com sucesso");
		return "redirect:/agendamentos/historico/paciente";
	}
	
	
	
	
}
