package com.mballem.curso.security.domain;

public enum PerfilTipo {
	
	//quando chamada, já coloca os dados nos campos cod e desc 
	
	ADMIN(1, "ADMIN"), MEDICO(2, "MEDICO"), PACIENTE(3, "PACIENTE");
	
	private long cod;
	private String desc;

	private PerfilTipo(long cod, String desc) {
		this.cod = cod;
		this.desc = desc;
	}

	public long getCod() {
		return cod;
	}

	public String getDesc() {
		return desc;
	}
}
