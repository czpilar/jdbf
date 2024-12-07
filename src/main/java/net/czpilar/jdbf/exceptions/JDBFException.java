package net.czpilar.jdbf.exceptions;

import java.io.Serial;

public class JDBFException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1219258031047124569L;

    public JDBFException(String message) {
        super(message);
    }

    public JDBFException(Throwable cause) {
        super(cause);
    }
}
