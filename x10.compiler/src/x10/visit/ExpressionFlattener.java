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

package x10.visit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ast.ArrayAccess;
import polyglot.ast.Assert;
import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.BooleanLit;
import polyglot.ast.Branch;
import polyglot.ast.Call;
import polyglot.ast.Case;
import polyglot.ast.Cast;
import polyglot.ast.Catch;
import polyglot.ast.Conditional;
import polyglot.ast.ConstructorCall;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Do;
import polyglot.ast.Do_c;
import polyglot.ast.Empty;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.FieldDecl;
import polyglot.ast.For;
import polyglot.ast.ForInit;
import polyglot.ast.ForUpdate;
import polyglot.ast.Id;
import polyglot.ast.If;
import polyglot.ast.Instanceof;
import polyglot.ast.Labeled;
import polyglot.ast.Lit;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalDecl;
import polyglot.ast.Local_c;
import polyglot.ast.Loop;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.ProcedureCall;
import polyglot.ast.Return;
import polyglot.ast.SourceFile;
import polyglot.ast.Special;
import polyglot.ast.Special_c;
import polyglot.ast.Stmt;
import polyglot.ast.Switch;
import polyglot.ast.SwitchBlock;
import polyglot.ast.Throw;
import polyglot.ast.Try;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.ast.While;
import polyglot.ast.While_c;
import polyglot.frontend.Job;
import polyglot.frontend.Source;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.Name;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ExtensionInfo;
import x10.ast.AssignPropertyCall;
import x10.ast.Async;
import x10.ast.AtEach;
import x10.ast.AtExpr;
import x10.ast.AtStmt;
import x10.ast.Atomic;
import x10.ast.Closure;
import x10.ast.ClosureCall;
import x10.ast.Finish;
import x10.ast.ForLoop;
import x10.ast.HasZeroTest;
import x10.ast.Here;
import x10.ast.Here_c;
import x10.ast.Next;
import x10.ast.Offer;
import x10.ast.ParExpr;
import x10.ast.RemoteActivityInvocation;
import x10.ast.SettableAssign;
import x10.ast.StmtExpr;
import x10.ast.StmtSeq;
import x10.ast.SubtypeTest;
import x10.ast.Tuple;
import x10.ast.When;
import x10.ast.X10ClassDecl;
import x10.errors.Warnings;
import x10.extension.X10Ext;
import x10.types.X10FieldInstance;
import x10.util.AltSynthesizer;

/**
 * @author Bowen Alpern
 *
 */
public final class ExpressionFlattener extends ContextVisitor {

    private static final boolean DEBUG = false;

    private final TypeSystem xts;
    private AltSynthesizer syn; // move functionality to Synthesizer
    private final SideEffectDetector sed;
    
    /*
     * When flattening For statements we introduce a new label for the body of the
     * for loop. Continue statements within the For statement must be rewritten as
     * breaks to the new label. The Continues are processed before the For statements
     * so we must create the new labels on enter in case it is needed. Other loops
     * are not affected.
     */
    private class labelInfoStruct {
        Id oldLabel;
        Id newLabel;
        boolean oldLabelUsed;
        boolean newLabelUsed;
        boolean loop;
    }
    
    private ArrayList<labelInfoStruct> labelInfo = new ArrayList<labelInfoStruct>(20);

    /**
     * @param job the job to run
     * @param ts the type system object
     * @param nf the factory to produce AST nodes
     */
    public ExpressionFlattener(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = ts;
        syn = new AltSynthesizer(ts, nf);
        sed = new SideEffectDetector(job, ts, nf);
    }

    @Override
    public NodeVisitor begin() {
        sed.begin();
        return super.begin();
    }

    /**
     * Don't visit nodes that cannot be flattened.
     * 
     * @param n the node to be visited (or not)
     * @return n if the node is NOT to be visited, otherwise null
     */
    @Override
    public Node override(Node n) {
        if (n instanceof X10ClassDecl) {
            if (DEBUG) System.out.println("DEBUG: flattening: " +((X10ClassDecl) n).classDef()+ " (@" +((X10ClassDecl) n).position()+ ")");
            return null;
        }
        if (cannotFlatten(n, job)) return n;
        return null;
    }

    /**
     * Is this Node unflattenable?
     * 
     * Nodes that cannot be flattened:
     *     Assert requires the Java back-end handle StmtExpr's to achieve the right semantics.
     *     When requires its tests to be evaluated atomically.
     *     Constructors and class initializers cannot be flattened because of Java's initialization rules (X10 proto would fix
     *         this but the Java back-end must be able to handle the new code).
     *     Case Expr's are constant expressions that will eventually get evaluated at compile time.
     *     
     * @param n an AST node that might be flattened
     * @return true if the node cannot be flattened, false otherwise
     */
    public static boolean cannotFlatten(Node n, Job job) {
        Position pos = n.position(); // for DEBUGGING
        boolean isManaged = ((x10.ExtensionInfo) job.extensionInfo()).isManagedX10();

        if (n instanceof ConstructorDecl && isManaged) { // can't flatten constructors unless local assignments can precede super() and this() in Java
            ClassType type = ((ConstructorDecl) n).constructorDef().container().get().toClass();
            if (ConstructorSplitterVisitor.isUnsplittable(type))
                return true;
        }
        if (n instanceof FieldDecl) { // can't flatten class initializes until assignments can precede field declarations
            return true;
        }
        if (n instanceof Assert) { // can't flatten assert's until Java can handle StmtExpr's
            return true;
        }
        if (n instanceof When) { // can't flatten when's because tests need to be evaluated atomically
            return true;
        }
        if (n instanceof Case) { // case Expr's will become constants, don't screw with them.
            return true;
        }
        return false;
    }
    
    @Override
    protected NodeVisitor enterCall(Node parent, Node child) {
        if (child instanceof Loop) {
            labelInfoStruct lblInfo = new labelInfoStruct();
            if (child instanceof For) {
                lblInfo.newLabel = syn.createLabel(child.position());
            }
            if (parent instanceof Labeled) {
                lblInfo.oldLabel = ((Labeled) parent).labelNode();
            }
            lblInfo.loop = true;
            labelInfo.add(lblInfo);
        } else if (child instanceof Block && parent instanceof Labeled) {
            labelInfoStruct lblInfo = new labelInfoStruct();
            lblInfo.oldLabel = ((Labeled) parent).labelNode();
            labelInfo.add(lblInfo);
        }
        return this;
        
    }

