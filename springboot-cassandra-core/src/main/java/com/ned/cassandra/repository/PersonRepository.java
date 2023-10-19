package com.ned.cassandra.repository;

import com.ned.cassandra.entity.Person;
import com.ned.cassandra.vo.PersonVO;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends CassandraRepository<Person, UUID> {

	CompletableFuture<List<PersonVO>> findAllBy();

	CompletableFuture<List<PersonVO>> findByNameContaining(String name);

	@AllowFiltering
	List<Person> findByAgeBetween(Integer minAge, Integer maxAge);

	@Query("SELECT * FROM person WHERE tutorialIds CONTAINS ?0 ALLOW FILTERING")
	List<Person> findByTutorialId(UUID tutorialId);

}
