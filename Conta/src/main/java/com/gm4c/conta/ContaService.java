package com.gm4c.conta;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


import com.gm4c.tef.Transferencia;
import com.google.gson.Gson;

@Service
public class ContaService {

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
		
		/** @TODO colocar a lógica para validar a conta origem e destino **/
		
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
		
		if (transferencia.getEvento().equalsIgnoreCase("efetivacao"))
		{
			/** @TODO colocar a inteligencia para atualizar o limote **/
		}
		
		//envia a respota do limite para o kafka no topico conta
		kafkaConta.send("conta", conta);
		
	}

}
