package ca.mcgill.ecse321.cooperator.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Document{
   private String name;

public void setName(String value) {
    this.name = value;
}
public String getName() {
    return this.name;
}
private Integer documentId;

public void setDocumentId(Integer value) {
    this.documentId = value;
}
@Id
public Integer getDocumentId() {
    return this.documentId;
}
private String documentURL;

public void setDocumentURL(String value) {
    this.documentURL = value;
}
public String getDocumentURL() {
    return this.documentURL;
}
}
