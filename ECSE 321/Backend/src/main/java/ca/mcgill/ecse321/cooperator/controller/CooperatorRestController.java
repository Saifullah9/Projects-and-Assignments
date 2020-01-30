package ca.mcgill.ecse321.cooperator.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.mcgill.ecse321.cooperator.dto.DocumentDto;
import ca.mcgill.ecse321.cooperator.dto.OfferDto;
import ca.mcgill.ecse321.cooperator.dto.PersonDto;
import ca.mcgill.ecse321.cooperator.dto.ScheduledInternshipDto;
import ca.mcgill.ecse321.cooperator.dto.SpecificInternshipDto;
import ca.mcgill.ecse321.cooperator.dto.StudentDto;
import ca.mcgill.ecse321.cooperator.model.Document;
import ca.mcgill.ecse321.cooperator.model.Offer;
import ca.mcgill.ecse321.cooperator.model.Person;
import ca.mcgill.ecse321.cooperator.model.ScheduledInternship;
import ca.mcgill.ecse321.cooperator.model.SpecificInternship;
import ca.mcgill.ecse321.cooperator.model.Student;
import ca.mcgill.ecse321.cooperator.model.Term;
import ca.mcgill.ecse321.cooperator.service.CooperatorService;


@CrossOrigin(origins = "*")
@RestController
public class CooperatorRestController {
	@Autowired
	private CooperatorService service;

	
	/**
	 * the method is for providing a sample point for other teams for testing
	 * @throws Exception
	 */
	 @PostConstruct
	    public void initIt() throws Exception {
	      Person person1 = service.createPerson("saif", "Elsayed", "dsfadsv@asd.com");
	      Student student1 = service.createStudent(person1, (long) 260733168 );
	      ScheduledInternship sc = service.createScheduledInternShip("AwladRagab", "hamada", "FALL");
	      Offer offer = service.createOffer(student1);
	      offer = service.validateOffer(offer);
	      SpecificInternship SI = service.registerSpecificInternship(student1, sc, 2020);
	      service.submitInternshipEvaluation(student1, "sdf", "sdfasdf");
	      service.finishSpecificInternship(SI);
	      
	      ScheduledInternship sc2 = service.createScheduledInternShip("Google", "TimCook", "FALL");
	      Offer offer1 = service.createOffer(student1);
	      service.validateOffer(offer1);
	      service.registerSpecificInternship(student1, sc2, 2021);
	      service.submitInternshipEvaluation(student1, "122121", "LOOOOOL");
	      Offer offer3 = service.createOffer(student1);
	      service.updateOffer(offer3, "u1rl", "name1");
	      
	    }	
	/**
	 * the method creates a student
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param studentId
	 * @return StudentDto
	 * @throws Exception
	 */
	@PostMapping(value = {"/create-student", "/create-student/"})
	public StudentDto createStudent(@RequestParam("firstName") String firstName,
			@RequestParam("lastName") String lastName,
			@RequestParam("email") String email,
			@RequestParam("studentId") long studentId) throws Exception {
		Person person = service.createPerson(firstName,lastName,email);
		Student student = service.createStudent(person, studentId);
		return convertToDto(student);
	}
	/**
	 * Http request for the password of the user. helps keep the password unknown by other view points.
	 * @param studentId
	 * @return
	 */
	@GetMapping(value = { "/getPassword/{studentId}", "/getPassword/{studentId}/" })
	public String getPassword(@PathVariable("studentId") long studentId)  {
		return (service.getStudent(studentId)).getPassword();
	}
	/**
	 * http request to set the password of the user.
	 * @param studentId
	 * @return
	 */
	@PostMapping(value = { "/setPassword/{studentId}", "/setPassword/{studentId}/" })
	public String setPassword(@PathVariable("studentId") long studentId)  {
		service.getStudent(studentId).setPassword(service.generatePassword(8));
		return (service.getStudent(studentId)).getPassword();
	}
	/**
	 * http request to get the offer of a student that is currently active.
	 * @param studentId
	 * @return
	 */
	@GetMapping(value = { "/getOffer/{studentId}", "/getOffer/{studentId}/" })
	public OfferDto getOffer(@PathVariable("studentId") long studentId) {
		return convertToDto(service.getOfferByStudentAndIsActive(service.getStudent(studentId)));
	}
	/**
	 * http request that gets all the offers of the student.
	 * @param studentId
	 * @return
	 */
	@GetMapping(value = { "/getOffers/{studentId}", "/getOffers/{studentId}/" })
	public List<OfferDto> getOffers(@PathVariable("studentId") long studentId) {

		return convertToDto(service.getOfferByStudent(service.getStudent(studentId)));
	} 
	
	
	/**
	 * http request to delete documents from an offer using the url of the document.
	 * @param studentId
	 * @param docUrl
	 * @return
	 */
	@DeleteMapping(value = { "/deleteDocument/{studentId}", "/deleteDocument/{studentId}/" })
	public Boolean deleteDocumentFromOffer(@PathVariable("studentId") long studentId,
			@RequestParam(name = "docUrl") String docUrl) {
		try {
		Offer offer = service.getOfferByStudentAndIsActive(service.getStudent(studentId));
		service.deleteDocumentByUrlFromOffer(offer, docUrl);
		return true;
		} catch(Exception e) {
			return false;
		}
	}
	/**
	 * http request to delete documents from internships using the url of the document.
	 * @param studentId
	 * @param docUrl
	 * @return
	 */
	@DeleteMapping(value = { "/deleteDocumentInternship/{studentId}", "/deleteDocumentInternship/{studentId}/" })
	public Boolean deleteDocumentFromInternship(@PathVariable("studentId") long studentId,
			@RequestParam(name = "docUrl") String docUrl) {
		try {
		SpecificInternship SI = service.getSpecificInternshipAndIsCompletedFalse(service.getStudent(studentId));
		service.deleteDocumentByUrlFromInternship(SI, docUrl);
		return true;
		} catch(Exception e) {
			
			return false;
		}
	}
	
	
	/**
	 * the method creates an offer 
	 * @param studentId
	 * @return OfferDto
	 * @throws Exception
	 */
	@PostMapping(value = {"/{studentId}/create-offer", "/{studentId}/create-offer/"})
	public OfferDto createOffer(@PathVariable("studentId") long studentId) throws Exception {	
		Student student = service.getStudent(studentId);
		Offer offer = service.createOffer(student);
		return convertToDto(offer);		
	}


