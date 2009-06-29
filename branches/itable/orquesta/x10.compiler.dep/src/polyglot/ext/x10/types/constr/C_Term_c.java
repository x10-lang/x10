/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types.constr;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import polyglot.ast.Variable;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.main.Report;
import polyglot.types.Type;

public abstract class C_Term_c implements C_Term {
	public final Type type;
	public C_Term_c(Type t) {
		super();
		this.type = t;
	}
	public Type type() { return type;}
	
	
	public boolean prefersBeingBound() {
		return isEQV() || this instanceof C_Special;
	}
	
	public void collectVars(List<C_Var> accum) {
	}

	public Type substituteType(C_Var y, C_Root x, boolean propagate, HashSet<C_Term> visited) throws Failure {
		Type type = type();

		if (visited.contains(this))
			return type;
		visited.add(this);
		
	    if (propagate && type instanceof X10Type) {
	        X10Type xType = (X10Type) type;

	        if (x instanceof C_Special && ((C_Special) x).kind() == C_Special.SELF) {
	            return type;
	        }
	        
	        Constraint c = X10TypeMixin.depClause(xType);
	        if (c != null)
	            type = X10TypeMixin.depClauseDeref(xType, c.substitute(y, x, true, visited)); // ### should we keep propagating? could cause inf recursion
	    }
	    return type;
	}

}
