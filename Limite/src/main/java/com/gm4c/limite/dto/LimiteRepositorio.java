package com.gm4c.limite.dto;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LimiteRepositorio extends CassandraRepository<LimiteDto, String>{
	@Query("SELECT id_limite, agencia, conta, dv, valor_limite, valor_utilizado from limite where agencia = :agencia and conta = :conta and dv = :dv ALLOW FILTERING")
	List<LimiteDto> pesquisaLimite(@Param("agencia") int agencia, @Param("conta") int conta, @Param("dv") int dv);
}
