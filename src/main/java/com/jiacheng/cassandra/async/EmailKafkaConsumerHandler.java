package com.jiacheng.cassandra.async;

import com.jiacheng.cassandra.entity.Person;
import com.jiacheng.cassandra.entity.Tutorial;
import com.jiacheng.cassandra.model.EmailAsyncRequestModel;
import com.jiacheng.cassandra.service.EmailService;
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

	@KafkaListener(topics = "demo-cassandra-email", groupId = "demo-cassandra-email-consumer")
	public void consumeKafkaMessage(EmailAsyncRequestModel request) {
		log.info("Received kafka message: {}", request.toString());
		sendEmail(request.getPerson(), request.getTutorial());
	}

	private void sendEmail(Person person, Tutorial tutorial) {
		emailService.sendEmailNotification(person, tutorial);
	}
}
