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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.Binary;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Node_c;
import polyglot.ast.TypeCheckFragmentGoal;
import polyglot.frontend.SetResolverGoal;
import polyglot.types.Context;
import polyglot.types.LazyRef;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeCheckPreparer;
import polyglot.visit.TypeChecker;
import x10.errors.Errors;
import polyglot.types.Context;
import polyglot.types.TypeSystem;
import polyglot.types.LazyRef_c;
import x10.types.constraints.CConstraint;
import x10.types.constraints.ConstraintManager;
import x10.types.constraints.TypeConstraint;
import x10.visit.X10TypeChecker;

/** An immutable representation of a dependent type constraint.
 * The corresponding syntax is [T](e){x: T; c}
 * This node is created for dependent X10 types.
 * @author vj Jan 9, 2005
 * 
 */
public class DepParameterExpr_c extends Node_c implements DepParameterExpr {
	
    protected List<Formal> formals;
    
    /** The boolean condition of a parameteric type.
     * Maybe null.
     */
    protected List<Expr> condition;
    
    private Ref<CConstraint> valueConstraint;
    private Ref<TypeConstraint> typeConstraint;
    
    /**
     * @param pos
     */
    public DepParameterExpr_c(Position pos, List<Formal> formals, List<Expr> e) {
	    super(pos);
	    if (formals == null)
		    this.formals = Collections.<Formal>emptyList();
	    else
		    this.formals = TypedList.copyAndCheck(formals, Formal.class, true);
	    assert e != null;
	    this.condition = TypedList.copyAndCheck(e, Expr.class, true);
    }
    public DepParameterExpr_c(Position pos, List<Expr> e) {
	    this(pos, null, e);
    }
    @Override
    public Context enterChildScope(Node child, Context c) {
    	Context xc=c;
    	if (child instanceof Formal) {
    		// pop the dep type
    		c = c.pop();
    	}
    	else if (! formals.isEmpty() && child == condition) {
    		// Add the formals to the scope but outside the dep type scope.
    		Ref<? extends Type> t = xc.depTypeRef();
    		if (t != null) {
    			c = c.pop().pushBlock();
    			// Push formals so they're in scope in the types of the other formals.
    			for (Formal f : formals) {
    				f.addDecls(c);
    			}
    			c = c.pushDepType(t);
    		}
    	}

        return super.enterChildScope(child, c);
    }

    public Ref<CConstraint> valueConstraint() {
		return valueConstraint;
    }

    
    public Ref<TypeConstraint> typeConstraint() {
        return typeConstraint;
    }
    
    public DepParameterExpr typeConstraint(Ref<TypeConstraint> c) {
        DepParameterExpr_c n = (DepParameterExpr_c) copy();
        n.typeConstraint = c;
        return n;
    }
    
    public List<Formal> formals() {
    	return this.formals;
    }
    public DepParameterExpr formals( List<Formal> formals ) {
	    DepParameterExpr_c n = (DepParameterExpr_c) copy();
	    n.formals = TypedList.copyAndCheck(formals, Formal.class, true);
	    return n;
    }
    
    public List<Expr> condition() {
	    return this.condition;
    }
    public DepParameterExpr condition( List<Expr> condition) {
        DepParameterExpr_c n = (DepParameterExpr_c) copy();
        n.condition = condition;
        return n;
    }
       
    protected DepParameterExpr reconstruct( List<Formal> formals, List<Expr> condition ) {
        if (formals == this.formals && condition == this.condition)
			return this;
        DepParameterExpr_c n = (DepParameterExpr_c) copy();
        n.condition = condition;
        n.formals = formals;
        return n;
    }

    @Override
    public Node visitChildren(NodeVisitor v) {
        List<Formal> formals = visitList(this.formals, v);
        List<Expr> condition = visitList(this.condition, v);
        return reconstruct(formals, condition);
    }
    
    public Node buildTypes(TypeBuilder tb) {
    	DepParameterExpr_c n = (DepParameterExpr_c) copy();
    	n.valueConstraint = Types.<CConstraint>lazyRef(ConstraintManager.getConstraintSystem().makeCConstraint(), new SetResolverGoal(tb.job()).intern(tb.job().extensionInfo().scheduler()));
    	n.typeConstraint = Types.<TypeConstraint>lazyRef(new TypeConstraint(), new SetResolverGoal(tb.job()).intern(tb.job().extensionInfo().scheduler()));
    	return n;
      }

