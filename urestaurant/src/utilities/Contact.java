package utilities;

import java.util.ArrayList;
import java.util.List;

public class Contact {
	private int id;
	private String firstName;
	private String lastName;
	private String primaryEmail;
	private List<String> emails;
	private List<String> phoneNumbers;
	private int photoId;
	private int userLinkId;
	
	public Contact() {
		emails = new ArrayList<>();
		phoneNumbers = new ArrayList<>();
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
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public List<String> getEmails() {
		return emails;
	}
	public void setEmails(List<String> emails) {
		this.emails = emails;
	}
	public List<String> getPhoneNumbers() {
		return phoneNumbers;
	}
	public void setPhoneNumbers(List<String> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}
	public int getPhotoId() {
		return photoId;
	}
	public void setPhotoId(int photoId) {
		this.photoId = photoId;
	}

	public int getUserLinkId() {
		return userLinkId;
	}

	public void setUserLinkId(int userLinkId) {
		this.userLinkId = userLinkId;
	}

	public String getPrimaryEmail() {
		return primaryEmail;
	}
	
	public void setPrimaryEmail(String primaryEmail) {
		this.primaryEmail = primaryEmail;
	}
}
