package com.dailytrack.config;
import com.dailytrack.repository.UserRepo; import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager; import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity; import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.security.web.SecurityFilterChain;
@Configuration
public class SecurityConfig {
 @Bean PasswordEncoder pe(){ return new BCryptPasswordEncoder(); }
 @Bean AuthenticationManager am(AuthenticationConfiguration c) throws Exception { return c.getAuthenticationManager(); }
 @Bean UserDetailsService uds(UserRepo r){ return e->r.findByEmail(e.toLowerCase()).map(u->User.withUsername(u.email).password(u.passwordHash).roles("USER").build()).orElseThrow(()->new UsernameNotFoundException(e)); }
 @Bean SecurityFilterChain chain(HttpSecurity h) throws Exception {
  h.csrf(c->c.disable()) // session cookie + same-origin SPA; enable CSRF tokens before cross-origin use
   .authorizeHttpRequests(a->a.requestMatchers("/api/auth/login","/api/auth/register").permitAll().requestMatchers("/api/**").authenticated().anyRequest().permitAll())
   .exceptionHandling(e->e.authenticationEntryPoint((q,r,x)->{r.setStatus(401);r.setContentType("application/json");r.getWriter().write("{\"message\":\"Please sign in\"}");}))
   .logout(l->l.logoutUrl("/api/auth/logout").logoutSuccessHandler((q,r,a)->r.setStatus(200)));
  return h.build(); } }
