// Licensed Materials - Property of IBM
// (C) Copyright IBM Corporation 2004,2005,2006. All Rights Reserved. 
// Note to U.S. Government Users Restricted Rights:  
// Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp. 
//                                                                             
// --------------------------------------------------------------------------- 

/*
 * Created by vj on Dec 9, 2004
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.ArrayInit;
import polyglot.ast.Block;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassMember;
import polyglot.ast.Expr;
import polyglot.ast.Expr_c;
import polyglot.ast.Formal;
import polyglot.ast.MethodDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.types.ClassDef;
import polyglot.types.Flags;
import polyglot.types.ParsedClassType;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;

/** An immutable representation of an X10 array constructor.
 * @author vj Dec 9, 2004
 * 
 */
public class ArrayConstructor_c 
extends Expr_c 
implements ArrayConstructor {
	
	protected TypeNode base;
	protected boolean isValue = false;
	protected boolean isSafe = true;
	
	/** distribution must contain a value whose type is either int or x10.lang.dist.
	 * If distribution == null, then init must be not null and in fact an Initializer.
	 * The expr then is a constructed form of [0..init.length()-1]->here.
	 * This can be implemented as a javaarray.
	 */
	protected /*nullable*/ Expr distribution;
	/**
	 * 
	 */
	// vj: init may be an ArrayInit or a fun expr, or could be null
	// rmf 3/1/2007: init is now either a Closure or an ArrayInit, but still an Expr
	protected /*nullable*/ Expr initializer;
	
	
	/**
	 * @param pos
	 */
	public ArrayConstructor_c(Position pos) {
		super(pos);
		// TODO Auto-generated constructor stub
	}
	public ArrayConstructor_c(Position pos, TypeNode base, boolean unsafe, 
			boolean isValue, Expr distribution, Expr initializer) {
		super(pos);
		assert (distribution !=null) || (initializer instanceof ArrayInit);
		this.base = base;
		this.isSafe = ! unsafe;
		this.isValue = isValue;
		this.distribution = distribution;
		this.initializer = initializer;
	}
	
	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10ArrayConstructor#arrayBaseType()
	 */
	public TypeNode arrayBaseType() {
		return this.base;
	}
	
	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10ArrayConstructor#region()
	 */
	public Expr distribution() {
		return this.distribution;
	}
	
	public Expr initializer() {
		return initializer;
	}
	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10ArrayConstructor#arrayBaseType(polyglot.ast.TypeNode)
	 */
	public ArrayConstructor arrayBaseType(TypeNode base) {
		ArrayConstructor_c n = (ArrayConstructor_c) copy();
		n.base = base;
		return n;
	}
	
	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10ArrayConstructor#region(polyglot.ast.Expr)
	 */
	public ArrayConstructor distribution(Expr distribution) {
		ArrayConstructor_c n = (ArrayConstructor_c) copy();
		n.distribution = distribution;
		return n;
	}
	
	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10ArrayConstructor#body(polyglot.ast.Block)
	 */
	public ArrayConstructor initializer(Expr init) {
		ArrayConstructor_c n = (ArrayConstructor_c) copy();
		n.initializer = init;
		return n;
	}
	
	public boolean isSafe() {
		return isSafe;
	}
	public boolean isValue() {
		return isValue;
	}
	/** Reconstruct the statement. */
	protected ArrayConstructor reconstruct( TypeNode base, Expr distribution, Expr init ) {
		if ( this.base == base && this.distribution == distribution && this.initializer == init ) 
			return this;
		
		ArrayConstructor_c n = (ArrayConstructor_c) copy();
		n.base = base;
		n.distribution = distribution;
		n.initializer = init;
		
		return n;
		
	}
    
	// RMF 3/1/2007 - dead method?
	public boolean hasInitializer() {
		return initializer != null && initializer instanceof ArrayInit;
	}
	
	/** Visit the children of the statement. */
	public Node visitChildren( NodeVisitor v ) {
		TypeNode base = (TypeNode) visitChild(this.base, v);
		Expr distribution = (Expr) visitChild(this.distribution, v);
		Expr init = (Expr) visitChild(this.initializer, v);
		return reconstruct( base,  distribution, init );
	}
	
	/** Type check the statement. 
	 * TODO: Check that the distribution and initializer have already been visited by the TC.
	 */
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
		X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();

		Node n = this.base.del().typeCheck(tc);
		
		if (! (n instanceof TypeNode))
			throw new SemanticException("Array constructor base type |" + n + "| is not a type." + position());
		
		TypeNode newBase = (TypeNode) n;
		Ref<? extends Type> newBaseType = newBase.typeRef();
		Expr newDistribution = distribution;
		if (this.distribution != null) {
			//Report.report(1, "ArrayConstructor dist is " + distribution.type());
			Type distType = distribution.type();
			boolean distributionIsInt = ts.isImplicitCastValid(distType, ts.Int());
			if (distributionIsInt) {
				// This is a NewArray in disguise. Unmask it and bail.
				List l = new LinkedList();
				l.add(distribution);
				Expr newArray = (Expr) nf.NewArray(position(), newBase, l, 0, null).typeCheck(tc);
				if (initializer != null) {
					// Before typechecking, rewrite this as the following call:
					//     x10.lang.ArrayOperations.arrayInit(Object[], new Pointwise() {
					//         public newBase apply(point p, newBase dummy) {
					//             initializer.body()
					//         }
					//     })
					assert (!(initializer instanceof ArrayInit));
					TypeNode arr_ops = nf.CanonicalTypeNode(position(), ts.ArrayOperations());
					TypeNode arr_type = nf.array(newBase, position(), 1);
					Closure init_closure = (Closure) initializer;
					List<Formal> init_closure_formals = init_closure.formals();
					X10Formal init_closure_formal = (X10Formal) init_closure_formals.get(0);

					List<ClassMember> pointwise_members = new TypedList(new LinkedList(), ClassMember.class, false);

					List<Formal> apply_formals = new TypedList(new LinkedList(), Formal.class, false);
					Formal formal = nf.Formal(position(), Flags.NONE, init_closure_formal.type(), init_closure_formal.id());
					formal = formal.localDef(ts.localDef(position(), Flags.NONE, newBaseType, init_closure_formal.id().id()));
					
					apply_formals.add(formal);

					Formal dummy_formal = nf.Formal(position(), Flags.NONE, nf.CanonicalTypeNode(position(), newBaseType), nf.Id(position(), "__dummy__"));
					dummy_formal = dummy_formal.localDef(ts.localDef(position(), Flags.NONE, newBaseType, "__dummy__"));
					apply_formals.add(dummy_formal);

					Block closure_body = init_closure.body();
					List<Stmt> index_inits = init_closure_formal.explode(nf, ts);
					Block body_with_index_vars = nf.Block(position(), closure_body.statements());
					for(Iterator<Stmt> iter= index_inits.iterator(); iter.hasNext();) {
					    Stmt index_init= iter.next();

					    body_with_index_vars = body_with_index_vars.prepend(index_init);
					}

					MethodDecl apply_method = nf.MethodDecl(position(), Flags.PUBLIC, newBase, nf.Id(position(), "apply"), apply_formals, new ArrayList(), body_with_index_vars);
					List<Ref<? extends Type>> apply_arg_types = new TypedList(new LinkedList(), Ref.class, false);
					apply_arg_types.add(newBaseType);

					ClassDef anon_type = ts.createClassDef();
					anon_type.kind(ClassDef.ANONYMOUS);
					anon_type.position(position());
					anon_type.outer(Types.ref(tc.context().currentClassScope()));
					apply_method = apply_method.methodDef(ts.methodDef(position(), Types.ref(anon_type.asType()), apply_method.flags(), apply_method.returnType().typeRef(), apply_method.name(), apply_arg_types, new ArrayList()));
					pointwise_members.add(apply_method);
					ClassBody pointwise_body = nf.ClassBody(position(), pointwise_members);
					TypeNode pointwise_type = nf.CanonicalTypeNode(position(), ts.OperatorPointwise());
					New new_pointwise = (New) nf.New(position(), pointwise_type, new ArrayList(), pointwise_body).type(ts.OperatorPointwise());

					new_pointwise = new_pointwise.anonType(anon_type);
					new_pointwise = new_pointwise.constructorInstance(ts.constructorDef(position(), Types.ref(anon_type.asType()), Flags.PUBLIC, new ArrayList(), new ArrayList()).asInstance());

					newArray = (Expr) nf.Call(position(), arr_ops, nf.Id(position(), "arrayInit"), newArray, new_pointwise /*initializer*/).typeCheck(tc);
					// [IP] Need the cast because arrayInit(java.lang.Object[]) returns java.lang.Object[]
					newArray = (Expr) nf.Cast(position(), arr_type, newArray).typeCheck(tc);
				}
				return newArray;
			}
			boolean distributionIsRegion = ts.isImplicitCastValid(distType, ts.region());
			if (distributionIsRegion) {
				// convert this region to a distribution.
				newDistribution = (Expr) nf.Call(position(), distribution, nf.Id(position(), "toDistribution")).typeCheck(tc);
			} else {
				// it must be a distribution.
				boolean distributionIsDist = ts.isImplicitCastValid(distType, ts.distribution());
				// System.out.println("ArrayConstructor_c: distributionIsDist = " + distributionIsDist + " " + distType );
				if (!distributionIsDist)
					throw new SemanticException("Array distribution specifier must be of type int or distribution" 
							+ position());
			}
		}

		Expr newInit= null;

		if (initializer != null) {
			if (initializer instanceof ArrayInit) {
				// RMF 3/1/2007 - If this is so, why does ArrayConstructor permit an ArrayInit initializer???
				throw new InternalError("ArrayConstructor_c should really have been NewArray_c" + this);
				// This is the {...} case, and the parser should rule this out.
				// ((ArrayInit) initializer).typeCheckElements(base.type());
			}
			
			// TODO: igor: The following is hardwired.
			// HACK RMF 3/1/2007
			Type initType = initializer.type();
			newInit = (Expr) initializer.typeCheck(tc);
//			if (! ts.isImplicitCastValid(initType, ts.OperatorPointwise()))
//				throw new SemanticException("Array initializer must be of type x10.array.Operator.Pointwise" 
//							+ position());
		}
		// Transfer the attributes from the dist to the array. This is in lieu of reading the
		// dependent type for the constructor from an X10 source file.
		X10ParsedClassType p = (X10ParsedClassType) ts.array(newBaseType, isValue);
		X10ParsedClassType t = transferAttributes(p, (X10ParsedClassType) newDistribution.type());
		// System.out.println("ArrayConstructor_c: t=" + t);
		ArrayConstructor_c n1 = (ArrayConstructor_c) type(t);
		
		return n1.distribution(newDistribution).arrayBaseType(newBase).initializer(newInit);
		
	}
	
	private X10ParsedClassType transferAttributes(X10ParsedClassType t, 
			X10ParsedClassType distType) {
		//Report.report(1, "ArrayConstructor: transferring attributes from " + distType
		//		+ " to " + t);
		C_Var self = distType.self();
		if (self != null) {
			t.setDistribution(self);
		}
		C_Var onePlace = distType.onePlace();
		if (onePlace !=null) {
			t.setOnePlace(onePlace);
			//Report.report(1, "Setting oneplace result is "  + t);
		}
		boolean isRect = distType.isRect();
		if (isRect) t.setRect();
		boolean zeroBased = distType.isZeroBased();
		//Report.report(1, "ArrayConstructor zeroBased? "  + zeroBased);
		if (zeroBased){
			t.setZeroBased();
		}
		C_Var rank = distType.rank();
		if (rank !=null) t.setRank(rank);
		if (t.hasLocalProperty() && zeroBased && isRect && ((X10TypeSystem) t.typeSystem()).ONE().equals(rank)) 
			t.setRail();
		C_Var region = distType.region();
		if (region != null) t.setRegion(region);
		//Report.report(1, "t is now " + t);
		return t;
	}

	/**
	 * Write the statement to an output file. 
	 * TODO: vj check if printBlock is the right thing to use here.
	 */
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		if (distribution == null && initializer == null)
			throw new InternalCompilerError("Missing both distribution and initializer", position());
		w.write("new ");
		printBlock(base, w, tr);
		if (isValue)
			w.write(" value ");
		if (!isSafe)
			w.write(" unsafe ");
		w.write("[");
		if (distribution != null)
			printBlock(distribution, w, tr);
		w.write("]");
		if (initializer != null)
			initializer.del().prettyPrint(w, tr);
	}

	public String toString() {
		StringBuffer s = new StringBuffer("new " + base);
		s.append( isValue ? " value " : "");
		s.append( ! isSafe ? " unsafe " : "");
		s.append("[");
		s.append(this.distribution == null ? "" : distribution.toString());
		s.append("]");
		s.append(this.initializer == null ? "" : initializer.toString());
		return s.toString();
	}
	/* (non-Javadoc)
	 * @see polyglot.ast.Term#entry()
	 */
	public Term firstChild() {
		return (distribution != null) ? 
				distribution
				:  (initializer  != null ? initializer : null);
	}
	
	/**
	 * Visit this term in evaluation order.
	 */
	public List acceptCFG(CFGBuilder v, List succs) {
		if (distribution != null) {
                    if (initializer != null) {
                        v.visitCFG(distribution, initializer, ENTRY);
                    }
                    else {
                        v.visitCFG(distribution, this, EXIT);
                    }
                }
                if (initializer != null) {
                    v.visitCFG(initializer, this, EXIT);
                }
		return succs;
	}
	
}
