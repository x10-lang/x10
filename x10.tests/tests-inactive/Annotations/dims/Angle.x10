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

package dims;

public interface Angle extends Measure { 
    @DerivedUnit(SI.radian) static radian: double = _;
    @DerivedUnit(radian * 180 / Math.PI) static degree: double = _;

    @DerivedUnit(degree / 60) static minute: double = _, min:double = _, arcminute:double = _;
    @DerivedUnit(minute / 60) static second: double = _, sec:double = _, arcsecond:double = _;
    static pi_rad: double@Unit(radian) = Math.PI;
    static two_pi_rad: double@Unit(radian) = 2 * Math.PI;
}
