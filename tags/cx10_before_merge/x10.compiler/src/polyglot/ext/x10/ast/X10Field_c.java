/*
 * Created by vj on May 23, 2005
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Call;
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.ast.Term;
import polyglot.ext.jl.ast.Call_c;
import polyglot.ext.jl.parse.Name;
import polyglot.ext.jl.ast.Field_c;
import polyglot.types.SemanticException;

import polyglot.util.Position;

import polyglot.visit.TypeChecker;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.ast.X10NodeFactory_c;


/** An immutable representation of an X10 Field access. It is the same as a Java
 * field access except for accesses of the field "location" for value types.
 * In this implementation such field accesses are implemented by the method call
 * x10.lang.Runtime.here().
 * @author vj May 23, 2005
 * 
 */
public class X10Field_c extends Field_c {

	
	/**
	 * @param pos
	 * @param target
	 * @param name
	 */
	public X10Field_c(Position pos, Receiver target, String name) {
		super(pos, target, name);
		
	}

	 public Node typeCheck(TypeChecker tc) throws SemanticException {
	 
	 	if (name.equals("location") && ((X10Type) target.type()).isValueType()) {
	 		return new X10NodeFactory_c().Here(position()).typeCheck(tc);
	 	}
	 	return super.typeCheck( tc );
	 }
}
