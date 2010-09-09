/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Jan 9, 2005
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.Collections;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Node;
import polyglot.ast.Node_c;
import polyglot.ast.TypeCheckFragmentGoal;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10TypeSystem;
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
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;

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
    protected Expr condition;
    
    private Ref<XConstraint> xconstraint;
    
    /**
     * @param pos
     */
    public DepParameterExpr_c(Position pos, List<Formal> formals, Expr cond) {
	    super(pos);
	    if (formals == null)
		    this.formals = Collections.EMPTY_LIST;
	    else
		    this.formals = TypedList.copyAndCheck(formals, Formal.class, true);
	    this.condition = cond;
    }
    public DepParameterExpr_c(Position pos, Expr cond) {
	    this(pos, null, cond);
    }
    @Override
    public Context enterChildScope(Node child, Context c) {
    	X10Context xc = (X10Context) c;
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
    			c = ((X10Context) c).pushDepType(t);
    		}
    	}

        return super.enterChildScope(child, c);
    }

    public Ref<XConstraint> xconstraint() {
		return xconstraint;
    }
    
    public List<Formal> formals() {
    	return this.formals;
    }
    public DepParameterExpr formals( List<Formal> formals ) {
	    DepParameterExpr_c n = (DepParameterExpr_c) copy();
	    n.formals = TypedList.copyAndCheck(formals, Formal.class, true);
	    return n;
    }
    
    public Expr condition() {
	    return this.condition;
    }
    public DepParameterExpr condition( Expr condition) {
        DepParameterExpr_c n = (DepParameterExpr_c) copy();
        n.condition = condition;
        return n;
    }
       
    protected DepParameterExpr reconstruct( List<Formal> formals, Expr condition ) {
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
        Expr condition = (Expr) visitChild(this.condition, v);
        return reconstruct(formals, condition);
    }
    
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
    	DepParameterExpr_c n = (DepParameterExpr_c) copy();
    	n.xconstraint = Types.<XConstraint>lazyRef(new XConstraint_c(), new SetResolverGoal(tb.job()));
    	return n;
      }

      public void setResolver(Node parent, final TypeCheckPreparer v) {
    	  TypeChecker tc = new TypeChecker(v.job(), v.typeSystem(), v.nodeFactory(), v.getMemo());
    	  tc = (TypeChecker) tc.context(v.context().freeze());

    	  LazyRef<XConstraint> xr = (LazyRef<XConstraint>) xconstraint;
    	  xr.setResolver(new TypeCheckFragmentGoal(parent, this, tc, xr, false));
      }
    
    @Override
    public Node disambiguate(ContextVisitor ar) throws SemanticException {
    	DepParameterExpr_c n = (DepParameterExpr_c) super.disambiguate(ar);
    	
    	if (((X10Context) ar.context()).inAnnotation() && condition == null) {
    		Expr lit = ar.nodeFactory().BooleanLit(position(), true);
    		lit = (Expr) this.visitChild(lit, ar);
    		return n.condition(lit);
    	}

    	return n;
    }
    
    /** Type check the statement. 
     */
    @Override
    public Node typeCheck(ContextVisitor tc) throws SemanticException {
        X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
      //Report.report(1, "DepParameterExpr: Typechecking " + this + this.getClass() + " " + condition);
        
        if (condition == null) {
        	return this;
//        	throw new SemanticError("The condition of a dependent type clause must be non-empty.", 
//        			position());
        }
        
        Type t = condition.type();
        
        if (! t.isBoolean())
        	throw new SemanticError("The type of the dependent clause, "+ condition 
        			+ ", must be boolean and not " + t + ".", position());

        XConstraint xc = ts.xtypeTranslator().constraint(formals, condition);
        ((LazyRef<XConstraint>) xconstraint).update(xc);
        
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
	    
	    if (condition != null) {
		    w.write(sep);
		    printBlock( condition, w, tr);
	    }
	    w.write("}");
    }
}
