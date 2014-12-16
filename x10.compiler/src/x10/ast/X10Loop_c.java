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

package x10.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import polyglot.ast.Binary;
import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Loop;
import polyglot.ast.Loop_c;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.types.ClassDef;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.LocalDef;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.util.ErrorInfo;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeCheckPreparer;
import polyglot.visit.TypeChecker;
import x10.constraint.XFailure;
import x10.constraint.XVar;
import x10.constraint.XTerm;
import x10.types.constraints.ConstraintManager;
import x10.constraint.XVar;
import x10.errors.Errors;
import x10.types.X10ClassType;
import polyglot.types.Context;
import x10.types.ConstrainedType;
import x10.types.X10FieldInstance;
import x10.types.X10LocalDef;
import x10.types.MethodInstance;

import x10.types.X10TypeEnv;
import x10.types.X10TypeEnv_c;
import x10.types.X10ParsedClassType_c;
import polyglot.types.TypeSystem;
import polyglot.types.LazyRef_c;
import x10.types.checker.Converter;
import x10.types.constraints.CConstraint;
import x10.types.constraints.xnative.CNativeLit;

import x10.types.matcher.Subst;
import x10.util.Synthesizer;
import x10.visit.X10TypeChecker;

/**
 * Captures the commonality of for, foreach and ateach loops in X10.
 * TODO:
 * (1) formal must be a variable whose type will be that of the region underlying domain.
 * (2) domain must be an array, distribution or region. If it is an array or distribution a,
 *     the system must behave as if the user typed a.region.
 * (3) Perhaps we can allow continue statements within a for loop, but not within
 *     a foreach or an ateach.
 *
 * COMMENTS / TODO (added by Christian)
 * (1) for now, continue/break should work as usual
 *      for foreach;
 * (2) ateach is broken in many respects, including break/continue,
 *      see comments in ateach.xcd
 * (3) this AST node does not seem to support the 'correct' syntax for
 *      multi-dimensional arrays (ateach(i,j:D) { S }).  But that's
 *      probably ok, for now the XCD files expect to see
 *      ateach(i:D) { S } and type 'i' as 'int[]' for multi-dimensional
 *      arrays, and as 'int' for single-dimensional arrays.
 *
 * @author vj Dec 9, 2004
 */
public abstract class X10Loop_c extends Loop_c implements X10Loop {
	protected Formal formal;
	protected Expr domain;
	protected Stmt body;
	protected List<Stmt> locals;

	protected LoopKind loopKind;

	/**
	 * @param pos
	 */
	protected X10Loop_c(Position pos) {
		super(pos);
	}

	protected X10Loop_c(Position pos, Formal formal, Expr domain, Stmt body) {
		super(pos);
		this.formal = formal;
		this.domain = domain;
		this.body = body;
	}

	/** Reconstruct the expression. */
	protected X10Loop_c reconstruct(Formal formal, Expr domain, Stmt body) {
		if (formal != this.formal || domain != this.domain || body != this.body) {
			X10Loop_c n = (X10Loop_c) copy();
			n.formal = formal;
			n.domain = domain;
			n.body = body;
			return n;
		}
		return this;
	}
	

	
	public Node typeCheckOverride(Node parent, ContextVisitor tc) {
	    TypeChecker tc1 = (TypeChecker) tc.enter(parent, this);
	    
	    Expr domain = (Expr) this.visitChild(this.domain, tc1);

        domainTypeRef.update( domain.type() );
        Formal f = formal;
        if (formal.type() instanceof UnknownTypeNode) {
            resolveIndexType(tc);
            final NodeFactory nf = tc.nodeFactory();
            final TypeNode tn = this.formal.type();
            f = f.type(nf.CanonicalTypeNode(tn.position(), tn.typeRef()));
        }
        f = (Formal) this.visitChild(f, tc1);
	    
	    return tc.visitEdgeNoOverride(parent, this.domain(domain).formal(f));
	}

