/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package polyglot.ext.x10.types;

import polyglot.types.Def;
import polyglot.types.Named;
import polyglot.types.ProcedureDef;
import polyglot.types.Ref;
import polyglot.types.Type;

public interface ParameterType extends X10NamedType {
	enum Variance {
    	CONTRAVARIANT, INVARIANT, COVARIANT
    }
    /** Procedure that defines the parameter */
	public Ref<? extends Def> def();
	public ParameterType def(Ref<? extends Def> def);
}
