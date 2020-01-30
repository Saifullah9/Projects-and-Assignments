package ca.mcgill.ecse321.cooperator.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class SpecificInternship{
   private boolean isCompleted;

public void setIsCompleted(boolean value) {
    this.isCompleted = value;
}
public boolean isIsCompleted() {
    return this.isCompleted;
}
private Integer year;

public void setYear(Integer value) {
    this.year = value;
}
public Integer getYear() {
    return this.year;
}
private ScheduledInternship scheduledInternship;

@ManyToOne(optional=false)
public ScheduledInternship getScheduledInternship() {
   return this.scheduledInternship;
}

public void setScheduledInternship(ScheduledInternship scheduledInternship) {
   this.scheduledInternship = scheduledInternship;
}

private Set<Document> reports;

@OneToMany
public Set<Document> getReports() {
   return this.reports;
}

public void setReports(Set<Document> reportss) {
   this.reports = reportss;
}

private Student student;

@ManyToOne(optional=false)
public Student getStudent() {
   return this.student;
}

public void setStudent(Student student) {
   this.student = student;
}

private String internshipId;

public void setInternshipId(String value) {
    this.internshipId = value;
}
@Id
public String getInternshipId() {
    return this.internshipId;
}
private boolean isActive;

public void setIsActive(boolean value) {
    this.isActive = value;
}
public boolean isIsActive() {
    return this.isActive;
}
}
