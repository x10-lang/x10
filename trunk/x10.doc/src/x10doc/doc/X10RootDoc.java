package x10doc.doc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.ConstructorDef;
import polyglot.types.FieldDef;
import polyglot.types.MethodDef;
import polyglot.types.Package;
import polyglot.types.Ref;
import x10.types.ClosureType;
import x10.types.ConstrainedType;
import x10.types.ParameterType;
import x10.types.ParametrizedType;
import x10.types.TypeDef;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10ConstructorDef;
import x10.types.X10Def;
import x10.types.X10FieldDef;
import x10.types.X10MethodDef;
import x10.types.X10ParsedClassType;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;

public class X10RootDoc extends X10Doc implements RootDoc {
	HashMap<String, X10ClassDoc> specClasses;
	HashMap<String, X10PackageDoc> specPackages;
	HashMap<String, X10ClassDoc> otherClasses;
	HashMap<String, X10PackageDoc> otherPackages;
	HashMap<String, X10Type> primitiveTypes;
	private String outputDir;
	
	public static boolean printSwitch = false;

	private static X10RootDoc globalRootDoc;
	public static X10RootDoc getRootDoc(String outputDir) {
		if (globalRootDoc != null) {
			assert (globalRootDoc.outputDir.equals(outputDir)) : "getRootDoc called with a different output directory";
		} else {
			globalRootDoc = new X10RootDoc(outputDir);
		}
		return globalRootDoc;
	}

	public static X10RootDoc getRootDoc() {
		assert (globalRootDoc != null) : "getRootDoc: rootDoc not set.";
		return globalRootDoc;
	}

	public X10RootDoc(String outputDir) {
		super("");
		this.specClasses = new HashMap<String, X10ClassDoc>();
		this.specPackages = new HashMap<String, X10PackageDoc>();
		this.otherClasses = new HashMap<String, X10ClassDoc>();
		this.otherPackages = new HashMap<String, X10PackageDoc>();
		this.primitiveTypes = new HashMap<String, X10Type>();
		this.outputDir = outputDir;
	}

	public static String classKey(X10ClassDef cd) {
		return cd.fullName().toString();
	}

	/**
	 * Returns a ClassDoc object for the given X10ClassDef object. If such an object exists as an unspecified 
	 * class, then returns that object after updating fields such as comments if required. Otherwise adds and returns 
	 * a new X10ClassDoc object. Note that the class cannot pre-exist as a specified class, because visits to specified 
	 * classes that result in this method call, occur exactly once. Specified classes are the ones in source files 
	 * specified on the command line or occurring in specified packages. This method is expected to be called from 
	 * X10DocGenerator.visit(X10ClassDecl_c).
	 */
	public X10ClassDoc getSpecCLass(X10ClassDef classDef, X10ClassDoc containingClass, String comments) {
		X10ClassDoc cd = null;
		String className = classKey(classDef);
		System.out.println("X10RootDoc.addClass(...): className = " + className);

		assert(!specClasses.containsKey(className)) : "X10RootDoc.addSpecClass(" + className + 
			",...): multiple attempts to create specified ClassDoc";

		boolean inOther = otherClasses.containsKey(className);
		if (!inOther) {
			cd = new X10ClassDoc(classDef, containingClass, comments);
			specClasses.put(className, cd);

			cd.initialize();
			cd.setIncluded(true);

//			X10PackageDoc containingPackage = getPackage(classDef.package_());
//			cd.setPackage(containingPackage);
//			containingPackage.addClass(cd);
//			// obtain ClassDoc and Type objects for superclass
//			Ref<? extends polyglot.types.Type> reft = classDef.superType();
//			polyglot.types.Type t = ((reft==null) ? null : reft.get());
//			X10ClassDef cdef = (X10ClassDef) ((t == null) ? null : t.toClass().def());
//			X10ClassDoc superClass = getUnspecClass(cdef);
//			Type superType = getType(t);
//			cd.setSuperclass(superClass);
//			cd.setSuperclassType(superType);
//			addInterfaces(classDef, cd);

			return cd;
		}
		else {
			cd = otherClasses.remove(className);
			specClasses.put(className, cd);
			assert(containingClass == cd.containingClass) : "X10RootDoc.addClass(" + className + 
				",...): mismatch between existing and given containingClass";
			String existingCmnt = cd.commentText();
			assert(existingCmnt.equals("") || existingCmnt.equals(comments)) : "X10RootDoc.addClass(" + 
				className + ",...): mismatch between existing and given comments";
		
			cd.setIncluded(true);
			cd.setRawCommentText(comments); // may need to add class invariant, type guard also later
			return cd;
		}
	}
	
