package com.jiacheng.cassandra.repository;

import com.jiacheng.cassandra.model.Person;
import com.jiacheng.cassandra.vo.PersonVO;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends CassandraRepository<Person, UUID> {

	CompletableFuture<List<PersonVO>> findAllBy();
	CompletableFuture<List<PersonVO>> findByNameContaining(String name);
}
