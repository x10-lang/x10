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

package x10.ast;

import java.util.ArrayList;
import java.util.Iterator;

import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.ConstructorDef;
import polyglot.types.ConstructorInstance;
import polyglot.types.Context;
import polyglot.types.FieldDef;
import polyglot.types.MethodDef;
import polyglot.types.ProcedureDef;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.types.Types;

import java.util.Collection;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.List;

import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import x10.util.CollectionFactory;
import polyglot.util.Position;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassMember;
import polyglot.ast.Formal;
import polyglot.ast.Node;
import polyglot.ast.MethodDecl;
import polyglot.ast.MethodDecl_c;
import polyglot.ast.Formal_c;
import polyglot.ast.Term;
import polyglot.types.ClassType_c;
import polyglot.util.TypedList;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.ExceptionChecker;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.ast.ClassBody_c;
import x10.constraint.XVar;
import x10.errors.Errors;
import x10.types.ClosureDef;
import x10.types.MacroType;
import x10.types.ParameterType;
import x10.types.TypeDef;
import x10.types.TypeParamSubst;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10ConstructorDef;
import polyglot.types.Context;

import x10.types.X10MethodDef;
import x10.types.MethodInstance;
import x10.types.X10ProcedureDef;
import polyglot.types.TypeSystem;
import x10.types.constraints.TypeConstraint;
import x10.types.constraints.XConstrainedTerm;

public class X10ClassBody_c extends ClassBody_c {
	
    protected List<ClassMember> members;
    
    public X10ClassBody_c(Position pos, java.util.List<ClassMember> members) {
        super(pos, members);
        assert(members != null);
        this.members = TypedList.copyAndCheck(members, ClassMember.class, true);
    }

    public Node conformanceCheck(ContextVisitor tc) {
        duplicateTypeDefCheck(tc);
        checkMethodCompatibility(tc);
        return superConformanceCheck(tc);
    }
    
    private Node superConformanceCheck(ContextVisitor tc) {
        duplicateFieldCheck(tc);
        duplicateConstructorCheck(tc);
        duplicateMethodCheck(tc);
        duplicateMemberClassCheck(tc);
        return this;
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
   
    private void checkMethodCompatibility(ContextVisitor tc) {
        TypeSystem ts = (TypeSystem) tc.typeSystem();

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
            MethodInstance mi = (MethodInstance) l.get(i);

            for (int j = i + 1; j < l.size(); j++) {
                MethodInstance mj = (MethodInstance) l.get(j);
                if (mi.def() == mj.def())
                    continue;

                if (! mi.name().equals(mj.name()))
                    continue;

                for (MethodInstance mk : mi.implemented(tc.context())) {
                    if (mk.def() == mi.def()) continue;

                    for (MethodInstance ml : mj.implemented(tc.context())) {
                        if (ml.def() == mj.def()) continue;
                        if (ml.def() == mk.def()) continue;

                        if (hasCompatibleArguments(mk.x10Def(), ml.x10Def(), tc.context()) && isParameterized(mk.x10Def()) && isParameterized(ml.x10Def())) {
                            Errors.issue(tc.job(),
                                    new Errors.MethodsOverrideWithCompatibleSignatures(mj, mi, mi.position()),
                                    this);
                        }
                    }
                }
            }
        }
        
        return;
    }

    @Override
    protected void duplicateConstructorCheck(ContextVisitor tc) {
        ClassDef type = tc.context().currentClassDef();
        TypeSystem ts = tc.typeSystem();
        
        ArrayList<ConstructorDef> l = new ArrayList<ConstructorDef>(type.constructors());
        
        for (int i = 0; i < l.size(); i++) {
            X10ConstructorDef ci = (X10ConstructorDef) l.get(i);
        
            for (int j = i+1; j < l.size(); j++) {
                X10ConstructorDef cj = (X10ConstructorDef) l.get(j);
                
                if (hasCompatibleArguments(ci, cj, tc.context())) {
                    Errors.issue(tc.job(),
                            new Errors.DuplicateConstructor(cj, ci, cj.position()));
                }
            }
        }
    }
    