    private Type getIndexType(Type domainType, ContextVisitor tc) {
    	/*
        final TypeSystem ts = tc.typeSystem();
        Type base = Types.baseType(domainType);

        if (ts.isUnknown(base)) {
            return ts.unknownType(base.position());
        }
        if (base instanceof X10ClassType) {
            if (((X10ClassType) base).error() != null) {
                return ts.unknownType(base.position());
            }
            if (ts.hasSameClassDef(base, ts.Iterable())) {
                return Types.getParameterType(base, 0);
            }
            if (ts.typeIsJLIterable(base)) {
                return ts.Any();
            }
            else {
                Type sup = ts.superClass(domainType);
                // sup needs to be translated, can contain 'this'
                if (sup != null) {
                    Type t = getIndexType(sup, tc);
                    if (t != null) return t;
                }
                for (Type ti : ts.interfaces(domainType)) {
                    Type t = getIndexType(ti, tc);
                    // t needs to be translated, can contain 'this'
                    if (t != null) {
                        return t;
                    }
                }
            }
        }
        return null;
        */
    	Set<Type> types = Types.getIterableIndex(domainType, tc.context());
    	for (Type t : types) return t;
    	return null;
    }

    private void resolveIndexType(ContextVisitor tc) {
        final LazyRef<Type> r = (LazyRef<Type>) formal.type().typeRef();
        final NodeFactory nf = tc.nodeFactory();
        final TypeSystem ts = tc.typeSystem();

        Type indexType;
        int length = ((X10Formal) formal).vars().size();
        if (length > 0) {
            indexType = ts.Point(); // todo: not true, it can also be an Array, e.g., for (x[i,j] in [[1,2],[3,4]])

            // Add a self.rank=n clause, if the formal
            // has n components.
            XVar self = Types.xclause(indexType).self();
            Synthesizer synth = new Synthesizer(nf, ts);
            XTerm v = synth.makePointRankTerm((XVar) self);
            XTerm rank = ConstraintManager.getConstraintSystem().makeLit((long)length, ts.Long());
            indexType = Types.addBinding(indexType, v, rank);
            r.update(indexType);
            return;
        }

        Type domainType = domainTypeRef.get();
        indexType = getIndexType(domainType, tc);
        if (indexType == null) {
            r.update(ts.unknownType(position()));
            return;
        }
        Type base = Types.baseType(domainType);


        XVar selfValue = Types.selfVarBinding(domainType);
        boolean generated = false;
        if (selfValue == null) {
            selfValue = ConstraintManager.getConstraintSystem().makeUQV();
            generated = true;
        }
        XVar thisVar = base instanceof X10ClassType ?
                ((X10ClassType) base).x10Def().thisVar() :null;

        // Now the problem is that indexType may
        // have a non-null thisVar (e.g. Foo#this). We need to
        // replace thisVar with var.
        if (thisVar != null) {
            try  {

                indexType = Subst.subst(indexType, selfValue, thisVar);
                if (generated) {
                    CConstraint c = Types.xclause(domainType);
                    c=c.instantiateSelf(selfValue);
                    indexType = Types.addConstraint(indexType, c);
                    assert Types.consistent(indexType);
                    indexType = Subst.subst(indexType, ConstraintManager.getConstraintSystem().makeEQV(), selfValue);
                }
            } catch (SemanticException z) {

            }
        }
        r.update(indexType);
    }

