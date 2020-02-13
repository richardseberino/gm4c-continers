package com.gm4c.tef;


import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.kafka.clients.consumer.ConsumerRecord;
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
		if (!simulacao.getRc_efetivacao().startsWith("[2]") || !simulacao.getRc_simulacao().startsWith("[0]"))
		{
			return ResponseEntity.status(403).build();
		}
		
		simulacao.setEvento("efetivacao");
		simulacao.setRc_credito("[2] pendente");
		simulacao.setRc_debito("[2] pendente");
		simulacao.setRc_efetivacao("[2] enviada");
		simulacao.setRc_limite("[2] pendente");
		simulacao.setRc_senha("[2] pendente");

		repTef.save(simulacao);
		
		Date inicio = new Date();
		Date agora = new Date();
		try
		{
			while (!verificaEtapa(id_simulacao, "efetivacao")) 
			{
				agora = new Date();
				if (agora.getTime()-inicio.getTime()>10000)
				{
						throw new Exception("[-3] timeout!");
				}
				Thread.sleep(500);
			}
			List<TefDto> list = repTef.findByTransactionid(id_simulacao);
			if (list.isEmpty())
			{
				throw new Exception("[-4] Falha ao atualizar a simulacao");
			}
			simulacao = list.get(0);
			
			if (!simulacao.getRc_debito().startsWith("[0]"))
			{
				throw new Exception("[-5] falha ao fazer o debito. " + simulacao.getRc_debito());
			}
			if (!simulacao.getRc_credito().startsWith("[0]"))
			{
				throw new Exception("[-6] falha ao fazer o credito. " + simulacao.getRc_credito());
			}
			if (!simulacao.getRc_limite().startsWith("[0]"))
			{
				throw new Exception("[-7] falha ao atualizar o limite. "  + simulacao.getRc_limite());
			}
			simulacao.setRc_efetivacao("[0] Efetivação concluida");

		}
		catch (Exception e)
		{
			simulacao.setRc_simulacao(e.getMessage());
			simulacao.setMsg_simulacao("Timeout, transacao demorou para receber retorno do limmite e conta");
			
		}
		
		repTef.save(simulacao);
		
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
				.setIdTransacao(simulacao.getId_tef())
				.build(); 
		
		//envia a mensagem ao kafka
		kafkaSimulacao.send(topico, efetivaAvro);
		
		ResultadoSimulacaoTefDto resultado = new ResultadoSimulacaoTefDto();
		resultado.setAgencia_destino(simulacao.getAgencia_destino());
		resultado.setConta_destino(simulacao.getConta_destino());
		resultado.setDv_destino(simulacao.getDv_destino());
		resultado.setAgencia_origem(simulacao.getAgencia_origem());
		resultado.setConta_origem(simulacao.getConta_origem());
		resultado.setDv_origem(simulacao.getDv_origem());
		resultado.setTipo_transacao(simulacao.getTipo());
		resultado.setValor(simulacao.getValor());
		resultado.setId_transacao(id_simulacao);
		resultado.setResultado(simulacao.getRc_simulacao());
		
		return ResponseEntity.ok(resultado);

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
				.setIdTransacao(idTransacao)
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
		sim.setTransactionid(idTransacao);
		sim.setValor(simulacao.getValor());
		sim.setTimestamp(new Timestamp(System.currentTimeMillis()));
		
		repTef.insert(sim);
		
		//envia a mensagem ao kafka
		kafkaSimulacao.send(topico, simulaAvro);
		
		Date inicio = new Date();
		Date agora = new Date();
		try
		{
			while (!verificaEtapa(idTransacao, "simulacao")) 
			{
				agora = new Date();
				if (agora.getTime()-inicio.getTime()>10000)
				{
					throw new Exception("[-3] timeout!");
				}

				Thread.sleep(500);
			}
			List<TefDto> list = repTef.findByTransactionid(idTransacao);
			if (list.isEmpty())
			{
				throw new Exception("[-4] Falha ao atualizar a simulacao");
			}
			sim = list.get(0);
			
			if (!sim.getRc_debito().startsWith("[0]"))
			{
				throw new Exception("[-5] falha na consta origem. " + sim.getRc_debito());
			}
			if (!sim.getRc_credito().startsWith("[0]"))
			{
				throw new Exception("[-6] falha na consta destino. " + sim.getRc_credito());
			}
			if (!sim.getRc_limite().startsWith("[0]"))
			{
				throw new Exception("[-7] limite recusado. "  + sim.getRc_limite());
			}
			if (!sim.getRc_senha().startsWith("[0]"))
			{
				throw new Exception("[-8] senha invalida. "  + sim.getRc_senha());
			}
			sim.setRc_simulacao("[0] Simulacao concluida");

		}
		catch (Exception e)
		{
			sim.setRc_simulacao(e.getMessage());
			sim.setMsg_simulacao("Timeout, transacao demorou para receber retorno de senha,limmite e conta");
			sim = repTef.findByTransactionid(idTransacao).get(0);
		}
		
		repTef.save(sim);
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
		resultado.setResultado(sim.getRc_simulacao());
		
		return ResponseEntity.ok(resultado);
	}
	
	/**
	 * metodo resposnavel por verificar se uma etapa (efetivacao ou simulacao) já foi concluida
	 * @param idSimulacao id da simulacao
	 * @param evento tipo de evento (simulacao ou efetivacao)
	 * @return true quando a etapa estiver concluida e false quando nao
	 */
	private boolean verificaEtapa(String idSimulacao, String evento)
	{

		List<TefDto> lista = repTef.findByTransactionid(idSimulacao);
		
		if (lista.isEmpty())
		{
			return false;
		}
		
		TefDto ev = lista.get(0);
		
		if (evento.equalsIgnoreCase("simulacao"))
		{
			if (ev.getRc_credito().startsWith("[2]") || ev.getRc_debito().startsWith("[2]") || ev.getRc_limite().startsWith("[2]") || ev.getRc_senha().startsWith("[2]"))
			{
				return false;
			}
		} 
		else // efetivaao
		{
			if (ev.getRc_credito().startsWith("[2]") || ev.getRc_debito().startsWith("[2]") || ev.getRc_limite().startsWith("[2]") || ev.getRc_senha().startsWith("[2]"))
			{
				return false;
			}
			
		}
		
		return true;
	}
	
	@KafkaListener(topics="limite", groupId = "tef")
	public void validaLimite(ConsumerRecord<String, Limite> record)
	{
		Object t1 = record.value();
		Limite limite = new Gson().fromJson(t1.toString(), Limite.class);
		
		java.util.List<TefDto> lista= repTef.findByTransactionid(limite.getIdSimulacao());
		
		//verifica se existe uma simulacao em andamento com esse id
		if (lista.size()<=0)
		{
			return;
		}
		TefDto simulacao = lista.get(0);
		
		
		
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
		System.out.println("RESPOSTA LIMITE " + simulacao.getRc_limite());
		repTef.save(simulacao);
	}

	
	@KafkaListener(topics="conta", groupId = "tef")
	public void validaConta(ConsumerRecord<String, ContaCorrente> record)
	{
		Object t1 = record.value();
		ContaCorrente conta = new Gson().fromJson(t1.toString(), ContaCorrente.class);
		
		java.util.List<TefDto> lista= repTef.findByTransactionid(conta.getIdSimulacao());
		
		//verifica se existe uma simulacao em andamento com esse id
		if (lista.size()<=0)
		{
			return;
		}
		TefDto simulacao = lista.get(0);
		
		//atualiza o retorno de contas na simulacao / efetivacao
		
		simulacao.setMsg_debito(conta.getMotivoContaOrigem());
		simulacao.setRc_debito(conta.getMotivoContaOrigem());
		simulacao.setMsg_credito(conta.getMotivoContaDestino());
		simulacao.setRc_credito(conta.getMotivoContaDestino());
		System.out.println("RESPOSTA CONTA " + simulacao.getRc_credito() + ", " + simulacao.getRc_debito());
		repTef.save(simulacao);
	}

	
	@KafkaListener(topics="senha", groupId = "tef")
	public void validaSenha(ConsumerRecord<String, Senha> record)
	{
		Object t1 = record.value();
		Senha senha = new Gson().fromJson(t1.toString(), Senha.class);
		
		java.util.List<TefDto> lista= repTef.findByTransactionid(senha.getIdSimulacao());
		
		//verifica se existe uma simulacao em andamento com esse id
		if (lista.size()<=0)
		{
			return;
		}
		TefDto simulacao = lista.get(0);
		
		if (senha.getAprovado())
		{
			simulacao.setMsg_senha("Senha correta");
			simulacao.setRc_senha("[0] Senha Correta");
		}
		else
		{
			simulacao.setMsg_senha("Senha inválida");
			simulacao.setRc_senha("[-1] Senha invalida");
		}
		System.out.println("RESPOSTA SENHA " + simulacao.getRc_senha());
		repTef.save(simulacao);
	}
	
}