	public void addInterfaces(X10ClassDef classDef, X10ClassDoc classDoc) {
		for (Ref<? extends polyglot.types.Type> ref: classDef.interfaces()) {
			classDoc.addInterface(getUnspecClass((X10ClassDef) ref.get().toClass().def()));
		}
	}
	
	public void addPackage(X10PackageDoc pd) {
		this.specPackages.put(pd.name(), pd);
	}

	public X10PackageDoc createPackage(String pkgName) {
		X10PackageDoc pd = new X10PackageDoc(pkgName);
		otherPackages.put(pkgName, pd);
		return pd;
	}

	public void makePackageIncluded(String pkgName) {
		assert(otherPackages.containsKey(pkgName)) : 
			"X10RootDoc.includePackage: non-existent package " + pkgName;
		assert(!specPackages.containsKey(pkgName)) :
			"X10RootDoc.includePackage: attempting to include specified package " + pkgName;
		specPackages.put(pkgName, otherPackages.remove(pkgName));
	}
	
	public X10Type getPrimitiveType(polyglot.types.Type t) {
		String typeName = t.toString();
		if (primitiveTypes.containsKey(typeName)) {
			return primitiveTypes.get(typeName);
		}
		X10Type xt = new X10Type(t);
		primitiveTypes.put(typeName, xt);
		return xt;
	}
	
	public Type getType(polyglot.types.Type t) {
		return getType(t, null); 
	}

