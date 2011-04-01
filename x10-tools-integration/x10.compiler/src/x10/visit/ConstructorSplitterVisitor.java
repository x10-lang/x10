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

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Allocation;
import polyglot.ast.ConstructorCall;
import polyglot.ast.FieldDecl;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.frontend.Job;
import polyglot.types.Flags;
import polyglot.types.Name;
import polyglot.types.ObjectType;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.types.X10TypeObjectMixin;
import x10.util.AltSynthesizer;

/**
 * Split X10 constructor nodes (X10New_c) into
 *     1) a primative X10Allocation that the backends will cause memory allocation
 *     2) a "method" (X10New_c) that has the X10Allocation as its target.
 *
 * see http://xj.watson.ibm.com/twiki/bin/view/Main/TheXtenProject/XtenConstructorSplitting
 *
 * @author Bowen Alpern
 *
 */
public class ConstructorSplitterVisitor extends ContextVisitor {

    private static final boolean DEBUG = false;
    private static final QName NATIVE_CLASS_ANNOTATION = QName.make("x10.compiler.NativeClass");
    private static final QName NATIVE_REP_ANNOTATION   = QName.make("x10.compiler.NativeRep");
    
    private void debug (Job job, String msg, Position pos) {
        if (!DEBUG) return;
        System.out.println("DEBUG: " +pos+ " " +msg);
    }
    
    private AltSynthesizer syn;
    /**
     * @param job
     * @param ts
     * @param nf
     */
    public ConstructorSplitterVisitor(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        assert x10.optimizations.Optimizer.CONSTRUCTOR_SPLITTING(job.extensionInfo());
        syn = new AltSynthesizer(ts, nf);
    }

    /* (non-Javadoc)
     * @see polyglot.visit.NodeVisitor#override(polyglot.ast.Node)
     * 
     * Note: C++ backend apparently cannot handle StmtExpr's "outside functions"
     */
    @Override
    public Node override(Node n) {
        if (n instanceof FieldDecl) return n;
        return super.override(n);
    }

    /**
     * Split X10 constructors (New AST nodes) into allocation and initialization parts.
     * Memory allocation is represented by an Allocation AST node.  The type of this node
     * tells the back-ends how much memory to allocate.  Initialization is handled by
     * ConstructorCall AST nodes (effectively, a call to this() with arguments).  These nodes
     * now take a target which resolves to the allocated storage.  In the case of Special
     * calls (explicit calls to this() or super()), this target is a synthesized "this" AST
     * node.
     * 
     * @see polyglot.visit.ErrorHandlingVisitor#leaveCall(polyglot.ast.Node, polyglot.ast.Node, polyglot.ast.Node, polyglot.visit.NodeVisitor)
     * @return an AST Node with all New nodes replaced by an Allocation and a ConstructorCall,
     * and all ConstructorCall nodes having a target.
     */
    @Override
    protected Node leaveCall(Node parent, Node old, Node node, NodeVisitor v) throws SemanticException {
        Position pos = node.position();
        if (node instanceof New && !(parent instanceof LocalDecl)){
            New n              = (New) node;
            if (cannotSplitConstructor(n.constructorInstance().container().toClass()))
                return n;
            Type type          = n.type();
            Allocation a       = syn.createAllocation(pos, type, n.typeArguments());
            LocalDecl ld       = syn.createLocalDecl(pos, Flags.FINAL, Name.makeFresh("alloc"), a);
            Local l            = syn.createLocal(pos, ld);
            ConstructorCall cc = syn.createConstructorCall(l, n);
            List<Stmt> stmts   = new ArrayList<Stmt>();
            stmts.add(ld);
            stmts.add(cc);
            Node result        = syn.createStmtExpr(pos, stmts, syn.createLocal(pos, ld));
            if (DEBUG) debug(job, "ConstructorSplitterVisitor splitting \n\t" +node+ "  ~>\n\t" +result, pos);
            return result;
        }
        if (node instanceof LocalDecl && ((LocalDecl) node).init() instanceof New) {
            LocalDecl ld       = (LocalDecl) node;
            New n              = (New) ld.init();
            if (cannotSplitConstructor(n.constructorInstance().container().toClass()))
                return ld;
            Type type          = n.type();
            Allocation a       = syn.createAllocation(pos, type, n.typeArguments());
            // We're in a statement context, so we can avoid a stmt expr.
            List<Stmt> stmts   = new ArrayList<Stmt>();
            if (type.typeSystem().typeDeepBaseEquals(ld.declType(), n.type(), context)) {
                ld                 = ld.init(a);
                ConstructorCall cc = syn.createConstructorCall(syn.createLocal(pos, ld), n);
                stmts.add(ld);
                stmts.add(cc);
            } else {
                // Type of the local != type of the new.
                // This happens when the type of the local decl is a supertype of the type of the new
                // Introduce additional localdecl so that the constructor call can be made
                // on a variable of the correct type. 
                LocalDecl ld2      = syn.createLocalDecl(pos, Flags.FINAL, Name.makeFresh("alloc"), a);
                ConstructorCall cc = syn.createConstructorCall(syn.createLocal(pos, ld2), n);
                ld                 = ld.init(syn.createLocal(pos, ld2));
                stmts.add(ld2);
                stmts.add(cc);
                stmts.add(ld);
            }
            Node result        = syn.createStmtSeq(pos, stmts);
            if (DEBUG) debug(job, "ConstructorSplitterVisitor splitting \n\t" +node+ "  ~>\n\t" +result, pos);
            return result;
        } 
        if (node instanceof ConstructorCall) {
            ConstructorCall cc = (ConstructorCall) node;
            if (null == cc.target())
                return cc.target(syn.createThis(node.position(), cc.constructorInstance().returnType()));
        }
        return super.leaveCall(parent, old, node, v);
    }

    /**
     * @param type
     * @return
     */
    public static boolean cannotSplitConstructor(Type type) {
        if (null == type || type.typeEquals(type.typeSystem().Object(), type.typeSystem().emptyContext()))
            return false;
        if (hasNaiveAnnotation(type)) 
            return true;
        if (type instanceof ObjectType)
            return cannotSplitConstructor(((ObjectType) type).superClass());
        return false;
    }

    /**
     * @param type
     * @return
     */
    private static boolean hasNaiveAnnotation(Type type) {
        List<Type> annotations = type.annotations();
        if (null == annotations || annotations.isEmpty()) 
            return false;
        if (!X10TypeObjectMixin.annotationsNamed(annotations, NATIVE_CLASS_ANNOTATION).isEmpty())
            return true;
        if (!X10TypeObjectMixin.annotationsNamed(annotations, NATIVE_REP_ANNOTATION).isEmpty())
            return true;
        return false;
    }

}
