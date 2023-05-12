package it.polimi.tiw.project.beans;

import java.io.Serializable;

public class Album implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String title;
	private String image;
	private String interpreter;
	private int publicationYear;
	
	public int getId() {
		return this.id;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String getImage() {
		return this.image;
	}
	
	public String getInterpreter() {
		return this.interpreter;
	}
	
	public int getPublicationYear() {
		return this.publicationYear;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setImage(String image) {
		this.image = image;
	}
	
	public void setInterpreter(String interpreter) {
		this.interpreter = interpreter;
	}
	
	public void setPublicationYear(int publicationYear) {
		this.publicationYear = publicationYear;
	}
}