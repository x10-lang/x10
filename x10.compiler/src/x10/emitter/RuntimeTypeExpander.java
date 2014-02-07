/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.emitter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.visit.Translator;
import x10.types.ConstrainedType;
import x10.types.FunctionType;
import x10.types.ParameterType;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10FieldInstance;
import polyglot.types.TypeSystem;
import x10.visit.X10PrettyPrinterVisitor;

// constants
import static x10.visit.X10PrettyPrinterVisitor.BOX_PRIMITIVES;
import static x10.visit.X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS;

final public class RuntimeTypeExpander extends Expander {

	// XTENLANG-2488
    public static final boolean useReflectionToGetRTT = true;

    private final Type at;

    public RuntimeTypeExpander(Emitter er, Type at) {
        super(er);
        if (at instanceof X10ClassType) {
            X10ClassType ct = (X10ClassType) at;

            if (ct.isAnonymous()) {
                if (ct.interfaces().size() > 0) {
                    ct = (X10ClassType) ct.interfaces().get(0);
                }
                else if (ct.superClass() != null) {
                    ct = (X10ClassType) ct.superClass();
                }
            }

            at = ct;
        }

        this.at = at;
    }

    @Override
    public String toString() {
    	return "RuntimeTypeExpander{#" + hashCode() + // todo: using hashCode leads to non-determinism in the output of the compiler
                ", " + at.toString() + "}";
    }
    
    public static boolean hasConflictingField(X10ClassType ct, Translator tr) {
    	if (!useReflectionToGetRTT) {
    		return false;
    	}
        TypeSystem xts = tr.typeSystem();
        boolean hasConflictingField = false;
        try {
        	// container is available only if ct is a member
        	if (ct.isMember()) {
        		X10ClassType container = ct.container();
        		X10FieldInstance fi = xts.findField(container, container, ct.name(), tr.context());
        		hasConflictingField = fi != null;
        	}
        } catch (SemanticException e) {
        	// exception means no such field
        }
        return hasConflictingField;
    }

    public static String getRTT(String qualifiedClassName, boolean hasConflictingField) {
    	String rttString = null;
    	if (useReflectionToGetRTT && hasConflictingField) {
    		rttString = X10PrettyPrinterVisitor.X10_RTT_TYPES + ".<" + qualifiedClassName + "> $RTT(" + qualifiedClassName + ".class)";
    	} else {
    		rttString = qualifiedClassName + "." + X10PrettyPrinterVisitor.RTT_NAME;
    	}
    	return rttString;
    }
    
