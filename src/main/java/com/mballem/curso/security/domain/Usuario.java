package com.mballem.curso.security.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("serial")
@Entity //segundo parametro serve para mapear algum indice que tenha no banco de dados
@Table(name = "usuarios", indexes = {@Index(name = "idx_usuario_email", columnList = "email")})//esse indice é para trabalharmos com consulta, que será feita pelo id ou email
public class Usuario extends AbstractEntity {	
	
	//============Construtores para a coluna ID do AbstractyEntity======================//
	public Usuario() {
		super();
	}

	public Usuario(Long id) {
		super.setId(id);
	}
	
	public Usuario(String email) {
		this.email = email;
	}
	//=======================CAMPOS DA TABELA=============================================//
	@Column(name = "email", unique = true, nullable = false)
	private String email;
	
	@JsonIgnore
	@Column(name = "senha", nullable = false)
	private String senha;
	
	@Column(name = "ativo", nullable = false, columnDefinition = "TINYINT(1)")
	private boolean ativo;
	
	@Column(name = "codigo_verificador", length = 6)
	private String codigoVerificador;
	
	//====================TABELA CRIADA A PARTIR DE DUAS COLUNAS DAS TABELAS==================//
	@ManyToMany
	@JoinTable(
		name = "usuarios_tem_perfis", 
        joinColumns = { @JoinColumn(name = "usuario_id", referencedColumnName = "id") }, 
        inverseJoinColumns = { @JoinColumn(name = "perfil_id", referencedColumnName = "id") }
	)
	private List<Perfil> perfis;
	
	//======================= adiciona perfis a lista===========================================//
	public void addPerfil(PerfilTipo tipo) {//Objeto que recebe o long e a descriçã (codigo + nome)(1, ADMIN)
		if (this.perfis == null) {//verifica se a lista de perfis está nula
			this.perfis = new ArrayList<>();//se estiver vai instaciar o objeto como uma lista de array
		}
		this.perfis.add(new Perfil(tipo.getCod()));//depois vai adicionar ao array o id pelo construtor
		//adciona o codigo ao perfil do usuario, ou seja, usuario_id é o id do abstractyEntity e o codigo
		//de nivel de acesso está sendo adicionado, que pode ser mais de 1 para este usuario.
	}

	//============================GETTERS E SETTERS===============================================//
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public List<Perfil> getPerfis() {
		return perfis;
	}

	public void setPerfis(List<Perfil> perfis) {
		this.perfis = perfis;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}	
	
	public String getCodigoVerificador() {
		return codigoVerificador;
	}

	public void setCodigoVerificador(String codigoVerificador) {
		this.codigoVerificador = codigoVerificador;
	}

}
