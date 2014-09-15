package com.free.exception;

public class FCException extends Exception {
	private static final long serialVersionUID = 1L;

	public FCException() {
	}

	public FCException(String paramString) {
		super(paramString);
	}

	public FCException(String paramString, Throwable paramThrowable) {
		super(paramString);
		super.initCause(paramThrowable);
	}
}