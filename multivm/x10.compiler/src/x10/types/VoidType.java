package x10.types;

import polyglot.types.Resolver;
import polyglot.types.Type_c;
import polyglot.types.Name;
import polyglot.types.JavaPrimitiveType;
import polyglot.types.QName;
import polyglot.types.Resolver;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.types.Type_c;
import polyglot.util.CodeWriter;

public class VoidType extends Type_c {
	private static final long serialVersionUID = -1026975473924276266L;

	protected Name name = Name.make("void");
	public VoidType(TypeSystem ts) {
		super(ts);
	}

	public boolean isX10Struct() {
		return false;
	}
	public boolean isGloballyAccessible() {
		return true;
	}

	public String typeToString() {
		return name.toString();
	}

	public String translate(Resolver c) {
		return name.toString();
	}

	public boolean isJavaPrimitive() { return false; }
	public JavaPrimitiveType toPrimitive() { return null; }
	
	public int hashCode() {
		return name.hashCode();
	}

	public boolean equalsImpl(TypeObject t) {
		if (t instanceof VoidType) {
			return true;
		}
		return false;
	}

	public String wrapperTypeString(TypeSystem ts) {
		return ts.wrapperTypeString(this);
	}

	public Name name() {
		return name;
	}

	public QName fullName() {
		return QName.make(null, name());
	}
	public void print(CodeWriter w) {
		w.write(name().toString());
	}
	public String typeName() { 
		return toString();
	}

}
