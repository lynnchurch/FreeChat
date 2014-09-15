package com.free.exception;

public class FCPermissionException
extends FCException
{
private static final long serialVersionUID = 1L;

public FCPermissionException() {}

public FCPermissionException(String paramString)
{
  super(paramString);
}

public FCPermissionException(String paramString, Throwable paramThrowable)
{
  super(paramString);
  super.initCause(paramThrowable);
}
}