package com.saikumar.spboot.spboot;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.saikumar.spboot.spboot.filters.JwtRequestFilter;
import com.saikumar.spboot.spboot.services.MyUserDetailsService;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	DataSource dataSource;
	
	@Autowired
	private MyUserDetailsService myUserDetailsService;
	
	@Autowired
	private JwtRequestFilter jwtRequestFilter;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
//		auth.inMemoryAuthentication()
//		.withUser("blah")
//		.password("blah")
//		.roles("USER")
//		.and()
//		.withUser("foo")
//		.password("foo")
//		.roles("ADMIN");
		
//		auth.jdbcAuthentication()
//			.dataSource(dataSource)
//			.withDefaultSchema()
//			.withUser(
//					User.withUsername("user")
//					.password("pass")
//					.roles("USER")
//			)
//			.withUser(
//					User.withUsername("admin")
//					.password("pass")
//					.roles("ADMIN")
//			);
		
//		auth.jdbcAuthentication()
//		.dataSource(dataSource)
//		.usersByUsernameQuery("select email , password, enabled from user where email = ?")
//		.authoritiesByUsernameQuery("select email, role from user where email = ?");
		
		auth.userDetailsService(myUserDetailsService);
		
	}
	
	@Bean
	public PasswordEncoder getPasswordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
//		http.authorizeRequests()
//			.antMatchers("/start/all").hasRole("ADMIN")
//			.antMatchers("/start/user/**").hasRole("USER")
//			.antMatchers("/start/jello").permitAll()
//			.antMatchers("/start/authenticate").permitAll()
//			.antMatchers("/**").hasAnyRole("ADMIN", "USER")
//			.and().formLogin();
		http.csrf().disable().authorizeRequests()
			.antMatchers("/start/authenticate").permitAll().anyRequest().authenticated()
			.and().sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
	}
	
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	
}
