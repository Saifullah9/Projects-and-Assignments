package ca.mcgill.ecse321.cooperator.dao;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.cooperator.model.Person;
import ca.mcgill.ecse321.cooperator.model.Student;

public interface StudentRepository extends CrudRepository<Student, Integer> {
	
	
	/*
	 * method reads student from database according to primary key
	 * primary key is student id
	 * 
	 * Spring supports automated JPA Query Creation from method names with specific language constructs
	 */

	Student findStudentByStudentId(long studentId);
	
	Student findStudentByPassword(String password);

	Student findByStudentId(long studentId);

	/*
	 * return Student by Person
	 */
	Student findStudentByPerson(Person person);
	

}