	/**
	 * Return a com.sun.javadoc.Type object for the given polyglot.types.Type object. 
	 * @param t 
	 * @param methodTypeVars Type variables defined by the method or constructor, on behalf of which this call is made to get param and return types.
	 * @return ClassDoc or TypeVariable or ParameterizedType or X10Type (for void) object
	 */
	public Type getType(polyglot.types.Type t, X10TypeVariable[] methodTypeVars) {
		if (t == null) return null;
		// System.out.println("X10RootDoc.getType(" + t +"): t.getClass() = " + t.getClass());
		if (t.isPrimitive()) {
			// System.out.println("Primitive X10Type returned.");
			return getPrimitiveType(t);
		}
		X10TypeSystem ts = (X10TypeSystem) t.typeSystem();
		if (ts.isParameterType(t)) {
			// TODO: get the constraints
			ParameterType p = (ParameterType) X10TypeMixin.baseType(t);
			// class, interface, method, or constructor that defines the type parameter
			X10Def owner = (X10Def) p.def().get(); 
			if (owner instanceof X10ClassDef) {
				X10ClassDoc cd = (X10ClassDoc) classNamed(classKey((X10ClassDef) owner));
				return cd.getTypeVariable(p);
			}
//			else if (owner instanceof X10ConstructorDef) {
//				X10ConstructorDef cdef = (X10ConstructorDef) owner;
//				X10ClassDoc cd = (X10ClassDoc) classNamed(classKey((X10ClassDef) cdef.container().get().toClass().def()));
//				X10ConstructorDoc constrDoc = cd.getConstructor(cdef);
//				return constrDoc.getTypeVariable(p);
//				
//			}
			else if (owner instanceof X10MethodDef || owner instanceof X10ConstructorDef || owner instanceof TypeDef) {
				assert(methodTypeVars != null) : "X10RootDoc.getType(" + t + 
				", null): expects non-null array of type variables defined by method/constructor" 
				+ owner;
				return findTypeVariable(p, methodTypeVars);
			  // X10MethodDef mdef = (X10MethodDef) owner;
			  // X10ClassDoc cd = (X10ClassDoc) classNamed(classKey((X10ClassDef) mdef.container().get().toClass().def()));
//			  MethodDoc[] methods = cd.methods();
//			  System.out.print("X10RootDoc.getType: cd.methods() = [");
//			  for (MethodDoc mdoc: methods) {
//				  X10MethodDef mdef2 = ((X10MethodDoc) mdoc).getMethodDef();
//				  System.out.print(mdef2.signature() + ", ");
//			  }
//			  System.out.println("]");
//			  for (MethodDoc mdoc: methods) {
//				  X10MethodDef mdef2 = ((X10MethodDoc) mdoc).getMethodDef();
//				  if (mdef == mdef2) {
//					  System.out.println("X10RootDoc.getType: " + mdef.signature() + " == " + 
//							  mdef2.signature());
//				  }	
//				  if (mdef.equals(mdef2)) {
//					  System.out.println("X10RootDoc.getType: " + mdef.signature() + 
//							  ".equals(" + mdef2.signature() + ")");
//		    	  }
//			  }
     		  // X10MethodDoc md = cd.getMethod(mdef);
			  // return md.getTypeVariable(p);
			}
			else {
				assert(false) : "getType(" + t + "): unexpected owner type";
			}
		}
		// if (t.typeEquals(X10TypeMixin.baseType(t), t.typeSystem().emptyContext())) {
//		if (classDef.typeParameters().size() == 0) {
//			System.out.println("X10ClassDoc returned.");
//			return getUnspecClass(classDef);
//		}
//		if (t instanceof ParametrizedType) {
//			return new X10ParameterizedType((x10.types.X10Type)t);
//		}
		X10ClassDef classDef = (X10ClassDef) t.toClass().def();
		if (t instanceof X10ParsedClassType) {
			// a ClosureType is an X10ParsedClassType where the type arguments (params) are 
			// the types of the closure's parameters and the return type of the closure
			// earlier test: "if (((X10ParsedClassType)t).typeArguments().size() > 0)"; this earlier test 
			// includes all closures except closures with 0 arguments, hence the instanceof test
			if ((t instanceof ClosureType) || (((X10ParsedClassType)t).typeArguments().size() > 0)) {
				return new X10ParameterizedType((x10.types.X10Type)t, methodTypeVars, false);
			}
			return getUnspecClass(classDef);
		}
		else if (t instanceof ConstrainedType) {
			polyglot.types.Type base = X10TypeMixin.baseType(t);
			if (base instanceof X10ParsedClassType) {
				if (((X10ParsedClassType)base).typeArguments().size() > 0) {
					// parameterized type with constraints
					return new X10ParameterizedType((x10.types.X10Type)t, methodTypeVars, true); 
				}
			}
			// non-parameterized type with constraints
			return new X10ParameterizedType((x10.types.X10Type)t);
		}
		return getUnspecClass(classDef);
	}

	public static X10TypeVariable findTypeVariable(ParameterType p, X10TypeVariable[] typeVars) {
		String name = p.name().toString();
		for (X10TypeVariable v: typeVars) {
			if (name.equals(v.typeName())) {
				return v;
			}
		}
		return null;
	}
	
