/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Dec 9, 2004
 *
 *
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Binary;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Loop;
import polyglot.ast.Loop_c;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.ext.x10.types.Subst;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10LocalDef;
import polyglot.ext.x10.types.X10MethodInstance;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
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
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeCheckPreparer;
import polyglot.visit.TypeChecker;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XRoot;
import x10.constraint.XTerm;

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
public abstract class X10Loop_c extends Loop_c implements X10Loop, Loop {
	protected Formal formal;
	protected Expr domain;
	protected Stmt body;
	protected List locals;

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
	
//	Formal setClauses(Formal formal, Expr domain, X10NodeFactory nf) {
//	    X10Type domainType = (X10Type) domain.type();
//	    X10TypeSystem ts = (X10TypeSystem) domainType.typeSystem();
//	    
//	    if (formal.type().type().isInt() && domain instanceof RegionMaker) {
//	        List<Expr> args = ((RegionMaker) domain).arguments();
//	        if (args.size() == 2) {
//	            Expr lo = args.get(0);
//	            Expr hi = args.get(1);
//	            if (lo.type().isIntOrLess() && hi.type().isIntOrLess()) {
//	                X10Type t = (X10Type) formal.type().type();
//					XConstraint c = X10TypeMixin.xclause(t);
//	                if (c == null) c = new XConstraint_c();
//	                else c = c.copy();
//
//	                Expr lbound = nf.Binary(lo.position(), nf.Self(lo.position()).type(ts.Int()), Binary.GE, lo).type(ts.Boolean());
//	                Expr ubound = nf.Binary(hi.position(), nf.Self(hi.position()).type(ts.Int()), Binary.LE, hi).type(ts.Boolean());
//	                
//	                try {
//	                    XConstraint lc = ts.xtypeTranslator().constraint(Collections.EMPTY_LIST, lbound, thisVar);
//	                    c.addIn(lc);
//
//	                    XConstraint uc = ts.xtypeTranslator().constraint(Collections.EMPTY_LIST, ubound, thisVar);
//	                    c.addIn(uc);
//	                }
//	                catch (SemanticException e) {
//	                }
//	                catch (XFailure e) {
//	                }
//	                
//	                Type newType = X10TypeMixin.xclause(X10TypeMixin.baseType(t), c);
//	                formal.localDef().setType(Types.ref(newType));
//	                return formal.type(nf.CanonicalTypeNode(formal.type().position(), Types.ref(newType)));
//	            }
//	        }
//	    }
//
//	    return formal;
//	}
	
	public Node typeCheckOverride(Node parent, ContextVisitor tc) throws SemanticException {
	    TypeChecker tc1 = (TypeChecker) tc.enter(parent, this);
	    
	    Expr domain = (Expr) this.visitChild(this.domain, tc1);
	    if (domain.type() instanceof UnknownType) {
	        throw new SemanticException();
	    }
	    X10Type domainType = (X10Type) domain.type();
	    
	    Formal formal = (Formal) this.visitChild(this.formal, tc1);
	    
	    X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
	    X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
	    
//	    if (ts.isPoint(formal.type().type())) {
//	        X10Type point = (X10Type) formal.type().type();
//	        formal = setClauses(formal, domain, nf);
//	    }
	    
	    return tc.visitEdgeNoOverride(parent, this.domain(domain).formal(formal));
	}

