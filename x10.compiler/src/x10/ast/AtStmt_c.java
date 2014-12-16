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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.Stmt_c;
import polyglot.ast.Term;
import polyglot.types.ClassType;
import polyglot.types.CodeDef;
import polyglot.types.CodeInstance;
import polyglot.types.Context;
import polyglot.types.Def;
import polyglot.types.FieldDef;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import x10.util.CollectionFactory;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.FlowGraph;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.PruningVisitor;
import polyglot.visit.TypeBuilder;
import x10.constraint.XConstraint;
import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.errors.Errors;
import x10.extension.X10Del;
import x10.extension.X10Del_c;
import x10.extension.X10Ext;
import x10.types.AtDef;
import x10.types.ClosureDef;
import x10.types.ParameterType;
import x10.types.ThisDef;
import x10.types.X10ClassDef;
import x10.types.X10MemberDef;
import x10.types.X10MethodDef;
import x10.types.X10ProcedureDef;
import x10.types.checker.Converter;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.CConstraint;
import x10.types.constraints.ConstraintManager;
import x10.types.constraints.XConstrainedTerm;

/**
 * Created on Oct 5, 2004
 *
 * @author Christian Grothoff
 * @author Philippe Charles
 * @author vj
 * @author nystrom
 */

public class AtStmt_c extends Stmt_c implements AtStmt {

	protected Expr place;
	protected Stmt body;
	protected AtDef atDef;
	protected List<Node> captures;

	public AtStmt_c(Position pos, Expr place, Stmt body) {
	    this(pos, place, null, body);
	}
	public AtStmt_c(Position pos, Expr place, List<Node> captures, Stmt body) {
		super(pos);
        assert body!=null;
		this.place = place;
		this.body = body;
		this.captures = captures;
	}

	public List<Node> captures() {
	    return captures;
	}

	public AtStmt_c captures(List<Node> captures) {
	    AtStmt_c n = (AtStmt_c) copy();
	    n.captures = captures;
	    return n;
	}

	/**
	 * Get the body of the statement.
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
	public AtStmt place(Expr place) {
		if (place != this.place) {
			AtStmt_c n = (AtStmt_c) copy();
			n.place = place;
			return n;
		}

		return this;
	}

	public AtDef atDef() {
	    return this.atDef;
	}

	public AtStmt atDef(AtDef ci) {
	    if (ci == this.atDef) return this;
	    AtStmt_c n = (AtStmt_c) copy();
	    n.atDef = ci;
	    return n;
	}

	/** Reconstruct the statement. */
	protected AtStmt_c reconstruct(Expr place, Stmt body) {
		if (place != this.place || body != this.body) {
			AtStmt_c n = (AtStmt_c) copy();
			n.place = place;
			n.body = body;
			return n;
		}
		return this;
	}

    public boolean isFinishPlace() {
        boolean isFinishPlace = false;
        AtDef def = atDef();
        if (null != def.finishPlaceTerm()) {
        	XConstraint constraint = ConstraintManager.getConstraintSystem().makeConstraint();;
            constraint.addBinding(def.finishPlaceTerm().term(),def.placeTerm().term());
            if (def.placeTerm().constraint().entails(constraint)) {
                isFinishPlace = true;
            }    
        }
        return isFinishPlace;
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

        AtDef mi = (AtDef) createDummyAsync(position(), ts, ct.asType(), code, code.staticContext(), false);

        AtStmt_c n = (AtStmt_c) X10Del_c.visitAnnotations(this, tb);

        List<AnnotationNode> as = ((X10Del) n.del()).annotations();
        if (as != null) {
            List<Ref<? extends Type>> ats = new ArrayList<Ref<? extends Type>>(as.size());
            for (AnnotationNode an : as) {
                ats.add(an.annotationType().typeRef());
            }
            mi.setDefAnnotations(ats);
        }

        // Unlike methods and constructors, do not create new goals for resolving the signature and body separately;
        // since closures don't have names, we'll never have to resolve the signature.  Just push the code context.
        TypeBuilder tb2 = tb.pushCode(mi);

        n = (AtStmt_c) n.del().visitChildren(tb2);

        if (code instanceof X10MemberDef) {
            assert mi.thisDef() == ((X10MemberDef) code).thisDef();
        }

        return n.atDef(mi);
    }

