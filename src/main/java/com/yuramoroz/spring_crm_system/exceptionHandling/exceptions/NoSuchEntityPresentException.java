package com.yuramoroz.spring_crm_system.exceptionHandling.exceptions;

public class NoSuchEntityPresentException extends RuntimeException{
    public NoSuchEntityPresentException(String message) {
        super(message);
    }
}
