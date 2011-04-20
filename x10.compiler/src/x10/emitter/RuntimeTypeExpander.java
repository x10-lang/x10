/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.emitter;

import java.util.Collections;
import java.util.List;

import polyglot.types.Type;
import polyglot.visit.Translator;
import x10.types.ConstrainedType;
import x10.types.FunctionType;
import x10.types.ParameterType;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import polyglot.types.TypeSystem;
import x10.visit.X10PrettyPrinterVisitor;

final public class RuntimeTypeExpander extends Expander {

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
            if (args.size() > 0) {
                er.w.write("new x10.rtt.ParameterizedType(");
                printFunRTT(ct, args, ret);
                for (Type a:args) {
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
                printFunRTT(ct, args, ret);
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
            
            List<Type> typeArgs = ct.typeArguments();
            if (typeArgs == null) typeArgs = Collections.<Type>emptyList();
            if (pat == null) {
                // XTENLANG-1102
                if (ct.isGloballyAccessible() && typeArgs.size() == 0) {
                    er.w.write(Emitter.mangleQName(cd.fullName()).toString() + "." + X10PrettyPrinterVisitor.RTT_NAME);
                } else {
                    er.w.write("new x10.rtt.ParameterizedType(");
                    er.w.write(Emitter.mangleQName(cd.fullName()).toString() + "." + X10PrettyPrinterVisitor.RTT_NAME);
                    for (int i = 0; i < typeArgs.size(); i++) {
                        er.w.write(", ");
                        new RuntimeTypeExpander(er, typeArgs.get(i)).expand(tr);
                    }
                    er.w.write(")");
                }
                return;
            }
            else {
            	Object[] components = new Object[1 + typeArgs.size() * 2];
            	int i = 0;
            	components[i++] = new TypeExpander(er, ct, X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS | X10PrettyPrinterVisitor.BOX_PRIMITIVES);
            	for (Type at : typeArgs) {
            		components[i++] = new TypeExpander(er, at, X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS | X10PrettyPrinterVisitor.BOX_PRIMITIVES);
            		components[i++] = new RuntimeTypeExpander(er, at);
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

    private void printFunRTT(FunctionType ct, List<Type> args, Type ret) {
        if (ret.isVoid()) {
            er.w.write(X10PrettyPrinterVisitor.X10_VOIDFUN_CLASS_PREFIX);
        } else {
            er.w.write(X10PrettyPrinterVisitor.X10_FUN_CLASS_PREFIX);
        }
        er.w.write("_" + ct.typeParameters().size());
        er.w.write("_" + args.size());
        er.w.write("." + X10PrettyPrinterVisitor.RTT_NAME);
    }

    String typeof(Type t) {
        if (t.isBoolean())
            return "x10.rtt.Types.BOOLEAN";
        if (t.isChar())
            return "x10.rtt.Types.CHAR";
        if (t.isNumeric()) {
            TypeSystem ts = (TypeSystem) er.tr.typeSystem();
            if (ts.isUnsigned(t)) {
                if (ts.isUByte(t))
                    return "x10.rtt.Types.UBYTE";
                if (ts.isUShort(t))
                    return "x10.rtt.Types.USHORT";
                if (ts.isUInt(t))
                    return "x10.rtt.Types.UINT";
                if (ts.isULong(t))
                    return "x10.rtt.Types.ULONG";
            } else {
                if (t.isByte())
                    return "x10.rtt.Types.BYTE";
                if (t.isShort())
                    return "x10.rtt.Types.SHORT";
                if (t.isInt())
                    return "x10.rtt.Types.INT";
                if (t.isLong())
                    return "x10.rtt.Types.LONG";
                if (t.isFloat())
                    return "x10.rtt.Types.FLOAT";
                if (t.isDouble())
                    return "x10.rtt.Types.DOUBLE";
            }
        }
        return null;
    }
}