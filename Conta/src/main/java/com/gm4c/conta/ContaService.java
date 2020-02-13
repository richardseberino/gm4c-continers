package com.gm4c.conta;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.gm4c.conta.dto.ContaCorrenteDto;
import com.gm4c.conta.dto.ContaRepositorio;
import com.gm4c.tef.Transferencia;
import com.google.gson.Gson;

@Service
public class ContaService {

	@Autowired
	ContaRepositorio repConta;
	
	@Autowired
	private final KafkaTemplate<String, ContaCorrente> kafkaConta;


	public ContaService(KafkaTemplate<String, ContaCorrente> k1)
	{
		this.kafkaConta = k1;
	}
	
	@KafkaListener(topics="tef", groupId = "conta")
	public void validaLimite(ConsumerRecord<String, Transferencia> record)
	{
		Object t1 = record.value();
		Transferencia transferencia = new Gson().fromJson(t1.toString(), Transferencia.class);

		
		boolean aprovadoOrigem = true;
		boolean aprovadoDestino = true;
		String razaoOrigem = "[0] Conta Origem verificada e saldo suficiente";
		String razaoDestino = "[0] Conta Destino verificada";
		
		System.out.println("transferencia " + transferencia);
		ContaCorrenteDto contaDestino=null;
		ContaCorrenteDto contaOrigem=null;

		//verificando conta origem
		try
		{
			contaOrigem = repConta.pesquisaPorAgenciaConta(transferencia.getAgenciaOrigem(), transferencia.getContaOrigem(), transferencia.getDvOrigem()).get(0);
		}
		catch (Exception e)
		{
			aprovadoOrigem = false;
		}
		//verificando conta destino
		try
		{
			contaDestino = repConta.pesquisaPorAgenciaConta(transferencia.getAgenciaDestino(), transferencia.getContaDestino(), transferencia.getDvDestino()).get(0);
			
		}
		catch (Exception e)
		{
			aprovadoDestino = false;
		}

		if (transferencia.getEvento().equalsIgnoreCase("efetivacao"))
		{
			if (contaOrigem!=null && contaDestino!=null)
			{
				contaOrigem.setValor_saldo(contaOrigem.getValor_saldo()-transferencia.getValor());
				contaDestino.setValor_saldo(contaDestino.getValor_saldo()+transferencia.getValor());
				repConta.save(contaOrigem);
				repConta.save(contaDestino);
				razaoOrigem = "[0] Debito realizado com sucesso na conta origem!";
				razaoDestino = "[0] Credito realizado com sucesso na conta destino";
				
			}
		}
		else //simulacao
		{
			
			if (contaOrigem ==null) 
			{
				aprovadoOrigem=false;
				razaoOrigem = "[-10] Conta Origem nao existe";
			}
			else if (contaOrigem.getBloqueio()==1)
			{
				aprovadoOrigem=false;
				razaoOrigem = "[-11] Conta Origem bloqueada";
			}
			else if (contaOrigem.getValor_saldo()<transferencia.getValor())
			{
				aprovadoOrigem=false;
				razaoOrigem = "[-12] Conta Origem bloqueada";
			}
			
			if (contaDestino == null) 
			{
				aprovadoDestino=false;
				razaoDestino = "[-10] Conta Destino nao existe";
			}
			else if (contaDestino.getBloqueio()==1 )
			{
				aprovadoDestino=false;
				razaoDestino = "[-11] Conta Destino bloqueada";
			}

		}

		
		//prepara o registro do avro sobre o retorno das contas
		ContaCorrente conta = ContaCorrente.newBuilder()
				.setAgenciaOrigem(transferencia.getAgenciaOrigem())
				.setContaOrigem(transferencia.getContaOrigem())
				.setDvOrigem(transferencia.getDvOrigem())
				.setAprovacaoContaOrigem(aprovadoOrigem)
				.setMotivoContaOrigem(razaoOrigem)
				.setAgenciaDestino(transferencia.getAgenciaDestino())
				.setContaDestino(transferencia.getContaDestino())
				.setDvDestino(transferencia.getDvDestino())
				.setAprovacaoContaDestino(aprovadoDestino)
				.setMotivoContaDestino(razaoDestino)
				.setIdSimulacao(transferencia.getIdTransacao())
				.setEvento(transferencia.getEvento())
				.build();
		
		//envia a respota do limite para o kafka no topico conta
		kafkaConta.send("conta", conta);
		
	}

}
