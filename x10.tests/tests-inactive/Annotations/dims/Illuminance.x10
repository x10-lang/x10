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

package dims;

public interface Illuminance extends Measure { 
    @DerivedUnit(SI.lux) static lux: double = _, lx: double = _;

    @DerivedUnit(10.764 * lux) static footcandle: double = _, fc: double = _;
}
