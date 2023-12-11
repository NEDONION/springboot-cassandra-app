package com.ned.cassandra.repository;

import com.ned.cassandra.entity.Tutorial;
import java.util.List;
import java.util.UUID;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TutorialRepository extends CassandraRepository<Tutorial, UUID> {

	@AllowFiltering
	List<Tutorial> findByPublished(boolean published);

//	@AllowFiltering
	List<Tutorial> findByTitleContaining(String title);
}
