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

import polyglot.util.Position;
import x10.types.constants.ConstantValue;

public class LocalInstance_c extends VarInstance_c<LocalDef> implements LocalInstance {
    private static final long serialVersionUID = -5115232710707624648L;

    public LocalInstance_c(TypeSystem ts, Position pos, Ref<? extends LocalDef> def) {
        super(ts, pos, def);
    }
    
    public LocalInstance flags(Flags flags) {
        return (LocalInstance) super.flags(flags);
    }

    public LocalInstance name(Name name) {
        return (LocalInstance) super.name(name);
    }

    public LocalInstance type(Type type) {
        return (LocalInstance) super.type(type);
    }

    public LocalInstance constantValue(ConstantValue o) {
        return (LocalInstance) super.constantValue(o);
    }

    public LocalInstance notConstant() {
        return (LocalInstance) super.notConstant();
    }
}
