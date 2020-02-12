package com.gm4c.limite;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.gm4c.tef.Simulacao;
import com.google.gson.Gson;

@Service
public class LimiteService {

	@Autowired
	private final KafkaTemplate<String, Limite> kafkaLimite;// = new KafkaProducer<String, Simulacao>(propriedades);


	public LimiteService(KafkaTemplate<String, Limite> k1)
	{
		this.kafkaLimite = k1;
	}
	
	@KafkaListener(topics="simulacao", groupId = "limite")
	public void validaLimite(ConsumerRecord<String, Simulacao> record)
	{
		Object t1 = record.value();
		Simulacao simulacao = new Gson().fromJson(t1.toString(), Simulacao.class);

		//prepara o registro do avro sobre o retorno do limite
		Limite limite = Limite.newBuilder()
				.setAgencia(simulacao.getAgenciaOrigem())
				.setConta(simulacao.getContaOrigem())
				.setDv(simulacao.getDvOrigem())
				.setValor(simulacao.getValor())
				.setAprovado(true)
				.build();
		
		//envia a respota do limite para o kafka no topico limite
		kafkaLimite.send("limite", limite);
		
	}
		
}
