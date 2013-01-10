package x10.types.constraints;

import polyglot.types.Type;
import polyglot.types.Types;
import x10.constraint.XDef;
import x10.constraint.XStringDef;

public class QualifierDef implements XDef<Type> {
	
	private Type type;

	public QualifierDef (Type t) { this.type = t; }

	@Override
	public Type resultType() {
		return type;
	}

	@Override
	public String getName() {
		return type.toString();
	}

	@Override
	public String toString() { return type.toString(); }
	
	@Override
	public boolean equals(Object o) {
		if (o==this) return true;
		if (!(o instanceof QualifierDef)) return false;
		QualifierDef oq = (QualifierDef) o;
		// Cannot think of a better way to compare them within a given constraint
		return oq.type == this.type;
	}

	public int hashCode() { return type.hashCode(); }
}
