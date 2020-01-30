package ca.mcgill.ecse321.cooperator.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import ca.mcgill.ecse321.cooperator.dao.CooperatorSystemRepository;
import ca.mcgill.ecse321.cooperator.dao.DocumentRepository;
import ca.mcgill.ecse321.cooperator.dao.OfferRepository;
import ca.mcgill.ecse321.cooperator.dao.PersonRepository;
import ca.mcgill.ecse321.cooperator.dao.ScheduledInternshipRepository;
import ca.mcgill.ecse321.cooperator.dao.SpecificInternshipRepository;
import ca.mcgill.ecse321.cooperator.dao.StudentRepository;
import ca.mcgill.ecse321.cooperator.model.CooperatorSystem;
import ca.mcgill.ecse321.cooperator.model.Offer;
import ca.mcgill.ecse321.cooperator.model.Person;
import ca.mcgill.ecse321.cooperator.model.ScheduledInternship;
import ca.mcgill.ecse321.cooperator.model.SpecificInternship;
import ca.mcgill.ecse321.cooperator.model.Student;
import ca.mcgill.ecse321.cooperator.model.Term;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestCooperatorService {
	@Autowired
	private CooperatorService service;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private SpecificInternshipRepository specificInternshipRepository;

	@Autowired
	private ScheduledInternshipRepository scheduledInternshipRepository;

	@Autowired
	private DocumentRepository documentRepository;

	@Autowired
	private CooperatorSystemRepository cooperatorSystemRepository;

	@Autowired
	private OfferRepository offerRepository;

	/*
	 * @After public void clearDatabaseEnd() { // clear all tables
	 * offerRepository.deleteAll(); cooperatorSystemRepository.deleteAll();
	 * personRepository.deleteAll(); specificInternshipRepository.deleteAll();
	 * studentRepository.deleteAll(); scheduledInternshipRepository.deleteAll();
	 * documentRepository.deleteAll(); }
	 */

	@After
	public void clearDatabaseEnd() {
		// clear all tables
		cooperatorSystemRepository.deleteAll();
		specificInternshipRepository.deleteAll();
		offerRepository.deleteAll();
		personRepository.deleteAll();
		studentRepository.deleteAll();
		scheduledInternshipRepository.deleteAll();
		documentRepository.deleteAll();
	}

	@Test
	public void testCreateCooperatorSystem() {
		service.createCooperatorSystem();
		List<CooperatorSystem> allSystems = service.getAllCooperatorSystems();
		assertEquals(1, allSystems.size());
	}
	@Test
	public void testCreatePerson() {
		assertEquals(0, service.getAllPersons().size());

		String firstName = "Hassan";
		String lastName = "Haidar";
		String email = "please.work@mail.mcgill.ca";

		try {
			service.createPerson(firstName, lastName, email);
		} catch (IllegalArgumentException e) {
			fail();
		}

		List<Person> allPersons = service.getAllPersons();

		assertEquals(1, allPersons.size());
		assertEquals(firstName, allPersons.get(0).getFirst());

	}

	@Test
	public void testCreatePersonNullName() {
		assertEquals(0, service.getAllPersons().size());

		String error = null;

		String firstName = null;
		String lastName = null;

		String email = "please.work@mail.mcgill.ca";

		try {
			service.createPerson(firstName, lastName, email);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}

		// check error
		assertEquals("Person name can't be empty", error);
		assertEquals(0, service.getAllPersons().size());
	}

	@Test
	public void testCreatePersonNullEmail() {
		assertEquals(0, service.getAllPersons().size());

		String error = null;

		String firstName = "Hassan";
		String lastName = "Haidar";

		String email = null;

		try {
			service.createPerson(firstName, lastName, email);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}

		// check error
		assertEquals("Email can't be empty", error);
		assertEquals(0, service.getAllPersons().size());
	}

	@Test
	public void testCreatePersonSpaceName() {
		assertEquals(0, service.getAllPersons().size());

		String error = null;

		String firstName = "   ";
		String lastName = "Haidar";

		String email = "please.work@mail.mcgill.ca";

		try {
			service.createPerson(firstName, lastName, email);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}

		// check error
		assertEquals("Person name can't be empty", error);
		assertEquals(0, service.getAllPersons().size());
	}

	@Test
	public void testCreatePersonSpaceEmail() {
		assertEquals(0, service.getAllPersons().size());

		String error = null;

		String firstName = "Hassan";
		String lastName = "Haidar";

		String email = "    ";

		try {
			service.createPerson(firstName, lastName, email);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}

		// check error
		assertEquals("Email can't be empty", error);
		assertEquals(0, service.getAllPersons().size());
	}

	@Test
	public void testCreateMultiplePersons() {
		String firstName = "Hassan";
		String lastName = "Haidar";
		String email = "please.work@mail.mcgill.ca";

		String firstName1 = "Ahmad";
		String lastName1 = "Hegazi";
		String email1 = "very.please.work@mail.mcgill.ca";

		try {
			service.createPerson(firstName, lastName, email);
			service.createPerson(firstName1, lastName1, email1);
		} catch (IllegalArgumentException e) {
			fail();
		}

		List<Person> allPersons = service.getAllPersons();
		assertEquals(2, allPersons.size());
		assertEquals(firstName, allPersons.get(0).getFirst());
		assertEquals(firstName1, allPersons.get(1).getFirst());
	}

	@Test
	public void testCreateMultiplePersonsDuplicateEmails() {
		String firstName = "Hassan";
		String lastName = "Haidar";
		String email = "please.work@mail.mcgill.ca";

		String firstName1 = "Ahmad";
		String lastName1 = "Hegazi";
		String email1 = "please.work@mail.mcgill.ca";

		try {
			service.createPerson(firstName, lastName, email);
			List<Person> allPersons = service.getAllPersons();
			assertEquals(1, allPersons.size());

			service.createPerson(firstName1, lastName1, email1);
		} catch (IllegalArgumentException e) {
			e.getMessage();
		}

		List<Person> allPersons = service.getAllPersons();
		assertEquals(1, allPersons.size());
		assertEquals(firstName, allPersons.get(0).getFirst());
	}

	@Test
	public void testCreateStudent() {
		// service.createCooperatorSystem();

		try {
			Person person = service.createPerson("Hassan", "Haidar", "please.work@mail.mcgill.ca");
			service.createStudent(person, (long)12345678);
			System.out.print("BREAK");
		} catch (Exception e) {
			fail();
		}
		List<Student> list = service.getAllStudents();
		int size = list.size();
		assertEquals(1, (long) size);

	}

	@Test
	public void testCreateMultipleStudents() {
		assertEquals(0, service.getAllStudents().size());
		try {
			Person person1 = service.createPerson("Hassan", "Haidar", "please.work@mail.mcgill.ca");
			service.createStudent(person1, (long)12345678);

			Person person2 = service.createPerson("Ahmad", "Hegazi", "very.please.work@mail.mcgill.ca");
			service.createStudent(person2, (long)123456789);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertEquals(2, service.getAllStudents().size());
	}

	@Test
	public void testCreateMultipleStudentsDupilicatePerson() {
		String error = null;
		assertEquals(0, service.getAllStudents().size());
		try {
			Person person1 = service.createPerson("Hassan", "Haidar", "please.work@mail.mcgill.ca");
			service.createStudent(person1, (long)12345678);

			service.createStudent(person1, (long)123456789);

		} catch (Exception e) {
			error = e.getMessage();
		}

		assertEquals("Error: User is already a student!", error);
		assertEquals(1, service.getAllStudents().size());
	}

	@Test
	public void testCreateMultipleStudentsDupilicateID() {
		String error = null;
		assertEquals(0, service.getAllStudents().size());
		try {
			Person person1 = service.createPerson("Hassan", "Haidar", "please.work@mail.mcgill.ca");
			service.createStudent(person1, (long)12345678);

			// should throw error
			Person person2 = service.createPerson("Ahmad", "Hegazi", "very.pleasework@mail.mcgill.ca");
			service.createStudent(person2, (long)12345678);

		} catch (Exception e) {
			error = e.getMessage();
		}
		assertEquals("Error: User already has this id", error);
		assertEquals(1, service.getAllStudents().size());
	}

	@Test
	public void testCreateScheduledInternship() {
		assertEquals(0, service.getAllScheduledInternships().size());
		try {
			service.createScheduledInternShip("name", "employer", "FALL");
		} catch (Exception e) {
			fail();
		}
		assertEquals(1, service.getAllScheduledInternships().size());
	}

	@Test
	public void testCreateMultipleScheduledInternship() {
		assertEquals(0, service.getAllScheduledInternships().size());
		try {
			service.createScheduledInternShip("GP", "Ubi", "FALL");
			service.createScheduledInternShip("QA", "BB", "FALL");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertEquals(2, service.getAllScheduledInternships().size());
	}

	@Test
	public void testCreateOffer() {
		assertEquals(0, service.getAllOffers().size());
		Person person = service.createPerson("Hassan", "Haidar", "please.work@mail.mcgill.ca");
		try {
			Student student = service.createStudent(person, (long)12345678);
			service.createOffer(student);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertEquals(1, service.getAllOffers().size());
	}

	@Test
	public void testCreateAnOfferForTwoStudents() {
		String error = null;
		assertEquals(0, service.getAllOffers().size());
		Person person = service.createPerson("Hassan", "Haidar", "please.work@mail.mcgill.ca");
		Student student = null;
		try {
			student = service.createStudent(person, (long)12345678);
			service.createOffer(student);
			assertEquals(1, service.getAllOffers().size());
			service.createOffer(student);
		} catch (Exception e) {
			error = e.getMessage();
		}
		assertEquals(1, service.getAllOffers().size());
		assertEquals("Student can't have two active offers", error);
	}

	@Test
	public void testCreateOfferNullStudent() {
		String error = null;
		assertEquals(0, service.getAllOffers().size());
		service.createPerson("Hassan", "Haidar", "please.work@mail.mcgill.ca");
		Student student = null;
		try {
			service.createOffer(student);
		} catch (Exception e) {
			error = e.getMessage();
		}
		assertEquals(0, service.getAllOffers().size());
		assertEquals("Error:Student can't be null!", error);
	}

	@Test
	public void testCreateMultipleOffersForOneStudent() {
		assertEquals(0, service.getAllOffers().size());
		Person person = service.createPerson("Hassan", "Haidar", "please.work@mail.mcgill.ca");
		Person person2 = service.createPerson("Ahmad", "Hegazi", "very.please.work@mail.mcgill.ca");
		Student student = null;
		Student student2 = null;
		try {
			student = service.createStudent(person, (long)12345678);
			student2 = service.createStudent(person2, (long)123456789);
			service.createOffer(student);
			assertEquals(1, service.getAllOffers().size());
			service.createOffer(student2);
			assertEquals(2, service.getAllOffers().size());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testUpdateOffer() {
		String url = "https://www.onedrive.com/document";
		String name = "Contract";
		Person person = service.createPerson("Hassan", "Haidar", "please.work@mail.mcgill.ca");
		assertEquals(1, service.getAllPersons().size());
		Student student = null;
		Offer offer = null;
		try {
			student = service.createStudent(person, (long)12345678);
			assertEquals(1, service.getAllStudents().size());
			offer = service.createOffer(student);
			assertEquals(1, service.getAllOffers().size());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			service.updateOffer(offer, url, name);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		// assert the the document was persisted
		assertEquals(1, service.getAllDocuments().size());

		// assert that number of offers stays the same
		assertEquals(1, service.getAllOffers().size());

	}

	@Test
	public void testUpdateOfferStudentHasTwoDocs() {

		String error = null;

		String url1 = "https://www.onedrive.com/Contract";
		String name1 = "Contract";

		String url2 = "https://www.onedrive.com/Form";
		String name2 = "Form";

		String url3 = "https://www.onedrive.com/nienf";
		String name3 = "Form";

		Person person = service.createPerson("Hassan", "Haidar", "please.work@mail.mcgill.ca");
		assertEquals(1, service.getAllPersons().size());
		Student student = null;
		Offer offer = null;
		try {
			student = service.createStudent(person, (long)12345678);
			assertEquals(1, service.getAllStudents().size());
			offer = service.createOffer(student);
			assertEquals(1, service.getAllOffers().size());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			service.updateOffer(offer, url1, name1);
			assertEquals(1, service.getAllDocuments().size());
			service.updateOffer(offer, url2, name2);
			assertEquals(2, service.getAllDocuments().size());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			service.updateOffer(offer, url3, name3);
		} catch (Exception e) {
			error = e.getMessage();
		}

		// assert the the document was persisted
		assertEquals(2, service.getAllDocuments().size());

		// assert that number of offers stays the same
		assertEquals(1, service.getAllOffers().size());

		// assert that we get the right error message
		assertEquals("Student already has two documents associated with this offer. "
				+ "Please delete the document you wish to replace.", error);

	}

	@Test
	public void testRegisterSpecificInternship() {
		ScheduledInternship scheduledInternship = service.createScheduledInternShip("GP", "Ubi", "SUMMER");
		assertEquals(1, service.getAllScheduledInternships().size());
		Person person = service.createPerson("Hassan", "Haidar", "please.work@mail.mcgill.ca");
		try {
			Student student = service.createStudent(person, (long)12345678);
			Offer offer = service.createOffer(student);
			// ==========THIS METHOD CALL PLAYS THE ROLE OF THE ADMIN===================
			service.validateOffer(offer);
			// ==========================================================================

			service.registerSpecificInternship(student, scheduledInternship,
					2018);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		int i = service.getAllSpecificInternships().size();
		assertEquals(1, i);
	}

	@Test
	public void testRegisterSpecificInternshipNoValidOffer() {
		String error = null;
		ScheduledInternship scheduledInternship = service.createScheduledInternShip("GP", "Ubi", "SUMMER");
		assertEquals(1, service.getAllScheduledInternships().size());
		Person person = service.createPerson("Hassan", "Haidar", "please.work@mail.mcgill.ca");
		try {
			Student student = service.createStudent(person, (long)12345678);
			service.createOffer(student);

			service.registerSpecificInternship(student, scheduledInternship,
					2018);

		} catch (Exception e) {
			error = e.getMessage();
		}
		int i = service.getAllSpecificInternships().size();
		assertEquals(0, i);

		assertEquals("Student does not have a valid offer.", error);
	}

	@Test
	public void testRegisterMultipleSpecificInternships() {
		// scheduled internship that we create specific internships from
		ScheduledInternship scheduledInternship = service.createScheduledInternShip("GP", "Ubi", "SUMMER");
		assertEquals(1, service.getAllScheduledInternships().size());
		Person person = service.createPerson("Hassan", "Haidar", "please.work@mail.mcgill.ca");
		assertEquals(1, service.getAllPersons().size());

		SpecificInternship specificInternship = null;

		try {
			Student student = service.createStudent(person, (long)12345678);

			// offer 1 and internship 1
			Offer offer = service.createOffer(student);
			assertEquals(1, service.getAllOffers().size());
			service.validateOffer(offer);
			assertEquals(1, service.getAllOffers().size());

			specificInternship = service.registerSpecificInternship(student, scheduledInternship, 2018);
			assertEquals(1, service.getAllSpecificInternships().size());
			service.finishSpecificInternship(specificInternship);
			assertEquals(1, service.getAllSpecificInternships().size());

			// offer 2 and internship 2
			Offer offer2 = service.createOffer(student);
			assertEquals(2, service.getAllOffers().size());
			service.validateOffer(offer2);
			assertEquals(2, service.getAllOffers().size());

			specificInternship = service.registerSpecificInternship(student, scheduledInternship, 2018);
			assertEquals(2, service.getAllSpecificInternships().size());
			service.finishSpecificInternship(specificInternship);
			assertEquals(2, service.getAllSpecificInternships().size());

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		assertEquals(2, service.getAllSpecificInternships().size());
	}

	@Test
	public void testRegisterSpecificInternshipStudentHasFourInternships() {
		String error = null;
		ScheduledInternship scheduledInternship = service.createScheduledInternShip("GP", "Ubi", "SUMMER");
		assertEquals(1, service.getAllScheduledInternships().size());
		Person person = service.createPerson("Hassan", "Haidar", "please.work@mail.mcgill.ca");
		try {
			Student student = service.createStudent(person, (long)12345678);

			// offer 1 and internship 1
			Offer offer = service.createOffer(student);
			// ==========THIS METHOD CALL PLAYS THE ROLE OF THE ADMIN===================
			service.validateOffer(offer);
			// ==========================================================================
			SpecificInternship specificInternship = service.registerSpecificInternship(student, scheduledInternship,
					2018);
			service.finishSpecificInternship(specificInternship);
			assertEquals(1, service.getAllSpecificInternships().size());

			// offer 2 and internship 2
			offer = service.createOffer(student);
			service.validateOffer(offer); // validate offer
			specificInternship = service.registerSpecificInternship(student, scheduledInternship, 2018);
			service.finishSpecificInternship(specificInternship);
			assertEquals(2, service.getAllSpecificInternships().size());

			// offer 3 and internship 3
			offer = service.createOffer(student);
			service.validateOffer(offer); // validate offer
			specificInternship = service.registerSpecificInternship(student, scheduledInternship, 2018);
			service.finishSpecificInternship(specificInternship);
			assertEquals(3, service.getAllSpecificInternships().size());

			// offer 4 and internship 4
			offer = service.createOffer(student);
			service.validateOffer(offer); // validate offer
			specificInternship = service.registerSpecificInternship(student, scheduledInternship, 2018);
			service.finishSpecificInternship(specificInternship);
			assertEquals(4, service.getAllSpecificInternships().size());

			// offer 5 and internship 5
			// should throw error
			offer = service.createOffer(student);
		} catch (Exception e) {
			error = e.getMessage();
		}
		assertEquals("Student already has 4 internships.\nCant create more offers", error);
	}

	@Test
	public void testDeleteStudent() {
		Person person = service.createPerson("Hassan", "Haidar", "please.work@mail.mcgill.ca");
		int id = 12345678;
		try {
			service.createStudent(person, (long)id);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertEquals(1, service.getAllStudents().size());

		service.deleteStudentByID(id);

		assertEquals(0, service.getAllStudents().size());

	}

	@Test
	public void testDeleteOfferDocument() {
		String url = "https://www.onedrive.com/document";
		String name = "Contract";
		Person person = service.createPerson("Hassan", "Haidar", "please.work@mail.mcgill.ca");
		assertEquals(1, service.getAllPersons().size());
		Student student = null;
		Offer offer = null;
		try {
			student = service.createStudent(person, (long)12345678);
			assertEquals(1, service.getAllStudents().size());
			offer = service.createOffer(student);
			assertEquals(1, service.getAllOffers().size());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			service.updateOffer(offer, url, name);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		// assert the the document was persisted
		assertEquals(1, service.getAllDocuments().size());

		// assert that number of offers stays the same
		assertEquals(1, service.getAllOffers().size());

		service.deleteDocumentByUrlFromOffer(offer, url);

		// assert that number of offers stays the same
		assertEquals(1, service.getAllOffers().size());
		// assert the the document was persisted
		assertEquals(0, service.getAllDocuments().size());
	}

	// ========================YOUSUF CODE=======================================

	@Test
	public void testDocsCompleted(){
		Person ps = service.createPerson("Yousuf", "Badawi", "yousuf.badawi@gmail.com");
		Student st = null;
		try {
			st = service.createStudent(ps, (long)260866956);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		ScheduledInternship schedI = service.createScheduledInternShip("Internship", "Ubisoft", "SUMMER");

		Offer of = null;
		try {
			of = service.createOffer(st);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		service.validateOffer(of);

		try {
			service.registerSpecificInternship(st, schedI, 2018);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		service.submitInternshipEvaluation(st, "URL", "Eval1");

		assertEquals(1, service.getDocsCompleted(st));
	}


	@Test
	public void testDocsCompletedNoSubmit() {

		String error = null;
		Person ps = service.createPerson("Yousuf", "Badawi", "yousuf.badawi@gmail.com");
		service.createScheduledInternShip("Internship", "Ubisoft", "SUMMER");
		Student st = null;
		try {
			st = service.createStudent(ps, (long) 260866956);
			Offer of = service.createOffer(st);
			service.validateOffer(of);	
			service.getDocsCompleted(st);
		}

		catch (Exception e) {
			error = e.getMessage();
		}

		assertEquals("Error: Student " + st.getStudentId() + " does not have an active Internship!", error);

	}

	//=================================================================================================



	@Test
	public void testGetTermsFinishedStudentNull() {
		//assertEquals(0, service.toList(service.studentRepository.findAll()).size())
		Student student = null;
		String error = null;

		try {
			service.getTermsFinished(student);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}

		// check error
		assertEquals(" The student does not exist!", error);

		// check no change in memory
		//assertEquals(0, service.toList(service.studentRepository.findAll()).size());

	}


	// ================================SAIF'S TESTS==================================
	@Test
	public void testGetTermsFinished() {
		//assertEquals(0, service.toList(service.studentRepository.findAll()).size());

		Student student = new Student();
		student.setTermsFinished(0);
		//studentRepository.save(student);

		try {
			service.getTermsFinished(student);
		} catch (IllegalArgumentException e) {
			//check that no error occurred
			fail();
		}

		// check size is now 1
		//assertEquals(1,service.toList(service.studentRepository.findAll()).size());

		// check terms is 0 as we set
		assertEquals(0, service.getTermsFinished(student));

	}
	@Test
	public void testGetTermsRemainingStudentNull() {
		//	assertEquals(0, service.toList(service.studentRepository.findAll()).size());
		Student student = null;
		String error = null;

		try {
			service.getTermsremaining(student);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}

		// check error
		assertEquals(" The student does not exist!", error);

		// check no change in memory
		//assertEquals(0, service.toList(service.studentRepository.findAll()).size());

	}

	@Test
	public void testGetTermsRemaining() {
		//		assertEquals(0, service.toList(service.studentRepository.findAll()).size());

		Student student = new Student();
		student.setTermsRemaining(0);
		//studentRepository.save(student);

		try {
			service.getTermsremaining(student);
		} catch (IllegalArgumentException e) {
			//check that no error occurred
			fail();
		}

		// check size is now 1
		//assertEquals(1,service.toList(service.studentRepository.findAll()).size());

		// check terms is 0 as we set
		assertEquals(0, service.getTermsFinished(student));

	}
	@Test
	public void testGetAllInternshipsStudentNull() {
		//	assertEquals(0, service.toList(service.studentRepository.findAll()).size());		
		Student student = null;
		String error = null;

		try {
			service.getAllInternships(student);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}

		// check error
		assertEquals(" The student does not exist!", error);

		// check no change in memory
		//assertEquals(0, service.toList(service.studentRepository.findAll()).size());

	}

	@Test
	public void testGetAllInternships() throws Exception {
		Person person = service.createPerson("Saif", "haa", "edy");
		Student student = service.createStudent(person, (long)260733168);
		Offer offer = service.createOffer(student);
		ScheduledInternship sc = service.createScheduledInternShip("hamada", "basha", "FALL");
		service.validateOffer(offer);
		service.registerSpecificInternship(student, sc, 2011);

		try {
			service.getAllInternships(student);
		} catch (IllegalArgumentException e) {
			fail();
		}

		assertEquals(1, (service.getAllInternships(student)).size());
		//	assertEquals(1, service.specificInternshipRepository.findByStudent(student).size());

	}
	@Test
	public void testGetCompanyInternshipNull() {

		ScheduledInternship internship = null;
		String error = null;

		try {
			service.getCompany(internship);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}

		// check error
		assertEquals(" The scheduled internship does not exist!", error);

		// check no change in memory
		//assertEquals(0, service.toList(service.scheduledInternshipRepository.findAll()).size());

	}
	@Test
	public void testGetCompany() {
		ScheduledInternship internship = new ScheduledInternship();
		internship.setEmployer("Naughty Dog");
		try {
			internship.getEmployer();
		} catch (IllegalArgumentException e) {
			fail();
		}

		assertEquals("Naughty Dog" , internship.getEmployer());
		//assertEquals(1, service.toList(service.scheduledInternshipRepository.findAll()).size());
	}
	@Test
	public void testGetSemesterNull() {

		ScheduledInternship internship =  null;
		String error = null;
		try {
			service.getSemester(internship);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}

		// check error
		assertEquals(" The scheduled internship does not exist!", error);

		// check no change in memory
		//assertEquals(0, service.toList(service.scheduledInternshipRepository.findAll()).size());

	}
	@Test
	public void testGetSemester() {

		ScheduledInternship internship = new ScheduledInternship();
		internship.setTerm(Term.valueOf("FALL"));
		//scheduledInternshipRepository.save(internship);
		try {
			service.getSemester(internship);
		} catch(IllegalArgumentException e) {
			fail();
		}

		assertEquals("FALL", service.getSemester(internship));

	}
	@Test
	public void testGetYearOfInternshipNotInRepo()  {
		Person person = service.createPerson("Saif", "haa", "edy");
		try {
			service.createStudent(person, (long)260733168);
		} catch (Exception e1) {

			e1.printStackTrace();
		}
		service.createScheduledInternShip("hamada", "basha", "FALL");
		SpecificInternship internship = new SpecificInternship();
		internship.setInternshipId("19");
		String error = null;

		try {
			service.getYearOfInternship(internship);
		} catch (Exception e) {
			error = e.getMessage();
		}

		assertEquals(" The Internship does not exist!", error);

	}
	@Test
	public void testGetYearOfInternship()  {
		Person person = service.createPerson("Saif", "haa", "edy");
		Student student = null;
		Offer offer = null;
		try {
			student = service.createStudent(person, (long)260733168);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		try {
			offer = service.createOffer(student);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
		ScheduledInternship sc = service.createScheduledInternShip("hamada", "basha", "FALL");
		service.validateOffer(offer);
		SpecificInternship internship = null;
		try {
			internship = service.registerSpecificInternship(student, sc, 2011);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
		int year = 0;
		String error = null;
		try {
			year =	service.getYearOfInternship(internship);
		} catch (Exception e) {
			error = e.getMessage();
			assertEquals("why" ,error);
			fail();

		}
		assertEquals( 2011, year);
	}

	@Test
	public void testGetIsCompletedInternshipNull() {

		Person person = service.createPerson("Saif", "haa", "edy");
		try {
			service.createStudent(person, (long)260733168);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
		service.createScheduledInternShip("hamada", "basha", "FALL");
		SpecificInternship internship = new SpecificInternship();
		String error = null;

		try {
			service.getIsCompleted(internship);
		} catch (Exception e) {
			error = e.getMessage();
		}

		assertEquals(" The Internship does not exist!", error);	

	}

	@Test
	public void testGetIsCompletedInternshipNullRepo() {
		SpecificInternship internship = new SpecificInternship();
		internship.setInternshipId("5");
		String error = null;

		try {
			service.getIsCompleted(internship);
		} catch (Exception e) {
			error = e.getMessage();
		}

		assertEquals(" The Internship does not exist!", error);	

	}
	@Test
	public void testGetIsCompleted() throws Exception {

		Person person = service.createPerson("Saif", "haa", "edy");
		Student student = null;
		Offer offer = null;
		try {
			student = service.createStudent(person, (long)260733169);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		try {
			offer = service.createOffer(student);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
		ScheduledInternship sc = service.createScheduledInternShip("hamada", "basha", "FALL");
		service.validateOffer(offer);
		SpecificInternship internship = null;
		try {
			internship = service.registerSpecificInternship(student, sc, 2011);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
		boolean isComplete = true;
		try {
			isComplete = service.getIsCompleted(internship);
		} catch (Exception e) {
			fail();
		}
		assertEquals( false, isComplete);
	}
	//=========================================================================================




	//===========================================AHMADS' TESTS=========================================================

	//-------------------------AZHAR TESTS -------------------//

	//===testSubmitInternshipEvaluation==//


	/*
	 * Check if correct error message is given
	 * when document has name == null
	 */
	@Test
	public void testSubmitInternshipEvalNameNull() {
		//THIS TEST WORKS, DONT SCREW THIS UP

		assertEquals(0, service.getAllDocuments().size());

		String name = null;
		String url = "link.up";
		String error = null;

		Person person = service.createPerson("Ali", "Jones", "xxx");

		//create Student
		Student student = null;

		try {
			student = service.createStudent(person, (long)1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		try {
			service.submitInternshipEvaluation(student, url, name);
		} catch (Exception e) {
			error = e.getMessage();
		}

		// check error
		assertEquals("The name of the document is invalid!", error);

		// check no change in memory
		assertEquals(0, service.getAllDocuments().size());
	}

	/*
	 * Check if correct error message is given
	 * when document has url == null
	 */
	@Test
	public void testSubmitInternshipEvalURLNull() {
		//THIS TEST WORKS, DONT SCREW THIS UP

		assertEquals(0, service.getAllDocuments().size());

		String name = "bucky";
		String url = null;
		String error = null;

		Person person = service.createPerson("Ali", "Jones", "xxx");

		//create Student
		Student student = null;

		try {
			student = service.createStudent(person, (long)1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		try {
			service.submitInternshipEvaluation(student, url, name);
		} catch (Exception e) {
			error = e.getMessage();
		}

		// check error
		assertEquals("The URL of the document is invalid!", error);

		// check no change in memory
		assertEquals(0, service.getAllDocuments().size());
	}



	/*
	 * Check if submitIntershipEval is successfull
	 * with the correct params
	 */

	@Test
	public void testSubmitInternshipEvaluation() {
		//THIS WORKS!

		assertEquals(0, service.getAllSpecificInternships().size());
		assertEquals(0, service.getAllStudents().size());
		assertEquals(0, service.getAllPersons().size());
		assertEquals(0, service.getAllDocuments().size());
		assertEquals(0, service.getAllScheduledInternships().size());

		//Doc details
		String url = "link.up";
		String name = "bucky";

		Person person = service.createPerson("Ali", "Jones", "xxx");

		//create Student
		Student student = null;

		try {
			student = service.createStudent(person, (long)1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(1, service.getAllStudents().size());


		//create SpecificInternship
		ScheduledInternship schedIn = service.createScheduledInternShip("HP", "Bob", "FALL"); 
		assertEquals(1, service.getAllScheduledInternships().size());

		Offer offer = null;
		try {
			offer = service.createOffer(student);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			fail();
		}
		service.validateOffer(offer);

		try {
			service.registerSpecificInternship(student, schedIn, 2019);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}

		Boolean check = null;

		try {
			check = service.submitInternshipEvaluation(student, url, name);
		} catch (IllegalArgumentException e) {
			// Check that no error occurred
			e.printStackTrace();
			fail();
		}

		// Check method return
		assertEquals(true, check);

		assertEquals(1,service.getAllDocuments().size());


	}


	/*
	 * Check if correct error message given
	 * when Student == null
	 */
	@Test
	public void testSubmitInternshipEvaluationStudentInvalid() {

		assertEquals(0, service.getAllSpecificInternships().size());
		assertEquals(0, service.getAllStudents().size());
		assertEquals(0, service.getAllPersons().size());
		assertEquals(0, service.getAllDocuments().size());
		assertEquals(0, service.getAllScheduledInternships().size());

		//Doc details
		String url = "link.up";
		String name = "bucky";

		Person person = service.createPerson("Ali", "Jones", "xxx");

		//create Student
		Student student = null;

		String error = null;

		try {
			student = service.createStudent(person, (long)1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertEquals(1, service.getAllStudents().size());

		try {
			service.submitInternshipEvaluation(student, url, name);
		} catch (Exception e) {
			error = e.getMessage();
		}

		// Check error
		assertEquals("Student does not have an active Internship!", error);

	}



	//==testSubmitCourseEvaluation==//


	/*
	 * Check if correct error message is given
	 * when document has name == null
	 */
	@Test
	public void testSubmitCourseEvalNameNull() {
		//THIS TEST WORKS, DONT SCREW THIS UP
		ScheduledInternship scheduledInternship = service.createScheduledInternShip("GP", "Ubisoft", "FALL");

		assertEquals(0, service.getAllDocuments().size());

		String name = null;
		String url = "link.up";
		String error = null;

		Person person = service.createPerson("Ali", "Jones", "xxx");

		//create Student
		Student student = null;

		try {
			student = service.createStudent(person, (long)1);

			Offer offer = service.createOffer(student);
			service.validateOffer(offer);
			service.registerSpecificInternship(student, scheduledInternship, 2011);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} 

		try {
			service.submitCourseEvaluation(student, url, name);
		} catch (Exception e) {
			error = e.getMessage();
		}

		// check error
		assertEquals("The name of the document is invalid!", error);

		// check no change in memory
		assertEquals(0, service.getAllDocuments().size());
	}

	/*
	 * Check if correct error message is given
	 * when document has url == null
	 */
	@Test
	public void testSubmitCourseEvalURLNull() {
		//THIS TEST WORKS, DONT SCREW THIS UP
		ScheduledInternship scheduledInternship = service.createScheduledInternShip("GP", "Ubisoft", "FALL");
		assertEquals(0, service.getAllDocuments().size());

		String name = "bucky";
		String url = null;
		String error = null;

		Person person = service.createPerson("Ali", "Jones", "xxx");

		//create Student
		Student student = null;

		try {
			student = service.createStudent(person, (long)1);
			Offer offer = service.createOffer(student);
			service.validateOffer(offer);
			service.registerSpecificInternship(student, scheduledInternship, 2011);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		try {
			service.submitCourseEvaluation(student, url, name);
		} catch (Exception e) {
			error = e.getMessage();
		}

		// check error
		assertEquals("The URL of the document is invalid!", error);

		// check no change in memory
		assertEquals(0, service.getAllDocuments().size());
	}



	/*
	 * Check if submitIntershipEval is successfull
	 * with the correct params
	 */

	@Test
	public void testSubmitCourseEvaluation() {
		//THIS WORKS!

		assertEquals(0, service.getAllSpecificInternships().size());
		assertEquals(0, service.getAllStudents().size());
		assertEquals(0, service.getAllPersons().size());
		assertEquals(0, service.getAllDocuments().size());
		assertEquals(0, service.getAllScheduledInternships().size());

		//Doc details
		String url = "link.up";
		String name = "bucky";

		Person person = service.createPerson("Ali", "Jones", "xxx");

		//create Student
		Student student = null;

		try {
			student = service.createStudent(person, (long)1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(1, service.getAllStudents().size());


		//create SpecificInternship
		ScheduledInternship schedIn = service.createScheduledInternShip("HP", "Bob", "FALL"); 
		assertEquals(1, service.getAllScheduledInternships().size());

		Offer offer = null;
		try {
			offer = service.createOffer(student);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			fail();
		}
		service.validateOffer(offer);

		try {
			service.registerSpecificInternship(student, schedIn, 2019);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}

		Boolean check = null;

		try {
			check = service.submitCourseEvaluation(student, url, name);
		} catch (IllegalArgumentException e) {
			// Check that no error occurred
			e.printStackTrace();
			fail();
		}

		// Check method return
		assertEquals(true, check);

		assertEquals(1,service.getAllDocuments().size());

	}
	@Test
	public void testSubmitInternshipEvaluationDuplicateURL() {
		//THIS WORKS!

		assertEquals(0, service.getAllSpecificInternships().size());
		assertEquals(0, service.getAllStudents().size());
		assertEquals(0, service.getAllPersons().size());
		assertEquals(0, service.getAllDocuments().size());
		assertEquals(0, service.getAllScheduledInternships().size());

		//Doc details
		String url = "link.up";
		String name = "bucky";

		Person person = service.createPerson("Ali", "Jones", "xxx");

		//create Student
		Student student = null;

		try {
			student = service.createStudent(person, (long)1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(1, service.getAllStudents().size());


		//create SpecificInternship
		ScheduledInternship schedIn = service.createScheduledInternShip("HP", "Bob", "FALL"); 
		assertEquals(1, service.getAllScheduledInternships().size());

		Offer offer = null;
		try {
			offer = service.createOffer(student);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			fail();
		}
		service.validateOffer(offer);

		try {
			service.registerSpecificInternship(student, schedIn, 2019);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}

		String error = null;
		try {
			service.submitInternshipEvaluation(student, url, name);
			service.submitInternshipEvaluation(student, url, "tehboi");
		} catch (IllegalArgumentException e) {
			// Check that no error occurred
			error =e.getMessage();

		}

		// Check method return
		assertEquals("Error: Document is already uploaded", error );

		assertEquals(1,service.getAllDocuments().size());


	}
	@Test
	public void testSubmitCourseEvaluationDuplicateURL() {
		//THIS WORKS!

		assertEquals(0, service.getAllSpecificInternships().size());
		assertEquals(0, service.getAllStudents().size());
		assertEquals(0, service.getAllPersons().size());
		assertEquals(0, service.getAllDocuments().size());
		assertEquals(0, service.getAllScheduledInternships().size());

		//Doc details
		String url = "link.up";
		String name = "bucky";

		Person person = service.createPerson("Ali", "Jones", "xxx");

		//create Student
		Student student = null;

		try {
			student = service.createStudent(person, (long)1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(1, service.getAllStudents().size());


		//create SpecificInternship
		ScheduledInternship schedIn = service.createScheduledInternShip("HP", "Bob", "FALL"); 
		assertEquals(1, service.getAllScheduledInternships().size());

		Offer offer = null;
		try {
			offer = service.createOffer(student);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			fail();
		}
		service.validateOffer(offer);

		try {
			service.registerSpecificInternship(student, schedIn, 2019);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}

		String error = null;
		try {
			service.submitCourseEvaluation(student, url, name);
			service.submitCourseEvaluation(student, url, "tehboi");
		} catch (IllegalArgumentException e) {
			// Check that no error occurred
			error =e.getMessage();

		}

		// Check method return
		assertEquals("Error: Document is already uploaded", error );

		assertEquals(1,service.getAllDocuments().size());


	}

	/*
	 * Check if correct error message given
	 * when Student == null
	 */
	@Test
	public void testSubmitCourseEvaluationStudentInvalid() {

		assertEquals(0, service.getAllSpecificInternships().size());
		assertEquals(0, service.getAllStudents().size());
		assertEquals(0, service.getAllPersons().size());
		assertEquals(0, service.getAllDocuments().size());
		assertEquals(0, service.getAllScheduledInternships().size());

		//Doc details
		String url = "link.up";
		String name = "bucky";

		Person person = service.createPerson("Ali", "Jones", "xxx");

		//create Student
		Student student = null;

		String error = null;

		try {
			student = service.createStudent(person, (long)1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertEquals(1, service.getAllStudents().size());

		try {
			service.submitCourseEvaluation(student, url, name);
		} catch (Exception e) {
			error = e.getMessage();
		}

		// Check error
		assertEquals("The student does not have an active Internship!", error);

	}




	//==============================================================================================================



}
