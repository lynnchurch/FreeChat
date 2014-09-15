package com.free.exception;

public class FCNetworkException
extends FCException
{
private static final long serialVersionUID = 1L;

public FCNetworkException() {}

public FCNetworkException(String paramString)
{
  super(paramString);
}

public FCNetworkException(String paramString, Throwable paramThrowable)
{
  super(paramString);
  super.initCause(paramThrowable);
}
}