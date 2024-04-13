package org.truBlog.exceptions;

public class ProfileLockStateException extends RuntimeException {
    public ProfileLockStateException(String message) {
        super(message);
    }
}
