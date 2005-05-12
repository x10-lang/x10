/*
 * Created by vj on Dec 9, 2004
 */
package polyglot.ext.x10.ast;

import java.util.LinkedList;
import java.util.List;

import polyglot.ast.ArrayInit;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.ast.Call_c;
import polyglot.ext.jl.ast.Expr_c;
import polyglot.ext.jl.ast.NewArray_c;
import polyglot.ext.x10.types.X10ReferenceType;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;

/** An immutable representation of an X10 array constructor.
 * @author vj Dec 9, 2004
 * 
 */
public class ArrayConstructor_c 
extends Expr_c 
implements ArrayConstructor {
	
	protected TypeNode base;
	protected boolean isValue = false;
	protected boolean isSafe = true;
	
	/** distribution must contain a value whose type is either int or x10.lang.dist.
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
	public ArrayConstructor_c(Position pos, TypeNode base, boolean unsafe, 
			boolean isValue, Expr distribution, Expr initializer ) {
		super(pos);
		assert (distribution !=null) || (initializer instanceof ArrayInit);
		this.base = base;
		this.isSafe = ! unsafe;
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
	
	public boolean isSafe() {
		return isSafe;
	}
	public boolean isValue() {
		return isValue;
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
		Expr newDistribution = distribution;
		
		
		if (this.distribution != null) {
			Type distType = distribution.type();
			boolean distributionIsInt = ts.isImplicitCastValid(distType, ts.Int());
			if (distributionIsInt) {
				// This is a NewArray in disguise. Unmask it and bail.
				List l = new LinkedList();
				l.add( distribution );
				return new NewArray_c( position(), newBase, l, 0, null).typeCheck( tc );
			}
			boolean distributionIsRegion = ts.isImplicitCastValid(distType, ts.region());
			if (distributionIsRegion) {
				// convert this region to a distribution.
				
				newDistribution = (Expr) (new Call_c(position(), distribution, "toDistribution", new LinkedList())).typeCheck( tc );
			} else {
				// it must be a distribution.
				boolean distributionIsDist = ts.isImplicitCastValid(distType, ts.distribution());
				// System.out.println("ArrayConstructor_c: distributionIsDist = " + distributionIsDist + " " + distType );
				if ( ! distributionIsDist)
					throw new SemanticException("Array distribution specifier must be of type int or distribution" 
							+ position());
			}
		}
		
		if (initializer != null) {
			if (initializer instanceof ArrayInit) {
				throw new InternalError("ArrayConstructor_c should really have been NewArray_c" + this);
				// This is the {...} case, and the parser should rule this out.
				// ((ArrayInit) initializer).typeCheckElements(base.type());
			}
			
			// TODO: vj/cg: The following is hardwired for int, double and long arrays. Other primitives will fail.
			Type initType = initializer.type();
			if ( newBaseType.isInt()){
				if (! ts.isImplicitCastValid(initType, ts.IntArrayPointwiseOp()))
					throw new SemanticException("Array initializer must be of type x10.lang.intArray.pointwiseOp" 
							+ position());
			} else if ( newBaseType.isDouble()) {
				if (! ts.isImplicitCastValid(initType, ts.DoubleArrayPointwiseOp()))
					throw new SemanticException("Array initializer must be of type x10.lang.doubleArray.pointwiseOp" 
							+ position());
			} else if (newBaseType.isLong()) {
				if (! ts.isImplicitCastValid(initType, ts.LongArrayPointwiseOp()))
					throw new SemanticException("Array initializer must be of type x10.lang.longArray.pointwiseOp" 
							+ position());
			} else if ( newBaseType.isBoolean()) {
                if (! ts.isImplicitCastValid(initType, ts.BooleanArrayPointwiseOp()))
                    throw new SemanticException("Array initializer must be of type x10.lang.booleanArray.pointwiseOp" 
                            + position());
            } else if ( newBaseType.isByte()) {
                if (! ts.isImplicitCastValid(initType, ts.ByteArrayPointwiseOp()))
                    throw new SemanticException("Array initializer must be of type x10.lang.byteArray.pointwiseOp" 
                            + position());
            } else if ( newBaseType.isChar()) {
                if (! ts.isImplicitCastValid(initType, ts.CharArrayPointwiseOp())) {
                    throw new SemanticException("Array initializer must be of type x10.lang.charArray.pointwiseOp" 
                            + position()); }
            } else if ( newBaseType.isShort()) {
                if (! ts.isImplicitCastValid(initType, ts.ShortArrayPointwiseOp()))
                    throw new SemanticException("Array initializer must be of type x10.lang.shortArray.pointwiseOp" 
                            + position());
            } else if ( newBaseType.isFloat()) {
                if (! ts.isImplicitCastValid(initType, ts.FloatArrayPointwiseOp()))
                    throw new SemanticException("Array initializer must be of type x10.lang.floatArray.pointwiseOp" 
                            + position());
            } else {
			    Type toType = ts.GenericArrayPointwiseOp((X10ReferenceType)newBaseType);
			    if (! ts.isImplicitCastValid(initType, toType))
			        throw new SemanticException("Array initializer must be of type " + toType.toString()+ " at " + position());
			}                     
		}
		Type t = ts.array( newBaseType, isValue );
		// System.out.println("ArrayConstructor_c: t=" + t);
		ArrayConstructor_c n1 = (ArrayConstructor_c) type(t);
		
		return n1.distribution( newDistribution ).arrayBaseType( newBase );
		
	}
	
	public Context enterScope(Node child, Context c) {
        if (child == initializer ) {
        	
         
        }
        Context result = super.enterScope(child, c);
        
        return result;
    }
	/** Write the statement to an output file. 
	 * TODO: vj check if printBlock is the right thing to use here.
	 * */
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		w.write("new ");
		printBlock(base, w, tr);
		if (isValue)
			w.write(" value ");
		if ( ! isSafe )
			w.write(" unsafe ");
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
		s.append( isValue ? " value " : "");
		s.append( ! isSafe ? " unsafe " : "");
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
				:  (initializer  != null ? initializer.entry() : this);
	}
	
	/**
	 * Visit this term in evaluation order.
	 */
	public List acceptCFG(CFGBuilder v, List succs) {
		if (distribution != null)
			v.visitCFG(distribution, (initializer != null) ? initializer.entry() : this);
		if (initializer != null)
			v.visitCFG(initializer, this);
		return succs;
	}
	
}
