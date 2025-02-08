package com.yuramoroz.spring_crm_system.exceptionHandling.exceptions;

public class ChangingConstraintViolationException extends RuntimeException {
    public ChangingConstraintViolationException(String message) {
        super(message);
    }
}
