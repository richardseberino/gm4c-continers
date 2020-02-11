package com.gm4c.tef.dto;

public class RequestSimulacaoTefDto {

	private int conta_origem;
	private int agencia_origem;
	private int dv_origem;

	private int conta_destino;
	private int agencia_destino;
	private int dv_destino;

	private String tipo_transacao;
	
	private float valor;
	
	private String senha;
	

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public float getValor() {
		return valor;
	}

	public void setValor(float valor) {
		this.valor = valor;
	}

	public int getConta_origem() {
		return conta_origem;
	}

	public void setConta_origem(int conta_origem) {
		this.conta_origem = conta_origem;
	}

	public int getAgencia_origem() {
		return agencia_origem;
	}

	public void setAgencia_origem(int agencia_origem) {
		this.agencia_origem = agencia_origem;
	}

	public int getDv_origem() {
		return dv_origem;
	}

	public void setDv_origem(int dv_origem) {
		this.dv_origem = dv_origem;
	}

	public int getConta_destino() {
		return conta_destino;
	}

	public void setConta_destino(int conta_destino) {
		this.conta_destino = conta_destino;
	}

	public int getAgencia_destino() {
		return agencia_destino;
	}

	public void setAgencia_destino(int agencia_destino) {
		this.agencia_destino = agencia_destino;
	}

	public int getDv_destino() {
		return dv_destino;
	}

	public void setDv_destino(int dv_destino) {
		this.dv_destino = dv_destino;
	}

	public String getTipo_transacao() {
		return tipo_transacao;
	}

	public void setTipo_transacao(String tipo_transacao) {
		this.tipo_transacao = tipo_transacao;
	}

	
	
}
