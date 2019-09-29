package com.agiraud.charon.resource.cassandra.controller;

import com.agiraud.charon.core.dao.ClientService;
import com.agiraud.charon.core.dao.ScopeService;
import com.agiraud.charon.core.dto.Client;
import com.agiraud.charon.resource.cassandra.validator.AdminRightsValidator;
import com.agiraud.charon.resource.cassandra.validator.ClientCreateValidator;
import com.agiraud.charon.resource.operations.ClientOperations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.WebRequest;

import java.util.Collection;

@Controller
public class ClientController implements ClientOperations {
	/* ************************************************************************* */
	// ATTRIBUTES
	/* ************************************************************************* */
	@Autowired
    private ClientService clientService;
	
	@Autowired
    private ScopeService scopeService;

    @Autowired
    private ClientCreateValidator clientCreateValidator;

    @Autowired
    private AdminRightsValidator adminRightsValidator;

	/* ************************************************************************* */
	// REQUEST MAPPING
	/* ************************************************************************* */
	public String createForm(WebRequest request, Model model) {
		Client client = new Client();		
	    model.addAttribute("client", client);
	    model.addAttribute("scopes", scopeService.getAll());
	    
	    return "form-registration-client.html";
	}
	
    public String createSubmit(@ModelAttribute Client client, BindingResult bindingResult, Model model) {
    	clientCreateValidator.validate(client, bindingResult);

        if (bindingResult.hasErrors()) {
            return "form-registration-client.html";
        }
        
        clientService.create(client);
	    model.addAttribute("scopes", scopeService.getAll());
    	model.addAttribute("success", "Created.client");

    	Collection<Client> clients = clientService.getAllClientsForAuthenticatedUser();		
	    model.addAttribute("clients", clients);
        return "panel-manage-clients.html";
	}
    
	public String getAll(WebRequest request, Model model) {
    	Collection<Client> clients = clientService.getAllClientsForAuthenticatedUser();		
	    model.addAttribute("clients", clients);
	    return "panel-manage-clients.html";
	}
    
	public String deleteById(@PathVariable String clientId,Model model) {
        if (!adminRightsValidator.isAdmin()) {
            model.addAttribute("error", "Unauthorized.notEnoughRights");
        } else {
    		clientService.deleteById(clientId);
            model.addAttribute("success", "Deleted.client");
        }

    	Collection<Client> clients = clientService.getAllClientsForAuthenticatedUser();		
	    model.addAttribute("clients", clients);

	    return "panel-manage-clients.html";
	}

}
