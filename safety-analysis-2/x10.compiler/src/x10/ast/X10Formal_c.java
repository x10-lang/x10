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

package x10.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.FlagsNode;
import polyglot.ast.Formal;
import polyglot.ast.Formal_c;
import polyglot.ast.Id;
import polyglot.ast.Id_c;
import polyglot.ast.IntLit;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.types.ClassDef;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.Name;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.UnknownType;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeCheckPreparer;
import polyglot.visit.TypeChecker;
import x10.extension.X10Del;
import x10.extension.X10Ext;
import x10.types.FunctionType;
import x10.types.X10Context;
import x10.types.X10LocalDef;
import x10.types.X10LocalInstance;
import x10.types.X10MethodInstance;

import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.XConstrainedTerm;
import x10.visit.X10PrettyPrinterVisitor;
import x10.visit.X10Translator;

/**
 * An immutable representation of an X10Formal, which is of the form
 *   Flag Type VarDeclaratorId
 * Recall that a VarDeclaratorId may have additional variable bindings.
 * @author vj Jan 23, 2005
 * @author igor Jan 13, 2006
 */
public class X10Formal_c extends Formal_c implements X10Formal {
	/* Invariant: vars != null */
	protected List<Formal> vars;
	boolean unnamed;

	public X10Formal_c(Position pos, FlagsNode flags, TypeNode type,
	                   Id name, List<Formal> vars, boolean unnamed)
	{
		super(pos, flags, type,
				name == null ? new Id_c(pos, X10PrettyPrinterVisitor.getId()) : name);
		if (vars == null) vars = Collections.EMPTY_LIST;
		this.vars = TypedList.copyAndCheck(vars, Formal.class, true);
		this.unnamed = unnamed;
		
		assert vars != null;
	}
	
	public Node visitChildren(NodeVisitor v) {
	
		X10Formal_c n = (X10Formal_c) super.visitChildren(v);
		List l = visitList(vars, v);
		if (! CollectionUtil.allEqual(l, this.vars)) {
			if (n == this) n = (X10Formal_c) copy();
			n.vars = TypedList.copyAndCheck(l, Formal.class, true);
		}
		return n;
	}
	
	
	public List<Formal> vars() {
		return vars;
	}
	
	public X10Formal vars(List<Formal> vars) {
	    X10Formal_c n = (X10Formal_c) super.copy();
	    n.vars = vars;
	    return n;
	}

	public boolean isUnnamed() {
		return unnamed;
	}

