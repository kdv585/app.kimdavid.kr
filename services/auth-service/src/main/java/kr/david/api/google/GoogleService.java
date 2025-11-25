package kr.david.api.google;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class GoogleService {

	private final WebClient webClient;

	@Value("${google.client-id}")
	private String clientId;

	@Value("${google.client-secret}")
	private String clientSecret;

	@Value("${google.redirect-uri}")
	private String redirectUri;

	@Value("${google.auth-url}")
	private String authUrl;

	@Value("${google.token-url}")
	private String tokenUrl;

	@Value("${google.user-info-url}")
	private String userInfoUrl;

	@Value("${google.scope}")
	private String scope;

	public GoogleService(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.build();
	}

	/**
	 * 구글 로그인 URL 생성
	 */
	public String getGoogleLoginUrl(String frontendRedirectUri) {
		String state = "random_state_string"; // CSRF 방지를 위한 state 값 (실제로는 랜덤 생성)
		String googleRedirectUri = redirectUri; // 백엔드 콜백 URI
		
		return authUrl + "?client_id=" + clientId
				+ "&redirect_uri=" + URLEncoder.encode(googleRedirectUri, StandardCharsets.UTF_8)
				+ "&response_type=code"
				+ "&scope=" + URLEncoder.encode(scope, StandardCharsets.UTF_8)
				+ "&state=" + state
				+ "&access_type=offline"
				+ "&prompt=consent";
	}

	/**
	 * 인가 코드로 액세스 토큰 교환
	 */
	public GoogleTokenResponse getAccessToken(String code) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", clientId);
		params.add("client_secret", clientSecret);
		params.add("redirect_uri", redirectUri);
		params.add("code", code);

		GoogleTokenResponse response = webClient.post()
				.uri(tokenUrl)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(BodyInserters.fromFormData(params))
				.retrieve()
				.bodyToMono(GoogleTokenResponse.class)
				.block();

		if (response == null || response.getAccessToken() == null) {
			throw new RuntimeException("구글 액세스 토큰 발급 실패");
		}

		return response;
	}

	/**
	 * 액세스 토큰으로 사용자 정보 조회
	 */
	public GoogleUserInfo getUserInfo(String accessToken) {
		GoogleUserInfoResponse response = webClient.get()
				.uri(userInfoUrl)
				.header("Authorization", "Bearer " + accessToken)
				.retrieve()
				.bodyToMono(GoogleUserInfoResponse.class)
				.block();

		if (response == null || response.getEmail() == null) {
			throw new RuntimeException("구글 사용자 정보 조회 실패");
		}

		return new GoogleUserInfo(
				response.getId(),
				response.getEmail(),
				response.getName(),
				response.getPicture(),
				response.getVerifiedEmail()
		);
	}
}

