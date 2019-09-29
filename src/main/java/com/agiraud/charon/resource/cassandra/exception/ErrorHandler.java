package com.agiraud.charon.resource.cassandra.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.agiraud.charon.core.exception.BadCredentialsException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public String exception(final BadCredentialsException authenticationException, final Model model) {
		log.info("Handle exception: "+authenticationException.toString());
        String errorMessage = (authenticationException != null ? authenticationException.getMessage() : "Unknown error");
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("loginError", true);
        return "login";
    }
    
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String InternalServerError(final Throwable throwable, final Model model) {
		log.info("Handle exception: "+throwable.toString());
        String errorMessage = (throwable != null ? throwable.getMessage() : "Unknown error");
        model.addAttribute("errorMessage", errorMessage);

        throwable.printStackTrace();
        throwable.getCause().printStackTrace();
        
        return "error";
    }

}
