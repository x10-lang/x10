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

package x10cpp.visit;

import static x10cpp.visit.SharedVarsMethods.chevrons;
import static x10cpp.visit.SharedVarsMethods.CLASS_TYPE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.types.Context;
import polyglot.types.LocalInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import x10.ast.ClosureCall;
import x10.types.ParameterType;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10MethodDef;
import x10.types.MethodInstance;
import polyglot.types.TypeSystem;

import x10cpp.types.X10CPPContext_c;

/**
 * A class to encapsulate details of how the methods
 * of an interface are mapped to itable entries and
 * various code-generation details of how itables
 * are implemented.
 */
public final class ITable {
	private final X10ClassType interfaceType;
	private final MethodInstance[] methods;
	private final boolean hasOverloadedMethods;
	private final boolean[] overloaded;

	/**
	 * Construct the ITable instance for the given X10 interface type
	 * @param interfaceType The interface for which to build the ITable
	 */
	public ITable(X10ClassType interfaceType) {
		assert interfaceType.flags().isInterface() : "Cannot create an ITable for a non-interface type";
		this.interfaceType = interfaceType;
		methods = collectMethods(interfaceType);
		Arrays.sort(methods, new MethodComparator());
		boolean foundOverload = false;
		overloaded = new boolean[methods.length];
		for (int i=1; i<methods.length; i++) {
			boolean ol = methods[i-1].name().toString().equals(methods[i].name().toString());
			if (ol) {
				overloaded[i-1] = true;
				overloaded[i] = true;
				foundOverload = true;
			}
		}
		hasOverloadedMethods = foundOverload;
	}

