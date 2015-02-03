/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package apgas.impl;

/**
 * The abstract {@link FinishFactory} class is the template of all finish
 * factories.
 */
abstract class FinishFactory {
  /**
   * Makes a new {@link Finish} instance.
   *
   * @param parent
   *          the parent finish object or null if root finish
   * @param p
   *          the place of the root task of the finish
   * @return the {@link Finish} instance
   */
  abstract Finish make(Finish parent, int p);
}
