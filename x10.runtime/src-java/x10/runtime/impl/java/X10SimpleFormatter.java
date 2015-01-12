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

package x10.runtime.impl.java;

import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class X10SimpleFormatter extends SimpleFormatter {
    
    @Override
    public synchronized String format(LogRecord record) {
        String message = super.format(record);
        long timestamp = java.lang.System.nanoTime() / 1000000L;
        try {
	        x10.xrx.Worker worker = (x10.xrx.Worker) x10.core.Thread.currentThread();
	        long placeId = x10.x10rt.X10RT.hereId();
	        int workerId = worker.workerId;
	        message = String.format("[P%d,W%d,T%d] %s", placeId, workerId, timestamp, message);
        } catch (Exception e) {
            // During startup/shutdown it is possible for currentThread or X10RT.here() to fail.
            // If that happens we want to swallow the exception and simply return
            // the formatted message with just a timestamp.
            message = String.format("[P? W? T%d] %s", timestamp, message); 
        }
        return message;
    }
}
