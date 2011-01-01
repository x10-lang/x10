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
package x10.optimizations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.BooleanLit;
import polyglot.ast.Branch;
import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.For;
import polyglot.ast.ForInit;
import polyglot.ast.ForUpdate;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.IntLit;
import polyglot.ast.Labeled;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.Loop;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.Switch;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.ast.Assign.Operator;
import polyglot.frontend.Job;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.ClosureCall;
import x10.ast.ForLoop;
import x10.ast.StmtExpr;
import x10.ast.StmtSeq;
import x10.ast.X10Binary_c;
import x10.ast.X10Call;
import x10.ast.X10Cast;
import x10.ast.X10Formal;
import x10.ast.SettableAssign;
import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.types.ConstrainedType;
import x10.types.X10FieldInstance;
import x10.types.MethodInstance;

import x10.types.checker.Converter;
import x10.types.constraints.CConstraint;
import x10.util.AltSynthesizer;
import x10.visit.ConstantPropagator;
import x10.visit.Desugarer;
import x10.visit.ExpressionFlattener;

/**
 * Optimize loops of the form:  for (formal in domain) S.
 * If domain is, or can be lowered to, a Region, that is rectangular with know rank,
 * tranform into a nest of traditional for-loops with iterates of type int.
 * 
 * @author vj
 * @author Bowen Alpern
 */
public class ForLoopOptimizer extends ContextVisitor {


    private static final Name ITERATOR = Name.make("iterator");
    private static final Name HASNEXT  = Name.make("hasNext");
    private static final Name REGION   = Name.make("region");
    private static final Name DIST     = Name.make("dist");
    private static final Name NEXT     = Name.make("next");
    private static final Name MAKE     = Name.make("make");
    private static final Name RANK     = Name.make("rank");
    private static final Name MIN      = Name.make("min");
    private static final Name MAX      = Name.make("max");
    private static final Name SET      = SettableAssign.SET;

    private final TypeSystem xts;
    private AltSynthesizer   syn;

    public ForLoopOptimizer(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = ts;
        syn = new AltSynthesizer(job, ts, nf);
    }

    @Override
    public NodeVisitor begin() {
        syn.begin();
        return super.begin();
    }

    @Override
    public ContextVisitor context(Context c) {
        ForLoopOptimizer res = (ForLoopOptimizer) super.context(c);
        if (res != this)
            res.syn = (AltSynthesizer) syn.context(c);
        return res;
    }

    @Override
    public NodeVisitor superEnter(Node parent, Node n) {
        ForLoopOptimizer res = (ForLoopOptimizer) super.superEnter(parent, n);
        if (res != this)
            res.syn = (AltSynthesizer) syn.enter(parent, n);
        return res;
    }

    private Name label = null;
    protected Name label() { return label; }
    protected ForLoopOptimizer label(Name label) {
        ForLoopOptimizer flo = (ForLoopOptimizer) copy();
        flo.label = label;
        return flo;
    }

    @Override
    protected NodeVisitor enterCall(Node n) throws SemanticException {
        // Set the label when seeing a Labeled; clear it for anything but a ForLoop
        if (n instanceof Labeled) {
            return label(((Labeled) n).labelNode().id());
        } else if (n instanceof ForLoop) {
            return this;
        }
        return label(null);
    }

