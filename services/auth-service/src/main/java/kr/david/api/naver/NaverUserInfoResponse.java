package kr.david.api.naver;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NaverUserInfoResponse {
	
	@JsonProperty("resultcode")
	private String resultCode;
	
	@JsonProperty("message")
	private String message;
	
	@JsonProperty("response")
	private Response response;

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public static class Response {
		@JsonProperty("id")
		private String id;
		
		@JsonProperty("email")
		private String email;
		
		@JsonProperty("name")
		private String name;
		
		@JsonProperty("nickname")
		private String nickname;
		
		@JsonProperty("profile_image")
		private String profileImage;
		
		@JsonProperty("gender")
		private String gender;
		
		@JsonProperty("birthday")
		private String birthday;
		
		@JsonProperty("birthyear")
		private String birthyear;
		
		@JsonProperty("mobile")
		private String mobile;

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

		public String getNickname() {
			return nickname;
		}

		public void setNickname(String nickname) {
			this.nickname = nickname;
		}

		public String getProfileImage() {
			return profileImage;
		}

		public void setProfileImage(String profileImage) {
			this.profileImage = profileImage;
		}

		public String getGender() {
			return gender;
		}

		public void setGender(String gender) {
			this.gender = gender;
		}

		public String getBirthday() {
			return birthday;
		}

		public void setBirthday(String birthday) {
			this.birthday = birthday;
		}

		public String getBirthyear() {
			return birthyear;
		}

		public void setBirthyear(String birthyear) {
			this.birthyear = birthyear;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}
	}
}

