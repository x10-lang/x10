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

import java.util.List;

import polyglot.types.ClassType;
import polyglot.types.ErrorRef_c;
import polyglot.types.Flags;
import polyglot.types.LocalInstance_c;
import polyglot.types.Name;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import x10.constraint.XFailure;
import x10.constraint.XLocal;
import x10.constraint.XTerm;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CConstraint;
import x10.types.constraints.ConstraintManager;

/**
 * @author vj
 *
 */
public class X10LocalInstance_c extends LocalInstance_c implements X10LocalInstance {
    private static final long serialVersionUID = -2728180556244846992L;

    public X10LocalInstance_c(TypeSystem ts, Position pos, Ref<? extends X10LocalDef> def) {
        super(ts, pos, def);
    }
    
    public X10LocalDef x10Def() {
        return (X10LocalDef) def();
    }
    
    public List<Type> annotations() {
        return X10TypeObjectMixin.annotations(this);
    }
    public List<Type> annotationsMatching(Type t) {
        return X10TypeObjectMixin.annotationsMatching(this, t);
    }
    
    public X10LocalInstance name(Name name) {
        if (name == this.name()) return this;
        return (X10LocalInstance) super.name(name);
    }
    
    public X10LocalInstance type(Type t) {
        if (t == this.type) return this;
        X10LocalInstance_c n = (X10LocalInstance_c) super.type(t);
        if (n == this) n = (X10LocalInstance_c) this.copy();
        // clear right type so it is recomputed from type
        n.rightType = null;
        return n;
    }
    
    private Type rightType;

    public Type rightType() {
        if (rightType != null) 
        	return rightType;

        rightType = type();
        assert rightType != null : "The type() for " + this + " at " + position() + " is null.";
        	rightType = PlaceChecker.ReplaceHereByPlaceTerm(rightType, x10Def().placeTerm());
        Flags flags = flags();
        TypeSystem xts = (TypeSystem) ts;
        if ((! flags.isFinal())|| xts.isUnknown(rightType)) {
        	return rightType;
        }
        // If the local variable is final, replace T by T{self==t}, 
        // do this even if depclause==null.
        CConstraint c = Types.xclause(rightType);
        c = c==null? ConstraintManager.getConstraintSystem().makeCConstraint() : c.copy();

        XLocal var = xts.xtypeTranslator().translate(this.type(rightType));
        c.addSelfBinding(var);
        rightType = Types.xclause(Types.baseType(rightType), c);

        assert rightType != null;
        return rightType;
    }

    private SemanticException error;

    public SemanticException error() {
        return error;
    }

    public X10LocalInstance error(SemanticException e) {
        X10LocalInstance_c n = (X10LocalInstance_c) copy();
        n.error = e;
        return n;
    }

    public String toString() {
        Name name = x10Def().isUnnamed() ? X10LocalDef_c.UNNAMED : name();
        String s = "local " + flags().prettyPrint() + name + ": " + safeType();
        return s;
    }

    public boolean isValid() {
        return !(def instanceof ErrorRef_c<?>);
    }
}
