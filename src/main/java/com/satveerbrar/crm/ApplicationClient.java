package com.satveerbrar.crm;

import javafx.beans.property.SimpleStringProperty;

public class ApplicationClient {
    private final SimpleStringProperty firstName;
    private final SimpleStringProperty lastName;
    private final SimpleStringProperty applicationType;
    private final SimpleStringProperty applicationStatus;
    private final SimpleStringProperty submissionDate;
    private final SimpleStringProperty priority;
    private final SimpleStringProperty notes;

    public ApplicationClient(String firstName, String lastName, String applicationType, String applicationStatus, String submissionDate, String priority , String notes){
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.applicationType = new SimpleStringProperty(applicationType);
        this.applicationStatus = new SimpleStringProperty(applicationStatus);
        this.submissionDate = new SimpleStringProperty(submissionDate);
        this.priority = new SimpleStringProperty(priority);
        this.notes = new SimpleStringProperty(notes);
    }

    public String getFirstName() {
        return firstName.get();
    }

    public SimpleStringProperty firstNameProperty() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public String getLastName() {
        return lastName.get();
    }

    public SimpleStringProperty lastNameProperty() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public String getApplicationType() {
        return applicationType.get();
    }

    public SimpleStringProperty applicationTypeProperty() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType.set(applicationType);
    }

    public String getApplicationStatus() {
        return applicationStatus.get();
    }

    public SimpleStringProperty applicationStatusProperty() {
        return applicationStatus;
    }

    public void setApplicationStatus(String applicationStatus) {
        this.applicationStatus.set(applicationStatus);
    }

    public String getSubmissionDate() {
        return submissionDate.get();
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

    public SimpleStringProperty priorityProperty() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority.set(priority);
    }

    public String getNotes() {
        return notes.get();
    }

    public SimpleStringProperty notesProperty() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes.set(notes);
    }
}
