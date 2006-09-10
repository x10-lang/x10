/*
 * Created by vj on Jan 9, 2005
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ext.jl.ast.Call_c;
import polyglot.ext.jl.ast.Expr_c;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.main.Report;
import polyglot.types.Flags;
import polyglot.types.MethodInstance;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

/** An immutable representation of the parameter list of a parameteric type.
 * The corresponding syntax is ( a1,..., ak : e ).
 * This node is created for parameteric X10 types.
 * @author vj Jan 9, 2005
 * 
 */
public class DepParameterExpr_c extends Expr_c implements DepParameterExpr {
    /** The argument list of a parametric type. Maybe null.
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
        this.args =  TypedList.copyAndCheck(l, Expr.class, false);
        this.condition = cond;
        //Report.report(1, "DepParameterExpr_c: created " + l + " cond=" + cond);
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
    public DepParameterExpr reconstruct( List args, Expr condition ) {
        if (args == this.args && condition == this.condition) return this;
        DepParameterExpr_c n = (DepParameterExpr_c) copy();
        n.condition = condition;
        n.args = args;
        return n;
    }
    public Node visitChildren(NodeVisitor v) {
        //Report.report(1, "DepParameterExpr_c:  " + v + " visits " + this);
        //if (v.toString().startsWith("AmbiguityRemover")) {
           // new Exception().printStackTrace();
        //}
        List arguments = visitList(this.args, v);
        Expr condition = (Expr) visitChild(this.condition, v);
        return reconstruct(arguments, condition);
    }
    
  
    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
        //Report.report(1, "DepParameterExpr_c: Disambiguate " + this);
     // new Exception().printStackTrace();
        List newArgs = new ArrayList();
        
        for(Iterator i = args.iterator(); i.hasNext();) {
            Expr e = (Expr) i.next();
            Node n = e.disambiguate(ar);
            
            if (n instanceof Expr) {
                newArgs.add(n);
                continue;
              }
          throw new SemanticException("Could not find field or local " +
                                      "variable \"" + e + "\".", position());
        }
        
        if (condition == null)
            return args(newArgs);
        
        Node newCond = condition.disambiguate(ar);
        
        if (newCond instanceof Expr) {
            return reconstruct(newArgs, (Expr) newCond);
        }
        throw new SemanticException("Could not disambiguate " 
                + condition + "\".", position());
        
    }
    /** Type check the statement. 
     * TODO: Implement type checking.
     * The boolean expression must only access parameter fields, and must only invoke specified methods.
     */
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
        Report.report(1, "DepParameterExpr: Typechecking " + this);
        return this;
    }
    /* (non-Javadoc)
     * @see polyglot.ast.Term#entry()
     */
    public Term entry() {
        return (args.isEmpty())
        ? condition.entry()
                : listEntry( args, this);
    }
    
    /* (non-Javadoc)
     * @see polyglot.ast.Term#acceptCFG(polyglot.visit.CFGBuilder, java.util.List)
     */
    public List acceptCFG(CFGBuilder v, List succs) {
        if (args == null) {
            if (condition != null) 
                v.visitCFG(condition, this);
        } else {
            if (condition != null) {
                v.visitCFGList( args, condition.entry());
                v.visitCFG(condition, this);
            } else {
                v.visitCFGList( args, this);
            }
        }
        
        return succs;
    }
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
