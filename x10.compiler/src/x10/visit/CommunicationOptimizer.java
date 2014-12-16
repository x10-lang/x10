/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 *  (C) Rice University 2010-2011.
 *  
 *  Code contributed to the X10 project by Rice under XTENLANG-2685.
 */
package x10.visit;

import polyglot.ast.*;
import polyglot.frontend.Job;
import polyglot.types.ClassType;
import polyglot.types.FieldInstance;
import polyglot.types.LocalDef;
import x10.types.MethodInstance;
import polyglot.types.Context;
import polyglot.types.Name;
import polyglot.types.NoClassException;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.TypeSystem_c;
import polyglot.types.Types;
import polyglot.types.LocalInstance;
import polyglot.types.VarInstance;
import polyglot.types.VarDef;
import polyglot.util.CollectionUtil;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.ErrorHandlingVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.*;
import x10.types.*;
import x10.constraint.*;
import x10.emitter.*;
import x10.X10CompilerOptions;
import x10.util.Synthesizer;
import x10.Configuration;
import x10.extension.X10Ext;

import java.util.*;

/**
 * Implementation of the communication optimizations described in
 * "Communication Optimizations for Distirbuted Memory X10 Programs" by 
 *   Barik, Zhao, Grove, Peshansky, Budimlic and Sarkar published in IPDPS 2011.
 * 
 * The core idea is to reduce redundant data serialization via
 * compiler transformations such as scalar replacement and task localization.
 * Optimization opportunities can be exposed via loop transformations such
 * as splitting, tiling, and distribution. 
 *
 * @author jisheng zhao
 * @author raj barik
 */
public class CommunicationOptimizer extends ContextVisitor {
    private final TypeSystem_c xts;
    private final X10NodeFactory_c xnf;
    private final Synthesizer synth;
    private final boolean shouldOpt;

    public CommunicationOptimizer(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (TypeSystem_c) ts;
        xnf = (X10NodeFactory_c) nf;
        synth = new Synthesizer(xnf, xts);
        shouldOpt = ((X10CompilerOptions)job.extensionInfo().getOptions()).x10_config.OPTIMIZE_COMMUNICATIONS;
    }

    private static Name getTmp() {
        return Name.makeFresh("comopt__var");
    }

    @Override
    public Node override(Node parent, Node n) {
        if (n instanceof Eval) {
        }

        return null;
    }

    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        if (shouldOpt) {
            if (n instanceof Async) 
                return visitAsync((Async)n);
            else if (n instanceof AtEach)
                return visitAtEach((AtEach)n);
            else if (n instanceof AtStmt)
                return visitAt((AtStmt)n);
            else if (n instanceof Block) {
                Block b = (Block) n;
                /*if (parent instanceof X10Loop_c)
                    return processX10Loop(parent, b);
                else if (parent instanceof Loop_c)
                    return processLoop(parent, b);
                else*/
                return processNormalBlock(parent, b);
            }
        }

