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

import harness.x10Test;


/**
 * @author mtake 7/2012
 */
public class Any1b extends x10Test {

    def aFun() = ()=>{};

    public def run():Boolean {
        val f = aFun();
        val ft = f.typeName();
        val fs = f.toString();
        val fh = f.hashCode();

        val ff:()=>void = f;
        val fft = ff.typeName();
        val ffs = ff.toString();
        val ffh = ff.hashCode();

        val fa:Any = f;
        val fat = fa.typeName();
        val fas = fa.toString();
        val fah = fa.hashCode();

        chk(ft.equals(fft));
        chk(ft.equals(fat));

        chk(fs.equals(ffs));
        chk(fs.equals(fas));

        chk(fh == ffh);
        chk(fh == fah);

        chk(f.equals(ff));
        chk(ff.equals(f));

        chk(f.equals(fa));
        chk(fa.equals(f));

        chk(ff.equals(fa));
        chk(fa.equals(ff));

        return true;
    }

    public static def main(Rail[String]) {
        new Any1b().execute();
    }

}
