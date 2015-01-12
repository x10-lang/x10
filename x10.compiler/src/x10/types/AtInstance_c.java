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

package x10.types;

import polyglot.types.ClassType;
import polyglot.types.CodeInstance;
import polyglot.types.Flags;
import polyglot.types.Ref;
import polyglot.types.ContainerType;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;

public class AtInstance_c extends MethodInstance_c implements AtInstance {
    private static final long serialVersionUID = 107893706880034330L;

    public AtInstance_c(TypeSystem ts, Position pos, Ref<? extends AtDef> def) {
        super(ts, pos, pos, def);
    }

    public AtDef def() {
        return (AtDef) super.def();
    }

    public AtDef x10Def() {
        return def();
    }

    public AtInstance flags(Flags f) {
        return (AtInstance) super.flags(f);
    }
    public ClassType container() {
        return (ClassType) super.container();
    }
    public AtInstance container(ContainerType t) {
        return (AtInstance) super.container(t);
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