	/** Type check the statement. */
	public Node typeCheck(ContextVisitor tc) {
		TypeSystem ts =  tc.typeSystem();
		Type domainType = domainTypeRef.get();
		if (domainType == null ) {
			// aha, in this case the type inferencer did not run, since an explicit type was given.
			domainType = domain.type();
		}
		ConstrainedType formalType = Types.toConstrainedType(formal.declType());
		assert domainType != null : "formal=" + formal + " domain = " + domain + " position = " + position();
        final Context context = tc.context();

        HashSet<Type> iterableIndex = Types.getIterableIndex(domainType, context);
        boolean newRes = false;
        for (Type tt : iterableIndex) {
            newRes |= ts.isSubtype(tt, formalType, context);
        }
        //assert newRes==ts.isSubtype(domainType, ts.Iterable(formalType), tc.context()); // when Iterable was covariant (i.e., Iterable[+T])
		if (newRes) {
		//	if (X10TypeMixin.areConsistent(formalType, domainType)
		    return this;
		}

//		// Check if there is a method with the appropriate name and type with the left 
		// operand as receiver.   
//		X10MethodInstance mi = ts.findMethod(domainType, ts.MethodMatcher(domainType, Name.make("iterator"), Collections.EMPTY_LIST), tc.context().currentClassDef());
//		Type rt = mi.returnType();
//		if (! mi.flags().isStatic() && ts.isSubtype(rt, Iterator))
//		    return this;

		if (ts.isSubtype(formalType, ts.Point(), context)) {
		    ConstrainedType Region = Types.toConstrainedType(ts.Region());
            final XTerm rankTerm = formalType.rank(context);
            if (rankTerm!=null) {
                Region = Region.addRank(rankTerm);
                Expr newDomain = Converter.attemptCoercion(tc, domain, Region);
                if (newDomain != null && newDomain != domain) {
                    domainTypeRef = Types.lazyRef(null);
                    Node nn = this.domain(newDomain).del().typeCheck(tc);
                    return nn;
                }
                ConstrainedType Dist = Types.toConstrainedType(ts.Dist());
                Dist = Dist.addRank(rankTerm);
                newDomain = Converter.attemptCoercion(tc, domain, Dist);
                if (newDomain != null && newDomain != domain) {
                    domainTypeRef = Types.lazyRef(null);
                    return this.domain(newDomain).del().typeCheck(tc);
                }
            }
		    Errors.issue(tc.job(), new SemanticException("The loop iterator is a Point whose rank is not the same as the rank of the loop domain.", position()));
		} else {
		// The expected type is Iterable[Foo].  The constraints on domainType do matter
		// for this failure, so don't strip them.
		Errors.issue(tc.job(),
		        new Errors.LoopDomainIsNotOfExpectedType(formalType, domainType, iterableIndex, position()));
        }
		return this;
	}

	/* (non-Javadoc)
	 * @see polyglot.ast.Term#entry()
	 */
	public Term firstChild() {
		return formal;
	}

