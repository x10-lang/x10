/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.visit;


import java.util.Collections;

import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ext.x10.ast.X10Cast;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.extension.X10Ext;
import polyglot.ext.x10.types.X10PrimitiveType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.MethodInstance;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Name;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;

/**
 * Visitor that inserts boxing and unboxing code into the AST.
 */
public class X10Boxer extends AscriptionVisitor
{
    X10TypeSystem xts;

    public X10Boxer(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (X10TypeSystem) ts;
    }

    private boolean needsExplicitConversion(Type fromType, Type toType) {
        if (ts.isImplicitCastValid(fromType, toType) && !ts.isSubtype(fromType, toType)) {
            return true;
        }
        return false;
    }

    /**
     * This method rewrites an AST node. We have to be careful also to
     * provide type information with the newly created node, because the
     * type checker ran before this pass and the node must hence be
     * annotated. Just calling the node factory is not sufficient.
     * @throws SemanticException 
     */
    public Expr ascribe(Expr e, Type toType) throws SemanticException {
        Type fromType = e.type();

        if (toType == null) {
            return e;
        }

        Position p = e.position();

        if (needsExplicitConversion(fromType, toType)) {
            X10NodeFactory nf = (X10NodeFactory) this.nf;
            X10Cast c = (X10Cast) nf.X10Cast(p, nf.CanonicalTypeNode(p, Types.ref(toType)), e, true);
            ContextVisitor tc = new ContextVisitor(job, ts, nf);
            tc = tc.context(this.context());
            return (Expr) c.typeCheck(tc);
        }

        return e;
    }

}
