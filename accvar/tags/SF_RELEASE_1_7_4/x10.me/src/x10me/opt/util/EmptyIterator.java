/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file was derived from code developed by the
 *  Jikes RVM project (http://jikesrvm.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  See the COPYRIGHT.txt file distributed with this work for information
 *  regarding copyright ownership.
 */

package x10me.opt.util;

import java.util.Iterator;

public class EmptyIterator implements Iterator<Object> {

  public boolean hasNext() {
    return false;
  }

  public Object next() {
    throw new java.util.NoSuchElementException();
  }

  public void remove() {
    throw new Error();
  }

  public static final EmptyIterator INSTANCE = new EmptyIterator();
}



