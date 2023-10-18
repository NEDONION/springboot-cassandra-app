package com.jiacheng.cassandra.service;

import com.jiacheng.cassandra.async.KafkaProducerService;
import com.jiacheng.cassandra.entity.Person;
import com.jiacheng.cassandra.entity.Tutorial;
import com.jiacheng.cassandra.model.EmailAsyncRequestModel;
import com.jiacheng.cassandra.model.KafkaMessageModel;
import com.jiacheng.cassandra.repository.PersonRepository;
import java.util.List;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {


	private final PersonRepository personRepository;

	private final EmailService emailService;

	private final KafkaProducerService kafkaProducerService;

	public NotificationService(PersonRepository personRepository, EmailService emailService,
			KafkaProducerService kafkaProducerService) {
		this.personRepository = personRepository;
		this.emailService = emailService;
		this.kafkaProducerService = kafkaProducerService;
	}

	public void notifyPersonsForTutorialUpdate(Tutorial updatedTutorial) {
		List<Person> relatedPersons = personRepository.findByTutorialId(updatedTutorial.getId());
		for (Person person : relatedPersons) {
			sendNotificationToKafkaProducer(person, updatedTutorial);
		}
	}

	private void sendNotificationToKafkaProducer(Person person, Tutorial tutorial) {
		String topic = "demo-cassandra-email";
		EmailAsyncRequestModel emailAsyncRequestModel = new EmailAsyncRequestModel(person, tutorial);

		KafkaMessageModel<EmailAsyncRequestModel> kafkaMessage = new KafkaMessageModel<>();
		kafkaMessage.setTopic(topic);
		kafkaMessage.setPayload(emailAsyncRequestModel);

		log.info("Sending kafka message: {}", kafkaMessage);
		kafkaProducerService.sendMessage(kafkaMessage);
	}


}
