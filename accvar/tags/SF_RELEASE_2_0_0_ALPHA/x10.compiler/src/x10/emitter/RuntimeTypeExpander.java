/**
 * 
 */
package x10.emitter;

import java.util.List;

import polyglot.types.Type;
import polyglot.visit.Translator;
import x10.constraint.XConstraint;
import x10.types.ClosureType;
import x10.types.ConstrainedType;
import x10.types.ParameterType;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.visit.X10PrettyPrinterVisitor;

final public class RuntimeTypeExpander extends Expander {
    /**
	 * 
	 */
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

    public String toString() {
    	return "RuntimeTypeExpander{#" + hashCode() + ", " + at.toString() + "}";
    }
    public void expand(Translator tr) {
        String s = typeof(at);
        if (s != null) {
            er.w.write(s);
            return;
        }
        
        if (at instanceof ParameterType) {
            ParameterType pt = (ParameterType) at;
            er.w.write(pt.name().toString());
            return;
        }

        if (at instanceof ClosureType) {
            ClosureType ct = (ClosureType) at;
            List<Type> args = ct.argumentTypes();
            Type ret = ct.returnType();
            er.w.write("new ");
            if (ret.isVoid()) {
                er.w.write("x10.core.fun.VoidFun");
            }
            else {
                er.w.write("x10.core.fun.Fun");
            }
            er.w.write("_" + ct.typeParameters().size());
            er.w.write("_" + args.size());
            er.w.write(".RTT(");
            String sep = "";
            for (Type a : args) {
                er.w.write(sep);
                sep = ",";
                new RuntimeTypeExpander(er, a).expand(tr);
            }
            if (! ret.isVoid()) {
                er.w.write(sep);
                new RuntimeTypeExpander(er, ret).expand(tr);
            }
            er.w.write(")");
            return;
        }

        if (at instanceof X10ClassType) {
            X10ClassType ct = (X10ClassType) at;
            X10ClassDef cd = ct.x10Def();
            String pat = er.getJavaRTTRep(cd);
            
            // Check for @NativeRep with null RTT class
            if (pat == null && er.getJavaRep(cd) != null) {
            	er.w.write("x10.types.Types.runtimeType(");
            	er.printType(at, 0);
            	er.w.write(".class");
            	er.w.write(")");
            	return;
            }
            
            if (pat == null) {
                if (ct.isGloballyAccessible() && ct.typeArguments().size() == 0) {
                    er.w.write(er.rttName(cd));
                    er.w.write(".it");
                }
                else {
                    er.w.write("new ");
                    er.w.write(er.rttName(cd));
                    
                    er.w.write("<");
                    for (int i = 0; i < ct.typeArguments().size(); i++) {
                    	if (i != 0)
                    		er.w.write(", ");
                    	new TypeExpander(er, ct.typeArguments().get(i), X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS | X10PrettyPrinterVisitor.BOX_PRIMITIVES).expand(tr);
                    }
                    er.w.write(">");

                    er.w.write("(");
                    for (int i = 0; i < ct.typeArguments().size(); i++) {
                        if (i != 0)
                            er.w.write(", ");
                        new RuntimeTypeExpander(er, ct.typeArguments().get(i)).expand(tr);
                    }
                    er.w.write(")");
                }
                return;
            }
            else {
            	Object[] components = new Object[1 + ct.typeArguments().size() * 2];
            	int i = 0;
            	components[i++] = new TypeExpander(er, ct, X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS | X10PrettyPrinterVisitor.BOX_PRIMITIVES);
            	for (Type at : ct.typeArguments()) {
            		components[i++] = new TypeExpander(er, at, X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS | X10PrettyPrinterVisitor.BOX_PRIMITIVES);
            		components[i++] = new RuntimeTypeExpander(er, at);
            	}
            	er.dumpRegex("Native", components, tr, pat);
            	return;
            }
        }
        
        if (at instanceof ConstrainedType) {
            ConstrainedType ct = (ConstrainedType) at;
            Type base = ct.baseType().get();
            if (X10PrettyPrinterVisitor.serialize_runtime_constraints) {
                XConstraint constraint = ct.constraint().get();
                er.w.write("new x10.types.ConstrainedType(");
                new RuntimeTypeExpander(er, base).expand(tr);
                er.w.write(", ");
                er.w.write("null, ");
                er.serializeConstraint(constraint);
                er.w.write(")");
            }
            else {
                new RuntimeTypeExpander(er, base).expand(tr);
            }
            return;
        }

        er.w.write("x10.types.Types.runtimeType(");
        er.printType(at, 0);
        er.w.write(".class");
        er.w.write(")");
    }

    String typeof(Type t) {
        if (t.isBoolean())
            return "x10.types.Types.BOOLEAN";
        if (t.isByte())
            return "x10.types.Types.BYTE";
        if (t.isShort())
            return "x10.types.Types.SHORT";
        if (t.isChar())
            return "x10.types.Types.CHAR";
        if (t.isInt())
            return "x10.types.Types.INT";
        if (t.isLong())
            return "x10.types.Types.LONG";
        if (t.isFloat())
            return "x10.types.Types.FLOAT";
        if (t.isDouble())
            return "x10.types.Types.DOUBLE";
        return null;
    }
}