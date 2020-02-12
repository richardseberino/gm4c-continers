package com.gm4c.senha.dto;

import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import com.datastax.driver.core.DataType.Name;

@Table("senha")
public class SenhaDto {

	@Column @PrimaryKey @CassandraType(type = Name.UUID) private String id_senha;
		
	@Column private int agencia;

	@Column private int conta;

	@Column private int dv;
		
	@Column private String senha;
		
	public int getAgencia() {
		return agencia;
	}

	public void setAgencia(int agencia) {
		this.agencia = agencia;
	}

	public int getConta() {
		return conta;
	}

	public void setConta(int conta) {
		this.conta = conta;
	}

	public int getDv() {
		return dv;
	}

	public void setDv(int dv) {
		this.dv = dv;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

}
