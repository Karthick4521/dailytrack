package com.dailytrack.controller;
import com.dailytrack.entity.AppUser; import com.dailytrack.repository.UserRepo;
import jakarta.servlet.http.HttpServletRequest; import jakarta.validation.Valid; import jakarta.validation.constraints.*; import java.time.LocalDate; import java.util.Map;
import org.springframework.http.HttpStatus; import org.springframework.security.authentication.*; import org.springframework.security.core.Authentication; import org.springframework.security.core.context.*;
import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.security.web.context.HttpSessionSecurityContextRepository; import org.springframework.web.bind.annotation.*; import org.springframework.web.server.ResponseStatusException;
@RestController @RequestMapping("/api/auth")
public class AuthController {
 public record Reg(@NotBlank(message="Name is required") String fullName,@Email(message="Enter a valid email") @NotBlank(message="Email is required") String email,@Size(min=8,message="Password needs at least 8 characters") String password,String confirmPassword){}
 public record Login(String email,String password){}
 private final UserRepo users; private final PasswordEncoder pe; private final AuthenticationManager am;
 public AuthController(UserRepo u,PasswordEncoder p,AuthenticationManager a){ users=u;pe=p;am=a; }
 @PostMapping("/register") public Map<String,Object> reg(@Valid @RequestBody Reg b,HttpServletRequest rq){
  if(!b.password().equals(b.confirmPassword())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Passwords do not match");
  String e=b.email().toLowerCase().trim(); if(users.existsByEmail(e)) throw new ResponseStatusException(HttpStatus.CONFLICT,"An account with this email already exists");
  AppUser u=new AppUser(); u.fullName=b.fullName().trim(); u.email=e; u.passwordHash=pe.encode(b.password()); u.createdAt=LocalDate.now(); users.save(u); return in(e,b.password(),rq); }
 @PostMapping("/login") public Map<String,Object> login(@RequestBody Login b,HttpServletRequest rq){ return in(b.email()==null?"":b.email().toLowerCase().trim(),b.password(),rq); }
 private Map<String,Object> in(String e,String p,HttpServletRequest rq){ Authentication a=am.authenticate(new UsernamePasswordAuthenticationToken(e,p));
  SecurityContext c=SecurityContextHolder.createEmptyContext(); c.setAuthentication(a); SecurityContextHolder.setContext(c);
  rq.getSession(true).setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,c); return Map.of("ok",true); } }
