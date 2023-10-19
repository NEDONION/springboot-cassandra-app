package com.ned.cassandra.async;

import com.ned.cassandra.model.KafkaMessageModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducerService {


	private final KafkaTemplate<String, Object> kafkaTemplate;

	public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public <T> void sendMessage(KafkaMessageModel<T> kafkaMessage) {
		String topic = kafkaMessage.getTopic();
		T payload = kafkaMessage.getPayload();
		log.info("Sending message to topic: {}, payload: {}", topic, payload);
		kafkaTemplate.send(topic, payload);
	}
}
