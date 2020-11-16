package com.mballem.curso.security.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mballem.curso.security.domain.Agendamento;
import com.mballem.curso.security.domain.Horario;
import com.mballem.curso.security.repository.projection.HistoricoPaciente;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
	
	//localizando os horarios nao agendadados, então vamos utilizar o subselect 
	//Vamos buscar os horarios pelo id do medico, data de consulta e quando o id 
	//na tabela de horario confere com o id na tabela de agendamento.
	//temos que retornar as que não são verdadeiras, pois ela nao existe na tabela e por isso
	//significa que nao foi feito o agendameto sobre aquele horario
	@Query("select h " 
			+ "from Horario h "
			+ "where not exists(" //onde nao exista esses horarios que nao existe 
				+ "select a.horario.id "
					+ "from Agendamento a " 
					+ "where "
						+ "a.medico.id = :id and " //verifica o id que passamos seja igual a quem tem na tabela do campo medico 
						+ "a.dataConsulta = :data and " //verifica se a data é igual a data campo
						+ "a.horario.id = h.id" //
						+ ") "
						+"order by h.horaMinuto asc"
	)
	List<Horario> findByMedicoIdAndDataNotHorarioAgendado(Long id, LocalDate data);
	
	// a query faz uma projeção de dados quando usa o "alias" ou seja o "as"
	@Query("select a.id as id, "
				+"a.paciente as paciente, "
				+"CONCAT(a.dataConsulta, ' ', a.horario.horaMinuto) as dataConsulta, "
				+"a.medico as medico, "
				+"a.especialidade as especialidade "
			+ "from  Agendamento a "
			+ "where a.paciente.usuario.email like :email"
			)
	Page<HistoricoPaciente> finHistoricoByPacienteEmail(String email, Pageable pageable);
	//acima vamos retorna um objeto do tipo HistoricoPaciente
	
	
	@Query("select a.id as id, "
			+"a.paciente as paciente, "
			+"CONCAT(a.dataConsulta, ' ', a.horario.horaMinuto) as dataConsulta, "
			+"a.medico as medico, "
			+"a.especialidade as especialidade "
		+ "from  Agendamento a "
		+ "where a.medico.usuario.email like :email"
		)
	Page<HistoricoPaciente> finHistoricoByMedicoEmail(String email, Pageable pageable);
	
	//acesso pelo id e usuario
	@Query(
			"select a from Agendamento a where (a.id = :id and a.paciente.usuario.email like :username)"
			+" or (a.id = :id and a.medico.usuario.email like :username)"
	)
	Optional<Agendamento> findByIdAndPacienteOrMedicoEmail(Long id, String username);
	
}
