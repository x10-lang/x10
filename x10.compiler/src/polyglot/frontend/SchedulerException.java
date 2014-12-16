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
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.frontend;

/**
 * A <code>SchedulerException</code> is a runtime exception that may abort
 * a running pass and return the scheduler to the scheduling loop.
 *
 * @author nystrom
 */
public class SchedulerException extends RuntimeException {
    private static final long serialVersionUID = -768934941025322321L;

    public SchedulerException() {
    }
    
    /**
     * @param message An error message
     * @param cause The cause of this scheduler exception
     */
    public SchedulerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message An error message
     */
    public SchedulerException(String message) {
        super(message);
    }

    /**
     * @param cause The cause of this scheduler exception
     */
    public SchedulerException(Throwable cause) {
        super(cause);
    }
}
