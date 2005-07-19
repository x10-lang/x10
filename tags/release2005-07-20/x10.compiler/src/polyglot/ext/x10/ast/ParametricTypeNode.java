/*
 * Created by vj on Jan 9, 2005
 *
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.TypeNode;

/**
 * @author vj Jan 9, 2005
 * @author Christian Grothoff
 */
public interface ParametricTypeNode extends TypeNode {
	TypeNode base();
	DepParameterExpr parameter();
	GenParameterExpr typeParameter();


}
