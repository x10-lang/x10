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



import java.util.LinkedList;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Conditional;
import polyglot.ast.Do;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.For;
import polyglot.ast.Id;
import polyglot.ast.Lit;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Special;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.frontend.Job;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.Name;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PruningVisitor;
import x10.ast.X10Binary;
import x10.ast.X10NodeFactory;
import x10.types.X10Context;
/**
 * A visitor to flatten out a statement into a series of statements, replacing
 * nested method calls by a sequence of method calls. 
 * This enables inlining of method calls, e.g. array accesses.
 * The visitor is organized as an AST transform. An AST transform is a visitor
 * that takes two visitors as arguments, an AST pattern and an AST action.
 * It visits each node checking if the pattern is satisfied by the node and its parent.
 * If it is, the action is applied on the node and the result
 * is returned. In this case the AST pattern is NeedsFlatteningVisitor, and the 
 * AST action is Flattener. 
 * @seeAlso StmtSeq
 * @author vj
 *
 */
public class ExprFlattener extends ContextVisitor  {
	public static final Position pos = Position.COMPILER_GENERATED;
	final static NodeVisitor done = new PruningVisitor();
	public static final Flags flags = Flags.FINAL;
	public ExprFlattener(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
	}
	/**
	 * On being invoked on a node, this visitor will determine if the subtree rooted 
	 * at this node contains an X10ArrayAccessNode whose parent is not 
	 * a LocalDecl.
	 * Such a node needs to be flattened.
	 * @author vj
	 *
	 */
	public static class NeedsFlatteningVisitor extends NodeVisitor {
		boolean needsFlattening = false;
		
		protected Node root;
		public NeedsFlatteningVisitor(Node root) { 
			super();
			this.root = root;
		}
		public NodeVisitor enter(Node p, Node n) {
			// If you already know that this needs flattening, then you are done.
			if (needsFlattening) return done;
			// Do not enter the body of a conditional.
			// TODO: Fix this. First flatten the conditional into a series
			// of IF statements and then perform this transformation again.
			if (p instanceof Conditional) return done;
			if (p instanceof Do) return done;
			if (p instanceof For) return done;
			// Do not enter  a statement.
			 if (n instanceof Stmt && n != root) return done;
			needsFlattening = false && ! (p instanceof LocalDecl);
			return needsFlattening ? done : this;
		}
		public boolean needsFlattening() {
			return needsFlattening;
		}
	}
	/**
	 * This visitor must be invoked in a context that is pushing statements,
	 * and on a node that is  a statement which needs flattening. It will
	 * return a block equivalent to the statement, containing a flattening
	 * of the statement.
	 * 
	 * @author vj
	 *
	 */
	public static class Flattener extends ContextVisitor {
		List stmtList = new LinkedList();
		PruningVisitor done = new PruningVisitor();
		protected Node root;
		public List stmtList() {
			return stmtList;
		}
		public void add(Stmt m) {
			stmtList.add(m);
		}
		public Flattener(Job job, TypeSystem ts, NodeFactory nf, Node root) {
			super(job, ts, nf);
			this.root = root;
		}
		
		public Node override(Node p, Node n) {
			if (n instanceof Stmt && n!= root) return n;
			if (n instanceof X10Binary) {
				X10Binary f = (X10Binary) n;
				Binary.Operator op = f.operator();
				if (ts.isImplicitCastValid(f.type(), ts.Boolean(), context) && (op == Binary.COND_AND || op == Binary.COND_OR)) {
					return f.flatten(this);
				}
			}
			if (n instanceof Conditional) {
			    return n;
			}
			// otherwise do not override
			return null;
		}
	
		public Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) {
			Node result = n;
			X10Context xc = (X10Context) context();
			
			if (n instanceof Expr) {
				// now flatten this Expr, this code works for any expr.
				
				final Expr e = (Expr) n;
				
				// Local variable accesses, field accesses, literals, 
				// specials (this, super) and assign statements do not
				// need more flattening than has already been accomplished
				// by visiting their children.
				if (e instanceof Local 
						|| e instanceof Field  
						|| e instanceof Lit 
						|| e instanceof Special
						|| e instanceof Assign)
					return e;
				
				// If the parent is a LocalDecl or Assign, return immediately
				// without modifying this node, since this node is not
				// an expression which itself needs to be expanded.
				if (parent instanceof LocalDecl || parent instanceof Assign)
					return e;
				
				// Now expand the expression. 
				// Generate: <Type> newvarname = e
				// and return a reference to newvarname. Thus the expression e
				// has been converted to a variable reference.
				final Type type = e.type();
				if (! ts.typeEquals(type, ts.Void(), context)) {
					final Name varName = xc.getNewVarName();
					final TypeNode tn = nf.CanonicalTypeNode(pos,type);
					final LocalDef li = ts.localDef(pos, flags, Types.ref(type), varName);
					final Id varId = nf.Id(n.position(), varName);
					final LocalDecl ld = nf.LocalDecl(n.position(), nf.FlagsNode(n.position(), flags), tn, varId, e).localDef(li);
					final Local ldRef = (Local) nf.Local(n.position(), varId).localInstance(li.asInstance()).type(type);
					stmtList.add(ld);
					result=ldRef;
				}
			}
			return  result;
		}
	}
	protected boolean needsFlattening(Node n) {
		NeedsFlatteningVisitor v = new NeedsFlatteningVisitor(n);
		n.visit(v);
		return v.needsFlattening();
	}
	public NodeVisitor enterCall(Node p, Node n) {
		if (p instanceof For) return done;
		return this;
	}
	public Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) {
		if ((! (n instanceof Stmt))) {// || n instanceof CompoundStmt) {
			
			return n;
		}
		Stmt s = (Stmt) n;
		X10Context xc = (X10Context) context();
		Node result = n;
		// Flatten a statement that needs flattening.
		// Note that if s' is obtained by flattening s,
		// then needsFlattening(s') will return false.
		if (needsFlattening(s)) {
		
			Flattener newVisitor = (Flattener) new Flattener(job,ts,nf,n).context(xc);
			// revisit the same statement.
			Node nn = n.visit(newVisitor);
			List/*<Stmt>*/ l = newVisitor.stmtList();
			
			// Add the statement returned by newVisitor -- this will be the current
			// node with its arguments replaced by a local variable references.
			// If the current node ends up being a local variable reference
			// itself, do not add it to the statementsequence since Java does not
			// permit x; as a statement, where x is a local variable. The value 
			// should simply be discarded.
			//Report.report(1, "ExprFlattener nn is |" + nn + "| " + nn.getClass());
			if (! (nn instanceof Local || (nn instanceof Eval && ((Eval) nn).expr() instanceof Local)))
				l.add((Stmt) nn);
			//Report.report(1, "ExprFlattener l is |" + l + "| " );
			X10NodeFactory xnf = (X10NodeFactory) nf;
			result = xnf.StmtSeq(pos, l);
			
		}
		
		return result;
	}
			
		
	
}
