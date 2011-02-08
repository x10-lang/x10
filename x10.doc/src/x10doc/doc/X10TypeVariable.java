package x10doc.doc;

import x10.types.constraints.TypeConstraint;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;

public class X10TypeVariable extends X10Type implements TypeVariable {
	private ProgramElementDoc owner;
	private TypeConstraint typeGuard;

	public X10TypeVariable(polyglot.types.Type t, ProgramElementDoc owner) {
		super(t);
		this.owner = owner;
		this.typeGuard = null;
	}
	
	public void setTypeGuard(TypeConstraint c) {
		this.typeGuard = c;
	}
	
	@Override
	public ClassDoc asClassDoc() {
		// TODO: return ClassDoc{x10.lang.Object}, since X10 does not have type erasure
		if (owner instanceof ClassDoc) {
			return (ClassDoc) owner;
		}
		else if (owner instanceof ExecutableMemberDoc) {
			return ((ExecutableMemberDoc) owner).containingClass();
		}
		return null;
	}

	@Override
	public TypeVariable asTypeVariable() {
		return this;
	}

	@Override
	public String qualifiedTypeName() {
		// TODO Auto-generated method stub
		return super.qualifiedTypeName();
	}

	@Override
	public String simpleTypeName() {
		// TODO Auto-generated method stub
		return super.simpleTypeName();
	}

	@Override
	public String typeName() {
		// return pType.toString() + (typeGuard == null ? "" : typeGuard.toString());
		return pType.name().toString();
	}

	@Override
	public String toString() {
		return typeName();
	}

	public Type[] bounds() {
		// experimenting with a bound
		// Type[] result = new Type[1];
		// result[0] = (ClassDoc) owner;
		return new Type[0];
	}

	public ProgramElementDoc owner() {
		return owner;
	}

}
