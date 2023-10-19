package com.jiacheng.cassandra.repository;

import com.jiacheng.cassandra.entity.LogRecord;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface LogRecordRepository extends CassandraRepository<LogRecord, String> {

}
