package com.agiraud.charon.resource.cassandra.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.WebRequest;

import com.agiraud.charon.core.dao.SecurityService;
import com.agiraud.charon.core.dao.UserService;
import com.agiraud.charon.core.dto.User;
import com.agiraud.charon.resource.cassandra.validator.AdminRightsValidator;
import com.agiraud.charon.resource.cassandra.validator.UserCreateValidator;
import com.agiraud.charon.resource.operations.UserOperations;

import java.util.Collection;
import java.util.UUID;

@Controller
public class UserController implements UserOperations {
	/* ************************************************************************* */
	// ATTRIBUTES
	/* ************************************************************************* */
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserCreateValidator userCreateValidator;

    @Autowired
    private AdminRightsValidator adminRightsValidator;

	/* ************************************************************************* */
	// REQUEST MAPPING
	/* ************************************************************************* */
	@Override
	public String createForm(WebRequest request, Model model) {
    	User user = new User();		
	    model.addAttribute("user", user);
	    return "form-registration-user.html";
	}

	@Override
	public String createSubmit(User user, BindingResult bindingResult, Model model) {
        userCreateValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            return "form-registration-user.html";
        }
        
        String password = user.getPassword();
        String username = user.getUsername();

        user.setPassword(passwordEncoder.encode(password));
        userService.create(user, false);
		securityService.autoLogin(username, password);
		
    	model.addAttribute("success", "Created.user");
        return "redirect:/";
	}

	@Override
	public String getAll(WebRequest request, Model model) {
    	Collection<User> users = userService.getAll();		
	    model.addAttribute("users", users);
	    return "panel-manage-users.html";
	}

	@Override
	public String upgrade(UUID userId, Model model) {
        if (!adminRightsValidator.isAdmin()) {
            model.addAttribute("error", "Unauthorized.notEnoughRights");
        } else {
        	userService.updateToAdmin(userId);
        	model.addAttribute("success", "Updated.user");
        }
        
		Collection<User> users = userService.getAll();		
	    model.addAttribute("users", users);

        return "panel-manage-users.html";
	}

	@Override
	public String deleteById(UUID userId, Model model) {

        if (!adminRightsValidator.isAdmin()) {
            model.addAttribute("error", "Unauthorized.notEnoughRights");
        } else {
        	userService.deleteById(userId);
            model.addAttribute("success", "Deleted.user");
        }

		Collection<User> users = userService.getAll();		
	    model.addAttribute("users", users);
	    
        return "panel-manage-users.html";
	}
}
