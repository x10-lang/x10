/*
 * Created by vj on Jan 11, 2005
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.LinkedList;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Precedence;
import polyglot.ast.Term;
import polyglot.ext.jl.ast.ArrayAccess_c;
import polyglot.ext.jl.ast.Call_c;
import polyglot.ext.jl.ast.Cast_c;
import polyglot.ext.jl.ast.Expr_c;
import polyglot.ext.x10.types.NullableType_c;
import polyglot.ext.x10.types.ParametricType_c;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.Flags;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;


/** An immutable representation of an X10 array access with a single index elemen between "[" and "]".
 * index.type() can be either an int, in which case this is an ArrayAccess
 *  or it is a point, in which case this is an X10ArrayAccess,
 * and the region of this point should be a subregion of the array.
 * 
 * TODO:
 *  (1) The index must be a Point whose static type (region) can be 
 *      cast to the index type of the array.
 *  (2) Warn if the region of the point cannot be determined statically.
 * 
 * @author vj Jan 11, 2005
 * 
 */
public class X10ArrayAccess1_c extends Expr_c implements X10ArrayAccess1 {

	protected Expr array;
    protected Expr index;
	/**
	 * @param pos
	 * @param array
	 * @param index
	 */
    public X10ArrayAccess1_c(Position pos, Expr array, Expr index) {
    	super(pos);
    	this.array = array;
    	this.index = index;
    }
    
    /** Get the precedence of the expression. */
    public Precedence precedence() { 
    	return Precedence.LITERAL;
    }
    
    /** Get the array of the expression. */
    public Expr array() {
    	return this.array;
    }
    
    /** Set the array of the expression. */
    public X10ArrayAccess1 array( Expr array ) {
    	X10ArrayAccess1_c n = (X10ArrayAccess1_c) copy();
    	n.array = array;
    	return n;
    }
    
    /** Get the index of the expression. */
    public Expr index() {
    	return this.index;
    }
    
    /** Set the index of the expression. */
    public X10ArrayAccess1 index(Expr index) {
    	X10ArrayAccess1_c n = (X10ArrayAccess1_c) copy();
    	n.index = index;
    	return n;
    }

    /** Return the access flags of the variable. */
    public Flags flags() {
        return Flags.NONE;
    }

    /** Reconstruct the expression. */
    protected X10ArrayAccess1_c reconstruct(Expr array, Expr index) {
    	if (array != this.array || index != this.index) {
    		X10ArrayAccess1_c n = (X10ArrayAccess1_c) copy();
    		n.array = array;
    		n.index = index;
    		return n;
    	}
    	
    	return this;
    }
    /** Visit the children of the expression. */
    public Node visitChildren(NodeVisitor v) {
    	Expr array = (Expr) visitChild(this.array, v);
    	Expr index = (Expr) visitChild(this.index, v);
    	return reconstruct(array, index);
    }

  
	/** Type check the expression. Fork into an ArrayAccess if the underlying
	 * array is a Java array, or if the index is an int and not a distribution.
	 * 
	 * */
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
		Type type = array.type();
		if (! type.isArray()) {
			X10Type target = (X10Type) type;
			if (! target.isX10Array())
				throw new SemanticException(
						"Subscript can only follow an array type.", position());
		}
			
		
		//System.out.println("X10ArrayAccess1_c: Checking if |" + this + "| should be an ArrayAccess.");
		if ( type.isArray() ) {
			// System.out.println("X10ArrayAccess1_c: yes, |" + this + "| should.");
			return new ArrayAccess_c(position(), array, index).typeCheck( tc );
		}
		//System.out.println("X10ArrayAccess1_c: no, |" + this + "| isn't.");
		if (!  ts.isImplicitCastValid(index.type(), ts.point())
				&& (! index.type().isInt())
				) {
			throw new SemanticException(
					"Array subscript |" + toString() + "| must be an integer or a point.", position());
		}
		List args = new LinkedList();
		args.add( index);
		if (type instanceof NullableType_c) {
			type = ((NullableType_c)type).base();
		}
		if (type instanceof ParametricType_c) {
			ParametricType_c pt = (ParametricType_c) type;
			return
			new Cast_c(position(), 
					X10NodeFactory_c.getFactory().CanonicalTypeNode(position(),
							(Type) pt.getTypeParameters().get(0)).type((Type) pt.getTypeParameters().get(0)),
							(Expr) new Call_c(position(), array, "get", args).typeCheck(tc)).typeCheck(tc);
			
		} 
			return new Call_c(position(), array, "get", args).typeCheck(tc);
			// 		return type(((X10Type) type).toX10Array().base());
		
	}

    public Type childExpectedType(Expr child, AscriptionVisitor av) {
        TypeSystem ts = av.typeSystem();
/* FIXME vj-> vj. Treat the int as a shortform for new point( expr ).
        if (child == index) {
            return ts.Int();
        }
*/
        if (child == array) {
            return ts.arrayOf(this.type);
        }

        return child.type();
    }
   
    
    public String toString() {
    	return array + "[" + index + "]";
    }

    /** Write the expression to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
    	printSubExpr(array, w, tr);
    	w.write ("[");
    	printBlock(index, w, tr);
    	w.write ("]");
    }

    public Term entry() {
        return array.entry();
    }

    public List acceptCFG(CFGBuilder v, List succs) {
        v.visitCFG(array, index.entry());
        v.visitCFG(index, this);
        return succs;
    }

    public List throwTypes(TypeSystem ts) {
        return CollectionUtil.list(ts.OutOfBoundsException(),
                                   ts.NullPointerException());
    }

}
