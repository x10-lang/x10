/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Dec 9, 2004
 */
package polyglot.ext.x10.ast;

import java.util.Iterator;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Id_c;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.Field_c;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.Context;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.PrettyPrinter;

/**
 * An immutable representation of the X10 statement: ateach (i : D) S
 * @author vj Dec 9, 2004
 * @author Christian Grothoff
 */
public class AtEach_c extends X10ClockedLoop_c implements AtEach, Clocked {

	/**
	 * @param pos
	 */
	public AtEach_c(Position pos) {
		super(pos);
	}

	/**
	 * @param pos
	 * @param formal
	 * @param domain
	 * @param clocks
	 * @param body
	 */
	public AtEach_c(Position pos, Formal formal, Expr domain, List clocks, Stmt body) {
		super(pos, formal, domain, clocks, body);
	}

	public Expr getDomain(Expr d) {
		return new Field_c(position(), d, new Id_c(position(), "distribution"));
	}

	/**
	 * The evaluation of place and list of clocks is not in the scope of the async.
	 */
	public Context enterScope(Context c) {
		X10TypeSystem ts = (X10TypeSystem) c.typeSystem();
		c = c.pushCode(ts.asyncCodeInstance(c.inStaticContext()));
		return c;
	}

	/**
	 * The evaluation of place and list of clocks is not in the scope of the async.
	 */
	public Context enterChildScope(Node child, Context c) {
		if (child != this.body) {
			c = c.pop();
		}
		return c;
	}

	public String toString() {
		return "ateach (" + formal + ":" + domain + ")" + (clocks != null ? " clocked(" + clocks + ")" : "") + body;
	}

	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		w.write("ateach(");
		printBlock(formal, w, tr);
		w.write(" : ");
		printBlock(domain, w, tr);
		w.write(") ");
		if (clocks != null) {
			w.write("clocked(");
			for (Iterator c = clocks.iterator(); c.hasNext(); ) {
				print((Expr)c.next(), w, tr);
				if (c.hasNext()) w.write(", ");
			}
			w.write(")");
		}
		printSubStmt(body, w, tr);
	}
}
