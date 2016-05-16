package utilities;

import java.sql.Timestamp;

public class Event {
	
	private String name;
	private String location;
	private String description;
	private Timestamp startDateTime;
	private Timestamp endDateTime;
	private boolean isPublic;
	private StringBuilder jsonValidation;
	
	public Event() { 
		jsonValidation = new StringBuilder();
	}
	
	public String getName() { return name; }
	public void setName(String name) {
		this.name = name;
	}
	public String getLocation() { return location; }
	public void setLocation(String location) {
		this.location = location;
	}
	public String getDescription() { return description; }
	public void setDescription(String description) {
		this.description = description;
	}
	public Timestamp getStartDateTime() { return startDateTime; }
	public void setStartDateTime(Timestamp startDateTime) {
		this.startDateTime = startDateTime;
	}
	public Timestamp getEndDateTime() { return endDateTime; }
	public void setEndDateTime(Timestamp endDateTime) {
		this.endDateTime = endDateTime;
	}
	public boolean isPublic() { return isPublic; }
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	public StringBuilder getJsonValidation() { return jsonValidation; }
	public void setJsonValidation(StringBuilder jsonValidation) {
		this.jsonValidation = jsonValidation;
	}
}
