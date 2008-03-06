/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Jan 11, 2005
 */
package polyglot.ext.x10.ast;

import java.util.LinkedList;
import java.util.List;

import polyglot.ast.CanonicalTypeNode_c;
import polyglot.ast.Expr;
import polyglot.ast.Expr_c;
import polyglot.ast.Node;
import polyglot.ast.Precedence;
import polyglot.ast.Term;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.NullableType;
import polyglot.ext.x10.types.NullableType_c;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.MethodInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
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
 * or it is a point, in which case this is an X10ArrayAccess,
 * and the region of this point should be a subregion of the array.
 *
 * TODO:
 *  (1) The index must be a Point whose static type (region) can be
 *      cast to the index type of the array.
 *  (2) Warn if the region of the point cannot be determined statically.
 *
 * @author vj Jan 11, 2005
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
	public X10ArrayAccess1 array(Expr array) {
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

	/**
	 * Type check the expression. Fork into an ArrayAccess if the underlying
	 * array is a Java array, or if the index is an int and not a distribution.
	 */
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
		X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
		Type type = array.type();
		if (type instanceof NullableType_c) {
			type = ((NullableType_c)type).base();
		}
		
		boolean isArray = type.isArray();
		boolean isIndexable = ts.isIndexable(type);
		//Report.report(1, "X10ArrayAccess_1 |" + this + "| type=" + type.getClass() + " isArray()= " + type.isArray());
		if (! (isArray || isIndexable)) {
				throw new SemanticException(
						"Subscript can only follow an array type, and not " + type+".", position());
		}
		if ( isArray)
			return nf.ArrayAccess(position(), array, index).del().typeCheck(tc);
		
		if (!ts.isImplicitCastValid(index.type(), ts.point()) &&
			(!index.type().isInt()))
		{
			throw new SemanticException(
					"Array subscript |" + toString() + "| must be an integer or a point.", position());
		}
        
		List args = new LinkedList();
		args.add(index);
        X10Type pt = (X10Type) type;
		if (X10TypeMixin.isParametric(pt)) {
            List params = pt.typeParameters();
            Type param = (Type) params.get(0);
            return type(param);
		}
        // find the return type by finding the return type of the get(index) method on type.
        
		   X10ClassType refType 
	        = (X10ClassType) (type instanceof NullableType ? ((NullableType) type).base() : type);
        String name = "get";
        List argTypes = new LinkedList();
        argTypes.add(index.type());
        // fake this since you know the method is public.
        ClassType currType= refType; 
        
        // May throw a semantic exception. Should prolly be caught and rethrown 
        // as an InternalError.
        MethodInstance m = ts.findMethod(refType, name, argTypes, tc.context().currentClassScope()); 
        Type retType = m.returnType();
        //Report.report(1, "X10ArrayAcces1 arraytype " + array.type() + "(" + array.type().getClass() + " for " + this);
        return type(retType);
	}

	public Type childExpectedType(Expr child, AscriptionVisitor av) {
		X10TypeSystem ts = (X10TypeSystem) av.typeSystem();
		/* FIXME vj-> vj. Treat the int as a shortform for new point(expr).*/
		if (child == index) {
			return ts.Int();
		}
		
		if (child == array) {
			return ts.array(this.type);
		}

		return child.type();
	}

	public String toString() {
	    Type pt = type;
	    String result = "";
	    if (pt instanceof X10Type && X10TypeMixin.isParametric((X10Type) pt)) {
	        Type type = (Type) ((X10Type) pt).typeParameters().get(0);
	        result = "(" + type + ")";
	    }
	    return  result + array + ".get("  + index + ")";
	}
	

	/** Write the expression to an output file. */
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		assert false;
        X10Type at = (X10Type) array.type();
        if (X10TypeMixin.isParametric(at)) {
            Type result = (Type) at.typeParameters().get(0);
            w.write("((");
            print(new CanonicalTypeNode_c(Position.COMPILER_GENERATED,Types.ref(result)), w, tr);
            w.write(")");
        }
        printSubExpr(array, w, tr);
        w.write(".get(");
        printBlock(index, w, tr);
        w.write (")");
        if (X10TypeMixin.isParametric(at)) {
        	w.write(")");
        }
	}

	public Term firstChild() {
		return array;
	}

	public List acceptCFG(CFGBuilder v, List succs) {
		v.visitCFG(array, index, ENTRY);
		v.visitCFG(index, this, EXIT);
		return succs;
	}

	public List throwTypes(TypeSystem ts) {
		return CollectionUtil.list(ts.OutOfBoundsException(),
								   ts.NullPointerException());
	}
    
}

