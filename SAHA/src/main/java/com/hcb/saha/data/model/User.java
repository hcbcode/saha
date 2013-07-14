package com.hcb.saha.data.model;

/**
 * Represents a user
 * @author Andreas Borglin
 */
public class User {
	
	// Id maps directly to database id
	private int id;
	// Name as registered by user
	private String name;
	// User directory path on sdcard
	private String directory;
	
	public User() {
		
	}
	
	public User(int id, String name, String directory) {
		this.id = id;
		this.name = name;
		this.directory = directory;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

}
