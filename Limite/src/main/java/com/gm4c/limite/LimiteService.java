package com.gm4c.limite;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.gm4c.limite.dto.LimiteDto;
import com.gm4c.limite.dto.LimiteRepositorio;
import com.gm4c.tef.Transferencia;
import com.google.gson.Gson;

@Service
public class LimiteService {
	
	//Malvaro
	@Autowired
	LimiteRepositorio replimite;
	// 
	
	@Autowired
	private final KafkaTemplate<String, Limite> kafkaLimite;// = new KafkaProducer<String, Simulacao>(propriedades);
	
	public LimiteService(KafkaTemplate<String, Limite> k1)
	{
		this.kafkaLimite = k1;
	}
	
	@KafkaListener(topics="tef", groupId = "limite")
	public void validaLimite(ConsumerRecord<String, Transferencia> record)
	{
		Object t1 = record.value();
		Transferencia transferencia = new Gson().fromJson(t1.toString(), Transferencia.class);
		
		boolean aprovado = true;

		LimiteDto dadosLimite = null;

		try 
		{
			dadosLimite = replimite.pesquisaLimite(transferencia.getAgenciaOrigem(), transferencia.getContaOrigem(), transferencia.getDvOrigem()).get(0); 
			if (transferencia.getEvento().equalsIgnoreCase("efetivacao"))
			{
				dadosLimite.setValor_utilizado(dadosLimite.getValor_utilizado()+transferencia.getValor());
				replimite.save(dadosLimite);
			}
			else //simulacao
			{
				if  ((dadosLimite.getValor_limite()- dadosLimite.getValor_utilizado())>=transferencia.getValor())
				{
					aprovado = true;
				} else
				{
					aprovado = false;
				}
			}

		}
		catch (Exception e)
		{ 
			aprovado = false;
		}

		
		//prepara o registro do avro sobre o retorno do limite
		Limite limite = Limite.newBuilder()
				.setAgencia(transferencia.getAgenciaOrigem())
				.setConta(transferencia.getContaOrigem())
				.setDv(transferencia.getDvOrigem())
				.setValor(transferencia.getValor())
				.setIdSimulacao(transferencia.getIdTransacao())
				.setAprovado(aprovado)
				.build();
		
		//envia a respota do limite para o kafka no topico limite
		kafkaLimite.send("limite", limite);
		
	}
		
}