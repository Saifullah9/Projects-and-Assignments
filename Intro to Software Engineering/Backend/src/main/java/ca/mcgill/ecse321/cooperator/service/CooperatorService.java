package ca.mcgill.ecse321.cooperator.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


@Service
public class CooperatorService {

	@Autowired
	CooperatorSystemRepository cooperatorSystemRepository;
	@Autowired
	OfferRepository offerRepository;
	@Autowired
	PersonRepository personRepository;
	@Autowired
	DocumentRepository documentRepository;
	@Autowired
	ScheduledInternshipRepository scheduledInternshipRepository;
	@Autowired
	SpecificInternshipRepository specificInternshipRepository;
	@Autowired
	StudentRepository studentRepository;
	


	/*@Transactional
	public CooperatorSystem createCooperatorSystem() {
		CooperatorSystem cs = new CooperatorSystem();
		cs.setSystemId((int)(cooperatorSystemRepository.count()));
		cooperatorSystemRepository.save(cs);
		return cs;
	}*/

	private static final Random RANDOM = new SecureRandom();
	private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	/**
	 * method that creates person
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @return Person
	 */
	@Transactional
	public Person createPerson(String firstName, String lastName, String email) {
		if (firstName == null || firstName.trim().length() == 0  || 
				lastName == null || lastName.trim().length() == 0 ) {
			throw new IllegalArgumentException("Person name can't be empty");
		}

		if (email == null || email.trim().length() == 0 ) {
			throw new IllegalArgumentException("Email can't be empty");
		}

		if (personRepository.findPersonByEmail(email) != null) {
			throw new IllegalArgumentException("Error: Email is already used!");
		}



		Person person = new Person();
		person.setFirst(firstName);
		person.setLast(lastName);
		person.setEmail(email);
		personRepository.save(person);

		return person;

	}

	/**
	 * create student from person
	 * @param person
	 * @param studentId
	 * @return student
	 * @throws Exception
	 */
	@Transactional
	public Student createStudent(Person person, Long studentId) throws Exception{
		if (studentId == null) {
			throw new IllegalArgumentException("Student ID can't be empty");
		}

		if (person == null) {
			throw new Exception("Person can't be empty");
		}


		// throw exception if person is already a student
		if (studentRepository.findStudentByPerson(person) != null) {
			throw new Exception("Error: User is already a student!");
		}


		// throw exception if person is already a student
		if (studentRepository.findByStudentId(studentId) != null) {
			throw new Exception("Error: User already has this id");
		}

		Student student = new Student();
		student.setOffers(new HashSet<Offer>());
		student.setSpecificInternships(new HashSet<SpecificInternship>());

		student.setStudentId(studentId);
		student.setTermsFinished(0);
		student.setTermsRemaining(4);
		student.setPassword(generatePassword(6));

		student.setPerson(person);
		person.setStudent(student);	

		studentRepository.save(student);
		personRepository.save(person);

		return student;
	}
	/**
	 * this method generates a random password with a specified length
	 * @param length
	 * @return
	 */
	@Transactional
	 public String generatePassword(int length) {
	        StringBuilder returnValue = new StringBuilder(length);
	        for (int i = 0; i < length; i++) {
	            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
	        }
	        return new String(returnValue);
	    }
	/**
	 * delete student by id
	 * @param id
	 * @return Student
	 * @throws IllegalArgumentException
	 */
	@Transactional
	public Student deleteStudentByID(int id) throws IllegalArgumentException {
		Student student = studentRepository.findByStudentId(id);
		if(student == null) {
			throw new IllegalArgumentException("Student with that ID is not found!");
		}

		Person person = personRepository.findByStudent(student);
		studentRepository.delete(student);
		personRepository.delete(person);
		
		return student;
	}

