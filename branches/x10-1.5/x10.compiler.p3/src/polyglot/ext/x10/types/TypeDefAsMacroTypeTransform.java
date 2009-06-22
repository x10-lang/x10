/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package polyglot.ext.x10.types;

import polyglot.types.Type;
import polyglot.util.InternalCompilerError;
import polyglot.util.Transformation;

public class TypeDefAsMacroTypeTransform implements Transformation<TypeDef, Type> {
    public Type transform(TypeDef def) {
	X10TypeSystem xts = (X10TypeSystem) def.typeSystem();
	MacroType mt = def.asType();
	if (mt.container() != null && !mt.flags().isStatic()) {
	    throw new InternalCompilerError("non-static member typedefs are unimplemented");
	}
	return mt;
    }
}
