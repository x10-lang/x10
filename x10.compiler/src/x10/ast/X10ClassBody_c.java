/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.ast;

import java.util.ArrayList;
import java.util.Iterator;

import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.ConstructorDef;
import polyglot.types.ConstructorInstance;
import polyglot.types.Context;
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
import polyglot.types.MethodInstance;
import polyglot.util.TypedList;
import polyglot.visit.ContextVisitor;
import polyglot.ast.ClassBody_c;
import x10.types.MacroType;
import x10.types.ParameterType;
import x10.types.TypeConstraint;
import x10.types.TypeDef;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10ConstructorDef;
import x10.types.X10Context;
import x10.types.X10Flags;
import x10.types.X10MethodDef;
import x10.types.X10MethodInstance;
import x10.types.X10ProcedureDef;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.X10TypeSystem_c;

public class X10ClassBody_c extends ClassBody_c {
    public X10ClassBody_c(Position pos, java.util.List<ClassMember> members) {
        super(pos, members);
    }

    public Node conformanceCheck(ContextVisitor tc) throws SemanticException {
        duplicateTypeDefCheck(tc);
        checkMethodCompatibility(tc);
        return super.conformanceCheck(tc);
    }
    
    private void getInheritedVirtualMethods(X10ClassType ct, List<MethodInstance> methods) {
        for (MethodInstance mi : ct.methods()) {
            if (mi.flags().isStatic()) continue;
            methods.add(mi);
        }
        Type sup = ct.superClass();
        if (sup instanceof X10ClassType) {
            getInheritedVirtualMethods((X10ClassType) sup, methods);
        }
        for (Type t : ct.interfaces()) {
            if (t instanceof X10ClassType) {
                getInheritedVirtualMethods((X10ClassType) t, methods);
            }
        }
    }
    
    private void checkMethodCompatibility(ContextVisitor tc) throws SemanticException {
        X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();

        ClassDef cd = tc.context().currentClassDef();
        
        List<MethodInstance> l = new ArrayList<MethodInstance>();
        getInheritedVirtualMethods((X10ClassType) cd.asType(), l);

        // Remove overridden methods.
        for (ListIterator<MethodInstance> i = l.listIterator(); i.hasNext(); ) {
            MethodInstance mi = i.next();           
            MethodInstance mj = ts.findImplementingMethod(cd.asType(), mi, true, tc.context());
            if (mj != null && mj.def() != mi.def())
                i.remove();
        }
        
        // It is a static error if:
        // * mi overrides mk
        // * mj overrides ml
        // * ml != mk
        // * mk and ml have compatible signatures
        // * mk and ml are parameterized
        for (int i = 0; i < l.size(); i++) {
            X10MethodInstance mi = (X10MethodInstance) l.get(i);

            for (int j = i + 1; j < l.size(); j++) {
                X10MethodInstance mj = (X10MethodInstance) l.get(j);
                if (mi.def() == mj.def())
                    continue;

                if (! mi.name().equals(mj.name()))
                    continue;

                for (MethodInstance mik : mi.implemented(tc.context())) {
                    X10MethodInstance mk = (X10MethodInstance) mik;
                    if (mk.def() == mi.def()) continue;

                    for (MethodInstance mjl : mj.implemented(tc.context())) {
                        X10MethodInstance ml = (X10MethodInstance) mjl;
                        if (ml.def() == mj.def()) continue;
                        if (ml.def() == mk.def()) continue;

                        if (hasCompatibleArguments(mk.x10Def(), ml.x10Def(), tc.context()) && isParameterized(mk.x10Def()) && isParameterized(ml.x10Def())) {
                            throw new SemanticException("Method " + mj.signature() + " in " + mj.container() + " and method " + mi.signature() + " in " + mi.container()
                                                        + " override methods with compatible signatures.", mi.position());
                        }
                    }
                }
            }
        }
        
        return;
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
                
                if (hasCompatibleArguments(ci, cj, tc.context())) {
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
        
                if (mi.name().equals(mj.name()) && hasCompatibleArguments(mi, mj, tc.context())) {
                    throw new SemanticException("Duplicate method \"" + mj + "\"; previous declaration at " + mi.position() + ".", mj.position());
                }
            }
        }
    }    

    public static boolean isParameterized(X10ProcedureDef p1) {
        X10TypeSystem ts = (X10TypeSystem) p1.typeSystem();
        
        for (int i = 0; i < p1.formalTypes().size(); i++) {
            Type t1 = Types.get(p1.formalTypes().get(i));
            
            // Erase types and expand formals.
            t1 = X10TypeMixin.baseType(t1);
            
            // Parameters conflict with everything
            if (t1 instanceof ParameterType)
            	return true;
        }
        
        return false;
    }

    public static boolean hasCompatibleArguments(X10ProcedureDef p1, X10ProcedureDef p2, Context context) {
        if (p1.typeParameters().size() != p2.typeParameters().size())
            return false;
        
        if (p1.formalTypes().size() != p2.formalTypes().size())
            return false;

        X10TypeSystem ts = (X10TypeSystem) p1.typeSystem();
        X10Context xcontext = (X10Context) context;
        TypeConstraint tc = (TypeConstraint) xcontext.currentTypeConstraint().copy();
        
        for (int i = 0; i < p1.formalTypes().size(); i++) {
            Type t1 = Types.get(p1.formalTypes().get(i));
            Type t2 = Types.get(p2.formalTypes().get(i));
            
            // Erase types and expand formals.
            //t1 = X10TypeMixin.baseType(t1);
            //t2 = X10TypeMixin.baseType(t2);
            
            // Parameters conflict with everything
            //if (t1 instanceof ParameterType || t2 instanceof ParameterType)
            //    continue;
            
            tc = tc.unify(t1,t2,ts);
            if (! tc.consistent(xcontext))
        	return false;
           /*
            // Uninstantiate the parameterized types.
            if (t1 instanceof X10ClassType) {
                X10ClassType ct = (X10ClassType) t1;
                t1 = ct.x10Def().asType();
            }
            
            if (t2 instanceof X10ClassType) {
                X10ClassType ct = (X10ClassType) t2;
                t2 = ct.x10Def().asType();
            }
            
            if (! ts.typeEquals(t1, t2, context))
                return false;*/
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

                if (mi.name().equals(mj.name()) && hasCompatibleArguments(mi, mj, tc.context())) {
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
