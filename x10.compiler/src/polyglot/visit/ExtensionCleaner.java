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

package polyglot.visit;

import polyglot.ast.*;
import polyglot.frontend.ExtensionInfo;
import polyglot.types.*;
import polyglot.types.Package;
import polyglot.util.InternalCompilerError;

/**
 * This visitor overwrites all extension object refs with null,
 * sets delegate object refs to point back to the node,
 * and strips type information out.
 **/
public class ExtensionCleaner extends NodeVisitor {
    protected NodeFactory nf;
    protected TypeSystem ts;
    protected ExtensionInfo javaExt;

    public ExtensionCleaner(ExtensionInfo javaExt) {
        this.javaExt = javaExt;
        this.nf = javaExt.nodeFactory();
        this.ts = javaExt.typeSystem();
    }

    @Override
    public Node leave(Node old, Node n, NodeVisitor v) {
        n = n.ext(null);
        n = n.del(null);
        n = strip(n);
        return n;
    }

    protected Node strip(Node n) {
        // FIXME: should use method dispatch for this.
        if (n instanceof Call) {
            n = ((Call) n).methodInstance(null);
        }

        if (n instanceof ClassDecl) {
            n = ((ClassDecl) n).classDef(null);
        }

        if (n instanceof ConstructorCall) {
            n = ((ConstructorCall) n).constructorInstance(null);
        }

        if (n instanceof Field) {
            n = ((Field) n).fieldInstance(null);
        }

        if (n instanceof FieldDecl) {
            n = ((FieldDecl) n).fieldDef(null);
            n = ((FieldDecl) n).initializerDef(null);
        }

        if (n instanceof Formal) {
            n = ((Formal) n).localDef(null);
        }

        if (n instanceof Initializer) {
            n = ((Initializer) n).initializerDef(null);
        }

        if (n instanceof Local) {
            n = ((Local) n).localInstance(null);
        }

        if (n instanceof LocalDecl) {
            n = ((LocalDecl) n).localDef(null);
        }

        if (n instanceof MethodDecl) {
            n = ((MethodDecl) n).methodDef(null);
        }

        if (n instanceof New) {
            n = ((New) n).anonType(null);
            n = ((New) n).constructorInstance(null);
        }

        if (n instanceof TypeNode) {
            n = convert((TypeNode) n);
        }

        if (n instanceof PackageNode) {
            n = convert((PackageNode) n);
        }

        if (n instanceof Expr) {
            n = ((Expr) n).type((Type) null);
        }

        return n;
    }

    protected TypeNode convert(TypeNode n) {
        Type t = n.type();

        if (n instanceof CanonicalTypeNode) {
            if (t.typeSystem() == ts) {
                return n;
            }
            else {
                throw new InternalCompilerError("Unexpected Jx type: " + t + " found in rewritten AST.");
            }
        }

        // Must be an ambiguous TypeNode

        if (t != null) {
            if (t.typeSystem() == ts) {
                return nf.CanonicalTypeNode(n.position(), t);
            }
        }

        // ### unimplemented
        assert false;
//        n = n.type(null);

        return n;
    }

    protected PackageNode convert(PackageNode n) {
        Package p = Types.get(n.package_());

        if (p != null) {
            if (p.typeSystem() == ts) {
                return nf.PackageNode(n.position(), Types.ref(p));
            }
        }
                  
        return nf.PackageNode(n.position(), Types.ref(ts.createPackage(QName.make(n.toString()))));
    }
}
