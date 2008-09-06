/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.visit;

import java.util.Collections;

import polyglot.ast.Assign;
import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldDecl;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ext.x10.ast.X10Cast;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.extension.X10Ext;
import polyglot.ext.x10.types.ParameterType;
import polyglot.ext.x10.types.ParameterType_c;
import polyglot.ext.x10.types.PathType;
import polyglot.ext.x10.types.TypeParamSubst;
import polyglot.ext.x10.types.X10ClassType;
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
public class X10Boxer extends AscriptionVisitor {
    X10TypeSystem xts;

    public X10Boxer(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (X10TypeSystem) ts;
    }

    private boolean needsExplicitConversion(Type fromType, Type toType) {
        // We want to insert explicit boxing and unboxing so that casts are
        // inserted during Java translation as needed.
        X10TypeSystem ts = xts;
        
        if (fromType == toType)
            return false;
        
        if (ts.typeEquals(fromType, toType))
            return false;
      
        if (ts.isImplicitCastValid(fromType, toType)) {
            fromType = X10TypeMixin.baseType(fromType);
            toType = X10TypeMixin.baseType(toType);
   
            if (toType.isNumeric() && ! fromType.isNumeric()) {
                return true;
            }
            
            if (toType.isBoolean() && ! fromType.isBoolean()) {
                return true;
            }
            
            if (fromType instanceof ParameterType || toType instanceof ParameterType) {
                if (TypeParamSubst.isSameParameter((ParameterType) fromType, (ParameterType) toType))
                    return false;
                return true;
            }

            if (fromType instanceof PathType || toType instanceof PathType) {
                return true;
            }
            
            // Be conservative and coerce if there are any type arguments.
            // This handles: Cons[Object] <-- Cons[Int], for instance.
            if (toType instanceof X10ClassType) {
                X10ClassType ct = (X10ClassType) toType;
                if (ct.typeArguments().size() > 0)
                    return true;
            }
            
            if (ts.isSubtype(fromType, toType)) {
                return false;
            }
            
            return true;
        }
        
        return false;
    }

    /**
     * Override to workaround problem with Assign to a boxed numeric from a
     * constant. The default implementation of childExpectedType uses the child
     * type if an implicit coercion is allowed, but we're trying to insert
     * explicit coercions in place of implicit.
     */
    public NodeVisitor enterCall(Node parent, Node n) throws SemanticException {
        Type t = null;

        if (parent instanceof LocalDecl) {
            LocalDecl a = (LocalDecl) parent;
            if (n == a.init()) {
                t = a.declType();
            }
        }

        if (parent instanceof FieldDecl) {
            FieldDecl a = (FieldDecl) parent;
            if (n == a.init()) {
                t = a.declType();
            }
        }

        if (parent instanceof Assign) {
            Assign a = (Assign) parent;
            if (n == a.right()) {
                t = a.leftType();
            }
        }
        
        if (t == null) {
            if (parent != null && n instanceof Expr) {
                t = parent.childExpectedType((Expr) n, this);
            }
        }

        X10Boxer v = (X10Boxer) copy();
        v.outerAscriptionVisitor = this;
        v.type = t;

        return v;
    }

    /**
     * This method rewrites an AST node. We have to be careful also to provide
     * type information with the newly created node, because the type checker
     * ran before this pass and the node must hence be annotated. Just calling
     * the node factory is not sufficient.
     * 
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