	/** Get the local instances of the bound variables. */
	public LocalDef[] localInstances() {
	    LocalDef[] lis = new LocalDef[vars.size()];
		for (int i = 0; i < vars.size(); i++) {
			lis[i] = vars.get(i).localDef();
		}
		return lis;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.jl.ast.Formal#addDecls()
	 */
	public void addDecls(Context c) {
		c.addVariable(li.asInstance());
	
		for (Iterator<Formal> j = this.vars().iterator(); j.hasNext(); ) {
			Formal fj = (Formal) j.next();
			fj.addDecls(c);
		}
	}

	 private String translateVars() {
		StringBuffer sb = new StringBuffer();
		if (! vars.isEmpty()) {
			sb.append("[");
			for (int i = 0; i < vars.size(); i++)
				sb.append(i > 0 ? "," : "").append(vars.get(i).name().id());
			sb.append("]");
		}
		return sb.toString();
	}

	 @Override
	 public Node buildTypes(TypeBuilder tb) throws SemanticException {
	     X10Formal_c n = (X10Formal_c) super.buildTypes(tb);
	  
	     
	     X10LocalDef fi = (X10LocalDef) n.localDef();
	     X10Ext  ex = (X10Ext) n.ext();
	     List<AnnotationNode> as = ((X10Del) n.del()).annotations();

    	
	     if (as != null) {
	         List<Ref<? extends Type>> ats = new ArrayList<Ref<? extends Type>>(as.size());
	         for (AnnotationNode an : as) {
	             ats.add(an.annotationType().typeRef());
	         }
	         fi.setDefAnnotations(ats);
	     }
	   
	     return n;
	 }
	 
	 public Node setResolverOverride(final Node parent, TypeCheckPreparer v) {
	     final X10TypeSystem ts = (X10TypeSystem) v.typeSystem();
	     final X10Context context = (X10Context) v.context();
	     final ClassDef currClassDef = context.currentClassDef();
	    

	     Formal f = (Formal) this;
	     X10LocalDef li = (X10LocalDef) f.localDef();
	    
	     if (f.type() instanceof UnknownTypeNode && parent instanceof Formal) {
	    	   // We infer the types of exploded formals
	         final UnknownTypeNode tn = (UnknownTypeNode) f.type();
	         final LazyRef<Type> r = (LazyRef<Type>) tn.typeRef();
	         r.setResolver(new Runnable() {
	             public void run() {
	                 Formal ff = (Formal) parent;
	                 Type containerType = ff.type().type();
	                 Type indexType = null;
	                 
	                 if (ts.isFunctionType(containerType)) {
	                     List<Type> actualTypes = Collections.singletonList(ts.Int());

	                     try {
	                         // Find the most-specific function type.
	                         X10MethodInstance mi = ts.findMethod(containerType, 
	                        		 ts.MethodMatcher(containerType, Name.make("apply"), 
	                        				 Collections.EMPTY_LIST, actualTypes, context));
	                         indexType = mi.returnType();
	                    
	                     }
	                     catch (SemanticException e) {
	                     }
	                 }

	                 if (indexType != null) {
	                     r.update(indexType);
	                     return;
	                 }

	                 r.update(ts.unknownType(tn.position()));
	             }
	         });
	     } 
	     return null;
	 }
	 

	 @Override
	 public Type declType() {
		 return type.type();
	 }
	    
	 @Override
	 public Node typeCheckOverride(Node parent, ContextVisitor tc) throws SemanticException {
		 NodeVisitor childtc = tc.enter(parent, this);

		 XConstrainedTerm  pt = ((X10Context) ((ContextVisitor) childtc).context()).currentPlaceTerm();

		 if (pt != null)
			 ((X10LocalDef) localDef()).setPlaceTerm(pt.term());
		 return null;
	 }
	 @Override
	public Node typeCheck(ContextVisitor tc) throws SemanticException {
	     X10Formal_c n = (X10Formal_c) super.typeCheck(tc);
	     if (n.type() instanceof UnknownTypeNode || n.type().type() instanceof UnknownType) {
	         throw new SemanticException("Could not infer type for formal parameter " + n + ".", position());
	     }
	     if (n.type().type().isVoid())
	         throw new SemanticException("Formal parameter cannot have type " + this.type().type() + ".", position());
	     return n;
	}

    public String toString() {
	StringBuffer sb = new StringBuffer();
	sb.append(flags.flags().clearFinal().translate());
	if (flags.flags().isFinal())
	    sb.append("val ");
	else
	    sb.append("var ");
	sb.append(name);
	if (! vars.isEmpty()) {
		sb.append("(");
		for (int i = 0; i < vars.size(); i++)
			sb.append(i > 0 ? "," : "").append(vars.get(i));
		sb.append(")");
	}
	sb.append(": ");
	sb.append(type);
	return sb.toString();
    }

	/* (non-Javadoc)
	 * @see x10.ast.X10Formal#hasExplodedVars()
	 */
	public boolean hasExplodedVars() {
		return ! vars.isEmpty();
	}

	/**
	 * Create a local variable declaration for an exploded var,
	 * at the given type, name and with the given initializer.
	 * The exploded variable is implicitly final.
	 *
	 * @param nf
	 * @param pos
	 * @param type
	 * @param name
	 * @param li
	 * @param init
	 * @return
	 */
	protected static LocalDecl makeLocalDecl(NodeFactory nf, Position pos,
		FlagsNode flags, TypeNode type,
											 Id name, LocalDef li,
											 Expr init)
	{
		/* boolean allCapitals = name.equals(name.id().toUpperCase());
		// vj: disable until we have more support for declarative programming in X10.
		Flags f = (false || allCapitals ? flags.set(Flags.FINAL) : flags);
		 */
		return nf.LocalDecl(pos, flags.flags(flags.flags().set(Flags.FINAL)), type, name, init)
					.localDef(li);
	}

	/**
	 * Return the initialization statements for the exploding variables.
	 *
	 * @param nf
	 * @param ts
	 * @return
	 * @throws SemanticException 
	 */
	public List<Stmt> explode(ContextVisitor tc) throws SemanticException {
		return explode(tc,  name(), position(), flags(), vars, localDef());
	}

	/* (non-Javadoc)
	 * @see x10.ast.X10Formal#explode(polyglot.ast.NodeFactory, polyglot.types.TypeSystem)
	 */
	public List<Stmt> explode(ContextVisitor tc, Stmt s) throws SemanticException {
		List<Stmt> init = this.explode(tc);
		if (s != null)
			init.add(s);
		return init;
	}

	
	public List<Stmt> explode(ContextVisitor tc, List<Stmt> s, boolean prepend) throws SemanticException {
		List<Stmt> init = this.explode(tc);
		if (s != null) {
			if (prepend) init.addAll(s);
			else init.addAll(0, s);
		}
		return init;
	}

	/**
	 * Return the initialization statements for the exploding variables.
	 *
	 * @param nf
	 * @param ts
	 * @param name
	 * @param pos
	 * @param flags
	 * @param vars
	 * @param lis
	 * @return
	 */
	private static List<Stmt> explode(ContextVisitor tc, Id name, Position pos, FlagsNode flags, List<Formal> vars, LocalDef bli) throws SemanticException
	{
	    TypeSystem ts = tc.typeSystem();
	    NodeFactory nf = tc.nodeFactory();
		if (vars == null || vars.isEmpty()) return null;
		X10NodeFactory x10nf = (X10NodeFactory) nf;
		List<Stmt> stmts = new TypedList<Stmt>(new ArrayList<Stmt>(vars.size()), Stmt.class, false);
		Local arrayBase =nf.Local(pos, name);
		if (bli != null)
		    arrayBase = (Local) arrayBase.localInstance(bli.asInstance()).type(bli.asInstance().type());
		for (int i = 0; i < vars.size(); i++) {
			// int arglist(i) = name[i];
			Formal var = vars.get(i);
			Expr index = x10nf.IntLit(var.position(), IntLit.INT, i).type(ts.Int());
			Expr init = x10nf.ClosureCall(var.position(), arrayBase,  Collections.singletonList(index));
			if (bli != null) {
			    init = (Expr) init.disambiguate(tc).typeCheck(tc).checkConstants(tc);
			}
			Stmt d = makeLocalDecl(nf, var.position(), flags, var.type(), var.name(), var.localDef(), init);
			stmts.add(d);
		}
		return stmts;
	}
//	public X10Formal_c pickUpTypeFromTypeNode(TypeChecker tc) {
//		X10LocalInstance xli = (X10LocalInstance) li;
//		X10Type newType = (X10Type) type.type();
//		xli.setType(newType);
//		xli.setSelfClauseIfFinal();
//		return  (X10Formal_c) type(type().type(xli.type()));
//	}
	
	public Context enterChildScope(Node child, Context c) {
		X10Context cxt = (X10Context) c;
		if (child == this.type) {
			TypeSystem ts = c.typeSystem();
			LocalDef li = localDef();
			cxt = (X10Context) cxt.copy();
			cxt.addVariable(li.asInstance());
			cxt.setVarWhoseTypeIsBeingElaborated(li);
		}
		Context cc = super.enterChildScope(child, c);
		return cc;
	}
	/**
	 * Return the initialization statements for the exploding variables
	 * early.
	 *
	 * @param nf
	 * @param ts
	 * @param name
	 * @param pos
	 * @param flags
	 * @param vars
	 * @return
	 * @throws SemanticException 
	 */
	public static List/*<Stmt>*/ explode(ContextVisitor tc,
										 Id name, Position pos,
										 FlagsNode flags, List<Formal> vars) throws SemanticException
	{
		return explode(tc, name, pos, flags, vars, null);
	}
	
}

