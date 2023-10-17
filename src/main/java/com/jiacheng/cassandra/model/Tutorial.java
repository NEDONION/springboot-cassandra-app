package com.jiacheng.cassandra.model;

import java.util.UUID;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table
@Data
public class Tutorial {

	@PrimaryKey
	private UUID id;
	private String title;
	private String description;
	private boolean published;

	public Tutorial() {

	}

	public Tutorial(UUID id, String title, String description, boolean published) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.published = published;
	}

	@Override
	public String toString() {
		return "Tutorial [id=" + id + ", title=" + title + ", desc=" + description + ", published=" + published + "]";
	}

}
