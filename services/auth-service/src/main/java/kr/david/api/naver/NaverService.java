package kr.david.api.naver;

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
public class NaverService {

	private final WebClient webClient;

	@Value("${naver.client-id}")
	private String clientId;

	@Value("${naver.client-secret}")
	private String clientSecret;

	@Value("${naver.redirect-uri}")
	private String redirectUri;

	@Value("${naver.auth-url}")
	private String authUrl;

	@Value("${naver.token-url}")
	private String tokenUrl;

	@Value("${naver.user-info-url}")
	private String userInfoUrl;

	public NaverService(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.build();
	}

	/**
	 * 네이버 로그인 URL 생성
	 */
	public String getNaverLoginUrl(String frontendRedirectUri) {
		String state = "random_state_string"; // CSRF 방지를 위한 state 값 (실제로는 랜덤 생성)
		String naverRedirectUri = redirectUri; // 백엔드 콜백 URI
		
		return authUrl + "?client_id=" + clientId
				+ "&redirect_uri=" + URLEncoder.encode(naverRedirectUri, StandardCharsets.UTF_8)
				+ "&response_type=code"
				+ "&state=" + state;
	}

	/**
	 * 인가 코드로 액세스 토큰 교환
	 */
	public NaverTokenResponse getAccessToken(String code, String state) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", clientId);
		params.add("client_secret", clientSecret);
		params.add("code", code);
		params.add("state", state);

		NaverTokenResponse response = webClient.post()
				.uri(tokenUrl)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(BodyInserters.fromFormData(params))
				.retrieve()
				.bodyToMono(NaverTokenResponse.class)
				.block();

		if (response == null || response.getAccessToken() == null) {
			String errorMsg = response != null && response.getError() != null 
					? response.getError() + ": " + response.getErrorDescription()
					: "네이버 액세스 토큰 발급 실패";
			throw new RuntimeException(errorMsg);
		}

		return response;
	}

	/**
	 * 액세스 토큰으로 사용자 정보 조회
	 */
	public NaverUserInfo getUserInfo(String accessToken) {
		NaverUserInfoResponse response = webClient.get()
				.uri(userInfoUrl)
				.header("Authorization", "Bearer " + accessToken)
				.retrieve()
				.bodyToMono(NaverUserInfoResponse.class)
				.block();

		if (response == null || response.getResponse() == null) {
			throw new RuntimeException("네이버 사용자 정보 조회 실패");
		}

		NaverUserInfoResponse.Response userResponse = response.getResponse();
		return new NaverUserInfo(
				userResponse.getId(),
				userResponse.getEmail(),
				userResponse.getName(),
				userResponse.getNickname(),
				userResponse.getProfileImage()
		);
	}
}

