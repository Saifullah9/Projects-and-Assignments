package ca.mcgill.ecse321.cooperator.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.cooperator.model.ScheduledInternship;
import ca.mcgill.ecse321.cooperator.model.SpecificInternship;
import ca.mcgill.ecse321.cooperator.model.Student;

public interface SpecificInternshipRepository extends CrudRepository<SpecificInternship, String> {
	
	
	
	// returns the specific internship with id as primary key
	SpecificInternship findSpecificInternshipByInternshipId(String internshipId);
	
	// find specific internships by Scheduled Internship
	List<SpecificInternship> findByScheduledInternship(ScheduledInternship scheduledInternship);
	
	// find specific internships by students
	List<SpecificInternship> findByStudent(Student student);
	
	// find all active specific internships
	List<SpecificInternship> findByIsActiveTrue();
	
	List<SpecificInternship> findAll();
	
	
	// find the active specific internships for a student
	SpecificInternship findByStudentAndIsActiveTrue(Student student);
	
	// count the number of specific internships for a student
	int countByStudent(Student student);

	SpecificInternship findByStudentAndIsCompletedFalse(Student student);
	

	
	

}
