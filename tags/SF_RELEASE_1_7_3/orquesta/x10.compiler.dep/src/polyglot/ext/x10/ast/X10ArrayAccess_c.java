/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.CanonicalTypeNode_c;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Precedence;
import polyglot.ast.Term;
import polyglot.ast.Expr_c;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.NullableType;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.main.Report;
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
import polyglot.util.TypedList;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;

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
		X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
		Type type = array.type();
		if (type.isArray())
			throw new SemanticException(
					"Multiple subscript cannot follow an array of rank 1.", position());
		if ( Report.should_report("debug",3))
        Report.report(3,"X10ArrayAccess_c: typeCheck type="  + type + " |" 
                + type.getClass() + "|" + ts.isIndexable(type));
        
		X10Type target = (X10Type) type;
		if (! ts.isIndexable(target)) {
			throw new SemanticException(
					"Multiple subscripts can only follow an array of rank > 1.", position());
		}
        for (Iterator<Expr> it = index.iterator(); it.hasNext();) {
            Expr item = (Expr) it.next();
            if (! ts.isImplicitCastValid(item.type(), ts.Int())) {
                throw new SemanticException(
                        "Array subscript " + item + " must be an integer.", position());
            }
        }
        List<Expr> args = new LinkedList();
        args.addAll(index);
       
        if (X10TypeMixin.isParametric(target)) {
            List<Type> params = target.typeParameters();
            Type param = (Type) params.get(0);
            return type(param);
        }
        // find the return type by finding the return type of the get(index) method on type.
        
        X10ClassType refType 
        = (X10ClassType) (type instanceof NullableType ? ((NullableType) type).ultimateBase() : type);
        		
        String name = "get";
        List argTypes = new LinkedList();
        for (Iterator<Expr> it = index.iterator(); it.hasNext();) {
            Expr item = (Expr) it.next();
            argTypes.add(item.type());
        }
        // fake this since you know the method is public.
        ClassType currType= refType; 
        
        // May throw a semantic exception. Should prolly be caught and rethrown 
        // as an InternalError.
        MethodInstance m = ts.findMethod(refType, name, argTypes, tc.context().currentClassScope()); 
        Type retType = m.returnType();
        return type(retType);
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
		return array.toString()  + index ;
	}
	
	/** Write the expression to an output file. */
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		assert false;
        X10Type at = ( X10Type) array.type();
    
        if (X10TypeMixin.isParametric(at)) {
            Type result = (Type) at.typeParameters().get(0);
            w.write("((");
            print(new CanonicalTypeNode_c(Position.COMPILER_GENERATED, Types.ref(result)), w, tr);
            w.write(")");
        }
        printSubExpr(array, w, tr);
        w.write (".get(");
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
       
        w.write (")");
        if (X10TypeMixin.isParametric(at)) { w.write (")");}
        
		
	}
	
	
	public Term firstChild() {
		return array;
	}
	
	public List acceptCFG(CFGBuilder v, List succs) {
                if (index.isEmpty()) {
                    v.visitCFG(array, this, EXIT);
                }
                else {
                    v.visitCFG(array, listChild(index, null), ENTRY);
                    v.visitCFGList(index, this, EXIT);
                }
		return succs;
	}
	
	public List throwTypes(TypeSystem ts) {
		return CollectionUtil.list(ts.OutOfBoundsException(),
				ts.NullPointerException());
	}
   
 
}
