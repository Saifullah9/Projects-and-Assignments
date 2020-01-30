package ca.mcgill.ecse321.cooperator.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.cooperator.model.Offer;
import ca.mcgill.ecse321.cooperator.model.Student;

public interface OfferRepository extends CrudRepository<Offer, Integer> {
	
	
	// return offer according to ID
	Offer findByOfferId(Integer offerId);
	
	
	// return offer by Student
	List<Offer> findOfferByStudent(Student student);
	
	// return active offer by Student
	Offer findOfferByStudentAndIsActiveTrue(Student student);
	
	
	// count the number of active offers for a student
	int countByStudentAndIsActiveTrue(Student student);
	
	// count the number of validated offers for a student
	int countByStudentAndIsValidatedTrue(Student student);

}
