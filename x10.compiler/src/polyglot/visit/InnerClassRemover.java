/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.visit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import polyglot.ast.*;
import polyglot.frontend.Job;
import polyglot.main.Reporter;
import polyglot.types.*;
import polyglot.util.InternalCompilerError;
import polyglot.util.Pair;
import polyglot.util.Position;
import polyglot.util.UniqueID;
import polyglot.util.CollectionUtil;
import x10.util.CollectionFactory;

// TODO:
//Convert closures to anon
//Add frame classes around anon and local
//now all classes access only final locals
//Convert local and anon to member
//Dup inner member to static
//Remove inner member

public abstract class InnerClassRemover extends ContextVisitor {
    // Name of field used to carry a pointer to the enclosing class.
    public static final Name OUTER_FIELD_NAME = Name.make("out$");

    public InnerClassRemover(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }

    protected Map<ClassDef, FieldDef> outerFieldInstance = CollectionFactory.newHashMap();
    
    /** Get a reference to the enclosing instance of the current class that is of type containerClass */
    Expr getContainer(Position pos, Expr this_, ClassType currentClass, ClassType containerClass) {
        if (containerClass.def() == currentClass.def())
            return this_;
        pos = pos.markCompilerGenerated();
        ClassType currentContainer = currentClass.container().toClass();
        FieldDef fi = boxThis(currentClass, currentContainer);
        Field f = nf.Field(pos, this_, nf.Id(pos, OUTER_FIELD_NAME));
        f = f.fieldInstance(fi.asInstance());
        f = (Field) f.type(fi.asInstance().type());
        f = f.targetImplicit(false);
        return getContainer(pos, f, currentContainer, containerClass);
    }
    
    protected abstract ContextVisitor localClassRemover();

    @Override
    public Node override(Node parent, Node n) {
        if (n instanceof SourceFile) {
            ContextVisitor lcv = localClassRemover();
            lcv = (ContextVisitor) lcv.begin();
            lcv = (ContextVisitor) lcv.context(context);

            if (reporter.should_report(Reporter.innerremover, 1)) {
            	System.out.println(">>> output ----------------------");
            	n.prettyPrint(System.out);
            	System.out.println("<<< output ----------------------");
            }

            n = n.visit(lcv);

            if (reporter.should_report(Reporter.innerremover, 1)) {
            	System.out.println(">>> locals removed ----------------------");
            	n.prettyPrint(System.out);
            	System.out.println("<<< locals removed ----------------------");
            }

            n = this.visitEdgeNoOverride(parent, n);

            if (reporter.should_report(Reporter.innerremover, 1)) {
            	System.out.println(">>> inners removed ----------------------");
            	n.prettyPrint(System.out);
            	System.out.println("<<< inners removed ----------------------");
            }

            return n;
        }
        else {
            return null;
        }
    }

    protected Node leaveCall(Node old, Node n, NodeVisitor v) {
        if (n instanceof Special) {
            return fixSpecial((Special) n);
        } else if (n instanceof Field) {
            return fixField((Field) n);
        } else if (n instanceof FieldAssign) {
            return fixFieldAssign((FieldAssign) n);
        } else if (n instanceof Call) {
            return fixCall((Call) n);
        } else if (n instanceof New) {
            return fixNew((New) n);
        } else if (n instanceof ConstructorCall) {
            return fixConstructorCall((ConstructorCall) n);
        } else if (n instanceof ClassDecl) {
            return fixClassDecl((ClassDecl) n);
        } else if (n instanceof TypeNode) {
            return fixTypeNode((TypeNode) n);
        }
        return n;
    }

    protected Expr fixSpecial(Special s) {
        if (s.qualifier() == null)
            return s;
        assert s.qualifier().type().toClass() != null;
        Context context = this.context();
        if (s.qualifier().type().toClass().def() == context.currentClassDef())
            return s;
        Position pos = s.position().markCompilerGenerated();
        return getContainer(pos, nf.This(pos).type(context.currentClass()), context.currentClass(), s.qualifier().type().toClass());
    }

    protected Node fixField(Field f) {
        if (f.isTargetImplicit() && !(f.target() instanceof Special))
            return f.targetImplicit(false);
        return f;
    }

    protected Node fixFieldAssign(FieldAssign f) {
        if (f.targetImplicit() && !(f.target() instanceof Special))
            return f.targetImplicit(false);
        return f;
    }

