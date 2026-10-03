package com.dailytrack.exception;
import org.springframework.http.*; import org.springframework.security.core.AuthenticationException; import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*; import org.springframework.web.server.ResponseStatusException; import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {
 private ResponseEntity<Map<String,String>> r(HttpStatusCode s,String m){ return ResponseEntity.status(s).body(Map.of("message",m)); }
 @ExceptionHandler(ResponseStatusException.class) ResponseEntity<?> rs(ResponseStatusException e){ return r(e.getStatusCode(),e.getReason()==null?"Request failed":e.getReason()); }
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<?> v(MethodArgumentNotValidException e){ return r(HttpStatus.BAD_REQUEST,e.getBindingResult().getFieldErrors().get(0).getDefaultMessage()); }
 @ExceptionHandler(AuthenticationException.class) ResponseEntity<?> a(AuthenticationException e){ return r(HttpStatus.UNAUTHORIZED,"Invalid email or password"); }
 @ExceptionHandler(Exception.class) ResponseEntity<?> x(Exception e){ return r(HttpStatus.INTERNAL_SERVER_ERROR,"Something went wrong. Please try again."); } }
