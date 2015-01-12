/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.util;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.Flags;

import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import x10.types.X10ClassType;
import x10.types.X10MethodDef;
import x10.types.MethodInstance;

public class HierarchyUtils {

	public static Set<ClassType> getSuperTypes(ClassType startingClass) {
		Set<ClassType> superTypes = new LinkedHashSet<ClassType>();
		ClassType previousType = startingClass;
		ClassType superType = toClass(startingClass.superClass());

		while (superType != null) {
			superTypes.add(superType);
			addInterfaces(previousType, superTypes);
			previousType = superType;
			superType = toClass(superType.superClass());
		}

		addInterfaces(previousType, superTypes);

		return superTypes;
	}

	public static Set<ClassType> getSuperClasses(ClassType startingClass) {
		Set<ClassType> superTypes = CollectionFactory.newHashSet();
		ClassType superType = toClass(startingClass.superClass());

		while (superType != null) {
			superTypes.add(superType);
			superType = toClass(superType.superClass());
		}

		return superTypes;
	}
	
	private static void addInterfaces(ClassType startingClass, Set<ClassType> interfaces) {
		for (Type type : startingClass.interfaces()) {
			interfaces.add(toClass(type));
			addInterfaces(toClass(type), interfaces);
		}
	}
	
	public static Set<ClassType> getInterfaces(Set<ClassType> classes) {
		Set<ClassType> interfaces = CollectionFactory.newHashSet();
		for (ClassType ct : classes) {
			addInterfaces(ct, interfaces);
		}

		return interfaces;
	}

	public static Set<MethodInstance> getMethods(Set<ClassType> classes) {
		return getMethods(classes, Flags.NONE);
	}

	public static Set<MethodInstance> getMethods(Set<ClassType> classes, Flags flags) {
		Set<MethodInstance> methods = CollectionFactory.newHashSet();
		for (ClassType ct : classes) {
			for (MethodInstance mi : ct.methods()) {
				if (mi.flags().contains(flags))
				{
					methods.add(mi);
				}
			}
		}

		return methods;
	}

	public static Set<MethodInstance> getImplementedMethods(Set<ClassType> classes) {
		Set<MethodInstance> methods = CollectionFactory.newHashSet();
		for (ClassType ct : classes) {
			for (MethodInstance mi : ct.methods()) {
				if (!mi.flags().isAbstract()) {
					methods.add(mi);
				}
			}
		}

		return methods;
	}

	public static boolean isMainMethod(X10MethodDef md, Context context) {
	    return isMainMethod(md.asInstance(), context);
	}

	public static boolean isMainMethod(MethodInstance mi, Context context) {
	    final TypeSystem ts = mi.typeSystem();
	    X10ClassType container = (X10ClassType) mi.container();
	    boolean result =
	        mi.flags().isPublic() &&
	        mi.flags().isStatic() &&
	        mi.name().toString().equals("main") &&
	        mi.returnType().isVoid() &&
	        mi.typeParameters().size() == 0 &&
	        mi.formalTypes().size() == 1 &&
	        ts.isSubtype(mi.formalTypes().get(0), ts.Rail(ts.String()), context);
	    return result;
	}
	
	private static ClassType toClass(Type t) {
	    return t == null ? null : t.toClass();
	}
}
