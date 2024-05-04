package com.satveerbrar.crm;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Represents a client's application with properties bindable in a JavaFX user interface. This class
 * stores details about the application such as name, type, status, etc., and provides property
 * accessors that facilitate UI updates when data changes.
 */
public class ApplicationClient {

  private final SimpleIntegerProperty id;
  private final SimpleStringProperty firstName;
  private final SimpleStringProperty lastName;
  private final SimpleStringProperty applicationType;
  private final SimpleStringProperty applicationStatus;
  private final SimpleStringProperty submissionDate;
  private final SimpleStringProperty priority;
  private final SimpleStringProperty notes;

  public ApplicationClient(
      int id,
      String firstName,
      String lastName,
      String applicationType,
      String applicationStatus,
      String submissionDate,
      String priority,
      String notes) {
    this.id = new SimpleIntegerProperty(id);
    this.firstName = new SimpleStringProperty(firstName);
    this.lastName = new SimpleStringProperty(lastName);
    this.applicationType = new SimpleStringProperty(applicationType);
    this.applicationStatus = new SimpleStringProperty(applicationStatus);
    this.submissionDate = new SimpleStringProperty(submissionDate);
    this.priority = new SimpleStringProperty(priority);
    this.notes = new SimpleStringProperty(notes);
  }

  public int getId() {
    return id.get();
  }

  public void setId(int id) {
    this.id.set(id);
  }

  public SimpleIntegerProperty idProperty() {
    return id;
  }

  public String getFirstName() {
    return firstName.get();
  }

  public void setFirstName(String firstName) {
    this.firstName.set(firstName);
  }

  public SimpleStringProperty firstNameProperty() {
    return firstName;
  }

  public String getLastName() {
    return lastName.get();
  }

  public void setLastName(String lastName) {
    this.lastName.set(lastName);
  }

  public SimpleStringProperty lastNameProperty() {
    return lastName;
  }

  public String getApplicationType() {
    return applicationType.get();
  }

  public void setApplicationType(String applicationType) {
    this.applicationType.set(applicationType);
  }

  public SimpleStringProperty applicationTypeProperty() {
    return applicationType;
  }

  public String getApplicationStatus() {
    return applicationStatus.get();
  }

  public void setApplicationStatus(String applicationStatus) {
    this.applicationStatus.set(applicationStatus);
  }

  public SimpleStringProperty applicationStatusProperty() {
    return applicationStatus;
  }

  public String getSubmissionDate() {
    return submissionDate.get();
  }

  public void setSubmissionDate(String submissionDate) {
    this.submissionDate.set(submissionDate);
  }

  public SimpleStringProperty submissionDateProperty() {
    return submissionDate;
  }

  public void setSubmission_date(String submissionDate) {
    this.submissionDate.set(submissionDate);
  }

  public String getPriority() {
    return priority.get();
  }

  public void setPriority(String priority) {
    this.priority.set(priority);
  }

  public SimpleStringProperty priorityProperty() {
    return priority;
  }

  public String getNotes() {
    return notes.get();
  }

  public void setNotes(String notes) {
    this.notes.set(notes);
  }

  public SimpleStringProperty notesProperty() {
    return notes;
  }

  /**
   * Checks if any of the textual properties of this application match the given search text.
   *
   * @param searchText The text to search for within the application's properties.
   * @return true if any property contains the search text, false otherwise.
   */
  public boolean matchesSearch(String searchText) {
    return getFirstName().toLowerCase().contains(searchText.toLowerCase())
        || getLastName().toLowerCase().contains(searchText.toLowerCase())
        || getApplicationType().toLowerCase().contains(searchText.toLowerCase())
        || getApplicationStatus().toLowerCase().contains(searchText.toLowerCase())
        || getSubmissionDate().contains(searchText)
        || getPriority().toLowerCase().contains(searchText.toLowerCase())
        || (getNotes() != null && getNotes().toLowerCase().contains(searchText.toLowerCase()));
  }
}
