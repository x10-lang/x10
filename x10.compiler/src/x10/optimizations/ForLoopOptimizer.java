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
package x10.optimizations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Binary.Operator;
import polyglot.ast.Block;
import polyglot.ast.Branch;
import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.For;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Labeled;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.Loop;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.Switch;
import polyglot.frontend.Job;
import polyglot.types.Context;
import polyglot.types.Flags;
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
import x10.ast.X10Binary_c;
import x10.ast.X10Formal;
import x10.ast.SettableAssign;
import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.types.ConstrainedType;
import x10.types.X10FieldInstance;
import x10.types.constants.ConstantValue;
import x10.types.constants.IntegralValue;
import x10.types.matcher.X10TypeMatcher;

import x10.util.AltSynthesizer;
import x10.visit.ConstantPropagator;

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
    private static final Name RANGE    = Name.make("range");
    private static final Name INDICES  = Name.make("indices");
    private static final Name MIN      = Name.make("min");
    private static final Name MAX      = Name.make("max");
    private static final Name SIZE     = Name.make("size");
    private static final Name SET      = SettableAssign.SET;
    private static final Name APPLY    = ClosureCall.APPLY;

    private final TypeSystem xts;
    private AltSynthesizer   syn;

    public ForLoopOptimizer(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = ts;
        syn = new AltSynthesizer(ts, nf);
    }

    private Name label = null;
    protected Name label() { return label; }
    protected ForLoopOptimizer label(Name label) {
        ForLoopOptimizer flo = (ForLoopOptimizer) shallowCopy();
        flo.label = label;
        return flo;
    }

    @Override
    protected NodeVisitor enterCall(Node parent, Node n) {
        // Set the label when seeing a Labeled; clear it for anything but a ForLoop
        ForLoopOptimizer res = this;
        if (n instanceof Labeled) {
            res = res.label(((Labeled) n).labelNode().id());
        } else if (!(n instanceof ForLoop)) {
            res = res.label(null);
        }
        return res;
    }

    @Override
    public Node leaveCall(Node old, Node n, NodeVisitor v) {
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
     *        point = Rail.make[Long](k+1);        // if p is named
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
        if (xts.isX10RegionDistArray(domain.type())) {
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
        if (xts.isX10RegionArray(domain.type())) {
            if (VERBOSE) System.out.println("  domain is Array, tranforming to Region");
            domain = syn.createFieldRef(pos, domain, REGION);
            assert (null != domain);
        }
        
        Id           label      = syn.createLabel(pos);
        Context      context    = (Context) context();
        List<Formal> formalVars = formal.vars();
        boolean      named      = !formal.isUnnamed();
        ConstrainedType domainType = Types.toConstrainedType(domain.type());
        boolean isRect     = domainType.isRect(context);
        Long domainRank = (Long) getPropertyConstantValue(domain, RANK);
        int rank       = (null != domainRank) ? domainRank.intValue() : (null != formalVars) ? formalVars.size() : -1;
        assert null == formalVars || formalVars.isEmpty() || formalVars.size() == rank;

        // Transform loops over IntRange and LongRange into counted for loops
        if (xts.isIntRange(domainType) || xts.isLongRange(domainType)) {
            Name varName  = null == formalVars || formalVars.isEmpty() ? Name.makeFresh("i") : Name.makeFresh(formalVars.get(0).name().id());
            Name minName  = Name.makeFresh(varName+ "min");
            Name maxName  = Name.makeFresh(varName+ "max");
            boolean isLong = xts.isLongRange(domainType);
            
            Expr low;
            Expr high;
            LocalDecl  domLDecl;
            Operator compareOp = Binary.LE;
            if (domain instanceof Call && ((Call)domain).name().id().equals(X10Binary_c.binaryMethodName(Binary.DOT_DOT))) {
                // SPECIAL CASE: if The IntRange is being created in the for loop header itself with the .. operator, avoid creating it entirely.
                List<Expr> args = ((Call) loop.domain()).arguments();
                assert (args.size() == 2);
                low = args.get(0);
                high = args.get(1);
                domLDecl = null;
            } else if (domain instanceof Call && ((Call)domain).name().id().equals(RANGE) &&
                    ((Call)domain).target() instanceof Expr && xts.isRail(((Call)domain).target().type())){
                // SPECIAL CASE: if the LongRange is being returned from Rail.range(), then avoid creating it and use 0..size-1
                domLDecl   = syn.createLocalDecl(domain.position(), Flags.FINAL, Name.makeFresh("rail"), (Expr)((Call)domain).target());
                low = syn.createLongLit(0);
                high =  syn.createFieldRef(pos, syn.createLocal(pos, domLDecl), SIZE);
                compareOp = Binary.LT;
            } else {
                domLDecl   = syn.createLocalDecl(domain.position(), Flags.FINAL, Name.makeFresh(varName+"domain"), domain);
                low = syn.createFieldRef(pos, syn.createLocal(pos, domLDecl), MIN);
                high =  syn.createFieldRef(pos, syn.createLocal(pos, domLDecl), MAX);
            }
            
            LocalDecl minLDecl = syn.createLocalDecl(pos, Flags.FINAL, minName, low);
            LocalDecl maxLDecl = syn.createLocalDecl(pos, Flags.FINAL, maxName, high);
           
            LocalDecl varLDecl = syn.createLocalDecl(pos, Flags.NONE, varName, isLong ? xts.Long() : xts.Int(), syn.createLocal(pos, minLDecl));
            Expr cond = syn.createBinary(domain.position(),
                                         syn.createLocal(pos, varLDecl),
                                         compareOp,
                                         syn.createLocal(pos, maxLDecl),
                                         this );
            Expr update = syn.createAssign(domain.position(),
                                           syn.createLocal(pos, varLDecl),
                                           Assign.ADD_ASSIGN,
                                           isLong ? syn.createLongLit(1) : syn.createIntLit(1),
                                           this);
            
            List<Stmt> bodyStmts = new ArrayList<Stmt>();
            if (named) {
                // declare the formal variable as a local and initialize it 
                LocalDecl formalLDecl = syn.createLocalDecl(formal, syn.createLocal(pos, varLDecl));
                bodyStmts.add(formalLDecl);
            }
            bodyStmts.add(body);
            body = syn.createBlock(loop.body().position(), bodyStmts);
            For forLoop = syn.createStandardFor(pos, varLDecl, cond, update, body);
            Stmt newLoop = forLoop;
            if (label() != null) {
                newLoop = syn.createLabeledStmt(pos, label(), forLoop);
            }
            if (domLDecl == null ) {
                return syn.createBlock(pos, minLDecl, maxLDecl, newLoop);
            } else {
                return syn.createBlock(pos, domLDecl, minLDecl, maxLDecl, newLoop);
            }
        }
        
        // transform loops over rectangular regions or iteration spaces of known rank 
        if ((xts.isRegion(domainType) || xts.isIterationSpace(domainType)) && isRect && rank > 0) {
            assert xts.isPoint(formal.declType());
            if (VERBOSE) System.out.println("  rectangular region/iteration space, rank=" +rank+ " point=" +formal);
            
            if (1 < rank) {
                body = labelFreeBreaks(body, label);
            }
            
            List<Stmt> stmts      = new ArrayList<Stmt>();
            Name       prefix     = named ? formal.name().id() : Name.make("p");
            Operator compareOp = Binary.LE;
                        
            // Prepare to redeclare the formal iterate as local Point variable (if the formal is not anonymous)
            Type       indexType  = null; // type of the formal var initializer (if any)
            LocalDecl  indexLDecl = null; // redeclaration of the formal var (if it has a name)
            if (named) {
                // create a rail to contain the value of the formal at each iteration
                Name       indexName  = Name.makeFresh(prefix);
                           indexType  = Types.makeRailOf(xts.Long(), rank, pos);           
                Expr       indexInit  = syn.createTuple(pos, rank, syn.createLongLit(0));
                           indexLDecl = syn.createLocalDecl(pos, Flags.FINAL, indexName, indexType, indexInit);
                // add the declaration of the index rail to the list of statements to be executed before the loop nest
                stmts.add(indexLDecl);
            }
            
            boolean simpleArrayOpt = false;
            LocalDecl  domLDecl = null;
            if (domain instanceof Call && ((Call)domain).name().id().equals(INDICES) && xts.isArray(((Call)domain).target().type())) {
                // SPECIAL CASE: if The IterationSpace is being created in the for loop header by calling Array.indices(), avoid creating it entirely.
                domLDecl = syn.createLocalDecl(domain.position(), Flags.FINAL, Name.makeFresh(prefix), (Expr)((Call)domain).target());
                simpleArrayOpt = true;
                compareOp = Binary.LT;
            } else {
                // cache the value of domain in a local temporary variable
                domLDecl   = syn.createLocalDecl(domain.position(), Flags.FINAL, Name.makeFresh(prefix), domain);
            }
            stmts.add(domLDecl);
            
            LocalDecl varLDecls[] = new LocalDecl[rank];
            // syn.create the loop nest (from the inside out)
            for (int r=rank-1; 0<=r; r--) {
                
                // syn.create new names for the r-th iterate and limits
                Name varName  = null == formalVars || formalVars.isEmpty() ? Name.makeFresh(prefix) 
                                                                           : Name.makeFresh(formalVars.get(r).name().id());
                Name minName  = Name.makeFresh(varName+ "min");
                Name maxName  = Name.makeFresh(varName+ "max");
                
                // create an AST node for the calls to domain.min() and domain.max()
                Expr minVal;
                Expr maxVal;
                if (simpleArrayOpt) {
                    minVal = syn.createLongLit(0);
                    if (rank == 1) {
                        maxVal = syn.createFieldRef(domain.position(), syn.createLocal(domain.position(), domLDecl), SIZE);
                    } else {
                        maxVal = syn.createFieldRef(domain.position(), syn.createLocal(domain.position(), domLDecl), Name.make("numElems_"+(r+1)));                       
                    }
                } else {
                    minVal = syn.createInstanceCall(pos, syn.createLocal(domain.position(), domLDecl), MIN, context, syn.createLongLit(r));
                    maxVal = syn.createInstanceCall(pos, syn.createLocal(domain.position(), domLDecl), MAX, context, syn.createLongLit(r));
                }
                
                // create an AST node for the declaration of the temporary locations for the r-th var, min, and max
                LocalDecl minLDecl = syn.createLocalDecl(pos, Flags.FINAL, minName, minVal);
                LocalDecl maxLDecl = syn.createLocalDecl(pos, Flags.FINAL, maxName, maxVal);
                LocalDecl varLDecl = syn.createLocalDecl(pos, Flags.NONE, varName, xts.Long(), syn.createLocal(pos, minLDecl));
                
                varLDecls[r] = varLDecl;
                
                // add the declarations for the r-th min and max to the list of statements to be executed before the loop nest
                stmts.add(minLDecl);
                stmts.add(maxLDecl);
                
                // create expressions for the second and third positions in the r-th for clause
                Expr cond = syn.createBinary(domain.position(), syn.createLocal(pos, varLDecl),
                                             compareOp, syn.createLocal(pos, maxLDecl), this);
                Expr update = syn.createAssign(domain.position(), syn.createLocal(pos, varLDecl),
                                               Assign.ADD_ASSIGN, syn.createLongLit(1), this);
                
                List<Stmt> bodyStmts = new ArrayList<Stmt>(); 
                
                if (null != formalVars && !formalVars.isEmpty()) {
                    bodyStmts.add(syn.createLocalDecl((X10Formal) formalVars.get(r), syn.createLocal(pos, varLDecl)));
                }
                
                // concoct declaration for formal, in case it might be referenced in the body
                if (named) {
                    // set the r-th slot int the index rail to the current value of the r-th iterate
                    Expr setExpr = syn.createInstanceCall( pos, 
                                                           syn.createLocal(pos, indexLDecl), 
                                                           SET, 
                                                           context,
                                                           syn.createLongLit(r),
                                                           syn.createLocal(pos, varLDecl) );
                    bodyStmts.addAll(syn.convertToStmtList(setExpr));
                    if (r+1 == rank) { // the innermost loop
                        // declare the formal variable as a local and initialize it to the index rail
                        Expr formExpr = syn.createStaticCall(pos, formal.declType(), MAKE, syn.createLocal(pos, indexLDecl));
                        bodyStmts.add(syn.createLocalDecl(formal, formExpr));
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
            if (false) {
                // 3/11/2011.  Dave G.  Disabled because transformation doesn't properly update closure environment.
                body = explodePoint(formal, indexLDecl, varLDecls, body);
            }
            if (label() != null) {
                body = syn.createLabeledStmt(pos, label(), body);
            }
            stmts.add(body);
            Block result = syn.createBlock(pos, stmts);
            if (VERBOSE) result.dump(System.out);
            return result;
        }
        
        // for (d in aRail) BODY ===> t = aRail; max = t.size; for (long idx = 0; idx < max; idx += 1) { d = t(i); BODY }
        if (xts.isRail(domainType)) {
            Name varName  = Name.makeFresh("idx");

            LocalDecl domLDecl   = syn.createLocalDecl(domain.position(), Flags.FINAL, Name.makeFresh("rail"), domain);
            Expr size = syn.createFieldRef(pos, syn.createLocal(pos, domLDecl), SIZE);
            LocalDecl sizeLDecl  = syn.createLocalDecl(domain.position(), Flags.FINAL, Name.makeFresh("size"), size);
            
            LocalDecl varLDecl = syn.createLocalDecl(pos, Flags.NONE, varName, xts.Long(), syn.createLongLit(0));
            Expr cond = syn.createBinary(domain.position(), syn.createLocal(pos, varLDecl), Binary.LT, syn.createLocal(pos, sizeLDecl), this);
            Expr update = syn.createAssign(domain.position(), syn.createLocal(pos, varLDecl), Assign.ADD_ASSIGN, syn.createLongLit(1), this);

            Expr valueExpr = syn.createInstanceCall(pos, syn.createLocal(domain.position(), domLDecl), APPLY, context, syn.createLocal(pos, varLDecl));
            LocalDecl formalLDecl = syn.createLocalDecl(formal, valueExpr);

            List<Stmt> bodyStmts = new ArrayList<Stmt>();           
            bodyStmts.add(formalLDecl);
            bodyStmts.add(body);
            body = syn.createBlock(loop.body().position(), bodyStmts);
            For forLoop = syn.createStandardFor(pos, varLDecl, cond, update, body);
            Stmt newLoop = forLoop;
            if (label() != null) {
                newLoop = syn.createLabeledStmt(pos, label(), forLoop);
            }
            return syn.createBlock(pos, domLDecl, sizeLDecl , newLoop);
        }

        assert Types.getIterableIndex(domainType, context).size()>=1; // When Iterable was covariant:  (xts.isSubtype(domainType, xts.Iterable(xts.Any()), context)); 
        Name iterName        = named ? Name.makeFresh(formal.name().id()) : Name.makeFresh();
        Expr iterInit        = syn.createInstanceCall(pos, domain, ITERATOR, context);
        LocalDecl iterLDecl  = syn.createLocalDecl(pos, Flags.FINAL, iterName, iterInit);
        Expr hasExpr         = syn.createInstanceCall(pos, syn.createLocal(pos, iterLDecl), HASNEXT, context);
        Expr nextExpr        = syn.createInstanceCall(pos, syn.createLocal(pos, iterLDecl), NEXT, context);
        if (!xts.typeEquals(nextExpr.type(), formal.declType(), context)) {
            Expr newNextExpr = syn.createCoercion(pos, nextExpr, formal.declType(), this);
            if (null == newNextExpr)
                throw new InternalCompilerError("Unable to cast "+nextExpr+" to the iterated type "+formal.declType(), pos);
            nextExpr = newNextExpr;
        }
        List<Stmt> bodyStmts = new ArrayList<Stmt>();
        LocalDecl varLDecl   = syn.createLocalDecl(formal, nextExpr);
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
     * Replace calls to the apply method on point with corresponding calls to the corresponding method on rail throughout the body.
     * 
     * 3/10/2011.  Dave G.  This transformation is not complete, therefore disabled.
     *                      The issue is that if the call is within a closure, then the captured environment
     *                      information for the closure (and all lexcially enclosing closures up to the for loop)
     *                      must be updated to reflect the additional variables being captured.
     * 
     * @param point a Point formal variable
     * @param rail the underlying Rail defining point
     * @param body the AST containing the usses of point to be replaced 
     * @return a copy of body with every call to point.apply() replaced by a call to rail.apply()
     */
    private Stmt explodePoint(final X10Formal point, final LocalDecl rail, final LocalDecl[] indices, final Stmt body) {
        assert false : "This transformation is not enabled because it is incomplete";
        ContextVisitor pointExploder = new ContextVisitor(job, xts, nodeFactory()) {
            /* (non-Javadoc)
             * @see polyglot.visit.ErrorHandlingVisitor#leaveCall(polyglot.ast.Node)
             */
            @Override
            protected Node leaveCall(Node n) {
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
                                int i = ((IntegralValue) arg.constantValue()).intValue();
                                return syn.createLocal(n.position(), indices[i]);
                            }
                            call = call.target(syn.createLocal(target.position(), rail));
                            call = call.methodInstance(syn.createMethodInstance( rail.type().type(),
                                                                                 ClosureCall.APPLY,
                                                                                 context, 
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
        return ConstantValue.toJavaObject(ConstantPropagator.constantValue(propertyExpr));
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
        return Types.addSelfBinding(type, value);
    }

}