    protected Node fixCall(Call c) {
        if (c.isTargetImplicit() && !(c.target() instanceof Special))
            return c.targetImplicit(false);
        return c;
    }

    public TypeNode fixTypeNode(TypeNode tn) {
        return tn;
    }

    public New fixNew(New neu) {
        Expr q = neu.qualifier();
        if (q == null)
            return neu;
        // Add the qualifier as an argument to constructor invocations.
        neu = neu.qualifier(null);
        ConstructorInstance ci = neu.constructorInstance();
        // Fix the ci.
        List<Type> argTypes = new ArrayList<Type>();
        argTypes.add(q.type());
        argTypes.addAll(ci.formalTypes());
        ci = ci.formalTypes(argTypes);
        neu = neu.constructorInstance(ci);
        List<Expr> args = new ArrayList<Expr>();
        args.add(q);
        args.addAll(neu.arguments());
        neu = (New) neu.arguments(args);
        return neu;
    }

    protected Node fixConstructorCall(ConstructorCall cc) {
        // Add the qualifier as an argument to constructor invocations.
        if (cc.kind() != ConstructorCall.SUPER) {
            // FIXME: [IP] Why?  What about calls to this()?
            return cc;
        }
        
        ConstructorInstance ci = cc.constructorInstance();
        
        ClassType ct = ci.container().toClass();
        
        // NOTE: we require that a constructor call to a non-static member have a qualifier.
        // We can't check for this now, though, since the type information may already have been
        // rewritten.
        
        
        
//            // Add a qualifier to non-static member class super() calls if not present.
//            if (ct.isMember() && ! ct.flags().isStatic()) {
//                if (cc.qualifier() == null) {
//                    cc = cc.qualifier(nf.This(pos).type(context.currentClass()));
//                }
//            }
        
        if (cc.qualifier() == null) {
            return cc;
        }

        Expr q = cc.qualifier();
        cc = cc.qualifier(null);
        
        boolean fixCI = cc.arguments().size()+1 != ci.formalTypes().size();
        
//            if (q == null) {
//                if (ct.isMember() && ! ct.flags().isStatic()) {
//                    q = getContainer(pos, nf.Special(pos, Special.THIS).type(context.currentClass()), context.currentClass(), ct);
//                }
//                else if (ct.isMember()) {
//                    // might have already been rewritten to static.  If so, the CI should have been rewritten also.
//                    if (((ConstructorInstance) ci.declaration()).formalTypes().size() >= cc.arguments().size()) {
//                        q = nf.Special(pos, Special.THIS).type(context.currentClass());
//                    }
//                }
//            }

        // Fix the ci if a copy; otherwise, let the ci be modified at the declaration node.
        if (fixCI) {
            List<Type> args = new ArrayList<Type>();
            args.add(q.type());
            args.addAll(ci.formalTypes());
            ci = ci.formalTypes(args);
            cc = cc.constructorInstance(ci);
        }
        
        List<Expr> args = new ArrayList<Expr>();
        args.add(q);
        args.addAll(cc.arguments());
        cc = (ConstructorCall) cc.arguments(args);

        return cc;
    }

    public static boolean isInner(ClassDef def) {
        return def.isMember() && (!def.flags().isStatic() || def.wasInner());
    }

    protected ClassDecl fixClassDecl(ClassDecl cd) {
        if (cd.classDef().isMember() && !cd.flags().flags().isStatic()) {
            cd.classDef().setWasInner(true);
            cd.classDef().flags(cd.classDef().flags().Static());
            Flags f = cd.classDef().flags();
            cd = cd.flags(cd.flags().flags(f));

            Context context = this.context();

            assert (Types.get(cd.classDef().container()).toClass().def() == context.currentClass().def());
            // Add a field for the enclosing class.
            ClassType ct = context.currentClass();
            
            FieldDef fi = boxThis(cd.classDef().asType(), ct);
            cd = addFieldsToClass(cd, Collections.singletonList(fi), ts, nf, true);
            cd = fixQualifiers(cd);
        }
        return cd;
    }

