/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package dims;

public interface Angle extends Measure { 
    @DerivedUnit(SI.radian) const radian: double = _;
    @DerivedUnit(radian * 180 / Math.PI) const degree: double = _;

    @DerivedUnit(degree / 60) const minute: double = _, min:double = _, arcminute:double = _;
    @DerivedUnit(minute / 60) const second: double = _, sec:double = _, arcsecond:double = _;
    const pi_rad: double@Unit(radian) = Math.PI;
    const two_pi_rad: double@Unit(radian) = 2 * Math.PI;
}
