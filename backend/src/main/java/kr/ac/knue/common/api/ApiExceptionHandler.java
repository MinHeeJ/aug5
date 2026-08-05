package kr.ac.knue.common.api;

import java.util.Map;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(IllegalArgumentException.class)
  ResponseEntity<Map<String,Object>> validation(IllegalArgumentException e) {
    return ResponseEntity.badRequest().body(Map.of("error", "VALIDATION_ERROR", "message", message(e)));
  }

  @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
  ResponseEntity<Map<String,Object>> unauthorized(Exception e) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "UNAUTHORIZED", "message", "아이디 또는 비밀번호를 확인해 주세요."));
  }

  private String message(Exception e) {
    return e.getMessage() == null ? "요청을 확인해 주세요" : e.getMessage();
  }
}
