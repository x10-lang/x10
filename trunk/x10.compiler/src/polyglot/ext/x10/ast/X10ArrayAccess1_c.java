/*
 * Created by vj on Jan 11, 2005
 */
package polyglot.ext.x10.ast;

import java.util.LinkedList;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Precedence;
import polyglot.ast.Term;
import polyglot.ext.jl.ast.Expr_c;
import polyglot.ext.x10.types.NullableType_c;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.main.Report;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.MethodInstance;
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
		if (!type.isArray()) {
			X10Type target = (X10Type) type;
			if (!target.isX10Array())
				throw new SemanticException(
						"Subscript can only follow an array type.", position());
		}

		//System.out.println("X10ArrayAccess1_c: Checking if |" + this + "| should be an ArrayAccess.");
		// [IP] TODO: Huh?  Didn't we just check for this?
		if (type instanceof NullableType_c) {
			type = ((NullableType_c)type).base();
		}
		if (type.isArray()) {
			// System.out.println("X10ArrayAccess1_c: yes, |" + this + "| should.");
			return nf.ArrayAccess(position(), array, index).typeCheck(tc);
		}
		//System.out.println("X10ArrayAccess1_c: no, |" + this + "| isn't.");
		if (!ts.isImplicitCastValid(index.type(), ts.point()) &&
			(!index.type().isInt()))
		{
			throw new SemanticException(
					"Array subscript |" + toString() + "| must be an integer or a point.", position());
		}
        
		List args = new LinkedList();
		args.add(index);
        X10Type pt = (X10Type) type;
		if (pt.isParametric()) {
            List params = pt.typeParameters();
            Type param = (Type) params.get(0);
            return type(param);
			/*return
				nf.Cast(position(),
						nf.CanonicalTypeNode(position(), param).type(param),
						(Expr) nf.Call(position(), array, "get", args).typeCheck(tc)).typeCheck(tc);*/

		}
        // find the return type by finding the return type of the get(index) method on type.
        
        X10ClassType refType = (X10ClassType) type;
        String name = "get";
        List argTypes = new LinkedList();
        argTypes.add(index.type());
        // fake this since you know the method is public.
        ClassType currType= refType; 
        
        // May throw a semantic exception. Should prolly be caught and rethrown 
        // as an InternalError.
        MethodInstance m = ts.findMethod(refType, name, argTypes, currType); 
        Type retType = m.returnType();
        return type(retType);
      

	}

	public Type childExpectedType(Expr child, AscriptionVisitor av) {
		TypeSystem ts = av.typeSystem();
		/* FIXME vj-> vj. Treat the int as a shortform for new point(expr).
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
	    X10Type pt = ( X10Type) type;
        String result = "";
	    if (pt.isParametric()) {
	        Type type = (Type) pt.typeParameters().get(0);
	        result = "(" + type + ")";
	    }
	    return  result + array + ".get("  + index + ")";
	}
	

	/** Write the expression to an output file. */
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        X10Type at = ( X10Type) array.type();
       
        if (at.isParametric()) {
            Type result = (Type) at.typeParameters().get(0);
            w.write("((");
            print(new X10CanonicalTypeNode_c(Position.COMPILER_GENERATED,result), w, tr);
            w.write(")");
            printSubExpr(array, w, tr);
            w.write (".get(");
            printBlock(index, w, tr);
            w.write ("))");
            return;
        }
        printSubExpr(array, w, tr);
        w.write (".get(");
        printBlock(index, w, tr);
        w.write (")");
            
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

