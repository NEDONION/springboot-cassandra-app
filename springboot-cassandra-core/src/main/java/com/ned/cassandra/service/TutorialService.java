package com.ned.cassandra.service;

import com.datastax.driver.core.utils.UUIDs;
import com.ned.cassandra.entity.Tutorial;
import com.ned.cassandra.exception.TutorialBusinessException;
import com.ned.cassandra.repository.TutorialRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TutorialService {

	private static final String TUTORIAL_SERVICE_CB = "TutorialServiceCB";
	private static final String TUTORIAL_SERVICE_RL = "TutorialServiceRL";

	private final TutorialRepository tutorialRepository;
	private final NotificationService notificationService;

	public TutorialService(TutorialRepository tutorialRepository, NotificationService notificationService) {
		this.tutorialRepository = tutorialRepository;
		this.notificationService = notificationService;
	}

	@CircuitBreaker(name = TUTORIAL_SERVICE_CB, fallbackMethod = "getAllTutorialsFallback")
	@RateLimiter(name = TUTORIAL_SERVICE_RL)
	public List<Tutorial> getAllTutorials(String title) {
		List<Tutorial> tutorials = new ArrayList<>();
		try {
			if (title == null) {
				tutorialRepository.findAll().forEach(tutorials::add);
			} else {
				tutorialRepository.findByTitleContaining(title).forEach(tutorials::add);
			}
			return tutorials;
		} catch (Exception e) {
			log.info("TutorialService.getAllTutorials() failed", e);
			throw new TutorialBusinessException("TutorialService.getAllTutorials() failed");
		}
	}

	public Optional<Tutorial> getTutorialById(UUID id) {
		return tutorialRepository.findById(id);
	}

	public Tutorial createTutorial(Tutorial tutorial) {
		return tutorialRepository.save(new Tutorial(
				UUIDs.timeBased(),
				tutorial.getTitle(),
				tutorial.getDescription(),
				false
		));
	}

	public Optional<Tutorial> updateTutorial(UUID id, Tutorial tutorial) {
		Optional<Tutorial> tutorialData = tutorialRepository.findById(id);
		if (tutorialData.isPresent()) {
			Tutorial _tutorial = tutorialData.get();
			_tutorial.setTitle(tutorial.getTitle());
			_tutorial.setDescription(tutorial.getDescription());
			_tutorial.setPublished(tutorial.isPublished());

			// notify users for tutorial update
			notificationService.notifyPersonsForTutorialUpdate(_tutorial);

			return Optional.of(tutorialRepository.save(_tutorial));
		} else {
			return Optional.empty();
		}
	}

	public void deleteTutorial(UUID id) {
		tutorialRepository.deleteById(id);
	}

	public void deleteAllTutorials() {
		tutorialRepository.deleteAll();
	}

	public List<Tutorial> findByPublished() {
		return tutorialRepository.findByPublished(true);
	}
	public List<Tutorial> getAllTutorialsFallback(Exception e) {
		List<Tutorial> tutorials = new ArrayList<>();
		return tutorials;
	}
}