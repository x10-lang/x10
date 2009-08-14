package x10.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.ClassBody;
import polyglot.ast.Eval_c;
import polyglot.ast.Expr;
import polyglot.ast.FlagsNode;
import polyglot.ast.For;
import polyglot.ast.ForInit;
import polyglot.ast.ForUpdate;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.ast.Binary.Operator;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.MethodDef;
import polyglot.types.Name;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.Position;
import x10.ast.X10ClassDecl_c;
import x10.ast.X10Formal;
import x10.ast.X10Loop;
import x10.ast.X10NodeFactory;
import x10.constraint.XConstraint;
import x10.constraint.XName;
import x10.constraint.XTerm;
import x10.constraint.XTerm_c;
import x10.constraint.XTerms;
import x10.constraint.XVar;
import x10.types.X10ArraysMixin;
import x10.types.X10Context;
import x10.types.X10FieldInstance;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;

/**
 * A utility to help synthesize fragments of ASTs. Most of the methods on this class are intended to
 * be used after an AST has been type-checked. These methods construct and add type-checked
 * AST nodes.
 * @author vj
 *
 */
public class Synthesizer {
	

	  /**
   * Create a synthetic MethodDecl from the given data and return
   * a new Class with this MethodDecl added in. No duplicate method
   * checks will be performed. It is up to the user to make sure
   * the constructed method will not duplicate an existing method.
   * 
   * Should be called after the class has been typechecked.
   * @param ct  -- The clas to which this code has to be added
   * @param flags -- The flags for the method
   * @param name -- The name of the method
   * @param fmls -- A list of LocalDefs specifying the parameters to the method.
   * @param returnType -- The return type of this method.
   * @param trow  -- The types of throwables from the method.
   * @param block -- The body of the method
   * @param xnf -- The X10NodeFactory to be used to create new AST nodes.
   * @param xts  -- The X10TypeSystem object to be used to create new types.
   * @return  this, with the method added.
   * 
   * TODO: Ensure that type parameters and a guard can be supplied as well.
   */
	
	 public static X10ClassDecl_c addSyntheticMethod(X10ClassDecl_c ct, Flags flags, Name name, List<LocalDef> fmls, 
	    		Type returnType, List<Type> trow, Block block,
	    		X10NodeFactory xnf, X10TypeSystem xts) {
	    	assert ct.classDef() != null;
	    	
	    	Position CG = Position.COMPILER_GENERATED;
	    	List<Expr> args = new ArrayList<Expr>();
	    	List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>();
	    	List<Formal> formals = new ArrayList<Formal>(fmls.size());
	    	for (LocalDef f : fmls) {
	    		Id id = xnf.Id(CG, f.name()); 

	    		Formal ff = xnf.Formal(CG,xnf.FlagsNode(CG, Flags.NONE), 
	    				xnf.CanonicalTypeNode(CG, f.type()),
	    				id);
	    		Local loc = xnf.Local(CG, id);
	    		LocalDef li = xts.localDef(CG, ff.flags().flags(), ff.type().typeRef(), id.id());
	    		ff = ff.localDef(li);
	    		loc = loc.localInstance(li.asInstance());
	    		loc = (Local) loc.type(li.asInstance().type());
	    		formals.add(ff);
	    		args.add(loc);
	    		argTypes.add(li.type());
	    	}
	    	FlagsNode newFlags = xnf.FlagsNode(CG, flags);
	    	TypeNode rt = xnf.CanonicalTypeNode(CG, returnType);


	    	List<TypeNode> throwTypeNodes = new ArrayList<TypeNode>();
	    	List<Ref<? extends Type>> throwTypes = new ArrayList<Ref<? extends Type>>();
	    	for (Type  t: trow) {
	    		Ref<Type> tref = Types.ref(t);
	    		throwTypes.add(tref);
	    		throwTypeNodes.add(xnf.CanonicalTypeNode(CG, t));
	    	}

	    	// Create the method declaration node and the CI.
	    	MethodDecl result = 
	    		xnf.MethodDecl(CG, newFlags, rt, xnf.Id(CG,name), formals, throwTypeNodes, block);

	    	MethodDef rmi = xts.methodDef(CG, Types.ref(ct.classDef().asType()), 
	    			newFlags.flags(), rt.typeRef(), name, argTypes, throwTypes);
	    	ct.classDef().addMethod(rmi);
	    	result = result.methodDef(rmi);
	    
	    	ClassBody b = ct.body();
	    	b = b.addMember(result);
	    	return (X10ClassDecl_c) ct.body(b);
	    }
	 
	 public static XTerm makeProperty(Type type, XVar receiver, String name, X10TypeSystem ts) {
		 X10FieldInstance fi = 
				X10ArraysMixin.getProperty(type, Name.make(name));
			XName field = XTerms.makeName(fi.def(), 
					Types.get(fi.def().container()) 
					+ "#" + fi.name().toString());
			return XTerms.makeField(receiver, field);
			
	 }
	 
