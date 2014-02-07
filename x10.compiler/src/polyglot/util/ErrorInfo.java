/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.util;

/** Information about an error message. */
public class ErrorInfo
{
  public static final int WARNING             = 0;
  public static final int INTERNAL_ERROR      = 1;
  public static final int IO_ERROR            = 2;
  public static final int LEXICAL_ERROR       = 3;
  public static final int SYNTAX_ERROR        = 4;
  public static final int SEMANTIC_ERROR      = 5;
  public static final int POST_COMPILER_ERROR = 6;
  public static final int DEBUG               = 7;
  public static final int ERR_MARKER        = 8;  // A line was marked with @ERR
  public static final int SHOULD_NOT_BE_ERR_MARKER    = 9;  // A line was marked with @ShouldNotBeERR
  public static final int SHOULD_BE_ERR_MARKER    = 10; // A line was marked with @ShouldBeErr
  public static final int INVARIANT_VIOLATION_KIND = 11;

  public static boolean isErrorKind(int kind) {
      return kind!=WARNING && kind!=DEBUG && kind!=SHOULD_BE_ERR_MARKER && kind!=SHOULD_NOT_BE_ERR_MARKER && kind!=ERR_MARKER;
  }
  protected static String[] errorStrings = {
    "Warning",
    "Internal Error",
    "I/O Error",
    "Lexical Error",
    "Syntax Error",
    "Semantic Error",
    "Post-compiler Error",
    "Debug",
    "@ERR",
    "@ShouldNotBeERR",
    "@ShouldBeErr",
    "AST invariant violation",
  };

  protected int kind;
  protected String message;
  protected Position position;
  
  public ErrorInfo(int kind, String message, Position position)
  {
    this.kind = kind;
    this.message = message;
    this.position = position;
  }

  public int getErrorKind()
  {
    return kind;
  }

  public String getMessage()
  {
    return message;
  }

  public Position getPosition()
  {
    return position;
  }

  public String getErrorString()
  {
    return getErrorString(kind);
  }

  public static String getErrorString(int kind)
  {
    if (0 <= kind && kind < errorStrings.length) {
      return errorStrings[kind];
    }
    return "(Unknown)";
  }  
  
  public String toString() {
      return getErrorString() + ": " + message;
  }
}

