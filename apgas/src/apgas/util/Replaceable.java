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

package apgas.util;

import java.io.ObjectStreamException;

import com.esotericsoftware.kryo.KryoSerializable;

/**
 * The {@link Replaceable} interface.
 */
public interface Replaceable extends KryoSerializable {
  default Object readResolve() throws ObjectStreamException {
    return this;
  }

  default Object writeReplace() throws ObjectStreamException {
    return this;
  }
}
