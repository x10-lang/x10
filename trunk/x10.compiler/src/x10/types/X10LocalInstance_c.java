/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.types;

import java.util.List;

import polyglot.types.Flags;
import polyglot.types.LocalInstance_c;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import x10.constraint.XFailure;
import x10.constraint.XLocal;
import x10.constraint.XTerm;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CConstraint_c;

/**
 * @author vj
 *
 */
public class X10LocalInstance_c extends LocalInstance_c implements X10LocalInstance {

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
    
    public X10LocalInstance type(Type t) {
        X10LocalInstance_c n = (X10LocalInstance_c) super.type(t);
        assert n != this;
        // clear right type so it is recomputed from type
        n.rightType = null;
        return n;
    }
    
    Type rightType;

    public Type rightType() {
        if (rightType != null) 
        	return rightType;

        rightType = type();
        assert rightType != null : "The type() for " + this + " at " + position() + " is null.";
        Flags flags = flags();
        if ((! flags.isFinal())|| rightType instanceof UnknownType) {
        	return rightType;
        }
        // If the local variable is final, replace T by T{self==t}, 
        // do this even if depclause==null
        try {
        	CConstraint c = X10TypeMixin.xclause(rightType);
        	if (c == null)
        		c = new CConstraint_c();
        	else
        		c = c.copy();
        	X10TypeSystem xts = (X10TypeSystem) ts;
        	XLocal var = xts.xtypeTranslator().trans(this, rightType);
        	c.addSelfBinding(var);
        	rightType = X10TypeMixin.xclause(X10TypeMixin.baseType(rightType), c);
        	assert rightType != null;
        	return rightType;
        }
        catch (SemanticException f) {
        	throw new InternalCompilerError("Could not add self binding.", f);
        }
        catch (XFailure f) {
        	throw new InternalCompilerError("Could not add self binding.", f);
        }
    }



    public String toString() {
	String s = "local " + X10Flags.toX10Flags(flags()).prettyPrint() + name() + ": " + type();
	return s;
    }

}
