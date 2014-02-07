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

package x10doc.doc;

import java.util.ArrayList;
import java.util.List;

import polyglot.types.ClassType;
import polyglot.types.Types;
import x10.constraint.XConstraint;
import x10.constraint.XFormula;
import x10.constraint.XTerm;
import x10.types.ConstrainedType;
import x10.types.FunctionType;
import x10.types.X10ClassType;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.Type;

public class X10ParameterizedType extends X10Type implements ParameterizedType {
    boolean depType; // true if this object represents an X10 ConstrainedType
                     // (with/without type parameters)
    X10ClassDoc classDoc;
    X10RootDoc rootDoc;
    Type[] typeArgs;
    Type superclassType;
    ArrayList<Type> interfaceTypes;

    // the created X10ParameterizedType object represents a parameterized type
    // with/without constraints
    public X10ParameterizedType(polyglot.types.Type t, X10TypeVariable[] methodTypeVars, boolean depType) {
        super(t);

        this.depType = depType;
        rootDoc = X10RootDoc.getRootDoc();
        classDoc = rootDoc.getUnspecClass(((X10ClassType) Types.baseType(t)).x10Def());

        polyglot.types.Type b = (depType ? (Types.baseType(t)) : t);
        // polyglot.types.Type b = (depType ? ((polyglot.types.Type)
        // X10TypeMixin.baseType(t)) : t);
        List<polyglot.types.Type> args = ((X10ClassType) b).typeArguments();
        if (args != null) {
            typeArgs = new Type[args.size()];
            for (int i = 0; i < typeArgs.length; i++) {
                typeArgs[i] = rootDoc.getType(args.get(i), methodTypeVars);
            }
        } else {
            typeArgs = new Type[0];
        }
        // System.out.println("X10ParameterizedType{" + t + "}.typeArgs = " +
        // Arrays.toString(typeArgs));
        // System.out.println("X10ParameterizedType{" + t + "}: t.getClass() = "
        // + t.getClass());

        ClassType c = t.toClass();
        superclassType = ((c.def().flags().isInterface()) ? null : rootDoc.getType(c.superClass(), methodTypeVars));

        this.interfaceTypes = new ArrayList<Type>();
        for (polyglot.types.Type y : c.interfaces()) {
            this.interfaceTypes.add(rootDoc.getType(y, methodTypeVars));
        }

        // System.out.println("X10ParameterizedType(" + t +
        // "): superclassType = " +
        // superclassType + ", interfaceTypes = " +
        // Arrays.toString(interfaceTypes.toArray(new Type[0])));
    }

