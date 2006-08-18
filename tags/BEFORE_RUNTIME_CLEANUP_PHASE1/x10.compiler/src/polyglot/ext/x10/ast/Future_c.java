/*
 * @author Philippe Charles
 * @author vj
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ext.jl.ast.Expr_c;
import polyglot.ext.x10.types.FutureType_c;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;


/** A <code>Future </code> is a representation of the X10 future construct:
 * <code>future (place) { expression }<code>
 */
public class Future_c extends Expr_c 
    implements Future {

    public Expr place; 
    public Expr body;

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
    

    /* (non-Javadoc)
     * @see polyglot.ext.x10.ast.Future#body(polyglot.ast.Expr)
     */
    public Future body(Expr body) {
        Future_c n = (Future_c) copy();
        n.body = body;
        return n;
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

    protected Future_c reconstruct( Expr place, Expr body ) {
	if ( place != this.place || body != this.body ) {
	    Future_c n = (Future_c) copy();
	    n.place = place;
	    n.body = body;
	    return n;
	}
	return this;
    }

    /** Visit the children of the expression. */
    public Node visitChildren( NodeVisitor v ) {
    	Expr place = (Expr) visitChild( this.place, v );
    	Expr body = (Expr) visitChild( this.body, v );
    	return reconstruct( place, body );
    }


    /** Type check the expression. */
    public Node typeCheck( TypeChecker tc ) throws SemanticException {
    	X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
    	X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
    	Type placeType = place.type();
    	Expr newPlace = place;
    	boolean placeIsPlace = ts.isImplicitCastValid(placeType, ts.place());
    	if ( ! placeIsPlace ) {
    	    newPlace = (Expr) nf.Field(position(), place, "location").typeCheck(tc);
    	}
    	
    	return ((Future_c) place(newPlace)).type( ts.createFutureType(position(), body.type()));
    }
    
    public Type childExpectedType(Expr child, AscriptionVisitor av) {
    	X10TypeSystem ts = (X10TypeSystem) av.typeSystem();
    	if ( child == place ) {
    		return ts.place();
    	}
    	return child.type();
    }

    public String toString() {
    	return  " future ( " + place + " ) { " + body + "}";
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
    public Term entry() {
        return place.entry();
    }

    /**
     * Visit this term in evaluation order.
     */
    public List acceptCFG(CFGBuilder v, List succs) {
        v.visitCFG(place, body());
        v.visitCFG(body(), this);
        return succs;
    }
        
}
