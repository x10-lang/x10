/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

/**
 * @author tardieu
 */
public class Runtime {

   /**
    * Sleep for the specified number of milliseconds.
    * [IP] NOTE: Unlike Java, x10 sleep() simply exits when interrupted.
    * @param millis the number of milliseconds to sleep
    * @return true if completed normally, false if interrupted
    */
    public static def sleep(millis: long): boolean {
        return x10.runtime.Runtime.sleep(millis);
    }

    public static def setExitCode(code: int): void {
        x10.runtime.NativeRuntime.setExitCode(code);
    }
}    
