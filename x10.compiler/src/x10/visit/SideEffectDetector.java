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
package x10.visit;

import java.util.HashSet;
import java.util.Set;

import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Cast;
import polyglot.ast.Expr;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.ProcedureCall;
import polyglot.ast.Unary;
import polyglot.frontend.Job;
import polyglot.types.LocalDef;
import polyglot.types.ProcedureDef;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.X10Cast;
import x10.errors.Errors;
import x10.extension.X10Ext;
import x10.types.X10Def;
import x10.types.checker.Converter;

/**
 * @author Bowen Alpern
 *
 */
public class SideEffectDetector extends ContextVisitor {

    public SideEffectDetector(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }

    private final boolean sideEffectDetected[] = new boolean[1];
    private final Set<LocalDef> captiveLocals = CollectionFactory.newHashSet();
    /**
     * @param expr
     * @return
     */
    public boolean hasSideEffects(Expr expr) {
        if (null == expr) return false;
        captiveLocals.clear();
        sideEffectDetected[0] = false;
        expr.visit(this);
        return sideEffectDetected[0];
    }

    /**
     * Annotation types.
     */
    private Type ImpureType;
    private Type PureType;

    /**
     * Names of the annotation types that pertain to dead assignment elimination.
     */
    private static final QName SIDE_EFFECTS_ANNOTATION    = QName.make("x10.compiler.Impure");
    private static final QName NO_SIDE_EFFECTS_ANNOTATION = QName.make("x10.compiler.Pure");

    @Override
    public NodeVisitor begin() {
        try {
            ImpureType = ts.systemResolver().findOne(SIDE_EFFECTS_ANNOTATION);
            PureType   = ts.systemResolver().findOne(NO_SIDE_EFFECTS_ANNOTATION);
        } catch (SemanticException e) {
            InternalCompilerError ice = new InternalCompilerError("Unable to find required Annotation Types");
            SemanticException se = new SemanticException(ice); 	//TODO: Internal compiler error should be removed
            Errors.issue(job, se);
            PureType = null;
        }
        return super.begin();
    }

    
    /* (non-Javadoc)
     * @see polyglot.visit.NodeVisitor#override(polyglot.ast.Node)
     */
    @Override
    public Node override(Node n) {
        if (sideEffectDetected[0]) return n;
        if (hasPureAnnotation(n)) return n;
        if (n instanceof Expr && ((Expr) n).isConstant()) return n;
        if ( hasImpureAnnotation(n)
             || (n instanceof ProcedureCall && !isPure(((ProcedureCall) n).procedureInstance().def()))
             || (n instanceof Assign && isGlobalAssign((Assign) n))
             || (n instanceof Unary && isGlobalIncrement((Unary) n))
             || (n instanceof Cast) && mustCheck((Cast) n)
             || (n instanceof Binary && ((Binary) n).operator() == Binary.DIV) ) {
            sideEffectDetected[0] = true;
            return n;
        }
        if (n instanceof LocalDecl) {
            LocalDecl ld = (LocalDecl) n;
            captiveLocals.add(ld.localDef());
        }
        return null;
    }

    /**
     * @param n
     * @return
     */
    private boolean mustCheck(Cast n) {
        // if (n.expr().type is a subtype of n.castType()) return false; // TODO: implement this
        if (!(n instanceof X10Cast)) 
            return true;
        X10Cast c = (X10Cast) n;
        if (c.conversionType() == Converter.ConversionType.CHECKED) {
            return true;
        }
        if (true) // DEBUG: for now, check all casts
            return true; 
        return false;
    }


    /**
     * Determine if a procedure has one or more side effects.
     * 
     * @param def the procedure in question
     * @return true, if def has a side effect; false, otherwise
     * TODO: examine the body of def for side effects.
     */
    private boolean isPure(ProcedureDef def) {
        if (!(def instanceof X10Def)) return false;
        if (null == PureType) 
            return false;
        try {
            return !((X10Def) def).annotationsMatching(PureType).isEmpty();
        } catch (NullPointerException npe) { // don't know why this happens, but it does
            return false;                    // conservatively assume the worst
        }
    }


    /**
     * @param n
     * @return
     */
    private boolean isGlobalAssign(Assign n) {
        if (!(n.left() instanceof Local)) return true;
        return !captiveLocals.contains(((Local) n.left()).localInstance().def());
    }


    /**
     * @param n
     * @return
     */
    private boolean isGlobalIncrement(Unary u) {
        if (u.expr() instanceof Local && captiveLocals.contains(((Local) u.expr()).localInstance().def())) return false;
        if (u.operator().equals(Unary.POST_DEC)) return true;
        if (u.operator().equals(Unary.PRE_DEC))  return true;
        if (u.operator().equals(Unary.POST_INC)) return true;
        if (u.operator().equals(Unary.PRE_INC))  return true;
        return false;
    }

    /**
     * @param node
     * @return
     */
    private boolean hasImpureAnnotation(Node node) {
        if (node.ext() instanceof X10Ext){
            X10Ext ext = (X10Ext) node.ext();
            return !ext.annotationMatching(ImpureType).isEmpty();
        }
        return false;
    }

    /**
     * @param node
     * @return
     */
    private boolean hasPureAnnotation(Node node) {
        if (node.ext() instanceof X10Ext){
            X10Ext ext = (X10Ext) node.ext();
            return !ext.annotationMatching(PureType).isEmpty();
        }
        return false;
    }

}
