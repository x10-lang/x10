/*
 * Created by vj on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.ast.Node;
import polyglot.ast.Formal;
import polyglot.ext.jl.ast.Field_c;
import polyglot.ext.jl.ast.Stmt_c;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10Type;

import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;


/** Captures the commonality of for, foreach and ateach loops in X10.
 * TODO:
 * (1) formal must be a variable whose type will be that of the region underlying domain.
 * (2) domain must be an array, distribution or region. If it is an array or distribution a, 
 *     the system must behave as if the user typed a.region.
 * (3) Perhaps we can allow continue statements within a for loop, but not within 
 *     a foreach or an ateach.
 * 
 * COMMENTS / TODO (added by Christian)
 * (1) for now, continue/break should work as usual
 *      for foreach;
 * (2) ateach is broken in many respects, including break/continue,
 *      see comments in ateach.xcd
 * (3) this AST node does not seem to support the 'correct' syntax for
 *      multi-dimensional arrays (ateach(i,j:D) { S }).  But that's
 *      probably ok, for now the XCD files expect to see
 *      ateach(i:D) { S } and type 'i' as 'int[]' for multi-dimensional
 *      arrays, and as 'int' for single-dimensional arrays.
 * 
 * @author vj Dec 9, 2004
 * 
 */
public abstract class X10Loop_c extends Stmt_c implements X10Loop {
    protected Formal formal;
    protected Expr domain;
    protected Stmt body;

    /**
     * @param pos
     */
    protected X10Loop_c(Position pos) {
	super(pos);
    }

    protected X10Loop_c( Position pos, Formal formal, Expr domain, Stmt body) {
    	super( pos );
    	this.formal = formal;
    	this.domain = domain;
    	this.body = body;
    	
    }
    
	/** Reconstruct the expression. */
	protected X10Loop_c reconstruct( Formal formal, Expr domain, Stmt body ) {
		if (formal != this.formal || domain != this.domain || body != this.body) {
			X10Loop_c n = (X10Loop_c) copy();
			n.formal = formal;
			n.domain = domain;
			n.body = body;
			return n;
		}
		return this;
	}
    
    /** Type check the statement. */
    public Node typeCheck(TypeChecker tc) throws SemanticException {
    	X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
		
		Expr newDomain = domain;
		X10Type domainType = (X10Type) domain.type();
		if (domainType.isX10Array())
			newDomain = (Expr) getDomain( domain ).typeCheck( tc );
		
		return domain( newDomain);
    }
    
    public Expr getDomain( Expr d ) {
		return new Field_c(position(), d, "region");
	}
    /* (non-Javadoc)
     * @see polyglot.ast.Term#entry()
     */
    public Term entry() {
    	return formal.entry();
    }
    
    /* (non-Javadoc)
     * @see polyglot.ast.Term#acceptCFG(polyglot.visit.CFGBuilder, java.util.List)
     */
    public List acceptCFG(CFGBuilder v, List succs) {
    	v.visitCFG( formal, domain.entry());
    	v.visitCFG( domain, body.entry());
    	v.visitCFG( body, this);
    	return succs;
    }
    
    public Context enterScope(Context c) {
    	return c.pushBlock();
        }

    /** Visit the children of the expression. */
	public Node visitChildren(NodeVisitor v) {
		Formal formal = (Formal) visitChild(this.formal, v);
		Expr domain = (Expr) visitChild(this.domain, v);
		Stmt body = (Stmt) visitChild(this.body, v);
		return reconstruct( formal, domain, body );
	}
    /* (non-Javadoc)
     * @see polyglot.ext.x10.ast.X10Loop#body()
     */
    public Stmt body() {
    	return this.body;
    }
    
    /* (non-Javadoc)
     * @see polyglot.ext.x10.ast.X10Loop#formal()
     */
    public Formal formal() {
    	return this.formal;
    }
    
    /* (non-Javadoc)
     * @see polyglot.ext.x10.ast.X10Loop#domain()
     */
    public Expr domain() {
    	return this.domain;
    }
    public X10Loop_c body(Stmt body) {
		X10Loop_c n = (X10Loop_c) copy();
		n.body = body;
		return n;
	}
	
	public X10Loop_c formal(Formal formal) {
		X10Loop_c n = (X10Loop_c) copy();
		n.formal = formal;
		return n;
	}
	
	public X10Loop_c domain(Expr domain) {
		X10Loop_c n = (X10Loop_c) copy();
		n.domain = domain;
		return n;
	}
	
    
    
}
