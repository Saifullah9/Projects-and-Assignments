package ca.mcgill.ecse321.cooperator.dao;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.cooperator.model.ScheduledInternship;
import ca.mcgill.ecse321.cooperator.model.SpecificInternship;

public interface ScheduledInternshipRepository extends CrudRepository<ScheduledInternship, String> {
	
	// get scheduled internship by Scheduled Internship ID
	ScheduledInternship findByPositionId(String id);
	
	//get scheduled internship by specific internship
	ScheduledInternship findBySpecificInternships(SpecificInternship specificInternship);
}
