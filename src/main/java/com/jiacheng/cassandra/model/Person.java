package com.jiacheng.cassandra.model;

import java.util.List;
import java.util.UUID;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table
@Data
public class Person {

	@PrimaryKey
	private UUID id;
	private String name;
	private int age;
	private List<UUID> tutorialIds;

	public Person() {

	}

	public Person(UUID id, String name, int age, List<UUID> tutorialIds) {
		this.id = id;
		this.name = name;
		this.age = age;
		this.tutorialIds = tutorialIds;
	}

}
