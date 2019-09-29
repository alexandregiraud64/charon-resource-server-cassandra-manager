package com.agiraud.charon.resource.cassandra;

import java.util.Arrays;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.agiraud.charon.core.dao.ScopeService;
import com.agiraud.charon.core.dao.UserService;
import com.agiraud.charon.core.dto.Scope;
import com.agiraud.charon.core.dto.User;
import com.agiraud.charon.core.exception.EntityNotFoundException;
import com.datastax.driver.core.utils.UUIDs;

@SpringBootApplication(scanBasePackages={"com.agiraud.charon"})
@Configuration
public class ManagerApplication extends SpringBootServletInitializer {
	/* ************************************************************************* */
	// ATTRIBUTES
	/* ************************************************************************* */
	@Autowired
	private ScopeService scopeService;

	@Autowired
	private UserService userService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	/* ************************************************************************* */
	// MAIN
	/* ************************************************************************* */
	public static void main(String[] args) {
		SpringApplication.run(ManagerApplication.class, args);
	}

	/* ************************************************************************* */
	// BEANS
	/* ************************************************************************* */
	@Bean
	InitializingBean sendDatabase() {
		return () -> {
			// Create a scope "profile" if it doesn't already exist.
			try {
				scopeService.findByScopeName("profile");
			} catch(EntityNotFoundException ex) {
				Scope profile = new Scope();
				profile.setName("profile");
				profile.setDescription("The 'profile' scope give access to the personnal data composing your profile.");
				profile.setFields(Arrays.asList(
						"username", 
						"user ID",
						"full name",
						"is blocked",
						"profile image",
						"roles"
					));
				profile.setEnabled(true);
				scopeService.create(profile);
			}

			// Create a scope "email" if it doesn't already exist.
			try {
				scopeService.findByScopeName("email");
			} catch(EntityNotFoundException ex) {
				Scope email = new Scope();
				email.setName("email");
				email.setDescription("The 'email' scope give access to your email address.");
				email.setFields(Arrays.asList("emailAddress"));
				email.setEnabled(true);
				scopeService.create(email);
			}

			// Create a scope "phone" if it doesn't already exist.
			try {
				scopeService.findByScopeName("phone");
			} catch(EntityNotFoundException ex) {
				Scope phone = new Scope();
				phone.setName("phone");
				phone.setDescription("The 'phone' scope give access to your phone number.");
				phone.setFields(Arrays.asList("phoneNumber"));
				phone.setEnabled(true);
				scopeService.create(phone);
			}

			// Create a user "admin" if it doesn't already exist.
			try {
				userService.findByUsername("admin");
			} catch(EntityNotFoundException ex) {
				User admin = new User();
				admin.setUserId(UUIDs.timeBased());
				admin.setUsername("admin");
				admin.setPassword(passwordEncoder.encode("secret"));
				admin.setRoles(Arrays.asList("ROLE_USER", "ROLE_ADMIN"));
				admin.setEmailAddress("admin@example.com");
				admin.setFullName("Leroy Jenkins");
				admin.setEnabled(true);
				admin.setPhoneNumber("+33 6 00 00 00 00");
				userService.create(admin, true);
			}
		};
	}
}