	/**
	 * Creates a ClassDoc object for the given X10ClassDef object, after recursively creating the 
	 * ClassDoc objects for the containing classes. Ensures that no duplicate ClassDoc objects for
	 * the a class.
	 */
	public X10ClassDoc getUnspecClass(X10ClassDef classDef) {
		if (classDef == null)
			return null;

		String ckey = classKey(classDef);
		X10ClassDoc cd = (X10ClassDoc) classNamed(ckey);
		if (cd != null)
			return cd;
		
		Ref<? extends ClassDef> ref = classDef.outer();
		X10ClassDef contClassDef = ((ref==null) ? null : (X10ClassDef) ref.get());
		X10ClassDoc containingClass = getUnspecClass(contClassDef);

		// cd = createUnspecClass(classDef, containingClass);
		cd = new X10ClassDoc(classDef, containingClass, "");
		otherClasses.put(ckey, cd);

		cd.initialize();
		
//		X10PackageDoc containingPackage = getPackage(classDef.package_());
//		cd.setPackage(containingPackage);
//		containingPackage.addClass(cd);
//		// obtain ClassDoc and Type objects for superclass
//		Ref<? extends polyglot.types.Type> reft = classDef.superType();
//		polyglot.types.Type t = ((reft==null) ? null : reft.get());
//		X10ClassDef cdef = (X10ClassDef) ((t == null) ? null : t.toClass().def());
//		X10ClassDoc superClass = getUnspecClass(cdef);
//		Type superType = getType(t);
//		cd.setSuperclass(superClass);
//		cd.setSuperclassType(superType);
//		addInterfaces(classDef, cd);
		
		for (FieldDef fd: classDef.fields()) {
			// cd.addField(new X10FieldDoc(((X10FieldDef) fd), cd, ""));
			cd.updateField((X10FieldDef) fd, "");
		}
		for (ConstructorDef constrDef: classDef.constructors()) {
			// X10ConstructorDoc doc = new X10ConstructorDoc(((X10ConstructorDef) constrDef), cd, "");
			cd.updateConstructor((X10ConstructorDef) constrDef, "");
		}
		for (MethodDef md: classDef.methods()) {
			// X10MethodDoc doc = new X10MethodDoc(((X10MethodDef) md), cd, "");
			// update cd with a MethodDoc object for method md 
			cd.updateMethod(((X10MethodDef) md), "");
		}
		// inner classes may need to be added
		return cd;
	}

	/** 
	 * Return a ClassDoc for the specified or unspecified class or interface name.
	 */
	public ClassDoc classNamed(String arg0) {
		// System.out.println("RootDoc.classNamed(" + arg0 + ") called.");
		if (specClasses.containsKey(arg0)) {
			return specClasses.get(arg0);
		}
		if (otherClasses.containsKey(arg0)) {
			return otherClasses.get(arg0);
		}
		return null;
	}

	public ClassDoc getClass(X10ClassDef cd) {
		System.out.println("RootDoc.getClass(X10ClassDef " + cd + ") called.");
		return classNamed(classKey(cd));
	}

	/** 
	 * Return the included classes and interfaces in all packages.
	 */
	public ClassDoc[] classes() {
		System.out.println("RootDoc.classes() called.");
		return specClasses.values().toArray(new ClassDoc[0]);
	}

	/**
	 * Command line options.
	 */
	public String[][] options() {
		// TODO Auto-generated method stub
		System.out.println("RootDoc.options() called.");
		String[][] result = new String[][] {
				{ "-d", outputDir }
		};
		return result;
	}

	/**
	 * Return a PackageDoc for the specified package name.
	 */
	public PackageDoc packageNamed(String arg0) {
		// System.out.println("RootDoc.packageNamed() called.");
		if (specPackages.containsKey(arg0)) {
			return specPackages.get(arg0);
		}
		return otherPackages.get(arg0);
	}

	public X10PackageDoc getPackage(Ref<? extends Package> pkg) {
		String pkgName = ((pkg == null) ? "unknown" : pkg.toString());
		X10PackageDoc pd = (X10PackageDoc) packageNamed(pkgName);
		return ((pd == null) ? createPackage(pkgName) : pd); 
	}
	
