package com.gm4c.senha;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


import com.gm4c.tef.Transferencia;
import com.google.gson.Gson;

@Service
public class SenhaService {

	@Autowired
	private final KafkaTemplate<String, Senha> kafkaSenha;


	public SenhaService(KafkaTemplate<String, Senha> k1)
	{
		this.kafkaSenha = k1;
	}
	
	@KafkaListener(topics="simulacao", groupId = "senha")
	public void validaLimite(ConsumerRecord<String, Transferencia> record)
	{
		Object t1 = record.value();
		Transferencia transferencia = new Gson().fromJson(t1.toString(), Transferencia.class);

		//verifica se for efetivação, não faz nada
		if (transferencia.getEvento().equalsIgnoreCase("efetivacao"))
		{
			return;
		}
		boolean aprovado = true;
		
		/** @TODO colocar a lógica para validar a senha **/
		
		//prepara o registro do avro sobre o retorno da senha
		Senha senha = Senha.newBuilder()
				.setAgencia(transferencia.getAgenciaOrigem())
				.setConta(transferencia.getContaOrigem())
				.setDv(transferencia.getDvOrigem())
				.setAprovado(aprovado)
				.build();
		
		
		//envia a respota da senha para o kafka no topico senha
		kafkaSenha.send("senha", senha);
		
	}

	
}
