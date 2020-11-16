package com.mballem.curso.security.domain;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity
@Table(name = "perfis")
public class Perfil extends AbstractEntity {
	
	//===CONSTRUTORES JÁ SALVAM O ID QUANDO CHAMADO A CLASSE
	public Perfil() {
		super();
	}

	public Perfil(Long id) {
		super.setId(id);
	}
	
	//SALVA A DESCRIÇÃO QUE VAI SER DADA PELO ENUM DE PerfilTipo
	@Column(name = "descricao", nullable = false, unique = true)
	private String desc;
	
	
	
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