	/**
	 * create scheduled internship
	 * @param name
	 * @param employer
	 * @param term
	 * @return scheduled internship
	 */
	@Transactional
	public ScheduledInternship createScheduledInternShip(String name, String employer, String term) {
		if (name == null || name.trim().length() == 0  || 
				employer == null || employer.trim().length() == 0 ||
				term == null ) {
			throw new IllegalArgumentException("Atleast one of the field are empty can't be empty");
		}
		if (!term.equals("FALL") && !term.equals("WINTER") && !term.equals("SUMMER")) {
			throw new IllegalArgumentException("Invalid value for term");
		}

		ScheduledInternship scheduledInternship = new ScheduledInternship();
		scheduledInternship.setName(name);
		scheduledInternship.setEmployer(employer);
		scheduledInternship.setTerm(Term.valueOf(term));
		scheduledInternship.setSpecificInternships(new HashSet<SpecificInternship>());
		scheduledInternship.setId((int)scheduledInternshipRepository.count() + 1);
		scheduledInternship.setPositionId(employer.concat(name) + "_" + scheduledInternship.getId());

		scheduledInternshipRepository.save(scheduledInternship);

		return scheduledInternship;
	}



	@Transactional
	/**
	 * Create offer for a student
	 * @param student
	 * @return
	 */
	public Offer createOffer(Student student) throws Exception {

		if (student == null) {
			throw new Exception("Error:Student can't be null!");
		}


		// check if there is already an active offer
		if (offerRepository.findOfferByStudentAndIsActiveTrue(student) != null) {
			throw new Exception("Student can't have two active offers");
		}


		// check if the already has 4 internships
		if (toList(specificInternshipRepository.findByStudent(student)).size() >= 4){
			throw new Exception("Student already has 4 internships.\nCant create more offers");
		}

		Offer offer = new Offer();

		// set offer as active
		offer.setIsActive(true);		

		//set offer id
		offer.setOfferId((int)offerRepository.count());

		// set docs for offer
		offer.setDocuments(new HashSet<Document>());
		
		// add offer to student
		Set<Offer> offers = student.getOffers();
		offers.add(offer);
		student.setOffers(offers);

		// add student to offer
		offer.setStudent(student);	


		for(Offer o:offers) {
			o.setIsActive(false);
		}

		offer.setIsActive(true);


		offerRepository.save(offer);
		studentRepository.save(student);

		return offer;
	}


	/**
	 * plays the role of the admin
	 * used only to complete testing
	 * @param offer
	 * @return offer
	 */
	@Transactional
	public Offer validateOffer(Offer offer) {
		if (offer == null) {
			throw new IllegalArgumentException("Error: Offer is null!");
		}

		if (offer.isIsValidated()) {
			throw new IllegalArgumentException("Error: Offer is already valid!");
		}

		if (!offer.isIsActive()) {
			throw new IllegalArgumentException("Error: Offer is not active!");
		}

		offer.setIsActive(false);
		offer.setIsValidated(true);

		offerRepository.save(offer);

		return offer;
	}

	/**
	 * delete document from offer
	 * @param offer
	 * @param url
	 * @return Document
	 */
	@Transactional
	public Document deleteDocumentByUrlFromOffer(Offer offer, String url) {
		Document doc = documentRepository.findByDocumentURL(url);

		if (doc == null) {
			throw new NullPointerException("The URL specified does not exits in the system database");
		}

		boolean containsDoc = false;	
		for (Document d : offer.getDocuments()) {
			if (d.getDocumentURL().equals(url)) {
				Set<Document> documents =  offer.getDocuments();
				documents.remove(d);
				offer.setDocuments(documents);
				containsDoc = true;
			}
		}

		if (!containsDoc) {
			throw new IllegalArgumentException("Offer does not have an document with that URL!");
		}



		documentRepository.delete(doc);
		offerRepository.save(offer);
		return doc;
	}

	/**
	 * delete document from internship
	 * @param offer
	 * @param url
	 * @return Document
	 */
	@Transactional
	public Document deleteDocumentByUrlFromInternship(SpecificInternship internship, String url) {
//		Document doc = documentRepository.findByDocumentURL(url);
//		if (!toList(internship.getReports()).contains(doc)) {
//			throw new IllegalArgumentException("Offer does not have an document with that URL!");
//		}
//
//		documentRepository.delete(doc);
//		specificInternshipRepository.save(internship);
//		return doc;
		Document doc = documentRepository.findByDocumentURL(url);

		if (doc == null) {
			throw new NullPointerException("The URL specified does not exits in the system database");
		}

		boolean containsDoc = false;	
		for (Document d : internship.getReports()) {
			if (d.getDocumentURL().equals(url)) {
				Set<Document> documents =  internship.getReports();
				documents.remove(d);
				internship.setReports(documents);
				containsDoc = true;
			}
		}

		if (!containsDoc) {
			throw new IllegalArgumentException("Offer does not have an document with that URL!");
		}



		documentRepository.delete(doc);
		specificInternshipRepository.save(internship);
		return doc;
	}




