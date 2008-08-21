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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Assign_c;
import polyglot.ast.CanonicalTypeNode_c;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Precedence;
import polyglot.ast.Term;
import polyglot.ext.x10.types.X10MethodInstance;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.Flags;
import polyglot.types.MethodInstance;
import polyglot.types.SemanticException;
import polyglot.types.Name;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.TypeSystem_c.MethodMatcher;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.Translator;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

/** An immutable representation of an X10 array access update: a[point] op= expr;
 * TODO
 * Typechecking rules:
 * (1) point must be of a type (region) that can be cast to the array index type.
 * (2) expr must be of a type that can be implicitly cast to the base type of the array.
 * (3) The operator, if any, must be permitted on the underlying type.
 * (4) No assignment is allowed on a value array.
 * @author vj Dec 9, 2004
 * 
 */
public class SettableAssign_c extends Assign_c implements SettableAssign {
   	protected Expr array;
   	protected List<Expr> index;

	/**
	 * @param pos
	 * @param left
	 * @param op
	 * @param right
	 */
    public SettableAssign_c(Position pos, Expr array, List<Expr> index, Operator op, Expr right) {
		super(pos, op, right);
		assert index.size() >= 1;
		this.array = array;
		this.index = index;	
	}
	
	public Type leftType() {
	    return right.type();
	}

	@Override
	public Expr left(NodeFactory nf) {
	    return nf.Call(position(), array, nf.Id(position(), "get"), index);
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
   	public SettableAssign array(Expr array) {
   	 SettableAssign_c n = (SettableAssign_c) copy();
   		n.array = array;
   		return n;
   	}
   	
   	/** Get the index of the expression. */
   	public List<Expr> index() {
   		return TypedList.copy(this.index, Expr.class, false);
   	}
   	
   	/** Set the index of the expression. */
   	public SettableAssign index(List<Expr> index) {
   	    SettableAssign_c n = (SettableAssign_c) copy();
   	    n.index = TypedList.copyAndCheck(index, Expr.class, true);
   	    return n;
   	}
   	
   	/** Reconstruct the expression. */
   	protected SettableAssign_c reconstruct( Expr array, List<Expr> index ) {
   		if (array != this.array || index != this.index) {
   		 SettableAssign_c n = (SettableAssign_c) copy();
   			n.array = array;
   			n.index = TypedList.copyAndCheck(index, Expr.class, true);
   			return n;
   		}
   		return this;
   	}
   	/** Return the access flags of the variable. */
   	public Flags flags() {
   		return Flags.NONE;
   	}
   	
   	
   	/** Visit the children of the expression. */
   	public Assign visitLeft(NodeVisitor v) {
   		Expr array = (Expr) visitChild(this.array, v);
   		List index =  visitList(this.index, v);
   		return reconstruct(array, index);
   	}
   	
   	public Type childExpectedType(Expr child, AscriptionVisitor av) {
   		X10TypeSystem ts = (X10TypeSystem) av.typeSystem();
   		
   		if (child == array) {
   			return ts.Settable();
   		}
   		
   		return child.type();
   	}
   	    
	public Name opString2(Operator op) {
	    return Name.make(opString(op));
	}
	
	public String opString(Operator op) {
	    if (op == ASSIGN ) return "set";
	    if (op == ADD_ASSIGN) return "addSet";
	    if (op == SUB_ASSIGN) return "subSet";
	    if (op == MUL_ASSIGN) return "mulSet";
	    if (op == DIV_ASSIGN) return "divSet";
	    if (op == MOD_ASSIGN) return "modSet";
	    if (op == BIT_AND_ASSIGN) return "bitAndSet";
	    if (op == BIT_OR_ASSIGN) return "bitOrSet";
	    if (op == BIT_XOR_ASSIGN) return "bitXorSet";
	    if (op == SHL_ASSIGN) return "shlSet";
	    if (op == SHR_ASSIGN) return "shrSet";
	    if (op == USHR_ASSIGN) return "ushrSet";
	    throw new InternalCompilerError("Unknown assignment operator");
	}
	
	MethodInstance mi;
	
	MethodInstance methodInstance() {
	    return mi;
	}
	SettableAssign_c methodInstance(MethodInstance mi) {
	    SettableAssign_c n = (SettableAssign_c) copy();
	    n.mi = mi;
	    return n;
	}
	
	@Override
	public Assign typeCheckLeft(ContextVisitor tc) throws SemanticException {
	    X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
	    X10TypeSystem xts = ts;

	    List<Type> argTypes = new ArrayList<Type>();
	    for (Expr e : index) {
		argTypes.add(e.type());
	    }
	    argTypes.add(right.type());
	    
	    Name methodName = Name.make(opString(op));
	    MethodMatcher methodMatcher = xts.MethodMatcher(array.type(), methodName, argTypes);

	    // Check if there is a method with the appropriate name and type with the left operand as receiver.   
	    try {
		X10MethodInstance mi = xts.findMethod(array.type(), methodMatcher, tc.context().currentClassDef());
		if (! mi.flags().isStatic())
		    return methodInstance(mi);
		throw new SemanticException("Cannot assign to element of " + array.type() + "; " + mi + " cannot be static.", position()); 
	    }
	    catch (SemanticException e) {
		// Cannot find the method.  Fall through.
		throw new SemanticException("Cannot assign to element of " + array.type() + "; " + e.getMessage(), position()); 
	    }
	}	
	
	public Term firstChild() {
	    return array;
	}
	
	protected void acceptCFGAssign(CFGBuilder v) {
		v.visitCFG(array, listChild(index, right()), ENTRY);
		v.visitCFGList(index, right(), ENTRY);
		v.visitCFG(right(), this, EXIT);
	}
	protected void acceptCFGOpAssign(CFGBuilder v) {
	    v.visitCFG(array, listChild(index, right()), ENTRY);
	    v.visitCFGList(index, right(), ENTRY);
	    v.visitCFG(right(), this, EXIT);
	}
	
	public List throwTypes(TypeSystem ts) {
		List l = new ArrayList(super.throwTypes(ts));
		l.add(ts.NullPointerException());
		l.add(ts.OutOfBoundsException());
		return l;
	}

	public String toString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append(array.toString());
	    sb.append("(");
	    String sep = "";
	    for (Expr e : index) {
		sb.append(sep);
		sep = ", ";
		sb.append(e);
	    }
	    sb.append(") ");
	    sb.append(op);
	    sb.append(" ");
	    sb.append(right.toString());
	    return sb.toString();
	}
    
