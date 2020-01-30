package ca.mcgill.ecse321.cooperator.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Person{
   private String first;

public void setFirst(String value) {
    this.first = value;
}
public String getFirst() {
    return this.first;
}
private String last;

public void setLast(String value) {
    this.last = value;
}
public String getLast() {
    return this.last;
}
private String email;

public void setEmail(String value) {
    this.email = value;
}
@Id
public String getEmail() {
    return this.email;
}
   private Student student;
   
   @OneToOne(cascade = {CascadeType.ALL})
   public Student getStudent() {
      return this.student;
   }
   
   public void setStudent(Student student) {
      this.student = student;
   }
   
   }
