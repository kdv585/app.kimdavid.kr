package kr.david.api.google;

public class GoogleUserInfo {
	private String id;
	private String email;
	private String name;
	private String picture;
	private Boolean verifiedEmail;

	public GoogleUserInfo(String id, String email, String name, String picture, Boolean verifiedEmail) {
		this.id = id;
		this.email = email;
		this.name = name;
		this.picture = picture;
		this.verifiedEmail = verifiedEmail;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public Boolean getVerifiedEmail() {
		return verifiedEmail;
	}

	public void setVerifiedEmail(Boolean verifiedEmail) {
		this.verifiedEmail = verifiedEmail;
	}
}

