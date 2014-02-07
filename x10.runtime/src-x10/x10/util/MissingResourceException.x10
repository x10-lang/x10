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
import x10.compiler.NativeRep;


/**
 * Signals that a resource is missing.
 * @see ResourceBundle
 */
@NativeRep("java", "java.util.MissingResourceException", null, "x10.rtt.Types.MISSING_RESOURCE_EXCEPTION")
public class MissingResourceException extends Exception {

    private val className:String;
    private val key:String;

    /**
     * Constructs a <code>MissingResourceException</code> with <code>message</code>,
     * <code>className</code>, and <code>key</code>.
     * @param message the detail message
     * @param className the name of the resource class
     * @param key the key for the missing resource
     */
    @Native("java", "new java.util.MissingResourceException(#message, #className, #key)")
    public def this(message: String, className:String, key:String)
    { super(message); this.className = className; this.key = key; }

    /**
     * Constructs a <code>MissingResourceException</code> with <code>message</code>,
     * <code>className</code>, <code>key</code>, and <code>cause</code>.
     * @param message the detail message
     * @param className the name of the resource class
     * @param key the key for the missing resource
     * @param cause the cause
     */
    @Native("java", "new Object() { java.util.MissingResourceException apply() { try { java.lang.reflect.Constructor<java.util.MissingResourceException> ctor = java.util.MissingResourceException.class.getDeclaredConstructor(String.class, String.class, String.class, Throwable.class); ctor.setAccessible(true); return ctor.newInstance(#message, #className, #key, #cause); } catch (Throwable e) { e.printStackTrace(); return null; } } }.apply()")
    public def this(message: String, className:String, key:String, cause:CheckedThrowable)
    { super(message, cause); this.className = className; this.key = key; }

    /**
     * Returns the name of the resource class
     * @return the name of the resource class
     */
    @Native("java", "#this.getClassName()")
    public def getClassName():String = this.className;

    /**
     * Returns the key for the missing resource
     * @return the key for the missing resource
     */
    @Native("java", "#this.getKey()")
    public def getKey():String = this.key;

}
