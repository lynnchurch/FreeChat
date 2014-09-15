package com.free.exception;

public class FCAuthenticationException
extends FCException
{
private static final long serialVersionUID = 1L;

public FCAuthenticationException() {}

public FCAuthenticationException(String paramString)
{
  super(paramString);
}

public FCAuthenticationException(String paramString, Throwable paramThrowable)
{
  super(paramString);
  super.initCause(paramThrowable);
}
}