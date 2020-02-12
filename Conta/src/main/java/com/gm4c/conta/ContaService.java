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
	ContaRepositorio rep;
	
	@Autowired
	private final KafkaTemplate<String, ContaCorrente> kafkaConta;


	public ContaService(KafkaTemplate<String, ContaCorrente> k1)
	{
		this.kafkaConta = k1;
	}
	
	@KafkaListener(topics="simulacao", groupId = "conta")
	public void validaLimite(ConsumerRecord<String, Transferencia> record)
	{
		Object t1 = record.value();
		Transferencia transferencia = new Gson().fromJson(t1.toString(), Transferencia.class);

		
		boolean aprovadoOrigem = true;
		boolean aprovadoDestino = true;
		
		System.out.println("transferencia " + transferencia);
		ContaCorrenteDto contaDestino=null;
		ContaCorrenteDto contaOrigem=null;

		//verificando conta origem
		try
		{
			contaOrigem = rep.pesquisaPorAgenciaConta(transferencia.getAgenciaOrigem(), transferencia.getContaOrigem(), transferencia.getDvOrigem()).get(0);
		}
		catch (Exception e)
		{
			aprovadoOrigem = false;
		}
		//verificando conta destino
		try
		{
			contaDestino = rep.pesquisaPorAgenciaConta(transferencia.getAgenciaDestino(), transferencia.getContaDestino(), transferencia.getDvDestino()).get(0);
			
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
				rep.save(contaOrigem);
				rep.save(contaDestino);
				
			}
		}
		else //simulacao
		{
			
			if (contaOrigem ==null || contaOrigem.getBloqueio()==1 || contaOrigem.getValor_saldo()< transferencia.getValor())
			{
				aprovadoOrigem=false;
				
			}
			
			if (contaDestino == null || contaDestino.getBloqueio()==1 )
			{
				aprovadoDestino=false;
				
			}

		}

		
		//prepara o registro do avro sobre o retorno das contas
		ContaCorrente conta = ContaCorrente.newBuilder()
				.setAgenciaOrigem(transferencia.getAgenciaOrigem())
				.setContaOrigem(transferencia.getContaOrigem())
				.setDvOrigem(transferencia.getDvOrigem())
				.setAprovacaoContaOrigem(aprovadoOrigem)
				.setMotivoContaOrigem("0")
				.setAgenciaDestino(transferencia.getAgenciaDestino())
				.setContaDestino(transferencia.getContaDestino())
				.setDvDestino(transferencia.getDvDestino())
				.setAprovacaoContaDestino(aprovadoDestino)
				.setMotivoContaDestino("0")
				.build();
		
		//envia a respota do limite para o kafka no topico conta
		kafkaConta.send("conta", conta);
		
	}

}
