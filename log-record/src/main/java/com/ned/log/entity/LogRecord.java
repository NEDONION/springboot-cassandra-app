package com.ned.log.entity;

import java.util.Date;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("log_record")
@Data
@NoArgsConstructor
@Builder
public class LogRecord {

	@PrimaryKey
	private String id;

	@Column("biz_id")
	private String bizId;

	@Column("exception")
	private String exception;

	@Column("operate_date")
	private Date operateDate;

	@Column("is_success")
	private Boolean isSuccess;

	@Column("msg")
	private String msg;

	@Column("execute_result")
	private String executeResult;

	@Column("execution_time")
	private Long executionTime;

	public LogRecord(String id, String bizId, String exception, Date operateDate, Boolean isSuccess, String msg,
			String executeResult, Long executionTime) {
		this.id = id;
		this.bizId = bizId;
		this.exception = exception;
		this.operateDate = operateDate;
		this.isSuccess = isSuccess;
		this.msg = msg;
		this.executeResult = executeResult;
		this.executionTime = executionTime;
	}

}
