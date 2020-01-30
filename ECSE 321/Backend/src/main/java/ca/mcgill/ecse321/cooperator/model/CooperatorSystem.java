package ca.mcgill.ecse321.cooperator.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class CooperatorSystem{
   private Set<Offer> offers;
   
   @OneToMany(cascade={CascadeType.ALL})
   public Set<Offer> getOffers() {
      return this.offers;
   }
   
   public void setOffers(Set<Offer> offerss) {
      this.offers = offerss;
   }
   
   private Set<Person> users;
   
   @OneToMany(cascade={CascadeType.ALL})
   public Set<Person> getUsers() {
      return this.users;
   }
   
   public void setUsers(Set<Person> userss) {
      this.users = userss;
   }
   
   private Set<Document> documents;
   
   @OneToMany(cascade={CascadeType.ALL})
   public Set<Document> getDocuments() {
      return this.documents;
   }
   
   public void setDocuments(Set<Document> documentss) {
      this.documents = documentss;
   }
   
   private Set<SpecificInternship> specificInternship;
   
   @OneToMany(cascade={CascadeType.ALL})
   public Set<SpecificInternship> getSpecificInternship() {
      return this.specificInternship;
   }
   
   public void setSpecificInternship(Set<SpecificInternship> specificInternships) {
      this.specificInternship = specificInternships;
   }
   
   private Set<ScheduledInternship> scheduledInternships;
   
   @OneToMany(cascade={CascadeType.ALL})
   public Set<ScheduledInternship> getScheduledInternships() {
      return this.scheduledInternships;
   }
   
   public void setScheduledInternships(Set<ScheduledInternship> scheduledInternshipss) {
      this.scheduledInternships = scheduledInternshipss;
   }
   
   private Integer systemId;

public void setSystemId(Integer value) {
    this.systemId = value;
}
@Id
public Integer getSystemId() {
    return this.systemId;
}
   private Set<Student> student;
   
   @OneToMany(cascade={CascadeType.ALL})
   public Set<Student> getStudent() {
      return this.student;
   }
   
   public void setStudent(Set<Student> students) {
      this.student = students;
   }
   
   }
