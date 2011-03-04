/*
 * Created on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.ClassBody;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.ast.ClassDecl_c;
import polyglot.types.Flags;
import polyglot.util.Position;

/**
 * @author vj
 *
 * 
 */
public class ValueClassDecl_c extends ClassDecl_c implements ValueClassDecl {

	/**
	 * @param pos
	 * @param flags
	 * @param name
	 * @param superClass
	 * @param interfaces
	 * @param body
	 */
	public ValueClassDecl_c(Position pos, Flags flags, String name,
			TypeNode superClass, List interfaces, ClassBody body) {
		super(pos, flags, name, superClass, interfaces, body);
		
	}

}
