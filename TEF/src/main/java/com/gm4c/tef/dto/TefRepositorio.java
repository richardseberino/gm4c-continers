package com.gm4c.tef.dto;


import org.springframework.data.cassandra.repository.CassandraRepository;

public interface TefRepositorio extends CassandraRepository<TefDto, String> 
{

}