    /* (non-Javadoc)
     * @see polyglot.visit.ErrorHandlingVisitor#leaveCall(polyglot.ast.Node, polyglot.ast.Node, polyglot.visit.NodeVisitor)
     * 
     * Flatten statements (Stmt) and expressions (Expr).
     */
    @Override
    public Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) {
        if (n instanceof Labeled) 
            return flattenLabeled((Labeled) n);
        if (n instanceof Loop) {
           Node returnNode = flattenLoop(n, parent instanceof Labeled);
           labelInfo.remove(labelInfo.size()-1);
           return returnNode;
        }
        if (n instanceof Expr) return flattenExpr((Expr) n);
        if (n instanceof Block) {
            // After StmtExpr Blocks have been flattened
            Node returnNode = flattenBlock((Block) n, parent instanceof Labeled);
            if (parent instanceof Labeled) {
                labelInfo.remove(labelInfo.size() - 1);
            }
            return returnNode;
        }
        if (n instanceof Stmt) return flattenStmt((Stmt) n);
        return n;
    }

    /**
     * @param stmt
     * @return
     */
    private Stmt flattenLabeled(Labeled stmt) {
        return stmt.statement();
    }

    /**
     * @param n
     * @param list
     * @return
     */
    private Node flattenLoop(Node n, boolean labeled) {
        if (n instanceof For) return flattenFor((For) n, labeled);
        if (n instanceof ForLoop) return flattenForLoop((ForLoop) n, labeled);
        if (n instanceof While) return flattenWhile((While_c) n, labeled);  // TODO: fix polyglot to allow getters in the interface
        if (n instanceof Do) return flattenDo((Do_c) n, labeled); // TODO: fix polyglot to allow getters in the interface
        if (n instanceof AtEach)    return flattenAtEach((AtEach) n, labeled);
        assert false;
        return null;
    }

    /**
     * Flatten Expr's.  (Flat expressions do not contain sub-expressions that arn't literal constants or final variables.)
     *  The result may be a StmtExpr.  (Statement flattening will eliminating these.)
     * 
     * @param expr the Expr to be flattened
     * @return a flat Expr equivalent to expr (often a StmtExpr)
     */
    private Expr flattenExpr(Expr expr) {
        if (expr instanceof Lit)                 return flattenPrimary(expr);
        else if (expr instanceof Local_c)        return flattenPrimary(expr);
        else if (expr instanceof Special_c)      return flattenPrimary(expr);
        else if (expr instanceof Here_c)         return flattenPrimary(expr);
        else if (expr instanceof ParExpr)        return flattenParExpr((ParExpr) expr);
        else if (expr instanceof Field)          return flattenField((Field) expr);
        else if (expr instanceof ArrayAccess)    return flattenArrayAccess((ArrayAccess) expr);
        else if (expr instanceof SettableAssign) return flattenSettableAssign((SettableAssign) expr);
        else if (expr instanceof FieldAssign)    return flattenFieldAssign((FieldAssign) expr);
        else if (expr instanceof LocalAssign)    return flattenLocalAssign((LocalAssign) expr);
        else if (expr instanceof Call)           return flattenMethodCall((Call) expr);
        else if (expr instanceof New)            return flattenNew((New) expr);
        else if (expr instanceof Tuple)          return flattenTuple((Tuple) expr);
        else if (expr instanceof Binary)         return flattenBinary((Binary) expr);
        else if (expr instanceof Unary)          return flattenUnary((Unary) expr);
        else if (expr instanceof Cast)           return flattenCast((Cast) expr);
        else if (expr instanceof Instanceof)     return flattenInstanceof((Instanceof) expr);
        else if (expr instanceof AtExpr)         return flattenAtExpr((AtExpr) expr);
        else if (expr instanceof SubtypeTest)    return flattenSubtypeTest((SubtypeTest) expr);
        else if (expr instanceof HasZeroTest)    return expr;
        else if (expr instanceof ClosureCall)    return flattenClosureCall((ClosureCall) expr);
        else if (expr instanceof Conditional)    return flattenConditional((Conditional) expr);
        else if (expr instanceof StmtExpr)       return flattenStmtExpr((StmtExpr) expr);
        else if (expr instanceof Closure)        return expr;
        else {
            if (DEBUG) System.err.println("INFO: ExpressionFlattener.flattenExpr: default class: " +expr.getClass()+ " (" +expr+ ") at" +expr.position());
            return expr;
        }
    }

    /**
     * Flatten a primary expression.
     *  (no-op: Primary expressions are already flat!)
     *  
     * @param expr the primary expression to be flattened
     * @return (the already flat) expr
     */
    private Expr flattenPrimary(Expr expr) {
        return expr;
    }

    /**
     * Flatten a statement expression (which almost certainly will already be flat).
     * <pre>
     * ({S; ({s1; t1}) })  ->  ({S; s1; t1}) 
     * </pre>
     * 
     * @param expr a statement expression to be flattened
     * @return a flat expression equivalent to expr
     */
    private Expr flattenStmtExpr(StmtExpr expr) {
        if (expr.result() instanceof StmtExpr) { // the inliner produces StmtExpr's
            expr = expr.append(getStatements(expr.result()));
            expr = expr.result(getResult(expr.result()));
        }
        return expr;
    }

    /**
     * Flatten a offer statement.
     * <pre>
     * offer ({s1; e1});  ->  s1; val t1 = e1; offer t1;
     * </pre>
     * 
     * @param stmt the offer statement to be flattened.
     * @return a flat statement with the same semantics as stmt
     */
    private StmtSeq flattenOffer(Offer stmt){
        List<Stmt> stmts = new ArrayList<Stmt>();
        Expr primary = getPrimaryAndStatements(stmt.expr(), stmts);
        stmts.add(stmt.expr(primary));
        return syn.createStmtSeq(stmt.position(), stmts);
    }
    
    /**
     * Flatten a parenthesized expression.
     * (This should never happen, but it does.)
     * <pre>
     * (({s1; e1}))  ->  ({s1; val t1=e1; t1})
     * (e)           ->  e
     * </pre>
     * 
     * @param expr the parenthesized expression to be flattened
     * @return a flat expression equivalent to expr (without the parens)
     */
    private Expr flattenParExpr(ParExpr expr) {
        List<Stmt> stmts = new ArrayList<Stmt>();
        Expr primary = expr;
        while (primary instanceof ParExpr) {
            primary = getPrimaryAndStatements(((ParExpr) primary).expr(), stmts);
        }
        return toFlatExpr(expr.position(), stmts, primary);
    }

    /**
     * Flatten an array access.
     * (Note: the StmtExpr must end in an array access in case it gets bumped: a(i)++.)
     * {s1; e1}({s2; e2})  ->  {s1; val t1=e1; s2; val t2=e2; t1(t2)}
     * 
     * @param expr
     * @return
     */
    private Expr flattenArrayAccess(ArrayAccess expr) {
        List<Stmt> stmts = new ArrayList<Stmt>();
        Expr array = getPrimaryAndStatements(expr.array(), stmts);
        Expr index = getPrimaryAndStatements(expr.index(), stmts);
        return toFlatExpr(expr.position(), stmts, expr.array(array).index(index));
    }

    /**
     * Flatten an object (or struct) allocation.
     * <pre>
     *            new T(({s1; e1}), ..., ({sk; ek}))  ->  ({s1; val t1 = e1; ... sk; val tk = ek; val t =    new T(t1, ..., tk); t})
     * ({s1; e1}).new T(({s2; e2}), ..., ({sk; ek}))  ->  ({s1; val t1 = e1; ... sk; val tk = ek; val t = t1.new T(t2, ..., tk); t})
     * </pre>
     * 
     * @param expr the New expression to be flattened
     * @return a flat expression equivalent to expr
     */
    private Expr flattenNew(New expr) {
        List<Stmt> stmts = new ArrayList<Stmt>();
        List<Expr> args  = new ArrayList<Expr>();
        for (Expr e : expr.arguments()) {
            Expr primary = getPrimaryAndStatements(e, stmts);
            args.add(primary);
        }
        return toFlatExpr(expr.position(), stmts, (Expr) expr.arguments(args));
    }

    /**
     * Flatten a field access.
     * <pre>
     *          T.f  ->  T.f 
     * ({s1; e1}).f  ->  ({s1; val t1 = e1; t1.f})
     * </pre>
     * 
     * @param expr the field access to be flattened
     * @return a flat expression equivalent to expr
     */
    private Expr flattenField(Field expr) {
        List<Stmt> stmts = new ArrayList<Stmt>();
        if (expr.target() instanceof TypeNode) {
            return expr;
        }
        Expr target = getPrimaryAndStatements((Expr) expr.target(), stmts);
        return toFlatExpr(expr.position(), stmts, expr.target(target));
    }


    /**
     * Flatten a unary expression.
     * If the unary operator (++ or --) updates its argument:
     * <pre>
     * op ({s1; e1})  ->  ({s1; op e1})
     * ({s1; e1}) op  ->  ({s1; e1 op})
     * </pre>,
     * otherwise
     * <pre>
     * ! ({s1; true})   ->  ({s1; false})
     * ! ({s1; false})  ->  ({s1; true})
     * op ({s1; e1})    ->  ({s1; val t1 = e1; op t1})
     * </pre>
     * 
     * @param expr the unary expression to flatten
     * @return a flat expression equivalent to expr
     */
    private Expr flattenUnary(Unary expr) {
        List<Stmt> stmts = new ArrayList<Stmt>();
        Expr result;
        if (isUpdateOp(expr.operator())) {
            stmts.addAll(getStatements(expr.expr()));
            result = getResult(expr.expr());
        } else {
            result = getPrimaryAndStatements(expr.expr(), stmts);
            if (result instanceof BooleanLit) {
                assert (Unary.NOT == expr.operator());
                if (((BooleanLit) result).value()) {
                    result = syn.createFalse(expr.position());
                } else {
                    result = syn.createTrue(expr.position());
                }
                return toFlatExpr(expr.position(), stmts, result);
            }
        }
        return toFlatExpr(expr.position(), stmts, expr.expr(result));
    }


    /**
     * Does the given unary operator update it arguement?  (Is it "++" or "--"?)
     * 
     * @param op the unary operator in question
     * @return true, if op updates its argument; false, otherwise
     */
    private boolean isUpdateOp(polyglot.ast.Unary.Operator op) {
        return op.equals(Unary.PRE_DEC) || op.equals(Unary.PRE_INC) || op.equals(Unary.POST_DEC) || op.equals(Unary.POST_INC);
    }

    /**
     * Flatten a cast expression.
     * <pre>
     * ({s1; e1}) as T  ->  ({s1; val t1 = e1; t1 as T}) 
     * </pre>
     * 
     * @param expr the cast to be flattened
     * @return a flat expression equivalent to expr
     */
    private Expr flattenCast(Cast expr) {
        List<Stmt> stmts = new ArrayList<Stmt>();
        Expr primary = getPrimaryAndStatements(expr.expr(), stmts);
        return toFlatExpr(expr.position(), stmts, expr.expr(primary));
    }


    /**
     * Flatten an instanceof expression.
     * <pre>
     * ({s1; e1}) instanceof T  ->  ({s1; val t1 = e1; t1 instanceof T}) 
     * </pre>
     * 
     * @param expr the instanceof expression to be flattened
     * @return a flat expression equivalent to expr
     */
    private Expr flattenInstanceof(Instanceof expr) {
        List<Stmt> stmts = new ArrayList<Stmt>();
        Expr primary = getPrimaryAndStatements(expr.expr(), stmts);
        return toFlatExpr(expr.position(), stmts, expr.expr(primary));
    }

    /**
     * Flatten an At expression.  
     * (Only the Place needs to be flattened; the body is already flat.)
     * <pre>
     * at (({s1; e1})) {S; ({s2; e2})}  ->  ({s1; val t1 = e1; at (t1) {S; s2; e2} }) 
     * </pre>
     * 
     * @param expr the at expression to be flattened
     * @return a flat expression equivalent to expr
     */
    private Expr flattenAtExpr(AtExpr expr) {
        List<Stmt> stmts = new ArrayList<Stmt>();
        return toFlatExpr(expr.position(), stmts, (Expr) flattenRemoteActivityInvocation(expr, stmts));
    }

    /**
     * Flatten a sub-type test.  This is a no-op.
     * <pre>
     * T1 <: T2  ->   T1 <: T2 
     * </pre>
     * 
     * @param expr the sub-type text to be flattened
     * @return a flat expression equivalent to expr
     * TODO: need source code to test this
     */
    private Expr flattenSubtypeTest(SubtypeTest expr) {
        return expr;
    }

    /**
     * Flatten a method call expression.
     * <pre>
     * ({s1; e1}).m(({s2; e2}), ..., ({sk; ek}))  ->  ({s1; val t1 = e1; ... sk; val tk = ek;      t1.m(t2, ..., tk)  })
     * ({s1; e1}).m(({s2; e2}), ..., ({sk; ek}))  ->  ({s1; val t1 = e1; ... sk; val tk = ek; Eval(t1.m(t2, ..., tk)) }) // (void call)
     *          T.m(({s1; e1}), ..., ({sk; ek}))  ->  ({s1; val t1 = e1; ... sk; val tk = ek;       T.m(t1, ..., tk)  })
     *          T.m(({s1; e1}), ..., ({sk; ek}))  ->  ({s1; val t1 = e1; ... sk; val tk = ek; Eval( T.m(t1, ..., tk)) }) // (void call)
     *</pre>
     * 
     * @param expr the method call to flatten
     * @return a flat expression equivalent to expr
     */
    private Expr flattenMethodCall(Call expr) {
        List<Stmt> stmts = new ArrayList<Stmt>();
        if (expr.target() instanceof Expr) {
            Expr target = getPrimaryAndStatements((Expr) expr.target(), stmts);
            expr = expr.target(target); 
        }
        List<Expr> args = new ArrayList<Expr>();
        for (Expr e : expr.arguments()) {
            Expr primary = getPrimaryAndStatements(e, stmts);
            args.add(primary);            
        };
        return toFlatExpr(expr.position(), stmts, (Expr) expr.arguments(args));
    }


    /**
     * Flatten a closure call expression.
     * <pre>
     * ({s1; e1})(({s2; e2}), ..., ({sk; ek}))  ->  ({s1; val t1 = e1; ... sk; val tk = ek; t1(t2, ..., tk) })
     * ({s1; e1})(({s2; e2}), ..., ({sk; ek}))  ->  ({s1; val t1 = e1; ... sk; val tk = ek; Eval(t1(t2, ..., tk)) }) // (void call)
     *</pre>
     * 
     * @param expr the closure call to flatten
     * @return a flat expression equivalent to expr
     */
    private Expr flattenClosureCall(ClosureCall expr) {
        List<Stmt> stmts = new ArrayList<Stmt>();
        Expr primary = getPrimaryAndStatements(expr.target(), stmts);
        expr = expr.target(primary); 
        List<Expr> args = new ArrayList<Expr>();
        for (Expr e : expr.arguments()) {
            Expr primary2 = getPrimaryAndStatements(e, stmts);
            args.add(primary2);            
        }
        return toFlatExpr(expr.position(), stmts, (Expr) expr.arguments(args));
    }


    /**
     * Flatten a tuple expression.
     * <pre>
     * [({s1; e1}), ..., ({sk; ek})]  ->  {s1; val t1 = e1; ... sk; val tk = ek; [t1, ..., tk] }) 
     * </pre>
     * 
     * @param expr the tuple to flatten
     * @return a flat expression equivalent to expr
     */
    private Expr flattenTuple(Tuple expr) {
        List<Stmt> stmts = new ArrayList<Stmt>();
        List<Expr> args  = new ArrayList<Expr>();
        for (Expr e : expr.arguments()) {
            Expr primary = getPrimaryAndStatements(e, stmts);
            args.add(primary);            
        }
        return toFlatExpr(expr.position(), stmts, expr.arguments(args));
    }

    /**
     * Flatten a binary expression.
     * (Short-circuit AND (&&) and OR (||) are special.)
     * <pre>
     * 
     * ({s1; false} && {s2; e2})  ->  ({s1; false})
     * ({s1; true}  && {s2; e2})  ->  ({s1; s2; e2})
     * ({s1; e1}    && {s2; e2})  ->  ({s1; var t  = e1; if ( t){s2; t = e2;}; t})
     * ({s1; false} || {s2; e2})  ->  ({s1; s2; e2})
     * ({s1; true}  || {s2; e2})  ->  ({s1; true})
     * ({s1; e1}    || {s2; e2})  ->  ({s1; var t  = e1; if (!t){s2; t =be2;}; t})
     * ({s1; e1}    op {s2; e2})  ->  ({s1; val t1 = e1; s2; val t2 = e2; t1 op t2}) 
     * </pre>
     * 
     * @param expr the binary expression to flatten
     * @return a flat expression equivalent to expr
     */
    private Expr flattenBinary(Binary expr) {
        Position pos = expr.position();
        List<Stmt> stmts = new ArrayList<Stmt>();
        if (expr.operator().equals(Binary.COND_AND)) {
            stmts.addAll(getStatements(expr.left()));
            Expr left = getResult(expr.left());
            if (left instanceof BooleanLit) {
                if (false == ((BooleanLit) left).value()) 
                    return toFlatExpr(pos, stmts, left);
                stmts.addAll(getStatements(expr.right()));
                return toFlatExpr(pos, stmts, getResult(expr.right()));
            }
            LocalDecl tmpLDecl = syn.createLocalDecl( pos, 
                                                      Flags.NONE, 
                                                      syn.createTemporaryName(), 
                                                      xts.Boolean(), 
                                                      left );
            stmts.add(tmpLDecl);
            List<Stmt> andStmts = new ArrayList<Stmt>();
            andStmts.addAll(getStatements(expr.right()));
            Expr right = getResult(expr.right());
            andStmts.add(syn.createAssignment( pos,
                                               syn.createLocal(pos, tmpLDecl), 
                                               Assign.ASSIGN, 
                                               right,
                                               this ));
            stmts.add(syn.createIf( pos, 
                                    syn.createLocal(pos, tmpLDecl), 
                                    syn.createBlock(pos, andStmts),
                                    null ));
            return toFlatExpr(pos, stmts, syn.createLocal(pos, tmpLDecl));
        } else if (expr.operator().equals(Binary.COND_OR)) {
            stmts.addAll(getStatements(expr.left()));
            Expr left = getResult(expr.left());
            if (left instanceof BooleanLit) {
                if (true == ((BooleanLit) left).value()) 
                    return toFlatExpr(pos, stmts, left);
                stmts.addAll(getStatements(expr.right()));
                return toFlatExpr(pos, stmts, getResult(expr.right()));
            }
            LocalDecl tmpLDecl = syn.createLocalDecl( pos, 
                                                      Flags.NONE, 
                                                      syn.createTemporaryName(), 
                                                      xts.Boolean(), 
                                                      left );
            stmts.add(tmpLDecl);
            List<Stmt> orStmts = new ArrayList<Stmt>();
            orStmts.addAll(getStatements(expr.right()));
            Expr right = getResult(expr.right());
            orStmts.add(syn.createAssignment( pos,
                                              syn.createLocal(pos, tmpLDecl), 
                                              Assign.ASSIGN, 
                                              right,
                                              this ));
            stmts.add(syn.createIf( pos,  
                                    syn.createNot(syn.createLocal(pos, tmpLDecl), this),
                                    syn.createBlock(pos, orStmts),
                                    null ));
            return toFlatExpr(pos, stmts, syn.createLocal(pos, tmpLDecl));
        }
        Expr left = getPrimaryAndStatements(expr.left(), stmts);
        Expr right = getPrimaryAndStatements(expr.right(), stmts);
        return toFlatExpr(pos, stmts, expr.left(left).right(right));
    }

    /**
     * Flatten an assignment to a field.
     * <pre>
     *          T.f op= ({s1; e1})  ->  ({s1; val t1 = e1;                   T.f op= t1})
     * ({s1; e1}).f op= ({s2; e2})  ->  ({s1; val t1 = e1; s2; val t2 = e2; t1.f op= t2})
     * </pre>
     * 
     * @param expr the field assignment to be flattened
     * @return a flat expression equivalent to expr
     */
    private Expr flattenFieldAssign(FieldAssign expr) {
        List<Stmt> stmts = new ArrayList<Stmt>();
        if (expr.target() instanceof Expr) {
            Expr target = getPrimaryAndStatements((Expr) expr.target(), stmts);
            expr = expr.target(target);
        }
        X10FieldInstance fi = (X10FieldInstance) expr.fieldInstance();
        if (!fi.annotationsMatching(typeSystem().Embed()).isEmpty()) {
            return expr;
        }
        Expr right = getPrimaryAndStatements(expr.right(), stmts);
        return toFlatExpr(expr.position(), stmts, expr.right(right));
    }


    /**
     * Flatten an assignment to a local variable.
     * <pre>
     * x op= ({s1; e1})  ->  ({s1; val t1 = e1; x op= t1})
     * </pre>
     * 
     * @param expr the local assignment to flatten
     * @return a flat expression equivalent to expr
     */
    private Expr flattenLocalAssign(LocalAssign expr) {
        List<Stmt> stmts = new ArrayList<Stmt>();
        Expr right = getPrimaryAndStatements(expr.right(), stmts);
        return toFlatExpr(expr.position(), stmts, expr.right(right));
    }


    /**
     * Flatten an array element assignment.
     * <pre>
     * ({s1; e1})(({s2; e2}), ... ({se; tk})) op= ({sk+1; ek+1})  ->  
     *     ({s1; val t1 = e1; ... sk+1; val tk+1 = ek+1; t1(t2, ..., tk) op= tk+1 }) 
     * </pre>
     * 
     * @param expr the array element assignment to flatten
     * @return a flat expression equivalent to expr
     */
    private Expr flattenSettableAssign(SettableAssign expr) {
        List<Stmt> stmts = new ArrayList<Stmt>();
        Expr array = getPrimaryAndStatements(expr.array(), stmts);
        List<Expr> exprs = new ArrayList<Expr>();
        for (Expr e : expr.index()) {
            Expr index = getPrimaryAndStatements(e, stmts);
            exprs.add(index);
        }
        Expr right = getPrimaryAndStatements(expr.right(), stmts);
        return toFlatExpr(expr.position(), stmts, expr.array(array).index(exprs).right(right));
    }

    /**
     * Flatten a conditional expression.
     * <pre>
     * ({s1; e1}) ? ({s2; e2}) : ({s3; e3}  ->  ({s1; val t1 = e1; var t; if (t1){s2; t = e2} else {s3; t = e3}; t}) 
     * </pre>
     * 
     * @param expr the conditional expression to flatten
     * @return a flat expression equivalent to expr
     */
    private Expr flattenConditional(Conditional expr) {
        assert (null != expr.alternative());
        List<Stmt> stmts = new ArrayList<Stmt>();
        Expr primary = getPrimaryAndStatements(expr.cond(), stmts);
        LocalDecl tmpLDecl = syn.createLocalDecl(expr.position(), Flags.NONE, syn.createTemporaryName(), expr.type());
        stmts.add(tmpLDecl);
        List<Stmt> thenStmts = new ArrayList<Stmt>();
        thenStmts.addAll(getStatements(expr.consequent()));
        thenStmts.add(syn.createAssignment( expr.position(), 
                                            syn.createLocal(expr.position(), tmpLDecl), 
                                            Assign.ASSIGN, 
                                            getResult(expr.consequent()),
                                            this ));
        List<Stmt> elseStmts = new ArrayList<Stmt>();
        elseStmts.addAll(getStatements(expr.alternative()));
        elseStmts.add(syn.createAssignment( expr.position(), 
                                            syn.createLocal(expr.position(), tmpLDecl), 
                                            Assign.ASSIGN, 
                                            getResult(expr.alternative()),
                                            this ));
        stmts.add(syn.createIf( expr.position(), 
                                primary, 
                                syn.createBlock(expr.consequent().position(), thenStmts), 
                                syn.createBlock(expr.alternative().position(), elseStmts) ));
        return toFlatExpr(expr.position(), stmts, syn.createLocal(expr.position(), tmpLDecl));
    }

    /**
     * Produce a statement in which all sub-expressions have been flattened.  
     * Transform any embedded statement expressions into statements and expressions.
     * (All children of this statement have already been flattened.)
     * 
     * @param stmt the statement to be flattened
     * @return a flat statement (possibly a Block) semantically equivalent to stmt
     */
    private Stmt flattenStmt(Stmt stmt) {
        if      (stmt instanceof Eval)      return flattenEval((Eval) stmt);
        else if (stmt instanceof LocalDecl) return flattenLocalDecl((LocalDecl) stmt);
        else if (stmt instanceof If)        return flattenIf((If) stmt);
        else if (stmt instanceof Return)    return flattenReturn((Return) stmt);
        else if (stmt instanceof Throw)     return flattenThrow((Throw) stmt);
        else if (stmt instanceof Offer)     return flattenOffer((Offer) stmt);
        else if (stmt instanceof Switch)    return flattenSwitch((Switch) stmt);
        else if (stmt instanceof Assert)    return flattenAssert((Assert) stmt);
        else if (stmt instanceof Async)     return flattenAsync((Async) stmt);
        else if (stmt instanceof AtStmt)    return flattenAtStmt((AtStmt) stmt);
        else if (stmt instanceof When)      return flattenWhen((When) stmt);
        else if (stmt instanceof AssignPropertyCall) return flattenAssignPropertyCall((AssignPropertyCall) stmt);
        else if (stmt instanceof ConstructorCall)    return flattenConstructorCall((ConstructorCall) stmt);
        else if (stmt instanceof Branch)             return inspectBranch((Branch) stmt);
        else if (stmt instanceof Finish)             return syn.createStmtSeq(stmt);
        else if (stmt instanceof Next)               return syn.createStmtSeq(stmt);
        else if (stmt instanceof Try) {
            if (((Try) stmt).tryBlock() instanceof StmtSeq) {
                List<Stmt> stmts = ((StmtSeq)((Try) stmt).tryBlock()).statements();
                assert (1 == stmts.size());
                return ((Try) stmt).tryBlock((Block) stmts.get(0));
            }
            return stmt;
        }
        else {
            if (DEBUG && !(stmt instanceof Try || stmt instanceof Catch || stmt instanceof Atomic || stmt instanceof Empty))
                System.err.println("INFO: ExpressionFlattener.flattenStmt: default class: " +stmt.getClass()+ " (" +stmt+ ") at " +stmt.position() );
            return stmt;
        }
    }

    /**
     * Flatten a block.
     * If this is a labeled block, we stick the labels on it because flattenLabeled
     * is about to rip them off our parent.
     * (Otherwise, the only thing we flatten is a block nested in a block,
     * the only block with a value, StmtExpr, has already been handled.)
     * <pre>
     * { S; ({s1; e1}) }  ->  { S; s1; e1 } 
     * </pre>
     * 
     * @param stmt the block to be flattened
     * @return a block with all of its constituent statements flattened.
     */
    private Stmt flattenBlock(Block stmt, boolean labeled) {
        assert (!(stmt instanceof StmtExpr));

        List<Stmt> bodyStmts = stmt.statements();
        if ((bodyStmts.size() == 1) && bodyStmts.get(0) instanceof Block) {
            Block inner = (Block) bodyStmts.get(0);
            boolean outerStmtSeq = stmt instanceof StmtSeq;
            boolean innerStmtSeq = inner instanceof StmtSeq;
            if ((!outerStmtSeq || innerStmtSeq) && !(stmt instanceof SwitchBlock)
                    && ((X10Ext) inner.ext()).annotations().isEmpty()) {
                stmt = stmt.statements(inner.statements());
            }
        }

        // we're about to strip off our parents label in flattenLabeled.
        // So, if this block is labeled we'd better cram it on here.
        return label(stmt, labeled);
    }

    /**
     * Flatten an assertion.
     * The semantics of a flat assertion require the backend to be able to handle a StmtExpr.  The java back-end cannot.
     * For the time being, assertions are not flattened.
     * <pre>
     * assert ({s1; e1});  ->  assert ({s1; e1}); // no-op 
     * </pre>
     * 
     * @param stmt the assertion to be flattened
     * @return a flat stmt with the same semantics as stmt
     * TODO: handle StmtExpr's in the Java back end.
     */
    private StmtSeq flattenAssert(Assert stmt) {
        assert false;
        return syn.createStmtSeq(stmt);
    }

    /**
     * Flatten an when statement.
     * The semantics of a flat assertion require the backend to be able to handle a StmtExpr.  The java back-end cannot.
     * For the time being when statements are not flattened.
     * <pre>
     * when (({s1; e1})) { S1 } ... or (({sk; ek})) { Sk };  ->  when (({s1; e1})) { S1 } ... or (({sk; ek})) { Sk }; // no-op 
     * </pre>
     * 
     * @param stmt the when statement to flatten
     * @return a flat statement with the same semantics as stmt
     * TODO: handle StmtExpr's in the Java back end.
     */
    private StmtSeq flattenWhen(When stmt) {
        assert false;
        return syn.createStmtSeq(stmt);
    }

    /**
     * Flatten an AtEach statement.
     * <pre>
     * ateach (x in ({s1; e1})) S  ->  s1; val t1 = e1; ateach (x in t1) S; 
     * </pre>
     * 
     * @param stmt the AtEach statement to flatten
     * @return a flat statement with the same semantics as stmt
     */
    private Block flattenAtEach(AtEach stmt, boolean labeled) {
        List<Stmt> stmts = new ArrayList<Stmt>();
        Expr domain = getPrimaryAndStatements(stmt.domain(), stmts);
        stmt = (AtEach) stmt.domain(domain);
        stmts.add(label(stmt, labeled));
        return syn.createBlock(stmt.position(), stmts);
    }

    /**
     * Flatten an At statement.
     * <pre>
     * at (x in ({s1; e1})) S  ->  s1; val t1 = e1; at (x in t1) S; 
     * </pre>
     * 
     * @param stmt the AtEach statement to flatten
     * @return a flat statement with the same semantics as stmt
     */
    private StmtSeq flattenAtStmt(AtStmt stmt) {
        List<Stmt> stmts = new ArrayList<Stmt>();
        Expr place = getPrimaryAndStatements(stmt.place(), stmts);
        stmts.add((Stmt) stmt.place(place));
        return syn.createStmtSeq(stmt.position(), stmts);
    }

    /**
     * Flatten the declaration of a local variable.
     * <pre>
     * val x = ({s1; e1})  ->  s1; x = e1; 
     * </pre>
     * 
     * @param stmt the local variable declaration to be flattened
     * @return a flat statement with the same semantics as stmt
     */
    private StmtSeq flattenLocalDecl(LocalDecl stmt) {
        List<Stmt> stmts = new ArrayList<Stmt>();
        stmts.addAll(getStatements(stmt.init()));
        stmts.add(stmt.init(getResult(stmt.init())));
        return syn.createStmtSeq(syn.createStmtSeq(stmt.position(), stmts));
    }

    /**
     * Flatten a conditional statement.
     * <pre>
     * if (({s1; e1})) S1 else S2;  ->  s1; if (t1) S1 else S2; 
     * </pre>
     * 
     * @param stmt the conditional statement to be flattened
     * @return a flat statement with the same semantics as stmt
     */
    private StmtSeq flattenIf(If stmt) {
        List<Stmt> stmts = new ArrayList<Stmt>();
        Expr cond = getPrimaryAndStatements(stmt.cond(), stmts);
        stmts.add(stmt.cond(cond));
        return syn.createStmtSeq(stmt.position(), stmts);
    }

    /**
     * Flatten a for-loop.
     * <pre>
     * for (x in ({s1; e1})) S;  ->  s1; val t1 = e1; for (x in t1) S; 
     * </pre>
     * 
     * @param stmt the for-loop to be flattened
     * @return a flat statement with
     */
    private Stmt flattenForLoop(ForLoop stmt, boolean labeled) {
        List<Stmt> stmts = new ArrayList<Stmt>();
        Expr primary = getPrimaryAndStatements(stmt.domain(), stmts);
        stmt = (ForLoop) stmt.domain(primary);
        stmts.add(label(stmt, labeled));
        return toStmt(stmt.position(), stmts);
    }

    /**
     * Flatten a throw statement.
     * <pre>
     * throw ({s1; e1});  ->  s1; val t1 = e1; throw t1;
     * </pre>
     * 
     * @param stmt the throw statement to be flattened.
     * @return a flat statement with the same semantics as stmt
     */
    private StmtSeq flattenThrow(Throw stmt) {
        List<Stmt> stmts = new ArrayList<Stmt>();
        Expr primary = getPrimaryAndStatements(stmt.expr(), stmts);
        stmts.add(stmt.expr(primary));
        return syn.createStmtSeq(stmt.position(), stmts);
    }


    /**
     * Flatten a switch statement.
     * <pre>
     * switch (({s1; e1})) { S; }  ->  s1; val t1 = e1; switch (t1) { S; }
     * </pre>
     * 
     * @param stmt the switch statement to be flattened.
     * @return a flat statement with the same semantics as stmt
     */
    private StmtSeq flattenSwitch(Switch stmt) {
        List<Stmt> stmts = new ArrayList<Stmt>();
        Expr primary = getPrimaryAndStatements(stmt.expr(), stmts);
        stmts.add(stmt.expr(primary));
        return syn.createStmtSeq(stmt.position(), stmts);
    }

    /**
     * Flatten an Async statement.
     * <pre>
     * async              S;  ->                   async      S; // no-op
     * async (({s1; e1})) S;  ->  s1; val t1 = e1; async (t1) S; 
     * </pre>
     * 
     * @param stmt the Async statement to flatten
     * @return a flat statement with the same semantics as stmt
     */
    private StmtSeq flattenAsync(Async stmt) {
        List<Stmt> stmts = new ArrayList<Stmt>();
        stmts.add((Async) stmt);
        return syn.createStmtSeq(stmt.position(), stmts);
    }

    /**
     * Flatten a return statements.
     * <pre>
     * return;             ->      return; // no-op
     * return ({s1; e1});  ->  s1; val t1 = e1; return t1;
     * </pre>
     * 
     * @param stmt the return statement to be flattened
     * @return a flat statement with the same semantics as stmt
     */
    private StmtSeq flattenReturn(Return stmt) {
        if (null == stmt.expr()) return syn.createStmtSeq(stmt);
        List<Stmt> stmts = new ArrayList<Stmt>();
        Expr expr = getPrimaryAndStatements(stmt.expr(), stmts);
        stmts.add(stmt.expr(expr));
        return syn.createStmtSeq(stmt.position(), stmts);
    }


    /**
     * Flatten the evaluation of an expression.
     * <pre>
     * ({s1; e1});    ->  s1;               if "e1" cannot have side-effects
     * ({s1; e1});    ->  s1; Eval(e1);     if "e1" might have side-effects and it's type is void
     * ({s1; e1));    ->  s1; val v = e1;   if "e1" might have side-effects and it's type isn't void
     * </pre>
     * 
     * @param stmt the evaluation to be flattened.
     * @return a flat statement with the same semantics as stmt
     */
    private StmtSeq flattenEval(Eval stmt) {
        List<Stmt> stmts = new ArrayList<Stmt>();
        stmts.addAll(getStatements(stmt.expr()));
        Expr result = getResult(stmt.expr());
        while (result instanceof ParExpr) 
            result = ((ParExpr) result).expr();
        if (sed.hasSideEffects(result)) {
            if (result instanceof ProcedureCall || result instanceof Assign || result instanceof Unary) {
                stmts.add(syn.createEval(result));
            } else if (!result.type().typeEquals(xts.Void(), context())){
                stmts.add(syn.createLocalDecl(result.position(), Flags.FINAL, Name.makeFresh("dummy"), result));
            } else {
                Warnings.issue(job, "DEBUG: eval : " +result, result.position());
                throw new InternalCompilerError("Cannot flatten " +result+ " at " +result.position()+ " (was " +stmt+ ")");
            }
        }
        return syn.createStmtSeq(syn.createStmtSeq(stmt.position(), stmts));
    }


    // ASK IGOR: how to handle early constructor rules?
    static final boolean JAVA_CONSTRUCTOR_RULES = false;

    /**
     * Flatten a property initializer call.
     * (This might entail handling X10 class constructors differently than Java class constructiors.
     * For the time being, X10 constructors are not flattened.)
     * <pre>
     * property(({s1; e1}), ..., ({sk, ek}));  ->  s1; val t1 = e1; ... sk; val tk = ek; property(t1, ..., tk);
     * </pre>
     * 
     * @param stmt the property call to be flattened
     * @return a flat statements with the same semantics as stmt
     */
    private StmtSeq flattenAssignPropertyCall(AssignPropertyCall stmt) {
        if (JAVA_CONSTRUCTOR_RULES) return syn.createStmtSeq(stmt);
        List<Stmt> stmts = new ArrayList<Stmt>();
        List<Expr> args  = new ArrayList<Expr>();
        for (Expr arg : stmt.arguments()) {
            Expr primary = getPrimaryAndStatements(arg, stmts);
            args.add(primary);
        }
        stmts.add(stmt.arguments(args));
        return syn.createStmtSeq(syn.createBlock(stmt.position(), stmts));
    }

    /**
     * Flatten a special method call (e.g. super() or this()) in a constructor.
     * (This would entail handling X10 class constructors differently than Java class constructiors.
     * For the time being, X10 constructors are not flattened.)
     * <pre>
     * this (({s1; e1}), ..., ({sk; ek}));  ->  {s1; val t1 = e1; ... sk; val tk = ek; this(t1, ... tk)}
     * super(({s1; e1}), ..., ({sk; ek}));  ->  {s1; val t1 = e1; ... sk; val tk = ek; super(t1, ... tk)}
     * </pre>
     * 
     * @param stmt the special call to be flattened
     * @return a flat statement with the same semantics as stmt
     */
    private StmtSeq flattenConstructorCall(ConstructorCall stmt) {
        if (JAVA_CONSTRUCTOR_RULES) return syn.createStmtSeq(stmt);
        List<Stmt> stmts = new ArrayList<Stmt>();
        if (null != stmt.qualifier()) {
            Expr qualifier = getPrimaryAndStatements(stmt.qualifier(), stmts);
            stmt = stmt.qualifier(qualifier);
        }
        List<Expr> args = new ArrayList<Expr>();
        for (Expr arg : stmt.arguments()) {
            Expr primary = getPrimaryAndStatements(arg, stmts);
            args.add(primary);
        }
        stmts.add((ConstructorCall) stmt.arguments(args));
        return syn.createStmtSeq(syn.createBlock(stmt.position(), stmts));
    }

    /**
     * Flatten a while loop.
     * <pre>
     * while (true)       S;  ->  while (true) S; 
     * while (false)      S;  ->  while (false) S;
     * while (({s1; e1})) S;  ->  while (true) { s1; val t1 = e1; if (!t1) break; S;} 
     * </pre>
     * 
     * @param stmt the while loop to be flattened
     * @return a flat statement with the same semantics as stmt
     */
    private Stmt flattenWhile(While_c stmt, boolean labeled) {
        if ( !(stmt.cond() instanceof BooleanLit) ) {
            List<Stmt> stmts = new ArrayList<Stmt>();
            Expr primary = getPrimaryAndStatements(stmt.cond(), stmts);
            stmts.add(syn.createIf( stmt.cond().position(), 
                                    syn.createNot(stmt.cond().position(), primary, this), 
                                    syn.createBreak(stmt.cond().position()), 
                                    null) );
            stmt = (While_c) stmt.cond(syn.createTrue(stmt.cond().position()));
            stmts.add(stmt.body());
            stmt = (While_c) stmt.body(syn.createBlock(stmt.position(), stmts));
        }
        return label(stmt, labeled);
    }

    /**
     * Flatten a do loop.
     * <pre>
     * do { S; } while(true);        ->  do { S; } while(true);
     * do { S; } while(false);       ->  do { S; } while(false);
     * do { S; } while(({s1; e1}));  ->  {var t = false; do { S; s1; val t1 = e1; t = t1} while(t);} 
     * </pre>
     * 
     * @param stmt the do loop to be flattened
     * @return a flat statement with the same semantics as stmt
     */
    private Stmt flattenDo(Do_c stmt, boolean labeled) {
        if (null == stmt.cond() || stmt.cond() instanceof BooleanLit) 
            return label(stmt, labeled);
        List<Stmt> stmts = new ArrayList<Stmt>();
        Position pos = stmt.position();
        LocalDecl tmpLDecl = syn.createLocalDecl( pos, 
                                                  Flags.NONE, 
                                                  syn.createTemporaryName(), 
                                                  xts.Boolean(), 
                                                  syn.createFalse(pos) );
        stmts.add(tmpLDecl);
        
        List<Stmt> bodyStmts = new ArrayList<Stmt>();
        bodyStmts.add(stmt.body());
        Expr primary = getPrimaryAndStatements(stmt.cond(), bodyStmts);
        bodyStmts.add(syn.createAssignment(pos, syn.createLocal(pos, tmpLDecl), Assign.ASSIGN, primary, this));
        stmt = (Do_c) stmt.cond(syn.createLocal(pos, tmpLDecl));
        stmt = (Do_c) stmt.body(syn.createBlock(pos, bodyStmts));
        stmts.add(label(stmt, labeled));
        return syn.createBlock(pos, stmts);
    }

    /** 
     * Flatten a (traditional) for-loop.
     * <pre>
     * for (S1; true;       S2) S3;  ->  {S1; for (;true;) {S3; S2;}} 
     * for (S1; ({s1; e1}); S2) S3;  ->  {S1; for (;true;) {s1; var t1=e1; if (!t1) break; S3; S2;}} 
     * </pre>
     * 
     * @param stmt the for-loop to be flattened
     * @return a flat statement with the same semantics as stmt
     */
    private Stmt flattenFor(For stmt, boolean labeled) {
        List<Stmt> stmts = new ArrayList<Stmt>();
        Position pos = stmt.position();
        stmts.addAll(stmt.inits());
        List<Stmt> bodyStmts = new ArrayList<Stmt>();
        if ((null!=stmt.cond()) && !((stmt.cond() instanceof BooleanLit) && ((BooleanLit) stmt.cond()).value()) ) {
            Expr primary = getPrimaryAndStatements(stmt.cond(), bodyStmts);
            bodyStmts.add(syn.createIf( stmt.cond().position(), 
                                        syn.createNot(stmt.cond().position(), primary, this), 
                                        syn.createBreak(stmt.cond().position()), 
                                        null) );
            stmt = stmt.cond(syn.createTrue(pos)); 
        }
        if (labelInfo.get(labelInfo.size()-1).newLabelUsed) {
             bodyStmts.add(syn.createLabeledStmt(stmt.body().position(), 
                                                 labelInfo.get(labelInfo.size()-1).newLabel, 
                                                 stmt.body()));
        } else {
            bodyStmts.add(stmt.body());
        }
        bodyStmts.add(syn.createBlock( pos, new ArrayList<Stmt>(stmt.iters())));
        stmt = stmt.inits(Collections.<ForInit>emptyList());
        stmt = stmt.iters(Collections.<ForUpdate>emptyList());
        stmt = stmt.body(syn.createBlock(pos, bodyStmts));
        stmts.add(label(stmt, labeled));
//        Id dummy = xnf.Id(Position.COMPILER_GENERATED, "dummy");
//        return syn.toStmtSeq(xnf.Labeled(pos, dummy, syn.toStmtSeq(syn.createBlock(pos, stmts))));
        return syn.createBlock(pos, stmts);
    }

    /**
     * Inspect branch statements. Redirect branches targeting For headers.
     * <pre>
     * continue Label1;   ->  break new-Label1;  // if Label1 is on a For
     * continue;          ->  break new-Label1;  // if enclosing loop is a For 
     * </pre>
     * 
     * @param stmt the branch statement to possibly be redirected
     * @return either the original branch or the redirected branch with 
     * the same semantics as stmt
     */
    private StmtSeq inspectBranch(Branch stmt) {
        Id branchTarget = stmt.labelNode();
        int i = labelInfo.size() - 1;
        boolean saveoldLabelUsed = true;
        if (branchTarget != null) {
            // Find the target in our label stack. It must be there.
            for ( ; ; i--) {
                Id oldLbl = labelInfo.get(i).oldLabel;
                if (oldLbl == null) continue;
                if (branchTarget.id().equals(oldLbl.id())) {
                    saveoldLabelUsed = labelInfo.get(i).oldLabelUsed;
                    labelInfo.get(i).oldLabelUsed = true;
                    break;
                }
            }            
        }
        if (stmt.kind() == Branch.CONTINUE) {
            // If the continue did not have a taget and we had labeled blocks,
            // we are not yet at the scope we want to target.
            for (; !labelInfo.get(i).loop; i--)
                ;
            // A null newLabel => loop was not a "For" - don't redirect
            if (labelInfo.get(i).newLabel != null) {
                if (DEBUG) {
                    System.out.println("rewriting a continue from old label " + labelInfo.get(i).oldLabel);
                    System.out.println("to break with label " + labelInfo.get(i).newLabel);
                }
                labelInfo.get(i).newLabelUsed = true;
                labelInfo.get(i).oldLabelUsed = saveoldLabelUsed;
                return syn.createStmtSeq(syn.createBreak(stmt.position(), labelInfo.get(i).newLabel.toString()));
            }
        }

        return syn.createStmtSeq(stmt);
    }
    
    /**
     * Flatten a remote activity invocation (Future, At, or Async).
     * 
     * @param rai the remote activity invocation to flatten
     * @param stmts a list of statements on which flattening assignments may be appended
     * @return a flat remote activity invocation equal to rai (after whatever assignments are appended to stmts as a side effect)
     */
    private RemoteActivityInvocation flattenRemoteActivityInvocation (RemoteActivityInvocation rai, List<Stmt> stmts) {
        if (null == rai.place()) return rai;
        Expr primary = getPrimaryAndStatements(rai.place(), stmts);
        return rai.place(primary);
    }

    // Helper methods to interact with flat StmtExprs
    //
    // Flat StmtExpr invariant:
    //    The result Expr of a StmtExpr is a flat Expr, amd
    //    The Stmt's of a StmtExpr contain no non-flat sub-expressions.
    //
    // An Expr is primary iff it is a literal constant or a final variable instance (or here or this or super).
    // Literal constants, named variables, and calls to this(), super(), and here() are primary.

    /**
     * Extract the preliminary statements from a compound statement expression.
     * 
     * @param expr the expression from which statements are to be extracted
     * @return a list of statements taken from expr (this list will be empty unless expr is a compound expression)
     */
    private List<Stmt> getStatements(Expr expr) {
        if (expr instanceof StmtExpr) return ((StmtExpr) expr).statements();
        return new ArrayList<Stmt>();
    }

    /**
     * Extract the result expression from a compound statement expression.
     * 
     * @param expr the exression from which the result is to be extracted
     * @return the result expression of expr
     */
    private Expr getResult(Expr expr) {
        if (expr instanceof StmtExpr) return ((StmtExpr) expr).result();
        return expr;
    }

    /**
     * Extract a final variable or literal constant as the result of a statement expression.
     * As a side effect, produce a list of statements to be executed before the primary is evaluated.
     * (This list will usually include the assignment to the primary if it is a variable.)
     * 
     * @param expr the expression from which the primary and statements are to be extracted.
     * return the primary extracted from expr
     */
    private Expr getPrimaryAndStatements(Expr expr, List<Stmt> stmts) {
            if (expr instanceof StmtExpr) {;
                stmts.addAll(getStatements(expr));
                expr = getResult(expr);
            }
            if (!isPrimary(expr)) {
                LocalDecl tmpLDecl = syn.createLocalDecl(expr.position(), Flags.FINAL, syn.createTemporaryName(), expr);
                stmts.add(tmpLDecl);
                expr = syn.createLocal(expr.position(), tmpLDecl);  
            }
            return expr;
    }

    /**
     * A primary is an Expr that does not have any sub-expressions.  Is the given Expr primary?
     * 
     * @param expr the given Expr
     * @return true if expr has no sub-expressions; otherwise, false
     * TODO: it may be expedient to consider other kinds of Expr primary on an interim basis.
     */
    public static boolean isPrimary(Expr expr) {
        if (null == expr) return true; // DEBUG
        if (expr instanceof Lit) return true;
        if (expr instanceof Local) return ((Local) expr).flags().contains(Flags.FINAL);
        if (expr instanceof Special) return true;
        if (expr instanceof Here_c) return true;
        return false;
    }

    /**
     * Create a flat expression from a possibly empty list of flat statements and a flat expression.
     * 
     * @param pos the Position of the flat expression in source code
     * @param stmts the sequence of flat statement to precede evaluation of the flat expression
     * @param expr the result expression
     * @return expr, if stmts is empty; a compound expression comprising stmts and expr, otherwise
     */
    private Expr toFlatExpr(Position pos, List<Stmt> stmts, Expr expr) {
        if (stmts.isEmpty()) return expr;
        return syn.createStmtExpr(pos, stmts, expr);
    }

    /**
     * @param pos
     * @param stmts
     * @return
     */
    private Stmt toStmt(Position pos, List<Stmt> stmts) {
        if (1 == stmts.size() && stmts.get(0) == pos ) return stmts.get(0);
        return syn.createBlock(pos, stmts);
    }

    /**
     * Prefix a list of labels to a statement.
     * 
     * @param stmt the statement to be labeled
     * @param labeled a flag indicating the parent is Labeled
     * @return either the stmt (there are no labels), or a Labeled statement
     */
    private Stmt label(Stmt stmt, boolean labeled) {
        if (!labeled) return stmt;
        if (!labelInfo.get(labelInfo.size()-1).oldLabelUsed) return stmt;
        return(syn.createLabeledStmt(stmt.position(), 
                              labelInfo.get(labelInfo.size()-1).oldLabel, 
                              stmt));        
    }

}
