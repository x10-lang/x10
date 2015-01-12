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
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.parse;

import polyglot.ast.*;
import polyglot.types.*;
import polyglot.types.Package;
import polyglot.util.*;

/**
 * Represents an ambiguous, possibly qualified, identifier encountered while parsing.
 */
public class ParsedName {
    public final ParsedName prefix;
    public final Id name;
    public final Position pos;
    public final NodeFactory nf;
    public final TypeSystem ts;

    public ParsedName(NodeFactory nf, TypeSystem ts, Position pos, Id name) {
        this(nf, ts, pos, null, name);
    }
    
    public ParsedName(NodeFactory nf, TypeSystem ts, Position pos, ParsedName prefix, Id name) {
        this.nf = nf;
        this.ts = ts;
        this.pos = pos != null ? pos : Position.COMPILER_GENERATED;
        this.prefix = prefix;
        this.name = name;
    }

    // expr
    public Expr toExpr() {
        if (prefix == null) {
            return nf.AmbExpr(pos, name);
        }

        return nf.Field(pos, prefix.toReceiver(), name);
    }

    // expr or type
    public Receiver toReceiver() {
        if (prefix == null) {
            return nf.AmbReceiver(pos, name);
        }

        return nf.AmbReceiver(pos, prefix.toPrefix(), name);
    }

    // expr, type, or package
    public Prefix toPrefix() {
        if (prefix == null) {
            return nf.AmbPrefix(pos, name);
        }

        return nf.AmbPrefix(pos, prefix.toPrefix(), name);
    }

    // package
    public PackageNode toPackage() {
        if (prefix == null) {
            return nf.PackageNode(pos, Types.ref(ts.createPackage((Ref<? extends Package>) null, name.id())));
        }
        else {
            return nf.PackageNode(pos, Types.ref(ts.createPackage(prefix.toPackage().package_(), name.id())));
        }
    }

    // type
    public TypeNode toType() {
        if (prefix == null) {
            return nf.AmbTypeNode(pos, name);
        }

        return nf.AmbTypeNode(pos, prefix.toPrefix(), name);
    }

    public String toString() {
        if (prefix == null) {
            return name.toString();
        }

        return prefix.toString() + "." + name.toString();
    }
}
