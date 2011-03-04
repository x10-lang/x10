/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.ast;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.CompoundStmt;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.ast.Stmt_c;
import polyglot.main.Report;
import polyglot.types.Context;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.FlowGraph;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import x10.types.X10Context;
import x10.types.X10MethodDef;
import x10.types.X10TypeSystem;

/**
 * Created on Oct 5, 2004
 *
 * @author Christian Grothoff
 * @author Philippe Charles
 * @author vj
 * @author nystrom
 */

public class AtStmt_c extends Stmt_c implements AtStmt {

	public Expr place;
	public Stmt body;

	public AtStmt_c(Position pos, Expr place, Stmt body) {
		super(pos);
		this.place = place;
		this.body = body;
	}

	public AtStmt_c(Position p) {
		super(p);
	}

	/* (non-Javadoc)
	 * @see x10.ast.Future#body()
	 */
	public Stmt body() {
		return body;
	}

	/**
	 * Set the body of the statement.
	 */
	public AtStmt body(Stmt body) {
		AtStmt_c n = (AtStmt_c) copy();
		n.body = body;
		return n;
	}

	/** Get the RemoteActivity's place. */
	public Expr place() {
		return place;
	}

	/** Set the RemoteActivity's place. */
	public RemoteActivityInvocation place(Expr place) {
		if (place != this.place) {
			AtStmt_c n = (AtStmt_c) copy();
			n.place = place;
			return n;
		}

		return this;
	}

	/** Reconstruct the statement. */
	protected AtStmt reconstruct(Expr place, Stmt body) {
		if (place != this.place || body != this.body) {
			AtStmt_c n = (AtStmt_c) copy();
			n.place = place;
			n.body = body;
			return n;
		}
		return this;
	}


	/** Visit the children of the statement. */
	public Node visitChildren(NodeVisitor v) {
		Expr place = (Expr) visitChild(this.place, v);
		Stmt body = (Stmt) visitChild(this.body, v);
		return reconstruct(place, body);
	}

	public Context enterScope(Context c) {
		if (Report.should_report(TOPICS, 5))
			Report.report(5, "enter at scope");
        X10TypeSystem ts = (X10TypeSystem) c.typeSystem();
        X10MethodDef asyncInstance = (X10MethodDef) ts.asyncCodeInstance(c.inStaticContext());
        if (c.currentCode() instanceof X10MethodDef) {
            X10MethodDef outer = (X10MethodDef) c.currentCode();
            List<Ref<? extends Type>> capturedTypes = outer.typeParameters();
            if (!capturedTypes.isEmpty()) {
                asyncInstance = ((X10MethodDef) asyncInstance.copy());
                asyncInstance.setTypeParameters(capturedTypes);
            }
        }
        c = c.pushCode(asyncInstance);
        return c;
	}

	public Context enterChildScope(Node child, Context c) {
		if (child != this.body) {
			c = c.pop();
		}
		return c;
	}

	public Node typeCheck(ContextVisitor tc) throws SemanticException {
		X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
		X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();

		Type placeType = place.type();
		Expr newPlace = place;
		boolean placeIsPlace = ts.isImplicitCastValid(placeType, ts.Place(), tc.context());
		if (! placeIsPlace) {
                    throw new SemanticException(
                        "Place expression of at must be of type \"" +
                        ts.Place() + "\", not \"" + place.type() + "\".",
                        place.position());
                }
		X10Context c = (X10Context) tc.context();
		if (c.inSequentialCode())
			throw new SemanticException("at may not be invoked in sequential code.", position());

		return (Node) place(newPlace);
	}


	public Type childExpectedType(Expr child, AscriptionVisitor av) {
		X10TypeSystem ts = (X10TypeSystem) av.typeSystem();
		if (child == place) {
			return ts.Place();
		}
		return child.type();
	}

	public String toString() {
		return "at (" + place + ") " + body;
	}

	/** Write the statement to an output file. */
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		w.write("at (");
		printBlock(place, w, tr);
		w.write(") ");
		printSubStmt(body, w, tr);
	}

	/**
	 * Return the first (sub)term performed when evaluating this
	 * term.
	 */
	public Term firstChild() {
		if (place != null) {
			return place;
		}

		return body;
	}



	/**
	 * Visit this term in evaluation order.
	 * [IP] Treat this as a conditional to make sure the following
	 *      statements are always reachable.
	 * FIXME: We should really build our own CFG, push a new context,
	 * and disallow uses of "continue", "break", etc. in asyncs.
	 */
	public List acceptCFG(CFGBuilder v, List succs) {

		if (place != null) {
			v.visitCFG(place, FlowGraph.EDGE_KEY_TRUE, body,
					ENTRY, FlowGraph.EDGE_KEY_FALSE, this, EXIT);
		}

		v.visitCFG(body, this, EXIT);

		return succs;
	}

	private static final Collection TOPICS =
		CollectionUtil.list(Report.types, Report.context);
}

