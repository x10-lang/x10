package x10doc.doc;

import java.util.ArrayList;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;

public class X10PackageDoc extends X10Doc implements PackageDoc {
	String name;
	ArrayList<X10ClassDoc> classes;
	X10RootDoc rootDoc;
	boolean included;

	public X10PackageDoc(String name) {
		super("");
		this.name = name;
		this.rootDoc = X10RootDoc.getRootDoc();
		this.classes = new ArrayList<X10ClassDoc>();
		this.included = false; // included is set to true when an included class is added to
		                       // the package for the first time
	}
	
	public void addClass(X10ClassDoc cd) {
		classes.add(cd);
		if (!included && cd.isIncluded()) {
			included = true;
			rootDoc.makePackageIncluded(name);
		}
	}
	
	@Override
	public String name() {
		return name;
	}
	
	public boolean isIncluded() {
		return included;
	}

	public ClassDoc[] allClasses() {
		System.out.println("PackageDoc.allClasses() called.");
		return classes.toArray(new X10ClassDoc[0]);
	}

	public ClassDoc[] allClasses(boolean arg0) {
		System.out.println("PackageDoc.allClasses() called.");
		return classes.toArray(new X10ClassDoc[0]);
	}

	public AnnotationTypeDoc[] annotationTypes() {
		// TODO Auto-generated method stub
		System.out.println("PackageDoc.annotationTypes() called.");
		return new AnnotationTypeDoc[0];
	}

	public AnnotationDesc[] annotations() {
		// TODO Auto-generated method stub
		System.out.println("PackageDoc.annotations() called.");
		return new AnnotationDesc[0];
	}

	public ClassDoc[] enums() {
		// TODO Auto-generated method stub
		System.out.println("PackageDoc.enums() called.");
		return new ClassDoc[0];
	}

	public ClassDoc[] errors() {
		// TODO Auto-generated method stub
		System.out.println("PackageDoc.errors() called.");
		return new ClassDoc[0];
	}

	public ClassDoc[] exceptions() {
		// TODO Auto-generated method stub
		System.out.println("PackageDoc.exceptions() called.");
		return new ClassDoc[0];
	}

	public ClassDoc findClass(String arg0) {
		// TODO Auto-generated method stub
		System.out.println("PackageDoc.findClass() called.");
		return null;
	}

	public ClassDoc[] interfaces() {
		// TODO Auto-generated method stub
		System.out.println("PackageDoc.interfaces() called.");
		return new ClassDoc[0];
	}

	public ClassDoc[] ordinaryClasses() {
		// TODO Auto-generated method stub
		System.out.println("PackageDoc.ordinaryClasses() called.");
		return allClasses();
	}

}
