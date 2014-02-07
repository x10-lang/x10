/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.types;

import polyglot.types.ClassType;
import polyglot.types.CodeInstance;
import polyglot.types.Flags;
import polyglot.types.Ref;
import polyglot.types.ContainerType;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;

public class AsyncInstance_c extends MethodInstance_c implements AsyncInstance {
    private static final long serialVersionUID = 8978058925652253461L;

    public AsyncInstance_c(TypeSystem ts, Position pos, Ref<? extends AsyncDef> def) {
        super(ts, pos, pos, def);
    }

    public AsyncDef def() {
        return (AsyncDef) super.def();
    }

    public AsyncDef x10Def() {
        return def();
    }

    public AsyncInstance flags(Flags f) {
        return (AsyncInstance) super.flags(f);
    }
    public ClassType container() {
        return (ClassType) super.container();
    }
    public AsyncInstance container(ContainerType t) {
        return (AsyncInstance) super.container(t);
    }

    public CodeInstance<?> methodContainer() {
        return Types.get(def().methodContainer());
    }

    public ClassType typeContainer() {
        return Types.get(def().typeContainer());
    }

    public String designator() {
        return def().designator();
    }

    public String toString() {
        return designator() + " in " + position();
    }
}

