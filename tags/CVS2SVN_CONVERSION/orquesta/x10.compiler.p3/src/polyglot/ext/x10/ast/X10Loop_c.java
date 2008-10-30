/*
 *
 * (C) Copyright IBM Corporation 2006
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
import polyglot.ext.x10.types.X10MethodInstance;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.SemanticException;
import polyglot.types.Name;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;

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
	
	Formal setClauses(Formal formal, Expr domain, X10NodeFactory nf) {
	    if (formal instanceof X10Formal) {
	        X10Formal xf = (X10Formal) formal;
	        if (domain instanceof RectRegionMaker) {
	            List<Formal> vars = new ArrayList<Formal>();
	            RectRegionMaker tuple = (RectRegionMaker) domain;
	            if (tuple.arguments().size() == xf.vars().size()) {
	                for (int i = 0; i < xf.vars().size(); i++) {
	                    Formal f = xf.vars().get(i);
	                    Formal f2 = setClauses(f, tuple.arguments().get(i), nf);
	                    vars.add(f2);
	                }
	                return xf.vars(vars);
	            }
	        }
	    }
	    
	    X10Type domainType = (X10Type) domain.type();
	    X10TypeSystem ts = (X10TypeSystem) domainType.typeSystem();
	    
	    if (formal.type().type().isInt() && domain instanceof RegionMaker) {
	        List<Expr> args = ((RegionMaker) domain).arguments();
	        if (args.size() == 2) {
	            Expr lo = args.get(0);
	            Expr hi = args.get(1);
	            if (lo.type().isIntOrLess() && hi.type().isIntOrLess()) {
	                X10Type t = (X10Type) formal.type().type();
					XConstraint c = X10TypeMixin.xclause(t);
	                if (c == null) c = new XConstraint_c();
	                else c = c.copy();

	                Expr lbound = nf.Binary(lo.position(), nf.Self(lo.position()).type(ts.Int()), Binary.GE, lo).type(ts.Boolean());
	                Expr ubound = nf.Binary(hi.position(), nf.Self(hi.position()).type(ts.Int()), Binary.LE, hi).type(ts.Boolean());
	                
	                try {
	                    XConstraint lc = ts.xtypeTranslator().constraint(Collections.EMPTY_LIST, lbound);
	                    c.addIn(lc);

	                    XConstraint uc = ts.xtypeTranslator().constraint(Collections.EMPTY_LIST, ubound);
	                    c.addIn(uc);
	                }
	                catch (SemanticException e) {
	                }
	                catch (XFailure e) {
	                }
	                
	                Type newType = X10TypeMixin.xclause(t, c);
	                formal.localDef().setType(Types.ref(newType));
	                return formal.type(nf.CanonicalTypeNode(formal.type().position(), Types.ref(newType)));
	            }
	        }
	    }

	    return formal;
	}
	
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
	    
	    if (ts.isPoint(formal.type().type())) {
	        X10Type point = (X10Type) formal.type().type();
	        formal = setClauses(formal, domain, nf);
	    }
	    
	    return tc.visitEdgeNoOverride(parent, this.domain(domain).formal(formal));
	}

	/** Type check the statement. */
	public Node typeCheck(ContextVisitor tc) throws SemanticException {
                NodeFactory nf = tc.nodeFactory();
		X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
		X10Type domainType = (X10Type) domain.type();
		
		Type formalType = formal.declType();
		
                    // Check if there is a method with the appropriate name and type with the left operand as receiver.   
                    try {
                        X10MethodInstance mi = ts.findMethod(domainType, ts.MethodMatcher(domainType, Name.make("iterator"), Collections.singletonList(formalType)), tc.context().currentClassDef());
                        return this;
                    }
                    catch (SemanticException e) {
                        // Cannot find the method.  Fall through.
                }

                return this;
//                Expr newDomain = domain;
//		if (ts.isX10Array(domainType))
//			newDomain = (Expr) nf.Field(position(), domain, nf.Id(position(), "distribution")).del().typeCheck(tc);
//		return domain(newDomain);
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

