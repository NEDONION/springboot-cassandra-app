package com.jiacheng.cassandra.service;

import com.jiacheng.cassandra.model.EmailMessageModel;
import java.util.Arrays;
import java.util.Objects;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {


	private final JavaMailSender emailSender;

	public EmailService(JavaMailSender emailSender) {
		this.emailSender = emailSender;
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
		emailSender.send(message);
	}

}
