/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.util;

/**
 * This exception is thrown when a PlaceHolder cannot be resolved
 * during deserialization.  The thrower should set up a goal
 * to resolve the place holder in another pass.
 * When caught, deserialization should fail.
 */
public class CannotResolvePlaceHolderException extends Exception {
    private static final long serialVersionUID = -7735565873449475448L;

    public CannotResolvePlaceHolderException(Throwable cause) {
        super(cause);
    }

    public CannotResolvePlaceHolderException(String m) {
        super(m);
    }

    public CannotResolvePlaceHolderException(String m, Throwable cause) {
        super(m, cause);
    }
}
