package com.free.exception;

public class FCDuplicateResourceException
extends FCException
{
private static final long serialVersionUID = 1L;

public FCDuplicateResourceException() {}

public FCDuplicateResourceException(String paramString)
{
  super(paramString);
}

public FCDuplicateResourceException(String paramString, Throwable paramThrowable)
{
  super(paramString);
  super.initCause(paramThrowable);
}
}