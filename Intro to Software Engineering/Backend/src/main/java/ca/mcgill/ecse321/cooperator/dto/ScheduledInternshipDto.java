package ca.mcgill.ecse321.cooperator.dto;

import java.util.List;

import ca.mcgill.ecse321.cooperator.model.Term;

public class ScheduledInternshipDto {
	private String name;
	private String positionId;
	private String employer;
	private int id;
	private Term term;
	private List<SpecificInternshipDto> specificInternships;
	
	public ScheduledInternshipDto(){}
	
	public ScheduledInternshipDto(String name, String positionId, String employer, int id, Term term){
		this.name = name;
		this.positionId = positionId;
		this.employer = employer;
		this.id = id;
		this.term = term;
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

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the positionId
	 */
	public String getPositionId() {
		return positionId;
	}

	/**
	 * @return the employer
	 */
	public String getEmployer() {
		return employer;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the term
	 */
	public Term getTerm() {
		return term;
	}
	
	
	
	
}
