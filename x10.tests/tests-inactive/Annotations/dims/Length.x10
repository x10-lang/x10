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

public interface Length extends Measure {
    // SI units
    @DerivedUnit(SI.meter)  static m: double = _;
    @DerivedUnit(1e-12 * m) static pm: double = _;
    @DerivedUnit(1e-10 * m) static angstrom: double = _;
    @DerivedUnit(1e-9  * m) static nm: double = _;
    @DerivedUnit(1e-6  * m) static um: double = _;
    @DerivedUnit(1e-3  * m) static mm: double = _;
    @DerivedUnit(1e-2  * m) static cm: double = _;
    @DerivedUnit(1e3   * m) static km: double = _;

    // astronomical units
    @DerivedUnit(3.08568025e13 * km) static parsec: double = _;
    @DerivedUnit(149597870691. * m) static AU: double = _;
    @DerivedUnit(Velocity.c * Time.year) static lightyear: double = _;
        // 9.4605284e15 * m;

    // English units
    @DerivedUnit(inch / 1000) static thou: double = _;
    @DerivedUnit(2.54 * cm) static inch: double = _;
    @DerivedUnit(12 * inch) static foot: double = _;
    @DerivedUnit(3 * foot) static yard: double = _;
    @DerivedUnit(5.5 * yard) static rod: double = _;
    @DerivedUnit(220 * yard) static furlong: double = _;
    @DerivedUnit(5280 * foot) static mile: double = _;
    @DerivedUnit(3 * mile) static league: double = _;

    @DerivedUnit(2 * yard) static fathom: double = _;
    @DerivedUnit(120 * fathom) static cable: double = _;
    @DerivedUnit(1852 * m) static nautical_mile: double = _;
    @DerivedUnit(8 * furlong) static statute_mile: double = _;

    @DerivedUnit(chain / 100) static link: double = _;
    @DerivedUnit(5.5 * yard) static pole: double = _;
    @DerivedUnit(22 * yard) static chain: double = _;

    // typographic units
    @DerivedUnit(pica / 12) static point: double = _;
    @DerivedUnit(inch / 6) static pica: double = _;
}