	/**
	 * the method validates the offer
	 * @param studentId
	 * @return OfferDto
	 * @throws Exception
	 */
	@PostMapping(value = {"/{studentId}/validate-offer", "/{studentId}/validate-offer"})
	public OfferDto validateOffer(@PathVariable("studentId") Integer studentId) throws Exception {
		Student student = service.getStudent(studentId);
		Offer offer = service.getOfferByStudentAndIsActive(student);
		offer = service.validateOffer(offer);	
		return convertToDto(offer);
	}
	/**
	 * http request that sets the specific internship to completed and sets the isActive attribute to false.
	 * @param studentId
	 * @return
	 * @throws Exception
	 */
	@PutMapping(value = {"/{studentId}/finishInternship", "/{studentId}/finishInternship"})
	public boolean finishInternship(@PathVariable("studentId") Integer studentId) throws Exception {
		Student student = service.getStudent(studentId);
		List<SpecificInternship> SIs = service.getAllInternships(student);
		SpecificInternship si = null;
		for(SpecificInternship si2 : SIs) {	
			if(si2.isIsActive()) {
				si = si2;
			}
		}
		if(si == null) {
			return false;
		}
		service.finishSpecificInternship(si);
		return true;
	}



	/**
	 * the method registers the specificInternship to the student
	 * @param studentId
	 * @param positionId
	 * @param name
	 * @param employer
	 * @param term
	 * @param year
	 * @return SpecificInternshipDto
	 * @throws Exception
	 */
	@PostMapping(value = {"/{studentId}/register-internship", "/{studentId}/register-internship/"})
	public SpecificInternshipDto Internship(@PathVariable("studentId") Integer studentId,
			@RequestParam(name = "scheduledInternship") String positionId, 
			@RequestParam(name = "name") String name,
			@RequestParam(name = "employer") String employer,
			@RequestParam(name = "term") String term,
			@RequestParam(name = "year") int year) throws Exception {

		ScheduledInternship scheduledInternship = service.getScheduledInternship(positionId);
		Student student = service.getStudent(studentId);
		if (scheduledInternship == null) {
			scheduledInternship = service.createScheduledInternShip(name, employer, term);
		}
		SpecificInternship specificInternship = service.registerSpecificInternship(student, scheduledInternship, year);		
		SpecificInternshipDto SIDTO = convertToDto(specificInternship);
		SIDTO.setEmployer(employer);
		SIDTO.setTerm(Term.valueOf(term));
		return SIDTO;

	}

