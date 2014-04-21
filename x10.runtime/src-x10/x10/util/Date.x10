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

package x10.util;

import x10.compiler.Native;
import x10.compiler.NativeCPPInclude;

@NativeCPPInclude("x10/lang/RuntimeNatives.h")
public class Date implements Comparable[Date] {
    private var millis:Long;

    public def this() {
        this(Timer.milliTime());
    }

    public def this(millis:Long) {
        this.millis = millis;
    }

    public def getTime() = millis;

    public def setTime(x:Long) {
        this.millis = x;
    }

    public def compareTo(x:Date):Int = this.millis.compareTo(x.millis);
    public def equals(x:Date):Boolean = (this.millis == x.millis);

    public def after(x:Date) {
        return this.compareTo(x) > 0;
    }

    public def before(x:Date) {
        return this.compareTo(x) < 0;
    }

    /**
     * Converts this Date object to a String of the form:
     * Www Mmm dd hh:mm:ss zzz yyyy
     * where
     *   Www        is the weekday; 
     *   Mmm        is the month (in letters);
     *   dd         is the day of the month;
     *   hh:mm:ss   is the time;
     *   zzz        is the time zone; and
     *   yyyy       is the year.
     */
    @Native("java", "new java.util.Date(#this.millis).toString()")
    @Native("c++", "::x10::lang::RuntimeNatives::timeToString((#this)->FMGL(millis) / 1000)")
    public native def toString():String;
}
