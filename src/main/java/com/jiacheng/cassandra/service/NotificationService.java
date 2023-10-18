package com.jiacheng.cassandra.service;

import com.jiacheng.cassandra.entity.Person;
import com.jiacheng.cassandra.entity.Tutorial;
import com.jiacheng.cassandra.model.EmailMessageModel;
import com.jiacheng.cassandra.repository.PersonRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {


	private final PersonRepository personRepository;

	private final EmailService emailService;

	public NotificationService(PersonRepository personRepository, EmailService emailService) {
		this.personRepository = personRepository;
		this.emailService = emailService;
	}

	public void notifyPersonsForTutorialUpdate(Tutorial updatedTutorial) {
		List<Person> relatedPersons = personRepository.findByTutorialId(updatedTutorial.getId());
		for (Person person : relatedPersons) {
			sendNotification(person, updatedTutorial);
		}
	}

	private void sendNotification(Person person, Tutorial tutorial) {
		EmailMessageModel emailMessageModel = new EmailMessageModel();

		String[] sendToPersonEmails = new String[10];
		sendToPersonEmails[0] = person.getEmail();

		emailMessageModel.setSubject("Tutorial Updated");
		emailMessageModel.setSendToPersonEmails(sendToPersonEmails);;
		emailMessageModel.setContent("Tutorial " + tutorial.getTitle() + " has been updated");

		emailService.sendSimpleMessage(emailMessageModel);
	}
}
