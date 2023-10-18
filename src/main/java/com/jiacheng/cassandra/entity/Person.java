package com.jiacheng.cassandra.entity;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person {

	@PrimaryKey
	private UUID id;
	private String name;
	private int age;
	private String email;
	private List<UUID> tutorialIds;

}
