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
import polyglot.types.ConstructorDef;
import polyglot.types.ConstructorInstance;
import polyglot.types.MethodDef;
import polyglot.types.Named;
import polyglot.types.ProcedureDef;
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
import polyglot.ext.x10.types.ParameterType;
import polyglot.ext.x10.types.TypeDef;
import polyglot.ext.x10.types.X10ClassDef;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10ConstructorDef;
import polyglot.ext.x10.types.X10Flags;
import polyglot.ext.x10.types.X10MethodDef;
import polyglot.ext.x10.types.X10ProcedureDef;
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
    
    @Override
    protected void duplicateConstructorCheck(ContextVisitor tc) throws SemanticException {
        ClassDef type = tc.context().currentClassDef();
        TypeSystem ts = tc.typeSystem();
        
        ArrayList<ConstructorDef> l = new ArrayList<ConstructorDef>(type.constructors());
        
        for (int i = 0; i < l.size(); i++) {
            X10ConstructorDef ci = (X10ConstructorDef) l.get(i);
        
            for (int j = i+1; j < l.size(); j++) {
                X10ConstructorDef cj = (X10ConstructorDef) l.get(j);
                
                if (hasCompatibleArguments(ci, cj)) {
                    throw new SemanticException("Duplicate constructor \"" + cj + "\"; previous declaration at " + ci.position() + ".", cj.position());
                }
            }
        }
    }
    
    @Override
    protected void duplicateMethodCheck(ContextVisitor tc) throws SemanticException {
        ClassDef type = tc.context().currentClassDef();
        
        TypeSystem ts = tc.typeSystem();
        
        ArrayList<MethodDef> l = new ArrayList<MethodDef>(type.methods());
        
        for (int i = 0; i < l.size(); i++) {
            X10MethodDef mi = (X10MethodDef) l.get(i);
        
            for (int j = i+1; j < l.size(); j++) {
                X10MethodDef mj = (X10MethodDef) l.get(j);
        
                if (mi.name().equals(mj.name()) && hasCompatibleArguments(mi, mj)) {
                    throw new SemanticException("Duplicate method \"" + mj + "\"; previous declaration at " + mi.position() + ".", mj.position());
                }
            }
        }
    }    

    protected boolean hasCompatibleArguments(X10ProcedureDef p1, X10ProcedureDef p2) {
        if (p1.typeParameters().size() != p2.typeParameters().size())
            return false;
        
        if (p1.formalTypes().size() != p2.formalTypes().size())
            return false;

        X10TypeSystem ts = (X10TypeSystem) p1.typeSystem();
        
        for (int i = 0; i < p1.formalTypes().size(); i++) {
            Type t1 = Types.get(p1.formalTypes().get(i));
            Type t2 = Types.get(p2.formalTypes().get(i));
            
            // Erase types and expand formals.
            t1 = X10TypeMixin.baseType(t1);
            t2 = X10TypeMixin.baseType(t2);
            
            // Parameters conflict with everything
            if (t1 instanceof ParameterType || t2 instanceof ParameterType)
                continue;
            
            // Uninstantiate the parameterized types.
            if (t1 instanceof X10ClassType) {
                X10ClassType ct = (X10ClassType) t1;
                t1 = ct.x10Def().asType();
            }
            
            if (t2 instanceof X10ClassType) {
                X10ClassType ct = (X10ClassType) t2;
                t2 = ct.x10Def().asType();
            }
            
            if (! ts.typeEquals(t1, t2))
                return false;
        }
        
        return true;
    }
    
    protected void duplicateTypeDefCheck(ContextVisitor tc) throws SemanticException {
        X10ClassDef type = (X10ClassDef) tc.context().currentClassDef();

        TypeSystem ts = tc.typeSystem();

        ArrayList<TypeDef> l = new ArrayList<TypeDef>(type.memberTypes());

        for (int i = 0; i < l.size(); i++) {
            TypeDef mi = l.get(i);

            for (int j = i + 1; j < l.size(); j++) {
                TypeDef mj = l.get(j);

                if (mi.name().equals(mj.name()) && hasCompatibleArguments(mi, mj)) {
                    throw new SemanticException("Duplicate type definition \"" + mj + "\"; previous declaration at " + mi.position() + ".", mj.position());
                }
            }

            for (Ref<? extends Type> tref : type.memberClasses()) {
                Type t = Types.get(tref);
                t = X10TypeMixin.baseType(t);
                if (t instanceof ClassType) {
                    ClassType ct = (ClassType) t;
                    if (ct.name().equals(mi.name())) {
                        throw new SemanticException("Type definition " + mi + " has the same name as member class " + ct + ".", mi.position());
                    }
                }
            }
        }
    }
}
