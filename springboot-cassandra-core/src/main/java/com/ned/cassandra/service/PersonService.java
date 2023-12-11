package com.ned.cassandra.service;

import com.datastax.driver.core.utils.UUIDs;
import com.ned.cassandra.entity.Person;
import com.ned.cassandra.entity.Tutorial;
import com.ned.cassandra.repository.PersonRepository;
import com.ned.cassandra.repository.TutorialRepository;
import com.ned.cassandra.vo.PersonVO;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PersonService {

	private static final String PERSON_SERVICE_CB = "PersonServiceCB";
	private static final String PERSON_SERVICE_RL = "PersonServiceRL";


	private final PersonRepository personRepository;

	private final TutorialRepository tutorialRepository;

	public PersonService(PersonRepository personRepository, TutorialRepository tutorialRepository) {
		this.personRepository = personRepository;
		this.tutorialRepository = tutorialRepository;
	}

	@Async
	@CircuitBreaker(name = PERSON_SERVICE_CB, fallbackMethod = "findAllPersonsFallback")
	@RateLimiter(name = PERSON_SERVICE_RL)
	public CompletableFuture<List<PersonVO>> findAllPersons() {
		List<Person> persons = personRepository.findAll();

		// 使用 CompletableFuture 的 allOf 方法等待所有异步操作完成
		CompletableFuture[] personVosFutures = persons.stream()
				.map(this::createPersonVOWithTutorials)
				.toArray(CompletableFuture[]::new);

		CompletableFuture<Void> allOf = CompletableFuture.allOf(personVosFutures);

		// 使用 thenApply 方法将 CompletableFuture 转换为 List<PersonVO>
		// 1. fetch tutorials for each person
		// 2. create PersonVO for each person
		// 3. wait for CF finish and collect all PersonVOs to a list

		/*
		For example:
		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello");
		String result = future.join();

		future.join() will block until the future is completed and return its result "Hello"
		 */

		return allOf.thenApply(v ->
				persons.stream()
						.map(person -> findTutorialsForPerson(person)
								.thenApply(tutorials -> new PersonVO(person, tutorials)))
						.map(CompletableFuture::join)
						.collect(Collectors.toList())
		);
	}
	@Async
	public CompletableFuture<Person> createPerson(Person person) {
		return CompletableFuture.completedFuture(personRepository.save(new Person(
				UUIDs.timeBased(),
				person.getName(),
				person.getAge(),
				person.getEmail(),
				person.getTutorialIds()
		)));
	}


	public CompletableFuture<PersonVO> createPersonVOWithTutorials(Person person) {
		return findTutorialsForPerson(person)
				.thenApply(tutorials -> {
					if (tutorials == null || tutorials.isEmpty()) {
						tutorials = new ArrayList<>();
					}
					return new PersonVO(person, tutorials);
				});
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


	public CompletableFuture<List<PersonVO>> findPersonByAgeRange(Integer minAge, Integer maxAge) {
		List<Person> persons = personRepository.findByAgeBetween(minAge, maxAge);

		CompletableFuture[] personVosFutures = persons.stream()
				.map(this::createPersonVOWithTutorials)
				.toArray(CompletableFuture[]::new);

		CompletableFuture<Void> allOf = CompletableFuture.allOf(personVosFutures);

		return allOf.thenApply(v ->
				persons.stream()
						.map(person -> findTutorialsForPerson(person)
								.thenApply(tutorials -> new PersonVO(person, tutorials)))
						.map(CompletableFuture::join)
						.collect(Collectors.toList())
		);
	}

	public Person findById(UUID id) {
		return personRepository.findById(id).orElse(null);
	}

	public CompletableFuture<Person> updatePerson(UUID id, Person person) {
		return CompletableFuture.supplyAsync(() -> {
			Optional<Person> existingPersonOpt = personRepository.findById(id);
			if (existingPersonOpt.isPresent()) {
				Person existingPerson = existingPersonOpt.get();

				// Update the attributes of the existing person
				existingPerson.setName(person.getName());
				existingPerson.setAge(person.getAge());
				existingPerson.setEmail(person.getEmail());
				existingPerson.setTutorialIds(person.getTutorialIds());

				return personRepository.save(existingPerson);
			} else {
				throw new RuntimeException("No person found with ID: " + id);
			}
		});
	}
	
	public List<PersonVO> findAllPersonsFallback(Throwable t) {
		log.info("findAllPersons in fallback method, return empty list");
		return Collections.emptyList();
	}

}
