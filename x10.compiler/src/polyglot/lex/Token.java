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

import polyglot.util.Position;
 
/** The base class of all tokens. */
public abstract class Token {
    protected Position position;
    protected int symbol;

  public Token(Position position, int symbol)
  {
    this.position = position;
    this.symbol = symbol;
  }

  public Position getPosition()
  {
    return position;
  }

  public int symbol() {
      return symbol;
  }

  protected static String escape(String s) {
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<s.length(); i++)
      switch(s.charAt(i)) {
      case '\t': sb.append("\\t"); break;
      case '\f': sb.append("\\f"); break;
      case '\n': sb.append("\\n"); break;
      default:
	if ((int)s.charAt(i)<0x20 ||
              ((int)s.charAt(i) > 0x7e && (int)s.charAt(i) < 0xFF))
	  sb.append("\\"+Integer.toOctalString((int)s.charAt(i)));
	else
	  sb.append(s.charAt(i));
      }
    return sb.toString();
  }
}
