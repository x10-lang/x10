package polyglot.ext.x10.visit;

import java.util.Collections;
import java.util.List;
import java.util.Iterator;

import polyglot.ast.Block;
import polyglot.ast.Formal;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.util.Position;
import polyglot.util.InternalCompilerError;
import polyglot.ext.x10.ast.X10ClockedLoop;
import polyglot.ext.x10.ast.X10Loop;
import polyglot.ext.x10.ast.X10Formal;

/**
 * Visitor that expands implicit declarations in formal parameters.
 */
public class X10ImplicitDeclarationExpander extends ContextVisitor
{
	public X10ImplicitDeclarationExpander(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
	}

	public Node leaveCall(Node old, Node n, NodeVisitor v)
		throws SemanticException
	{
		if (n instanceof MethodDecl)
			return visitMethodDecl((MethodDecl) n);
		if (n instanceof X10Loop)
			return visitLoop((X10Loop) n);
		return n;
	}

	private Node visitMethodDecl(MethodDecl n) {
		List/*<Stmt>*/ stmts = Collections.EMPTY_LIST;
		List/*<Formal>*/ fs = n.formals();
		for (Iterator/*<Formal>*/ i = fs.iterator(); i.hasNext(); ) {
			X10Formal f = (X10Formal) i.next();
			if (!f.hasExplodedVars())
				continue;
			stmts = f.explode(nf, ts, stmts, false);
		}
		if (stmts.isEmpty())
			return n;
		Block b = n.body();
		stmts.addAll(b.statements());
		return n.body(b.statements(stmts));
	}

	private Node visitLoop(X10Loop n) {
		X10Formal f = (X10Formal) n.formal();
		if (!f.hasExplodedVars())
			return n;
		return n.locals(f.explode(nf, ts));
	}
}

