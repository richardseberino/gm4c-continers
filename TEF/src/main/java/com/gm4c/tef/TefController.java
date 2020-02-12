package com.gm4c.tef;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gm4c.conta.ContaCorrente;
import com.gm4c.limite.Limite;
import com.gm4c.senha.Senha;
import com.gm4c.tef.dto.RequestSimulacaoTefDto;
import com.gm4c.tef.dto.ResultadoSimulacaoTefDto;
import com.google.gson.Gson;

import io.confluent.kafka.serializers.KafkaAvroSerializer;

@RestController
@RequestMapping("/tef")
public class TefController {

	@Autowired
	private final KafkaTemplate<String, Transferencia> kafkaSimulacao;// = new KafkaProducer<String, Simulacao>(propriedades);
	
	public TefController(KafkaTemplate<String, Transferencia> kafka)
	{
		this.kafkaSimulacao = kafka;
	}
	
	
	
	@PostMapping("/simulacao")
	public ResponseEntity<ResultadoSimulacaoTefDto> simulaTransferencia(@RequestBody RequestSimulacaoTefDto simulacao, HttpServletRequest request)
	{
		/** @TODO Adicionar regra para gerar o ID da transacao de forma automatica para cada simulacao  */
		String idTrandacao = "AAAAAAA-AAAAAA-AAAAA";

		//Define o topico onde o evento de simulacao será gravado
		String topico = "simulacao";
		
		//cria o objeto do Avro com a mensagem do evento de simulacao
		Transferencia simulaAvro = Transferencia.newBuilder()
				.setEvento("simulacao")
				.setAgenciaOrigem(simulacao.getAgencia_origem())
				.setContaOrigem(simulacao.getConta_origem())
				.setDvOrigem(simulacao.getDv_origem())
				.setAgenciaDestino(simulacao.getAgencia_destino())
				.setContaDestino(simulacao.getConta_destino())
				.setDvDestino(simulacao.getDv_destino())
				.setValor(simulacao.getValor())
				.setTipoTransacao(simulacao.getTipo_transacao())
				.setSenha(simulacao.getSenha())
				.setIdTranscao(idTrandacao)
				.build();
				
		//envia a mensagem ao kafka
		kafkaSimulacao.send(topico, simulaAvro);
		
		
		/** @TODO implementar a logica para aguardar o resultado da senha, conta e limite para preprar o resultado **/
		
		//Prepara resultado
		ResultadoSimulacaoTefDto resultado = new ResultadoSimulacaoTefDto();
		resultado.setAgencia_destino(simulacao.getAgencia_destino());
		resultado.setConta_destino(simulacao.getConta_destino());
		resultado.setDv_destino(simulacao.getDv_destino());
		resultado.setAgencia_origem(simulacao.getAgencia_origem());
		resultado.setConta_origem(simulacao.getConta_origem());
		resultado.setDv_origem(simulacao.getDv_origem());
		resultado.setTipo_transacao(simulacao.getTipo_transacao());
		resultado.setValor(simulacao.getValor());
		resultado.setId_transacao(idTrandacao);
		
		return ResponseEntity.ok(resultado);
	}
	
	@KafkaListener(topics="limite", groupId = "simulacao")
	public void validaLimite(ConsumerRecord<String, Limite> record)
	{
		Object t1 = record.value();
		Limite limite = new Gson().fromJson(t1.toString(), Limite.class);
		
		if (limite.getAprovado())
		{
			System.out.println("Limite aprovado!");
		}
		else
		{
			System.out.println("Limite insuficiente");
		}
	}

	
	@KafkaListener(topics="conta", groupId = "simulacao")
	public void validaConta(ConsumerRecord<String, ContaCorrente> record)
	{
		Object t1 = record.value();
		ContaCorrente conta = new Gson().fromJson(t1.toString(), ContaCorrente.class);
		
		if (conta.getAprovacaoContaOrigem())
		{
			System.out.println("Conta Origem aprovada!");
		}
		else
		{
			System.out.println("Conta Origem reprovada, razao: " + conta.getMotivoContaOrigem());
		}
		if (conta.getAprovacaoContaDestino())
		{
			System.out.println("Conta Destino aprovada!");
		}
		else
		{
			System.out.println("Conta Destino reprovada, razao: " + conta.getMotivoContaDestino());
		}
	}

	
	@KafkaListener(topics="senha", groupId = "simulacao")
	public void validaSenha(ConsumerRecord<String, Senha> record)
	{
		Object t1 = record.value();
		Senha senha = new Gson().fromJson(t1.toString(), Senha.class);
		
		if (senha.getAprovado())
		{
			System.out.println("Senha Validada!");
		}
		else
		{
			System.out.println("Senha incorreta");
		}
	}
	
	
}
