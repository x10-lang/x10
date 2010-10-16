/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.visit;


import polyglot.frontend.ExtensionInfo;
import polyglot.types.*;
import polyglot.util.InternalCompilerError;
import x10.ast.Call;
import x10.ast.CanonicalTypeNode;
import x10.ast.ClassDecl;
import x10.ast.ConstructorCall;
import x10.ast.Expr;
import x10.ast.Field;
import x10.ast.FieldDecl;
import x10.ast.Formal;
import x10.ast.Initializer;
import x10.ast.Local;
import x10.ast.LocalDecl;
import x10.ast.MethodDecl;
import x10.ast.New;
import x10.ast.Node;
import x10.ast.NodeFactory;
import x10.ast.PackageNode;
import x10.ast.TypeNode;
import x10.types.Package;
import x10.types.QName;
import x10.types.Type;
import x10.types.TypeSystem;
import x10.types.Types;

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