    @Override
    public Node typeCheckOverride(Node parent, ContextVisitor tc) {
    	TypeSystem ts = (TypeSystem) tc.typeSystem();
    	NodeVisitor v = tc.enter(parent, this);
    	
    	if (v instanceof PruningVisitor) {
    		return this;
    	}
    	ContextVisitor childtc = (ContextVisitor) v;
    	Expr place = (Expr) visitChild(this.place, childtc);
    	place = Converter.attemptCoercion(tc, place, ts.Place());
    	if (place == null) {
    	    Errors.issue(tc.job(), 
    	            new Errors.AtArgMustBePlace(this.place, ts.Place(), this.place.position()));
    	    place = tc.nodeFactory().Here(this.place.position()).type(ts.Place());
    	}

    	Context c = tc.context();
    	AtDef def = this.atDef();
    	if (def.placeTerm() == null) {
            XConstrainedTerm placeTerm;
            XConstrainedTerm finishPlaceTerm = c.currentFinishPlaceTerm();
            CConstraint d = ConstraintManager.getConstraintSystem().makeCConstraint();
            XTerm term = PlaceChecker.makePlace();
            try {
                placeTerm = XConstrainedTerm.instantiate(d, term);
            } catch (XFailure z) {
                throw new InternalCompilerError("Cannot construct placeTerm from term and constraint.");
            }
            try {
                XConstrainedTerm realPlaceTerm = PlaceChecker.computePlaceTerm(place, c, ts);
                d.addBinding(placeTerm, realPlaceTerm);
            } catch (SemanticException e) { }
            def.setPlaceTerm(placeTerm);
            def.setFinishPlaceTerm(finishPlaceTerm);
    	}

    	// now that placeTerm is computed for this node, install it in the context
    	// and continue visiting children

    	Context oldC = c;
        c = super.enterChildScope(body, childtc.context());
        XConstrainedTerm pt = def.placeTerm();
        if (pt != null) {
        	if (c == oldC)
        		c = c.pushBlock();
            c.setPlace(pt);
        }
        Stmt body = (Stmt) visitChild(this.body, childtc.context(c));
        AtStmt_c n = this.reconstruct(place, body);

		List<AnnotationNode> oldAnnotations = ((X10Ext)n.ext()).annotations();
		if (oldAnnotations != null && !oldAnnotations.isEmpty()) {
			List<AnnotationNode> newAnnotations = visitList(oldAnnotations, v);
			if (! CollectionUtil.allEqual(oldAnnotations, newAnnotations)) {
				n = (AtStmt_c) ((X10Del) n.del()).annotations(newAnnotations);
			}
		}

        return tc.leave(parent, this, n, childtc);
}

    @Override
    public Node typeCheck(ContextVisitor tc) {
        Context c = tc.context();
        AtDef def = this.atDef();
        //if (!def.capturedEnvironment().isEmpty()) {
        //    System.out.println(this.position() + ": " + this + " captures "+def.capturedEnvironment());
        //}
        Closure_c.propagateCapturedEnvironment(c, def);

        return this;
    }

    @Override
    public Context enterChildScope(Node child, Context c) {
        if (child != this.body) return c.pop();
        Context oldC = c;
        c = super.enterChildScope(child, c);
        if (c == oldC)
            c = c.pushBlock();
        c.setPlace(atDef.placeTerm());
        return c;
    }

	/** Visit the children of the statement. */
	public Node visitChildren(NodeVisitor v) {
		Expr place = (Expr) visitChild(this.place, v);
		Stmt body = (Stmt) visitChild(this.body, v);
		return reconstruct(place, body);
	}

	public static X10MethodDef createDummyAsync(Position pos, TypeSystem ts, ClassType cc, CodeDef cd, boolean isStaticContext, boolean isAsync) {        
	    ThisDef thisDef = null;
	    List<ParameterType> capturedTypes = Collections.<ParameterType>emptyList();
	    CodeInstance<?> ci = cd.asInstance();
	    if (cd instanceof X10ProcedureDef) {
	        X10ProcedureDef outer = (X10ProcedureDef) cd;
	        thisDef = outer.thisDef();
	        capturedTypes = outer.typeParameters();
	    }
	    X10MethodDef asyncInstance =
	        isAsync ?
	                ts.asyncCodeInstance(pos, thisDef, capturedTypes, Types.ref(ci), Types.ref(cc), isStaticContext)
	                :
	                ts.atCodeInstance(pos, thisDef, capturedTypes, Types.ref(ci), Types.ref(cc), isStaticContext);
	    return asyncInstance; 
	}

	@Override
	public Context enterScope(Context c) {
	    c = c.pushBlock();
	    c = c.pushAt(atDef);
	    c.x10Kind = Context.X10Kind.At;
	    return c;
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
		return place;
	}

	/**
	 * Visit this term in evaluation order.
	 */
	public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        v.visitCFG(place, body, ENTRY);
		v.visitCFG(body, this, EXIT);

		return succs;
	}

}

