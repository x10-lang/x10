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
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.frontend.Job;
import polyglot.types.Flags;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.X10NodeFactory_c;
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
   //       Type type          = Types.baseType(n.type());
            Type type          = n.type();
            Allocation a       = createAllocation(pos, type, n.typeArguments());
            LocalDecl ld       = syn.createLocalDecl(pos, Flags.FINAL, Name.makeFresh("alloc"), a);
            Local l            = syn.createLocal(pos, ld);
            ConstructorCall cc = createConstructorCall(n).target(l);
            List<Stmt> stmts   = new ArrayList<Stmt>();
            stmts.add(ld);
            stmts.add(cc);
            Node result        = syn.createStmtExpr(pos, stmts, syn.createLocal(pos, ld));
            if (DEBUG) debug(job, "ConstructorSplitterVisitor splitting " +n+ "\n\t" +result, pos);
            return result;
        }
        if (node instanceof LocalDecl && ((LocalDecl) node).init() instanceof New) {
            // We're in a statement context, so we can avoid a stmt expr.
            LocalDecl ld       = (LocalDecl) node;
            New n              = (New) ld.init();
            Type type          = n.type();
            Allocation a       = createAllocation(pos, type, n.typeArguments());
            List<Stmt> stmts   = new ArrayList<Stmt>();
            if (type.typeSystem().typeDeepBaseEquals(ld.declType(), n.type(), context)) {
                ld                 = ld.init(a);
                ConstructorCall cc = createConstructorCall(n).target(syn.createLocal(pos, ld));
                stmts.add(ld);
                stmts.add(cc);
            } else {
                // Type of the local != type of the new.
                // This happens when the type of the local decl is a supertype of the type of the new
                // Introduce additional localdecl so that the constructor call can be made
                // on a variable of the correct type. 
                LocalDecl ld2      = syn.createLocalDecl(pos, Flags.FINAL, Name.makeFresh("alloc"), a);
                ConstructorCall cc = createConstructorCall(n).target(syn.createLocal(pos, ld2));
                ld                 = ld.init(syn.createLocal(pos, ld2));
                stmts.add(ld2);
                stmts.add(cc);
                stmts.add(ld);
            }
            Node result        = syn.createStmtSeq(pos, stmts);
            if (DEBUG) debug(job, "ConstructorSplitterVisitor splitting " +node+ "\n\t" +result, pos);
            return result;
        }
        return super.leaveCall(parent, old, node, v);
    }

    /**
     * @param n
     * @param pos
     * @return
     */
    private ConstructorCall createConstructorCall(New n) {
        return nf.ThisCall(n.position(), n.arguments()).constructorInstance(n.constructorInstance());
    }

    /**
     * @param pos
     * @param type
     * @return
     * TODO: move to Synthesizer
     */
    private Expr createThis(Position pos, Type type) {
        return nf.This(pos).type(type);
    }

    /**
     * Create an artificial Allocation node.
     * 
     * @param pos the Position of the allocation
     * @param type the Type of the object (or struct) being allocated
     * @param typeArgs 
     * @return a synthesized Allocation node.
     * TODO: move to Synthesizer
     */
    private Allocation createAllocation(Position pos, Type type, List<TypeNode> typeArgs) {
        return (Allocation) ((Allocation) ((X10NodeFactory_c) nf).Allocation(pos).type(type)).typeArguments(typeArgs);
    }

}
