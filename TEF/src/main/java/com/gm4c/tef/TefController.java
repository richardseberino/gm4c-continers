package com.gm4c.tef;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gm4c.conta.ContaCorrente;
import com.gm4c.limite.Limite;
import com.gm4c.senha.Senha;
import com.gm4c.tef.dto.RequestSimulacaoTefDto;
import com.gm4c.tef.dto.ResultadoSimulacaoTefDto;
import com.gm4c.tef.dto.TefDto;
import com.gm4c.tef.dto.TefRepositorio;
import com.google.gson.Gson;

import io.confluent.kafka.serializers.KafkaAvroSerializer;

@RestController
@RequestMapping("/tef")
public class TefController {

	@Autowired
	private final KafkaTemplate<String, Transferencia> kafkaSimulacao;// = new KafkaProducer<String, Simulacao>(propriedades);
	
	@Autowired
	private TefRepositorio repTef;
	
	public TefController(KafkaTemplate<String, Transferencia> kafka)
	{
		this.kafkaSimulacao = kafka;
	}
	
	@PatchMapping("/efetiva")
	public ResponseEntity<ResultadoSimulacaoTefDto> efetivaTransferencia(@RequestBody String id_simulacao)
	{
		//define o topico kafka
		String topico = "tef";
		
		//Recupera os dados da simulacao
		
		Optional<TefDto> t1= repTef.findById(id_simulacao); 
		
		//verifica se existe uma simulacao feita com esse id
		if (!t1.isPresent())
		{
			return ResponseEntity.notFound().build();
		}
		
		TefDto simulacao = t1.get();
		
		//cria o objeto do Avro com a mensagem do evento de efetiva ao
		Transferencia efetivaAvro = Transferencia.newBuilder()
				.setEvento("efetivacao")
				.setAgenciaOrigem(simulacao.getAgencia_origem())
				.setContaOrigem(simulacao.getConta_origem())
				.setDvOrigem(simulacao.getDv_origem())
				.setAgenciaDestino(simulacao.getAgencia_destino())
				.setContaDestino(simulacao.getConta_destino())
				.setDvDestino(simulacao.getDv_destino())
				.setValor(simulacao.getValor())
				.setTipoTransacao(simulacao.getId_tef())
				.setSenha(simulacao.getSenha())
				.setIdTranscao(simulacao.getId_tef())
				.build(); 
		
		//envia a mensagem ao kafka
		kafkaSimulacao.send(topico, efetivaAvro);
		
		return null;
	}
	
	
	@PostMapping("/simulacao")
	public ResponseEntity<ResultadoSimulacaoTefDto> simulaTransferencia(@RequestBody RequestSimulacaoTefDto simulacao, HttpServletRequest request)
	{
		
		String idTransacao = UUID.randomUUID().toString();

		//Define o topico onde o evento de simulacao será gravado
		String topico = "tef";
		
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
				.setIdTranscao(idTransacao)
				.build();
				
		//armazena os dados da simulacao na base do cassandra para posterior efetivacao
		TefDto sim = new TefDto();
		sim.setAgencia_destino(simulacao.getAgencia_destino());
		sim.setAgencia_origem(simulacao.getAgencia_origem());
		sim.setConta_destino(simulacao.getConta_destino());
		sim.setConta_origem(simulacao.getConta_origem());
		sim.setDv_destino(simulacao.getDv_destino());
		sim.setDv_origem(simulacao.getDv_origem());
		sim.setEvento("simulacao");
		sim.setId_tef(idTransacao);
		sim.setMsg_credito("pendente");
		sim.setMsg_debito("pendente");
		sim.setMsg_efetivacao("pendente");
		sim.setMsg_limite("pendente");
		sim.setMsg_senha("pendente");
		sim.setMsg_simulacao("enviada");
		sim.setRc_credito("[2] pendente");
		sim.setRc_debito("[2] pendente");
		sim.setRc_efetivacao("[2] pendente");
		sim.setRc_limite("[2] pendente");
		sim.setRc_senha("[2] pendente");
		sim.setRc_simulacao("[10] enviada");
		sim.setSenha(simulacao.getSenha());
		sim.setTipo(simulacao.getTipo_transacao());
		sim.setTransacionid(idTransacao);
		sim.setValor(simulacao.getValor());
		sim.setTimestamp(new Timestamp(System.currentTimeMillis()));
		
		repTef.insert(sim);
		
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
		resultado.setId_transacao(idTransacao);
		
		return ResponseEntity.ok(resultado);
	}
	
	@KafkaListener(topics="limite", groupId = "tef")
	public void validaLimite(ConsumerRecord<String, Limite> record)
	{
		Object t1 = record.value();
		Limite limite = new Gson().fromJson(t1.toString(), Limite.class);
		
		Optional<TefDto> op= repTef.findById(limite.getIdSimulacao());
		
		//verifica se existe uma simulacao em andamento com esse id
		if (!op.isPresent())
		{
			return;
		}
		TefDto simulacao = op.get();
		
		
		if (limite.getAprovado())
		{
			simulacao.setMsg_limite("Limite aprovado");
			simulacao.setRc_limite("[0] limite aprovado");
		}
		else
		{
			simulacao.setMsg_limite("Limite insuficiente");
			simulacao.setRc_limite("[-1] limite insuficiente");
		}
	}

	
	@KafkaListener(topics="conta", groupId = "tef")
	public void validaConta(ConsumerRecord<String, ContaCorrente> record)
	{
		Object t1 = record.value();
		ContaCorrente conta = new Gson().fromJson(t1.toString(), ContaCorrente.class);
		
		Optional<TefDto> op= repTef.findById(conta.getIdSimulacao());
		
		//verifica se existe uma simulacao em andamento com esse id
		if (!op.isPresent())
		{
			return;
		}
		
		
		//atualiza o retorno de contas na simulacao / efetivacao
		TefDto simulacao = op.get();
		simulacao.setMsg_debito(conta.getMotivoContaOrigem());
		simulacao.setRc_debito(conta.getMotivoContaOrigem());
		simulacao.setMsg_credito(conta.getMotivoContaDestino());
		simulacao.setRc_credito(conta.getMotivoContaDestino());
		
		repTef.save(simulacao);
	}

	
	@KafkaListener(topics="senha", groupId = "tef")
	public void validaSenha(ConsumerRecord<String, Senha> record)
	{
		Object t1 = record.value();
		Senha senha = new Gson().fromJson(t1.toString(), Senha.class);
		
		Optional<TefDto> op= repTef.findById(senha.getIdSimulacao()); 
		
		//verifica se existe uma simulacao feita com esse id
		if (!op.isPresent())
		{
			return;
		}
		
		TefDto simulacao = op.get();
		if (senha.getAprovado())
		{
			simulacao.setMsg_senha("Senha correta");
			simulacao.setRc_senha("[0]");
		}
		else
		{
			simulacao.setMsg_senha("Senha inválida");
			simulacao.setRc_senha("[-1]");
		}
		repTef.save(simulacao);
		
	}
	
	
}
