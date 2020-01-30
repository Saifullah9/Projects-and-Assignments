package ca.mcgill.ecse321.cooperator.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Student{
   private long studentId;
   private String password;

public void setStudentId(long value) {
    this.studentId = value;
}
public void setPassword(String value) {
    this.password = value;
}
public String getPassword() {
	return this.password;
}
@Id
public long getStudentId() {
    return this.studentId;
}
private Set<Offer> offers;

@OneToMany(mappedBy="student" )
public Set<Offer> getOffers() {
   return this.offers;
}

public void setOffers(Set<Offer> offerss) {
   this.offers = offerss;
}

private Set<SpecificInternship> specificInternships;

@OneToMany(mappedBy="student" )
public Set<SpecificInternship> getSpecificInternships() {
   return this.specificInternships;
}

public void setSpecificInternships(Set<SpecificInternship> specificInternshipss) {
   this.specificInternships = specificInternshipss;
}

private int termsFinished;

public void setTermsFinished(int value) {
    this.termsFinished = value;
}
public int getTermsFinished() {
    return this.termsFinished;
}
private int termsRemaining;

public void setTermsRemaining(int value) {
    this.termsRemaining = value;
}
public int getTermsRemaining() {
    return this.termsRemaining;
}
   private Person person;
   
   @OneToOne(mappedBy="student" , optional=false)
   public Person getPerson() {
      return this.person;
   }
   
   public void setPerson(Person person) {
      this.person = person;
   }
   
   }
