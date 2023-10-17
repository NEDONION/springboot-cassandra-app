package com.jiacheng.cassandra.service;

import com.datastax.driver.core.utils.UUIDs;
import com.jiacheng.cassandra.annotation.LogExecutionTime;
import com.jiacheng.cassandra.model.Person;
import com.jiacheng.cassandra.model.Tutorial;
import com.jiacheng.cassandra.repository.PersonRepository;
import com.jiacheng.cassandra.repository.TutorialRepository;
import com.jiacheng.cassandra.vo.PersonVO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PersonService {


	private final PersonRepository personRepository;

	private final TutorialRepository tutorialRepository;

	public PersonService(PersonRepository personRepository, TutorialRepository tutorialRepository) {
		this.personRepository = personRepository;
		this.tutorialRepository = tutorialRepository;
	}

	@Async
	public CompletableFuture<List<PersonVO>> findAllPersons() {
		List<Person> persons = personRepository.findAll();

		// 使用 CompletableFuture 的 allOf 方法等待所有异步操作完成
		CompletableFuture[] personVosFutures = persons.stream()
				.map(person -> findTutorialsForPerson(person)
						.thenApply(tutorials -> {
							if (tutorials != null && !tutorials.isEmpty()) {
								return new PersonVO(person, tutorials);
							} else {
								// 如果 tutorials 为空，创建一个没有教程的 PersonVO
								return new PersonVO(person, new ArrayList<>());
							}
						}))
				.toArray(CompletableFuture[]::new);

		CompletableFuture<Void> allOf = CompletableFuture.allOf(personVosFutures);

		// 使用 thenApply 方法将 CompletableFuture 转换为 List<PersonVO>
		return allOf.thenApply(v ->
				persons.stream()
						.map(person -> findTutorialsForPerson(person)
								.thenApply(tutorials -> new PersonVO(person, tutorials)))
						.map(CompletableFuture::join)
						.collect(Collectors.toList())
		);
	}


	public CompletableFuture<Person> createPerson(Person person) {
		return CompletableFuture.completedFuture(personRepository.save(new Person(
				UUIDs.timeBased(),
				person.getName(),
				person.getAge(),
				person.getTutorialIds()
		)));
	}

	private CompletableFuture<List<Optional<Tutorial>>> findTutorialsForPerson(Person person) {
		List<UUID> tutorialIds = person.getTutorialIds();

		if (tutorialIds == null) {
			tutorialIds = Collections.emptyList(); // 将 tutorialIds 初始化为一个空列表
		}

		List<Optional<Tutorial>> tutorials = new ArrayList<>();

		for (UUID id : tutorialIds) {
			Optional<Tutorial> tutorial = tutorialRepository.findById(id);
			if (tutorial != null) {
				tutorials.add(tutorial);
			} else {
				tutorials.add(Optional.empty());
			}
		}

		return CompletableFuture.completedFuture(tutorials);
	}



}
