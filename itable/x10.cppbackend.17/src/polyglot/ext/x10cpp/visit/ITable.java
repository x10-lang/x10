package polyglot.ext.x10cpp.visit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.types.Context;
import polyglot.types.MethodInstance;
import polyglot.types.Type;
import polyglot.util.CodeWriter;

/**
 * A class to encapsulate details of how the methods
 * of an interface are mapped to itable entries and
 * various code-generation details of how itables
 * are implemented.
 */
public final class ITable {
	private static final HashMap<X10ClassType, ITable> cachedITables = new HashMap<X10ClassType, ITable>();

	private final X10ClassType interfaceType;
	private final MethodInstance[] methods;
	private final boolean hasOverloadedMethods;

	/**
	 * Construct the ITable instance for the given X10 interface type
	 * @param interfaceType The interface for which to build the ITable
	 */
	private ITable(X10ClassType interfaceType) {
		assert interfaceType.flags().isInterface() : "Cannot create an ITable for a non-interface type";
		this.interfaceType = interfaceType;
		methods = collectMethods(interfaceType);
		Arrays.sort(methods, new MethodComparator());
		boolean foundOverload = false;
		for (int i=1; i<methods.length; i++) {
			foundOverload |= methods[i-1].name().toString().equals(methods[i].name().toString());
		}
		hasOverloadedMethods = foundOverload;
	}

	private static MethodInstance[] collectMethods(X10ClassType interfaceType) {
		ArrayList<MethodInstance> uniqueMethods = new ArrayList<MethodInstance>();
		uniqueMethods.addAll(interfaceType.methods());

		for (X10ClassType superInterface : allImplementedInterfaces(interfaceType)) {
			for (MethodInstance newMethod : superInterface.methods()) {
				boolean duplicate = false;
				for (MethodInstance oldMethod : uniqueMethods) {
					if (MethodComparator.compareTo(oldMethod, newMethod) == 0) {
						duplicate = true;
						break;
					}
				}
				if (!duplicate) {
					uniqueMethods.add(newMethod);
				}
			}
		}

		return uniqueMethods.toArray(new MethodInstance[uniqueMethods.size()]);
	}

	/**
	 * Find or construct the ITable instance for the argument X10 interface type.
	 */
	public static ITable getITable(X10ClassType interfaceType) {
		ITable ans = cachedITables.get(interfaceType);
		if (ans == null) {
			ans = new ITable(interfaceType);
			cachedITables.put(interfaceType, ans);
		}
		return ans;
	}

	/**
	 * Return a list of all interfaces directly and indirectly
	 * implemented by the argument class, excluding x10.lang.Object.
	 * If c is an interface, this method does not include c in the result!
	 */
	public static List<X10ClassType> allImplementedInterfaces(X10ClassType c) {
		ArrayList<X10ClassType> ans =  new ArrayList<X10ClassType>();
		allImplementedInterfaces(c, ans);
		return ans;
	}

	private static void allImplementedInterfaces(X10ClassType c, ArrayList<X10ClassType> l) {
		X10TypeSystem_c xts = (X10TypeSystem_c) c.typeSystem();
		Context context = xts.createContext();
		if (c.typeEquals(xts.Object(), context)) {
			return;
		}

		for (X10ClassType old : l) {
			if (c.typeEquals(old, context)) {
				return; /* Already been here */
			}
		}

		if (c.flags().isInterface()) {
			l.add(c);
		}

		if (c.superClass() != null) {
			allImplementedInterfaces((X10ClassType)X10TypeMixin.baseType(c.superClass()), l);
		}

		for (Type parent : c.interfaces()) {
			allImplementedInterfaces((X10ClassType)X10TypeMixin.baseType(parent), l);
		}
	}

	/**
	 * @return the canonically ordered array of MethodDecls that
	 * are included in the itable for this interface.  This is
	 * a set union of the methods directly declared by the interface and
	 * methods inherited from its superinterfaces.
	 */
	public MethodInstance[] getMethods() {
		return methods;
	}

	/**
	 * @return the interfaceType that this itable is being used to implement.
	 */
	public X10ClassType getInterface() {
		return interfaceType;
	}

