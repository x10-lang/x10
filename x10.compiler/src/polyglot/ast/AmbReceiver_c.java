/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.ast;

import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.*;
import x10.errors.Errors;
import x10.types.X10LocalInstance;

/**
 * An <code>AmbReceiver</code> is an ambiguous AST node composed of
 * dot-separated list of identifiers that must resolve to a receiver.
 */
public class AmbReceiver_c extends AmbPrefix_c implements AmbReceiver
{
    protected Type type;

    public AmbReceiver_c(Position pos, Prefix prefix, Id name) {
	super(pos, prefix, name);
	assert(name != null); // prefix may be null
    }

    public Type type() {
            return this.type;
    }

    public AmbReceiver type(Type type) {
            AmbReceiver_c n = (AmbReceiver_c) copy();
            n.type = type;
            return n;
    }

    public Node buildTypes(TypeBuilder tb) {
        return type(tb.typeSystem().unknownType(position()));
    }

    /** Disambiguate the receiver. */
    public Node disambiguate(ContextVisitor ar) {
//        try {
            return super.disambiguate(ar);
//        } catch (SemanticException e) {
//            Errors.issue(ar.job(), e, this);
//            TypeSystem xts =  ar.typeSystem();
//            X10LocalInstance li = xts.createFakeLocal(name.id(), e);
//            return ar.nodeFactory().Local(position(), name).localInstance(li).type(li.type());
//        }
    }
    

    public Node typeCheck(ContextVisitor tc) {
        // Didn't finish disambiguation; just return.
        return this;
    }
    

}
