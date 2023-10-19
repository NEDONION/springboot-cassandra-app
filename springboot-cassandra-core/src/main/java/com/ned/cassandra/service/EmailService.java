package com.ned.cassandra.service;

import com.ned.cassandra.entity.Person;
import com.ned.cassandra.entity.Tutorial;
import com.ned.cassandra.model.EmailMessageModel;
import java.util.Arrays;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {


	private final JavaMailSender emailSender;

	public EmailService(JavaMailSender emailSender) {
		this.emailSender = emailSender;
	}


	public void sendEmailNotification(Person person, Tutorial tutorial) {
		EmailMessageModel emailMessageModel = new EmailMessageModel();

		String[] sendToPersonEmails = new String[10];
		sendToPersonEmails[0] = person.getEmail();

		emailMessageModel.setSubject("Tutorial Updated");
		emailMessageModel.setSendToPersonEmails(sendToPersonEmails);
		emailMessageModel.setContent("Tutorial " + tutorial.getTitle() + " has been updated");
		log.info("created emailMessageModel: {}", emailMessageModel);
		sendSimpleMessage(emailMessageModel);
	}

	public void sendSimpleMessage(EmailMessageModel emailMessageModel) {
		String[] nonNullEmails = Arrays.stream(emailMessageModel.getSendToPersonEmails())
				.filter(Objects::nonNull)
				.toArray(String[]::new);

		if (nonNullEmails.length == 0) {
			return;  // 如果过滤后没有电子邮件地址，则直接返回，不发送消息
		}

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(nonNullEmails);
		message.setSubject(emailMessageModel.getSubject());
		message.setText(emailMessageModel.getContent());

		log.info("Sending email message to emailSender: {}", message);
		emailSender.send(message);
	}

}