        return n;
    }

    /**
     * Visit the async closure
     *
     * @param async
     */
    protected Async visitAsync(Async a) throws SemanticException {
        List<VarInstance<? extends VarDef>> env = a.asyncDef().capturedEnvironment();
        List<VarInstance<? extends VarDef>> env1 = new ArrayList<VarInstance<? extends VarDef>>(env);
        Stmt newBody = processAsync(a, (Block)a.body(), env1);

        Async newAsync = a.body(newBody);

        newAsync.asyncDef().setCapturedEnvironment(env1);

        return newAsync;
    }

    /**
     * Visit the ateach closure
     *
     * @param ateach
     */
    protected AtEach visitAtEach(AtEach ateach) throws SemanticException {
        List<VarInstance<? extends VarDef>> env = ateach.atDef().capturedEnvironment();
        List<VarInstance<? extends VarDef>> env1 = new ArrayList<VarInstance<? extends VarDef>>(env);

        Stmt newBody = processAtEach(ateach, (Block)ateach.body(), env1);
        System.out.println("proc ateach");
        AtEach newAteach = (AtEach)ateach.body(newBody);

        newAteach.atDef().setCapturedEnvironment(env1);

        return newAteach;
    }

    /**
     * Visit the ateach closure
     *
     * @param ateach
     */
    protected Stmt visitAt(AtStmt at) throws SemanticException {
        List<VarInstance<? extends VarDef>> env = at.atDef().capturedEnvironment();
        List<VarInstance<? extends VarDef>> env1 = new ArrayList<VarInstance<? extends VarDef>>(env);
        Stmt newBody = processAt(at, (Block)at.body(), env1);

        AtStmt newAt = at.body(newBody);
        newAt.atDef().setCapturedEnvironment(env1);

        return newAt;
    }

    /**     
     * Process the block in loop closure               
     *                                               
     * @param parent loop node                               
     * @param block                                 
     */
    private Block processLoop(Node parent, Block b) {
        // Let's handle the X10Loop's block                      
        List<Stmt> ss = new ArrayList<Stmt>();
        for (Stmt s : b.statements()) {
            if (s instanceof Async || s instanceof AtEach || s instanceof AtStmt 
                    || s instanceof X10Loop/* || s instanceof Future */|| s instanceof Loop_c) {
                ArrayList<Stmt> stmtList = scalarReplaceMap.get(s.position());
                if (stmtList != null && stmtList.size() != 0) {
                    // Process the statements                            
                    for (Stmt ts : stmtList) {
                        Stmt stmt = processStmt(ts, (Loop_c)parent, null);
                        if (stmt != null) {
                            ss.add(stmt);
                        }
                    }
                }
                ss.add(s);
                continue;
            }

            Stmt stmt = processStmt(s, (Loop_c)parent, null);
            if (stmt != null)
                ss.add(stmt);
        }

        return b.statements(ss);
    }

    /**                                                                      
     * Process the block in X10Loop closure       
     * TODO: if this process is necessary? or just use normal loop handler to replace 
     * @param parent X10 loop node                                                
     * @param block                                                              
     */
    private Block processX10Loop(Node parent, Block b) {
        // Let's handle the X10Loop's block                            
        List<Stmt> ss = new ArrayList<Stmt>();
        for (Stmt s : b.statements()) {
            if (s instanceof Async || s instanceof AtEach || s instanceof AtStmt || s instanceof X10Loop/* || s instanceof Future*/) {
                ArrayList<Stmt> stmtList = scalarReplaceMap.get(((X10Loop)s).position());
                if (stmtList != null && stmtList.size() != 0) {
                    // Process the statements 
                    for (Stmt ts : stmtList) {
                        Stmt stmt = processStmt(ts, (X10Loop)parent, null);
                        if (stmt != null) {
                            ss.add(stmt);
                        }
                    }
                }
                ss.add(s);
                continue;
            }

            Stmt stmt = processStmt(s, (X10Loop)parent, null);
            if (stmt != null)
                ss.add(stmt);
        }

        return b.statements(ss);
    }

    /**
     * Process the block in async closure
     *
     * @param parent async node
     * @param block
     */
    private Block processAsync(Node parent, Block b, List<VarInstance<? extends VarDef>> env) throws SemanticException {
        // Let's handle the async's block                                                     
        List<Stmt> ss = new ArrayList<Stmt>();
        // Add the scalar replaced stmts
        for (Stmt s : b.statements()) {
            if (s instanceof Async || s instanceof AtEach || s instanceof AtStmt 
                    || s instanceof X10Loop || s instanceof Loop_c) {
                ArrayList<Stmt> stmtList = scalarReplaceMap.get(s.position());
                if (stmtList != null && stmtList.size() != 0) {
                    // Process the statements
                    for (Stmt ts : stmtList) {
                        Stmt stmt = processStmt(ts, (Async_c)parent, env);
                        if (stmt != null) {
                            ss.add(stmt);
                        } else
                            ss.add(ts);
                    }
                }

                if (s instanceof AtStmt)
                    ss.add(localizeAt((AtStmt)s));
                else
                    ss.add(s);

                continue;
            }

            // TODO: check place id
            Stmt stmt = processStmt(s, (Async_c)parent, env);
            if (stmt != null) {
                ss.add(stmt);
            }
        }

        return b.statements(ss);
    }

    /**
     * Process the block in ateach closure
     *
     * @param parent ateach node
     * @param block
     */
    private Block processAtEach(Node parent, Block b, List<VarInstance<? extends VarDef>> env) throws SemanticException {
        // Let's handle the ateach's block                       
        List<Stmt> ss = new ArrayList<Stmt>();
        // Add the scalar replaced stmts
        for (Stmt s : b.statements()) {
            if (s instanceof Async || s instanceof AtEach || s instanceof AtStmt 
                    || s instanceof X10Loop || s instanceof Loop_c) {
                ArrayList<Stmt> stmtList = scalarReplaceMap.get(s.position());
                if (stmtList != null && stmtList.size() != 0) {
                    // Process the statements
                    for (Stmt ts : stmtList) {
                        Stmt stmt = processStmt(ts, (AtEach)parent, env);
                        if (stmt != null) {
                            ss.add(stmt);
                        } else
                            ss.add(ts);
                    }
                }

                if (s instanceof AtStmt)
                    ss.add(localizeAt((AtStmt)s));
                else
                    ss.add(s);
                continue;
            }

            // TODO: AtEach special processing, i.e. checking place id
            Stmt stmt = processStmt(s, (AtEach)parent, env);
            if (stmt != null)
                ss.add(stmt);
        }

        return b.statements(ss);
    }

    /**
     * Process the block in at stmt
     *
     * @param parent at node
     * @param block
     * @param closure env
     */
    private Block processAt(Node parent, Block b, List<VarInstance<? extends VarDef>> env) throws SemanticException {
        // Let's handle the at's block        
        List<Stmt> ss = new ArrayList<Stmt>();
        // Add the scalar replaced stmts
        for (Stmt s : b.statements()) {
            if (s instanceof Async || s instanceof AtEach || s instanceof AtStmt 
                    || s instanceof X10Loop || s instanceof Loop_c) {
                ArrayList<Stmt> stmtList = scalarReplaceMap.get(s.position());
                // Process the statements
                if (stmtList != null && stmtList.size() > 0)
                    for (Stmt ts : stmtList) {
                        Stmt stmt = processStmt(ts, (AtStmt)parent, env);
                        if (stmt != null) {
                            ss.add(stmt);
                        } else 
                            ss.add(ts);
                    }

                if (s instanceof AtStmt)
                    ss.add(localizeAt((AtStmt)s));
                else
                    ss.add(s);

                continue;
            }

            // TODO: At special processing, i.e. checking place id  
            Stmt stmt = processStmt(s, (AtStmt)parent, env);
            if (stmt != null)
                ss.add(stmt);
        }

        return b.statements(ss);
    }

    /**
     * Process the normal block which is not async, ateach or at
     *
     * @param block
     */
    private Block processNormalBlock(Node parent, Block b) throws SemanticException {
        // For those block those parent are the scalar replacement related constructs, we skip
        if (parent instanceof Async || parent instanceof AtEach || parent instanceof AtStmt)
            return b;

        // Let's hande the normal block
        List<Stmt> ss = new ArrayList<Stmt>();
        for (Stmt s : b.statements()) {
            if (s instanceof Async || s instanceof AtEach || s instanceof AtStmt 
                    || s instanceof Loop_c) {
                ArrayList<Stmt> stmtList = scalarReplaceMap.get(s.position());
                if (stmtList != null) {
                    // Insert the scalar replacement statements here                        
                    ss.addAll(stmtList);
                }
            }

            if (s instanceof AtStmt)
                ss.add(localizeAt((AtStmt)s));
            else
                ss.add(s);
        }

        return b.statements(ss);
    }

    /**
     * Process the stmt inside the async, ateach and at
     *
     * @param stmt
     * @param closure
     */
    private Stmt processStmt(Stmt s, Term outerClosure, List<VarInstance<? extends VarDef>> env) {
        if (s instanceof Eval_c) {
            if (((Eval)s).expr() instanceof X10Call_c)
                return xnf.Eval(s.position(), handleCall((X10Call_c)((Eval)s).expr(), outerClosure, env));
            else if (((Eval)s).expr() instanceof Assign_c)
                return xnf.Eval(s.position(), handleAssign((Assign)((Eval)s).expr(), outerClosure, env));
            return s;
        } else if (s instanceof X10LocalDecl_c)
            return handleLocalDecl((X10LocalDecl_c)s, outerClosure, env);
        else if (s instanceof X10Return_c) 
            return handleX10Return((X10Return_c)s, outerClosure, env);
        else if (s instanceof If) 
            return handleIf((If)s, outerClosure, env);
        else 
            return s;
    }

    /**
     * Handle the assign expr 
     *
     * @param Assign expr
     * @param the outer closure
     * @param closure env
     */
    private Expr handleAssign(Assign assign, Term outerClosure, List<VarInstance<? extends VarDef>> env) {
        // Only the settable assign need to be considered for handling the left side array ref and 
        // index expr;
        if (assign instanceof SettableAssign) {
            assign = handleSettableAssign((SettableAssign)assign, outerClosure, env);
        }

        // Check the right side of expr
        Expr right = assign.right();
        if (right instanceof X10Call_c) {
            assign = assign.right(handleCall((X10Call_c)right, outerClosure, env));
        } else if (right instanceof Binary) {
            assign = assign.right(handleBinary((Binary)right, outerClosure, env));
        } else if (right instanceof X10Field_c) {
            assign = assign.right(handleField((X10Field_c)right, outerClosure, env));
        }

        return assign;
    }

    /**                                                           
     * Handle the case for Settable Assign       
     *                                                        
     * @param Settable Assign expr
     * @param the outer closure  
     */
    private SettableAssign handleSettableAssign(SettableAssign setAssign, Term outerClosure, List<VarInstance<? extends VarDef>> env) {
        Expr array = setAssign.array();
        if (array instanceof X10Call_c)
            setAssign = setAssign.array(handleCall((X10Call_c)array, outerClosure, env));

        ArrayList<Expr> newIndex = new ArrayList<Expr>();
        Iterator<Expr> indexIter = setAssign.index().iterator();
        while (indexIter.hasNext()) {
            Expr index = indexIter.next();
            if (index instanceof X10Call_c)
                newIndex.add(handleCall((X10Call_c)index, outerClosure, env));
            else if (index instanceof Binary)
                newIndex.add(handleBinary((Binary)index, outerClosure, env));
            else
                newIndex.add(index);
        }
        setAssign = setAssign.index(newIndex);

        return setAssign;
    }

    /**
     * Handle the case for method call
     *
     * @param method call expr
     * @param the outer closure
     * @param closure environment
     */
    private Expr handleCall(X10Call_c call, Term outerClosure, List<VarInstance<? extends VarDef>> env) { 
        // Handling the nested call AST                                                      
        if (call.target() instanceof X10Call_c) {
            call = (X10Call_c)call.target(handleCall((X10Call_c)call.target(), outerClosure, env));
        }                                                                              

        if (hasInvariantReceiver(call.target(), outerClosure, env)) {
            Type classType = call.target().type();

            // Verify the array
            if (xts.isRegionArray(classType)
                    && !xts.isX10RegionDistArray(classType)
                    && call.methodInstance().name().equals(OperatorNames.APPLY)) {
                // Check the method arguments, here we only handle .apply(ind)
                List<Expr> arguments = call.arguments();
                for (Expr arg : arguments) {
                    if (!checkInvariant(arg, outerClosure, env))
                        // There's variant, so can't apply scalar replacement
                        return call;
                }

                // Create the new local variable which is final                  
                Position pos = call.position();                                  
                Name srName = Name.makeFresh(getReceiverName(call.target()));
                Type valType = TypeSystem_c.getArrayComponentType(classType);                   
                LocalDef srDef = xts.localDef(pos, xts.Final(), Types.ref(valType), srName);
                // Create the scalar replacement statement
                LocalDecl srLocalDecl = xnf.LocalDecl(pos, xnf.FlagsNode(pos, xts.Final()), xnf.CanonicalTypeNode(pos, Types.ref(valType)), xnf.Id(pos, srName), call).localDef(srDef);
                regScalarReplace(outerClosure, srLocalDecl);                         
                // Create the scalar assignment statement         
                X10Local_c valLocal = (X10Local_c)xnf.Local(pos, xnf.Id(pos, srName)).localInstance(srDef.asInstance()).type(valType);
                if (env != null) env.add(valLocal.localInstance());

                return valLocal;
            }
        }

        return call;
    }

    /**
     * Handle the case for class field
     *
     * @param class field ref
     * @param the outer closure
     * @param closure environment
     */
    private Expr handleField(X10Field_c field, Term outerClosure, List<VarInstance<? extends VarDef>> env) {
        if (!hasInvariantReceiver(field.target(), outerClosure, env))
            return field;

        Position pos = field.position();                                 
        Name srName = Name.makeFresh(field.name().toString());

        Type valType = field.fieldInstance().type();
        LocalDef srDef = xts.localDef(pos, xts.Final(), Types.ref(valType), srName);  

        // Create the scalar replacement statement
        Id srId = xnf.Id(pos, srName);            
        LocalDecl srLocalDecl = xnf.LocalDecl(pos, xnf.FlagsNode(pos, xts.Final()), xnf.CanonicalTypeNode(pos, Types.ref(valType)), srId, field).localDef(srDef);
        regScalarReplace(outerClosure, srLocalDecl);

        // Create the scalar assignment statement                                    
        X10Local_c valLocal = (X10Local_c)xnf.Local(pos, srId).localInstance(srDef.asInstance()).type(srLocalDecl.type().typeRef().get());
        if (env != null) env.add(valLocal.localInstance());
        return valLocal;
    }

    /**
     * Handle the case for local declaration
     *
     * @param the local declaration statement
     * @param the outer closure
     */
    private Stmt handleLocalDecl(X10LocalDecl_c localDecl, Term outerClosure, List<VarInstance<? extends VarDef>> env) {
        if (localDecl.init() instanceof X10Call_c) {                                             
            X10Call_c call = (X10Call_c)localDecl.init(); 

            if (hasInvariantReceiver(call.target(), outerClosure, env)) {    
                Type classType = call.target().type(); 

                // Verify the array
                if (xts.isRegionArray(classType) 
                        && !xts.isX10RegionDistArray(classType)
                        && call.methodInstance().name().equals(OperatorNames.APPLY)) { 
                    // Check the method arguments, here we only handle .apply(ind)           
                    List<Expr> arguments = call.arguments();                                 
                    for (Expr arg : arguments) {
                        if (!checkInvariant(arg, outerClosure, env))
                            // There's variant, so can't apply scalar replacement
                            return (Stmt)localDecl;
                    }

                    // Create the new local variable which is final                  
                    Position pos = localDecl.position();                             
                    Name srName = Name.makeFresh(localDecl.name().toString());
                    LocalDef srDef = xts.localDef(pos, xts.Final(), localDecl.type().typeRef(), srName);
                    // Create the scalar replacement statement
                    Id srId = xnf.Id(pos, srName);
                    LocalDecl srLocalDecl = xnf.LocalDecl(pos, xnf.FlagsNode(pos, xts.Final()), xnf.CanonicalTypeNode(pos, localDecl.type().typeRef()), srId, call).localDef(srDef);

                    regScalarReplace(outerClosure, srLocalDecl); 

                    // Create the scalar assignment statement                        
                    // Name tmpName = Name.make(localDecl.name().toString()); 
                    // LocalDef lDef = xts.localDef(pos, xts.NoFlags(), localDecl.type().typeRef(), tmpName);      
                    X10Local_c valLocal = (X10Local_c)xnf.Local(pos, srId).localInstance(srDef.asInstance()).type(srLocalDecl.type().typeRef().get());                              
                    if (env != null) env.add(valLocal.localInstance());

                    LocalDecl newLocalDecl = localDecl.init(valLocal);
                    // xnf.LocalDecl(pos, xnf.FlagsNode(pos, xts.Final()), xnf.CanonicalTypeNode(pos, localDecl.type().typeRef()), xnf.Id(pos, tmpName), valLocal).localDef(lDef); 
                    return newLocalDecl;      
                }         
            }                                                                                    
        } else if (localDecl.init() instanceof X10Field_c) {
            X10Field_c field = (X10Field_c)localDecl.init();

            if (!hasInvariantReceiver(field.target(), outerClosure, env))
                return (Stmt)localDecl;

            Position pos = localDecl.position();                                
            Name srName = Name.makeFresh(localDecl.name().toString());
            LocalDef srDef = xts.localDef(pos, xts.Final(), localDecl.type().typeRef(), srName); 

            // Create the scalar replacement statement
            Id srId = xnf.Id(pos, srName);
            LocalDecl srLocalDecl = xnf.LocalDecl(pos, xnf.FlagsNode(pos, xts.Final()), xnf.CanonicalTypeNode(pos, localDecl.type().typeRef()), srId, field).localDef(srDef);

            regScalarReplace(outerClosure, srLocalDecl);

            // Create the scalar assignment statement  
            X10Local_c valLocal = (X10Local_c)xnf.Local(pos, srId).localInstance(srDef.asInstance()).type(srLocalDecl.type().typeRef().get());
            if (env != null) env.add(valLocal.localInstance());
            LocalDecl newLocalDecl = localDecl.init(valLocal);
            return newLocalDecl;
        }

        return (Stmt)localDecl;
    }

    /**
     * Handle the case for local assignment
     * 
     * @param local assignment expr
     * @param the outer closure
     */
    private Expr handleLocalAssign(X10LocalAssign_c localAssign, Term outerClosure, List<VarInstance<? extends VarDef>> env) {
        if (localAssign.right() instanceof X10Call_c) {
            X10Call_c call = (X10Call_c)localAssign.right();

            if (call.target() instanceof X10Local_c) {
                X10Local_c target = (X10Local_c)call.target();

                // if (target.localInstance().type() instanceof X10ParsedClassType_c) {
                //     X10ParsedClassType_c classType = (X10ParsedClassType_c)target.localInstance().type();
                Type classType = target.localInstance().type();

                if (xts.isRegionArray(classType) 
                        && !xts.isX10RegionDistArray(classType)
                        && call.methodInstance().name().equals(OperatorNames.APPLY)) {
                    // Handle the Array element reading, which is an array inside the async closure         
                    // Check the method arguments, here we only handle .apply(ind)
                    List<Expr> arguments = call.arguments();
                    if (arguments.size() == 1) {
                        if (checkInvariant(arguments.get(0), outerClosure, env)) {
                            // Create the new local variable which is final                  
                            Position pos = localAssign.position();
                            Name srName = Name.makeFresh(localAssign.local().name().toString());
                            Type leftType = localAssign.leftType();
                            LocalDef srDef = xts.localDef(pos, xts.Final(), Types.ref(leftType), srName);
                            // Create the scalar replacement statement                       
                            LocalDecl srLocalDecl = xnf.LocalDecl(pos, xnf.FlagsNode(pos, xts.Final()), xnf.CanonicalTypeNode(pos, Types.ref(leftType)), xnf.Id(pos, srName), call).localDef(srDef);
                            regScalarReplace(outerClosure, srLocalDecl);
                            // Create the scalar assignment statement
                            Expr valLocal = xnf.Local(pos, xnf.Id(pos, srName)).localInstance(srDef.asInstance()).type(leftType);
                            return localAssign.right(valLocal);
                        }
                    }
                }
                // }
            }
        }

        return localAssign;
    }

    /**
     * Handle the case for class field assignment
     *
     * @param class field assignment expr
     * @param the outer closure
     */
    private Expr handleFieldAssign(X10FieldAssign_c fieldAssign, Term outerClosure) {
        if (fieldAssign.right() instanceof X10Call_c) {
            X10Call_c call = (X10Call_c)fieldAssign.right();

            if (call.target() instanceof X10Local_c) {
                X10Local_c target = (X10Local_c)call.target();

                // if (target.localInstance().type() instanceof X10ParsedClassType_c) {
                //     X10ParsedClassType_c classType = (X10ParsedClassType_c)target.localInstance().type();
                Type classType = target.localInstance().type();

                if (xts.isRegionArray(classType) 
                        && !xts.isX10RegionDistArray(classType)
                        && call.methodInstance().name().equals(OperatorNames.APPLY)) {
                    // Handle the Array element reading, which is an array inside the async closure
                    // Check the method arguments, here we only handle .apply(ind)
                    List<Expr> arguments = call.arguments();
                    if (arguments.size() == 1) {
                        if (checkInvariant(arguments.get(0), outerClosure, null)) {
                            // Create the new local variable which is final
                            Position pos = fieldAssign.position();
                            Name srName = Name.makeFresh(fieldAssign.name().toString());
                            Type leftType = fieldAssign.leftType();
                            LocalDef srDef = xts.localDef(pos, xts.Final(), Types.ref(leftType), srName);
                            // Create the scalar replacement statement
                            LocalDecl srLocalDecl = xnf.LocalDecl(pos, xnf.FlagsNode(pos, xts.Final()), xnf.CanonicalTypeNode(pos, Types.ref(leftType)), xnf.Id(pos, srName), call).localDef(srDef);
                            regScalarReplace(outerClosure, srLocalDecl);

                            // Create the scalar assignment statement
                            Expr valLocal = xnf.Local(pos, xnf.Id(pos, srName)).localInstance(srDef.asInstance()).type(leftType);

                            return fieldAssign.right(valLocal);
                        }
                    }
                }
                // }
            }
        }

        return fieldAssign;
    }

    /**   
     * Handle the case for X10 return stmt    
     *   
     * @param X10 return expr     
     * @param the outer closure   
     */
    private Stmt handleX10Return(X10Return_c retStmt, Term outerClosure, List<VarInstance<? extends VarDef>> env) {
        if (retStmt.expr() instanceof X10Call_c)
            return xnf.X10Return(retStmt.position(), handleCall((X10Call_c)retStmt.expr(), outerClosure, env), true);

        return retStmt;
    }

    /**                                                                   
     * Handle the case for X10 If stmt             
     *                                                                          
     * @param X10 If stmt                                              
     * @param the outer closure                                                   
     */
    private Stmt handleIf(If ifStmt, Term outerClosure, List<VarInstance<? extends VarDef>> env) {
        if (ifStmt.cond() instanceof Binary)
            return ifStmt.cond(handleBinary((Binary)ifStmt.cond(), outerClosure, env));

        return ifStmt;
    }

    /**
     * Handle the Binary expression
     *
     * @param Binary expr
     * @param the outer closure
     */
    private Expr handleBinary(Binary cond, Term outerClosure, List<VarInstance<? extends VarDef>> env) {
        // Check left side
        Expr left = cond.left();
        if (left instanceof Binary)
            cond = cond.left(handleBinary((Binary)left, outerClosure, env));
        else if (left instanceof X10Call_c)
            cond = cond.left(handleCall((X10Call_c)left, outerClosure, env));

        // Check right side
        Expr right = cond.right();
        if (right instanceof Binary)
            cond = cond.right(handleBinary((Binary)right, outerClosure, env));
        else if(right instanceof X10Call_c)
            cond = cond.right(handleCall((X10Call_c)right, outerClosure, env));
        else if (right instanceof X10Field_c) {
            cond = cond.right(handleField((X10Field_c)right, outerClosure, env));
        }

        return cond;
    }

    private HashMap<java.lang.Object, ArrayList<Stmt>> scalarReplaceMap = new HashMap<java.lang.Object, ArrayList<Stmt>>();

    /**
     * Check if the input local variable is an invariant inside current block region
     *
     * @param local variable
     * @param the outer closure, i.e. async, ateach, at
     */
    private boolean checkInvariant(Expr indexExpr, Term outerClosure, 
            List<VarInstance<? extends VarDef>> env) {
        if (indexExpr instanceof X10Local_c) {
            // Local index                                                   
            X10Local_c indexLocal = (X10Local_c)indexExpr;
            // Check if the index local is invariant, i.e. the local is defined outside current closure
            Context context = context().pushCode(context().currentCode());
            boolean isFinal = false;

            try {
                LocalInstance li = context.findLocal(indexLocal.name().id());
                // if the local is defined in an outer class, then it must be final, i.e. invariant
                if (!context.isLocal(li.name()))
                    isFinal = true;
            } catch (Exception e) {}

            return isFinal;
        } else if (indexExpr instanceof IntLit_c)
            // This is the case for integer constant
            return true;
        else if (indexExpr instanceof Here_c) 
            // This is the case for here operation
            return true;
        else if (indexExpr instanceof X10Binary_c)
            // This is the binary index
            return checkInvariant(((X10Binary_c)indexExpr).left(), outerClosure, env)
            && checkInvariant(((X10Binary_c)indexExpr).right(), outerClosure, env);

        return false;
    }

    /**
     * Register the statement as the scalar replace statements for current async/ateach/at region
     * 
     * @param current async/ateach/at region
     * @param s scalar replace statement
     */
    private void regScalarReplace(Term outerClosure, Stmt s) {
        System.out.println("reg scalar rep " + s);
        ArrayList<Stmt> stmtList = scalarReplaceMap.get(outerClosure.position());
        if (stmtList == null) {
            stmtList = new ArrayList<Stmt>();
            scalarReplaceMap.put(outerClosure.position(), stmtList);
        }

        stmtList.add(s);
    }

    /**
     * Get the X10Field target, this is for handling the recursive case, e.g. p.q.x
     *
     * @param Receiver target
     */
    private String getReceiverName(Receiver receiver) {
        if (receiver instanceof Special)
            return null;
        else if (receiver instanceof X10Local_c)
            return ((X10Local_c)receiver).name().toString();
        else if (receiver instanceof X10Field_c)
            return ((X10Field_c)receiver).name().toString();
        else if (receiver instanceof X10Call_c)
            return getReceiverName(((X10Call_c)receiver).target());

        return null;
    }

    /**
     * Check if the given receiver is an invariant for the given ouuterClosure
     *
     * @param Receiver receiver
     * @param Term outerClosure
     */
    private boolean hasInvariantReceiver(Receiver receiver, Term outerClosure, 
            List<VarInstance<? extends VarDef>> env) {
        if (receiver instanceof Special)
            // This is for the class field whose receiver is this
            return true;
        else if (receiver instanceof X10Local_c)
            // The receiver is Local, check it directly
            return checkInvariant((X10Local_c)receiver, outerClosure, env);
        else if (receiver instanceof X10Field_c)
            // The receiver is class field, check it recursively
            return hasInvariantReceiver(((X10Field_c)receiver).target(), outerClosure, env);
        else if (receiver instanceof X10Call_c) {
            // Check if this is for array case
            X10Call_c call = (X10Call_c)receiver;
            if (hasInvariantReceiver(call.target(), outerClosure, env)) {
                // X10Local_c target = (X10Local_c)call.target();
                // Type classType = target.localInstance().type();
                Type classType = call.target().type();

                if (xts.isRegionArray(classType)
                        && !xts.isX10RegionDistArray(classType)
                        && call.methodInstance().name().equals(OperatorNames.APPLY)) {
                    // Check the method arguments
                    List<Expr> arguments = call.arguments();
                    for (Expr arg : arguments) {
                        if (!checkInvariant(arg, outerClosure, env)) 
                            return false;
                    }
                    // If receiver and all arguments are invariant, then return true
                    return true;
                } else
                    // We don't handle the normal method call and presume there's side-effect
                    return false;
            } else
                return false;
        } 

        return false;
    }

    /**
     * Perform localization on at stmt
     * e.g. at(p) {...} ==> if (p == here) {...} else at(p) {...}
     *
     * @param AtStmt at
     */
    private Stmt localizeAt(AtStmt at) throws SemanticException {
        return at;

        // Check if AtStmt is legal for localization, i.e. the env variables should be final
        /*List<VarInstance<? extends VarDef>> env = at.atDef().capturedEnvironment();
        for (VarInstance vi : env) {
            if (!vi.flags().isFinal())
                return at;
        }

    Position pos = at.position();
    Expr h = synth.makeStaticCall(pos, xts.Runtime(), Name.make("home"), xts.Place(), xContext());

    Expr cond = xnf.Binary(pos, at.place(), Binary_c.EQ, h).type(xts.Boolean());
    Stmt localizedAt = at.body();

    Stmt ifStmt = xnf.If(pos, cond, localizedAt, at);

    return ifStmt;*/
    }
}
