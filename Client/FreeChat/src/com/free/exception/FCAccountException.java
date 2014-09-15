package com.free.exception;

public class FCAccountException extends FCException {
	private static final long serialVersionUID = 1L;

	public FCAccountException() {
	}

	public FCAccountException(String paramString) {
		super(paramString);
	}

	public FCAccountException(String paramString, Throwable paramThrowable) {
		super(paramString);
		super.initCause(paramThrowable);
	}
}