	@Transactional
	/**
	 * update offer by adding a document to it
	 * @param offer to update
	 * @param url of the document
	 * @param name of the document
	 * @return added document
	 * @throws Exception
	 */
	public Document updateOffer(Offer offer, String url, String name) throws Exception{
		Document document = null;
		// try creating document
		// throw error if error was caught
		try {
			document = createDoc(url, name);
		}
		catch (IllegalArgumentException e) {
			throw e;
		}

		if (offer == null) {
			throw new IllegalArgumentException("Error: An offer needs to be selected for update!");
		}

		if (document == null) {
			throw new IllegalArgumentException("Error: A document needs to be selected for update!");
		}


		if (toList(offer.getDocuments()).size() >= 2) {
			throw new Exception("Student already has two documents associated with this offer. "
					+ "Please delete the document you wish to replace.");
		}

		// Add document to offer
		Set<Document> documents = offer.getDocuments();
		
		if(documents.size() >= 2) {
			throw new ArrayIndexOutOfBoundsException("You already submitted the maximum number of documents!");
		}
		documents.add(document);
		offer.setDocuments(documents);

		//persist changes
		documentRepository.save(document);
		offerRepository.save(offer);

		return document;
	}



	@Transactional
	/**
	 * Create a specific internship and associate it with a student
	 * @param student
	 * @return SpecificInternship
	 */

	public SpecificInternship registerSpecificInternship(Student student, ScheduledInternship scheduledInternship,
			int year) throws Exception {


		// throw exception if student already has 4 internships
		if (toList(specificInternshipRepository.findByStudent(student)).size() >= 4) {
			throw new Exception("Student already has 4 internships.");
		}



		// throw exception if student does not have a valid offer
		if (offerRepository.countByStudentAndIsValidatedTrue(student) == 0) {
			throw new Exception("Student does not have a valid offer.");
		}


		// throw exception if student's number of valid offers is not equal to the number of his specific internships + 1
		// makes the check before it unneccessary
		if (offerRepository.countByStudentAndIsValidatedTrue(student) != 
				(specificInternshipRepository.countByStudent(student) + 1 )) {
			throw new Exception("Student does not have a valid.");
		}



		// throw exception if student already has an active internship
		if (specificInternshipRepository.findByStudentAndIsActiveTrue(student) != null) {
			throw new Exception("Student already has a an active internship.");
		}


		// throw exception if there is no scheduled internship to associate specific internship with
		if (scheduledInternshipRepository.count() == 0) {
			throw new Exception("There is no scheduled internship to associate specific internship with");
		}


		// throw exception if input is null
		if (student == null) {
			throw new IllegalArgumentException("Error: Invalid Input. Student must be active for registration");
		}


		// throw exception if input is null
		if ( scheduledInternship == null) {
			throw new IllegalArgumentException("Error: Invalid Input. Scheduled Internship must be selected for registration");
		}


		SpecificInternship specificInternship = new SpecificInternship();

		// set the id of the new internship
		String id = scheduledInternship.getPositionId() + "_" + student.getStudentId() + "_" + year + "_" +  specificInternshipRepository.count();
		specificInternship.setInternshipId(id);

		// set the year of the specific Internship
		specificInternship.setYear(year);

		// add specific internship to scheduled internship's specific internships
		Set<SpecificInternship> specificInternships = scheduledInternship.getSpecificInternships();
		specificInternships.add(specificInternship);
		scheduledInternship.setSpecificInternships(specificInternships);


		// add scheduled internship to specific internship
		specificInternship.setScheduledInternship(scheduledInternship);


		// add specific internship to student
		specificInternships = student.getSpecificInternships();
		specificInternships.add(specificInternship);
		student.setSpecificInternships(specificInternships);


		// add student to specific internship
		specificInternship.setStudent(student);

		// set specific internship as active
		specificInternship.setIsActive(true);

		// persist changes
		specificInternshipRepository.save(specificInternship);
		scheduledInternshipRepository.save(scheduledInternship);
		studentRepository.save(student);



		return specificInternship;
	}



