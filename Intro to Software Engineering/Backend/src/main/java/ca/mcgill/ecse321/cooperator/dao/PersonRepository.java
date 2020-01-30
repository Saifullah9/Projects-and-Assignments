package ca.mcgill.ecse321.cooperator.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.cooperator.model.Person;
import ca.mcgill.ecse321.cooperator.model.Student;

public interface PersonRepository extends CrudRepository<Person, String>{
	
	
	
	/*
	 * finds person by email
	 */
	Person findPersonByEmail(String email);
	
	/*
	 * finds all people that share the first name and last name
	 */
	List<Person> findPersonByFirstAndLast(String first, String name);
	

	Person findByStudent(Student student);
	

	
}
