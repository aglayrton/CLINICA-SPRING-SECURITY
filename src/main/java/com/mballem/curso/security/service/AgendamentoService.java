package com.mballem.curso.security.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mballem.curso.security.datatables.Datatables;
import com.mballem.curso.security.datatables.DatatablesColunas;
import com.mballem.curso.security.domain.Agendamento;
import com.mballem.curso.security.domain.Horario;
import com.mballem.curso.security.exception.AcessoNegado;
import com.mballem.curso.security.repository.AgendamentoRepository;
import com.mballem.curso.security.repository.projection.HistoricoPaciente;

@Service
public class AgendamentoService {
	@Autowired
	private AgendamentoRepository repository;
	
	@Autowired
	private Datatables datatables;
	
	@Transactional(readOnly = true)
	public List<Horario> buscarPorMedicos(Long id, LocalDate data) {
		return repository.findByMedicoIdAndDataNotHorarioAgendado(id, data);
	}
	
	@Transactional(readOnly = false)
	public void salvar(Agendamento agendamento) {
		repository.save(agendamento);
	}
	
	@Transactional(readOnly = true)
	public Map<String, Object> buscarHistoricoPorPacienteEmail(String email, HttpServletRequest request) {
		datatables.setRequest(request);
		datatables.setColunas(DatatablesColunas.AGENDAMENTOS);
		Page<HistoricoPaciente> page = repository.finHistoricoByPacienteEmail(email, datatables.getPageable());
		return datatables.getResponse(page);
	}
	
	@Transactional(readOnly = true)
	public Map<String, Object> buscarHistoricoPorMedicoEmail(String email, HttpServletRequest request) {
		datatables.setRequest(request);
		datatables.setColunas(DatatablesColunas.AGENDAMENTOS);
		Page<HistoricoPaciente> page = repository.finHistoricoByMedicoEmail(email, datatables.getPageable());
		return datatables.getResponse(page);
	}
	
	@Transactional(readOnly = true)
	public Agendamento buscarPorId(Long id) {
		return repository.findById(id).get();
	}
	
	@Transactional(readOnly = false)
	public void editar(Agendamento agendamento, String username) {
		//Agendamento ag = busc(agendamento.getId());
		Agendamento ag = buscarPorIdEUsuario(agendamento.getId(), username);
		ag.setDataConsulta(agendamento.getDataConsulta());
		ag.setEspecialidade(agendamento.getEspecialidade());
		ag.setHorario(agendamento.getHorario());
		ag.setMedico(agendamento.getMedico());
	}
	
	@Transactional(readOnly = true)
	public Agendamento buscarPorIdEUsuario(Long id, String username) {
		return repository
				.findByIdAndPacienteOrMedicoEmail(id, username)
				.orElseThrow(()-> new AcessoNegado("Acesso negado ao usuário: "+username));
	}
	
	@Transactional(readOnly = false)
	public void remove(Long id) {
		repository.deleteById(id);
	}
}
