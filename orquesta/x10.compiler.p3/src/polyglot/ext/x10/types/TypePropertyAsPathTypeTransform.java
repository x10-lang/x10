package polyglot.ext.x10.types;

import polyglot.ext.x10.ast.X10Special;
import polyglot.ext.x10.types.constr.C_Special_c;
import polyglot.util.Transformation;

public class TypePropertyAsPathTypeTransform implements
		Transformation<TypeProperty, PathType> {

	public PathType transform(TypeProperty def) {
		PathType p = (PathType) def.asType();
		p = p.base(new C_Special_c(X10Special.THIS, p.base().type()));
		return p;
	}
}
