package com.agiraud.charon.resource.cassandra.validator;

import java.util.Collection;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.agiraud.charon.core.dao.ScopeService;
import com.agiraud.charon.core.dao.SessionService;
import com.agiraud.charon.core.dto.Client;
import com.agiraud.charon.core.dto.Scope;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ClientCreateValidator implements Validator {
    @Autowired
    private ScopeService scopeService;

    @Autowired
    private SessionService sessionService;

    @Override
    public boolean supports(Class<?> aClass) {
        return Client.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
    	log.info("Checking if the data are correct before adding the client into the database");
    	Client client = (Client) o;

        boolean isAdmin = sessionService.getPrincipalAsCustomUserPrincipal().getRoles().contains("ROLE_ADMIN");

    	validateGrantType(errors, client, isAdmin);
    	validateScopes(errors, client);
    	
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "displayName", "NotEmpty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "clientSecret", "NotEmpty");

        if (!client.getClientSecretConfirm().equals(client.getClientSecret())) {
            errors.rejectValue("clientSecretConfirm", "Diff.clientForm.clientSecretConfirm");
        }	
		
    }

	private void validateScopes(Errors errors, Client client) {
		Collection<Scope> scopes = scopeService.getAll();
		Set<String> clientScopes = client.getScope();
		for (String scopeClient : clientScopes) {
			if(!scopes.stream().map(Scope::getName).filter(scopeClient::equals).findFirst().isPresent()) {
				errors.rejectValue("scopes", "NotFound.scope");
			}
		}
	}

	private void validateGrantType(Errors errors, Client client, boolean isAdmin) {
		Set<String> authorizedGrantTypes = client.getAuthorizedGrantTypes();
		for(String grantType : authorizedGrantTypes){
			switch(grantType) {
				case "authorization-code":
				case "refresh-token":
					// Nothing to do
					break;
				case "implicit":
				case "password":
				case "device-code":
					if(!isAdmin) {
						errors.rejectValue("global", "Unauthorized.global");
					}
					break;
				default:
					errors.rejectValue("global", "NotFound.grantType");
			}
		}
	}
}
