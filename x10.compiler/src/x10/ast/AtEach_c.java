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

package x10.ast;

import java.util.Iterator;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Id_c;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.Field_c;
import polyglot.types.CodeDef;
import polyglot.types.Context;
import polyglot.types.Def;
import polyglot.types.FieldDef;
import polyglot.types.Flags;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeBuilder;
import x10.ast.X10Loop.LoopKind;

import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.types.constraints.ConstraintManager;
import x10.errors.Errors;
import polyglot.types.Context;
import x10.types.AtDef;
import x10.types.X10ClassDef;
import x10.types.X10MemberDef;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CConstraint;
import x10.types.constraints.XConstrainedTerm;

/**
 * An immutable representation of the X10 statement: [clocked] ateach (i : D) S
 * @author vj Dec 9, 2004
 * @author Christian Grothoff
 */
public class AtEach_c extends X10ClockedLoop_c implements AtEach, Clocked {

    protected AtDef atDef;

	/**
	 * @param pos
	 */
	public AtEach_c(Position pos) {
		super(pos);
		loopKind=LoopKind.ATEACH;
	}

	/**
	 * @param pos
	 * @param formal
	 * @param domain
	 * @param clocks
	 * @param body
	 */
	public AtEach_c(Position pos, Formal formal, Expr domain, List<Expr> clocks, Stmt body) {
		super(pos, formal, domain, clocks, body);
		loopKind=LoopKind.ATEACH;
	}
	public AtEach_c(Position pos, Formal formal, Expr domain, Stmt body) {
		super(pos, formal, domain, body);
		loopKind=LoopKind.ATEACH;
	}

	public AtDef atDef() {
	    return this.atDef;
	}

	public AtEach atDef(AtDef ci) {
	    if (ci == this.atDef) return this;
	    AtEach_c n = (AtEach_c) copy();
	    n.atDef = ci;
	    return n;
	}

	@Override
	public Node buildTypesOverride(TypeBuilder tb) {
	    TypeSystem ts = (TypeSystem) tb.typeSystem();

	    X10ClassDef ct = (X10ClassDef) tb.currentClass();
	    assert ct != null;

	    Def def = tb.def();

	    if (def instanceof FieldDef) {
	        // FIXME: is this possible?
	        FieldDef fd = (FieldDef) def;
	        def = fd.initializer();
	    }

	    if (!(def instanceof CodeDef)) {
	        Errors.issue(tb.job(),
	                new Errors.CannotOccurOutsideCodeBody(Errors.CannotOccurOutsideCodeBody.Element.At, position()));
	        // Fake it
	        def = ts.initializerDef(position(), Types.ref(ct.asType()), Flags.STATIC);
	    }

	    CodeDef code = (CodeDef) def;

	    AtDef mi = (AtDef) AtStmt_c.createDummyAsync(position(), ts, ct.asType(), code, code.staticContext(), false);

	    // Unlike methods and constructors, do not create new goals for resolving the signature and body separately;
	    // since closures don't have names, we'll never have to resolve the signature.  Just push the code context.
	    TypeBuilder tb2 = tb.pushCode(mi);

	    AtEach_c n = (AtEach_c) this.del().visitChildren(tb2);
	    n = (AtEach_c) n.del().buildTypes(tb2);

	    if (code instanceof X10MemberDef) {
	        assert mi.thisDef() == ((X10MemberDef) code).thisDef();
	    }

	    return n.atDef(mi);
	}

	@Override
	public Context enterChildScope(Node child, Context c) {
		Context oldC = c;
		if (child == this.body) {
		    c = c.pushAt(atDef);
		    c.x10Kind = Context.X10Kind.At; // this is an at, not an async
		}

		if (child == this.body) {
		    if (c == oldC)
		        c = c.pushBlock();
		    c.setPlace(atDef.placeTerm());
		}

		return c;
	}

	@Override
	public Node typeCheckOverride(Node parent, ContextVisitor tc) {
	    Context c = tc.context();
	    AtDef def = this.atDef();
	    if (def.placeTerm() == null) {
	        // FIXME: this creates a new place term; ideally, it should be the place associated with each
	        // point in the ateach distribution
	        XConstrainedTerm placeTerm = XConstrainedTerm.make(PlaceChecker.makePlace());
	        XConstrainedTerm finishPlaceTerm = c.currentFinishPlaceTerm();
	        def.setPlaceTerm(placeTerm);
	        def.setFinishPlaceTerm(finishPlaceTerm);
	    }
	    return super.typeCheckOverride(parent, tc);
	}

	@Override
	public Node typeCheck(ContextVisitor tc) {
	    AtEach_c n = (AtEach_c) super.typeCheck(tc);

	    Context c = tc.context();
	    AtDef def = n.atDef;
	    //if (!def.capturedEnvironment().isEmpty()) {
	    //    System.out.println(this.position() + ": " + this + " captures "+def.capturedEnvironment());
	    //}
	    Closure_c.propagateCapturedEnvironment(c, def);

	    return n;
	}

	public String toString() {
		return "ateach (" + formal + " in " + domain + ")" + (clocks != null ? " clocked(" + clocks + ")" : "") + body;
	}

	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		w.write("ateach(");
		printBlock(formal, w, tr);
		w.write(" in ");
		printBlock(domain, w, tr);
		w.write(") ");
		if (clocks != null) {
			w.write("clocked(");
			for (Iterator<Expr> c = clocks.iterator(); c.hasNext(); ) {
				print(c.next(), w, tr);
				if (c.hasNext()) w.write(", ");
			}
			w.write(")");
		}
		printSubStmt(body, w, tr);
	}
}
