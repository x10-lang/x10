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

package polyglot.types;

import polyglot.util.InternalCompilerError;
import polyglot.util.Position;

public class ErrorRef_c<T> extends TypeObject_c implements Ref<T> {
    private static final long serialVersionUID = -9197233963214096876L;

    String errorMessage;
    
    public ErrorRef_c(TypeSystem ts, Position pos, String errorMessage) {
        super(ts, pos, pos);
        this.errorMessage = errorMessage;
    }

    public T get() {
        throw new InternalCompilerError(errorMessage, position());
    }

    public T getCached() {
        throw new InternalCompilerError(errorMessage, position());
    }

    public boolean known() {
        return false;
    }

    public void update(T v) {
        throw new InternalCompilerError("Cannot update reference: " + errorMessage, position());
    }
}
