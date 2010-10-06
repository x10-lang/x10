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
package x10c.visit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.FloatLit;
import polyglot.ast.IntLit;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.frontend.Job;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LocalInstance;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.AtExpr;
import x10.ast.AtStmt;
import x10.ast.X10Binary_c;
import x10.ast.X10CanonicalTypeNode;
import x10.ast.X10IntLit_c;
import x10.ast.X10NodeFactory;
import x10.ast.X10Unary_c;
import x10.types.X10ConstructorInstance;
import x10.types.X10Context;
import x10.types.X10LocalDef;
import x10.types.X10ParsedClassType;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.checker.Converter;

public class VarsBoxer extends ContextVisitor {
    private static final String POSTFIX_BOXED_VAR = "$boxed";

    private static final QName GLOBAL_REF = QName.make("x10.lang.GlobalRef");
    
    private final X10TypeSystem xts;
    private final X10NodeFactory xnf;
    private X10ParsedClassType globalRefType;
    
    public VarsBoxer(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (X10TypeSystem) ts;
        xnf = (X10NodeFactory) nf;
    }
    
    @Override
    public NodeVisitor begin() {
        try {
            globalRefType = (X10ParsedClassType) xts.typeForName(GLOBAL_REF);
        } catch (SemanticException e) {
            throw new InternalCompilerError("Something is terribly wrong", e);
        }
        return super.begin();
    }
    
    @Override
    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        
        if (n instanceof AtStmt) {
            Context context2 = n.enterScope(context);

            final List<LocalDecl> privatizations = new ArrayList<LocalDecl>();
            final List<Name> outerLocals = new ArrayList<Name>();
            
            AtStmt atSt = (AtStmt) n;
            atSt = atSt.body((Stmt) atSt.body().visit(createAtVisitor(context2, privatizations, outerLocals)));
            
            if (outerLocals.size() > 0 || privatizations.size() > 0) {
                List<Stmt> newBody = new ArrayList<Stmt>();
                
                addPrivatizationDeclToBody(context2, privatizations, outerLocals, newBody);
                
                Stmt body = addBoxValWriteBackCallToBody(context2, atSt);
                
                newBody.add(body);
                
                atSt = atSt.body(xnf.StmtSeq(Position.COMPILER_GENERATED, newBody));
                
                List<Stmt> stmts1 = addOuterNodes(context2, privatizations, outerLocals, atSt);

                return xnf.Block(Position.COMPILER_GENERATED, stmts1);
            }
            return n;
        }
        
