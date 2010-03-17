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
import polyglot.types.FieldDef;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.InnerClassRemover;
import polyglot.visit.NodeVisitor;
import x10.ast.X10ClassDecl_c;
import x10.ast.X10FieldDecl_c;
import x10.types.TypeParamSubst;
import x10.types.X10ClassType;
import x10.types.X10Flags;

public class X10InnerClassRemover extends InnerClassRemover {
    public X10InnerClassRemover(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }

    @Override
    protected ContextVisitor localClassRemover() {
        return new X10LocalClassRemover(job, ts, nf);
    }

    @Override
    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        // FIX:XTENLANG-215
        if (n instanceof X10ClassDecl_c) {
            X10ClassDecl_c cd = (X10ClassDecl_c) n;
            if (cd.classDef().isMember() && !cd.classDef().flags().isStatic()){
                List<ClassMember> newMember = new ArrayList<ClassMember>();
                List<X10FieldDecl_c> fieldDs = new ArrayList<X10FieldDecl_c>();
                List<ClassMember> members = cd.body().members();
                // remove initializers of final variables;
                // final int bar = foo(define outer class val) -> final int bar;  
                for (ClassMember classMember : members) {
                    if (classMember instanceof X10FieldDecl_c) {
                        X10FieldDecl_c fieldD = (X10FieldDecl_c) classMember;
                        if (fieldD.fieldDef().flags().isFinal() && fieldD.init() != null) {
                            fieldDs.add(fieldD);
                            newMember.add(fieldD.init(null));
                            continue;
                        }
                    }
                    newMember.add(classMember);
                }
                // add code to initialize final variables after super()
                for (int i = 0; i < members.size(); i ++) {
                    ClassMember classMember = members.get(i);
                    if (classMember instanceof ConstructorDecl) {
                        ArrayList<Stmt> statements = new ArrayList<Stmt>();
                        for (X10FieldDecl_c fieldD : fieldDs) {
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
        return super.leaveCall(parent, old, n, v);
    }

    // Create a field instance for a qualified this.
    protected FieldDef boxThis(ClassDef currClass, ClassDef outerClass) {
        FieldDef fi = outerFieldInstance.get(currClass);
        if (fi != null) return fi;
        
        Position pos = outerClass.position();
        
        X10ClassType currType = (X10ClassType) currClass.asType();
        TypeParamSubst subst = X10LocalClassRemover.inverseSubst(currType);
        Type outerType = outerClass.asType();
        if (subst != null)
            outerType = subst.reinstantiate(outerType);
        
        fi = ts.fieldDef(pos, Types.ref(currClass.asType()), X10Flags.GLOBAL.Final().Private(), Types.ref(outerType), OUTER_FIELD_NAME);
        fi.setNotConstant();
        
        currClass.addField(fi);
        
        outerFieldInstance.put(currClass, fi);
        return fi;
    }
}