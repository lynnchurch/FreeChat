package com.free.exception;

public class FCResourceNotExistException
extends FCException
{
private static final long serialVersionUID = 1L;

public FCResourceNotExistException() {}

public FCResourceNotExistException(String paramString)
{
  super(paramString);
}

public FCResourceNotExistException(String paramString, Throwable paramThrowable)
{
  super(paramString);
  super.initCause(paramThrowable);
}
}