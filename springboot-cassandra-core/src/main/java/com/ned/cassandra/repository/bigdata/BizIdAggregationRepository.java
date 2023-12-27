package com.ned.cassandra.repository.bigdata;

import com.ned.cassandra.entity.bigdata.BizIdAggregationKey;
import com.ned.cassandra.entity.bigdata.BizIdAggregationModel;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

public interface BizIdAggregationRepository extends
		CassandraRepository<BizIdAggregationModel, BizIdAggregationKey> {

	@Query("SELECT * FROM biz_id_aggregation WHERE operate_day >= ?0 AND operate_day <= ?1 ALLOW FILTERING")
	List<BizIdAggregationModel> findByKeyOperateDayBetween(LocalDate start, LocalDate end);
}
