package com.darkfactory.education.identityaccess.auth;

public class DuplicateRoleAssignmentException extends RuntimeException {
    public DuplicateRoleAssignmentException(String message) {
        super(message);
    }
}

