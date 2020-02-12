package com.gm4c.conta.dto;

import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import com.datastax.driver.core.DataType.Name;

@Table("conta")
public class ContaCorrenteDto {

	
   @Column @PrimaryKey @CassandraType(type = Name.UUID) private String id_conta;
	
	@Column private int agencia;

	@Column private int conta;

	@Column private int dv;
	
	@Column private double valor_saldo;
	
	@Column private int bloqueio;
	
	

	public int getBloqueio() {
		return bloqueio;
	}

	public void setBloqueio(int bloqueio) {
		this.bloqueio = bloqueio;
	}

	public String getId_conta() {
		return id_conta;
	}

	public void setId_conta(String id_conta) {
		this.id_conta = id_conta;
	}

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

	public double getValor_saldo() {
		return valor_saldo;
	}

	public void setValor_saldo(double valor_saldo) {
		this.valor_saldo = valor_saldo;
	}
	
	
}
