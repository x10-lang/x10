package polyglot.ext.x10.types;

import polyglot.types.TypeSystem;
import polyglot.types.UnknownType_c;

public class X10UnknownType_c extends UnknownType_c implements X10UnknownType {
	protected X10UnknownType_c() {}

	public X10UnknownType_c(TypeSystem ts) {
		super(ts);
	}

	public boolean safe() {
		return false;
	}

	public String toString() {
		return super.toString();
	}

}
