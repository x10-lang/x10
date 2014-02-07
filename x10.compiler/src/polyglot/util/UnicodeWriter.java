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

import java.io.*;

/**
 * Output stream for writing unicode.  Non-ASCII Unicode characters
 * are escaped.
 */
public class UnicodeWriter extends FilterWriter
{
  public UnicodeWriter(Writer out)
  {
    super(out);
  }

  public void write(int c) throws IOException
  {
    if( c <= 0xFF) {
      super.write(c);
    }
    else {
      String s = String.valueOf(Integer.toHexString(c));
      super.write('\\');
      super.write('u');
      for(int i = s.length(); i < 4; i++) {
        super.write('0');
      }
      write(s);
    }
  }
  
  public void write(char[] cbuf, int off, int len) throws IOException
  {
    for( int i = 0; i < len; i++)
    {
      write((int)cbuf[i+off]);
    }
  }

  public void write(String str, int off, int len) throws IOException
  {
    write(str.toCharArray(), off, len);
  }
}
