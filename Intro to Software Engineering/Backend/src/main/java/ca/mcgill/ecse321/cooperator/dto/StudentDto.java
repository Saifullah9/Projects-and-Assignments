package ca.mcgill.ecse321.cooperator.dto;

import java.util.ArrayList;
import java.util.List;



public class StudentDto {
	private long studentID;
	private int termsFinished = 0;
	private int termsRemaining; // NOT SURE ABOUT THAT
	private String password;
	

	private PersonDto person;
	private List<OfferDto> offers = new ArrayList<OfferDto>();
	private List<SpecificInternshipDto> specificInternships = new ArrayList<SpecificInternshipDto>();

	
	
	public StudentDto() {
	}
	
	public StudentDto(PersonDto person, long studentID) {
		this.studentID = studentID;
		this.person = person;
		this.termsRemaining = 4;
		this.termsFinished = 0;
		this.password = "";
	}
	
	
	/**
	 * @param termsFinished the termsFinished to set
	 */
	public void setTermsFinished(int termsFinished) {
		this.termsFinished = termsFinished;
	}
	

	/**
	 * @param termsRemaining the termsRemaining to set
	 */
	public void setTermsRemaining(int termsRemaining) {
		this.termsRemaining = termsRemaining;
	}
	
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the offers
	 */
	public List<OfferDto> getOffers() {
		return offers;
	}

	/**
	 * @param offers the offers to set
	 */
	public void setOffers(List<OfferDto> offers) {
		this.offers = offers;
	}

	/**
	 * @return the specificInternships
	 */
	public List<SpecificInternshipDto> getSpecificInternships() {
		return specificInternships;
	}

	/**
	 * @param specificInternships the specificInternships to set
	 */
	public void setSpecificInternships(List<SpecificInternshipDto> specificInternships) {
		this.specificInternships = specificInternships;
	}
	
	public void addSpecificInternship(SpecificInternshipDto specificInternship) {
		this.specificInternships.add(specificInternship);
	}
	
	public void addOffer(OfferDto offer) {
		this.offers.add(offer);
	}
	

	/**
	 * @return the studentID
	 */
	public long getStudentID() {
		return studentID;
	}
	
	/**
	 * @return the passsword
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return the termsFinished
	 */
	public int getTermsFinished() {
		return termsFinished;
	}

	/**
	 * @return the termsRemaining
	 */
	public int getTermsRemaining() {
		return termsRemaining;
	}

	/**
	 * @return the person
	 */
	public PersonDto getPerson() {
		return person;
	}
	
	

}