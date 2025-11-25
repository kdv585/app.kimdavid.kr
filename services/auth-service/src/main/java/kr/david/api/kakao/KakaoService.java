package kr.david.api.kakao;

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
public class KakaoService {

	private final WebClient webClient;

	@Value("${kakao.client-id}")
	private String clientId;

	@Value("${kakao.client-secret}")
	private String clientSecret;

	@Value("${kakao.redirect-uri}")
	private String redirectUri;

	@Value("${kakao.auth-url}")
	private String authUrl;

	@Value("${kakao.token-url}")
	private String tokenUrl;

	@Value("${kakao.user-info-url}")
	private String userInfoUrl;

	public KakaoService(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.build();
	}

	/**
	 * 카카오 로그인 URL 생성
	 */
	public String getKakaoLoginUrl(String frontendRedirectUri) {
		String state = "random_state_string"; // CSRF 방지를 위한 state 값 (실제로는 랜덤 생성)
		String kakaoRedirectUri = redirectUri; // 백엔드 콜백 URI
		
		return authUrl + "?client_id=" + clientId
				+ "&redirect_uri=" + URLEncoder.encode(kakaoRedirectUri, StandardCharsets.UTF_8)
				+ "&response_type=code"
				+ "&state=" + state;
	}

	/**
	 * 인가 코드로 액세스 토큰 교환
	 */
	public KakaoTokenResponse getAccessToken(String code) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", clientId);
		params.add("client_secret", clientSecret);
		params.add("redirect_uri", redirectUri);
		params.add("code", code);

		KakaoTokenResponse response = webClient.post()
				.uri(tokenUrl)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(BodyInserters.fromFormData(params))
				.retrieve()
				.bodyToMono(KakaoTokenResponse.class)
				.block();

		if (response == null || response.getAccessToken() == null) {
			throw new RuntimeException("카카오 액세스 토큰 발급 실패");
		}

		return response;
	}

	/**
	 * 액세스 토큰으로 사용자 정보 조회
	 */
	public KakaoUserInfo getUserInfo(String accessToken) {
		KakaoUserInfoResponse response = webClient.get()
				.uri(userInfoUrl)
				.header("Authorization", "Bearer " + accessToken)
				.retrieve()
				.bodyToMono(KakaoUserInfoResponse.class)
				.block();

		if (response == null || response.getKakaoAccount() == null) {
			throw new RuntimeException("카카오 사용자 정보 조회 실패");
		}

		return new KakaoUserInfo(
				response.getId(),
				response.getKakaoAccount().getEmail(),
				response.getKakaoAccount().getProfile().getNickname(),
				response.getKakaoAccount().getProfile().getProfileImageUrl()
		);
	}
}

