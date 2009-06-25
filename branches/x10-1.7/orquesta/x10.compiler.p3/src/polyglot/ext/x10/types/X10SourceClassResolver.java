package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.frontend.Compiler;
import polyglot.frontend.ExtensionInfo;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.LazyRef;
import polyglot.types.Matcher;
import polyglot.types.MethodDef;
import polyglot.types.Named;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Name;
import polyglot.types.SourceClassResolver;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.types.TypeSystem_c.TypeMatcher;
import polyglot.types.reflect.ClassFileLoader;
import polyglot.util.StringUtil;

public class X10SourceClassResolver extends SourceClassResolver {
    Map<String, String> classMap;
    Map<String, String> revMap;

	public X10SourceClassResolver(Compiler compiler, ExtensionInfo ext, String classpath, ClassFileLoader loader, boolean allowRawClasses,
			boolean compileCommandLineOnly, boolean ignoreModTimes) {
		super(compiler, ext, classpath, loader, allowRawClasses, compileCommandLineOnly, ignoreModTimes);

	        X10TypeSystem ts = (X10TypeSystem) this.ts;
	        
		revMap = new HashMap<String, String>();

		// These classes might be mentioned in raw java class files; we need to map them to the corresponding
		// x10 class.
		// Note: arrays are also rewritten.
		revMap.put("java.lang.Object", "x10.lang.Object");
		revMap.put("java.lang.String", "x10.lang.String");
		revMap.put("java.lang.Class", "x10.lang.Class");
		revMap.put("java.lang.Throwable", "x10.lang.Throwable");
		revMap.put("java.lang.Exception", "x10.lang.Exception");
		revMap.put("java.lang.Error", "x10.lang.Error");
		revMap.put("java.lang.RuntimeException", "x10.lang.RuntimeException");
		revMap.put("java.lang.NullPointerException", "x10.lang.NullPointerException");
		revMap.put("java.lang.ArrayIndexOutOfBoundsException", "x10.lang.ArrayIndexOutOfBoundsException");
		revMap.put("java.lang.ArithmeticException", "x10.lang.ArithmeticException");
		revMap.put("java.lang.Cloneable", "x10.lang.Cloneable");
		revMap.put("java.io.Serializable", "x10.io.Serializable");
		revMap.put("java.lang.ClassCastException", "x10.lang.ClassCastException");
		revMap.put("java.lang.ArrayStoreException", "x10.lang.ArrayStoreException");
		revMap.put("java.lang.System", "x10.lang.System");

		// Load the java equivalent for these classes.
		// The classes need to be baked in to get the compiler bootstrapped.
		// The problem: x10.lang.Object -> x10.lang.Int (in hashCode) -> x10.lang.Object (supertype)
		// x10.lang.Object is not yet in the system resolver cache when ts.Object() is invoked again.
		classMap = new HashMap<String, String>();
//		classMap.put("x10.lang.Object", "java.lang.Object");
	}

	@Override
	public Named find(QName name) throws SemanticException {
		X10TypeSystem ts = (X10TypeSystem) this.ts;
		
		String revName = revMap.get(name.toString());
		if (revName != null) {
	            return find(QName.make(revName));
	        }
	        
		if (name.equals(QName.make("x10.lang.Box"))) return (Named) ts.Box();
		if (name.equals(QName.make("x10.lang.Void"))) return (Named) ts.Void();
	
		// Change java.lang.Object to x10.lang.Object.
		QName newName = QName.make(classMap.get(name.toString()));
		
		if (newName != null) {
		    Named n = ts.systemResolver().check(newName);
		    if (n == null) {
		        n = super.find(newName);
		    }

		    if (n instanceof X10ParsedClassType) {
		        X10ParsedClassType ct = (X10ParsedClassType) n;
		        X10ClassDef cd = ct.x10Def();

			QName newPackage = name.qualifier();
			cd.setPackage(Types.ref(ts.packageForName(newPackage)));
			
			ts.systemResolver().install(newName, n);
			ts.systemResolver().install(name, n);
			
			// Modify x10.lang.Object from java.lang.Object:
			// 1. Object is an interface.
			// 2. Object has only equals, hashCode, toString methods.
			if (name.equals(QName.make("x10.lang.Object"))) {
			    cd.flags(cd.flags().Interface());

			    List<MethodDef> methods = new ArrayList<MethodDef>();
			    for (MethodDef m : cd.methods()) {
				if (m.name().equals(Name.make("equals")))
				    methods.add(m);
				if (m.name().equals(Name.make("hashCode")))
				    methods.add(m);
				if (m.name().equals(Name.make("toString")))
				    methods.add(m);
				m.setFlags(m.flags().clearNative().Abstract());
			    }
			    
			    cd.setMethods(methods);
			}
			
			// Null the superclass of interfaces.
			// Add x10.lang.Object to the interface list of every class.
			if (cd.flags().isInterface()) {
			    cd.superType(null);
			}
			else {
			    cd.addInterface(Types.ref(ts.Object()));
			}

			// Fix the super type to be x10.lang.Ref rather than java/x10.lang.Object
			if (cd.superType() != null) {
			    final X10ClassDef xcd = cd;
			    final Ref<? extends Type> oldSup = cd.superType();
			    final X10TypeSystem xts = ts;
			    final LazyRef<Type> sup = Types.<Type>lazyRef(null);
			    sup.setResolver(new Runnable() {
				public void run() {
				    Type actual = Types.get(oldSup);
				    if (actual instanceof ClassType) {
					ClassDef acd = ((ClassType) actual).def();
					if (acd.fullName().equals(QName.make("x10.lang.Object")))
					    actual = xts.Ref();
				    }
				    sup.update(actual);
				}
			    });
			    cd.superType(sup);
			}
		    }

		    return n;
		}

		return super.find(name);
	}
}