	 public static XTerm makeRegionRankTerm(XVar receiver, X10TypeSystem ts) {
		 return makeProperty(ts.Point(), receiver, "rank", ts);
	 }
	 
	 public static XTerm makeRectTerm(XVar receiver, X10TypeSystem ts) {
		 return makeProperty(ts.Region(), receiver, "rect", ts);
	 }
	
	 public static Type addRectConstraint(Type type, XVar receiver, X10TypeSystem ts) {
			XTerm v = Synthesizer.makeRectTerm(receiver, ts);
			return X10TypeMixin.addTerm(type, v);
		 
	 }
	 
	 public static Type addRankConstraint(Type type, XVar receiver, int n, X10TypeSystem ts) {
			XTerm v = Synthesizer.makeRegionRankTerm(receiver, ts);
			XTerm rank = XTerms.makeLit(new Integer(n));
			return X10TypeMixin.addBinding(type, v, rank);
		 
	 }
	 
	 /**
	  * If formal = p(x) construct
	  *  for|foreach|ateach ( var _gen = low; _gen <= high; _gen++) {
	  *    val x = _gen;
	  *    val p = [x] as Point;
	  *    stm
	  *  }
	  *  If formal = (x) construct
	  *  for|foreach|ateach (var _gen = low; _gen <= high; _gen ++) {
	  *     val x = _gen;
	  *     stm
	  *  }
	  *  If formal is null construct:
	  *  for|foreach|ateach (var _gen = low; _gen <= high; _gen ++) {
	  *     stm
	  *  }
	  * @param pos
	  * @param kind
	  * @param formal
	  * @param low
	  * @param high
	  * @param stm
	  * @return
	  */
	public static For makeForLoop(Position pos, 
			X10Formal formal, Expr low, Expr high, Stmt body, 
			NodeFactory nf, X10TypeSystem ts, X10Context context) {
		Position CG = Position.COMPILER_GENERATED;
		List<Stmt> inits = new ArrayList<Stmt>();
		Expr local = makeLocalVar(CG, null, low, inits,  nf, ts, context);
		Expr test = nf.Binary(CG, local, Binary.LE, high).type(ts.Boolean());
		Expr iters = nf.Unary(CG, local, Unary.POST_INC).type(ts.Int());
		Stmt formalInit = makeLocalVar(CG, formal.localDef(), local,nf);
		Block block = nf.Block(CG, formalInit,body);
		List<ForInit> inits2 = new ArrayList<ForInit>();
		inits2.add((ForInit) inits.get(0));
		List<ForUpdate> itersL = new ArrayList<ForUpdate>();
		itersL.add( nf.Eval(CG, iters));
		For node = nf.For(pos, inits2, test, itersL, block);
		return node;
	}
	
	/**
	 * Create a new local variable declaration from the given LocalDef. The user is responsible
	 * for ensuring that the variable is not going to be defined elsewhere in the context.
	 * @param pos
	 * @param li
	 * @param e
	 * @param nf
	 * @return
	 */
	public static Stmt makeLocalVar(Position pos, LocalDef li, Expr e, NodeFactory nf) {
		final TypeNode tn = nf.CanonicalTypeNode(pos, li.type());
		final LocalDecl ld = nf.LocalDecl(pos, nf.FlagsNode(pos, li.flags()), tn, nf.Id(pos,li.name()), e).localDef(li);
		return ld;
	}
	
	/**
	 * Create a new local variable with the given flags, with initializer e. Add the variable declaration to stmtList.
	 * Return a reference to the variable.
	 * 
	 * Returns null if the type is Void.
	 * 
	 * @param pos
	 * @param e
	 * @param stmtList
	 * @param nf
	 * @param ts
	 * @param xc
	 * @return
	 */
	
	public static Expr makeLocalVar(Position pos, Flags flags, Expr  e, List<Stmt> stmtList,
			NodeFactory nf, X10TypeSystem ts, X10Context xc) {
		Expr result = null;
		if (flags == null) {
			flags = Flags.NONE;
		}
		// has been converted to a variable reference.
		final Type type = e.type();
		if (! ts.typeEquals(type, ts.Void(), xc)) {
			final Name varName = xc.getNewVarName();
			final TypeNode tn = nf.CanonicalTypeNode(pos,type);
			final LocalDef li = ts.localDef(pos, flags, Types.ref(type), varName);
			final Id varId = nf.Id(pos, varName);
			final LocalDecl ld = nf.LocalDecl(pos, nf.FlagsNode(pos, flags), tn, varId, e).localDef(li);
			final Local ldRef = (Local) nf.Local(pos, varId).localInstance(li.asInstance()).type(type);
			stmtList.add(ld);
			result=ldRef;
		}
		return result;
	}

}
