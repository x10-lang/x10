/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.visit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Term;
import polyglot.ast.Expr;
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.visit.InitChecker;
import polyglot.visit.NodeVisitor;
import polyglot.visit.FlowGraph;
import x10.ast.X10ClassDecl;
import x10.ast.ParExpr;

public class X10InitChecker extends InitChecker {

    public X10InitChecker(Job job, TypeSystem ts, NodeFactory nf) {
	super(job, ts, nf);
    }

    public Map flow(Item trueItem, Item falseItem, Item otherItem,
            FlowGraph graph, Term n, boolean entry, Set succEdgeKeys) {
        Item inItem = safeConfluence(trueItem, FlowGraph.EDGE_KEY_TRUE,
                                     falseItem, FlowGraph.EDGE_KEY_FALSE,
                                     otherItem, FlowGraph.EDGE_KEY_OTHER,
                                     n, entry, graph);
        if (entry) {
            return itemToMap(inItem, succEdgeKeys);
        }
        if (inItem == BOTTOM) {
            return itemToMap(BOTTOM, succEdgeKeys);
        }
        DataFlowItem inDFItem = ((DataFlowItem)inItem);
        if (n instanceof ParExpr && ((ParExpr)n).type().isBoolean()) {
            if (trueItem == null) trueItem = inDFItem;
            if (falseItem == null) falseItem = inDFItem;
            return itemsToMap(trueItem, falseItem, inDFItem, succEdgeKeys);
        }
        return super.flow(trueItem, falseItem, otherItem, graph,n, entry, succEdgeKeys);
    }
    /**
     * Overridden superclass method.
     * 
     * Set up the state that must be tracked during a Class Declaration.
     */
    protected NodeVisitor enterCall(Node parent, Node n) throws SemanticException {
        if (n instanceof ClassBody) {
            // we are starting to process a class declaration, but have yet
            // to do any of the dataflow analysis.
            ClassBody cb = (ClassBody) n;
            
            // Add the properties to the class body when initializing.
	    if (parent instanceof X10ClassDecl) {
                List<ClassMember> members;
                members = new ArrayList<ClassMember>();
                members.addAll(cb.members());
                members.addAll(((X10ClassDecl) parent).properties());
                cb = cb.members(members);
            }
	    
	    // set up the new ClassBodyInfo, and make sure that it forms
	    // a stack.
	    ClassDef ct = null;
            if (parent instanceof ClassDecl) {
        	ct = ((ClassDecl) parent).classDef();
            }
            else if (parent instanceof New) {
                ct = ((New) parent).anonType();
            }
            if (ct == null) {
                throw new InternalCompilerError("ClassBody found but cannot find the class.", n.position());
            }
            
            setupClassBody(ct, cb);
        }
      
        return super.enterCall(n);
    }

}
