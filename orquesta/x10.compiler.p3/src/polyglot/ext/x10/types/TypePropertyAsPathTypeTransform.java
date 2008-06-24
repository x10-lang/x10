package polyglot.ext.x10.types;

import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Transformation;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XVar;

public class TypePropertyAsPathTypeTransform implements
		Transformation<TypeProperty, Type> {

	public Type transform(TypeProperty def) {
		X10TypeSystem xts = (X10TypeSystem) def.typeSystem();
		try {
			XVar this_ = xts.xtypeTranslator().transThis(def.container().get());
			return PathType_c.pathBase(def.asType(), this_, Types.get(def.container()));
		}
		catch (SemanticException e) {
			throw new InternalCompilerError(e);
		}
	}
}
