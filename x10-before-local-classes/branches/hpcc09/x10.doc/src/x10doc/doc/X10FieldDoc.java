package x10doc.doc;

import x10.types.X10FieldDef;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.SerialFieldTag;
import com.sun.javadoc.Type;

public class X10FieldDoc extends X10Doc implements FieldDoc {
	X10FieldDef fieldDef;
	X10ClassDoc containingClass;
	X10RootDoc rootDoc;
	
	public X10FieldDoc(X10FieldDef fd, X10ClassDoc containingClass, String comment) {
		super(comment);
		this.fieldDef = fd;
		this.containingClass = containingClass;
		this.rootDoc = X10RootDoc.getRootDoc();
	}
	
	@Override
	public String name() {
		return fieldDef.name().toString();
	}

	public Object constantValue() {
		return fieldDef.constantValue();
	}

	public String constantValueExpression() {
		return "";
	}

	@Override
	public boolean isField() {
		return true;
	}

	@Override
	public boolean isIncluded() {
		return true;
	}

	public boolean isTransient() {
		return fieldDef.flags().isTransient();
	}

	public boolean isVolatile() {
		return fieldDef.flags().isVolatile();
	}

	public SerialFieldTag[] serialFieldTags() {
		return new SerialFieldTag[0];
	}

	public Type type() {
		System.out.println("FieldDoc(" + name() + ").type() called.");
		// return new X10Type(fieldDef.type().get());
		return rootDoc.getType(fieldDef.type().get());
	}

	public boolean isSynthetic() {
		// TODO Auto-generated method stub
		return false;
	}

	public AnnotationDesc[] annotations() {
		return new AnnotationDesc[0];
	}

	public ClassDoc containingClass() {
		return containingClass;
	}

	public PackageDoc containingPackage() {
		return containingClass.containingPackage();
	}

	public boolean isFinal() {
		return fieldDef.flags().isFinal();
	}

	public boolean isPackagePrivate() {
		return fieldDef.flags().isPackage();
	}

	public boolean isPrivate() {
		return fieldDef.flags().isPrivate();
	}

	public boolean isProtected() {
		return fieldDef.flags().isProtected();
	}

	public boolean isPublic() {
		return fieldDef.flags().isPublic();
	}

	public boolean isStatic() {
		return fieldDef.flags().isStatic();
	}

	public int modifierSpecifier() {
		return X10Doc.flagsToModifierSpecifier(fieldDef.flags().flags());
	}

	public String modifiers() {
		return fieldDef.flags().toString();
	}

	public String qualifiedName() {
		String str = fieldDef.type().toString();
		System.out.println("FieldDoc.qualifiedName() called. fieldDef.type().toString() = " + str); 
		return str;
	}
}