    @Override
    protected void duplicateMethodCheck(ContextVisitor tc) {
        ClassDef type = tc.context().currentClassDef();
        
        TypeSystem ts = tc.typeSystem();
        
        ArrayList<MethodDef> l = new ArrayList<MethodDef>(type.methods());
        
        for (int i = 0; i < l.size(); i++) {
            X10MethodDef mi = (X10MethodDef) l.get(i);
            
            for (int j = i+1; j < l.size(); j++) {
                X10MethodDef mj = (X10MethodDef) l.get(j);
        
                if (mi.name().equals(mj.name()) && hasCompatibleArguments(mi, mj, tc.context())) {
                    Errors.issue(tc.job(),
                            new Errors.DuplicateMethod(mj, mi, mj.position()));
                }
            }
        }
    }    

    public static boolean isParameterized(X10ProcedureDef p1) {
        TypeSystem ts = (TypeSystem) p1.typeSystem();
        
        for (int i = 0; i < p1.formalTypes().size(); i++) {
            Type t1 = Types.get(p1.formalTypes().get(i));
            
            // Erase types and expand formals.
            t1 = Types.baseType(t1);
            
            // Parameters conflict with everything
            if (t1 instanceof ParameterType)
            	return true;
        }
        
        return false;
    }

    public static boolean hasCompatibleArguments(List<Type> l1, List<Type> l2, Context context) {        
        TypeConstraint tc = context.currentTypeConstraint().copy();
        TypeSystem ts = context.typeSystem();
        for (int i = 0; i < l1.size(); i++) {
            Type t1 = l1.get(i);
            Type t2 = l2.get(i);
            if (!tc.unify(t1,t2,ts) || !tc.consistent(context))
        	    return false;
        }
        return true;
    }
    public static boolean hasCompatibleArguments(X10ProcedureDef p1, X10ProcedureDef p2, Context context) {
        List<ParameterType> tps1 = p1.typeParameters();
        List<ParameterType> tps2 = p2.typeParameters();
        if (tps1.size() != tps2.size())
            return false;

        final int size = p1.formalTypes().size();
        if (size != p2.formalTypes().size())
            return false;

        TypeParamSubst subst = null;
        if (!tps1.isEmpty()) {
            subst = new TypeParamSubst(p1.typeSystem(), tps1, tps2);
        }

        ArrayList<Type> l1 = new ArrayList<Type>(size);
        ArrayList<Type> l2 = new ArrayList<Type>(size);
        for (int i = 0; i < size; i++) {
            Type t1 = Types.get(p1.formalTypes().get(i));
            Type t2 = Types.get(p2.formalTypes().get(i));
            l1.add(t1);
            if (subst != null) {
                t2 = subst.reinstantiate(t2);
            }
            l2.add(t2);
        }
        return hasCompatibleArguments(l1,l2,context);
    }
    
    protected void duplicateTypeDefCheck(ContextVisitor tc) {
        X10ClassDef type = (X10ClassDef) tc.context().currentClassDef();

        TypeSystem ts = tc.typeSystem();

        ArrayList<TypeDef> l = new ArrayList<TypeDef>(type.memberTypes());

        for (int i = 0; i < l.size(); i++) {
            TypeDef mi = l.get(i);

            for (int j = i + 1; j < l.size(); j++) {
                TypeDef mj = l.get(j);

                if (mi.name().equals(mj.name()) &&
                        mi.typeParameters().size()==mj.typeParameters().size() &&
                        hasCompatibleArguments(mi, mj, tc.context())) {
                    Errors.issue(tc.job(),
                            new Errors.DuplicateTypeDefinition(mj, mi, mj.position()),
                            this);
                }
            }

            if (mi.formalTypes().isEmpty()) {
                for (Ref<? extends Type> tref : type.memberClasses()) {
                    Type t = Types.get(tref);
                    t = Types.baseType(t);
                    if (t instanceof ClassType) {
                        ClassType ct = (ClassType) t;
                        if (ct.name().equals(mi.name()) &&
                                ct.def().typeParameters().size()==mi.typeParameters().size()) {
                            Errors.issue(tc.job(),
                                    new Errors.TypeDefinitionSameNameAsMemberClass(mi, ct, mi.position()),
                                    this);
                        }
                    }
                }
            }
        }
    }
    
