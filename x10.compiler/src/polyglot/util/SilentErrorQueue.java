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

package polyglot.util;

import java.util.ArrayList;
import java.util.List;

/**
 * A <code>SilentErrorQueue</code> records but does not output error messages.
 */
public class SilentErrorQueue extends AbstractErrorQueue
{
    private List<ErrorInfo> errors;

    public SilentErrorQueue(int limit, String name) {
        super(limit, name);
        this.errors = new ArrayList<ErrorInfo>(limit);
    }

    public void displayError(ErrorInfo e) {
        errors.add(e);
    }
    
    public List<ErrorInfo> getErrors() {
        return errors;
    }
}
