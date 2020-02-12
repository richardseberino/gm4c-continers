package com.gm4c.conta.dto;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ContaRepositorio extends CassandraRepository<ContaCorrenteDto, String>
{

	@Query("SELECT id_conta, agencia,conta, dv, valor_saldo, bloqueio from Conta where agencia = :agencia and conta = :conta and dv = :dv ALLOW FILTERING")
	List<ContaCorrenteDto> pesquisaPorAgenciaConta(@Param("agencia") int agencia, @Param("conta") int conta, @Param("dv") int dv);
}
