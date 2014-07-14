/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */
package x10c.visit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.ast.Assign;
import polyglot.ast.Block;
import polyglot.ast.Call;
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
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.VarDef;
import polyglot.types.VarInstance;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.AtExpr;
import x10.ast.AtStmt;
import x10.ast.ClosureCall;
import x10.ast.SettableAssign;
import x10.ast.X10Binary_c;
import x10.ast.X10CanonicalTypeNode;
import x10.ast.X10Unary_c;
import x10.types.EnvironmentCapture;
import x10.types.X10ConstructorInstance;
import x10.types.X10LocalDef;
import x10.types.MethodInstance;
import x10.types.X10ParsedClassType;
import x10.types.checker.Converter;

public class VarsBoxer extends ContextVisitor {

    private static final String POSTFIX_BOXED_VAR = "$b";
    private static final QName LOCAL_VAR = QName.make("x10.compiler.LocalVar");
    private static final Name GET = Name.make("get");
    private static final Name SET = Name.make("set");
    
    private final TypeSystem xts;
    private final NodeFactory xnf;
    private final Map<X10LocalDef,X10LocalDef> defToDef = CollectionFactory.newHashMap();
    private X10ParsedClassType localVarType;
    
    public VarsBoxer(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (TypeSystem) ts;
        xnf = (NodeFactory) nf;
    }
    
