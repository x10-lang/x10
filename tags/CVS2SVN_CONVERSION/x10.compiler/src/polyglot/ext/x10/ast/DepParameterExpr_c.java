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
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ast.Expr_c;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10NamedType;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.main.Report;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;

/** An immutable representation of the parameter list of a parameteric type.
 * The corresponding syntax is ( a1,..., ak : e ).
 * This node is created for parameteric X10 types.
 * @author vj Jan 9, 2005
 * 
 */
public class DepParameterExpr_c extends Expr_c implements DepParameterExpr {
	
    /** The argument list of a parametric type. Maybe null.
     * vj: This is currently being used only in specifying the arguments for an X10 array type.
     * 
     */
    protected List/*Expr*/ args;
    
    /** The boolean condition of a parameteric type.
     * Maybe null.
     */
    protected Expr condition;
    
    /**
     * @param pos
     */
    public DepParameterExpr_c(Position pos, List l) {
        this(pos, l, null);
    }
    public DepParameterExpr_c(Position pos, Expr cond) {
        this(pos, Collections.EMPTY_LIST, cond);
    }
    public DepParameterExpr_c(Position pos, List l, Expr cond) {
        super(pos);
        this.args =  TypedList.copyAndCheck(l, Expr.class, true);
        this.condition = cond;
    }
    
    public Expr condition() {
        return this.condition;
    }
    public List args() {
        return this.args;
    }
    public DepParameterExpr condition( Expr condition) {
        DepParameterExpr_c n = (DepParameterExpr_c) copy();
        n.condition = condition;
        return n;
    }
    public DepParameterExpr args( List args ) {
        DepParameterExpr_c n = (DepParameterExpr_c) copy();
        n.args = args;
        return n;
    }
//    public Context enterScope(Context c) {
//        X10Context xc = (X10Context) c;
//        if (xc.inAnnotation()) {
//        	X10NamedType dt = xc.currentDepType();
//        	xc = (X10Context) xc.pushDepType(null);
//        	xc = (X10Context) xc.pushCode(c.typeSystem().initializerInstance(position(), xc.currentClass(), Flags.NONE));
//        	xc = (X10Context) xc.pushDepType(dt);
//        	xc.clearAnnotation();
//        }
//        return super.enterScope(xc);
//    }
       
    public DepParameterExpr reconstruct( List args, Expr condition ) {
        if (args == this.args && condition == this.condition) return this;
        DepParameterExpr_c n = (DepParameterExpr_c) copy();
        n.condition = condition;
        n.args = args;
        return n;
    }
    @Override
    public Node visitChildren(NodeVisitor v) {
        //Report.report(1, "DepParameterExpr_c:  " + v + " visits " + this);
        //if (v.toString().startsWith("AmbiguityRemover")) {
           // new Exception().printStackTrace();
        //}
        List arguments = visitList(this.args, v);
        Expr condition = (Expr) visitChild(this.condition, v);
        return reconstruct(arguments, condition);
    }
    
//    public boolean isDisambiguated() {
//    	boolean val = (condition == null) || condition.isDisambiguated();
//    	return val;
//    }
    
    @Override
    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
    	DepParameterExpr_c n = (DepParameterExpr_c) super.disambiguate(ar);
    	
    	if (((X10Context) ar.context()).inAnnotation() && condition == null) {
    		return n.condition(ar.nodeFactory().BooleanLit(position(), true));
    	}
    	
    	return n;
    }
    
    /** Type check the statement. 
     */
    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
      //Report.report(1, "DepParameterExpr: Typechecking " + this + this.getClass() + " " + condition);
        
        if (condition == null) {
        	throw new SemanticError("The condition of a dependent type clause must be non-empty.", 
        			position());
        }
        Type t = condition.type();
        
        if (! t.isBoolean())
        	throw new SemanticError("The type of the dependent clause, "+ condition 
        			+ ", must be boolean and not " + t + ".", position());
        return type(t);
    }
    /* (non-Javadoc)
     * @see polyglot.ast.Term#entry()
     */
    public Term firstChild() {
        return (args.isEmpty())
                ? condition
                : listChild( args, null);
    }
    
    /* (non-Javadoc)
     * @see polyglot.ast.Term#acceptCFG(polyglot.visit.CFGBuilder, java.util.List)
     */
    @Override
    public List acceptCFG(CFGBuilder v, List succs) {
        if (args == null) {
            if (condition != null) 
                v.visitCFG(condition, this, EXIT);
        } else {
            if (condition != null) {
                v.visitCFGList( args, condition, ENTRY);
                v.visitCFG(condition, this, EXIT);
            } else {
                v.visitCFGList( args, this, EXIT);
            }
        }
        
        
        return succs;
    }
    @Override
    public String toString() {
        String s = "(";
        int count = 0;
        
        for (Iterator i = args.iterator(); i.hasNext(); ) {
            if (count++ > 2) {
                s += "...";
                break;
            }
            Expr n = (Expr) i.next();
            s += n.toString();
            
            if (i.hasNext()) {
                s += ", ";
            }
        }
        
        if (condition != null) {
            s += ":" + condition;
        } 
        return s + ")";
    }
    /** Write the statement to an output file. */
    @Override
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        if (args.isEmpty() && condition==null) return;
        if (args.isEmpty()) {
            w.write("(:");
            printBlock( condition, w, tr);
            w.write(")");
            return;
        }
        
        w.write("(");
        w.begin(0);
        
        for (Iterator it = args.iterator(); it.hasNext();) {
            Expr e = (Expr) it.next();
            print(e, w, tr);
            
            if (it.hasNext()) {
                w.write(",");
                w.allowBreak(0, " ");
            }	
        }
        w.end();
        if (condition!=null)
            print( condition, w, tr);
        w.write(")");
    }

}
