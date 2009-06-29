/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.visit;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Block;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassMember;
import polyglot.ast.ConstructorCall;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Eval;
import polyglot.ast.FieldAssign;
import polyglot.ast.FieldDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Special;
import polyglot.ast.Stmt;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.frontend.Job;
import polyglot.types.FieldDef;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;

/**
 * Visitor that inserts boxing and unboxing code into the AST.
 */
public class FieldInitializerMover extends ContextVisitor {
    X10TypeSystem xts;

    public FieldInitializerMover(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (X10TypeSystem) ts;
    }
    
    @Override
    public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        if (n instanceof ClassBody) {
            ClassBody cb = (ClassBody) n;
            List<Stmt> assignments = new ArrayList<Stmt>();
            List<ClassMember> members = new ArrayList<ClassMember>();
            
            for (ClassMember cm : cb.members()) {
                if (cm instanceof FieldDecl) {
                    FieldDecl fd = (FieldDecl) cm;
                    FieldDef def = fd.fieldDef();

                    if (fd.init() != null && !def.flags().isStatic() && !def.isConstant()) {
                        Position p = fd.init().position();
                        Special this_ = nf.This(p);
                        this_ = (Special) this_.type(def.asInstance().container());
                        FieldAssign a = nf.FieldAssign(p, this_, nf.Id(p, def.name()), Assign.ASSIGN, fd.init());
                        a = a.fieldInstance(def.asInstance());
                        a = (FieldAssign) a.type(def.asInstance().type());
                        
                        assert this_.type() != null;
                        assert a.type() != null;
                        
                        Eval eval = nf.Eval(p, a);
                        assignments.add(eval);
                        
                        fd = fd.init(null);
                    }

                    members.add(fd);
                }
                else {
                    members.add(cm);
                }
            }
            
            if (assignments.size() > 0) {
                List<ClassMember> members2 = new ArrayList<ClassMember>();

                for (ClassMember cm : members) {
                    if (cm instanceof ConstructorDecl) {
                        ConstructorDecl cd = (ConstructorDecl) cm;

                        Block body = cd.body();
                        List<Stmt> stmts = body.statements();
                        if (stmts.size() > 0) {
                            Stmt s = stmts.get(0);
                            if (s instanceof ConstructorCall) {
                                ConstructorCall cc = (ConstructorCall) s;
                                if (cc.kind() == ConstructorCall.SUPER) {
                                    List<Stmt> ss = new ArrayList<Stmt>();
                                    ss.add(s);
                                    ss.addAll(assignments);
                                    ss.addAll(stmts.subList(1, stmts.size()));
                                    body = body.statements(ss);
                                }
                            }
                            else {
                                // implicit super call
                                List<Stmt> ss = new ArrayList<Stmt>();
                                ss.addAll(assignments);
                                ss.addAll(stmts);
                                body = body.statements(ss);
                            }
                        }
                        else {
                            // implicit super call
                            List<Stmt> ss = new ArrayList<Stmt>();
                            ss.addAll(assignments);
                            ss.addAll(stmts);
                            body = body.statements(ss);
                        }
                        
                        if (body != cd.body()) {
                            cd = (ConstructorDecl) cd.body(body);
                        }
                        
                        members2.add(cd);
                    }
                    else {
                        members2.add(cm);
                    }
                }
                
                members = members2;
            }
            
            return cb.members(members);
        }
        
        return super.leaveCall(old, n, v);
    }
}
