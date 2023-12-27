package com.ned.cassandra.repository.bigdata;

import com.ned.cassandra.entity.bigdata.ClientIpAggregationKey;
import com.ned.cassandra.entity.bigdata.ClientIpAggregationModel;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientIpAggregationRepository extends
		CassandraRepository<ClientIpAggregationModel, ClientIpAggregationKey> {

	@AllowFiltering
	List<ClientIpAggregationModel> findByKeyOperateDayBetween(LocalDate start, LocalDate end);
}
