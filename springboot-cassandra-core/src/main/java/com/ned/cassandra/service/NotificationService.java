package com.ned.cassandra.service;

import com.ned.cassandra.async.KafkaProducerService;
import com.ned.cassandra.entity.Person;
import com.ned.cassandra.entity.Tutorial;
import com.ned.cassandra.model.EmailAsyncRequestModel;
import com.ned.cassandra.model.KafkaMessageModel;
import com.ned.cassandra.repository.PersonRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {


	private final PersonRepository personRepository;

	private final KafkaProducerService kafkaProducerService;

	@Value("${kafka.email.topic}")
	private String KAFKA_EMAIL_TOPIC;

	public NotificationService(PersonRepository personRepository,
			KafkaProducerService kafkaProducerService) {
		this.personRepository = personRepository;
		this.kafkaProducerService = kafkaProducerService;
	}

	public void notifyPersonsForTutorialUpdate(Tutorial updatedTutorial) {
		List<Person> relatedPersons = personRepository.findByTutorialId(updatedTutorial.getId());
		for (Person person : relatedPersons) {
			sendNotificationToKafkaProducer(person, updatedTutorial);
		}
	}

	private void sendNotificationToKafkaProducer(Person person, Tutorial tutorial) {
		String topic = KAFKA_EMAIL_TOPIC;
		EmailAsyncRequestModel emailAsyncRequestModel = new EmailAsyncRequestModel(person, tutorial);
		KafkaMessageModel<EmailAsyncRequestModel> kafkaMessage = new KafkaMessageModel<>();
		kafkaMessage.setTopic(topic);
		kafkaMessage.setPayload(emailAsyncRequestModel);

		log.info("Sending kafka message: {}", kafkaMessage);
		kafkaProducerService.sendMessage(kafkaMessage);
	}


}
