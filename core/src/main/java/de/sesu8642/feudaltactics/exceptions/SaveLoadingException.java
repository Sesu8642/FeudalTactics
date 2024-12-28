// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.exceptions;

/**
 * Exception for problems loading saved data.
 **/
public class SaveLoadingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SaveLoadingException() {
        super();
    }

    public SaveLoadingException(String message, Throwable cause, boolean enableSuppression,
                                boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public SaveLoadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public SaveLoadingException(String message) {
        super(message);
    }

    public SaveLoadingException(Throwable cause) {
        super(cause);
    }

}
