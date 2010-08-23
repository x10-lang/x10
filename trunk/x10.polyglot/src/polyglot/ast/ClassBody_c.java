/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2007 Polyglot project group, Cornell University
 * Copyright (c) 2006-2007 IBM Corporation
 * 
 */

package polyglot.ast;

import java.util.*;

import polyglot.main.Report;
import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;

/**
 * A <code>ClassBody</code> represents the body of a class or interface
 * declaration or the body of an anonymous class.
 */
public class ClassBody_c extends Term_c implements ClassBody
{
    protected List<ClassMember> members;

    public ClassBody_c(Position pos, List<ClassMember> members) {
        super(pos);
        assert(members != null);
        this.members = TypedList.copyAndCheck(members, ClassMember.class, true);
    }

    public List<ClassMember> members() {
        return this.members;
    }

    public ClassBody members(List<ClassMember> members) {
        ClassBody_c n = (ClassBody_c) copy();
        n.members = TypedList.copyAndCheck(members, ClassMember.class, true);
        return n;
    }

    public ClassBody addMember(ClassMember member) {
        ClassBody_c n = (ClassBody_c) copy();
        List<ClassMember> l = new ArrayList<ClassMember>(this.members.size() + 1);
        l.addAll(this.members);
        l.add(member);
        n.members = TypedList.copyAndCheck(l, ClassMember.class, true);
        return n;
    }

    protected ClassBody_c reconstruct(List<ClassMember> members) {
        if (! CollectionUtil.<ClassMember>allEqual(members, this.members)) {
            ClassBody_c n = (ClassBody_c) copy();
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

    public Node disambiguate(ContextVisitor ar) throws SemanticException {
        return this;
    }

    public String toString() {
        return "{ ... }";
    }

    protected void duplicateFieldCheck(ContextVisitor tc) throws SemanticException {
        ClassDef type = tc.context().currentClassDef();

        ArrayList<FieldDef> l = new ArrayList<FieldDef>(type.fields());

        for (int i = 0; i < l.size(); i++) {
            FieldDef fi = (FieldDef) l.get(i);

            for (int j = i+1; j < l.size(); j++) {
                FieldDef fj = (FieldDef) l.get(j);

                if (fi.name().equals(fj.name())) {
                    throw new SemanticException("Duplicate field \"" + fj + "\".", fj.position());
                }
            }
        }
    }

    protected void duplicateConstructorCheck(ContextVisitor tc) throws SemanticException {
        ClassDef type = tc.context().currentClassDef();
        TypeSystem ts = tc.typeSystem();

        ArrayList<ConstructorDef> l = new ArrayList<ConstructorDef>(type.constructors());

        for (int i = 0; i < l.size(); i++) {
            ConstructorDef ci = l.get(i);
            ConstructorInstance ti = ci.asInstance();

            for (int j = i+1; j < l.size(); j++) {
                ConstructorDef cj = l.get(j);
                ConstructorInstance tj = cj.asInstance();

                if (ti.hasFormals(tj.formalTypes(), tc.context())) {
                    throw new SemanticException("Duplicate constructor \"" + cj + "\".", cj.position());
                }
            }
        }
    }

    protected void duplicateMethodCheck(ContextVisitor tc) throws SemanticException {
        ClassDef type = tc.context().currentClassDef();

        TypeSystem ts = tc.typeSystem();

        ArrayList<MethodDef> l = new ArrayList<MethodDef>(type.methods());

        for (int i = 0; i < l.size(); i++) {
            MethodDef mi = l.get(i);
            MethodInstance ti = mi.asInstance();

            for (int j = i+1; j < l.size(); j++) {
                MethodDef mj = l.get(j);
                MethodInstance tj = mj.asInstance();

                if (ti.isSameMethod(tj, tc.context())) {
                    throw new SemanticException("Duplicate method \"" + mj + "\".", mj.position());
                }
            }
        }
    }

    protected void duplicateMemberClassCheck(ContextVisitor tc) throws SemanticException {
        ClassDef type = tc.context().currentClassDef();

        ArrayList<Ref<? extends Type>> l = new ArrayList<Ref<? extends Type>>(type.memberClasses());

        for (int i = 0; i < l.size(); i++) {
            Type mi = l.get(i).get();

            for (int j = i+1; j < l.size(); j++) {
                Type mj = l.get(j).get();

                if (mi instanceof Named && mj instanceof Named) {
                    if (((Named) mi).name().equals(((Named) mj).name())) {
                        throw new SemanticException("Duplicate member type \"" + mj + "\".", mj.position());
                    }
                }
            }
        }
    }

    protected boolean isSameMethod(TypeSystem ts,
                                   MethodInstance mi, MethodInstance mj, Context context) {
        return mi.isSameMethod(mj, context);
    }

    public Node conformanceCheck(ContextVisitor tc) throws SemanticException {
        duplicateFieldCheck(tc);
        duplicateConstructorCheck(tc);
        duplicateMethodCheck(tc);
        duplicateMemberClassCheck(tc);
        return this;
    }
    
    public NodeVisitor exceptionCheckEnter(ExceptionChecker ec) throws SemanticException {
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
    public List<Term> acceptCFG(CFGBuilder v, List<Term> succs) {
        return succs;
    }

    private static final Collection<String> TOPICS = 
                CollectionUtil.list(Report.types, Report.context);
     
}
