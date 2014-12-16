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

package x10.visit;

import java.util.HashMap;
import java.util.Map;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.main.Reporter;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.types.UnknownType;
import polyglot.util.ErrorInfo;
import polyglot.util.Position;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;
import x10.errors.Errors;

/**
 * @author vj
 *
 */
public class X10TypeChecker extends TypeChecker {

	/**
	 * @param job
	 * @param ts
	 * @param nf
	 */
	private X10TypeChecker(Job job, TypeSystem ts, NodeFactory nf) {
	    this(job, ts, nf, CollectionFactory.<Node, Node>newHashMap());
	}
	/**
	 * @param job
	 * @param ts
	 * @param nf
	 * @param memo
	 */
	public X10TypeChecker(Job job, TypeSystem ts, NodeFactory nf, Map<Node, Node> memo) {
	    this(job, ts, nf, memo, false);
	}
	public X10TypeChecker(Job job, TypeSystem ts, NodeFactory nf,
			Map<Node, Node> memo, boolean isFragmentChecker) {
		super(job, ts, nf, memo);
		this.extensionInfo = (x10.ExtensionInfo) job.extensionInfo();
		this.memo = memo;
	}
	
	private x10.ExtensionInfo extensionInfo;
	
	// TODO: record the top-level node in a memo only if typechecking a fragment
    @Override
	public Node override(Node parent, Node n) {
	    Node n_ = memo.get(n);
	    if (n_ != null) {
	        this.addDecls(n_);
	        return n_;
	    }

	    if (reporter.should_report(reporter.visit, 2))
	        reporter.report(2, ">> " + this + "::override " + n);

	    Node m = n.del().typeCheckOverride(parent, this);

	    if (m != null) {
//	        memo.put(n, m);
//	        memo.put(m, m);
	    }

	    return m;
	}

	protected NodeVisitor enterCall(Node n) {
	    return super.enterCall(n);
	}
	    
	protected Node leaveCall(Node old, Node n, NodeVisitor v) {
	    final TypeChecker tc = (TypeChecker) v;
	    // Inline the super call without checking for expressions with unknown type
	    Node m = n;
	    m = m.del().disambiguate(tc);
	    m = m.del().typeCheck(tc);
	    m = m.del().checkConstants(tc);
	    // Record the new node in the memo table.
//	    memo.put(old, m);
//	    memo.put(n, m);
//	    memo.put(m, m);
	    return m;
	}

	public static X10TypeChecker getTypeChecker(ContextVisitor tc) {
	    return (X10TypeChecker)
	        (tc instanceof X10TypeChecker ? tc :
	            new X10TypeChecker(tc.job(), tc.typeSystem(), tc.nodeFactory()).context(tc.context()));
	}
}
