package com.gm4c.limite;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


import com.gm4c.tef.Transferencia;
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
	public void validaLimite(ConsumerRecord<String, Transferencia> record)
	{
		Object t1 = record.value();
		Transferencia transferencia = new Gson().fromJson(t1.toString(), Transferencia.class);
		
		
		
		if (transferencia.getEvento().equalsIgnoreCase("efetivacao"))
		{
			/** @TODO colocar a inteligencia para atualizar o limote **/
		}
		else
		{
			/** @TODO colocar a lógica para vlidar o limite **/
		}
		
		boolean aprovado = true;
		
		
		
		//prepara o registro do avro sobre o retorno do limite
		Limite limite = Limite.newBuilder()
				.setAgencia(transferencia.getAgenciaOrigem())
				.setConta(transferencia.getContaOrigem())
				.setDv(transferencia.getDvOrigem())
				.setValor(transferencia.getValor())
				.setAprovado(aprovado)
				.build();
		

		
		//envia a respota do limite para o kafka no topico limite
		kafkaLimite.send("limite", limite);
		
	}
		
}
