/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 */

package apgas;

import java.util.Collection;

/**
 * A {@link MixedException} is a {@link MultipleException} caused by at least
 * one dead place exception but possibly other exceptions.
 */
public class MixedException extends MultipleException {
  private static final long serialVersionUID = -4389710198011450772L;

  /**
   * Constructs a new {@link MixedException} from the specified
   * {@code exceptions}.
   *
   * @param exceptions
   *          the uncaught exceptions that contributed to this
   *          {@code MultipleException}
   */
  protected MixedException(Collection<Throwable> exceptions) {
    super(exceptions);
  }
}
