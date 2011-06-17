/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2010.
 */
package x10.optimizations.inlining;

import polyglot.types.Types;

import x10.ast.Closure;
import x10.types.ClosureDef;
import x10.types.ClosureInstance;
import x10.types.TypeParamSubst;
import x10.types.X10CodeDef;
import x10.visit.Reinstantiator;

/**
 * @author Bowen Alpern
 *
 */
public class InliningTypeTransformer extends Reinstantiator {

    private X10CodeDef caller;
    private X10CodeDef callee;

    /**
     * @param subst
     */
    public InliningTypeTransformer(TypeParamSubst subst, X10CodeDef caller, X10CodeDef callee) {
        super(subst);
        this.caller = caller;
        this.callee = callee;
    }

    /* (non-Javadoc)
     * @see x10.visit.TypeTransformer#transformClosureInstance(x10.types.ClosureInstance)
     */ /*
    @Override
    protected ClosureInstance transformClosureInstance(ClosureInstance ci) {
        ci = super.transformClosureInstance(ci);
        ClosureDef cd = ci.def();
        cd.setStaticContext(staticContext);
        cd.setMethodContainer(Types.ref(visitor().context().currentCode().asInstance()));
        cd.setTypeContainer(Types.ref(visitor().context().currentClass()));
        return ci;
    } */

    /* (non-Javadoc)
     * @see x10.visit.TypeTransformer#transform(x10.ast.Closure, x10.ast.Closure)
     */
    @Override
    protected Closure transform(Closure d, Closure old) {
        Closure c = super.transform(d, old);
        ClosureDef cd = c.closureDef();
        cd.setStaticContext(caller.staticContext());
        X10CodeDef currentCode = visitor().context().currentCode();
        if (currentCode == callee) currentCode = caller; // minor hack: remap callee to caller
        cd.setMethodContainer(Types.ref(currentCode.asInstance()));
        cd.setTypeContainer(Types.ref(visitor().context().currentClass()));
        return c.closureDef(cd);
    }

}
