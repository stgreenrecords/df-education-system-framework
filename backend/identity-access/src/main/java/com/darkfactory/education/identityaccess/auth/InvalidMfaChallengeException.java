package com.darkfactory.education.identityaccess.auth;

public class InvalidMfaChallengeException extends RuntimeException {
    public InvalidMfaChallengeException(String message) {
        super(message);
    }

    public InvalidMfaChallengeException(String message, Throwable cause) {
        super(message, cause);
    }
}

