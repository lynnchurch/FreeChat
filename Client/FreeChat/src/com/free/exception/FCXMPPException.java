package com.free.exception;

public class FCXMPPException
extends FCException
{
private static final long serialVersionUID = 1L;

public FCXMPPException() {}

public FCXMPPException(String paramString)
{
  super(paramString);
}

public FCXMPPException(String paramString, Throwable paramThrowable)
{
  super(paramString);
  super.initCause(paramThrowable);
}
}

