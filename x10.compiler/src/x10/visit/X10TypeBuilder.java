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

package x10.visit;

import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.TypeBuilder;
import x10.types.X10ClassDef;
import x10.types.constraints.TypeConstraint;

public class X10TypeBuilder extends TypeBuilder {

    public X10TypeBuilder(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }

    @Override
    protected X10ClassDef createAnonClass(Position pos) {
        X10ClassDef def = (X10ClassDef) super.createAnonClass(pos);

        TypeSystem ts = typeSystem();

        def.setThisDef(typeSystem().thisDef(pos, Types.ref(def.asType())));
        def.setTypeBounds(Types.ref(new TypeConstraint()));

        return def;
    }

}
