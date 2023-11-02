package com.ned.cassandra.controller;

import com.ned.cassandra.annotation.LogExecutionTime;
import com.ned.cassandra.entity.Tutorial;
import com.ned.cassandra.repository.TutorialRepository;
import com.ned.cassandra.service.NotificationService;
import com.ned.log.annotation.OperationLog;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.datastax.driver.core.utils.UUIDs;

@RestController
@RequestMapping("/api")
public class TutorialController {

	private final TutorialRepository tutorialRepository;

	private final NotificationService notificationService;

	public TutorialController(TutorialRepository tutorialRepository, NotificationService notificationService) {
		this.tutorialRepository = tutorialRepository;
		this.notificationService = notificationService;
	}

	@GetMapping("/tutorials")
	@OperationLog(bizId = "'getAllTutorials'", msg = "'Request with title = ' + #title")
	public ResponseEntity<List<Tutorial>> getAllTutorials(@RequestParam(required = false)
	String title) {

		List<Tutorial> tutorials = new ArrayList<>();
		try {
			if (title == null) {
				tutorialRepository.findAll().forEach(tutorials::add);
			} else {
				tutorialRepository.findByTitleContaining(title).forEach(tutorials::add);
			}
			if (tutorials.isEmpty()) {
				return ResponseEntity.noContent().build();
			}
			return ResponseEntity.ok(tutorials);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/tutorials/{id}")
	@OperationLog(bizId = "'getTutorialById'", msg = "'Request with id = ' + #id")
	public ResponseEntity<Tutorial> getTutorialById(@PathVariable("id") UUID id) {
		Optional<Tutorial> tutorialData = tutorialRepository.findById(id);

		if (tutorialData.isPresent()) {
			return ResponseEntity.ok(tutorialData.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/tutorials")
	@LogExecutionTime
	public ResponseEntity<Tutorial> createTutorial(@RequestBody Tutorial tutorial) {
		try {
			Tutorial _tutorial = tutorialRepository.save(new Tutorial(
					UUIDs.timeBased(),
					tutorial.getTitle(),
					tutorial.getDescription(),
					false
			));
			return ResponseEntity.status(HttpStatus.CREATED).body(_tutorial);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/tutorials/{id}")
	@OperationLog(bizId = "'updateTutorial'", msg = "'Updated description = ' + #tutorial.description + ' and title = ' + #tutorial.title")
	public ResponseEntity<Tutorial> updateTutorial(@PathVariable UUID id, @RequestBody Tutorial tutorial) {
		Optional<Tutorial> tutorialData = tutorialRepository.findById(id);

		if (tutorialData.isPresent()) {
			Tutorial _tutorial = tutorialData.get();
			_tutorial.setTitle(tutorial.getTitle());
			_tutorial.setDescription(tutorial.getDescription());
			_tutorial.setPublished(tutorial.isPublished());

			// notify users for tutorial update
			notificationService.notifyPersonsForTutorialUpdate(_tutorial);

			return ResponseEntity.ok(tutorialRepository.save(_tutorial));
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/tutorials/{id}")
	@LogExecutionTime
	public ResponseEntity<HttpStatus> deleteTutorial(@PathVariable("id") UUID id) {
		try {
			tutorialRepository.deleteById(id);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/tutorials")
	@LogExecutionTime
	public ResponseEntity<HttpStatus> deleteAllTutorials() {
		try {
			tutorialRepository.deleteAll();
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/tutorials/published")
	@LogExecutionTime
	public ResponseEntity<List<Tutorial>> findByPublished() {
		List<Tutorial> tutorials = new ArrayList<>();

		try {
			tutorials = tutorialRepository.findByPublished(true);
			if (tutorials.isEmpty()) {
				return ResponseEntity.noContent().build();
			}
			return ResponseEntity.ok(tutorials);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


}
