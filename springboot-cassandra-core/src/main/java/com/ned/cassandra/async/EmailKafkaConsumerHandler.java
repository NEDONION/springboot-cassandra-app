package com.ned.cassandra.async;

import com.ned.cassandra.entity.Person;
import com.ned.cassandra.entity.Tutorial;
import com.ned.cassandra.model.EmailAsyncRequestModel;
import com.ned.cassandra.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class EmailKafkaConsumerHandler {

	private final EmailService emailService;

	public EmailKafkaConsumerHandler(EmailService emailService) {
		this.emailService = emailService;
	}

	// Dynamic config topics and groupId from application.properties
	@KafkaListener(topics = "${kafka.email.topic}", groupId = "${kafka.email.consumer.group.id}")
	public void consumeKafkaMessage(EmailAsyncRequestModel request) {
		log.info("Received kafka message: {}", request.toString());
		sendEmail(request.getPerson(), request.getTutorial());
	}

	private void sendEmail(Person person, Tutorial tutorial) {
		emailService.sendEmailNotification(person, tutorial);
	}
}
