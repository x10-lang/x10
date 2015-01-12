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
package x10.util.synthesizer;

/**
 * If the synthesizer is closed, it cannot be modified any more.
 */
public class StateSynthClosedException extends Exception {
    private static final long serialVersionUID = -2278601964705157332L;

    public StateSynthClosedException(String message){
        super(message);
    }
}
