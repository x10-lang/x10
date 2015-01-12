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

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeCPPInclude;

import x10.io.Console;
import x10.util.Map;
import x10.util.Timer;

@NativeCPPInclude("x10/lang/RuntimeNatives.h")
public class System {

    private def this() {}

    /**
     * Provides the current time in milliseconds.
     *
     * @return The current time in milliseconds.
     */
    public static def currentTimeMillis():Long = Timer.milliTime();

    /**
     * Provides the current time in nanoseconds, as precise as the system timers provide.
     *
     * @return The current time in nanoseconds.
     */
    public static def nanoTime():Long = Timer.nanoTime();

    /**
     * Kills the current place, as if due to a hardware or low level software failure.  
     * Behaviour is only well-defined if executed at a place other than Place.FIRST_PLACE 
     * and the program is executing in one of the resilient modes
     *
     * @see Configuration#resilient_mode
     */
    @Native("java", "java.lang.System.exit(1)")
    @Native("c++", "::x10::lang::RuntimeNatives::exit(1)")
    public static native def killHere(): void;


    /**
     * Asynchronously kills the victim place, as if due 
     * to a hardware or low level software failure.  
     * The victim should not be Place.FIRST_PLACE.
     * The program ahould be executing in one of the resilient modes 
     * for correct operation.
     *
     * @see Configuration#resilient_mode
     */
    public static def killThere(victim:Place) {
        at (victim) @x10.compiler.Immediate("killThere") async killHere();
    }
    
    /**
     * Requests the launcher to create N additional places asynchronously.
     * Please note that since this is an asynchronous operation, a return 
     * code greater than zero does not guarantee those places have actually
     * started, as they may fail for reasons outside of the launcher's control
     * 
     * @param newPlaces the number of new places to add
     * @return The number of new places that this request attempted to spawn.
     * May be less than the number requested, if resources are not available, 
     * or if the current launcher does not support adding places after startup.
     */
    
    @Native("java", "x10.x10rt.X10RT.addPlaces(#newPlaces)")
    public static def addPlaces(newPlaces:Long): Long {
        return 0;
    }

    /**
     * Requests the launcher to create N additional places synchronously, 
     * waiting up to 'timeout' milliseconds for the places to join before 
     * returning.
     * 
     * @param newPlaces the number of new places to add
     * @param timeout how many milliseconds to wait for the places to join
     * @return The number of new places that joined successfully.  This may be 
     * fewer than the requested number of places if we timed out, or more than 
     * the requested number of places if there were multiple overlapping calls
     * to this method
     */
    public static def addPlacesAndWait(newPlaces:Long, timeout:Long): Long {
        val initialPlaceCount = Place.numPlaces();
        val launcherAdded = addPlaces(newPlaces);
        if (launcherAdded == 0) return 0; // the launcher can't add places.  Don't bother waiting.

        // clumsy wait for newPlaces to join
        val timePlacesRequested = currentTimeMillis();
        while (Place.numPlaces() < initialPlaceCount + launcherAdded) {
            if (currentTimeMillis() > timePlacesRequested+timeout) {
                // timeout
                return (Place.numPlaces() - initialPlaceCount);
            }
            System.sleep(100);
        }
        return launcherAdded;
    }

    /**
     * Sets the exit code with which the X10 program will exit.
     * However, calling this method has no effect on the timing
     * of when the computation will exit.
     * 
     * This method is primarily intended for usage by testing 
     * frameworks that use non-zero exit codes to encode testcase 
     * failures.
     *
     * Implementation note: This method currently must internally
     *   shift execution to Place.FIRST_PLACE and set the exitCode
     *   there because exitCodes are intentionally not implicitly 
     *   propagated back to Place.FIRST_PLACE when other Places 
     *   exit.  Therefore the caller should be aware that calling this
     *   method from within an <code>atomic</code> or <code>when</code>
     *   will result in an exception being raised.
     */
    public static def setExitCode(exitCode:Int):void {
        if (here != Place.FIRST_PLACE) {
            at (Place.FIRST_PLACE) setExitCode(exitCode);
        } else {
            @Native("java", "x10.runtime.impl.java.Runtime.setExitCode(exitCode);")
            @Native("c++", "::x10aux::exitCode = (exitCode);")
            { 
            }
       }
    }

    /**
     * Provides an estimate in bytes of the size of the X10 heap
     * allocated to the current place. The accuracy of this estimate
     * is highly dependent on the implementation details of the
     * underlying memory management scheme being used by the X10 runtime,
     * and in some cases may simply return Long.MAX_VALUE or some other similarly
     * over conservative approximation.
     *
     * @return An upper bound in bytes on the size of the X10 heap allocated to the current place.
     */
    @Native("java", "java.lang.Runtime.getRuntime().totalMemory()")
    @Native("c++", "::x10aux::heap_size()")
    public static native def heapSize():Long;

    /**
     * Trigger a garbage collection.
     */
    @Native("java", "java.lang.System.gc()")
    @Native("c++", "::x10aux::trigger_gc()")
    public static native def gc():void;

    /**
     * Returns an immutable map from environment variables to values.
     */
    public static def getenv():Map[String,String] = Runtime.env;

    /**
     * Returns the value of the specified environment variable, or null if the variable is not defined.
     */
    public static def getenv(name:String):String = Runtime.env.getOrElse(name, null);

    /**
     * Sets the system property with the given name to the given value.
     *
     * @param p the name of the system property.
     * @param v the value for the system property.
     * TODO: @ return The previous value of the property, or null if it did not have one.
     */
    // TODO: XTENLANG-180.  Provide full System properties API in straight X10
    @Native("java", "java.lang.System.setProperty(#p,#v)")
    @Native("c++", "printf(\"not setting %s\\n\", (#p)->c_str())") // FIXME: Trivial definition to allow XRX compilation to go through.
    public static native def setProperty(p:String,v:String):void;

    /** Get the type name of T as a string
     * @param T a type
     * @return The name of type T 
     */
    @Native("java", "#T$rtt.typeName()")
    @Native("c++", "::x10aux::makeStringLit(x10aux::getRTT< #T>()->name())")
    static native def typeName[T]():String;

    @Native("java", "x10.rtt.Types.typeName(#o)")
    @Native("c++", "::x10aux::type_name(#o)")
    public static native def identityTypeName(o:Any) : String;

    @Native("java", "java.lang.System.identityHashCode(#o)")
    @Native("c++", "::x10aux::identity_hash_code(reinterpret_cast<x10::lang::Reference*>(#o))")
    public static native def identityHashCode(o:Any) : Int;

    public static def identityToString(o:Any) : String = o.typeName() + "@" + System.identityHashCode(o).toHexString();

    public static def identityEquals(o1:Any, o2:Any) : Boolean = o1==o2;

    /**
     * Sleep for the specified number of milliseconds.
     * [IP] NOTE: Unlike Java, x10 sleep() simply exits when interrupted.
     * @param millis the number of milliseconds to sleep
     * @return true if completed normally, false if interrupted
     */
    public static def sleep(millis:Long):Boolean {
        try {
            Runtime.increaseParallelism();
            Thread.sleep(millis);
            Runtime.decreaseParallelism(1n);
            return true;
        } catch (e:InterruptedException) {
            Runtime.decreaseParallelism(1n);
            return false;
        }
    }

    /**
     * Sleep for the specified number of milliseconds.
     * @param millis the number of milliseconds to sleep
     * @return true if completed normally, false if interrupted
     */
    public static def threadSleep(millis:Long):Boolean {
        try {
            Thread.sleep(millis);
            return true;
        } catch (e:InterruptedException) {
            return false;
        }
    }
}
