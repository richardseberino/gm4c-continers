package com.gm4c.tef;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gm4c.tef.dto.RequestSimulacaoTefDto;
import com.gm4c.tef.dto.ResultadoSimulacaoTefDto;

import io.confluent.kafka.serializers.KafkaAvroSerializer;

@RestController
@RequestMapping("/tef")
public class TefController {

	@Autowired
	private final KafkaTemplate<String, Simulacao> kafkaSimulacao;// = new KafkaProducer<String, Simulacao>(propriedades);
	
	public TefController(KafkaTemplate<String, Simulacao> kafka)
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
		Simulacao simulaAvro = Simulacao.newBuilder()
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
		
		//produz o registro do avro para ser enviado ao Kafka
		ProducerRecord<String, Simulacao> prodRecord = new ProducerRecord<String, Simulacao>(topico, simulaAvro);
		
		//envia a mensagem ao kafka
		kafkaSimulacao.send(prodRecord);
		
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
	
}
