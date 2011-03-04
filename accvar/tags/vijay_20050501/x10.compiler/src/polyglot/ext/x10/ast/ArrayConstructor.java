/*
 * Created by vj on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Term;
import polyglot.ast.Block;
import polyglot.ast.Variable;
import polyglot.ast.TypeNode;
import polyglot.ast.Expr;


/** An immutable representation of an X10 Array constructor, which has
 * a basetype for the array, a distribution for the array, 
 * an (optional) index variable (whose type is the index type of the array),
 * and a Block.
 * 
 * @author vj Dec 9, 2004
 * 
 */
public interface ArrayConstructor extends Term {
	TypeNode arrayBaseType();
	Expr distribution();
	Block body();
	Variable formal();
	ArrayConstructor arrayBaseType( TypeNode t);
	ArrayConstructor distribution( Expr e);
	ArrayConstructor body(Block b);
	ArrayConstructor formal( Variable v);
	
	
	//TODO: vj Determine if this needs a CodeInstance as well.

}
