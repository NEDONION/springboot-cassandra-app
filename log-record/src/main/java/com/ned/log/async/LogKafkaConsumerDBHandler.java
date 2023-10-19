package com.ned.log.async;

import com.ned.log.entity.LogRecord;
import com.ned.log.model.LogModel;
import com.ned.log.repository.LogRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Component
public class LogKafkaConsumerDBHandler {

	private final LogRecordRepository logRecordRepository;

	public LogKafkaConsumerDBHandler(LogRecordRepository logRecordRepository) {
		this.logRecordRepository = logRecordRepository;
	}

	@KafkaListener(topics = "${kafka.log.record.topic}", groupId = "${kafka.log.record.consumer.group.id}")
	public void consumeKafkaMessage(LogModel logModel) {
		log.info("Received kafka message: {}", logModel.toString());
		LogRecord logRecord = convertToLogRecord(logModel);
		saveLogRecord(logRecord);
	}

	private void saveLogRecord(LogRecord logRecord) {
		logRecordRepository.save(logRecord);
		log.info("Saved log record: {}", logRecord.toString());
	}

	private LogRecord convertToLogRecord(LogModel logModel) {
		return LogRecord.builder()
				.id(logModel.getLogId())
				.bizId(logModel.getBizId())
				.exception(logModel.getException())
				.operateDate(logModel.getOperateDate())
				.isSuccess(logModel.getIsSuccess())
				.msg(logModel.getMsg())
				.executeResult(logModel.getExecuteResult())
				.executionTime(logModel.getExecutionTime())
				.build();
	}
}
