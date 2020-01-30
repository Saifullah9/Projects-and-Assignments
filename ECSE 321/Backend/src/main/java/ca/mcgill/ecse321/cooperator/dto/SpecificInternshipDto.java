package ca.mcgill.ecse321.cooperator.dto;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.ecse321.cooperator.model.Term;

public class SpecificInternshipDto {
	private int scheduledInternshipId;
	private long studentId;
	private boolean isCompleted;
	private int year;
	private String internshipId;
	private String employer;
	private Term term;
	private String company;
	

	private List<DocumentDto> documents = new ArrayList<DocumentDto>();
	
	public SpecificInternshipDto() {
		
	}
	
	public SpecificInternshipDto(int scheduledInternshipId, long studentId, int year, String internshipId) {
		this.scheduledInternshipId = scheduledInternshipId;
		this.studentId = studentId;
		this.year = year;
		this.isCompleted = false;
		this.internshipId = internshipId;
	}

	/**
	 * @return the isCompleted
	 */
	public boolean isCompleted() {
		return isCompleted;
	}

	/**
	 * @param isCompleted the isCompleted to set
	 */
	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	/**
	 * @return the documents
	 */
	public List<DocumentDto> getDocuments() {
		return documents;
	}

	/**
	 * @param documents the documents to set
	 */
	public void setDocuments(List<DocumentDto> documents) {
		this.documents = documents;
	}
	public void adddDocument(DocumentDto document) {
		this.documents.add(document);
	}
	


	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * @return the internshipId
	 */
	public String getInternshipId() {
		return internshipId;
	}

	public long getStudentId() {
		return studentId;
	}

	public void setStudentId(long studentId) {
		this.studentId = studentId;
	}

	public int getScheduledInternshipId() {
		return scheduledInternshipId;
	}

	public void setScheduledInternshipId(int scheduledInternshipId) {
		this.scheduledInternshipId = scheduledInternshipId;
	}
	
	public Term getTerm() {
		return term;
	}

	public void setTerm(Term term) {
		this.term = term;
	}

	public String getEmployer() {
		return employer;
	}

	public void setEmployer(String employer) {
		this.employer = employer;
	}
	
	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}
	
	

}