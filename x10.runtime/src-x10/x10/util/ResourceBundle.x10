/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2013.
 */

package x10.util;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "x10.core.util.ResourceBundle", null, "x10.core.util.ResourceBundle.$RTT")
public final class ResourceBundle {

    private def this() {}

    @Native("java", "x10.core.util.ResourceBundle.getBundle(#baseName)")
    public static def getBundle(baseName:String):ResourceBundle = null;

    @Native("java", "x10.core.util.ResourceBundle.getBundle(#baseName, #loaderOrLoaded)")
    public static def getBundle(baseName:String, loaderOrLoaded:Any):ResourceBundle = null;

    @Native("java", "#this.containsKey$O(#key)")
    public final def containsKey(key:String):Boolean = false;

    @Native("java", "#this.getAny(#key)")
    public final def getAny(key:String):Any = null;

    @Native("java", "#this.getString(#key)")
    public final def getString(key:String):String = null;

    @Native("java", "#this.getStringRail(#key)")
    public final def getStringRail(key:String):Rail[String] = null;

    @Native("java", "#this.keySet()")
    public final def keySet():Set[String] = null;
}