    /** Write the expression to an output file. */
  public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
      Type at = array.type();

      printSubExpr(array, w, tr);
      w.write ("(");
      w.begin(0);

      for(Iterator i = index.iterator(); i.hasNext();) {
      	Expr e = (Expr) i.next();
      	print(e, w, tr);
          if (i.hasNext()) {
              w.write(",");
              w.allowBreak(0, " ");
      }
      }
      
      w.write(" ");
      w.write(opString(op));
      w.write(" ");
      
      print(right, w, tr);
      
      w.end();
     
      w.write (")");
 	}
  
	
	/** Write the expression to an output file. */
	public void translate(CodeWriter w, Translator tr) {
    Type at = array.type();

     Type result = X10TypeMixin.getParameterType(at, 0);
     if (result != null) {
     	w.write("((");
     	print(new CanonicalTypeNode_c(Position.COMPILER_GENERATED, Types.ref(result)), w, tr);
     	w.write(")");
     }
     printSubExpr(array, w, tr);
     w.write (".set(");
     w.begin(0);

     for(Iterator i = index.iterator(); i.hasNext();) {
     	Expr e = (Expr) i.next();
     	print(e, w, tr);
         
             w.write(",");
             w.allowBreak(0, " ");
     }
     
     print(right, w, tr);
     
     w.end();
    
     w.write (")");
     if (result != null) { w.write (")");}
	}

}