    @Override
    public NodeVisitor begin() {
        try {
            localVarType = (X10ParsedClassType) xts.forName(LOCAL_VAR);
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
            List<VarInstance<? extends VarDef>> caps = atSt.atDef().capturedEnvironment();
            atSt = atSt.body((Stmt) atSt.body().visit(createAtExtraNodeVisitor(context2, privatizations, caps, atSt.atDef())));
            atSt = atSt.body((Stmt) atSt.body().visit(createAtOuterVarAccessVisitor(context2, outerLocals, caps)));
            
            if (outerLocals.size() > 0 || privatizations.size() > 0) {
                List<Stmt> newBody = new ArrayList<Stmt>();
                
                addPrivatizationDeclToBody(atSt.atDef(), context2, privatizations, outerLocals, newBody);
                
                Stmt body = addBoxValWriteBackCallToBody(context2, atSt, outerLocals);
                
                newBody.add(body);
                
                atSt = atSt.body(xnf.Block(Position.COMPILER_GENERATED, newBody));
                
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
                
                List<VarInstance<? extends VarDef>> caps = atEx.closureDef().capturedEnvironment();
                
                atEx = (AtExpr) atEx.body((Block) atEx.body().visit(createAtExtraNodeVisitor(context2, privatizations, caps, atEx.closureDef())));
                atEx = (AtExpr) atEx.body((Block) atEx.body().visit(createAtOuterVarAccessVisitor(context2, outerLocals, caps)));

                if (outerLocals.size() > 0 || privatizations.size() > 0) {
                    List<Stmt> newBody = new ArrayList<Stmt>();
                    
                    addPrivatizationDeclToBody(atEx.closureDef(), context2, privatizations, outerLocals, newBody);
                    
                    Stmt body = addBoxValWriteBackCallToBody(context2, atEx, outerLocals);
                    
                    newBody.add(body);
                    
                    atEx = (AtExpr) atEx.body(xnf.Block(Position.COMPILER_GENERATED, newBody));
                    
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
    private Stmt addBoxValWriteBackCallToBody(Context context2, AtStmt atSt, List<Name> outerLocals) {
        Stmt body = (Stmt) atSt.body().visit(createAddWriteBackCallVisitor(context2, outerLocals));
        return body;
    }

    // add write back call to box val in the body
    // TODO optimize write back once when returning the body
    private Stmt addBoxValWriteBackCallToBody(Context context2, AtExpr atEx, List<Name> outerLocals) {
        Stmt body = (Stmt) atEx.body().visit(createAddWriteBackCallVisitor(context2, outerLocals));
        return body;
    }

    private ContextVisitor createAddWriteBackCallVisitor(Context context2, final List<Name> outerLocals) {
        return new ContextVisitor(job, ts, nf) {
            @Override
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
                    if (outerLocals.contains(local.name().id())) {
                        LocalInstance libox = getBoxLocalDef(Position.COMPILER_GENERATED, local.localInstance()).asInstance();
                        return createSetCall(Position.COMPILER_GENERATED, local.localInstance(), libox, la);
                    }
                }
                if (n instanceof X10Unary_c) {
                    X10Unary_c unary = (X10Unary_c) n;
                    Expr expr = unary.expr();
                    if (expr instanceof Local) {
                        Local local = (Local) expr;
                        if (unary.operator() == X10Unary_c.PRE_DEC || unary.operator() == X10Unary_c.PRE_INC) {
                            if (outerLocals.contains(local.name().id())) {
                                LocalInstance libox = getBoxLocalDef(Position.COMPILER_GENERATED, local.localInstance()).asInstance();
                                return createSetCall(Position.COMPILER_GENERATED, local.localInstance(), libox, unary);
                            }
                        }
                        else if (unary.operator() == X10Unary_c.POST_INC) {
                            if (outerLocals.contains(local.name().id())) {
                                LocalInstance libox = getBoxLocalDef(Position.COMPILER_GENERATED, local.localInstance()).asInstance();
                                Expr one = getLiteral(Position.COMPILER_GENERATED, local.type(), 1);
                                return xnf.Binary(Position.COMPILER_GENERATED, createSetCall(Position.COMPILER_GENERATED, local.localInstance(), libox, xnf.Binary(Position.COMPILER_GENERATED, unary, X10Binary_c.ADD, one).type(local.type())), X10Binary_c.SUB, one).type(local.type());
                            }
                        }
                        else if (unary.operator() == X10Unary_c.POST_DEC) {
                            if (outerLocals.contains(local.name().id())) {
                                LocalInstance libox = getBoxLocalDef(Position.COMPILER_GENERATED, local.localInstance()).asInstance();
                                Expr one = getLiteral(Position.COMPILER_GENERATED, local.type(), 1);
                                return xnf.Binary(Position.COMPILER_GENERATED, createSetCall(Position.COMPILER_GENERATED, local.localInstance(), libox, xnf.Binary(Position.COMPILER_GENERATED, unary, X10Binary_c.SUB, one).type(local.type())), X10Binary_c.ADD, one).type(local.type());
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
        type = Types.baseType(type);
        Expr lit = null;
        if (ts.isByte(type)) {
            lit = nf.IntLit(pos, IntLit.BYTE, val);
        } else if (ts.isUByte(type)) {
            lit = nf.IntLit(pos, IntLit.UBYTE, val);
        } else if (ts.isShort(type)) {
            lit = nf.IntLit(pos, IntLit.SHORT, val);
        } else if (ts.isUShort(type)) {
            lit = nf.IntLit(pos, IntLit.USHORT, val);
        } else if (ts.isInt(type)) {
            lit = nf.IntLit(pos, IntLit.INT, val);
        } else if (xts.isLong(type)) {
            lit = xnf.IntLit(pos, IntLit.LONG, val);
        } else if (xts.isUInt(type)) {
            lit = xnf.IntLit(pos, IntLit.UINT, val);
        } else if (xts.isULong(type)) {
            lit = xnf.IntLit(pos, IntLit.ULONG, val);
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
    private void addPrivatizationDeclToBody(EnvironmentCapture ec, Context context2, final List<LocalDecl> privatizations,
                                     final List<Name> outerLocals, List<Stmt> stmts) throws SemanticException {
        for (Name name : outerLocals) {
            LocalInstance li = context2.findLocal(name);
            stmts.add(createDeclForPrivatization(Position.COMPILER_GENERATED, ec, li));
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

    private ContextVisitor createAtExtraNodeVisitor(Context context2, final List<LocalDecl> privatizations,
                                          final List<VarInstance<? extends VarDef>> caps, final EnvironmentCapture ec) {
        return new ContextVisitor(job, ts, nf) {
            @Override
            public Node override(Node parent, Node n) {
                if (n instanceof AtStmt || n instanceof AtExpr) {
                    return n;
                }
                return null;
            };
            
            public Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
                // check privatization node and remove
                if (n instanceof LocalDecl) {
                    LocalDecl ldecl = (LocalDecl) n;
                    LocalInstance li;
                    try {
                        li = context.findLocal(Name.make(ldecl.name().toString().replace(POSTFIX_BOXED_VAR, "")));
                    } catch (SemanticException e) {
                        return n;
                    }
                    if (containsCapturedEnv(caps, li)) {
                        replaceCapturedEnv(Position.COMPILER_GENERATED, ec, li);
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
                                if (containsCapturedEnv(caps, ((LocalAssign) e).local().localInstance())) {
                                    return null;
                                }
                            }
                        }
                    }
                }
                return n;
            }
        }.context(context2);
    }

    private ContextVisitor createAtOuterVarAccessVisitor(Context context2, final List<Name> outerLocals,
                                          final List<VarInstance<? extends VarDef>> caps) {
        return new ContextVisitor(job, ts, nf) {
            @Override
            public Node override(Node parent, Node n) {
                if (n instanceof AtStmt || n instanceof AtExpr) {
                    return n;
                }
                return null;
            };
            
            public Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
                if (n instanceof Local) {
                    Local local = (Local) n;
                    Name name = local.name().id();
                    if (parent instanceof LocalAssign) {
                        if (((LocalAssign) parent).right() instanceof Call) {
                            Call call = (Call) ((LocalAssign) parent).right();
                            Receiver receiver = call.target();
                            if (receiver instanceof Local) {
                                if (((Local) receiver).name().id().toString().endsWith(POSTFIX_BOXED_VAR)) {
                                    if (containsCapturedEnv(caps, ((LocalAssign) parent).local().localInstance())) {
                                        return n;
                                    }
                                }
                            }
                        }
                    }
                    if (containsCapturedEnv(caps, local.localInstance()) && !local.flags().isFinal() && !outerLocals.contains(name) && !name.toString().endsWith(POSTFIX_BOXED_VAR)) {
                        outerLocals.add(name);
                    }
                }
                return n;
            }
        }.context(context2);
    }

    private static boolean containsCapturedEnv(final List<VarInstance<? extends VarDef>> caps, LocalInstance li) {
        for (VarInstance<? extends VarDef> vi : caps) {
            if (vi.def().equals(li.def())) return true;
        }
        return false;
    }
    
    private LocalDecl createBoxDecl(final Position pos, Name name, LocalInstance li) {
        X10ParsedClassType lvt = createLocalVarType(li);
        X10CanonicalTypeNode tn = xnf.X10CanonicalTypeNode(pos, lvt);
        
        LocalDecl ldecl = xnf.LocalDecl(pos, xnf.FlagsNode(pos, Flags.FINAL), tn, xnf.Id(pos, name.toString() + POSTFIX_BOXED_VAR));
        
        X10LocalDef localDef = getBoxLocalDef(pos, li);
        
        ldecl = ldecl.localDef(localDef);
        
        Local local = (Local) xnf.Local(pos, xnf.Id(pos, name)).localInstance(li).type(li.type());
        List<Expr> args = new ArrayList<Expr>();
        args.add(local);
        
        X10ConstructorInstance ci;
        try {
            ci = xts.findConstructor(lvt, xts.ConstructorMatcher(lvt, lvt.typeArguments(), context));
        } catch (SemanticException e) {
            throw new InternalCompilerError(""); // TODO
        }
        ci = (X10ConstructorInstance) ci.container(lvt);
        New new1 = (New) xnf.New(pos, tn, args).constructorInstance(ci).type(lvt);
        
        ldecl = ldecl.init(new1);
        return ldecl;
    }

    private X10LocalDef getBoxLocalDef(final Position pos, LocalInstance li) {
        X10LocalDef def = (X10LocalDef) li.def();
        if (defToDef.containsKey(def)) {
            return defToDef.get(def);
        }
        else {
            X10ParsedClassType lvt = createLocalVarType(li);
            X10LocalDef localDef = xts.localDef(pos, li.flags(), Types.ref(lvt), Name.make(li.name().toString() + POSTFIX_BOXED_VAR));
            defToDef.put(def, localDef);
            return localDef;
        }
    }

    private LocalDecl createDeclForPrivatization(final Position pos, EnvironmentCapture ec, LocalInstance li) {
        LocalDecl ldecl = xnf.LocalDecl(pos, xnf.FlagsNode(pos, li.flags()), xnf.X10CanonicalTypeNode(pos, li.type()), xnf.Id(pos, li.name()));
        ldecl = ldecl.localDef(li.def());
        X10LocalDef boxld = getBoxLocalDef(pos, li);
        LocalInstance boxli = boxld.asInstance();
        replaceCapturedEnv(pos, ec, li);
        
        Call call = createGetCall(pos, li, boxli);
        ldecl = ldecl.init(call);
        return ldecl;
    }
    
    private void replaceCapturedEnv(final Position pos, EnvironmentCapture ec, LocalInstance li) {
        List<VarInstance<? extends VarDef>> newEnv = new ArrayList<VarInstance<? extends VarDef>>();
        for (VarInstance<? extends VarDef> vi : ec.capturedEnvironment()) {
            if (vi.equals(li)) {
                newEnv.add(getBoxLocalDef(pos, li).asInstance());
            } else {
                newEnv.add(vi);
            }
        }
        ec.setCapturedEnvironment(newEnv);
    }

    private Call createGetCall(final Position pos, LocalInstance lilocal, LocalInstance libox) {
        Name mname = GET;
        
        X10ParsedClassType lvt = createLocalVarType(lilocal);
        
        MethodDef md = xts.methodDef(pos, pos, Types.ref(lvt), Flags.FINAL, Types.ref(localVarType.x10Def().typeParameters().get(0)), mname, Collections.<Ref<? extends Type>>emptyList(), Collections.<Ref<? extends Type>>emptyList());
        MethodInstance mi = md.asInstance();
        
        Local local = (Local) xnf.Local(pos, xnf.Id(pos, lilocal.name().toString() + POSTFIX_BOXED_VAR)).type(libox.type());
        return (Call) xnf.Call(pos, local.localInstance(libox), xnf.Id(pos, mname)).methodInstance(mi).type(lilocal.type());
    }
    
    private Call createSetCall(final Position pos, LocalInstance lilocal, LocalInstance libox, Expr arg) {
        Name mname = SET;
        
        List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>();
        argTypes.add(Types.ref(localVarType.x10Def().typeParameters().get(0)));

        X10ParsedClassType lvt = createLocalVarType(lilocal);
        
        MethodDef md = xts.methodDef(pos, pos, Types.ref(lvt), Flags.FINAL, Types.ref(localVarType.x10Def().typeParameters().get(0)), mname, argTypes, Collections.<Ref<? extends Type>>emptyList());
        MethodInstance mi = md.asInstance();
        
        Local local = (Local) xnf.Local(pos, xnf.Id(pos, lilocal.name().toString() + POSTFIX_BOXED_VAR)).localInstance(libox).type(libox.type());
        
        return (Call) xnf.Call(pos, local, xnf.Id(pos, mname), arg).methodInstance(mi).type(lilocal.type());
    }

    private Call createApplyCall(final Position pos, LocalInstance lilocal, LocalInstance libox) {
        Name mname = ClosureCall.APPLY;
        
        X10ParsedClassType lvt = createLocalVarType(lilocal);
        
        MethodDef md = xts.methodDef(pos, pos, Types.ref(lvt), Flags.FINAL, Types.ref(localVarType.x10Def().typeParameters().get(0)), mname, Collections.<Ref<? extends Type>>emptyList(), Collections.<Ref<? extends Type>>emptyList());
        MethodInstance mi = md.asInstance();
        
        Local local = (Local) xnf.Local(pos, xnf.Id(pos, lilocal.name().toString() + POSTFIX_BOXED_VAR)).type(libox.type());
        return (Call) xnf.Call(pos, local.localInstance(libox), xnf.Id(pos, mname)).methodInstance(mi).type(lilocal.type());
    }

    private LocalAssign createWriteBack(final Position cg, LocalInstance li, LocalDecl ldecl) {
        LocalAssign la = (LocalAssign) xnf.LocalAssign(cg, (Local) xnf.Local(cg, xnf.Id(cg, li.name())).localInstance(li).type(li.type()), Assign.ASSIGN, createApplyCall(cg, li, ldecl.localDef().asInstance())).type(li.type());
        return la;
    }

    private X10ParsedClassType createLocalVarType(LocalInstance li) {
        List<Type> typeArgs = new ArrayList<Type>();
        typeArgs.add(li.type());
        return localVarType.typeArguments(typeArgs);
    }
}
