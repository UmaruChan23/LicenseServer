package com.server.licenseserver.exception.handler;

import com.server.licenseserver.exception.*;
import com.server.licenseserver.exception.model.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler(ActivationException.class)
    public ResponseEntity<Response> handleActivationException(ActivationException ex) {
        Response response = new Response(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TrialAlreadyExistsException.class)
    public ResponseEntity<Response> handleTrialAlreadyExistsException(TrialAlreadyExistsException ex) {
        Response response = new Response(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Response> handleUserNotFoundException(UserNotFoundException ex) {
        Response response = new Response(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Response> handleProductNotFoundException(ProductNotFoundException ex) {
        Response response = new Response(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Response> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        Response response = new Response(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
