package com.ned.cassandra.async;

import com.ned.cassandra.model.KafkaMessageModel;
import com.ned.log.interfaces.DataPipelineService;

import com.ned.log.model.LogModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaLogPipelineService implements DataPipelineService {

	private final KafkaProducerService kafkaProducerService;

	private String KAFKA_LOG_RECORD_TOPIC = "demo-log-record";

	public KafkaLogPipelineService(KafkaProducerService kafkaProducerService) {
		this.kafkaProducerService = kafkaProducerService;
	}

	@Override
	public boolean createLog(LogModel logModel) {
		String topic = KAFKA_LOG_RECORD_TOPIC;
		KafkaMessageModel<LogModel> kafkaMessage = new KafkaMessageModel<>();
		kafkaMessage.setTopic(topic);
		kafkaMessage.setPayload(logModel);

		log.info("Sending kafka message: {}", kafkaMessage);
		kafkaProducerService.sendMessage(kafkaMessage);
		return true;
	}
}
