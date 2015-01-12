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
package x10.util.logging;

import x10.compiler.Native;
import x10.compiler.NativeRep;

/**
 * This class is based on <a href="http://commons.apache.org/proper/commons-logging/javadocs/api-release/org/apache/commons/logging/LogFactory.html">org.apache.commons.logging.LogFactory</a>
 */
@NativeRep("java", "org.apache.commons.logging.LogFactory", null, "x10.rtt.NamedType.make(\"x10.util.logging.LogFactory\", org.apache.commons.logging.LogFactory.class)") 
public abstract class LogFactory {

    /** Construct (if necessary) and return a LogFactory instance, using the following ordered lookup procedure to determine the name of the implementation class to be loaded. */
    @Native("java", "org.apache.commons.logging.LogFactory.getFactory()")
    public static def getFactory():LogFactory = ConsoleLogFactory.getFactory();
    /** Release any internal references to previously created Log instances returned by this factory. */
    @Native("java", "#this.release()")
    public abstract def release():void;
    /** Release any internal references to previously created LogFactory instances that have been associated with the specified class loader (if any), after calling the instance method release() on each of them. */
    // static void release(ClassLoader classLoader)
    /** Release any internal references to previously created LogFactory instances, after calling the instance method release() on each of them. */
    @Native("java", "org.apache.commons.logging.LogFactory.releaseAll()")
    public static def releaseAll():void {
        ConsoleLogFactory.releaseAll();
    }

    /** Return an array containing the names of all currently defined configuration attributes. */
    @Native("java", "x10.interop.Java.convert(#this.getAttributeNames())")
    public abstract def getAttributeNames():Rail[String];
    /** Return the configuration attribute with the specified name (if any), or null if there is no such attribute. */
    @Native("java", "#this.getAttribute(#name)")
    public abstract def getAttribute(name:String):Any;
    /** Set the configuration attribute with the specified name. */
    @Native("java", "#this.setAttribute(#name, #value)")
    public abstract def setAttribute(name:String, value:Any):void;
    /** Remove any configuration attribute associated with the specified name. */
    @Native("java", "#this.removeAttribute(#name)")
    public abstract def removeAttribute(name:String):void;

    /** Construct (if necessary) and return a Log instance, using the factory's current set of configuration attributes. */
    @Native("java", "#this.getInstance(#name)")
    public abstract def getInstance(name:String):Log;
    /**
     * Convenience method to derive a name from the specified class and call getInstance(String) with it.
     * Note that all type parameters in T must be specified and bound.
     * E.g. LogFactory.getInstance[Cell[Int]](LogFactory.getFactory()) is OK, but LogFactory.getInstance[Cell](LogFactory.getFactory()) is not. 
     */
    @Native("java", "#factory.getInstance(#T$rtt.getJavaClass())")
    public static def getInstance[T](factory:LogFactory):Log = factory.getInstance(typeName[T]());


    /** Convenience method to return a named logger, without the application having to care about factories. */
    @Native("java", "org.apache.commons.logging.LogFactory.getLog(#name)")
    public static def getLog(name:String):Log = getFactory().getInstance(name);
    /** 
     * Convenience method to return a named logger, without the application having to care about factories.
     * Note that all type parameters in T must be specified and bound.
     * E.g. LogFactory.getLog[Cell[Int]]() is OK, but LogFactory.getLog[Cell]() is not.
     */
    @Native("java", "org.apache.commons.logging.LogFactory.getLog(#T$rtt.getJavaClass())")
    public static def getLog[T]():Log = getInstance[T](getFactory());    


    /** Returns a string that uniquely identifies the specified object, including its class. */
    @Native("java", "org.apache.commons.logging.LogFactory.objectId(#o)")
    public static def objectId(o:Any):String {
        if (o == null) {
            return "null";
        }
        return System.identityTypeName(o) + "@" + System.identityHashCode(o);
    }

    /**
     * Returns the type name of T as a string
     * @param T a type
     * @return The name of type T 
     */
    @Native("java", "#T$rtt.typeName()")
    @Native("c++", "::x10aux::makeStringLit(x10aux::getRTT< #T>()->name())")
    static native def typeName[T]():String;

}
