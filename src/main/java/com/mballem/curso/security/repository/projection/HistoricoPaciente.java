package com.mballem.curso.security.repository.projection;

import com.mballem.curso.security.domain.Especialidade;
import com.mballem.curso.security.domain.Medico;
import com.mballem.curso.security.domain.Paciente;

public interface HistoricoPaciente {
	
	Long getId();
	
	Paciente getPaciente();
	
	String getDataConsulta();
	
	Medico getMedico();
	
	Especialidade getEspecialidade();
}