    public List<ClassMember> members() {
        return this.members;
    }

    public ClassBody members(List<ClassMember> members) {
        X10ClassBody_c n = (X10ClassBody_c) copy();
        n.members = TypedList.copyAndCheck(members, ClassMember.class, true);
        return n;
    }

    public ClassBody addMember(ClassMember member) {
        X10ClassBody_c n = (X10ClassBody_c) copy();
        List<ClassMember> l = new ArrayList<ClassMember>(this.members.size() + 1);
        l.addAll(this.members);
        l.add(member);
        n.members = TypedList.copyAndCheck(l, ClassMember.class, true);
        return n;
    }

    protected ClassBody_c reconstruct(List<ClassMember> members) {
        if (! CollectionUtil.<ClassMember>allEqual(members, this.members)) {
            X10ClassBody_c n = (X10ClassBody_c) copy();
            n.members = TypedList.copyAndCheck(members,
                                               ClassMember.class, true);
            return n;
        }

        return this;
    }

    public Node visitChildren(NodeVisitor v) {
        List<ClassMember> members = visitList(this.members, v);
        return reconstruct(members);
    }

    public Node disambiguate(ContextVisitor ar) {
        return this;
    }

    public String toString() {
        return "{ ... }";
    }

    protected void duplicateFieldCheck(ContextVisitor tc) {
        ClassDef type = tc.context().currentClassDef();

        ArrayList<FieldDef> l = new ArrayList<FieldDef>(type.fields());

        for (int i = 0; i < l.size(); i++) {
            FieldDef fi = (FieldDef) l.get(i);

            for (int j = i+1; j < l.size(); j++) {
                FieldDef fj = (FieldDef) l.get(j);

                if (fi.name().equals(fj.name())) {
                    reportDuplicate(fj,tc);
                }
            }
        }
    }
    
    protected void duplicateMemberClassCheck(ContextVisitor tc) {
        ClassDef type = tc.context().currentClassDef();

        ArrayList<Ref<? extends Type>> l = new ArrayList<Ref<? extends Type>>(type.memberClasses());

        for (int i = 0; i < l.size(); i++) {
            Type mi = l.get(i).get();

            for (int j = i+1; j < l.size(); j++) {
                Type mj = l.get(j).get();

                if (mi.name().equals(mj.name())) {
                    reportDuplicate(mj,tc);
                }
            }
        }
    }
    private void reportDuplicate(TypeObject def, ContextVisitor tc) {
        new Errors.DuplicateMember(def).issue(tc.job());

    }

    protected boolean isSameMethod(TypeSystem ts,
                                   MethodInstance mi, MethodInstance mj, Context context) {
        return mi.isSameMethod(mj, context);
    }
    
    public NodeVisitor exceptionCheckEnter(ExceptionChecker ec) {
        return ec.push();
    }

    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        if (!members.isEmpty()) {
            w.newline(4);
            w.begin(0);
	    ClassMember prev = null;

            for (Iterator<ClassMember> i = members.iterator(); i.hasNext(); ) {
                ClassMember member = i.next();
		if ((member instanceof polyglot.ast.CodeDecl) ||
		    (prev instanceof polyglot.ast.CodeDecl)) {
			w.newline(0);
		}
		prev = member;
                printBlock(member, w, tr);
                if (i.hasNext()) {
                    w.newline(0);
                }
            }

            w.end();
            w.newline(0);
        }
    }

    /**
     * Return the first (sub)term performed when evaluating this
     * term.
     */
    public Term firstChild() {
        // Do _not_ visit class members.
        return null;
    }

    /**
     * Visit this term in evaluation order.
     */
    public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
        return succs;
    }

}