	/**
	 * the method uploads a document to specificInternship
	 * @param studentId
	 * @param docUrl
	 * @param docname
	 * @throws IllegalArgumentException
	 */
	@PostMapping(value = {"/{studentId}/specific-internship/upload-doc","/{studentId}/specific-internship/upload-doc/"})
	public void submitInternshipDoc(
			@PathVariable("studentId") long studentId, 
			@RequestParam(name = "documentURL") String docUrl, 
			@RequestParam(name = "documentName") String docname ) 
					throws IllegalArgumentException
	{
		Student student = service.getStudent(studentId);
		service.submitInternshipEvaluation(student, docUrl, docname);
		
	}


	/**
	 * the method uploads a document to offer
	 * @param studentId
	 * @param documentUrl
	 * @param docname
	 * @throws Exception 
	 */
	@PostMapping(value = {"/{studentId}/offer/upload-doc","/{studentId}/offer/upload-doc/"})
	public void submitOfferDoc(
			@PathVariable("studentId") long studentId, 
			@RequestParam(name = "documentURL") String documentUrl, 
			@RequestParam(name = "documentName") String docname  ) 
					throws Exception
	{

		Student student = service.getStudent(studentId);
		Offer offer = service.getOfferByStudentAndIsActive(student);
			service.updateOffer(offer, documentUrl, docname);

	}

	/**
	 * the method converts person to personDto
	 * @param p
	 * @return PersonDto
	 */
	private PersonDto convertToDto(Person p) {
		if (p == null) {
			throw new IllegalArgumentException("There is no such Person!");
		}
		PersonDto personDto = new PersonDto(p.getFirst(), p.getLast(), p.getEmail());
		return personDto;
	}

	/**
	 * the method converts student to studentDto
	 * @param student
	 * @return StudentDto
	 */
	private StudentDto convertToDto (Student student) {
		if (student == null) {
			throw new IllegalArgumentException("There is no such Student!");
		}
		PersonDto pDto = convertToDto(student.getPerson());
		pDto.setStudentId(student.getStudentId());
		StudentDto studentDto =  new StudentDto (pDto, student.getStudentId());
		try{
			if (service.getOfferByStudentAndIsActive(student) != null) {
				studentDto.addOffer(convertToDto(service.getOfferByStudentAndIsActive(student)));
			}
		}
		catch(NullPointerException e) {

		}
		List<SpecificInternshipDto> spDtos = new ArrayList<>();
		for (SpecificInternship sp : service.getAllInternships(student)) {
			spDtos.add(convertToDto(sp));
		}
		studentDto.setSpecificInternships(spDtos);
		return studentDto;
	}

	/**
	 * returns the year of the internship in an http request
	 * @param ID
	 * @return
	 */
	@GetMapping(value = { "/getYear/", "/getYear/" })
	public int getYearOfInternship(@RequestParam("SpecificInternship")  String ID) {
		return service.getYearOfInternship(service.getSpecificInternship(ID));
	}
	
	/**
	 * returns the scheduledInternship of a specificInternship.
	 * @param ID
	 * @return
	 */
	@GetMapping(value = { "/scheduledInternship", "/scheduledInternship/" })
	public ScheduledInternshipDto getScheduledInternship(@RequestParam("SpecificInternship")  String ID) {
		return convertToDto(service.getSpecificInternship(ID).getScheduledInternship());
	}
	
	/**
	 * returns the specificInternship by its ID.
	 * @param ID
	 * @return
	 */
	@GetMapping(value = { "/specificInternship", "/specificInternship/" })
	public SpecificInternshipDto getSpecificInternshipByID(@RequestParam("SpecificInternship")  String ID) {
		return convertToDto(service.getSpecificInternship(ID));
	}

	/**
	 * the method returns student by studentId
	 * @param studentIds
	 * @return StudentDto
	 */
	@GetMapping(value = { "/getStudent/{studentId}", "/getStudent/" })
	public StudentDto getStudent(@PathVariable("studentId") long studentId) {

		return convertToDto(service.getStudent(studentId));
	}

	/**
	 * the method returns the number of terms finished
	 * @param studentId
	 * @return termsFinished
	 */
	@GetMapping(value = { "/getTermsFinished/{studentId}", "/get/" })
	public int getTermsFinished(@PathVariable("studentId") long studentId) {
		return service.getTermsFinished(service.getStudent(studentId));
	}