	/**
	 * The method sets the specificInternship to be finished
	 * @param specificInternship
	 */
	@Transactional
	public void finishSpecificInternship(SpecificInternship specificInternship) {
		specificInternship.setIsActive(false);
		specificInternship.setIsCompleted(true);
		specificInternshipRepository.save(specificInternship);
	}

	/**
	 * helper method that turns an iterable into a list
	 * 
	 * @param iterable
	 * @return List
	 */
	public <T> List<T> toList(Iterable<T> iterable) {
		List<T> resultList = new ArrayList<T>();
		for (T t : iterable) {
			resultList.add(t);
		}
		return resultList;
	}


	@Transactional
	
	/**
	 * Creates an instance of the document class using the name and path of the
	 * document as references to it
	 * 
	 * @param URL
	 * @param name
	 * @return Document
	 */ 
	public Document createDoc(String URL, String name) {
		// creates an instance of the doc class
		if (name == null || name.trim().length() == 0) {
			throw new IllegalArgumentException("The name of the document is invalid!");
		}
		if (URL == null || URL.trim().length() == 0) {
			throw new IllegalArgumentException("The URL of the document is invalid!");
		}

		if (documentRepository.findByDocumentURL(URL) != null) {
			throw new IllegalArgumentException("Error: Document is already uploaded");
		}

		Document doc = new Document();
		doc.setDocumentURL(URL);
		doc.setName(name);
		doc.setDocumentId(URL.hashCode());
		return doc;
	}

	@Transactional
	/**
	 * The method allows the Student to submit couse evaluation documents
	 * 
	 * @param student
	 * @param URL
	 * @param name
	 * @return Void
	 */
	public boolean submitCourseEvaluation(Student student, String URL, String name) {

		// find the active internship the student is undertaking
		SpecificInternship SI = specificInternshipRepository.findByStudentAndIsActiveTrue(student);
		if (SI == null) {
			throw new NullPointerException("The student does not have an active Internship!");
		}
		// Retrieve all the documents from the internship
		Set<Document> docs = SI.getReports();
		// creates a document with the given name and URL
		Document doc = createDoc(URL, name);
		if (doc == null) {
			throw new NullPointerException("The document does not exist");
		}

		// adds the document to the list of documents
		docs.add(doc);
		// sets the list of documents of the internship to the new list
		SI.setReports(docs);

		documentRepository.save(doc);
		specificInternshipRepository.save(SI);

		return true;

	}

	@Transactional
	/**
	 * This method retrieves the number of documents completed by a given student
	 * during their active coop term.
	 * 
	 * @param student
	 * @return docsCompleted: number of completed documents.
	 */
	public int getDocsCompleted(Student student) {
		// Initialized to zero in case the set is empty

		// Finds the student's active specific internship
		SpecificInternship SI = specificInternshipRepository.findByStudentAndIsActiveTrue(student);
		if (SI == null) {
			throw new NullPointerException(
					"Error: Student " + student.getStudentId() + " does not have an active Internship!");

		}

		// Gets the documents in the report
		return SI.getReports().size();

	}

	@Transactional
	/**
	 * THe method returns the number of terms finished
	 * 
	 * @param student
	 * @return TermsFinished
	 */
	public int getTermsFinished(Student student) {
		if (student == null) {

			throw new IllegalArgumentException(" The student does not exist!");

		}
		return student.getTermsFinished();
	}

	@Transactional
	/**
	 * THe method returns the number of terms remaining
	 * 
	 * @param student
	 * @return TermsRemaining
	 */
	public int getTermsremaining(Student student) {
		if (student == null) {

			throw new IllegalArgumentException(" The student does not exist!");

		}
		return student.getTermsRemaining();
	}

	@Transactional
	/**
	 * The method returns a list of all the internships the student has completed
	 * 
	 * @param student
	 * @return AllInternships
	 */
	public List<SpecificInternship> getAllInternships(Student student) {
		if (student == null) {

			throw new IllegalArgumentException(" The student does not exist!");

		}
		List<SpecificInternship> allInterns = specificInternshipRepository.findByStudent(student);
		if (allInterns == null) {
			throw new NullPointerException("The student does not have any internships!");

		}
		return allInterns;
	}

