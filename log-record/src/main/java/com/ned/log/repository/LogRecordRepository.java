package com.ned.log.repository;

import com.ned.log.entity.LogRecord;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRecordRepository extends CassandraRepository<LogRecord, String> {

}
