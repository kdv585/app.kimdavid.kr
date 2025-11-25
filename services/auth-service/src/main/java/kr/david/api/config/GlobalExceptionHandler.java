package kr.david.api.config;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 전역 예외 처리 및 에러 페이지 핸들러
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 404 Not Found 예외 처리
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoHandlerFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("status", 404);
        response.put("error", "Not Found");
        response.put("message", "요청한 리소스를 찾을 수 없습니다: " + ex.getRequestURL());
        response.put("path", ex.getRequestURL());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * 일반 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("status", 500);
        response.put("error", "Internal Server Error");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

/**
 * /error 엔드포인트 커스텀 핸들러
 */
@RestController
class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object path = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            response.put("status", statusCode);
            
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                response.put("error", "Not Found");
                response.put("message", "요청한 페이지를 찾을 수 없습니다.");
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                response.put("error", "Internal Server Error");
                response.put("message", "서버 내부 오류가 발생했습니다.");
            } else {
                response.put("error", "Error");
                response.put("message", message != null ? message.toString() : "오류가 발생했습니다.");
            }
            
            response.put("path", path != null ? path.toString() : "unknown");
            return ResponseEntity.status(statusCode).body(response);
        }
        
        response.put("status", 500);
        response.put("error", "Unknown Error");
        response.put("message", "알 수 없는 오류가 발생했습니다.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

