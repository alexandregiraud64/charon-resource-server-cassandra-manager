package com.agiraud.charon.resource.cassandra.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.agiraud.charon.core.dao.SessionService;
import com.agiraud.charon.core.dto.User;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AdminRightsValidator implements Validator {
    @Autowired
    private SessionService sessionService;

    public boolean isAdmin() {
    	log.info("Checking if the user is an admin");
        return sessionService.getPrincipalAsCustomUserPrincipal().getRoles().contains("ROLE_ADMIN");
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
    	log.info("Checking if the data are correct before adding the user into the database");
    	
    	if (!sessionService.getPrincipalAsCustomUserPrincipal().getRoles().contains("ROLE_ADMIN")) {
            errors.rejectValue("global", "Unauthorized.global");
        }
    }
}