	/**
	 * @return true if the ITable contains two methods with the same name
	 *  that are statically overloaded.  If true, then when declaring the
	 *  iTable we will need to do additional name mangling to disambiguate
	 *  the C++ names used for the elements of the ITable.
	 */
	public boolean hasOverloadedMethods() {
		return hasOverloadedMethods;
	}

	public String mangledName(MethodInstance meth) {
		if (hasOverloadedMethods) {
			for (int i=0; i<methods.length; i++) {
				if (MethodComparator.compareTo(methods[i], meth) == 0) {
					return "_m"+i+"__"+Emitter.mangled_method_name(meth.name().toString());
				}
			}
			assert false : "Method "+meth+" is not a member of interface "+interfaceType;
			return Emitter.mangled_method_name(meth.name().toString());
		} else {
			return Emitter.mangled_method_name(meth.name().toString());
		}
	}

	public void emitFunctionPointerDecl(CodeWriter cw, Emitter emitter, MethodInstance meth, String templateType) {
		String returnType = emitter.translateType(meth.returnType(), true);
		String name = mangledName(meth);
		cw.write(returnType+" ("+templateType+"::*"+name+") (");
		boolean first = true;
		for (Type f : meth.formalTypes()) {
			if (!first) cw.write(", ");
			first = false;
			cw.write(emitter.translateType(f, true));
		}
		cw.write(")");
	}

	public void emitForClass(X10ClassType cls, int itableNum, Emitter emitter, CodeWriter h, CodeWriter sw) {
		String interfaceCType = emitter.translateType(interfaceType, false);
		String clsCType = emitter.translateType(cls, false);
		boolean doubleTemplate = cls.typeArguments().size() > 0 && interfaceType.typeArguments().size() > 0;
		String itableCType = interfaceCType+(doubleTemplate ? "::template" : "::")+" itable<"+clsCType+" >";

		h.write((doubleTemplate ? "static typename " : "static ")+itableCType+" _it"+itableNum+";"); h.newline();

		// Generate the itable initialization
		if (!cls.typeArguments().isEmpty()) {
			emitter.printTemplateSignature(cls.typeArguments(), sw);
		}
		sw.write((doubleTemplate ? "typename ":"")+itableCType+" "+emitter.translateType(cls,false)+"::_it"+itableNum+" = ");
		sw.write(interfaceCType+"::itable(");
		for (int i=0, numMethods = methods.length; i<numMethods; i++) {
			if (i > 0) sw.write(", ");
			sw.write("&"+clsCType+"::"+emitter.mangled_method_name(methods[i].name().toString()));
		}
		sw.write(");"); sw.newline();
	}

	/**
	 * Helper class to impose a canonical ordering on the methods of an interface.
	 */
	private static final class MethodComparator implements Comparator<MethodInstance> {
		public int compare(MethodInstance m1, MethodInstance m2) {
			return compareTo(m1, m2);
		}

		public static int compareTo(MethodInstance m1, MethodInstance m2) {
			int nameCompare = m1.name().toString().compareTo(m2.name().toString());
			if (nameCompare != 0) return nameCompare;
			// Statically overloaded method.  Order by comparing function signatures.
			List<Type> m1Formals = m1.formalTypes();
			List<Type> m2Formals = m2.formalTypes();
			if (m1Formals.size() < m2Formals.size()) return -1;
			if (m1Formals.size() > m2Formals.size()) return 1;
			// Have same number of formal parameters; impose arbitrary order via toString of formals
			Iterator<Type> i1 = m1Formals.iterator();
			Iterator<Type> i2 = m2Formals.iterator();
			while (i1.hasNext()) {
				Type f1 = i1.next();
				Type f2 = i2.next();
				int fcompare = f1.toString().compareTo(f2.toString());
				if (fcompare != 0) return fcompare;
			}
			// Formals all equal. Use return type as final comparison.
			// TODO:  Does X10 also allow method overloading based on constraints or # of type parameters?
			return m1.returnType().toString().compareTo(m2.returnType().toString());
		}
	}

}