	/**
	 * the method returns the number of terms remaining
	 * @param studentId
	 * @return termsRemaining
	 */
	@GetMapping(value = { "/getTermsRemaining/{studentId}", "/get/" })
	public int getTermsRemaining(@PathVariable("studentId") long studentId) {

		return service.getTermsremaining(service.getStudent(studentId));
	}
	/**
	 * returns all students in the repository
	 * @return
	 */
	@GetMapping(value = { "/getAllStudents/", "/getAllStudents/" })
	public List<StudentDto> getAllStudents() {
		List<StudentDto> stDtos = new ArrayList<>();
		for (Student st : service.getAllStudents()) {
			stDtos.add(convertToDto(st));
		}

		return stDtos;
	}



	/**
	 * the method returns all specific Internships
	 * @param studentID
	 * @return List<SpecificInternshipDto
	 */
	@GetMapping(value = { "/getAllInternships/", "/getAllInternships/" })
	public List<SpecificInternshipDto> getAllInternships(@RequestParam("Student")  long studentID) {
		Student student = service.getStudent(studentID);
		List<SpecificInternshipDto> spDtos = new ArrayList<>();
		for (SpecificInternship sp : service.getAllInternships(student)) {
			spDtos.add(convertToDto(sp));
		}
		return spDtos;
	}

	/**
	 * the method converts Document to DocumentDto
	 * @param document
	 * @return DocumentDto
	 */
	private DocumentDto convertToDto(Document document) {
		if (document == null) {
			throw new IllegalArgumentException("There is no such Document!");
		}
		return new DocumentDto(document.getDocumentURL(), document.getName(), document.getDocumentId());	
	}


	/**
	 * the method converts SpecificInternship to SpecificInternshipDto
	 * @param sp
	 * @param student
	 * @param sc
	 * @return SpecificInternshipDto
	 */
	private SpecificInternshipDto convertToDto(SpecificInternship sp) {
		if (sp == null) {
			throw new IllegalArgumentException("There is no such Student!");
		}

		SpecificInternshipDto SI = new  SpecificInternshipDto(sp.getScheduledInternship().getId(), sp.getStudent().getStudentId(), sp.getYear(), sp.getInternshipId());
	    SI.setTerm(sp.getScheduledInternship().getTerm());
	    SI.setEmployer(sp.getScheduledInternship().getEmployer());
	    SI.setCompany(sp.getScheduledInternship().getName());
	   
	    if(sp.getReports()!=null) {
	    	List<DocumentDto> docDtos = new ArrayList<>();

	    	for (Document doc : sp.getReports()) {
	    		docDtos.add(convertToDto(doc));
	    	}
	    	SI.setDocuments(docDtos);
	    }
	    
	    SI.setCompleted(sp.isIsCompleted());
	    return SI;
	}


	/**
	 * the method converts offer to OfferDto
	 * @param offer
	 * @param student
	 * @return OfferDto 
	 */
	private OfferDto convertToDto(Offer offer) {
		OfferDto offerDto = new OfferDto(offer.getStudent().getStudentId(), offer.getOfferId());
		offerDto.setActive(offer.isIsActive());
		offerDto.setValidated(offer.isIsValidated());
		List<DocumentDto> documents = new ArrayList<>();
		for(Document doc : offer.getDocuments()) {
			documents.add(convertToDto(doc));
		}
		offerDto.setDocuments(documents);
		return offerDto;
	}
	
	/**
	 * the method converts offer to OfferDto
	 * @param offer
	 * @param student
	 * @return OfferDto 
	 */
	private List<OfferDto> convertToDto(List<Offer> offer) {
		List<OfferDto> offerDto = new ArrayList<OfferDto>();
		for ( int i = 0; i < offer.size(); i++) {
			offerDto.add( convertToDto(offer.get(i)));
		}

		return offerDto;
	}

	/**
	 * the method converts ScheduledInternship to ScheduledInternshipDto
	 * @param s
	 * @return ScheduledInternshipDto
	 */
	private ScheduledInternshipDto convertToDto(ScheduledInternship s) {
		if (s == null) {
			throw new IllegalArgumentException("There is no such Scheduled Internship!");
		}

		return new ScheduledInternshipDto(s.getName(),s.getPositionId(),s.getEmployer(),s.getId(),s.getTerm());	
	}



}