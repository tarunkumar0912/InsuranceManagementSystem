package com.example.insurance.config;

import com.example.insurance.security.JwtAuthenticationFilter;
import com.example.insurance.security.JwtService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;

	public SecurityConfig(JwtService jwtService, UserDetailsService userDetailsService) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		// NOTE: created with 'new' (not a Spring bean) so Spring Boot does NOT also
		// register it as a plain servlet filter outside the security chain.
		JwtAuthenticationFilter jwtAuthenticationFilter =
				new JwtAuthenticationFilter(jwtService, userDetailsService);

		http.csrf(csrf -> csrf.disable())

				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				.exceptionHandling(exception -> exception

						// 401 - no token, invalid token, or expired token
						.authenticationEntryPoint((request, response, authException) -> {
							response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
							response.setContentType("application/json");
							response.getWriter().write("{\"status\":401,\"error\":\"Unauthorized\","
									+ "\"message\":\"A valid JWT token is required\"}");
						})

						// 403 - valid token, but the role is not allowed (e.g. CUSTOMER calling an ADMIN endpoint)
						.accessDeniedHandler((request, response, accessDeniedException) -> {
							response.setStatus(HttpServletResponse.SC_FORBIDDEN);
							response.setContentType("application/json");
							response.getWriter().write("{\"status\":403,\"error\":\"Forbidden\","
									+ "\"message\":\"You do not have permission to access this resource\"}");
						}))

				.authorizeHttpRequests(auth -> auth

						// Public authentication endpoints
						.requestMatchers("/api/auth/**").permitAll()

						// Public API documentation
						.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

						// Public health check
						.requestMatchers("/actuator/health").permitAll()

						// Admin-only product operations
						.requestMatchers(HttpMethod.POST, "/api/products").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")

						// All other API requests require authentication
						.anyRequest().authenticated())

				.httpBasic(basic -> basic.disable()).formLogin(form -> form.disable())

				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}