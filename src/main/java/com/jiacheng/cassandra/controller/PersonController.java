package com.jiacheng.cassandra.controller;


import com.jiacheng.cassandra.annotation.LogExecutionTime;
import com.jiacheng.cassandra.entity.Person;
import com.jiacheng.cassandra.service.PersonService;
import com.jiacheng.cassandra.vo.PersonVO;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PersonController {

	private final PersonService personService;

	public PersonController(PersonService personService) {
		this.personService = personService;
	}

	@GetMapping("/person")
	@LogExecutionTime
	public CompletableFuture<ResponseEntity<List<PersonVO>>> getAllPerson() {
		try {
			CompletableFuture<List<PersonVO>> personFuture = personService.findAllPersons();
			return personFuture.thenApply(ResponseEntity::ok);

		} catch (Exception e) {
			CompletableFuture<ResponseEntity<List<PersonVO>>> exceptionResponse = new CompletableFuture<>();
			exceptionResponse.complete(new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR));
			return exceptionResponse;
		}
	}

	@PostMapping("/person")
	@LogExecutionTime
	public CompletableFuture<ResponseEntity<Person>> createPerson(@RequestBody Person person) {
		try {
			CompletableFuture<Person> personFuture = personService.createPerson(person);
			return personFuture.thenApply(ResponseEntity::ok);
		} catch (Exception e) {
			CompletableFuture<ResponseEntity<Person>> exceptionResponse = new CompletableFuture<>();
			exceptionResponse.complete(new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR));
			return exceptionResponse;
		}
	}

	// Fetch for Person by age range
	@GetMapping("/person/age/range")
	@LogExecutionTime
	public CompletableFuture<ResponseEntity<List<PersonVO>>> getPersonByAgeRange(
			@RequestParam(required = false) Integer minAge,
			@RequestParam(required = false) Integer maxAge) {

		try {
			if (minAge == null && maxAge == null) {
				throw new IllegalArgumentException("At least one of minAge or maxAge should be provided");
			}

			CompletableFuture<List<PersonVO>> personFuture = personService.findPersonByAgeRange(minAge, maxAge);
			return personFuture.thenApply(ResponseEntity::ok);

		} catch (IllegalArgumentException e) {
			CompletableFuture<ResponseEntity<List<PersonVO>>> exceptionResponse = new CompletableFuture<>();
			exceptionResponse.complete(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
			return exceptionResponse;
		} catch (Exception e) {
			CompletableFuture<ResponseEntity<List<PersonVO>>> exceptionResponse = new CompletableFuture<>();
			exceptionResponse.complete(new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR));
			return exceptionResponse;
		}
	}


	@PutMapping("/person/{id}")
	@LogExecutionTime
	public CompletableFuture<ResponseEntity<Person>> updatePerson(@PathVariable UUID id, @RequestBody Person person) {
		try {
			CompletableFuture<Person> personFuture = personService.updatePerson(id, person);
			return personFuture.thenApply(ResponseEntity::ok);
		} catch (Exception e) {
			CompletableFuture<ResponseEntity<Person>> exceptionResponse = new CompletableFuture<>();
			exceptionResponse.complete(new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR));
			return exceptionResponse;
		}
	}





}