    // This is a challenge because DepParam is used both in methods and closures
    // (for closures we shouldn't create a resolver, but for methods we should but without freezing).
    // C:\cygwin\home\Yoav\intellij\sourceforge\x10.tests\examples\Issues\XTENLANG_2330.x10
    // C:\cygwin\home\Yoav\intellij\sourceforge\x10.tests\examples\Constructs\Place\At_MustFailCompile.x10
    // C:\cygwin\home\Yoav\intellij\sourceforge\x10.tests\examples\Misc\x10\frontend\tests\FrontEndTests_MustFailCompile.x10
      public void setResolver(Node parent, final TypeCheckPreparer v) {
    	  TypeChecker tc = new X10TypeChecker(v.job(), v.typeSystem(), v.nodeFactory(), v.getMemo());
    	  tc = (TypeChecker) tc.context(v.context().freeze());

    	  {
    	      LazyRef<CConstraint> xr = (LazyRef<CConstraint>) valueConstraint;
    	      assert xr != null : "setResolver pass run before buildTypes for " + this;
    	      xr.setResolver(new TypeCheckFragmentGoal<CConstraint>(parent, this, tc, xr, false));
    	  }
    	  {
    	      LazyRef<TypeConstraint> xr = (LazyRef<TypeConstraint>) typeConstraint;
    	      assert xr != null : "setResolver pass run before buildTypes for " + this;
    	      xr.setResolver(new TypeCheckFragmentGoal<TypeConstraint>(parent, this, tc, xr, false));
    	  }
      }

    @Override
    public Node disambiguate(ContextVisitor ar) {
    	DepParameterExpr_c n = this;
    	
    	if (((Context) ar.context()).inAnnotation() && condition == null) {
    		return n.condition(Collections.<Expr>emptyList());
    	}

    	return n;
    }
    
    /** Type check the statement. 
     */
    @Override
    public Node typeCheck(ContextVisitor tc) {
        TypeSystem ts = (TypeSystem) tc.typeSystem();
        //Report.report(1, "DepParameterExpr: Typechecking " + this + this.getClass() + " " + condition);
        
        if (condition == null) {
        	return this;
//        	throw new SemanticError("The condition of a dependent type clause must be non-empty.", 
//        			position());
        }
        
        Expr values = null;
        Expr types = null;

        LinkedList<Expr> cond = new LinkedList<Expr>();
        cond.addAll(condition);

        while (! cond.isEmpty()) {
            Expr e = cond.removeFirst();
            Type t = e.type();

            if (! t.isBoolean())
                Errors.issue(tc.job(),
                        new Errors.TypeConstraintMustBeBoolean(e, t, position()));

            if (e instanceof Binary) {
                Binary b = (Binary) e;
                if (b.operator() == Binary.COND_AND || b.operator() == Binary.BIT_AND) {
                    cond.addFirst(b.left());
                    cond.addFirst(b.right());
                    continue;
                }
            }

            NodeFactory nf = tc.nodeFactory();
            
            if (Types.isTypeConstraintExpression(e)) {
                if (types == null)
                    types = e;
                else
                    types = nf.Binary(new Position(types.position(), e.position()), types, Binary.COND_AND, e).type(ts.Boolean());
            }
            else {
                if (values == null)
                    values = e;
                else
                    values = nf.Binary(new Position(values.position(), e.position()), values, Binary.COND_AND, e).type(ts.Boolean());
            }
        }
        
        try {
            CConstraint xvc = ts.xtypeTranslator().constraint(formals, values, (Context) tc.context());
            ((LazyRef<CConstraint>) valueConstraint).update(xvc);
        } catch (SemanticException e) {
        	Errors.issue(tc.job(),  e);
        }

        try {
            TypeConstraint xtc = ts.xtypeTranslator().typeConstraint(formals, types, (Context) tc.context());
            ((LazyRef<TypeConstraint>) typeConstraint).update(xtc);
        } catch (SemanticException e) {
        	Errors.issue(tc.job(),  e);
        }
        
        return this;
    }
    
    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append("{");
	String sep = "";
	for (Formal f : formals) {
		sb.append(sep);
		sep = ", ";
		sb.append(f.toString());
	}
	if (! sep.equals(""))
		sep = "; ";
        
        if (condition != null) {
        	sb.append(sep);
        	sb.append(condition.toString());
        }
        sb.append("}");
        return sb.toString();
    }
    /** Write the statement to an output file. */
    @Override
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	    w.write("{");
	    String sep = "";
	    for (Formal f : formals) {
		    w.write(sep);
		    sep = ", ";
		    printBlock( f, w, tr);
	    }
	    if (! sep.equals(""))
		    sep = "; ";
	    
	    for (Expr e: condition) {
	        w.write(sep);
	        printBlock( e, w, tr);
	        sep = ", ";
	    }
	    w.write("}");
    }
}