    public ClassDecl fixQualifiers(ClassDecl cd) {
        return (ClassDecl) cd.visitChildren(new NodeVisitor() {
            LocalDef li;

            @Override
            public Node override(Node parent, Node n) {
                if (n instanceof ClassBody) {
                    return null;
                }
                
                if (n instanceof ConstructorDecl) {
                    return null;
                }
                
                if (parent instanceof ConstructorDecl && n instanceof Formal) {
                    Formal f = (Formal) n;
                    LocalDef li = f.localDef();
                    if (li.name().equals(OUTER_FIELD_NAME)) {
                        this.li = li;
                    }
                    return n;
                }
                
                if (parent instanceof ConstructorDecl && n instanceof Block) {
                    return null;
                }
                
                if (parent instanceof Block && n instanceof ConstructorCall) {
                    return null;
                }
                
                
                if (parent instanceof ConstructorCall) {
                    return null;
                }
                
                return n;
//
//                if (n instanceof ClassMember) {
//                    this.li = null;
//                    return n;
//                }
//
//                return null;
            }

            @Override
            public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
                if (parent instanceof ConstructorCall && li != null && n instanceof Expr) {
                    return fixQualifier((Expr) n, li);
                }
                return n;
            }
        });
    }
    
    public Expr fixQualifier(Expr e, final LocalDef li) {
        return (Expr) e.visit(new NodeVisitor() {
            @Override
            public Node leave(Node old, Node n, NodeVisitor v) {
                if (n instanceof Field) {
                    Field f = (Field) n;
                    if (f.target() instanceof Special) {
                        Special s = (Special) f.target();
                        if (s.kind() == Special.THIS && f.name().id().equals(OUTER_FIELD_NAME)) {
                            Local l = nf.Local(n.position().markCompilerGenerated(), f.name());
                            l = l.localInstance(li.asInstance());
                            l = (Local) l.type(li.asInstance().type());
                            return l;
                        }
                    }
                }
                if (n instanceof FieldAssign) {
                    FieldAssign f = (FieldAssign) n;
                    if (f.target() instanceof Special) {
                	Special s = (Special) f.target();
                	if (s.kind() == Special.THIS && f.name().id().equals(OUTER_FIELD_NAME)) {
                	    Local l = nf.Local(n.position().markCompilerGenerated(), f.name());
                	    l = l.localInstance(li.asInstance());
                	    l = (Local) l.type(li.asInstance().type());
                	    return l;
                	}
                    }
                }
                return n;
            }
        });
    }
    
    public ClassDecl addFieldsToClass(ClassDecl cd, List<FieldDef> newFields, TypeSystem ts, NodeFactory nf, boolean rewriteMembers) {
        if (newFields.isEmpty()) {
            return cd;
        }

        ClassBody b = cd.body();
        
        // Add the new fields to the class.
        List<ClassMember> newMembers = new ArrayList<ClassMember>();
        for (Iterator<FieldDef> i = newFields.iterator(); i.hasNext(); ) {
            FieldDef fi = i.next();
            Position pos = fi.position().markCompilerGenerated();
            FieldDecl fd = nf.FieldDecl(pos, nf.FlagsNode(pos, fi.flags()), nf.CanonicalTypeNode(pos, fi.type()), nf.Id(pos, fi.name()));
            fd = fd.fieldDef(fi);
            newMembers.add(fd);
        }

        for (Iterator<ClassMember> i = b.members().iterator(); i.hasNext(); ) {
            ClassMember m = i.next();
            if (m instanceof ConstructorDecl) {
                ConstructorDecl td = (ConstructorDecl) m;

                // Create a list of formals to add to the constructor.
                List<Formal> formals = new ArrayList<Formal>();
                List<LocalDef> locals = new ArrayList<LocalDef>();
                
                for (FieldDef fi : newFields) {
                    Position pos = fi.position().markCompilerGenerated();
                    LocalDef li = ts.localDef(pos, Flags.FINAL, fi.type(), fi.name());
                    li.setNotConstant();
                    Formal formal = nf.Formal(pos, nf.FlagsNode(pos, li.flags()), nf.CanonicalTypeNode(pos, li.type()), nf.Id(pos, li.name()));
                    formal = formal.localDef(li);
                    formals.add(formal);
                    locals.add(li);
                }
                
                List<Formal> newFormals = new ArrayList<Formal>();
                newFormals.addAll(formals);
                newFormals.addAll(td.formals());
                td = td.formals(newFormals);

                // Create a list of field assignments.
                List<Stmt> statements = new ArrayList<Stmt>();

                for (int j = 0; j < newFields.size(); j++) {
                    FieldDef fi = newFields.get(j);
                    LocalDef li = formals.get(j).localDef();

                    Position pos = fi.position().markCompilerGenerated();

                    Local l = nf.Local(pos, nf.Id(pos, li.name()));
                    l = (Local) l.type(li.asInstance().type());
                    l = l.localInstance(li.asInstance());

                    FieldAssign a = nf.FieldAssign(pos, nf.This(pos).type(fi.asInstance().container()), nf.Id(pos, fi.name()), Assign.ASSIGN, l);
                    a = (FieldAssign) a.type(fi.asInstance().type());
                    a = (FieldAssign) a.fieldInstance(fi.asInstance());
                    a = a.targetImplicit(false);

                    Eval e = nf.Eval(pos, a);
                    statements.add(e);
                }
                
                // Add the assignments to the constructor body after the super call.
                // Or, add pass the locals to another constructor if a this call.
                Block block = td.body();
                if (block.statements().size() > 0) {
                    Stmt s0 = (Stmt) block.statements().get(0);
                    if (s0 instanceof ConstructorCall) {
                        ConstructorCall cc = (ConstructorCall) s0;
                        ConstructorInstance ci = cc.constructorInstance();
                        if (cc.kind() == ConstructorCall.THIS) {
                            // Not a super call.  Pass the locals as arguments.
                            List<Expr> arguments = new ArrayList<Expr>();
                            for (Iterator<Stmt> j = statements.iterator(); j.hasNext(); ) {
                                Stmt si = j.next();
                                Eval e = (Eval) si;
                                Assign a = (Assign) e.expr();
                                arguments.add(a.right());
                            }
                            
                            // Modify the CI if it is a copy of the declaration CI.
                            // If not a copy, it will get modified at the declaration.
                            {
                        	List<Type> newFormalTypes = new ArrayList<Type>();
                                for (int j = 0; j < newFields.size(); j++) {
                                    FieldDef fi = newFields.get(j);
                                    newFormalTypes.add(fi.asInstance().type());
                                }
                                newFormalTypes.addAll(ci.formalTypes());
                                ci = ci.formalTypes(newFormalTypes);
                                cc = cc.constructorInstance(ci);
                            }
                            
                            arguments.addAll(cc.arguments());
                            cc = (ConstructorCall) cc.arguments(arguments);
                        }
                        else {
                            // A super call.  Don't rewrite it here; the visitor will handle it elsewhere.
                        }
                        
                        // prepend the super call
                        statements.add(0, cc);
                    } else {
                    	// [DC] The absence of this case in previous versions was a bug
                    	// but I believe it would only have caused a problem with the root (Object)
                    	// which was not an inner class (and was @NativeRep anyway)
                    	statements.add(s0);
                    }
                    statements.addAll(block.statements().subList(1, block.statements().size()));
                }
                else {
                    statements.addAll(block.statements());
                }

                block = block.statements(statements);
                td = (ConstructorDecl) td.body(block);

                newMembers.add(td);

                adjustConstructorFormals(td.constructorDef(), newFormals);
            }
            else {
                newMembers.add(m);
            }
        }

        b = b.members(newMembers);
        return cd.body(b);
    }

    protected void adjustConstructorFormals(ConstructorDef ci, List<Formal> newFormals) {
        List<Ref<? extends Type>> newFormalTypes = new ArrayList<Ref<? extends Type>>();
        for (Formal f : newFormals) {
            newFormalTypes.add(f.type().typeRef());
        }
        ci.setFormalTypes(newFormalTypes);
    }

    // Add local variables to the argument list until it matches the declaration.
    List<Expr> addArgs(ProcedureCall n, ConstructorInstance nci, Expr q) {
        if (nci == null || q == null)
            return n.arguments();
        List<Expr> args = new ArrayList<Expr>();
        args.add(q);
        args.addAll(n.arguments());
        assert args.size() == nci.formalTypes().size();
        return args;
    }

    // Create a field instance for a qualified this.
    protected FieldDef boxThis(ClassType currClass, ClassType outerClass) {
        FieldDef fi = outerFieldInstance.get(currClass.def());
        if (fi != null) return fi;
        
        Position pos = outerClass.position();
        
        fi = ts.fieldDef(pos, Types.ref(currClass), Flags.FINAL.Private(), Types.ref(outerClass), OUTER_FIELD_NAME);
        fi.setNotConstant();
        
        currClass.def().addField(fi); // FIXME: should boxThis() be idempotent?
        
        outerFieldInstance.put(currClass.def(), fi);
        return fi;
    }

    public static <K,V> V hashGet(Map<K,V> map, K k, V v) {
        return LocalClassRemover.<K,V>hashGet(map, k, v);
    }
}
