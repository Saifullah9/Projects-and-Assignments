package ca.mcgill.ecse321.cooperator;


import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.quality.Strictness;
import org.junit.Test; 
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.quality.Strictness;
import org.mockito.Incubating;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.mcgill.ecse321.cooperator.dao.CooperatorSystemRepository;
import ca.mcgill.ecse321.cooperator.dao.DocumentRepository;
import ca.mcgill.ecse321.cooperator.dao.OfferRepository;
import ca.mcgill.ecse321.cooperator.dao.PersonRepository;
import ca.mcgill.ecse321.cooperator.dao.ScheduledInternshipRepository;
import ca.mcgill.ecse321.cooperator.dao.SpecificInternshipRepository;
import ca.mcgill.ecse321.cooperator.dao.StudentRepository;
import ca.mcgill.ecse321.cooperator.model.CooperatorSystem;
import ca.mcgill.ecse321.cooperator.model.Document;
import ca.mcgill.ecse321.cooperator.model.Offer;
import ca.mcgill.ecse321.cooperator.model.Person;
import ca.mcgill.ecse321.cooperator.model.ScheduledInternship;
import ca.mcgill.ecse321.cooperator.model.SpecificInternship;
import ca.mcgill.ecse321.cooperator.model.Student;
import ca.mcgill.ecse321.cooperator.model.Term;
import ca.mcgill.ecse321.cooperator.service.CooperatorService;

import org.mockito.invocation.InvocationOnMock;
import org.springframework.test.context.junit4.SpringRunner;
import ca.mcgill.ecse321.cooperator.controller.CooperatorRestController;

