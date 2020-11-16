package com.mballem.curso.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mballem.curso.security.domain.Paciente;
import com.mballem.curso.security.repository.PacienteRepository;

@Service
public class PacienteService {
	
	@Autowired
	private PacienteRepository repository;

	@Transactional(readOnly = true)
	public Paciente buscarPorUsuarioEmail(String username) {
		return repository.findByUsuarioEmail(username).orElse(new Paciente());
	}

	@Transactional(readOnly = false)
	public void salvar(Paciente paciente) {
		repository.save(paciente);
	}
	
	@Transactional(readOnly = false)
	public void editar(Paciente paciente) {
		Paciente p = repository.findById(paciente.getId()).get();
		p.setNome(paciente.getNome());
		p.setDtNascimento(paciente.getDtNascimento());
		repository.save(p);
	}
	
	
	
}
