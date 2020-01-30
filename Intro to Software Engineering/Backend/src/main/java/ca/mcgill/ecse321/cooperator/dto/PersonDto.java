package ca.mcgill.ecse321.cooperator.dto;

public class PersonDto {
	private String first;
	private String last;
	private String email;
	private long studentId;
	
	public long getStudentId() {
		return studentId;
	}
	public void setStudentId(long studentId) {
		this.studentId = studentId;
	}
	public PersonDto(){
	}
	public PersonDto(String first, String last, String email) {
		this.first= first;
		this.last = last;
		this.email = email;
	}
	
	
	/**
	 * set the role of person as student
	 * @param student
	 */
	
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return first;
	}
	
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return last;
	}
	
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	
	
	
	
	
	
}
