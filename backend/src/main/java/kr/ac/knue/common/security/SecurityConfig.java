package kr.ac.knue.common.security;

import jakarta.servlet.http.HttpServletResponse;
import kr.ac.knue.common.persistence.CommonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
  @Bean PasswordEncoder passwordEncoder() { return NoOpPasswordEncoder.getInstance(); }
  @Bean UserDetailsService userDetailsService(CommonMapper mapper) { return username -> java.util.Optional.ofNullable(mapper.findUser(username)).map(u -> User.withUsername((String)u.get("user_id")).password((String)u.get("password")).authorities("ROLE_" + u.getOrDefault("role_code", "R09")).disabled(!Boolean.TRUE.equals(u.get("system_enabled"))).build()).orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException(username)); }
  @Bean DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService users, PasswordEncoder encoder) { DaoAuthenticationProvider provider = new DaoAuthenticationProvider(); provider.setUserDetailsService(users); provider.setPasswordEncoder(encoder); return provider; }
  @Bean AuthenticationManager authenticationManager(DaoAuthenticationProvider provider) { return new ProviderManager(provider); }
  @Bean SecurityContextRepository securityContextRepository() { return new HttpSessionSecurityContextRepository(); }
  @Bean SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(c -> c.disable()).authorizeHttpRequests(a -> a.requestMatchers("/api/health", "/api/auth/**").permitAll().anyRequest().authenticated()).exceptionHandling(e -> e.authenticationEntryPoint((req,res,ex)->res.sendError(HttpServletResponse.SC_UNAUTHORIZED)).accessDeniedHandler((req,res,ex)->res.sendError(HttpServletResponse.SC_FORBIDDEN)));
    return http.build();
  }
}
