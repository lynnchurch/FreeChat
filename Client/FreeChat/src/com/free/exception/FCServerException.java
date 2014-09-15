package com.free.exception;

public class FCServerException
extends FCException
{
private static final long serialVersionUID = 1L;

public FCServerException() {}

public FCServerException(String paramString)
{
  super(paramString);
}

public FCServerException(String paramString, Throwable paramThrowable)
{
  super(paramString);
  super.initCause(paramThrowable);
}
}

