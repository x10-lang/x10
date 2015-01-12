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

package x10.ast;

import polyglot.ast.Block;
import polyglot.ast.FlagsNode;
import polyglot.ast.Initializer_c;
import polyglot.ast.Node;
import polyglot.types.ClassDef;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.InitializerDef;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import x10.types.X10ClassDef;
import polyglot.types.Context;
import x10.types.X10InitializerDef;
import x10.types.checker.PlaceChecker;

public class X10Initializer_c extends Initializer_c {

    public X10Initializer_c(Position pos, FlagsNode flags, Block body) {
        super(pos, flags, body);
    }

    public InitializerDef createInitializerDef(TypeSystem ts, ClassDef ct, Flags flags) {
        X10InitializerDef ii;
        ii = (X10InitializerDef) super.createInitializerDef(ts, ct , flags);
        if (! ii.flags().isStatic())
        	ii.setThisDef(((X10ClassDef) ct).thisDef());
        return ii;
    }
    @Override
    public Context enterChildScope(Node child, Context c) {
        if (child == body) {
        	//c = PlaceChecker.pushHereTerm(initializerDef(), (X10Context) c);
        }
        return c;
    }

}
