package ca.mcgill.ecse321.cooperator.dto;

import java.util.List;

public class OfferDto {
	private boolean isActive;
	private boolean isValidated;
	private Integer offerId;
	private long studentId;
	
	private List<DocumentDto> documents;

	public OfferDto() {
	}
	public OfferDto(long studentId, Integer offerId) {
		this.isActive = true;
		this.isValidated = false;
		this.studentId = studentId;
		this.offerId = offerId;
	}
	
	public long getStudentId() {
		return studentId;
	}
	public void setStudentId(long studentId) {
		this.studentId = studentId;
	}
	
	/**
	 * @return the isActive
	 */
	public boolean isActive() {
		return isActive;
	}
	/**
	 * @param isActive the isActive to set
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	/**
	 * @return the isValidated
	 */
	public boolean isValidated() {
		return isValidated;
	}
	/**
	 * @param isValidated the isValidated to set
	 */
	public void setValidated(boolean isValidated) {
		this.isValidated = isValidated;
	}
	/**
	 * @return the offerId
	 */
	public Integer getOfferId() {
		return offerId;
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
	
	
	

}