    @Override
    public void expand(Translator tr) {
        String s = typeof(at);
        if (s != null) {
            er.w.write(s);
            return;
        }
        
        if (at instanceof ParameterType) {
            ParameterType pt = (ParameterType) at;
            er.w.write(Emitter.mangleParameterType(pt));
            return;
        }

        if (at instanceof FunctionType) {
            FunctionType ct = (FunctionType) at;
            List<Type> args = ct.argumentTypes();
            Type ret = ct.returnType();
            
            // XTENLANG-1102
            if (args.size() > 0 || !ret.isVoid()) {
                er.w.write("x10.rtt.ParameterizedType.make(");
                printFunRTT(ct);
                for (Type a : args) {
                    er.w.write(",");
                    new RuntimeTypeExpander(er, a).expand(tr);
                }
                if (!ret.isVoid()) {
                    er.w.write(",");
                    new RuntimeTypeExpander(er, ret).expand(tr);
                }
                er.w.write(")");
            }
            else {
                printFunRTT(ct);
            }
            return;
        }

        if (at instanceof X10ClassType) {
            X10ClassType ct = (X10ClassType) at;
            X10ClassDef cd = ct.x10Def();
            String pat = Emitter.getJavaRTTRep(cd);
            
            // Check for @NativeRep with null RTT class
            if (pat == null && Emitter.getJavaRep(cd) != null) {
                er.w.write("new x10.rtt.RuntimeType<Class<?>>(");
            	er.printType(at, 0);
            	er.w.write(".class");
            	er.w.write(")");
            	return;
            }
            
            List<Type> classTypeArgs = ct.typeArguments();
            if (classTypeArgs == null) classTypeArgs = Collections.<Type>emptyList();
            if (pat == null) {
                String rttString = getRTT(Emitter.mangleQName(cd.fullName()).toString(), hasConflictingField(ct, tr));
                // XTENLANG-2118 hack: RTTs for Java types should be looked up using getRTT
                if (ct.isJavaType()) {
                    rttString = X10PrettyPrinterVisitor.X10_RTT_TYPES + ".getRTT("+Emitter.mangleQName(cd.fullName()).toString()+".class)";
                }
                // XTENLANG-1102
                if (ct.isGloballyAccessible() && classTypeArgs.size() == 0) {
                    er.w.write(rttString);
                } else {
                    er.w.write("x10.rtt.ParameterizedType.make(");
                    er.w.write(rttString);
                    for (int i = 0; i < classTypeArgs.size(); i++) {
                        er.w.write(", ");
                        new RuntimeTypeExpander(er, classTypeArgs.get(i)).expand(tr);
                    }
                    er.w.write(")");
                }
                return;
            }
            else {
                List<ParameterType> classTypeParams  = cd.typeParameters();
//                if (classTypeParams == null) classTypeParams = Collections.<ParameterType>emptyList();
                Iterator<ParameterType> classTypeParamsIter = null;
                if (classTypeParams != null) {
                    classTypeParamsIter = classTypeParams.iterator();
                }
            	Map<String,Object> components = new HashMap<String,Object>();
            	int i = 0;
            	Object component;
            	String name;
            	component =  new TypeExpander(er, ct, PRINT_TYPE_PARAMS | BOX_PRIMITIVES);
            	components.put(String.valueOf(i++), component);
            	components.put("class", component);
            	for (Type at : classTypeArgs) {
                    if (classTypeParamsIter != null) {
                        name = classTypeParamsIter.next().name().toString();
                    } else {
                        name = null;
                    }
                    // for RTT of Java.array[Comparable[Int]] -> x10.rtt.Types.getRTT(java.lang.Comparable/*<x10.core.Int>*/[].class)
                    int printTypeParamsIfNotNativeRepedToJava = Emitter.isNativeRepedToJava(at) ? 0 : PRINT_TYPE_PARAMS;
                    component = new TypeExpander(er, at, printTypeParamsIfNotNativeRepedToJava);
//                	components.put(String.valueOf(i++), component); // N.B. don't use number index to avoid breaking existing code
                    if (name != null) { components.put(name, component); }
                    component = new TypeExpander(er, at, printTypeParamsIfNotNativeRepedToJava | BOX_PRIMITIVES);
                	components.put(String.valueOf(i++), component);
                    if (name != null) { components.put(name+Emitter.NATIVE_ANNOTATION_BOXED_REP_SUFFIX, component); }
            		component = new RuntimeTypeExpander(er, at);
                	components.put(String.valueOf(i++), component);
                    if (name != null) { components.put(name+Emitter.NATIVE_ANNOTATION_RUNTIME_TYPE_SUFFIX, component); }
            	}
            	er.dumpRegex("NativeRep", components, tr, pat);
            	return;
            }
        }
        
        if (at instanceof ConstrainedType) {
            ConstrainedType ct = (ConstrainedType) at;
            Type base = ct.baseType().get();
            new RuntimeTypeExpander(er, base).expand(tr);
            return;
        }

        er.w.write("new x10.rtt.RuntimeType<Class<?>>(");
        er.printType(at, 0);
        er.w.write(".class");
        er.w.write(")");
    }

    private void printFunRTT(FunctionType ct) {
        if (ct.returnType().isVoid()) {
            er.w.write(X10PrettyPrinterVisitor.X10_VOIDFUN_CLASS_PREFIX);
        } else {
            er.w.write(X10PrettyPrinterVisitor.X10_FUN_CLASS_PREFIX);
        }
        er.w.write("_" + ct.typeParameters().size());
        er.w.write("_" + ct.argumentTypes().size());
        er.w.write("." + X10PrettyPrinterVisitor.RTT_NAME);
    }

    String typeof(Type t) {
        if (t.isBoolean())
            return X10PrettyPrinterVisitor.X10_RTT_TYPES + ".BOOLEAN";
        if (t.isChar())
            return X10PrettyPrinterVisitor.X10_RTT_TYPES + ".CHAR";
        if (t.isNumeric()) {
            TypeSystem ts = (TypeSystem) er.tr.typeSystem();
            if (t.isUnsignedNumeric()) {
                if (t.isUByte())
                    return X10PrettyPrinterVisitor.X10_RTT_TYPES + ".UBYTE";
                if (t.isUShort())
                    return X10PrettyPrinterVisitor.X10_RTT_TYPES + ".USHORT";
                if (t.isUInt())
                    return X10PrettyPrinterVisitor.X10_RTT_TYPES + ".UINT";
                if (t.isULong())
                    return X10PrettyPrinterVisitor.X10_RTT_TYPES + ".ULONG";
            } else if (t.isSignedNumeric()) {
                if (t.isByte())
                    return X10PrettyPrinterVisitor.X10_RTT_TYPES + ".BYTE";
                if (t.isShort())
                    return X10PrettyPrinterVisitor.X10_RTT_TYPES + ".SHORT";
                if (t.isInt())
                    return X10PrettyPrinterVisitor.X10_RTT_TYPES + ".INT";
                if (t.isLong())
                    return X10PrettyPrinterVisitor.X10_RTT_TYPES + ".LONG";
            } else {
                if (t.isFloat())
                    return X10PrettyPrinterVisitor.X10_RTT_TYPES + ".FLOAT";
                if (t.isDouble())
                    return X10PrettyPrinterVisitor.X10_RTT_TYPES + ".DOUBLE";
            }
        }
        return null;
    }
}
