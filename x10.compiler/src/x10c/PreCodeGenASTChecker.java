/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10c;

import polyglot.ast.ArrayAccess;
import polyglot.ast.ArrayInit;
import polyglot.ast.LocalClassDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.Try;
import polyglot.ast.Typed;
import polyglot.frontend.Job;
import polyglot.types.Type;
import polyglot.util.ErrorInfo;
import polyglot.visit.NodeVisitor;
import x10.ast.Async;
import x10.ast.Async_c;
import x10.ast.AtExpr;
import x10.ast.AtExpr_c;
import x10.ast.AtStmt;
import x10.ast.AtStmt_c;
import x10.ast.Atomic;
import x10.ast.Atomic_c;
import x10.ast.Finish;
import x10.ast.Finish_c;
import x10.ast.Here;
import x10.ast.Here_c;
import x10.ast.Next;
import x10.ast.Next_c;
import x10.ast.SettableAssign;
import x10.ast.StmtExpr;
import x10.ast.Tuple;
import x10.ast.When;
import x10.ast.When_c;
import x10.ast.X10Loop;

/**
 * The job of this visitor is to run immediately before the
 * final Java code generation pass and verify that no unexpected
 * kinds of ASTs are being handed to the codegen pass.
 */
public class PreCodeGenASTChecker extends NodeVisitor {
    private Job job;

    public PreCodeGenASTChecker(Job job) {
        this.job = job;
    }

    @Override
    public Node visitEdgeNoOverride(Node parent, Node n) {
    	String m = isIllegalAST(n);

    	if (m!=null) {
    		String msg = "java codegen: "+m+("!")+(" n=")+(n).toString();
    		job.compiler().errorQueue().enqueue(ErrorInfo.INVARIANT_VIOLATION_KIND,msg,n.position());
    	} else {
    	    n.del().visitChildren(this); // only recurse to the children if there isn't an error already.
        }
    	return n;
    }
    
    private String isIllegalAST(Node n) {
        if (n == null) return "Cannot visit null";

        if (n instanceof X10Loop) {
            return "X10Loop should have been expanded before codegen";
        }
        
        if (n instanceof Atomic_c || n instanceof Next_c || n instanceof Finish_c ||
                n instanceof AtExpr_c  || n instanceof AtStmt_c || n instanceof Here_c ||
                n instanceof When_c || n instanceof Async_c) {
            return "High-level X10 construct should have been lowered";
        }

        if (n instanceof SettableAssign) {
            return "Settable assign should have been expanded";
        }
        
        if (n instanceof Typed) {
            Type t = ((Typed)n).type();
            if (t != null && t.typeSystem().isUnknown(t)) {
                // Hmm, it appears odd that a Typed can have a Type of null,
                // but it does happen more often than one would expect.
                // A different investigation...
                return "<unknown> type present in AST";
            }
        }

        return null;
    }
}