/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types.constr;

import polyglot.ast.Variable;
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
	
}
