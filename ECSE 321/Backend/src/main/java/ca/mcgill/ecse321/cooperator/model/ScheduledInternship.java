package ca.mcgill.ecse321.cooperator.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class ScheduledInternship{

@Enumerated(EnumType.STRING)
private Term term;

public void setTerm(Term value) {
this.term = value;
}
public Term getTerm() {
return this.term;
}
private int id;

public void setId(int value) {
this.id = value;
}
@Id
public int getId() {
return this.id;
}
   private String name;

public void setName(String value) {
    this.name = value;
}
public String getName() {
    return this.name;
}
private Set<SpecificInternship> specificInternships;

@OneToMany(mappedBy="scheduledInternship" ) // SHOULDN'T IT BE MAPPED BY SPECIFIC INTERNSHIP???
public Set<SpecificInternship> getSpecificInternships() {
   return this.specificInternships;
}

public void setSpecificInternships(Set<SpecificInternship> specificInternshipss) {
   this.specificInternships = specificInternshipss;
}

private String employer;

public void setEmployer(String value) {
    this.employer = value;
}
public String getEmployer() {
    return this.employer;
}
private String positionId;

public void setPositionId(String value) {
    this.positionId = value;
}
public String getPositionId() {
    return this.positionId;
}
}
