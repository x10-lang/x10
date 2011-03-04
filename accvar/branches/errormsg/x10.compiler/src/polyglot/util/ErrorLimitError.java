/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.util;

/** Exception thrown when the number of errors exceeds a limit. */
public class ErrorLimitError extends RuntimeException
{
  private static final long serialVersionUID = 3299943344400162231L;

  public ErrorLimitError( String msg)
  {
    super( msg);
  }

  public ErrorLimitError()
  {
    super();
  }
}
