package com.gm4c.limite.dto;

import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import com.datastax.driver.core.DataType.Name;

@Table("limite")
public class LimiteDto {

	@Column @PrimaryKey @CassandraType(type = Name.UUID) private String id_limite;
	
	@Column private int agencia, conta, dv;
	@Column private double valor_limite, valor_utilizado;
	
	public String getId_limite() {
		return id_limite;
	}
	public void setId_limite(String id_limite) {
		this.id_limite = id_limite;
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
	public double getValor_limite() {
		return valor_limite;
	}
	public void setValor_limite(double valor_limite) {
		this.valor_limite = valor_limite;
	}
	public double getValor_utilizado() {
		return valor_utilizado;
	}
	public void setValor_utilizado(double valor_utilizado) {
		this.valor_utilizado = valor_utilizado;
	}
	
}
