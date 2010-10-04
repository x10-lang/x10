/**
 * 
 */
package x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Expr_c;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import x10.errors.Errors;
import x10.types.X10Context;
import x10.types.X10ParsedClassType;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.checker.PlaceChecker;

/**
 * A representation of the syntactic form new Array[T]{ e1,..., en}.
 * Each ei must be of type T. The type of this expression is Array[T](0..n-1).
 * 
 * From an implementation point of view, an ArrayLiteral_c is simply a Tuple with
 * a user-specified type. It is translated in the Desugarer into a Tuple, to be
 * implemented by backends as appropriate.
 * @author vj
 *
 */
public class ArrayLiteral_c extends Expr_c implements ArrayLiteral {

	TypeNode array;
	TypeNode indexType;
	Tuple_c tuple;
	/**
	 * @param pos
	 */
	public ArrayLiteral_c(Position pos, TypeNode array, TypeNode indexType, Tuple_c tuple) {
		super(pos);
		this.array=array;
		this.indexType = indexType;
		this.tuple = tuple;
	}

	public Tuple_c tuple() {
		return tuple;
	}
	/* (non-Javadoc)
	 * @see polyglot.ast.Term#firstChild()
	 */
	public Term firstChild() {
		return indexType;
	}

	public TypeNode indexType() {
		return indexType;
	}

	 /* (non-Javadoc)
	 * @see polyglot.ast.Term_c#acceptCFG(polyglot.visit.CFGBuilder, java.util.List)
     * Visit this term in evaluation order.
     */
	@Override
    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
    	v.visitCFG(indexType, tuple, ENTRY);
    	v.visitCFG(tuple, this, EXIT);
        return succs;
    }
	  public Node visitChildren( NodeVisitor v ) {
		  TypeNode atn = (TypeNode) visitChild(this.array, v);
		  TypeNode tn = (TypeNode) visitChild( this.indexType, v );
		  Tuple_c t = (Tuple_c) visitChild(this.tuple, v);
		  if (atn != array || t != tuple || tn != indexType) {
			  ArrayLiteral_c n = (ArrayLiteral_c) copy();
			  n.array = atn;
			  n.tuple = t;
			  n.indexType = tn;
			  return n;
		  }
		  return this;
	  }
	  public Node typeCheck(ContextVisitor tc) throws SemanticException {
		  ArrayLiteral_c n = (ArrayLiteral_c) super.typeCheck(tc);
		  Type type = tuple.type();
		  Type iType = indexType.type();
		  X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
		 
		  String arrayTypeName = array.type().toString();
	      if (! (arrayTypeName.equals("x10.array.Array") || arrayTypeName.equals("Array")))
	    		Errors.issue(tc.job(),
			        new Errors.ArrayLiteralMustBeOfArrayType(array.type().toString(), n.position()));
	     
	      Type aType =  X10TypeMixin.typeArg(type,0);
		  if (! ts.isSubtype(aType, iType, tc.context()))
			  Errors.issue(tc.job(), 
			      new Errors.ArrayLiteralTypeMismatch(type, iType, n.position()));
		  Type resultType = X10TypeMixin.makeArrayRailOf(iType, n.tuple().arguments().size(), n.position());
		  
	      return n.type(resultType);
	    }
	    
	    public String toString() {
	    	return  "new Array[" + indexType + "]{ " + tuple.stringValue() + "}";
	    }
	   
	    /** Write the expression to an output file. */

	    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	    	w.write("new Array[");
	    	printBlock(indexType, w, tr);
	    	w.write("]");
	    	tuple.prettyPrint(w,tr);
	    	
	    }


}
