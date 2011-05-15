package net.czpilar.jdbf.exceptions;

public class JDBFException extends RuntimeException {

	private static final long serialVersionUID = 1219258031047124569L;

	public JDBFException() {

	}

	public JDBFException(String message) {

		super(message);
	}

	public JDBFException(Throwable cause) {

		super(cause);
	}

	public JDBFException(String message, Throwable cause) {

		super(message, cause);
	}

}
