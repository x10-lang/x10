/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.Iterator;

import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.MethodDef;
import polyglot.types.Named;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.List;
import polyglot.util.Position;
import polyglot.ast.ClassMember;
import polyglot.ast.Formal;
import polyglot.ast.Node;
import polyglot.ast.MethodDecl;
import polyglot.ast.MethodDecl_c;
import polyglot.ast.Formal_c;
import polyglot.types.ClassType_c;
import polyglot.ext.x10.types.MacroType;
import polyglot.ext.x10.types.TypeDef;
import polyglot.ext.x10.types.X10ClassDef;
import polyglot.ext.x10.types.X10Flags;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.types.MethodInstance;
import polyglot.util.TypedList;
import polyglot.visit.ContextVisitor;
import polyglot.ast.ClassBody_c;

public class X10ClassBody_c extends ClassBody_c {
    public X10ClassBody_c(Position pos, java.util.List<ClassMember> members) {
        super(pos, members);
    }

    public Node conformanceCheck(ContextVisitor tc) throws SemanticException {
        duplicateTypeDefCheck(tc);
        return super.conformanceCheck(tc);
    }

    protected void duplicateTypeDefCheck(ContextVisitor tc) throws SemanticException {
        X10ClassDef type = (X10ClassDef) tc.context().currentClassDef();

        TypeSystem ts = tc.typeSystem();

        ArrayList<TypeDef> l = new ArrayList<TypeDef>(type.memberTypes());

        for (int i = 0; i < l.size(); i++) {
            TypeDef mi = l.get(i);
            MacroType ti = mi.asType();

            for (int j = i + 1; j < l.size(); j++) {
                TypeDef mj = l.get(j);
                MacroType tj = mj.asType();

                if (ti.name().equals(tj.name()) && ti.typeParameters().size() == tj.typeParameters().size() && ti.hasFormals(tj.formalTypes())) {
                    throw new SemanticException("Duplicate type definition \"" + mj + "\".", mj.position());
                }
            }

            for (Ref<? extends Type> tref : type.memberClasses()) {
                Type t = Types.get(tref);
                t = X10TypeMixin.baseType(t);
                if (t instanceof ClassType) {
                    ClassType ct = (ClassType) t;
                    if (ct.name().equals(ti.name())) {
                        throw new SemanticException("Type definition " + mi + " has the same name as member class " + ct + ".", mi.position());
                    }
                }
            }
        }
    }
}