	/* (non-Javadoc)
	 * @see polyglot.ast.Term#acceptCFG(polyglot.visit.CFGBuilder, java.util.List)
	 */
	@Override
	public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
		v.visitCFG(formal, domain, ENTRY);
		v.visitCFG(domain, body, ENTRY);
		v.visitCFG(body, this, EXIT);
		return succs;
	}

	@Override
	public Context enterScope(Context c) {
		return c.pushBlock();
	}

	/** Visit the children of the expression. */
	@Override
	public Node visitChildren(NodeVisitor v) {
		Formal formal = (Formal) visitChild(this.formal, v);
		Expr domain = (Expr) visitChild(this.domain, v);
		Stmt body = (Stmt) visitChild(this.body, v);
		return reconstruct(formal, domain, body);
	}

	/* (non-Javadoc)
	 * @see x10.ast.X10Loop#body()
	 */
	public Stmt body() {
		return this.body;
	}

	/* (non-Javadoc)
	 * @see x10.ast.X10Loop#formal()
	 */
	public Formal formal() {
		return this.formal;
	}

	/* (non-Javadoc)
	 * @see x10.ast.X10Loop#domain()
	 */
	public Expr domain() {
		return this.domain;
	}

	/* (non-Javadoc)
	 * @see x10.ast.X10Loop#locals()
	 */
	public List<Stmt> locals() {
		return this.locals == null ? Collections.<Stmt>emptyList() : this.locals;
	}

	/* (non-Javadoc)
	 * @see x10.ast.X10Loop#body(polyglot.ast.Stmt)
	 */
	public X10Loop body(Stmt body) {
		X10Loop_c n = (X10Loop_c) copy();
		n.body = body;
		return n;
	}

	/* (non-Javadoc)
	 * @see x10.ast.X10Loop#formal(polyglot.ast.Formal)
	 */
	public X10Loop formal(Formal formal) {
		X10Loop_c n = (X10Loop_c) copy();
		n.formal = formal;
		return n;
	}

	/* (non-Javadoc)
	 * @see x10.ast.X10Loop#domain(polyglot.ast.Expr)
	 */
	public X10Loop domain(Expr domain) {
		X10Loop_c n = (X10Loop_c) copy();
		n.domain = domain;
		return n;
	}

	/* (non-Javadoc)
	 * @see x10.ast.X10Loop#locals(java.util.List)
	 */
	public X10Loop locals(List<Stmt> locals) {
		X10Loop_c n = (X10Loop_c) copy();
		n.locals = locals;
		return n;
	}

	public Term continueTarget() {
		return formal;
	}

	public Expr cond() { return null; }
	
	/*
	 * Type inference works as follows.
	 * 
	 * We first seek to infer the type of the index expression i in the for loop for (i in D) S
	 * (if this type is not specified explicitly).
	 * 
	 * There are two source of information about index type. First, the index expression itself.
	 * If it is of the form (k1,..., kn) we should infer that the type is Point(n). 
	 * 
	 * Second, we look to find if the type DT of the domain expression D is of the form Iterable[T].
	 * In this case, the inferred type of i is T. 
	 * 
	 * The subtlety here is that T may have a reference to Foo@this; this needs to be replaced with
	 * a logical variable corresponding to D. However, D may not have a self var binding. So we have
	 * to create such a variable. 
	 * 
	 * Examples that should work:
	 * 	
	def g(g:Dist(1)): Dist(1) = g;
	def m(gridDist:Dist(1)) {
		for ((i) in g(gridDist) ) ;
		for ((i) in 0..9) ;
		for ((i) in gridDist | here);
		//for ((i,j) in 0..9) ;
	}
	 * 
	 */
	
	LazyRef<Type> domainTypeRef = Types.lazyRef(null);
	public Type domainType() {
		Type domainType = domainTypeRef.get();
		if (domainType == null ) {
			// aha, in this case the type inferencer did not run, since an explicit type was givem.
			domainType = domain.type();
		}
		return domainType;
	}

	@Override
	public Node setResolverOverride(final Node parent, final TypeCheckPreparer v) {
		final Expr domain = this.domain;
		final X10Loop loop = this;

		final Formal f = this.formal;
		if (f.type() instanceof UnknownTypeNode) {
			UnknownTypeNode tn = (UnknownTypeNode) f.type();

			NodeVisitor childv = v.enter(parent, loop);
			childv = childv.enter(loop, domain);

			if (childv instanceof TypeCheckPreparer) {

				// Create a ref and goal for computing the domain type.
				final LazyRef<Type> domainTypeRef = this.domainTypeRef;
				domainTypeRef.setResolver(LazyRef_c.THROW_RESOLVER);

				final LazyRef<Type> r = (LazyRef<Type>) tn.typeRef();
				// Now, infer index Type.
				r.setResolver(LazyRef_c.THROW_RESOLVER);
			}
		}

		return super.setResolverOverride(parent, v);
	}

	public Node buildTypes(TypeBuilder tb) {
		X10Loop n = (X10Loop) super.buildTypes(tb);
		
		// Set the final flag on all formals introduced in the loop.
		Formal f = (Formal) n.formal().visit(new NodeVisitor() {
			@Override
			public Node leave(Node old, Node n, NodeVisitor v) {
				if (n instanceof Formal) {
					Formal f = (Formal) n;
					LocalDef li = f.localDef();
					Flags flags = f.flags().flags();
					flags = flags.Final();
					li.setFlags(flags);
					return f.flags(f.flags().flags(flags)).localDef(li);
				}
				return n;
			}
		});
		return n.formal(f);
	}
}