	/** Type check the statement. */
	public Node typeCheck(ContextVisitor tc) throws SemanticException {
                NodeFactory nf = tc.nodeFactory();
		X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
		X10Type domainType = (X10Type) domain.type();
		
		Type formalType = formal.declType();
		Type Iterable = ts.Iterable(formalType);
		
		if (ts.isSubtypeWithValueInterfaces(domainType, Iterable, tc.context())) {
		    return this;
		}

//		// Check if there is a method with the appropriate name and type with the left operand as receiver.   
//		X10MethodInstance mi = ts.findMethod(domainType, ts.MethodMatcher(domainType, Name.make("iterator"), Collections.EMPTY_LIST), tc.context().currentClassDef());
//		Type rt = mi.returnType();
//		if (! mi.flags().isStatic() && ts.isSubtype(rt, Iterator))
//		    return this;

		if (ts.isSubtype(formalType, ts.Point(), tc.context())) {
		    try {
		        Expr newDomain = X10New_c.attemptCoercion(tc, domain, ts.Region());
		        if (newDomain != domain)
		            return this.domain(newDomain).del().typeCheck(tc);
		    }
		    catch (SemanticException e) {
		    }
		    try {
		        Expr newDomain = X10New_c.attemptCoercion(tc, domain, ts.Dist());
		        if (newDomain != domain)
		            return this.domain(newDomain).del().typeCheck(tc);
		    }
		    catch (SemanticException e) {
		    }
		}
		
		if (true)
		    return this;
		
		try {
		    throw new SemanticException("Loop domain " + domainType + " is not Iterable[" + formalType + "].", position());
		}
		catch (SemanticException e) {
		    tc.errorQueue().enqueue(ErrorInfo.WARNING, "WARNING (should be error, but type-checker is broken): " + e.getMessage(), position());
		    return this;
		}
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
	public List acceptCFG(CFGBuilder v, List succs) {
		v.visitCFG(formal, domain, ENTRY);
		v.visitCFG(domain, body, ENTRY);
		v.visitCFG(body, this, EXIT);
		return succs;
	}

	public Context enterScope(Context c) {
		return c.pushBlock();
	}

	/** Visit the children of the expression. */
	public Node visitChildren(NodeVisitor v) {
		Formal formal = (Formal) visitChild(this.formal, v);
		Expr domain = (Expr) visitChild(this.domain, v);
		Stmt body = (Stmt) visitChild(this.body, v);
		return reconstruct(formal, domain, body);
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10Loop#body()
	 */
	public Stmt body() {
		return this.body;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10Loop#formal()
	 */
	public Formal formal() {
		return this.formal;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10Loop#domain()
	 */
	public Expr domain() {
		return this.domain;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10Loop#locals()
	 */
	public List/*<Stmt>*/ locals() {
		return this.locals == null ? Collections.EMPTY_LIST : this.locals;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10Loop#body(polyglot.ast.Stmt)
	 */
	public X10Loop body(Stmt body) {
		X10Loop_c n = (X10Loop_c) copy();
		n.body = body;
		return n;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10Loop#formal(polyglot.ast.Formal)
	 */
	public X10Loop formal(Formal formal) {
		X10Loop_c n = (X10Loop_c) copy();
		n.formal = formal;
		return n;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10Loop#domain(polyglot.ast.Expr)
	 */
	public X10Loop domain(Expr domain) {
		X10Loop_c n = (X10Loop_c) copy();
		n.domain = domain;
		return n;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10Loop#locals(java.util.List)
	 */
	public X10Loop locals(List/*<Stmt>*/ locals) {
		X10Loop_c n = (X10Loop_c) copy();
		n.locals = locals;
		return n;
	}

	public Term continueTarget() {
		return formal;
	}

	public Expr cond() { return null; }
	
	@Override
	public Node setResolverOverride(final Node parent, final TypeCheckPreparer v) {
	    final Expr domain = this.domain;
	    final X10Loop loop = this;
	    
            Formal f = this.formal;
            X10LocalDef li = (X10LocalDef) f.localDef();
            if (f.type() instanceof UnknownTypeNode) {
                UnknownTypeNode tn = (UnknownTypeNode) f.type();

                NodeVisitor childv = v.enter(parent, loop);
                childv = childv.enter(loop, domain);

                if (childv instanceof TypeCheckPreparer) {
                    TypeCheckPreparer tcp = (TypeCheckPreparer) childv;
                    final LazyRef<Type> r = (LazyRef<Type>) tn.typeRef();
                    TypeChecker tc = new TypeChecker(v.job(), v.typeSystem(), v.nodeFactory(), v.getMemo());
                    tc = (TypeChecker) tc.context(tcp.context().freeze());
                    
                    // Create a ref and goal for computing the domain type.
                    final LazyRef<Type> domainTypeRef = Types.lazyRef(null);
                    domainTypeRef.setResolver(new TypeCheckExprGoal(loop, domain, tc, domainTypeRef));
                    
                    final X10TypeSystem ts = (X10TypeSystem) v.typeSystem();
                    final ClassDef curr = v.context().currentClassDef();
                    
                    // Now, get the index type from the domain.
                    // FIXME: Dist{self==d} index type is Point{rank==this.rank}
                    // should be Point{rank==d.rank}
                    r.setResolver(new Runnable() { 
                        Type getIndexType(Type domainType) {
                            Type base = X10TypeMixin.baseType(domainType);
                            
                            if (base instanceof X10ClassType) {
                                if (ts.hasSameClassDef(base, ts.Iterable())) {
                                    return X10TypeMixin.getParameterType(base, 0);
                                }
                                else {
                                    Type sup = ts.superClass(domainType);
                                    if (sup != null) {
                                        Type t = getIndexType(sup);
                                        if (t != null) return t;
                                    }
                                    for (Type ti : ts.interfaces(domainType)) {
                                        Type t = getIndexType(ti);
                                        if (t != null) {
                                            return t;
                                        }
                                    }
                                }
                            }

//                            // HACK: need to add iterable
//                            try {
//                                X10MethodInstance mi = (X10MethodInstance) ts.findMethod(domainType, ts.MethodMatcher(domainType, Name.make("iterator"), Collections.EMPTY_LIST), curr);
//                                return X10TypeMixin.getParameterType(mi.returnType(), 0);
//                            }
//                            catch (SemanticException e) {
//                            }
                            
                            return null;
                        }

                        public void run() {
                            Type domainType = domainTypeRef.get();
                            Type indexType = getIndexType(domainType);    
                            
                            // subst self for this (from the domain type) in the index type.
                            Type base = X10TypeMixin.baseType(domainType);
                            XConstraint c = X10TypeMixin.xclause(domainType);
                            XTerm selfVar = c != null ? c.bindingForVar(c.self()) : null;
                            XRoot thisVar = base instanceof X10ClassType ? ((X10ClassType) base).x10Def().thisVar() : null;
                            if (thisVar != null && selfVar != null)
                                try {
                                    indexType = Subst.subst(indexType, selfVar, thisVar);
                                }
                                catch (SemanticException e) {
                                }

                            if (indexType != null) {
                                r.update(indexType);
                            }
                        }
                    });
                }
            }
            
//            final X10TypeSystem ts = (X10TypeSystem) v.typeSystem();
//
//	    formal.visitChildren(new NodeVisitor() {
//	        @Override
//	        public Node override(final Node parent, Node n) {
//	            if (n instanceof Formal) {
//	                Formal f = (Formal) n;
//	                X10LocalDef li = (X10LocalDef) f.localDef();
//	                if (f.type() instanceof UnknownTypeNode) {
//	                    final UnknownTypeNode tn = (UnknownTypeNode) f.type();
//	                    final LazyRef<Type> r = (LazyRef<Type>) tn.typeRef();
//	                    r.setResolver(new Runnable() {
//	                        public void run() {
//	                            if (parent instanceof Formal) {
//	                                Formal ff = (Formal) parent;
//	                                Ref<Type> fr = (Ref<Type>) ff.type().typeRef();
//	                                Type ftype = fr.get();
//	                                Type t = X10TypeMixin.getParameterType(ftype, 0);
//	                                r.update(t);
//	                            }
//	                            else {
//	                                r.update(ts.unknownType(tn.position()));
//	                            }
//	                        }
//	                    });
//	                }
//	                return null;
//	            }
//	            return n;
//	        }
//	    });

	    return super.setResolverOverride(parent, v);
	}

	public Node buildTypes(TypeBuilder tb) throws SemanticException {
		X10Loop n = (X10Loop) super.buildTypes(tb);
		
		// Set the final flag on all formals introduced in the loop.
		Formal f = (Formal) n.formal().visit(new NodeVisitor() {
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

