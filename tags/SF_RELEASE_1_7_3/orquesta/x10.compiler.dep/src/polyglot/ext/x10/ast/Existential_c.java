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
import polyglot.ast.Expr_c;
import polyglot.ast.Formal;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.main.Report;
import polyglot.types.Context;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.TypedList;
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
public class Existential_c extends Expr_c implements Existential {
	
    /** The EQVs 
     */
    protected List<Formal> formals;
    
    /** The boolean condition of a parameteric type.
     * Maybe null.
     */
    protected Expr condition;
    
    /**
     * @param pos
     */
    public Existential_c(Position pos, List l) {
        this(pos, l, null);
    }
    public Existential_c(Position pos, Expr cond) {
        this(pos, Collections.EMPTY_LIST, cond);
    }
    public Existential_c(Position pos, List l, Expr cond) {
        super(pos);
        this.formals =  TypedList.copyAndCheck(l, Formal.class, true);
        this.condition = cond;
    }
    
    @Override
    public Context enterChildScope(Node child, Context c) {
    	assert ((X10Context) c).inDepType();
//    	c = c.pushBlock();

    	if (! formals.isEmpty() && child == condition) {
            // Push formals so they're in scope in the types of the other formals.
            for (Formal f : formals) {
                f.addDecls(c);
            }
        }

        return super.enterChildScope(child, c);
    }

    public Expr condition() {
        return this.condition;
    }
    public List<Formal> formals() {
        return this.formals;
    }
    public Existential condition( Expr condition) {
        Existential_c n = (Existential_c) copy();
        n.condition = condition;
        return n;
    }
    public Existential formals( List formals ) {
        Existential_c n = (Existential_c) copy();
        n.formals = formals;
        return n;
    }
       
    public Existential reconstruct( List<Formal> formals, Expr condition ) {
        if (formals == this.formals && condition == this.condition) return this;
        Existential_c n = (Existential_c) copy();
        n.condition = condition;
        n.formals = formals;
        return n;
    }

    @Override
    public Node visitChildren(NodeVisitor v) {
        List<Formal> arguments = visitList(this.formals, v);
        Expr condition = (Expr) visitChild(this.condition, v);
        return reconstruct(arguments, condition);
    }
    
    /** Type check the statement. 
     */
    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
      //Report.report(1, "Existential: Typechecking " + this + this.getClass() + " " + condition);
        
        if (formals.isEmpty()) {
        	throw new SemanticError("The existentially quantified variables  must be non-empty.", 
        			position());
        }
        if (condition == null) {
        	throw new SemanticError("The condition of a dependent type clause must be non-empty.", 
        			position());
        }
        Type t = condition.type();
        
        if (! t.isBoolean())
        	throw new SemanticError("The type of the dependent clause, "+ condition 
        			+ ", must be boolean and not " + t + ".", position());
        
        return this.type(t);
    }
    
    /* (non-Javadoc)
     * @see polyglot.ast.Term#entry()
     */
    public Term firstChild() {
        return (formals.isEmpty())
                ? condition
                : listChild( formals, null);
    }
    
    /* (non-Javadoc)
     * @see polyglot.ast.Term#acceptCFG(polyglot.visit.CFGBuilder, java.util.List)
     */
    @Override
    public List acceptCFG(CFGBuilder v, List succs) {
        if (formals == null) {
            if (condition != null) 
                v.visitCFG(condition, this, EXIT);
        } else {
            if (condition != null) {
                v.visitCFGList( formals, condition, ENTRY);
                v.visitCFG(condition, this, EXIT);
            } else {
                v.visitCFGList( formals, this, EXIT);
            }
        }
        
        
        return succs;
    }

    @Override
    public String toString() {
        String s = "(";
        int count = 0;
        
        for (Iterator i = formals.iterator(); i.hasNext(); ) {
            if (count++ > 2) {
                s += "...";
                break;
            }
            Formal n = (Formal) i.next();
            s += n.toString();
            
            if (i.hasNext()) {
                s += ", ";
            }
        }
        
        if (condition != null) {
            s += ";" + condition;
        } 
        return s + ")";
    }
    /** Write the statement to an output file. */
    @Override
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        if (formals.isEmpty() && condition==null) return;

        if (formals.isEmpty()) {
            print( condition, w, tr);
        }
        
        w.write("(");
        w.begin(0);
        
        for (Iterator it = formals.iterator(); it.hasNext();) {
            Formal e = (Formal) it.next();
            print(e, w, tr);
            
            if (it.hasNext()) {
                w.write(";");
                w.allowBreak(0, " ");
            }	
        }
        w.end();
        if (condition!=null)
            print( condition, w, tr);
        w.write(")");
    }

}