	@Transactional
	/**
	 * The method checks if the internship is completed
	 * 
	 * @param internship
	 * @return boolean
	 */
	public boolean getIsCompleted(SpecificInternship internship) {
		if (internship == null) {
			throw new IllegalArgumentException(" The Internship does not exist!");
		}

		SpecificInternship specificInternship = specificInternshipRepository.findSpecificInternshipByInternshipId(internship.getInternshipId());	

		if (specificInternship == null) {
			throw new IllegalArgumentException(" The Internship does not exist!");
		}

		

		return internship.isIsCompleted();
	}

	@Transactional
	/**
	 * The method gets the year of the internship
	 * 
	 * @param internship
	 * @return Year
	 */
	public int getYearOfInternship(SpecificInternship internship) {
		if(internship == null) {
			throw new IllegalArgumentException(" The Internship does not exist!");
		}
		SpecificInternship specificInternship = specificInternshipRepository.findSpecificInternshipByInternshipId(internship.getInternshipId());
		if (specificInternship == null) {
			throw new IllegalArgumentException(" The Internship does not exist!");
		}

		if (!specificInternship.getInternshipId().equals(internship.getInternshipId())) {
			throw new IllegalArgumentException(" The Internship does not exist!");
		}
		return internship.getYear();
	}

	@Transactional
	/**
	 * The method gets the term of the internship
	 * 
	 * @param internship
	 * @return Term
	 */
	public String getSemester(ScheduledInternship internship) {
		if (internship == null) {
			throw new IllegalArgumentException(" The scheduled internship does not exist!");
		}
		return internship.getTerm().toString();
	}

	@Transactional
	/**
	 * The method gets the Employer's name of the internship
	 * 
	 * @param internship
	 * @return Employer
	 */
	public String getCompany(ScheduledInternship internship) {
		if (internship == null) {
			throw new IllegalArgumentException(" The scheduled internship does not exist!");
		}
		return internship.getEmployer();
	}

	@Transactional
	/**
	 * The method allows the Student to submit internship evaluation document
	 * 
	 * @param student
	 * @param URL
	 * @param name
	 * @return Void
	 */

	public Boolean submitInternshipEvaluation(Student student, String URL, String name) {

		// creates a document with the given name and URL
		// for the evaluation form

		Document evalForm = null;
		try {
			evalForm = createDoc(URL, name);
		}
		catch(IllegalArgumentException e) {
			throw e;
		}
		if (evalForm == null) {
			throw new NullPointerException("The document does not exist");
		}

		// find the active internship the student is undertaking
		SpecificInternship activeInternship = specificInternshipRepository.findByStudentAndIsActiveTrue(student);
		if (activeInternship == null) {
			throw new NullPointerException("Student does not have an active Internship!");
		}

		// Retrieve all the documents from the internship
		Set<Document> docs_internship = activeInternship.getReports();
		
		if(docs_internship.size() >= 4) {
			throw new ArrayIndexOutOfBoundsException("You already submitted the maximum number of documents!");
		}


		// adds the document to the list of documents
		docs_internship.add(evalForm);

		// sets the list of documents of the internship to the new list
		activeInternship.setReports(docs_internship);

		// Persist Changes
		documentRepository.save(evalForm);
		specificInternshipRepository.save(activeInternship);

		return true;

	}


	/**
	 * return all persons in the person repository
	 * @return List<Person>
	 */
	@Transactional
	public List<Person> getAllPersons(){
		return toList(personRepository.findAll());
	}


	/**
	 * return all offers in the offer repository
	 * @return List<Offer>
	 */
	@Transactional
	public List<Offer> getAllOffers(){
		return toList(offerRepository.findAll());
	}



	/**
	 * return all SpecificInternships in the SpecificInternship repository
	 * @return List<SpecificInternship>
	 */
	@Transactional
	public List<SpecificInternship> getAllSpecificInternships(){
		return toList(specificInternshipRepository.findAll());
	}



