package com.satveerbrar.crm;

import javafx.beans.property.SimpleStringProperty;

public class Client {
    private final SimpleStringProperty firstName;
    private final SimpleStringProperty lastName;
    private final SimpleStringProperty email;
    private final SimpleStringProperty phoneNumber;
    private final SimpleStringProperty reference;
    private final SimpleStringProperty citizenship;
    private final SimpleStringProperty date;
    private final SimpleStringProperty notes;

    public Client(String firstName, String lastName, String email, String phoneNumber, String reference, String citizenship, String date, String notes) {
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.email = new SimpleStringProperty(email);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.reference = new SimpleStringProperty(reference);
        this.citizenship = new SimpleStringProperty(citizenship);
        this.date = new SimpleStringProperty(date);
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

    public String getEmail() {
        return email.get();
    }

    public SimpleStringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public SimpleStringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }

    public String getReference() {
        return reference.get();
    }

    public SimpleStringProperty referenceProperty() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference.set(reference);
    }

    public String getCitizenship() {
        return citizenship.get();
    }

    public SimpleStringProperty citizenshipProperty() {
        return citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship.set(citizenship);
    }

    public String getDate() {
        return date.get();
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public void setDate(String date) {
        this.date.set(date);
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
