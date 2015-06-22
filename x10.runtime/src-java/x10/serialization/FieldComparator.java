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

package x10.serialization;

import java.lang.reflect.Field;
import java.util.Comparator;

/**
 * Used as the comparator to sort fields of classes for serialization/deserialization
 * using reflection
 */
class FieldComparator implements Comparator<Field>{

    public int compare(Field o1, Field o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
