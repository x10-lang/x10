/*
 * Created by vj on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.ArrayAccess;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Precedence;
import polyglot.ast.Term;
import polyglot.ext.jl.ast.ArrayAccess_c;
import polyglot.ext.jl.ast.Expr_c;
import polyglot.ext.jl.ast.Call_c;

import polyglot.types.Flags;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Flags;

import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.util.TypedList;

import polyglot.visit.AscriptionVisitor;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;
import polyglot.visit.AscriptionVisitor;

import java.util.LinkedList;

import polyglot.ext.x10.types.X10Type;

import java.util.Iterator;
import java.util.List;

/** An immutable representation of a (multdimensional) X10 array access, involving more than
 * one index.
 * 
 * 
 * @seealso X10ArrayAccess1_c
 * @author vj Dec 9, 2004
 * 
 */
public class X10ArrayAccess_c extends Expr_c implements X10ArrayAccess {
	protected Expr array;
	protected List/*<Expr>*/ index;
	/**
	 * @param pos
	 * @param array
	 * @param index
	 */
	public X10ArrayAccess_c(Position pos, Expr array, List/*<Expr>*/ index) {
		super(pos);
		assert index.size() > 1;
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
	public X10ArrayAccess array(Expr array) {
		X10ArrayAccess_c n = (X10ArrayAccess_c) copy();
		n.array = array;
		return n;
	}
	
	/** Get the index of the expression. */
	public List/*<Expr>*/ index() {
		return TypedList.copy(this.index, Expr.class, false);
	}
	
	/** Set the index of the expression. */
	public X10ArrayAccess index(List index) {
		X10ArrayAccess_c n = (X10ArrayAccess_c) copy();
		n.index = index;
		return n;
	}
	
	/** Reconstruct the expression. */
	protected X10ArrayAccess_c reconstruct( Expr array, List index ) {
		if (array != this.array || index != this.index) {
			X10ArrayAccess_c n = (X10ArrayAccess_c) copy();
			n.array = array;
			n.index = index;
			return n;
		}
		return this;
	}
	/** Return the access flags of the variable. */
	public Flags flags() {
		return Flags.NONE;
	}
	
	
	/** Visit the children of the expression. */
	public Node visitChildren(NodeVisitor v) {
		Expr array = (Expr) visitChild(this.array, v);
		List index =  visitList(this.index, v);
		return reconstruct(array, index);
	}
	
	/** Type check the expression. 
	 * TOOD: vj Check the dimensionality of the array and the number of dimensions is the same.
	 * */
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		TypeSystem ts = tc.typeSystem();
		Type type = array.type();
		if (type.isArray())
			throw new SemanticException(
					"Multiple subscript cannot follow an array of rank 1.", position());
		//System.out.println("X10ArrayAccess_c: typeCheck type="  + type);
		X10Type target = (X10Type) type;
		if (! target.isX10Array()) {
			throw new SemanticException(
					"Multiple subscripts can only follow an array of rank > 1.", position());
		}
		// Note that we cannot change X10NodeFactory to produce this call to begin with because
		// we need to let X10ArrayAccessAssign have a chance to see an X10ArrayAccess object.
		// Typechecking is a good time to do the rewriting.
		
		return new Call_c(position(), array, "get", index).typeCheck(tc);
		/*
		for (Iterator it = index.iterator(); it.hasNext();) {
			Expr item = (Expr) it.next();
			if (! ts.isImplicitCastValid(item.type(), ts.Int())) {
				throw new SemanticException(
						"Array subscript " + item + " must be an integer.", position());
			}
		}
		// TODO
		// * (1) Check if Java catches IndexOutOfBounds errors at compiletime for a constant point. 
		// *      If so, X10 should do the same.
		//* (2) The X10 runtime should throw an IndexOutOfBounds when a variable of type
		//*     region takes a value not in the region (e.g. as a result of arithmetic operations). 
		
		return type(target.toX10Array().base());
		*/
	}
	
	public Type childExpectedType(Expr child, AscriptionVisitor av) {
		TypeSystem ts = av.typeSystem();
		
		if (child == index) {
			return ts.Int();
		}
		
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
		printSubExpr( array, w, tr );
		w.write("[");
		w.begin(0);
		
		for(Iterator i = index.iterator(); i.hasNext();) {
			Expr e = (Expr) i.next();
			print(e, w, tr);
			
			if (i.hasNext()) {
				w.write(",");
				w.allowBreak(0, " ");
			}
		}
		
		w.end();
		w.write("]");
	}
	
	
	public Term entry() {
		return array.entry();
	}
	
	public List acceptCFG(CFGBuilder v, List succs) {
		v.visitCFG(array, listEntry(index, this));
		v.visitCFGList(index, this);
		return succs;
	}
	
	public List throwTypes(TypeSystem ts) {
		return CollectionUtil.list(ts.OutOfBoundsException(),
				ts.NullPointerException());
	}
}
