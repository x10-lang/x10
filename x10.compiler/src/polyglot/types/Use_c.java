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

package polyglot.types;

import polyglot.util.Position;

public class Use_c<T extends Def> extends TypeObject_c implements Use<T> {
    private static final long serialVersionUID = -6571291950402711547L;

    protected Ref<? extends T> def;

    public Use_c(TypeSystem ts, Position pos, Position errorPos, Ref<? extends T> def) {
        super(ts, pos, errorPos);
        this.def = def;
    }

    public T def() {
        return def.get();
    }
    
    public String toString() {
        return def.toString();
    }
}