	private static MethodInstance[] collectMethods(X10ClassType interfaceType) {
	    TypeSystem xts = (TypeSystem)interfaceType.typeSystem();
		ArrayList<MethodInstance> uniqueMethods = new ArrayList<MethodInstance>();
		uniqueMethods.addAll(interfaceType.methods());

		for (X10ClassType superInterface : ((TypeSystem)interfaceType.typeSystem()).allImplementedInterfaces(interfaceType)) {
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
	
	/**
	 * @return true if the ITable contains no methods at all, false otherwise.
	 */
	public boolean isEmpty() {
	    return methods.length == 0;
	}

	public String mangledName(MethodInstance meth) {
		if (hasOverloadedMethods) {
			for (int i=0; i<methods.length; i++) {
				if (MethodComparator.compareTo(methods[i], meth) == 0) {
					if (overloaded[i]) {
						return "_m"+i+"__"+Emitter.mangled_method_name(meth.name().toString());
					} else {
						return Emitter.mangled_method_name(meth.name().toString());
					}
				}
			}
			assert false : "Method "+meth+" is not a member of interface "+interfaceType;
			return Emitter.mangled_method_name(meth.name().toString());
		} else {
			return Emitter.mangled_method_name(meth.name().toString());
		}
	}

	public void emitFunctionPointerDecl(CodeWriter cw, Emitter emitter, MethodInstance meth, String memberPtr, boolean includeName) {
		MethodInstance mi = (MethodInstance) meth;
		X10MethodDef md = mi.x10Def();
		Type rootReturnType = emitter.findRootMethodReturnType(md, null, mi);
		String returnType = Emitter.translateType(rootReturnType, true);
		String name = mangledName(meth);
		cw.write(returnType+" ("+memberPtr+"::*"+(includeName ? name : "")+") (");
		boolean first = true;
		for (Type f : meth.formalTypes()) {
			if (!first) cw.write(", ");
			cw.write(Emitter.translateType(f, true));
			first = false;
		}
		cw.write(")");
	}

	public void emitITableDecl(X10ClassType cls, int itableNum, Emitter emitter, CodeWriter h) {
		String interfaceCType = Emitter.translateType(interfaceType, false);
		X10ClassDef cd = cls.x10Def();
		boolean doubleTemplate = cd.typeParameters().size() > 0 && interfaceType.x10Def().typeParameters().size() > 0;
		h.write("static "+(doubleTemplate ? "typename ":"")+interfaceCType+
				(doubleTemplate ? "::template itable< ":"::itable< ")+Emitter.translateType(cls, false)+" > _itable_"+itableNum+";");
		h.newline();
	}

	public void emitITableInitialization(X10ClassType cls, int itableNum, MessagePassingCodeGenerator cg, 
	                                     CodeWriter h, CodeWriter sw, boolean emittedUsingDecl, X10CPPContext_c context) {
	    X10ClassDef cd = cls.x10Def();
	    if (cls.isX10Struct()) {
	        // For an interface implemented by a struct, we need to generate 
	        // an additional thunk class and itable for use by the IBox of the
	        // struct.
            String interfaceCType = Emitter.translateType(interfaceType, false);
            String clsCType = Emitter.translateType(cls, false, true);
            String thunkBaseType = Emitter.mangled_non_method_name(cd.name().toString());
            String thunkParams = "";
            if (cd.typeParameters().size() != 0) {
                String args = "";
                int s = cd.typeParameters().size();
                for (Type t: cd.typeParameters()) {
                    args += Emitter.translateType(t, true); // type arguments are always translated as refs
                    if (--s > 0)
                        args +=", ";
                }
                thunkParams = chevrons(args);
            }
            boolean doubleTemplate = cd.typeParameters().size() > 0 && interfaceType.x10Def().typeParameters().size() > 0;
            
            if (cd.package_() != null) {
                Emitter.openNamespaces(sw, cd.package_().get().fullName()); sw.newline();
            }            

            String thunkType = thunkBaseType + "_ibox"+itableNum;
            String parentCType = "::x10::lang::IBox"+chevrons(clsCType);
            String recvArg = "this->value";

            cg.emitter.printTemplateSignature(cd.typeParameters(), sw);
            sw.write("class "+thunkType+" : public "+parentCType+" {"); sw.newline();
            sw.write("public:"); sw.newline(4); sw.begin(0);
            sw.write("static "+(doubleTemplate ? "typename ":"")+interfaceCType+
                     (doubleTemplate ? "::template itable< ":"::itable< ")+thunkType+thunkParams+" > itable;");
            sw.newline();

            for (MethodInstance meth : methods) {
                sw.write(Emitter.translateType(meth.returnType(), true));
                sw.write(" ");
                sw.write(Emitter.mangled_method_name(meth.name().toString())); 
                sw.write("(");
                boolean first = true;
                int argNum=0;
                for (Type f : meth.formalTypes()) {
                    if (!first) sw.write(", ");
                    sw.write(Emitter.translateType(f, true)+" arg"+(argNum++));
                    first = false;
                }
                sw.write(") {"); sw.newline(4); sw.begin(0);
                if (!meth.returnType().isVoid()) sw.write("return ");

                sw.write(recvArg+"->"+Emitter.mangled_method_name(meth.name().toString())+"(");
                boolean firstArg = true;
                for (int j=0; j<meth.formalTypes().size(); j++) {
                    sw.write((firstArg ? "arg": ", arg")+j);
                    firstArg = false;
                }
                sw.write(")");
                sw.write(";");
                sw.end(); sw.newline();
                sw.write("}"); sw.newline();
            }            
            sw.end(); sw.newline();
            sw.write("};"); sw.newline();

            cg.emitter.printTemplateSignature(cd.typeParameters(), sw);
            sw.write((doubleTemplate ? "typename " : "")+interfaceCType+(doubleTemplate ? "::template itable< " : "::itable< ")+
                     thunkType+thunkParams+" > "+" "+thunkType+thunkParams+"::itable");
            if (!isEmpty()) {
                int methodNum = 0;
                sw.write("(");
                for (MethodInstance meth : methods) {
                    if (methodNum > 0) sw.write(", ");
                    sw.write("&"+thunkType+thunkParams+"::"+Emitter.mangled_method_name(meth.name().toString()));
                    methodNum++;
                }
                sw.write(")");
            }
            sw.write(";"); sw.newline();
            
            if (cd.package_() != null) {
                Emitter.closeNamespaces(sw, cd.package_().get().fullName()); sw.newline();
            }
	    }
	    
	    String interfaceCType = Emitter.translateType(interfaceType, false);
	    String clsCType = Emitter.translateType(cls, false, false);
	    boolean doubleTemplate = cd.typeParameters().size() > 0 && interfaceType.x10Def().typeParameters().size() > 0;

	    cg.emitter.printTemplateSignature(cd.typeParameters(), sw);
	    sw.write((doubleTemplate ? "typename " : "")+interfaceCType+(doubleTemplate ? "::template itable< " : "::itable< ")+
	             Emitter.translateType(cls, false)+" > "+" "+clsCType+"::_itable_"+itableNum+"");
	    TypeSystem xts = cls.typeSystem();
	    if (!isEmpty()) {
	        int methodNum = 0;
	        sw.write("(");
	        for (MethodInstance meth : methods) {
	            String containerType = clsCType;
	            if (methodNum > 0) sw.write(", ");
                try {
                    MethodInstance ami = xts.findMethod(cls, xts.MethodMatcher(cls, meth.name(), meth.formalTypes(), context));
                    if (ami.container().isAny()) {
                        containerType = CLASS_TYPE;
                    } else if (!xts.hasSameClassDef(ami.container(), cls) && !ami.flags().isAbstract()) {
                        containerType = Emitter.translateType(ami.container(), false);
                    }
                } catch (SemanticException e) {
                    // Ignore exception, just use clsCType
                }
	            sw.write("&"+containerType+"::"+Emitter.mangled_method_name(meth.name().toString()));
	            methodNum++;
	        }
	        sw.write(")");
	    }
	    sw.write(";"); sw.newline();
	    
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
			// NOTE: Exploiting X10 2.0 semantics that methods can't be overloaded based on constraints.
			//       If that is changed in the future, we will have to fix this code.
			Iterator<Type> i1 = m1Formals.iterator();
			Iterator<Type> i2 = m2Formals.iterator();
			while (i1.hasNext()) {
				Type f1 = Types.baseType(i1.next());
				Type f2 = Types.baseType(i2.next());
				int fcompare = f1.toString().compareTo(f2.toString());
				if (fcompare != 0) return fcompare;
			}
			
			// X10 allows covariant return types, but not overloading based on return type.
			// Therefore we ignore return type in comparing methods and if we get to this point the
			// methods are considered to be equal.
			return 0;
		}
	}
}
