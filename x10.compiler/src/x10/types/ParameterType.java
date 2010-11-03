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

import polyglot.types.Def;
import polyglot.types.Named;
import polyglot.types.ProcedureDef;
import polyglot.types.Ref;
import polyglot.types.Type;

public interface ParameterType extends X10NamedType {
	enum Variance {
    	CONTRAVARIANT(-1), INVARIANT(0), COVARIANT(1);
        private final int sign;
        private Variance(int sign) {
            this.sign = sign;
        }
        Variance mult(Variance v) {
            // like multiplying two numbers:
            // INVARIANT is 0
            // CONTRAVARIANT is -1
            // COVARIANT is 1
            int res = sign*v.sign;
            return res==0 ? INVARIANT : res<0 ? CONTRAVARIANT : COVARIANT;
        }
    }
    /** Procedure that defines the parameter */
    public Ref<? extends Def> def();
    public ParameterType def(Ref<? extends Def> def);
    public Variance getVariance();
}