    // creates an X10ParameterizedType representing an X10 ConstrainedType that
    // does not have any type
    // parameters, in other words a ConstrainedType for whose base type b,
    // |b.typeParameters()| == 0
    public X10ParameterizedType(polyglot.types.Type t) {
        super(t);
        this.depType = true;
        X10RootDoc rootDoc = X10RootDoc.getRootDoc();
        classDoc = rootDoc.getUnspecClass(((X10ClassType) Types.baseType(t)).x10Def());

        // the following may need to be changed, e.g., if the superclass of a
        // ConstrainedType ct is the super class
        // obtained below with the constraints of ct; similar argument for
        // interfaceTypes
        this.superclassType = classDoc.superclassType();
        this.interfaceTypes = new ArrayList<Type>();
        for (Type y : classDoc.interfaceTypes()) {
            this.interfaceTypes.add(y);
        }
        typeArgs = new Type[0];

        // XConstraint myXC = X10TypeMixin.xclause(pType);
        // System.err.println("XConstraint: "+myXC);
        // if (myXC != null) {
        // System.err.println("\tatoms: "+myXC.atoms());
        // System.err.println("\teqvs: "+myXC.eqvs());
        // List<XTerm> terms = myXC.constraints();
        // System.err.println("\tconstraints: "+terms);
        // for (XTerm term : terms) {
        // System.err.println("\t\tterm: "+term+"("+term.kind()+")");
        // System.err.println("\t\targuments: "+((XFormula) term).arguments() +
        // "; operator: " + ((XFormula)term).operator());
        // for (XTerm sub: ((XFormula)term).arguments()) {
        // System.err.print("\t\ttype(" + sub + ") = " + sub.getClass());
        // // // if (sub instanceof XVar) {
        // // // XVar xv = ((XVar)sub).rootVar();
        // // // System.err.print("; type(" + xv + ") = " + xv.getClass());
        // // // if (xv instanceof XLocal) {
        // // // System.err.print("(Local_c.name()=" + ((XLocal)xv).name() +
        // ")");
        // // // }
        // // // }
        // if (sub instanceof XField) {
        // XField f = (XField)sub;
        // System.err.print("; type(" + f.receiver() + ") = " +
        // f.receiver().getClass());
        // System.err.print("; type(" + f.field() + ") = " +
        // f.field().getClass());
        // XName xn = f.field();
        // if (xn instanceof XNameWrapper<?>) {
        // FieldDef fd = ((XNameWrapper<FieldDef>)xn).val();
        // System.err.print("; fd.name() = " + fd.name());
        // }
        // String fieldName = "";
        //
        // for (XVar v: f.vars()) {
        // if (v instanceof XEQV) {
        // try {
        // if (myXC.entails(myXC.self(), v)) {
        // fieldName += "self";
        // }
        // else {
        // fieldName += v.toString();
        // }
        // }
        // catch (XFailure xf) {
        // }
        // }
        // else if (v instanceof XLocal) {
        // XName n = ((XLocal)v).name();
        // if (v.toString().endsWith("#this")) {
        // fieldName += "this";
        // }
        // else {
        // fieldName += v.toString();
        // }
        // // if (n instanceof XNameWrapper<?>) {
        // // System.err.println(n + " = XNameWrapper<" +
        // ((XNameWrapper<?>)n).val().getClass() +
        // // ">; toString() = " + ((XNameWrapper<?>)n).val() + "; kind = " +
        // v.kind());
        // // }
        // }
        // else {
        // XName n = f.field();
        // if (n instanceof XNameWrapper<?>) {
        // FieldDef fd = ((XNameWrapper<FieldDef>)n).val();
        // fieldName += "." + fd.name();
        // }
        // }
        // }
        // System.err.print("; fieldName = " + fieldName);
        // }
        // System.err.println();
        // }
        // }
        // }
    }

    public boolean isX10Specific() {
        if (pType instanceof FunctionType) { // earlier test:
                                             // "(classDoc.classDef.asType() instanceof FunctionType)"
            return true;
        }
        if (depType) {
            return true;
        }
        for (Type t : this.typeArgs) {
            if (X10Type.isX10Specific(t)) {
                return true;
            }
        }
        return false;
    }

    public String descriptor() {
        XConstraint xc = Types.xclause(pType);
        List<? extends XTerm> terms = xc.constraints();
        String result = "  {";
        boolean first = true;
        for (XTerm t : terms) {
            if (first) {
                first = false;
            } else {
                result += ", ";
            }
            result += t;
        }
        result += "}";
        return result;
    }

    public Type containingType() {
        // TODO Auto-generated method stub
        return null;
    }

    public Type[] interfaceTypes() {
        return interfaceTypes.toArray(new Type[0]);
        // return new Type[0];
    }

    public Type superclassType() {
        return superclassType;
        // return null;
    }

    public Type[] typeArguments() {
        return typeArgs;
    }

    @Override
    public ClassDoc asClassDoc() {
        // return
        // X10RootDoc.getRootDoc().getUnspecClass(((X10ClassType)X10TypeMixin.baseType(pType)).x10Def());
        return classDoc;
    }

    @Override
    public ParameterizedType asParameterizedType() {
        return this;
    }

    @Override
    public String qualifiedTypeName() {
        // TODO Auto-generated method stub
        return super.qualifiedTypeName();
    }

    @Override
    public String simpleTypeName() {
        // TODO Auto-generated method stub
        return super.simpleTypeName();
    }

    @Override
    public String typeName() {
        // TODO Auto-generated method stub
        return super.typeName();
    }

    // temporary defn used in println statement in constructor
    public String toString() {
        if (pType instanceof ConstrainedType) {
            ConstrainedType ct = ((ConstrainedType) pType);
            XConstraint xc = ct.constraint().get();
            String str = ct.name() + "{";
            boolean first = true;
            for (XFormula f : xc.atoms()) {
                if (first) {
                    first = false;
                    str += (f.left() + " " + f.operator() + " " + f.right());
                } else {
                    str += (", " + f.left() + " " + f.operator() + " " + f.right());
                }
            }
            str += "}";
            System.out.println("X10ParameterizedType.toString(): " + str + "; ConstrainedType.constraint().get() = "
                    + xc);
        }
        return pType.toString();
    }
}
