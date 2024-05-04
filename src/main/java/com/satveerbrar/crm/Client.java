package com.satveerbrar.crm;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Represents a client with observable properties that can be bound to a JavaFX UI. This class
 * includes properties for client details like name, email, phone number, and others, enabling
 * automatic updates to the UI when these properties change.
 */
public class Client {

  private final SimpleIntegerProperty id;
  private final SimpleStringProperty firstName;
  private final SimpleStringProperty lastName;
  private final SimpleStringProperty email;
  private final SimpleStringProperty phoneNumber;
  private final SimpleStringProperty reference;
  private final SimpleStringProperty citizenship;
  private final SimpleStringProperty date;
  private final SimpleStringProperty notes;

  public Client(
      int id,
      String firstName,
      String lastName,
      String email,
      String phoneNumber,
      String reference,
      String citizenship,
      String date,
      String notes) {
    this.id = new SimpleIntegerProperty(id);
    this.firstName = new SimpleStringProperty(firstName);
    this.lastName = new SimpleStringProperty(lastName);
    this.email = new SimpleStringProperty(email);
    this.phoneNumber = new SimpleStringProperty(phoneNumber);
    this.reference = new SimpleStringProperty(reference);
    this.citizenship = new SimpleStringProperty(citizenship);
    this.date = new SimpleStringProperty(date);
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

  public String getEmail() {
    return email.get();
  }

  public void setEmail(String email) {
    this.email.set(email);
  }

  public SimpleStringProperty emailProperty() {
    return email;
  }

  public String getPhoneNumber() {
    return phoneNumber.get();
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber.set(phoneNumber);
  }

  public SimpleStringProperty phoneNumberProperty() {
    return phoneNumber;
  }

  public String getReference() {
    return reference.get();
  }

  public void setReference(String reference) {
    this.reference.set(reference);
  }

  public SimpleStringProperty referenceProperty() {
    return reference;
  }

  public String getCitizenship() {
    return citizenship.get();
  }

  public void setCitizenship(String citizenship) {
    this.citizenship.set(citizenship);
  }

  public SimpleStringProperty citizenshipProperty() {
    return citizenship;
  }

  public String getDate() {
    return date.get();
  }

  public void setDate(String date) {
    this.date.set(date);
  }

  public SimpleStringProperty dateProperty() {
    return date;
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
   * Determines if the client's details match the given search text. This search is case-insensitive
   * and checks if the search text is contained within any of the client's relevant properties.
   *
   * @param searchText The text to search within the client's properties.
   * @return true if any property contains the search text; false otherwise.
   */
  public boolean matchesSearch(String searchText) {
    return getFirstName().toLowerCase().contains(searchText.toLowerCase())
        || getLastName().toLowerCase().contains(searchText.toLowerCase())
        || getEmail().toLowerCase().contains(searchText.toLowerCase())
        || getPhoneNumber().contains(searchText)
        || getReference().toLowerCase().contains(searchText.toLowerCase())
        || getCitizenship().toLowerCase().contains(searchText.toLowerCase())
        || getDate().contains(searchText)
        || (getNotes() != null && getNotes().toLowerCase().contains(searchText.toLowerCase()));
  }
}
