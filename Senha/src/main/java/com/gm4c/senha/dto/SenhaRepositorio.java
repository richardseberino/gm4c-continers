package com.gm4c.senha.dto;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;


public interface SenhaRepositorio extends CassandraRepository<SenhaDto, String>
{

	@Query("SELECT id_senha, agencia,conta, dv, senha from Senha where agencia = :agencia and conta = :conta and dv = :dv ALLOW FILTERING")
	List<SenhaDto> pesquisaPorAgenciaConta(@Param("agencia") int agencia, @Param("conta") int conta, @Param("dv") int dv);
}
