package com.jiacheng.cassandra.vo;

import com.jiacheng.cassandra.model.Person;
import com.jiacheng.cassandra.model.Tutorial;
import java.util.List;
import java.util.Optional;
import lombok.Data;

@Data
public class PersonVO {

	private Person person;
	private List<Tutorial> tutorials;

	public PersonVO() {

	}


	public PersonVO(Person person, List<Optional<Tutorial>> tutorials) {
		this.person = person;
		this.tutorials = tutorials.stream()
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(java.util.stream.Collectors.toList());
	}
}
