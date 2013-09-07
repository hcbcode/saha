package com.hcb.saha.internal.data.model;

/**
 * Represents a user
 *
 * @author Andreas Borglin
 */
public class User {

	public static User createUser(String firstName, String surname,
			String googleAccount) {
		User user = new User();
		user.setFirstName(firstName);
		user.setSurName(surname);
		user.setGoogleAccount(googleAccount);
		return user;
	}

	// Id maps directly to database id
	private int id;

	// First name
	private String firstName;

	// Surname
	private String surname;

	// Attached Google account
	private String googleAccount;

	public User() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getSurName() {
		return surname;
	}

	public void setSurName(String surName) {
		this.surname = surName;
	}

	public String getGoogleAccount() {
		return googleAccount;
	}

	public void setGoogleAccount(String googleAccount) {
		this.googleAccount = googleAccount;
	}

	public String getDirectoryId() {
		return id + firstName;
	}

	@Override
	public String toString() {
		return firstName + " " + surname;
	}

}
