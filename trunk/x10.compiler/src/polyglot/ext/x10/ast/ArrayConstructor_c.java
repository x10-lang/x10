/*
 * Created by vj on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.ast.Variable;
import polyglot.ast.ArrayInit;
import polyglot.types.Type;

import polyglot.ext.jl.ast.Expr_c;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;

import polyglot.ext.x10.types.X10TypeSystem;

/** An immutable representation of an X10 array constructor.
 * @author vj Dec 9, 2004
 * 
 */
public class ArrayConstructor_c 
extends Expr_c 
implements ArrayConstructor {
		
	protected TypeNode base;
	protected boolean isValue = false;
	protected int baseType;
	
	/** distribution must contain a value whose type is either int or x10.lang.distribution.
	 * If distribution == null, then init must be not null and in fact an Initializer.
	 * The expr then is a constructed form of [0..init.length()-1]->here.
	 * This can be implemented as a javaarray.
	 */
	protected /*nullable*/ Expr distribution;
	/**
	 * 
	 */
	// vj: init may be an ArrayInit or a fun expr, or could be null
	protected /*nullable*/ Expr initializer;
	
	
	/**
	 * @param pos
	 */
	public ArrayConstructor_c(Position pos) {
		super(pos);
		// TODO Auto-generated constructor stub
	}
	public ArrayConstructor_c(Position pos, TypeNode base, boolean isValue, Expr distribution, Expr initializer ) {
		super(pos);
		assert (distribution !=null) || (initializer instanceof ArrayInit);
		this.base = base;
		this.isValue = isValue;
		this.distribution = distribution;
		this.initializer = initializer;
	}
	
	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10ArrayConstructor#arrayBaseType()
	 */
	public TypeNode arrayBaseType() {
		return this.base;
	}
	
	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10ArrayConstructor#region()
	 */
	public Expr distribution() {
		return this.distribution;
	}
	
	public Expr initializer() {
		return initializer;
	}
	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10ArrayConstructor#arrayBaseType(polyglot.ast.TypeNode)
	 */
	public ArrayConstructor arrayBaseType(TypeNode base) {
		ArrayConstructor_c n = (ArrayConstructor_c) copy();
		n.base = base;
		return n;
	}
	
	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10ArrayConstructor#region(polyglot.ast.Expr)
	 */
	public ArrayConstructor distribution(Expr distribution) {
		ArrayConstructor_c n = (ArrayConstructor_c) copy();
		n.distribution = distribution;
		return n;
	}
	
	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10ArrayConstructor#body(polyglot.ast.Block)
	 */
	public ArrayConstructor initializer(Expr init) {
		ArrayConstructor_c n = (ArrayConstructor_c) copy();
		n.initializer = init;
		return n;
	}
	
	/** Is the expression between the square brackets of type int.
	 *  Valid only after type checking.
	 * @return
	 */
	public boolean hasLocal1DimDistribution() {
		return distribution.type().isInt();
	}
	/** Reconstruct the statement. */
	protected ArrayConstructor reconstruct( TypeNode base, Expr distribution, Expr init ) {
		if ( this.base == base && this.distribution == distribution && this.initializer == init ) 
			return this;
		
		ArrayConstructor_c n = (ArrayConstructor_c) copy();
		n.base = base;
		n.distribution = distribution;
		n.initializer = init;
		
		return n;
		
	}
	public boolean isIntArray() {
		return base.type().isInt();
	}
	public boolean isDoubleArray() {
		return base.type().isDouble();
	}
	public boolean hasInitializer() {
		return initializer != null && initializer instanceof ArrayInit;
	}
	
	/** Visit the children of the statement. */
	public Node visitChildren( NodeVisitor v ) {
		TypeNode base = (TypeNode) visitChild(this.base, v);
		Expr distribution = (this.distribution == null) ? null : (Expr) visitChild(this.distribution, v);
		Expr init = (this.initializer == null) ? null : (Expr) visitChild(this.initializer, v);
		return reconstruct( base,  distribution, init );
	}
	
	/** Type check the statement. 
	 * TODO: Check that the distribution and initializer have already been visited by the TC.
	 */
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
		
		Node n = this.base.typeCheck( tc );
		
		if (! (n instanceof TypeNode))
			throw new SemanticException("Array constructor base type |" + n + "| is not a type." + position());
		
		TypeNode newBase = (TypeNode) n;
		Type newBaseType = newBase.type();
		

		if (this.distribution != null) {
			Type distType = distribution.type();
			boolean distributionIsInt = ts.isImplicitCastValid(distType, ts.Int());
			boolean distributionIsDist = ts.isImplicitCastValid(distType, ts.distribution());
			if (! distributionIsInt & ! distributionIsDist)
				throw new SemanticException("Array distribution specifier must be of type int or distribution" 
						+ position());
		}
		
		if (initializer != null) {
			if (initializer instanceof ArrayInit) 
				// This is the {...} case
				((ArrayInit) initializer).typeCheckElements(base.type());
			else {
				// TODO: vj The following is hardwired for int and double arrays.
				Type initType = initializer.type();
				if ( ts.isImplicitCastValid(newBaseType, ts.Int()) 
						&& ! ts.isImplicitCastValid(initType, ts.IntArrayPointwiseOp()))
					throw new SemanticException("Array initializer must be of type x10.lang.intArray.pointwiseOp" 
							+ position());
				if ( ts.isImplicitCastValid(newBaseType, ts.Double()) 
						&& ! ts.isImplicitCastValid(initType, ts.DoubleArrayPointwiseOp()))
					throw new SemanticException("Array initializer must be of type x10.lang.doubleArray.pointwiseOp" 
							+ position());
				
			}
		}
		
		Type t = ts.x10arrayOf(position(), newBaseType);
		
		ArrayConstructor_c n1 = (ArrayConstructor_c) type(t);
	
		return n1.arrayBaseType( newBase);
	
	}
	
	/** Write the statement to an output file. 
	 * TODO: vj check if printBlock is the right thing to use here.
	 * */
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		w.write("new ");
		printBlock(base, w, tr);
		if (distribution == null) {
			w.write("[]");
			initializer.prettyPrint(w, tr);
			return;
		}
		w.write("[");
		printBlock(distribution, w, tr);
		w.write("]");
		if (initializer != null)
			initializer.prettyPrint(w, tr);
	}
	
	public String toString() {
		StringBuffer s = new StringBuffer("new " + base);
		s.append("[");
		s.append(this.distribution == null ? "" : distribution.toString());
		s.append("]");
		s.append(this.initializer == null ? "" : initializer.toString());
		return s.toString();
	}
	/* (non-Javadoc)
	 * @see polyglot.ast.Term#entry()
	 */
	public Term entry() {
		return (distribution != null) ? 
				distribution.entry() 
				:  initializer.entry();
	}
	
	/**
	 * Visit this term in evaluation order.
	 */
	public List acceptCFG(CFGBuilder v, List succs) {
		if (distribution!= null)
			v.visitCFG(distribution, this);
		if (initializer != null)
			v.visitCFG(initializer, this);
		return succs;
	}
	
	
	
	
}
