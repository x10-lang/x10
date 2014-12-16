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

package polyglot.lex;

import java.io.IOException;

/**
 * The interface "Lexer" describes lexers produced by JFlex for
 * Polyglot.
 */
public interface Lexer {

  /** This character denotes the end of file */
  final public static int YYEOF = -1;

  /**
   * The file being scanned, for use in constructing diagnostic
   * messages.
   */
  public String file();

  /**
   * The path to the file being scanned, for use in constructing diagnostic
   * messages.
   */
  public String path();

  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   IOException  if any I/O-Error occurs
   */
  public Token nextToken() throws IOException;
}
