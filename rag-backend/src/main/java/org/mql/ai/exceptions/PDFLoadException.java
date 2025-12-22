package org.mql.ai.exceptions;


public class PDFLoadException extends RuntimeException {
    public PDFLoadException(String message) {
        super(message);
    }

    public PDFLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}

