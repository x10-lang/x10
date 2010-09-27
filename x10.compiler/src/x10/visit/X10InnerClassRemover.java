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

package x10.visit;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Block;
import polyglot.ast.ClassMember;
import polyglot.ast.ConstructorCall;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Eval;
import polyglot.ast.FieldAssign;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.FieldDef;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.InnerClassRemover;
import polyglot.visit.NodeVisitor;
import x10.ast.X10ClassDecl;
import x10.ast.X10FieldDecl;
import x10.types.TypeParamSubst;
import x10.types.X10ClassType;
import x10.types.X10Flags;

/**
 * This class rewrites inner classes to static nested classes.
 * As part of the rewrite, it first transforms local classes
 * (including anonymous classes) to inner/nested classes using
 * an instance of {@link X10LocalClassRemover}.
 */
public class X10InnerClassRemover extends InnerClassRemover {
    public X10InnerClassRemover(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }

    @Override
    protected ContextVisitor localClassRemover() {
        return new X10LocalClassRemover(job, ts, nf);
    }

    @Override
    protected Node leaveCall(Node old, Node n, NodeVisitor v) {
        // FIX:XTENLANG-215
        if (n instanceof X10ClassDecl) {
            X10ClassDecl cd = (X10ClassDecl) n;
            ClassDef def = cd.classDef();
            if (def.isMember() && !def.flags().isStatic()) {
                List<ClassMember> newMember = new ArrayList<ClassMember>();
                List<X10FieldDecl> fieldDs = new ArrayList<X10FieldDecl>();
                List<ClassMember> members = cd.body().members();
                // remove initializers of final variables;
                // final int bar = foo(define outer class val) -> final int bar;  
                for (ClassMember classMember : members) {
                    if (classMember instanceof X10FieldDecl) {
                        X10FieldDecl fieldD = (X10FieldDecl) classMember;
                        if (fieldD.fieldDef().flags().isFinal() && !fieldD.fieldDef().flags().isStatic() && fieldD.init() != null) {
                            fieldDs.add(fieldD);
                            newMember.add(fieldD.init(null));
                            continue;
                        }
                    }
                    newMember.add(classMember);
                }
                // add code to initialize final variables after super()
                for (int i = 0; i < members.size(); i++) {
                    ClassMember classMember = members.get(i);
                    if (classMember instanceof ConstructorDecl) {
                        ArrayList<Stmt> statements = new ArrayList<Stmt>();
                        for (X10FieldDecl fieldD : fieldDs) {
                            Position pos = fieldD.position();
                            FieldDef fi = fieldD.fieldDef();
                            FieldAssign a = nf.FieldAssign(pos, nf.This(pos).type(fi.asInstance().container()), nf.Id(pos, fi.name()), Assign.ASSIGN, fieldD.init());
                            a = (FieldAssign) a.type(fi.asInstance().type());
                            a = a.fieldInstance(fi.asInstance());
                            a = a.targetImplicit(false);

                            Eval e = nf.Eval(pos, a);
                            statements.add(e);
                        }
                        ConstructorDecl decl = (ConstructorDecl) classMember;
                        Block block = decl.body();
                        if (block.statements().size() > 0) {
                            Stmt stmt = block.statements().get(0);
                            if (stmt instanceof ConstructorCall) {
                                statements.add(0, stmt);
                            }
                            statements.addAll(block.statements().subList(1, block.statements().size()));
                        }
                        newMember.set(i,(ClassMember) decl.body(block.statements(statements)));
                    }
                }
                n = cd.body(cd.body().members(newMember));
            }
        }
        return super.leaveCall(old, n, v);
    }

    @Override
    protected FieldDef boxThis(ClassType currClass, ClassType outerClass) {
        X10ClassType currType = (X10ClassType) currClass;
        TypeParamSubst subst = X10LocalClassRemover.inverseSubst(currType);
        if (subst != null)
            outerClass = subst.reinstantiate(outerClass);
        FieldDef fi = super.boxThis(currClass, outerClass);
        if (!(fi.flags() instanceof X10Flags))
            fi.setFlags(X10Flags.toX10Flags(fi.flags()));
        return fi;
    }
}