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

package x10.util;

/**
 * @author Dave Cunningham
*/
// This class exists as a work-around for XTENLANG-624
public struct Option {
    short_:String; // underscore is workaround for XTENLANG-623
    long_:String;
    description:String;
    public def this(s:String, l:String, d:String) {
        short_ = "-"+s; long_="--"+l; description=d;
    }
    public global safe def toString() = description;
}