        if (n instanceof LocalDecl) {
            LocalDecl ld = (LocalDecl) n;
            Expr e = ld.init();
            if (e instanceof AtExpr) {
                AtExpr atEx = (AtExpr) e;
                
                Context context2 = e.enterScope(context);
                
                final List<LocalDecl> privatizations = new ArrayList<LocalDecl>();
                final List<Name> outerLocals = new ArrayList<Name>();
                
                atEx = (AtExpr) atEx.body((Block) atEx.body().visit(createAtVisitor(context2, privatizations, outerLocals)));
                
                if (outerLocals.size() > 0 || privatizations.size() > 0) {
                    List<Stmt> newBody = new ArrayList<Stmt>();
                    
                    addPrivatizationDeclToBody(context2, privatizations, outerLocals, newBody);
                    
                    Stmt body = addBoxValWriteBackCallToBody(context2, atEx);
                    
                    newBody.add(body);
                    
                    atEx = (AtExpr) atEx.body(xnf.StmtSeq(Position.COMPILER_GENERATED, newBody));
                    
                    Stmt at = ld.init(atEx);
                    
                    List<Stmt> stmts1 = addOuterNodes(context2, privatizations, outerLocals, at);
                    
                    return xnf.StmtSeq(Position.COMPILER_GENERATED, stmts1);
                }
                return n;
            }
        }
        return n;
    }

    // add write back call to box val in the body
    // TODO optimize write back once when returning the body
    private Stmt addBoxValWriteBackCallToBody(Context context2, AtStmt atSt) {
        Stmt body = (Stmt) atSt.body().visit(createAddWriteBackCallVisitor(context2));
        return body;
    }

    // add write back call to box val in the body
    // TODO optimize write back once when returning the body
    private Stmt addBoxValWriteBackCallToBody(Context context2, AtExpr atEx) {
        Stmt body = (Stmt) atEx.body().visit(createAddWriteBackCallVisitor(context2));
        return body;
    }

    public ContextVisitor createAddWriteBackCallVisitor(Context context2) {
        return new ContextVisitor(job, ts, nf) {
            public Node override(Node parent, Node n) {
                if (n instanceof AtStmt || n instanceof AtExpr) {
                    return n;
                }
                return null;
            };
            public Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
                if (n instanceof LocalAssign) {
                    LocalAssign la = (LocalAssign) n;
                    Local local = la.local();
                    if (!context.isLocal(local.name().id())) {
                        LocalInstance libox = createBoxLocalDef(Position.COMPILER_GENERATED, local.localInstance()).asInstance();
                        return createSetCall(Position.COMPILER_GENERATED, local.localInstance(), libox, la);
                    }
                }
                if (n instanceof X10Unary_c) {
                    X10Unary_c unary = (X10Unary_c) n;
                    Expr expr = unary.expr();
                    if (expr instanceof Local) {
                        Local local = (Local) expr;
                        if (unary.operator() == X10Unary_c.PRE_DEC || unary.operator() == X10Unary_c.PRE_INC) {
                            if (!context.isLocal(local.name().id())) {
                                LocalInstance libox = createBoxLocalDef(Position.COMPILER_GENERATED, local.localInstance()).asInstance();
                                return createSetCall(Position.COMPILER_GENERATED, local.localInstance(), libox, unary);
                            }
                        }
                        else if (unary.operator() == X10Unary_c.POST_INC) {
                            if (!context.isLocal(local.name().id())) {
                                LocalInstance libox = createBoxLocalDef(Position.COMPILER_GENERATED, local.localInstance()).asInstance();
                                Expr one = getLiteral(Position.COMPILER_GENERATED, local.type(), 1);
                                return xnf.Binary(Position.COMPILER_GENERATED, createSetCall(Position.COMPILER_GENERATED, local.localInstance(), libox, xnf.Binary(Position.COMPILER_GENERATED, unary, X10Binary_c.ADD, one).type(local.type())), X10Binary_c.SUB, one);
                            }
                        }
                        else if (unary.operator() == X10Unary_c.POST_DEC) {
                            if (!context.isLocal(local.name().id())) {
                                LocalInstance libox = createBoxLocalDef(Position.COMPILER_GENERATED, local.localInstance()).asInstance();
                                Expr one = getLiteral(Position.COMPILER_GENERATED, local.type(), 1);
                                return xnf.Binary(Position.COMPILER_GENERATED, createSetCall(Position.COMPILER_GENERATED, local.localInstance(), libox, xnf.Binary(Position.COMPILER_GENERATED, unary, X10Binary_c.SUB, one).type(local.type())), X10Binary_c.ADD, one);
                            }
                        }
                        return n;
                    }
                }
                return n;
            };
        }.context(context2);
    }

    // copied from Desugarer
    private Expr getLiteral(Position pos, Type type, long val) throws SemanticException {
        type = X10TypeMixin.baseType(type);
        Expr lit = null;
        if (xts.isIntOrLess(type)) {
            lit = xnf.IntLit(pos, IntLit.INT, val);
        } else if (xts.isLong(type)) {
            lit = xnf.IntLit(pos, IntLit.LONG, val);
        } else if (xts.isUInt(type)) {
            lit = xnf.IntLit(pos, X10IntLit_c.UINT, val);
        } else if (xts.isULong(type)) {
            lit = xnf.IntLit(pos, X10IntLit_c.ULONG, val);
        } else if (xts.isFloat(type)) {
            lit = xnf.FloatLit(pos, FloatLit.FLOAT, val);
        } else if (xts.isDouble(type)) {
            lit = xnf.FloatLit(pos, FloatLit.DOUBLE, val);
        } else if (xts.isChar(type)) {
            // Don't want to cast
            return (Expr) xnf.IntLit(pos, IntLit.INT, val).typeCheck(this);
        } else
            throw new InternalCompilerError(pos, "Unknown literal type: "+type);
        lit = (Expr) lit.typeCheck(this);
        if (!xts.isSubtype(lit.type(), type)) {
            lit = xnf.X10Cast(pos, xnf.CanonicalTypeNode(pos, type), lit,
                    Converter.ConversionType.PRIMITIVE).type(type);
        }
        return lit;
    }
    
    // add decls for privatization
    private void addPrivatizationDeclToBody(Context context2, final List<LocalDecl> privatizations,
                                     final List<Name> outerLocals, List<Stmt> stmts) throws SemanticException {
        for1:for (Name name : outerLocals) {
            LocalInstance li = context2.findLocal(name);
            stmts.add(createDeclForPrivatization(Position.COMPILER_GENERATED, li));
        }
    }

    private List<Stmt> addOuterNodes(Context context2, final List<LocalDecl> privatizations, final List<Name> outerLocals,
                                  Stmt st) throws SemanticException {
        List<Stmt> stmts1 = new ArrayList<Stmt>();
        
        stmts1.add(st);
        
        for (LocalDecl ldecl : privatizations) {
            stmts1.add(0, ldecl);
            LocalInstance li = context2.findLocal(Name.make(ldecl.name().toString().replace(POSTFIX_BOXED_VAR, "")));
            stmts1.add(xnf.Eval(Position.COMPILER_GENERATED, createWriteBack(Position.COMPILER_GENERATED, li, ldecl)));
        }
        
        for1:for (Name name : outerLocals) {
            for (LocalDecl ldecl : privatizations) {
                if (ldecl.name().toString().replace(POSTFIX_BOXED_VAR, "").equals(name.toString())) {
                    continue for1;
                }
            }
            LocalInstance li = context2.findLocal(name);
            // create decl for box
            LocalDecl ldecl = createBoxDecl(Position.COMPILER_GENERATED, name, li);
            stmts1.add(0, ldecl);
            
            // create write back node
            stmts1.add(xnf.Eval(Position.COMPILER_GENERATED, createWriteBack(Position.COMPILER_GENERATED, li, ldecl)));
        }
        return stmts1;
    }

    private ContextVisitor createAtVisitor(Context context2, final List<LocalDecl> privatizations,
                                          final List<Name> outerLocals) {
        return new ContextVisitor(job, ts, nf) {
            public Node override(Node parent, Node n) {
                if (n instanceof AtStmt || n instanceof AtExpr) { // TODO async
                    return n;
                }
                return null;
            };
            
            public Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
                // check access node to outer var 
                if (n instanceof Local) {
                    Local local = (Local) n;
                    X10Context xcontext = (X10Context) context;
                    Name name = local.name().id();
                    if (parent instanceof LocalAssign) {
                        if (((LocalAssign) parent).right() instanceof Call) {
                            Call call = (Call) ((LocalAssign) parent).right();
                            Receiver receiver = call.target();
                            if (receiver instanceof Local) {
                                if (((Local) receiver).name().id().toString().endsWith(POSTFIX_BOXED_VAR)) {
                                    if (!context.isLocal(((LocalAssign) parent).local().name().id())) {
                                        return n;
                                    }
                                }
                            }
                        }
                    }
                    // TODO 
                    if (!xcontext.isLocal(name) && !local.flags().isFinal() && !outerLocals.contains(name) && !name.toString().endsWith(POSTFIX_BOXED_VAR)) {
                        outerLocals.add(name);
                    }
                }
                // check privatization node and remove
                if (n instanceof LocalDecl) {
                    LocalDecl ldecl = (LocalDecl) n;
                    LocalInstance li;
                    try {
                        li = context.findLocal(Name.make(ldecl.name().toString().replace(POSTFIX_BOXED_VAR, "")));
                    } catch (SemanticException e) {
                        return n;
                    }
                    if (!context.isLocal(li.name())) {
                        privatizations.add(ldecl);
                        return null;
                    }
                }
                // check write back node and remove
                if (n instanceof Eval) {
                    Expr e = ((Eval) n).expr();
                    if ((e instanceof LocalAssign) && (((LocalAssign) e).right() instanceof Call) ) {
                        Call call = (Call) ((LocalAssign) e).right();
                        Receiver receiver = call.target();
                        if (receiver instanceof Local) {
                            if (((Local) receiver).name().id().toString().endsWith(POSTFIX_BOXED_VAR)) {
                                if (!context.isLocal(((LocalAssign) e).local().name().id())) {
                                    return null;
                                }
                            }
                        }
                    }
                }
                return n;
            };
        }.context(context2);
    }

    private LocalDecl createBoxDecl(final Position cg, Name name, LocalInstance li) {
        List<Type> typeArgs = new ArrayList<Type>();
        typeArgs.add(li.type());
        X10ParsedClassType grt = globalRefType.typeArguments(typeArgs);
        X10CanonicalTypeNode tn = xnf.X10CanonicalTypeNode(cg, grt);
        
        LocalDecl ldecl = xnf.LocalDecl(cg, xnf.FlagsNode(cg, Flags.FINAL), tn, xnf.Id(cg, name.toString() + POSTFIX_BOXED_VAR));
        
        X10LocalDef localDef = createBoxLocalDef(cg, li);
        
        ldecl = ldecl.localDef(localDef);
        
        Local local = xnf.Local(cg, xnf.Id(cg, name)).localInstance(li);
        List<Expr> args = new ArrayList<Expr>();
        args.add(local);
        
        X10ConstructorInstance ci;
        try {
            ci = xts.findConstructor(globalRefType, xts.ConstructorMatcher(globalRefType, globalRefType.typeArguments(), context));
        } catch (SemanticException e) {
            throw new InternalCompilerError(""); // TODO
        }
        ci = (X10ConstructorInstance) ci.container(grt);
        New new1 = (New) xnf.New(cg, tn, args).constructorInstance(ci).type(globalRefType);
        
        ldecl = ldecl.init(new1);
        return ldecl;
    }

    private X10LocalDef createBoxLocalDef(final Position pos, LocalInstance li) {
        X10LocalDef localDef = xts.localDef(pos, li.flags(), Types.ref(li.type()), xnf.Id(pos, li.name().toString() + POSTFIX_BOXED_VAR).id());
        return localDef;
    }

    private LocalDecl createDeclForPrivatization(final Position pos, LocalInstance li) {
        LocalDecl ldecl = xnf.LocalDecl(pos, xnf.FlagsNode(pos, li.flags()), xnf.X10CanonicalTypeNode(pos, li.type()), xnf.Id(pos, li.name()));
        ldecl = ldecl.localDef(xts.localDef(pos, li.flags(), Types.ref(li.type()), li.name()));
        
        Call call = createGetCall(pos, li, createBoxLocalDef(pos, li).asInstance());
        ldecl = ldecl.init(call);
        return ldecl;
    }

    private Call createGetCall(final Position pos, LocalInstance lilocal, LocalInstance libox) {
        Name mname = Name.make("apply");
        
        List<Type> typeArgs = new ArrayList<Type>();
        typeArgs.add(lilocal.type());
        X10ParsedClassType grt = globalRefType.typeArguments(typeArgs);
        
        MethodDef md = xts.methodDef(pos, Types.ref(grt), Flags.FINAL, Types.ref(globalRefType.typeArguments().get(0)), mname, Collections.<Ref<? extends Type>>emptyList());
        MethodInstance mi = md.asInstance();
        
        Local local = xnf.Local(pos, xnf.Id(pos, lilocal.name().toString() + POSTFIX_BOXED_VAR));
        return (Call) xnf.Call(pos, local.localInstance(libox), xnf.Id(pos, mname)).methodInstance(mi).type(lilocal.type());
    }
    
    private Call createSetCall(final Position pos, LocalInstance lilocal, LocalInstance libox, Expr arg) {
        Name mname = Name.make("set");
        
        List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>();
        argTypes.add(Types.ref(arg.type()));

        List<Type> typeArgs = new ArrayList<Type>();
        typeArgs.add(lilocal.type());
        X10ParsedClassType grt = globalRefType.typeArguments(typeArgs);
        
        MethodDef md = xts.methodDef(pos, Types.ref(grt), Flags.FINAL, Types.ref(globalRefType.typeArguments().get(0)), mname, argTypes);
        MethodInstance mi = md.asInstance();
        
        Local local = xnf.Local(pos, xnf.Id(pos, lilocal.name().toString() + POSTFIX_BOXED_VAR)).localInstance(libox);
        
        return (Call) xnf.Call(pos, local, xnf.Id(pos, mname), arg).methodInstance(mi).type(lilocal.type());
    }

    private LocalAssign createWriteBack(final Position cg, LocalInstance li, LocalDecl ldecl) {
        LocalAssign la = (LocalAssign) xnf.LocalAssign(cg, (Local) xnf.Local(cg, xnf.Id(cg, li.name())).localInstance(li).type(li.type()), Assign.ASSIGN, createGetCall(cg, li, ldecl.localDef().asInstance())).type(li.type());
        return la;
    }
}
