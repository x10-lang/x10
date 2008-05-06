/**
 * 
 */
package polyglot.ext.x10.types;

import polyglot.ext.x10.types.constr.C_Special_c;
import polyglot.ext.x10.types.constr.C_Var;

public interface PathType extends X10Type {
	C_Var base();
	PathType base(C_Var v);

	TypeProperty property();
	PathType property(TypeProperty v);
}