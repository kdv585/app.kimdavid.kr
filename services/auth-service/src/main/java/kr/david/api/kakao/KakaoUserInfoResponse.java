package kr.david.api.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KakaoUserInfoResponse {
	
	@JsonProperty("id")
	private Long id;
	
	@JsonProperty("kakao_account")
	private KakaoAccount kakaoAccount;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public KakaoAccount getKakaoAccount() {
		return kakaoAccount;
	}

	public void setKakaoAccount(KakaoAccount kakaoAccount) {
		this.kakaoAccount = kakaoAccount;
	}

	public static class KakaoAccount {
		@JsonProperty("email")
		private String email;
		
		@JsonProperty("profile")
		private Profile profile;

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public Profile getProfile() {
			return profile;
		}

		public void setProfile(Profile profile) {
			this.profile = profile;
		}
	}

	public static class Profile {
		@JsonProperty("nickname")
		private String nickname;
		
		@JsonProperty("profile_image_url")
		private String profileImageUrl;

		public String getNickname() {
			return nickname;
		}

		public void setNickname(String nickname) {
			this.nickname = nickname;
		}

		public String getProfileImageUrl() {
			return profileImageUrl;
		}

		public void setProfileImageUrl(String profileImageUrl) {
			this.profileImageUrl = profileImageUrl;
		}
	}
}