@SuppressWarnings("unused")
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class CooperatorApplicationTests {
	@InjectMocks
	private CooperatorService service;

	@Mock
	private CooperatorService serviceMock;

	@Mock
	private CooperatorSystemRepository coopDao;

	@Mock
	private PersonRepository personDao;

	@Mock
	private StudentRepository studentDao;

	@Mock
	private SpecificInternshipRepository specificInternshipDao;

	@Mock
	private ScheduledInternshipRepository scheduledInternshipDao;

	@Mock
	private DocumentRepository documentDao;

	@Mock
	private OfferRepository offerDao;

	@InjectMocks
	private CooperatorRestController controller;
	
	
	private MockMvc mockMvc;

	private Person person;
	private Person personDuplicate;
	private Student student;

	private Student student1;
	private Student student2;
	private Student studentDuplicate;
	private Student studentWithSI;
	private ScheduledInternship scheduledInternship;
	private Offer offer;
	private Document document;
	private SpecificInternship si;

	private SpecificInternship specificInternship;
	private static final String PERSON_KEY = "email@email.com";
	private static final String NONEXISTING_KEY = "NotAPerson";
	private static final Integer  DOCUMENT_KEY = 315384;
	private static final Long STUDENT_KEY =  (long) 260733168;
	private static final String SPECIFICINTERNSHIP_KEY =  "internshipId";
	private static final String URL_KEY =  "pleasehelp.gov";
	



	@Before
	public void setupMock() {
		MockitoAnnotations.initMocks(this);
		person = mock(Person.class);
		personDuplicate = mock(Person.class);
		student = mock(Student.class);
		student1 = mock(Student.class);
		student2 = mock(Student.class);
		studentDuplicate = mock(Student.class);
		scheduledInternship = mock(ScheduledInternship.class);
		offer = mock(Offer.class);

		document = mock(Document.class);
		si = mock(SpecificInternship.class);
		specificInternship = mock(SpecificInternship.class);
		studentWithSI = mock(Student.class);


		//Whenever anything is saved, just return a null value object of that kind
		Mockito.when(personDao.save(any(Person.class))).thenReturn(new Person());
		Mockito.when(studentDao.save(any(Student.class))).thenReturn(new Student());
		Mockito.when(specificInternshipDao.save(any(SpecificInternship.class))).thenReturn(new SpecificInternship());
		Mockito.when(scheduledInternshipDao.save(any(ScheduledInternship.class))).thenReturn(new ScheduledInternship());
		Mockito.when(offerDao.save(any(Offer.class))).thenReturn(new Offer());
		Mockito.when(documentDao.save(any(Document.class))).thenReturn(new Document());


		Mockito.when(scheduledInternshipDao.count()).thenReturn((long) 1);

	}


	@Before
	public void setMockOutputPerson() {
		when(personDao.findPersonByEmail(anyString())).thenAnswer( (InvocationOnMock invocation) -> {
			if(invocation.getArgument(0).equals(PERSON_KEY)){
				Person person = new Person();
				person.setEmail(PERSON_KEY);
				return person;
			} else {
				return null;
			}
		});
	}

	@Before
	public void setMockOutputStudentById() {
		when(studentDao.findByStudentId(anyLong())).thenAnswer( (InvocationOnMock invocation) -> {
			if(invocation.getArgument(0).equals(STUDENT_KEY)){
				Student student = new Student();
				return student;
			} else {
				return null;
			}
		});
	}

	@Before
	public void setMockOutputStudentByPerson() {
		when(studentDao.findStudentByPerson(any(Person.class))).thenAnswer( (InvocationOnMock invocation) -> {
			if(invocation.getArgument(0).equals(personDuplicate)){
				Student student = new Student();
				student.setPerson(personDuplicate);
				return student;
			} else {
				return null;
			}
		});
	}

	@Before
	public void setMockOutputFindOfferByStudent() {
		when(offerDao.findOfferByStudentAndIsActiveTrue(any(Student.class))).thenAnswer( (InvocationOnMock invocation) -> {
			if(invocation.getArgument(0).equals(studentDuplicate)){
				Offer offer = new Offer();
				offer.setStudent(studentDuplicate);
				return offer;
			} else {
				return null;
			}
		});
	}

	@Before
	public void setMockOutputCountByStudent() {
		when(offerDao.countByStudentAndIsValidatedTrue(any(Student.class))).thenAnswer( (InvocationOnMock invocation) -> {
			if(invocation.getArgument(0).equals(student) || invocation.getArgument(0).equals(studentWithSI)){
				return 1;
			} else {
				return null;
			}
		});
	}


	@Before
	public void setMockOutputSpecificInternship() {
		when(specificInternshipDao.findByStudentAndIsActiveTrue(any(Student.class))).thenAnswer( (InvocationOnMock invocation) -> {
			if(invocation.getArgument(0).equals(studentWithSI)){
				SpecificInternship si =  new SpecificInternship();
				si.setIsActive(true);
				si.setStudent(studentWithSI);
				Set<Document> reports = new HashSet<Document>();
				si.setReports(reports);
				return si;
			} else {
				return null;
			}
		});
	}


	
	@Before
	public void setMockOutputCountByStudent2() {
		when(specificInternshipDao.countByStudent(any(Student.class))).thenAnswer( (InvocationOnMock invocation) -> {
			if(invocation.getArgument(0).equals(student) || invocation.getArgument(0).equals(studentDuplicate)|| invocation.getArgument(0).equals(student1) || invocation.getArgument(0).equals(student2)){
				return 0;
			} else {
				return null;
			}
		});
	}
	//findSpecificInternshipByInternshipId
	@Before
	public void setMockOutputInternshipByID() {
		when(specificInternshipDao.findSpecificInternshipByInternshipId(anyString())).thenAnswer( (InvocationOnMock invocation) -> {
			if(invocation.getArgument(0).equals(SPECIFICINTERNSHIP_KEY)){
				SpecificInternship SI = new SpecificInternship();
				SI.setInternshipId(SPECIFICINTERNSHIP_KEY);
				return SI;
			} else {
				return null;
			}
		});
	}
	//findByDocumentURL
	
	@Before
	public void setMockOutputDocument() {
		when(documentDao.findByDocumentId(anyInt())).thenAnswer( (InvocationOnMock invocation) -> {
			if(invocation.getArgument(0).equals(DOCUMENT_KEY)){
				Document document = new Document();
				document.setDocumentId(DOCUMENT_KEY);
				return document;
			} else {
				return null;
			}
		});
	}
	@Before
	public void setMockOutputDocumentByURL() {
		when(documentDao.findByDocumentURL(anyString())).thenAnswer( (InvocationOnMock invocation) -> {
			if(invocation.getArgument(0).equals(URL_KEY)){
				Document document = new Document();
				document.setDocumentURL(URL_KEY);
				return document;
			} else {
				return null;
			}
		});
	}


	//======================================End of Initialization==================================

	@Test
	public void testCreatePerson() {

		String firstName = "Hassan";
		String lastName = "Haidar";
		String email = "please.work@mail.mcgill.ca";

		try {
			person = service.createPerson(firstName, lastName, email);
		} catch (IllegalArgumentException e) {
			fail();
		}
		assertEquals(firstName, person.getFirst());
		assertEquals(lastName, person.getLast());
		assertEquals(email, person.getEmail());
	}

	@Test
	public void testCreatePersonNullName() {

		String error = null;

		String firstName = null;
		String lastName = null;

		String email = PERSON_KEY;

		try {
			person = service.createPerson(firstName, lastName, email);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}

		// check error
		assertEquals("Person name can't be empty", error);

	}

	@Test
	public void testCreatePersonNullEmail() {
		String error = null;

		String firstName = "Hassan";
		String lastName = "Haidar";

		String email = null;

		try {
			person = service.createPerson(firstName, lastName, email);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}

		// check error
		assertEquals("Email can't be empty", error);
	}

	@Test
	public void testCreatePersonSpaceName() {

		String error = null;

		String firstName = "   ";
		String lastName = "Haidar";

		String email = "please.work@mail.mcgill.ca";

		try {
			person = service.createPerson(firstName, lastName, email);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}

		// check error
		assertEquals("Person name can't be empty", error);
	}

	@Test
	public void testCreatePersonSpaceEmail() {
		assertEquals(0, service.getAllPersons().size());

		String error = null;

		String firstName = "Hassan";
		String lastName = "Haidar";

		String email = "    ";

		try {
			person = service.createPerson(firstName, lastName, email);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}

		// check error
		assertEquals("Email can't be empty", error);
	}

	@Test
	public void testCreateMultiplePersonsDuplicateEmails() {
		String error = null;

		String firstName = "Hassan";
		String lastName = "Haidar";
		String email = PERSON_KEY;

		try {
			service.createPerson(firstName, lastName, email);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertEquals("Error: Email is already used!", error);
	}

	@Test
	public void testCreateStudent() {

		try {
			person = service.createPerson("Hassan", "Haidar", "pleasework@mail.mcgill.ca");
			student = service.createStudent(person, (long)123456);
		} catch (Exception e) {
			fail();
		}

		assertEquals(123456, student.getStudentId());

	}

	@Test
	public void testCreateMultipleStudentsDupilicatePerson() {
		String error = null;
		assertEquals(0, service.getAllStudents().size());
		try {
			personDuplicate = service.createPerson("Hassan", "Haidar", "pleasework@mail.mcgill.ca");
			student = service.createStudent(personDuplicate, (long) 12345678);

			// should throw error


		} catch (Exception e) {
			error = e.getMessage();
		}

		assertEquals("Error: User is already a student!", error);
	}

	@Test
	public void testCreateMultipleStudentsDupilicateId() {
		String error = null;
		try {
			person = service.createPerson("Hassan", "Haidar", "pleasework@mail.mcgill.ca");
			student = service.createStudent(person, (long)STUDENT_KEY);
			// should throw error

		} catch (Exception e) {
			error = e.getMessage();
		}
		assertEquals("Error: User already has this id", error);
	}

	@Test
	public void testCreateScheduledInternship() {
		try {
			scheduledInternship = service.createScheduledInternShip("name", "employer", "FALL");
		} catch (Exception e) {
			fail();
		}
		assertEquals("name", scheduledInternship.getName());
	}

	@Test
	public void testCreateOffer() {
		assertEquals(0, service.getAllOffers().size());
		person = service.createPerson("Hassan", "Haidar", "please.work@mail.mcgill.ca");
		try {
			student = service.createStudent(person, (long)12345678);
			offer = service.createOffer(student);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertEquals(student, offer.getStudent());
	}

	@Test
	public void testCreateAnOfferForTwoStudents() {
		String error = null;
		Person person = service.createPerson("Hassan", "Haidar", "please.work@mail.mcgill.ca");
		try {
			studentDuplicate = service.createStudent(person, (long) 12345678);
			offer = service.createOffer(studentDuplicate);
		} catch (Exception e) {
			error = e.getMessage();
		}
		assertEquals("Student can't have two active offers", error);
	}

	@Test
	public void testCreateOfferNullStudent() {
		String error = null;
		student = null;
		try {
			offer = service.createOffer(student);
		} catch (Exception e) {
			error = e.getMessage();
		}
		assertEquals("Error:Student can't be null!", error);
	}

	@Test
	public void testUpdateOffer() {
		String url = "https://www.onedrive.com/document";
		String name = "Contract";
		person = service.createPerson("Hassan", "Haidar", "please.work@mail.mcgill.ca");
		try {
			student = service.createStudent(person, (long) 12345678);
			offer = service.createOffer(student);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try {
			document = service.updateOffer(offer, url, name);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		assertEquals(1, offer.getDocuments().size());

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
		Student student = null;
		Offer offer = null;
		try {
			student = service.createStudent(person, (long) 12345678);
			offer = service.createOffer(student);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		Document document1 = null;
		Document document2 = null;

		try {
			document1 = service.updateOffer(offer, url1, name1);
			document2 = service.updateOffer(offer, url2, name2);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		Document document3 = null;
		try {
			document3 = service.updateOffer(offer, url3, name3);
		} catch (Exception e) {
			error = e.getMessage();
		}

		// assert the the document was persisted
		// assert that number of offers stays the same
		assertEquals(2, offer.getDocuments().size());

		// assert that we get the right error message
		assertEquals("Student already has two documents associated with this offer. "
				+ "Please delete the document you wish to replace.", error);

	}

	@Test
	public void testRegisterSpecificInternship() {
		scheduledInternship = service.createScheduledInternShip("GP", "Ubi", "SUMMER");
		person = service.createPerson("Hassan", "Haidar", "please.work@mail.mcgill.ca");
		try {
			student = service.createStudent(person, (long) 12345678);
			offer = service.createOffer(student);
			// ==========THIS METHOD CALL PLAYS THE ROLE OF THE ADMIN===================
			offer.setIsActive(false);
			offer.setIsValidated(true);
			// ==========================================================================

			specificInternship = service.registerSpecificInternship(student, scheduledInternship, 2018);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		assertEquals(student, specificInternship.getStudent());
	}

	@Test
	public void testRegisterSpecificInternshipNoValidOffer() {
		String error = null;
		scheduledInternship = service.createScheduledInternShip("GP", "Ubi", "SUMMER");
		person = service.createPerson("Hassan", "Haidar", "please.work@mail.mcgill.ca");
		try {
			studentDuplicate = service.createStudent(person, (long) 12345678);
			specificInternship = service.registerSpecificInternship(studentDuplicate, scheduledInternship, 2018);

		} catch (Exception e) {
			error = e.getMessage();
		}
		assertEquals("Student does not have a valid offer.", error);
	}

	@Test
	public void testSubmitCourseEvalNameNull() {
		//THIS TEST WORKS, DONT SCREW THIS UP
		String name = null;
		String url = "link.up";
		String error = null;
		try {
			service.submitCourseEvaluation(studentWithSI, url, name);
		} catch (Exception e) {
			error = e.getMessage();
		}
		// check error
		assertEquals("The name of the document is invalid!", error);
	}

	@Test
	public void testMockDocumentCreation() {
		assertNotNull(document);
	}

	@Test
	public void testDocumentQueryFound() {
		assertEquals(DOCUMENT_KEY, (service.getDocument(DOCUMENT_KEY)).getDocumentId());
	}

	@Test
	public void testMockPersonCreation() {
		assertNotNull(person);
	}

	@Test
	public void testPersonQueryFound() {
		assertEquals(PERSON_KEY, service.getPerson(PERSON_KEY).getEmail());
	}

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
		Student student = null;
		String error = null;

		try {
			service.getTermsremaining(student);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}

		// check error
		assertEquals(" The student does not exist!", error);
	}

	@Test
	public void testGetTermsRemaining() {

		Student student = new Student();
		student.setTermsRemaining(0);

		try {
			service.getTermsremaining(student);
		} catch (IllegalArgumentException e) {
			//check that no error occurred
			fail();
		}


		// check terms is 0 as we set
		assertEquals(0, service.getTermsremaining(student));

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
	public void testGetYearOfInternshipNull()  {
		Person person = service.createPerson("Saif", "haa", "edy");
		try {
			student = service.createStudent(person,(long) 260733169);
		} catch (Exception e1) {

			e1.printStackTrace();
		}
		ScheduledInternship sc = service.createScheduledInternShip("hamada", "basha", "FALL");
		SpecificInternship internship = null;
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
		scheduledInternship = service.createScheduledInternShip("GP", "Ubi", "SUMMER");
		person = service.createPerson("Hassan", "Haidar", "please.work@mail.mcgill.ca");
		try {
			student = service.createStudent(person,(long) 12345678);
			offer = service.createOffer(student);
			// ==========THIS METHOD CALL PLAYS THE ROLE OF THE ADMIN===================
			offer.setIsActive(false);
			offer.setIsValidated(true);
			// ==========================================================================

			specificInternship = service.registerSpecificInternship(student, scheduledInternship, 2018);
			specificInternship.setInternshipId(SPECIFICINTERNSHIP_KEY);
			specificInternship.setIsCompleted(false);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		int year = 0;
		String error = null;
		try {
			year =	service.getYearOfInternship(specificInternship);
		} catch (Exception e) {
			e.printStackTrace();

			fail();

		}
		assertEquals(2018, year);
	}

	@Test
	public void testGetIsCompletedInternshipNull() {

		
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
	public void testGetIsCompleted() throws Exception {

		scheduledInternship = service.createScheduledInternShip("GP", "Ubi", "SUMMER");
		person = service.createPerson("Hassan", "Haidar", "please.work@mail.mcgill.ca");
		try {
			student = service.createStudent(person,(long) 12345678);
			offer = service.createOffer(student);
			// ==========THIS METHOD CALL PLAYS THE ROLE OF THE ADMIN===================
			offer.setIsActive(false);
			offer.setIsValidated(true);
			// ==========================================================================

			specificInternship = service.registerSpecificInternship(student, scheduledInternship, 2018);
			specificInternship.setIsCompleted(false);
			specificInternship.setInternshipId(SPECIFICINTERNSHIP_KEY);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		boolean isComplete = true;
		try {
			isComplete = service.getIsCompleted(specificInternship);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertEquals( false, isComplete);
	}

	@Test
	public void testSubmitInternshipEvalURLNull() {
		//THIS TEST WORKS, DONT SCREW THIS UP


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
			fail();
		} 

		try {
			service.submitInternshipEvaluation(student, url, name);
		} catch (Exception e) {
			error = e.getMessage();
		}

		// check error
		assertEquals("The URL of the document is invalid!", error);

	}


	@Test
	public void testSubmitInternshipEvaluation() {

		String url = "link.up";
		String name = "bucky";

		Boolean check = null;

		try {
			check = service.submitInternshipEvaluation(studentWithSI, url, name);
		} catch (IllegalArgumentException e) {
			// Check that no error occurred
			e.printStackTrace();
			fail();
		}

		// Check method return
		assertEquals(true, check);
	}


	/*
	 * Check if correct error message given
	 * when Student == null
	 */
	@Test
	public void testSubmitInternshipEvaluationStudentInvalid() {


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
			fail();
		}


		try {
			service.submitInternshipEvaluation(student, url, name);
		} catch (Exception e) {
			error = e.getMessage();
		}

		// Check error
		assertEquals("Student does not have an active Internship!", error);

	}
	
	@Test
	public void testSubmitCourseEvalURLNull() {
		//THIS TEST WORKS, DONT SCREW THIS UP

		String name = "bucky";
		String url = null;
		String error = null;

		scheduledInternship = service.createScheduledInternShip("GP", "Ubi", "SUMMER");
		person = service.createPerson("Hassan", "Haidar", "please.work@mail.mcgill.ca");
		try {
			studentWithSI = service.createStudent(person, (long)12345678);
			offer = service.createOffer(student);
			// ==========THIS METHOD CALL PLAYS THE ROLE OF THE ADMIN===================
			offer.setIsActive(false);
			offer.setIsValidated(true);
			// ==========================================================================

			specificInternship = service.registerSpecificInternship(student, scheduledInternship, 2018);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		try {
			service.submitCourseEvaluation(studentWithSI, url, name);
		} catch (Exception e) {
			error = e.getMessage();
		}

		// check error
		assertEquals("The URL of the document is invalid!", error);

		// check no change in memory
	}

	@Test
	public void testSubmitCourseEvaluation() {
		String url = "link.up";
		String name = "bucky";
		Boolean check = null;

		try {
			check = service.submitCourseEvaluation(studentWithSI, url, name);
		} catch (IllegalArgumentException e) {
			// Check that no error occurred
			e.printStackTrace();
			fail();
		}

		// Check method return
		assertEquals(true, check);

	}
	
	@Test
	public void testSubmitInternshipEvaluationDuplicateURL() {
		
		String name = "bucky";
		Boolean check = null;
		String error = null;
		try {
			check = service.submitInternshipEvaluation(studentWithSI, URL_KEY, name);
		} catch (IllegalArgumentException e) {
			// Check that no error occurred
			error =e.getMessage();

		}
		// Check method return
		assertEquals("Error: Document is already uploaded", error );

	}
	
	@Test
	public void testSubmitCourseEvaluationDuplicateURL() {

		String name = "bucky";
		

		Boolean check = null;
		String error = null;
		try {
			check = service.submitCourseEvaluation(studentWithSI, URL_KEY, name);
		} catch (IllegalArgumentException e) {
			// Check that no error occurred
			error = e.getMessage();
		}

		// Check method return
		assertEquals("Error: Document is already uploaded", error );

	}
	
	@Test
	public void testCreateDocDuplicateURL() {
		String name = "yolo";
		String error = null;
		try {
			service.createDoc(URL_KEY, name);
		}
		catch(IllegalArgumentException e){
			error = e.getMessage();
		}
		assertEquals("Error: Document is already uploaded", error );

	}

	@Test
	public void testSubmitCourseEvaluationStudentInvalid() {
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
			fail();
		}

		try {
			service.submitCourseEvaluation(student, url, name);
		} catch (Exception e) {
			error = e.getMessage();

		}

		// Check error
		assertEquals("The student does not have an active Internship!", error);
	}
	
	@Test
	public void contextLoads() {
	}
	
}