	/**
	 * return all ScheduledInternships in the ScheduledInternship repository
	 * @return <ScheduledInternship>
	 */
	@Transactional
	public List<ScheduledInternship> getAllScheduledInternships(){
		return toList(scheduledInternshipRepository.findAll());
	}

	/**
	 * return all Students in the Student repository
	 * @return List<Student>
	 */
	@Transactional
	public List<Student> getAllStudents(){
		return toList(studentRepository.findAll());
	}


	/**
	 * return all documents in document repository
	 * @return List<Document>
	 */
	@Transactional
	public List<Document> getAllDocuments(){
		return toList(documentRepository.findAll());
	}

	/**
	 * return student by studentId from the student repository
	 * @param studentId
	 * @return Student
	 */
	@Transactional
	public Student getStudent(long studentId) {
		return studentRepository.findStudentByStudentId(studentId);
	}
	
	/**
	 * return person by email from the person repository
	 * @param email
	 * @return Person
	 */
	@Transactional
	public Person getPerson(String email) {
		return personRepository.findPersonByEmail(email);
	}
	
	/**
	 *  return scheduledInternship by positionId from the scheduledInternship repository
	 * @param SIId
	 * @return ScheduledInternship
	 */
	@Transactional
	public ScheduledInternship getScheduledInternship(String SIId) {
		return scheduledInternshipRepository.findByPositionId(SIId);
	}
	
	/**
	 *  return specificInternship by internshipId from the specificInternship repository
	 * @param SIId
	 * @return SpecificInternship
	 */
	@Transactional
	public SpecificInternship getSpecificInternship(String SIId) {
		return specificInternshipRepository.findSpecificInternshipByInternshipId(SIId);
	}
	
	/**
	 *  return document by documentId from the document repository
	 * @param Id
	 * @return Document
	 */
	@Transactional
	public Document getDocument(Integer Id) {
		return documentRepository.findByDocumentId(Id);
	}
	
	/**
	 *  return offer by offerId from the offer repository
	 * @param offerId
	 * @return Offer
	 */
	@Transactional
	public Offer getOffer(Integer offerId) {
		return offerRepository.findByOfferId(offerId);
	}
	
	@Transactional
	public SpecificInternship getSpecificInternshipAndIsCompletedFalse(Student student) {
		return specificInternshipRepository.findByStudentAndIsCompletedFalse(student);
	}
	
	/**
	 *  return CooperatorSystem by systemId from the cooperatorSystem repository
	 * @param systemId
	 * @return CooperatorSystem
	 */
	@Transactional
	public CooperatorSystem getCoopSystem(Integer systemId) {
		return cooperatorSystemRepository.findBySystemId(systemId);
	}
	
	/**
	 *  return offer by student from the offer repository
	 * @param student
	 * @return Offer
	 */
	@Transactional
	public Offer getOfferByStudentAndIsActive(Student student) {
		Offer offer =  offerRepository.findOfferByStudentAndIsActiveTrue(student);
		if (offer == null) {
			throw new NullPointerException(
					"Error: Student " + student.getStudentId() + " does not have an active offer!");
		}
		return offer;
	}
	
	/**
	 *  return offer by student from the offer repository
	 * @param student
	 * @return Offer
	 */
	@Transactional
	public List<Offer> getOfferByStudent(Student student) {
		List<Offer> offer =  offerRepository.findOfferByStudent(student);
		if (offer == null) {
			throw new NullPointerException(
					"Error: Student " + student.getStudentId() + " does not have an offer!");
		}
		return offer;
	}
	
	/**
	 * the method creates cooperatorSystem and saves it to the cooperatorSystem repository
	 * @return CooperatorSystem
	 */
	@Transactional
	public CooperatorSystem createCooperatorSystem() {
		CooperatorSystem cs = new CooperatorSystem();
		cs.setSystemId((int)(cooperatorSystemRepository.count()));
		cooperatorSystemRepository.save(cs);
		return cs;
	}
	/**
	 * return all CooperatorSystems from the cooperatorSystem repository
	 * @return List<CooperatorSystem>
	 */
	//	@Transactional
	public List<CooperatorSystem> getAllCooperatorSystems(){
		return toList(cooperatorSystemRepository.findAll());
	}
}