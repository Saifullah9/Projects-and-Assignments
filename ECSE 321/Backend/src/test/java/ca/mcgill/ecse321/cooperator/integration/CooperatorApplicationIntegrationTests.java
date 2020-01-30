package ca.mcgill.ecse321.cooperator.integration;
import static org.junit.Assert.assertEquals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import ca.mcgill.ecse321.cooperator.dao.CooperatorSystemRepository;
import ca.mcgill.ecse321.cooperator.dao.DocumentRepository;
import ca.mcgill.ecse321.cooperator.dao.OfferRepository;
import ca.mcgill.ecse321.cooperator.dao.PersonRepository;
import ca.mcgill.ecse321.cooperator.dao.ScheduledInternshipRepository;
import ca.mcgill.ecse321.cooperator.dao.SpecificInternshipRepository;
import ca.mcgill.ecse321.cooperator.dao.StudentRepository;
import ca.mcgill.ecse321.cooperator.model.Offer;
import ca.mcgill.ecse321.cooperator.model.Person;
import ca.mcgill.ecse321.cooperator.model.ScheduledInternship;
import ca.mcgill.ecse321.cooperator.model.Student;
import ca.mcgill.ecse321.cooperator.service.CooperatorService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CooperatorApplicationIntegrationTests{


	@LocalServerPort
	private int port;

	private String host = "http://localhost:" ;

	@Autowired
	private TestRestTemplate restTemplate; //= new TestRestTemplate();

	private HttpHeaders headers = new HttpHeaders();


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

	@Before
	public void construct() throws Exception {
		
		Person person = service.createPerson("hamada", "hilaall", "saba7o");
		long studentId = 69696969;
		Student student = new Student();;
		
		 student = service.createStudent(person, studentId );
		ScheduledInternship sc = service.createScheduledInternShip("boy", "isnt", "FALL");
		Offer offer = service.createOffer(student);
		service.validateOffer(offer);
 		service.registerSpecificInternship(student, sc, 2020);

		
		
	}
	
	
	
	
	
	
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

/*	@Before
	public void initializeDataBase() {
		String firstName = "Database";
		String lastName = "Filler";
		String email = "database.filler@mail.mcgill.ca";
		
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		// Create Person
		String url = host + port + "/" + "persons/" + firstName;
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
				.queryParam("last", lastName)
				.queryParam("email", email);
		HttpEntity entity = new HttpEntity(headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.POST, entity, String.class);
		
		// Create Student
		
		
	}*/



	@Test
	public void testCreateStudent() {
		// initializa paramaters
		String firstName = "Hassan";
		String lastName = "Haidar";
		String email = "please.work@mail.mcgill.ca";
		long studentId = 123456789;

		headers.setContentType(MediaType.APPLICATION_JSON);

		String url = host + port + "/" + "create-student/" ;

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
				.queryParam("firstName", firstName)
				.queryParam("lastName", lastName)
				.queryParam("email", email)
				.queryParam("studentId", studentId);

		@SuppressWarnings({ })
		HttpEntity<?> entity = new HttpEntity<Object>(headers);

		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.POST, entity, String.class);
		response.getBody().toString(); 
		assertEquals(HttpStatus.OK, response.getStatusCode());

	}
	
	@Test
	public void testCreateOffer() {
		// initializa paramaters
		
		//CREATE STUDENT
		String firstName = "Hassan";
		String lastName = "Haidar";
		String email = "please.work@mail.mcgill.ca";
		long studentId = 123456789;

		headers.setContentType(MediaType.APPLICATION_JSON);

		String url_person = host + port + "/" + "create-student/" ;
		UriComponentsBuilder builder_student = UriComponentsBuilder.fromHttpUrl(url_person)
				.queryParam("firstName", firstName)
				.queryParam("lastName", lastName)
				.queryParam("email", email)
				.queryParam("studentId", studentId);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		ResponseEntity<String> response_student = restTemplate.exchange(
				builder_student.toUriString(),HttpMethod.POST, entity, String.class);
		String result_student= response_student.getBody().toString();
		System.out.print(result_student);
		assertEquals(HttpStatus.OK, response_student.getStatusCode());
		

		// CREATE OFFER
		String url_offer = host + port + "/" + "123456789/create-offer/" ;		
		UriComponentsBuilder builder_offer = UriComponentsBuilder.fromHttpUrl(url_offer);
		ResponseEntity<String> response_offer= restTemplate.exchange(
				builder_offer.toUriString(),HttpMethod.POST, entity, String.class);
		response_offer.getBody().toString();
		assertEquals(HttpStatus.OK, response_offer.getStatusCode());
		
		
		// VALIDATE OFFER
		String url_validate = host + port + "/" + "123456789/" + "validate-offer";
		UriComponentsBuilder builder_validate= UriComponentsBuilder.fromHttpUrl(url_validate);
		ResponseEntity<String> response_validate = restTemplate.exchange(
				builder_validate.toUriString(),HttpMethod.POST, entity, String.class);
		String result_validate = response_validate.getBody().toString();
		System.out.print(result_validate);
		assertEquals(HttpStatus.OK, response_validate.getStatusCode());
		
		// REGISTER SPECIFIC INTERNSHIP
		String url_register = host + port + "/" + "123456789/" + "register-internship";
		UriComponentsBuilder builder_register= UriComponentsBuilder.fromHttpUrl(url_register)
				.queryParam("scheduledInternship", "ABC123")
				.queryParam("name", "General Programmer")
				.queryParam("employer", "Ubisoft")
				.queryParam("term", "FALL")
				.queryParam("year", 2020);
		ResponseEntity<String> response_register= restTemplate.exchange(
				builder_register.toUriString(),HttpMethod.POST, entity, String.class);
		response_register.getBody().toString();
		assertEquals(HttpStatus.OK, response_register.getStatusCode());

	}
	@Test
	public void testSubmitInternshipDoc(){
		// initializa paramaters
		
		//CREATE STUDENT
		String firstName = "Hassan";
		String lastName = "Haidar";
		String email = "veryplebsdpiufahnqwase.work@mail.mcgill.ca";
		long studentId = 123456789;

		headers.setContentType(MediaType.APPLICATION_JSON);

		String url_student = host + port + "/" + "create-student/" ;
		UriComponentsBuilder builder_student = UriComponentsBuilder.fromHttpUrl(url_student)
				.queryParam("firstName", firstName)
				.queryParam("lastName", lastName)
				.queryParam("email", email)
				.queryParam("studentId", studentId);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		ResponseEntity<String> response_student = restTemplate.exchange(
				builder_student.toUriString(),HttpMethod.POST, entity, String.class);
		String result_student= response_student.getBody().toString();
		System.out.print(result_student);
		assertEquals(HttpStatus.OK, response_student.getStatusCode());
		

		// CREATE OFFER
		String url_offer = host + port + "/" + "123456789/create-offer/" ;		
		UriComponentsBuilder builder_offer = UriComponentsBuilder.fromHttpUrl(url_offer);
		ResponseEntity<String> response_offer= restTemplate.exchange(
				builder_offer.toUriString(),HttpMethod.POST, entity, String.class);
		response_offer.getBody().toString();
		assertEquals(HttpStatus.OK, response_offer.getStatusCode());
		
		
		// VALIDATE OFFER
		String url_validate = host + port + "/" + "123456789/" + "validate-offer";
		UriComponentsBuilder builder_validate= UriComponentsBuilder.fromHttpUrl(url_validate);
		ResponseEntity<String> response_validate = restTemplate.exchange(
				builder_validate.toUriString(),HttpMethod.POST, entity, String.class);
		String result_validate = response_validate.getBody().toString();
		System.out.print(result_validate);
		assertEquals(HttpStatus.OK, response_validate.getStatusCode());
		
		// REGISTER SPECIFIC INTERNSHIP
		String url_register = host + port + "/" + "123456789/" + "register-internship";
		UriComponentsBuilder builder_register= UriComponentsBuilder.fromHttpUrl(url_register)
				.queryParam("scheduledInternship", "ABC123")
				.queryParam("name", "General Programmer")
				.queryParam("employer", "Ubisoft")
				.queryParam("term", "FALL")
				.queryParam("year", 2020);
		ResponseEntity<String> response_register= restTemplate.exchange(
				builder_register.toUriString(),HttpMethod.POST, entity, String.class);
		response_register.getBody().toString();
		assertEquals(HttpStatus.OK, response_register.getStatusCode());
		
		String docUrl = "please.work@drive.google.ca";
		String docName = "please.work@drive.google.ca";
				
		String urldoc = host + port + "/123456789/specific-internship/upload-doc/" ;
		UriComponentsBuilder builder_submitdoc = UriComponentsBuilder.fromHttpUrl(urldoc)
		.queryParam("documentURL", docUrl)
		.queryParam("documentName", docName);
		ResponseEntity<String> response_submitdoc= restTemplate.exchange(
				builder_submitdoc.toUriString(),HttpMethod.POST, entity, String.class);
		assertEquals(HttpStatus.OK, response_submitdoc.getStatusCode());
		
		
		
	}
	
	

	@Test
	public void testgetStudent() {

		long studentId = 69696969 ;

		headers.setContentType(MediaType.APPLICATION_JSON);

		String url_person = host + port + "/" + "getStudent/" + studentId;
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url_person);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
		builder.toUriString(),HttpMethod.GET, entity, String.class);
		String result = response.getBody().toString(); 
		System.out.print(result);
		JSONObject obj = null;
		try {
			obj = new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String testID = "-1";
		try { 
			testID = obj.getString("studentID");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(testID);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("69696969",testID);
		
	}
		

	
	
		
	@Test
	public void testgetTermsFinished() {

		long studentId = 69696969 ;

		headers.setContentType(MediaType.APPLICATION_JSON);

		String url_person = host + port + "/" + "getTermsFinished/" + studentId;
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url_person);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
		builder.toUriString(),HttpMethod.GET, entity, String.class);
		String result = response.getBody().toString(); 
		System.out.print(result);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("0",result);
	}

	
	
	@Test
	public void testgetTermsRemaining() {

		long studentId = 69696969 ;

		headers.setContentType(MediaType.APPLICATION_JSON);

		String url_person = host + port + "/" + "getTermsRemaining/" + studentId;
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url_person);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
		builder.toUriString(),HttpMethod.GET, entity, String.class);
		String result = response.getBody().toString(); 
		System.out.println(result);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("4",result);
	}

	
	
	@Test
	public void testgetAllInternships() {

		long studentId = 69696969 ;

		headers.setContentType(MediaType.APPLICATION_JSON);

		String url_person = host + port + "/" + "getAllInternships/" ;
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url_person)
				.queryParam("Student", studentId);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
		builder.toUriString(),HttpMethod.GET, entity, String.class);
		String result = response.getBody().toString();
		JSONArray obj = null;
		JSONObject student1= null;
		try {
			obj = new JSONArray(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String n = null;
		try {
			n = obj.get(0).toString();
			student1 = new JSONObject(n);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String testID = "-1";
		try { 
			testID = student1.getString("studentId");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(testID);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("69696969",testID);
		
	}
	@Test
	public void testSubmitOfferDoc(){
		// initialize paramaters

		//CREATE STUDENT
		String firstName = "Hassan";
		String lastName = "Haidar";
		String email = "please.work@mail.mcgill.ca";
		long studentId = 123456789;

		headers.setContentType(MediaType.APPLICATION_JSON);

		String url_person = host + port + "/" + "create-student/" ;
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url_person)
				.queryParam("firstName", firstName)
				.queryParam("lastName", lastName)
				.queryParam("email", email)
				.queryParam("studentId", studentId);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.POST, entity, String.class);
		response.getBody().toString(); 
		assertEquals(HttpStatus.OK, response.getStatusCode());





		// use id of a student that is already stored in the database
		String url = host + port + "/" + "/123456789/create-offer/" ;		
		UriComponentsBuilder builder_offer = UriComponentsBuilder.fromHttpUrl(url);
		ResponseEntity<String> response_offer= restTemplate.exchange(
				builder_offer.toUriString(),HttpMethod.POST, entity, String.class);
		String result_offer= response_offer.getBody().toString(); 
		System.out.print(result_offer);
		assertEquals(HttpStatus.OK, response_offer.getStatusCode());
		
		String docUrl = "please.work@drive.google.ca";
		String docName = "please.work@drive.google.ca";
				
		String urldoc = host + port + "/123456789/offer/upload-doc/" ;
		UriComponentsBuilder builder_submitdoc = UriComponentsBuilder.fromHttpUrl(urldoc)
		.queryParam("documentURL", docUrl)
		.queryParam("documentName", docName);
		ResponseEntity<String> response_submitdoc= restTemplate.exchange(
				builder_submitdoc.toUriString(),HttpMethod.POST, entity, String.class);
		assertEquals(HttpStatus.OK, response_submitdoc.getStatusCode());
		
		
		
	}

}
