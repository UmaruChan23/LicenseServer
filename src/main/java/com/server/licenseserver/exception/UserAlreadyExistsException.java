package com.server.licenseserver.exception;

public class UserAlreadyExistsException extends Exception{
    public UserAlreadyExistsException(String msg) {
        super(msg);
    }
}