    @Override
    public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        if (n instanceof ForLoop)
            return visitForLoop((ForLoop) n);
        if (n instanceof Labeled) {
            Labeled l = (Labeled) n;
            ForLoopOptimizer flo = (ForLoopOptimizer) v;
            assert (l.labelNode().id().equals(flo.label()));
            if (old instanceof Labeled && ((Labeled) old).statement() instanceof ForLoop && !(l.statement() instanceof ForLoop)) {
                return l.statement(); // The label will have been propagated onto the loop
            }
        }
        return n;
    }

    private static final boolean VERBOSE = false;
    
    /**
     * Transform a ForLoop with a Point iterate over a rectangular region to a nest of simple For loops.
     * <pre>
     * for (p:Point(k+1) in r) S ->
     *     {                                      // k=r.rank-1
     *        point = Rail.make[Int](k+1);        // if p is named
     *        mink=r.min(k); maxk=r.max(k);
     *        ...
     *        min0=r.min(0); max0=r.max(0); 
     *        for (i0 = min0; i0<=max0; i0+=1) {
     *           point(0) = i0;                   // if p is named
     *           ...
     *              for (ik = mink; ik<=maxk; ik+=1) {
     *                 point(k) = ik;             // if p is named
     *                 p = Point.make(point);     // if p is named
     *                 S
     *              }
     *        }
     *     }
     * </pre>
     * <tt>r</tt> can be an Array, a DistArray, a Dist, or a Region.
     * 
     * Also, desugars untransformed ForLoops, TODO: move this to the desugarer
     * <pre>
     * for (x in y) S -> for (i=y.iterator(); i.hasNext(); ) { x=i.next(); S }
     * </pre>
     * 
     * @param loop the ForLoop to be transformed
     * @return the transformed loop
     */
    public Node visitForLoop(ForLoop loop) {
        
        Position     pos        = loop.position();
        X10Formal    formal     = (X10Formal) loop.formal();
        Expr         domain     = loop.domain();
        Stmt         body       = loop.body();
        
        if (VERBOSE) {
            System.out.println("\nOptimizing ForLoop at " +pos);
            loop.prettyPrint(System.out);
            System.out.println();
        }

        // if domain <: DistArray, transform to Distribution
        if (xts.isX10DistArray(domain.type())) {
            if (VERBOSE) System.out.println("  domain is DistArray, tranforming to Dist");
            domain = syn.createFieldRef(pos, domain, DIST);
            assert (null != domain);
        }

        // if domain <: Distribution, transform to Region
        if (xts.isDistribution(domain.type())) {
            if (VERBOSE) System.out.println("  domain is Dist, transforming to Region");
            domain = syn.createFieldRef(pos, domain, REGION);
            assert (null != domain);
        }
        
        // if domain <: Array, transform to Region
        if (xts.isX10Array(domain.type())) {
            if (VERBOSE) System.out.println("  domain is Array, tranforming to Region");
            domain = syn.createFieldRef(pos, domain, REGION);
            assert (null != domain);
        }

        Id           label      = syn.createLabel(pos);
        Context      context    = (Context) context();
        List<Formal> formalVars = formal.vars();
        boolean      named      = !formal.isUnnamed();
        ConstrainedType domainType = Types.toConstrainedType(domain.type());
        boolean      isRect     = domainType.isRect(context);
        Integer      domainRank = (Integer) getPropertyConstantValue(domain, RANK);
        int          rank       = (null != domainRank) ? (int) domainRank :
                                  (null != formalVars) ? formalVars.size() : 
                                  -1;
        assert null == formalVars || formalVars.isEmpty() || formalVars.size() == rank;

        // SPECIAL CASE (XTENLANG-1340):
        // for (p(i) in e1..e2) S  =>
        //     val min=e1; val max=e2; for(var z:Int=min; z<=max; z++){ val p=Point.make(z); val i=z; S }
        // TODO inline (min and max), scalar replace Region object and its constituent Arrays then delete this code
        //
        if (1 == rank && domain instanceof Call && ((Call)domain).target().type().isInt() &&
                ((Call)domain).name().id().equals(X10Binary_c.binaryMethodName(Binary.DOT_DOT))) {
            List<Expr> args = ((Call) loop.domain()).arguments();
            assert (args.size() == 2);
            Expr low = args.get(0);
            Expr high = args.get(1);
            Name varName  = null == formalVars || formalVars.isEmpty() ? Name.makeFresh("i") 
                                                                       : Name.makeFresh(formalVars.get(0).name().id());
            Name minName  = Name.makeFresh(varName+ "min");
            Name maxName  = Name.makeFresh(varName+ "max");
            LocalDecl minLDecl = syn.createLocalDecl(pos, Flags.FINAL, minName, low);
            LocalDecl maxLDecl = syn.createLocalDecl(pos, Flags.FINAL, maxName, high);
            
            LocalDecl varLDecl = syn.createLocalDecl(pos, Flags.NONE, varName, xts.Int(), syn.createLocal(pos, minLDecl));
            Expr cond = syn.createBinary(domain.position(), syn.createLocal(pos, varLDecl), Binary.LE, syn.createLocal(pos, maxLDecl));
            Expr update = syn.createAssign(domain.position(), syn.createLocal(pos, varLDecl), Assign.ADD_ASSIGN, syn.createIntLit(1));
            
            List<Stmt> bodyStmts = new ArrayList<Stmt>();
            if (named) {
                // declare the formal variable as a local and initialize it 
                Expr formExpr = syn.createStaticCall(pos, formal.declType(), MAKE, syn.createLocal(pos, varLDecl));
                LocalDecl formalLDecl = syn.transformFormalToLocalDecl(formal, formExpr);
                bodyStmts.add(formalLDecl);
            }
            if (null != formalVars && !formalVars.isEmpty()) {
                bodyStmts.add(syn.transformFormalToLocalDecl((X10Formal) formalVars.get(0), syn.createLocal(pos, varLDecl)));
            }
            bodyStmts.add(body);
            body = syn.createBlock(loop.body().position(), bodyStmts);
            For forLoop = syn.createStandardFor(pos, varLDecl, cond, update, body);
            Stmt newLoop = forLoop;
            if (label() != null) {
                newLoop = syn.createLabeledStmt(pos, label(), forLoop);
            }
            return syn.createBlock(pos, minLDecl, maxLDecl, newLoop);
        }

        // transform rectangular regions of known rank 
        if (xts.isRegion(domainType) && isRect && rank > 0) {
            assert xts.isPoint(formal.declType());
            if (VERBOSE) System.out.println("  rectangular region, rank=" +rank+ " point=" +formal);
            
            if (1 < rank) {
                body = labelFreeBreaks(body, label);
            }
            
            List<Stmt> stmts      = new ArrayList<Stmt>();
            Name       prefix     = named ? formal.name().id() : Name.make("p");
            
            // cache the value of domain in a local temporary variable
            LocalDecl  domLDecl   = syn.createLocalDecl(domain.position(), Flags.FINAL, Name.makeFresh(prefix), domain);
            stmts.add(domLDecl);
            
            // Prepare to redeclare the formal iterate as local Point variable (if the formal is not anonymous)
            Type       indexType  = null; // type of the formal var initializer (if any)
            LocalDecl  indexLDecl = null; // redeclaration of the formal var (if it has a name)
            if (named) {
                // create a rail to contain the value of the formal at each iteration
                Name       indexName  = Name.makeFresh(prefix);
                           indexType  = xts.Rail(xts.Int()); // PlaceChecker.AddIsHereClause(xts.Rail(xts.Int()), context);
                List<Type> railType   = Collections.<Type>singletonList(xts.Int());
                Expr       indexInit  = syn.createStaticCall(pos, xts.Rail(), MAKE, railType, syn.createIntLit(rank));
                           indexLDecl = syn.createLocalDecl(pos, Flags.FINAL, indexName, indexType, indexInit);
                // add the declaration of the index rail to the list of statements to be executed before the loop nest
                stmts.add(indexLDecl);
            }
            
            LocalDecl varLDecls[] = new LocalDecl[rank];
            // syn.create the loop nest (from the inside out)
            for (int r=rank-1; 0<=r; r--) {
                
                // syn.create new names for the r-th iterate and limits
                Name varName  = null == formalVars || formalVars.isEmpty() ? Name.makeFresh(prefix) 
                                                                           : Name.makeFresh(formalVars.get(r).name().id());
                Name minName  = Name.makeFresh(varName+ "min");
                Name maxName  = Name.makeFresh(varName+ "max");
                
                // create an AST node for the calls to domain.min() and domain.max()
                Expr minVal   = syn.createInstanceCall(pos, syn.createLocal(domain.position(), domLDecl), MIN, syn.createIntLit(r));
                Expr maxVal   = syn.createInstanceCall(pos, syn.createLocal(domain.position(), domLDecl), MAX, syn.createIntLit(r));
                
                // create an AST node for the declaration of the temporary locations for the r-th var, min, and max
                LocalDecl minLDecl = syn.createLocalDecl(pos, Flags.FINAL, minName, minVal);
                LocalDecl maxLDecl = syn.createLocalDecl(pos, Flags.FINAL, maxName, maxVal);
                LocalDecl varLDecl = syn.createLocalDecl(pos, Flags.NONE, varName, xts.Int(), syn.createLocal(pos, minLDecl));
                
                varLDecls[r] = varLDecl;
                
                // add the declarations for the r-th min and max to the list of statements to be executed before the loop nest
                stmts.add(minLDecl);
                stmts.add(maxLDecl);
                
                // create expressions for the second and third positions in the r-th for clause
                Expr cond = syn.createBinary(domain.position(), syn.createLocal(pos, varLDecl), Binary.LE, syn.createLocal(pos, maxLDecl));
                Expr update = syn.createAssign(domain.position(), syn.createLocal(pos, varLDecl), Assign.ADD_ASSIGN, syn.createIntLit(1));
                
                List<Stmt> bodyStmts = new ArrayList<Stmt>(); 
                
                if (null != formalVars && !formalVars.isEmpty()) {
                    bodyStmts.add(syn.transformFormalToLocalDecl((X10Formal) formalVars.get(r), syn.createLocal(pos, varLDecl)));
                }
                
                // concoct declaration for formal, in case it might be referenced in the body
                if (named) {
                    // set the r-th slot int the index rail to the current value of the r-th iterate
                    Expr setExpr = syn.createInstanceCall( pos, 
                                                           syn.createLocal(pos, indexLDecl), 
                                                           SET, 
                                                           syn.createLocal(pos, varLDecl), 
                                                           syn.createIntLit(r) );
                    bodyStmts.addAll(syn.convertToStmtList(setExpr));
                    if (r+1 == rank) { // the innermost loop
                        // declare the formal variable as a local and initialize it to the index rail
                        Expr formExpr = syn.createStaticCall(pos, formal.declType(), MAKE, syn.createLocal(pos, indexLDecl));
                        bodyStmts.add(syn.transformFormalToLocalDecl(formal, formExpr));
                    }
                }
                bodyStmts.add(body);
                body = syn.createBlock(pos, bodyStmts);
                
                // syn.create the AST node for the r-th concocted for-statement
                body = syn.createStandardFor(pos, varLDecl, cond, update, body);
                
            }
            if (1 < rank) {
                Position position = body.position();
                body = syn.createLabeledStmt(position, label, body);
            }
            body = explodePoint(formal, indexLDecl, varLDecls, body);
            if (label() != null) {
                body = syn.createLabeledStmt(pos, label(), body);
            }
            stmts.add(body);
            Block result = syn.createBlock(pos, stmts);
            if (VERBOSE) result.dump(System.out);
            return result;
        }

        assert (xts.isSubtype(domainType, xts.Iterable(xts.Any()), context)); 
        Name iterName        = named ? Name.makeFresh(formal.name().id()) : Name.makeFresh();
        Expr iterInit        = syn.createInstanceCall(pos, domain, ITERATOR);
        LocalDecl iterLDecl  = syn.createLocalDecl(pos, Flags.FINAL, iterName, iterInit);
        Expr hasExpr         = syn.createInstanceCall(pos, syn.createLocal(pos, iterLDecl), HASNEXT);
        Expr nextExpr        = syn.createInstanceCall(pos, syn.createLocal(pos, iterLDecl), NEXT);
        if (!xts.typeEquals(nextExpr.type(), formal.declType(), context)) {
            Expr newNextExpr = syn.createCoercion(pos, nextExpr, formal.declType());
            if (null == newNextExpr)
                throw new InternalCompilerError("Unable to cast "+nextExpr+" to the iterated type "+formal.declType(), pos);
            nextExpr = newNextExpr;
        }
        List<Stmt> bodyStmts = new ArrayList<Stmt>();
        LocalDecl varLDecl   = syn.transformFormalToLocalDecl(formal, nextExpr);
        bodyStmts.add(varLDecl);
        if (null != formalVars && !formalVars.isEmpty()) try {
            bodyStmts.addAll(formal.explode(this));
        } catch (SemanticException e) {
            throw new InternalCompilerError("We cannot explode the formal.  Huh?", formal.position(), e);
        }
        if (body instanceof Block) {
            bodyStmts.addAll(((Block) body).statements());
        } else {
            bodyStmts.add(body);
        }
        Stmt result = syn.createStandardFor(pos, iterLDecl, hasExpr, syn.createBlock(pos, bodyStmts));
        if (label() != null) {
            result = syn.createLabeledStmt(pos, label(), result);
        }
        if (VERBOSE) result.dump(System.out);
        return result;
    }

    /**
     * Replace calls to the apply method on point with corresponding calls to the corresponding method on rail throughout the body
     * 
     * @param point a Point formal variable
     * @param rail the underlying Rail defining point
     * @param body the AST containing the usses of point to be replaced 
     * @return a copy of body with every call to point.apply() replaced by a call to rail.apply()
     */
    private Stmt explodePoint(final X10Formal point, final LocalDecl rail, final LocalDecl[] indices, final Stmt body) {
        ContextVisitor pointExploder = new ContextVisitor(job, xts, nodeFactory()) {
            /* (non-Javadoc)
             * @see polyglot.visit.ErrorHandlingVisitor#leaveCall(polyglot.ast.Node)
             */
            @Override
            protected Node leaveCall(Node n) throws SemanticException {
                if (n instanceof Call) {
                    X10Formal p = point;
                    LocalDecl r = rail;
                    LocalDecl[] is = indices;
                    Call call = (Call) n;
                    Receiver target = call.target();
                    if (target instanceof Local && call.methodInstance().name().equals(ClosureCall.APPLY)) {
                        if (((Local) target).localInstance().def() == point.localDef()) {
                            List<Expr> args = call.arguments();
                            assert (1 == args.size());
                            Expr arg = args.get(0);
                            if (arg.isConstant()) {
                                int i =(Integer) arg.constantValue();
                                return syn.createLocal(n.position(), indices[i]);
                            }
                            call = call.target(syn.createLocal(target.position(), rail));
                            call = call.methodInstance(syn.createMethodInstance( rail.type().type(), 
                                                                                 ClosureCall.APPLY, 
                                                                                 Collections.<Type>emptyList(),
                                                                                 call.methodInstance().formalTypes()));
                            return call;
                        }
                    }
                }
                return n;
            }
        };
        return (Stmt) body.visit(pointExploder.begin());
    }

    /**
     * Change free unlabeled breaks in the body to refer to a given label.
     * 
     * @param body the body of a ForLoop
     * @param label a label to be attached to the outermost synthesized For
     * @return aa copy of the body with its free breaks suitably captured
     */
    private Stmt labelFreeBreaks(Stmt body, final Id label) {
        return (Stmt) body.visit(new NodeVisitor(){
            @Override
            public Node override(Node node) { // these constructs capture free breaks
                if (node instanceof Loop) return node;
                if (node instanceof Switch) return node;
                return null;
            }
            @Override
            public Node leave(Node old, Node n, NodeVisitor v) {
                if (n instanceof Branch) {
                    Branch b = (Branch) n;
                    if (b.kind().equals(Branch.BREAK) && null == b.labelNode()) {
                        return b.labelNode(label);
                    }
                }
                return n;
            }
        });
    }

    // General helper methods

    /** 
     * Obtain the constant value of a property of an expression, if that value is known at compile time.
     * 
     * @param expr the Expr whose property is to be extracted
     * @param name the Name of the property to extract
     * @return the value of the named property of expr if it is a compile-time constant, or null if none
     * TODO: move into ASTQuery
     */
    public Object getPropertyConstantValue(Expr expr, Name name) {
        X10FieldInstance propertyFI = Types.getProperty(expr.type(), name);
        if (null == propertyFI) return null;
        Expr propertyExpr = syn.createFieldRef(expr.position(), expr, propertyFI);
        if (null == propertyExpr) return null;
        return ConstantPropagator.constantValue(propertyExpr);
    }

    /**
     * Add a constraint to the type that binds a given property to a given value.
     * 
     * @param type the Type to be constrained
     * @param name the Name of a property of type
     * @param value the value of the named property for this type
     * @return the type with the additional constraint {name==value}, or null if no such property
     * TODO: move into Synthesizer
     */
    public static Type addPropertyConstraint(Type type, Name name, XTerm value) throws XFailure {
    	// Must ensure that arg to findOrSynthesize is a constrained type
    	// since the synthesized property may need to refer to self.
    	ConstrainedType type1 = Types.toConstrainedType(type);
    	XTerm property = type1.findOrSynthesize(name);
    	if (null == property) return null;
    	return Types.addBinding(type1, property, value);
    }

    /**
     * Add a constraint to the type that binds a given property to a given value.
     * 
     * @param type the Type to be constrained
     * @param name the Name of a property of type
     * @param value the value of the named property for this type
     * @return the type with the additional constraint {name==value}, or null if no such property
     * TODO: move into Synthesizer
     */
   /* public static Type addPropertyConstraint(Type type, Name name, Object value) {
        return addPropertyConstraint(type, name, XTerms.makeLit(value));
    }*/

    /**
     * Add a self constraint to the type that binds self to a given value.
     * 
     * @param type the Type to be constrained
     * @param value the value of self for this type
     * @return the type with the additional constraint {self==value}, or null if the proposed
     * binding is inconsistent
     * TODO: move into Synthesizer
     */
    public static Type addSelfConstraint(Type type, XTerm value) {
        try {
            return Types.addSelfBinding(type, value);
        } catch (XFailure e) {
            return null;
        }
    }

}