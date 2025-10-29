// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.exceptions;

/**
 * Exception for any critical error that should cause a crash after a crash
 * report is generated.
 **/
public class FatalErrorException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public FatalErrorException() {
    }

    public FatalErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public FatalErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public FatalErrorException(String message) {
        super(message);
    }

    public FatalErrorException(Throwable cause) {
        super(cause);
    }

}
