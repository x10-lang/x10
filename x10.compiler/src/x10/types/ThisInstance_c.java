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

import java.util.List;

import polyglot.types.ErrorRef_c;
import polyglot.types.Flags;
import polyglot.types.LocalInstance;
import polyglot.types.Name;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.VarInstance_c;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import x10.constraint.XFailure;
import x10.constraint.XLocal;
import x10.types.checker.PlaceChecker;
import x10.types.constants.ConstantValue;
import x10.types.constraints.CConstraint;

public class ThisInstance_c extends VarInstance_c<ThisDef> implements ThisInstance {
    private static final long serialVersionUID = -2728180556244846992L;

    public ThisInstance_c(TypeSystem ts, Position pos, Ref<? extends ThisDef> def) {
        super(ts, pos, def);
    }
    
    public ThisDef x10Def() {
        return def();
    }
    
    public List<Type> annotations() {
        return X10TypeObjectMixin.annotations(this);
    }
    public List<Type> annotationsMatching(Type t) {
        return X10TypeObjectMixin.annotationsMatching(this, t);
    }
    
    public ThisInstance name(Name name) {
        if (name == this.name()) return this;
        if (true) throw new InternalCompilerError("Setting name for 'this'");
        return (ThisInstance) super.name(name);
    }
    
    public ThisInstance type(Type t) {
        if (t == this.type) return this;
        ThisInstance_c n = (ThisInstance_c) super.type(t);
        return n;
    }
    
    private SemanticException error;

    public SemanticException error() {
        return error;
    }

    public ThisInstance error(SemanticException e) {
        ThisInstance_c n = (ThisInstance_c) copy();
        n.error = e;
        return n;
    }

    public String toString() {
        return "this: " + safeType();
    }

    public boolean isValid() {
        return true;
    }

    public ThisInstance flags(Flags flags) {
        return (ThisInstance) super.flags(flags);
    }

    public ThisInstance constantValue(ConstantValue o) {
        return (ThisInstance) super.constantValue(o);
    }

    public ThisInstance notConstant() {
        return (ThisInstance) super.notConstant();
    }
}
