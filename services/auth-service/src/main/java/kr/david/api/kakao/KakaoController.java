package kr.david.api.kakao;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/kakao")
public class KakaoController {

    private final KakaoService kakaoService;

    public KakaoController(KakaoService kakaoService) {
        this.kakaoService = kakaoService;
    }

    /**
     * 카카오 로그인 시작
     * 프론트엔드에서 redirect_uri와 함께 호출
     * 예: GET /api/auth/kakao?redirect_uri=http://localhost:3000/auth/kakao/callback
     */
    @GetMapping
    public RedirectView kakaoLogin(
            @RequestParam(required = false, defaultValue = "http://localhost:3000/auth/kakao/callback") String redirect_uri,
            HttpSession session) {
        System.out.println("카카오 로그인 요청: redirect_uri=" + redirect_uri);
        // 프론트엔드 redirect_uri를 세션에 저장
        session.setAttribute("frontend_redirect_uri", redirect_uri);

        // 카카오 인증 페이지로 리다이렉트
        String loginUrl = kakaoService.getKakaoLoginUrl(redirect_uri);
        return new RedirectView(loginUrl);
    }

    /**
     * 카카오 로그인 콜백 처리
     * 카카오 인증 후 리다이렉트되는 엔드포인트
     * 프론트엔드로 code와 함께 리다이렉트
     */
    @GetMapping("/callback")
    public RedirectView kakaoCallback(
            @RequestParam String code,
            @RequestParam(required = false) String state,
            HttpSession session) {
        try {
            // 세션에서 프론트엔드 redirect_uri 가져오기
            String frontendRedirectUri = (String) session.getAttribute("frontend_redirect_uri");
            if (frontendRedirectUri == null) {
                frontendRedirectUri = "http://localhost:3000/auth/kakao/callback";
            }

            // code를 프론트엔드로 전달
            String redirectUrl = frontendRedirectUri + "?code=" + URLEncoder.encode(code, StandardCharsets.UTF_8);
            return new RedirectView(redirectUrl);
        } catch (Exception e) {
            // 에러 발생 시 프론트엔드로 에러 전달
            String frontendRedirectUri = (String) session.getAttribute("frontend_redirect_uri");
            if (frontendRedirectUri == null) {
                frontendRedirectUri = "http://localhost:3000/auth/kakao/callback";
            }
            String redirectUrl = frontendRedirectUri + "?error="
                    + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            return new RedirectView(redirectUrl);
        }
    }

    /**
     * 카카오 로그인 완료 처리
     * 프론트엔드에서 code를 받아서 로그인 처리
     * POST /api/auth/kakao/login
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> kakaoLoginComplete(@RequestBody Map<String, String> request) {
        try {
            String code = request.get("code");
            if (code == null || code.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "code 파라미터가 필요합니다");
                return ResponseEntity.badRequest().body(response);
            }

            // 카카오 액세스 토큰 발급
            KakaoTokenResponse tokenResponse = kakaoService.getAccessToken(code);

            // 사용자 정보 조회
            KakaoUserInfo userInfo = kakaoService.getUserInfo(tokenResponse.getAccessToken());

            // TODO: 사용자 정보를 DB에 저장/조회하고 JWT 토큰 발급
            // 여기서는 간단히 사용자 정보만 반환

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("user", userInfo);
            response.put("accessToken", tokenResponse.getAccessToken());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
