/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package apgas;

import java.io.Serializable;
import java.util.concurrent.Callable;

/**
 * A generic serializable functional interface with no arguments and a return
 * value.
 * <p>
 * The functional method is {@link #call()}.
 *
 * @param <T>
 *          the type of the result
 */
@FunctionalInterface
public interface Fun<T> extends Callable<T>, Serializable {
  @Override
  public T call() throws Exception;
}
