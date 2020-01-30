package ca.mcgill.ecse321.cooperator.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Offer{
   private boolean isValidated;

public void setIsValidated(boolean value) {
    this.isValidated = value;
}
public boolean isIsValidated() {
    return this.isValidated;
}
private Student student;

@ManyToOne(optional=false)
public Student getStudent() {
   return this.student;
}

public void setStudent(Student student) {
   this.student = student;
}

private Set<Document> documents;

@OneToMany
public Set<Document> getDocuments() {
   return this.documents;
}

public void setDocuments(Set<Document> documentss) {
   this.documents = documentss;
}

private Integer offerId;

public void setOfferId(Integer value) {
    this.offerId = value;
}
@Id
public Integer getOfferId() {
    return this.offerId;
}
private boolean isActive;

public void setIsActive(boolean value) {
    this.isActive = value;
}
public boolean isIsActive() {
    return this.isActive;
}
}
