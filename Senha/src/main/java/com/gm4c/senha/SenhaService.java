package com.gm4c.senha;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.gm4c.senha.dto.SenhaDto;
import com.gm4c.senha.dto.SenhaRepositorio;
import com.gm4c.tef.Transferencia;
import com.google.gson.Gson;

@Service
public class SenhaService {

	@Autowired
	SenhaRepositorio repSenha;

	@Autowired
	private final KafkaTemplate<String, Senha> kafkaSenha;


	public SenhaService(KafkaTemplate<String, Senha> k1)
	{
		this.kafkaSenha = k1;
	}
	
	@KafkaListener(topics="tef", groupId = "senha")
	public void validaSenha(ConsumerRecord<String, Transferencia> record)
	{
		Object t1 = record.value();
		Transferencia transferencia = new Gson().fromJson(t1.toString(), Transferencia.class);
	
		
		//verifica se for efetivacao, não faz nada
		if (transferencia.getEvento().equalsIgnoreCase("efetivacao"))
		{
			return;
		}
		boolean aprovado = false;
		
		SenhaDto senha=null;

		//verificando agencia conta e dv  (busca o registro pelos 3 campos>
		try
		{
//			Thread.sleep(400);

			senha = repSenha.pesquisaPorAgenciaConta(transferencia.getAgenciaOrigem(), transferencia.getContaOrigem(), transferencia.getDvOrigem()).get(0);
		}
		catch (Exception e)
		{
			aprovado = false;
		}
		
		
		if (senha!=null && senha.getSenha().equals(transferencia.getSenha()))
		{
			aprovado = true;
		}
		if (senha!=null)
		{
			System.out.println("senha payload " + transferencia.getSenha() + ", senha banco " + senha.getSenha());
		}
		//prepara o registro do avro sobre o retorno da senha
		Senha senhaResp = Senha.newBuilder()
				.setAgencia(transferencia.getAgenciaOrigem())
				.setConta(transferencia.getContaOrigem())
				.setDv(transferencia.getDvOrigem())
				.setAprovado(aprovado)
				.setEvento(transferencia.getEvento())
				.setIdSimulacao(transferencia.getIdTransacao())
				.build();
		
		
		//envia a respota da senha para o kafka no topico senha
		System.out.println("ZZZ999");
		kafkaSenha.send("senha", senhaResp);
		System.out.println("ZZZ998");
		
	}
}
