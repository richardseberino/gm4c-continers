package com.gm4c.tef.dto;


import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;



public interface TefRepositorio extends CassandraRepository<TefDto, String> 
{
	@Query("SELECT * from Tef where transactionid = :transactionid ALLOW FILTERING")
	List<TefDto> findByTransactionid(@Param("transactionid") String transactionid);
}
