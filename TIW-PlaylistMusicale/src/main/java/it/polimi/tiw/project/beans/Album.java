package it.polimi.tiw.project.beans;

public class Album{
	
	private int id;
	private String name;
	private String album_image;
	private String interpreter;
	private int publication_year;
	
	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getImage() {
		return this.album_image;
	}
	
	public String getInterpreter() {
		return this.interpreter;
	}
	
	public int getYear() {
		return this.publication_year;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setImage(String image) {
		this.album_image = image;
	}
	
	public void setInterpreter(String interpreter) {
		this.interpreter = interpreter;
	}
	
	public void setYear(int year) {
		this.publication_year = year;
	}
}