/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * @author Philippe Charles
 * @author vj
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Expr_c;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.ext.x10.types.FutureType;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10NamedType;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.visit.ExprFlattener;
import polyglot.ext.x10.visit.ExprFlattener.Flattener;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.ReachChecker;
import polyglot.visit.TypeChecker;


/** A <code>Future </code> is a representation of the X10 future construct:
 * <code>future (place) { expression }<code>
 * stmts are used to represent the fully exploded version of the expression
 * as might be needed in order to inline array expressions.
 */
public class Future_c extends Expr_c
    implements Future {

    public Expr place; 
    public Expr body;
    public StmtSeq stmt;

    public Future_c(Position p, Expr place, Expr body) {
        super(p);
        this.place = place;
        this.body = body;
    }
    
    public Future_c(Position p) {
        super(p);
    }
    
    /* (non-Javadoc)
     * @see polyglot.ext.x10.ast.TranslateWhenDumpedNode#getArgument(int)
     */
    public Node getArgument(int id) {
        if (id == 0)
            return place;
        if (id == 1)
            return body;
        assert (false);
        return null;
    }
    

    public Future stmt(StmtSeq stmt) {
    	Future_c n = (Future_c) copy();
    	n.stmt = stmt;
    	return n;
    }
    /* (non-Javadoc)
     * @see polyglot.ext.x10.ast.Future#body(polyglot.ast.Expr)
     */
    public Future body(Expr body) {
        Future_c n = (Future_c) copy();
        n.body = body;
        return n;
    }

    public StmtSeq stmt() {
    	return stmt;
    }
    // In place update. Use with care, see ExprFlattener.
    public void setStmt(StmtSeq s) {
    	this.stmt = s;
    }
    // In place update. Use with care, see ExprFlattener.
    public void setExpr(Expr e) {
    	this.body = e;
    }
    public void setPlace(Expr e) {
    	this.place = e;
    }
    /* (non-Javadoc)
     * @see polyglot.ext.x10.ast.Future#body()
     */
    public Expr body() {
        return body;
    }

    /** Get the RemoteActivity's place. */
    public Expr place() {
        return place;
    }
    
    /** Set the RemoteActivity's place. */
    public RemoteActivityInvocation place(Expr place) {
        this.place = place;
        return this;
    }

    protected Future_c reconstruct( Expr place, Expr body, StmtSeq stmt ) {
	if ( place != this.place || body != this.body || stmt != this.stmt) {
	    Future_c n = (Future_c) copy();
	    n.place = place;
	    n.body = body;
	    n.stmt = stmt;
	    return n;
	}
	return this;
    }

    /** Visit the children of the expression. 
     * vj: TODO: I use a hack below to bypass 
     * visiting the embedded stmt if the visitor is a ReachChecker.
     * Otherwise a reach error is generated that is in fact spurious.
     * There must be a way to convince the ReachChecker legitimately that this statement
     * is reachable if the future is reachable.
     * */
    public Node visitChildren( NodeVisitor v ) {
    	Expr place = (Expr) visitChild( this.place, v );
    	Expr body = (Expr) visitChild( this.body, v );
    	StmtSeq s = stmt;
    	if ((v instanceof ReachChecker)) {
    		s = (s==null ? null :(StmtSeq) stmt.reachable(true));
    	} else {
    	 s = (s == null ? null : (StmtSeq) visitChild( this.stmt, v ));
    	}
    	return reconstruct( place, body, s );
    }


    /** Type check the expression. */
    public Node typeCheck( TypeChecker tc ) throws SemanticException {
    	X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
    	X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
    	Type placeType = place.type();
    	Expr newPlace = place;
    	boolean placeIsPlace = ts.isImplicitCastValid(placeType, ts.place());
    	if ( ! placeIsPlace ) {
    	    newPlace = (Expr) nf.Field(position(), place, nf.Id(position(), "location")).del().typeCheck(tc);
    	}
    	
    	return ((Future_c) place(newPlace)).type( ts.createFutureType(position(), 
    			Types.ref((X10NamedType) body.type())));
    }
    
    /** Flatten the expressions in place and body, creating stmt if necessary.
     * The place field must be visited by the given flattener since those statements must be executed outside
     * the future. Howeever, the body must be visited in a new flattener and the statements produced
     * captured and stored in stmt. 
     * Note that this works by side-effecting the current node. This is necessary
     * because the method is called from within an enter call for a Visitor. I dont know
     * of any way of making the enter call return a copy of the node. 
     * @param fc
     * @return
     */
   public Future flatten(ExprFlattener.Flattener fc) {
	   //Report.report(1, "Future_c: entering future " + this);
		
		Expr place = (Expr) this.place.visit(fc);
		
		X10Context xc = (X10Context) fc.context();
		Flattener newVisitor = (Flattener) new ExprFlattener.Flattener(fc.job(), fc.typeSystem(),
				fc.nodeFactory(), this).context(xc);
		
		Expr		body = (Expr) this.body.visit(newVisitor);
		List/*<Stmt>*/ l = newVisitor.stmtList();
		StmtSeq stmt;
		if (! l.isEmpty()) {
			if (this.stmt == null) {	
				X10NodeFactory xnf = (X10NodeFactory) fc.nodeFactory();
				stmt = xnf.StmtSeq(Position.COMPILER_GENERATED, l);
			} else {
				List ll = this.stmt.statements();
				ll.add(l);
				stmt = (StmtSeq) this.stmt.statements(ll);
			}
			
		}
		else {
		    stmt = null;
		}
		//Report.report(1, "Future_c: returning " + this);
		return reconstruct(place, body, stmt);
		
   }
    public Type childExpectedType(Expr child, AscriptionVisitor av) {
    	X10TypeSystem ts = (X10TypeSystem) av.typeSystem();
    	if ( child == place ) {
    		return ts.place();
    	}
    	if ( child == body ) {
    	    Type t = this.type();
    	    if (t instanceof FutureType) {
    	        FutureType ft = (FutureType) t;
    	        return ft.base();
    	    }
    	}
    	return child.type();
    }

    public String toString() {
    	return  " future ( " + place + " ) { " + stmt + " " + body + "}";
    }
   
    /** Write the expression to an output file. */

    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
    	w.write("future (");
    	printSubExpr(place, false, w, tr);
    	w.write(" ) {  ");
    	printSubExpr(body, false, w, tr);
    	w.write("}");
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
    public List acceptCFG(CFGBuilder v, List succs) {
    	v.visitCFG(place, body(), ENTRY);
    	if ((stmt != null)) {
    		v.visitCFG(body, stmt, ENTRY);
    		v.visitCFG(stmt, this, EXIT);
    	} else {
    		v.visitCFG(body, this, EXIT);
    	}
    	return succs;
    }
        
}
