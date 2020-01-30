package ca.mcgill.ecse321.cooperator.dto;

public class DocumentDto {
	private String url;
	private String name;
	private Integer documentId;
	
	public DocumentDto() {
	
	}
	public DocumentDto(String url, String name, Integer documentId) {
		this.url = url;
		this.name = name;
		this.documentId = documentId;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the documentId
	 */
	public Integer getDocumentId() {
		return documentId;
	}
	

}
