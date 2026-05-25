package com.darkfactory.education.identityaccess.auth;

public class DuplicateUsernameException extends RuntimeException {

    public DuplicateUsernameException(String username) {
        super("Username already exists for the active deployment tenant: " + username);
    }
}