	/**
	 * Return the classes and interfaces specified as source file names on the command line.
	 */
	public ClassDoc[] specifiedClasses() {
		System.out.println("RootDoc.specifiedClasses() called.");
		return specClasses.values().toArray(new ClassDoc[0]);
	}

	/**
	 * Return the packages specified  on the command line.
	 */
	public PackageDoc[] specifiedPackages() {
		System.out.println("RootDoc.specifiedPackages() called.");
		return specPackages.values().toArray(new PackageDoc[0]);
	}

	public void printError(String arg0) {
		// TODO Auto-generated method stub

	}

	public void printError(SourcePosition arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	public void printNotice(String arg0) {
		// TODO Auto-generated method stub

	}

	public void printNotice(SourcePosition arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	public void printWarning(String arg0) {
		// TODO Auto-generated method stub

	}

	public void printWarning(SourcePosition arg0, String arg1) {
		// TODO Auto-generated method stub

	}
	
	public String stringDocObjNames(Collection<? extends Doc> c) {
		int n = c.size();
		if (n == 0) 
			return "[]";
		if (n == 1)
			return "[" + c.iterator().next().name() + "]";
		
		Iterator<? extends Doc> iterator = c.iterator();
		String result = "[" + iterator.next().name();
		while (iterator.hasNext()){
			result += ", " + iterator.next().name();
		}
		result += "];";
		return result;
	}
		
	public void printStats() {
		System.out.println("X10RootDoc details:");
		System.out.println(specClasses.size() + " specified classes: " + 
				stringDocObjNames(specClasses.values()));	
		System.out.println(otherClasses.size() + " other classes: " +
				stringDocObjNames(otherClasses.values()));
		System.out.println(specPackages.size() + " specified packages: " + 
				stringDocObjNames(specPackages.values()));	
		System.out.println(otherPackages.size() + " other packages: " +
				stringDocObjNames(otherPackages.values()));
	}

//	 added by IP; not quite what is needed
//	public X10ClassDoc addUnspecClass(X10ClassDef classDef) {
//		// 
//		X10ClassDoc outer = null;
//		if (classDef.outer() != null) {
//			String className = classKey((X10ClassDef) classDef.outer().get());
//			outer = (X10ClassDoc) classNamed(className);
//		}
//		
//		return createUnspecClass(classDef, outer);
//		
//		// X10ClassDef cd; X10FieldDef fd; X10MethodDef md; X10ConstructorDef cnd;
//	}
//	
//	/**
//	 * Adds a basic X10ClassDoc object corresponding to the given X10ClassDef object for an unspecified 
//	 * class. Details of fields, constructors, methods, etc. are yet to be filled in.
//	 * CAUTION: This method should be called only after checking if an X10ClassDoc exists for the 
//	 * class of interest, i.e., after calling classNamed, or calling getUnspecClass instead of 
//	 * createUnspecClass
//	 */
//	private X10ClassDoc createUnspecClass(X10ClassDef classDef, X10ClassDoc containingClass) {
//		String className = classKey(classDef);
//		System.out.println("X10RootDoc.addUnspecClass(...) called for " + className);
//		
//		boolean inSpec = specClasses.containsKey(className);
//		boolean inOther = otherClasses.containsKey(className);
//		if (!inSpec && !inOther) {
//			X10ClassDoc cd = new X10ClassDoc(classDef, containingClass, "");
//			X10PackageDoc containingPackage = getPackage(classDef.package_());
//			cd.setPackage(containingPackage);
//			containingPackage.addClass(cd);
//			otherClasses.put(className, cd);
//			return cd;
//		}
//		else {
//			HashMap<String, X10ClassDoc> targetMap = (inSpec ? specClasses : otherClasses);
//			X10ClassDoc cd = targetMap.get(className);
//			assert(cd.containingClass() == containingClass) : ("X10RootDoc.addUnspecClass(" + className + 
//				",...): mismatch between existing and given containingClass.");
//			return cd;
//		}
//	}
}
