package com.ned.cassandra.model;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EmailMessageModel implements Serializable {

	private String subject;
	private String content;
	private String[] sendToPersonEmails;
}
