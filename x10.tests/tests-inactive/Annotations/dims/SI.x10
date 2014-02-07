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

public interface SI {
    static yotta: double = 1e24, Y: double = 1e24;
    static zetta: double = 1e21, Z: double = 1e21;
    static exa: double = 1e18, E: double = 1e18;
    static peta: double = 1e15, P: double = 1e15;
    static tera: double = 1e12, T: double = 1e12;
    static giga: double = 1e9,  G: double = 1e9;
    static mega: double = 1e6,  M: double = 1e6;
    static kilo: double = 1e3,  k: double = 1e3;
    static hecto: double = 1e2, h: double = 1e2;
    static deca: double = 1e1,  da: double = 1e1;
    static deci: double = 1e-1, d: double = 1e-1;
    static centi: double = 1e-2, c: double = 1e-2;
    static milli: double = 1e-3, m: double = 1e-3;
    static micro: double = 1e-6, u: double = 1e-6;
    static nano: double = 1e-9,  n: double = 1e-9;
    static pico: double = 1e-12, p: double = 1e-12;
    static femto: double = 1e-15, f: double = 1e-15;
    static atto: double = 1e-18, a: double = 1e-18;
    static zepto: double = 1e-21, z: double = 1e-21;
    static yocto: double = 1e-24, y: double = 1e-24;

    @BaseUnit static meter: double = Measure._;
    @BaseUnit static kilogram: double = Measure._;
    @BaseUnit static second: double = Measure._;
    @BaseUnit static ampere: double = Measure._;
    @BaseUnit static kelvin: double = Measure._;
    @BaseUnit static mole: double = Measure._;
    @BaseUnit static candela: double = Measure._;

    @BaseUnit static radian: double = Measure._, rad : double = Measure._;
    @BaseUnit static steradian: double = Measure._, sr: double = Measure._;
    
    @DerivedUnit(1 / second) static hertz: double = Measure._;

    @DerivedUnit(meter * kilogram / (second*second)) static newton: double = Measure._;
    @DerivedUnit(newton * meter) static joule: double = Measure._;
    @DerivedUnit(joule / second) static watt: double = Measure._;
    @DerivedUnit(newton / (meter*meter)) static pascal: double = Measure._;
    @DerivedUnit(candela * steradian) static lumen: double = Measure._;
    @DerivedUnit(lumen / (meter*meter)) static lux: double = Measure._;
    @DerivedUnit(second * ampere) static coulomb: double = Measure._;
    @DerivedUnit(watt / ampere) static volt: double = Measure._;
    @DerivedUnit(volt / ampere) static ohm: double = Measure._;
    @DerivedUnit(coulomb / volt) static farad: double = Measure._;
    @DerivedUnit(joule / ampere) static weber: double = Measure._;
    @DerivedUnit(volt * second / (meter*meter)) static tesla: double = Measure._;
    @DerivedUnit(volt * second / ampere) static henry: double = Measure._;
    @DerivedUnit(1 / ohm) static siemens: double = Measure._;
    @DerivedUnit(1 / second) static becquerel: double = Measure._;
    @DerivedUnit(joule / kilogram) static gray: double = Measure._;
    @DerivedUnit(joule / kilogram) static sievert: double = Measure._;
    @DerivedUnit(mole / second) static katal: double = Measure._;

    @DerivedUnit(kelvin - 273.16) static celsius: double = Measure._;
}
