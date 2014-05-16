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

/** An instant in time, to millisecond precision. */
@NativeCPPInclude("x10/lang/RuntimeNatives.h")
public class Date implements Comparable[Date] {
    private var millis:Long;

    /**
     * Construct a new Date representing the current instant.
     */
    public def this() {
        this(Timer.milliTime());
    }

    /**
     * Construct a new Date representing the point in time that is the given
     * number of milliseconds after January 1, 1970, 00:00:00 GMT.
     */
    public def this(millis:Long) {
        this.millis = millis;
    }

    /** 
     * Return the number of milliseconds since January 1, 1970, 00:00:00 GMT
     * represented by this Date.
     */
    public def getTime() = millis;

    /** 
     * Set this Date to a point in time that is the given number of milliseconds
     * after January 1, 1970, 00:00:00 GMT.
     */
    public def setTime(millis:Long) {
        this.millis = millis;
    }

    /**
     * Returns a negative Int, zero, or a positive Int if this Date is less than, equal
     * to, or greater than the given Date.
     */
    public def compareTo(x:Date):Int = this.millis.compareTo(x.millis);

    /** Return true if this date is equal to the given Date. */
    public def equals(x:Date):Boolean = (this.millis == x.millis);

    /** Return true if this date is before the given Date. */
    public def before(x:Date) {
        return this.compareTo(x) < 0;
    }

    /** Return true if this date is after the given Date. */
    public def after(x:Date) {
        return this.compareTo(x) > 0;
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
