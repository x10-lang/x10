package x10.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.ClassBody;
import polyglot.ast.Eval_c;
import polyglot.ast.Expr;
import polyglot.ast.Field;
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
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.ast.Binary.Operator;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.Position;
import x10.ast.Closure;
import x10.ast.X10ClassDecl_c;
import x10.ast.X10Field_c;
import x10.ast.X10Formal;
import x10.ast.X10Loop;
import x10.ast.X10NodeFactory;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XName;
import x10.constraint.XRoot;
import x10.constraint.XTerm;
import x10.constraint.XTerm_c;
import x10.constraint.XTerms;
import x10.constraint.XVar;
import x10.types.ClosureDef;
import x10.types.X10Context;
import x10.types.X10Context_c;
import x10.types.X10FieldInstance;
import x10.types.X10Type;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.X10TypeSystem_c;

/**
 * A utility to help synthesize fragments of ASTs. Most of the methods on this class are intended to
 * be used after an AST has been type-checked. These methods construct and add type-checked
 * AST nodes.
 * @author vj
 *
 */
public class Synthesizer {
	
	X10TypeSystem xts;
	X10NodeFactory xnf;
	public Synthesizer(X10NodeFactory nf, X10TypeSystem ts) {
		xts=ts;
		xnf=nf;
	}

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
	
	 public  X10ClassDecl_c addSyntheticMethod(X10ClassDecl_c ct, Flags flags, 
			 Name name, List<LocalDef> fmls, 
			 Type returnType, List<Type> trow, Block block) {
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
	 
	 public static XTerm makeProperty(Type type, XVar receiver, String name) {
		 X10FieldInstance fi = 
				X10TypeMixin.getProperty(type, Name.make(name));
			XName field = XTerms.makeName(fi.def(), 
					Types.get(fi.def().container()) 
					+ "#" + fi.name().toString());
			return XTerms.makeField(receiver, field);
			
	 }
	 
	 public XTerm makeRegionRankTerm(XVar receiver) {
		 return makeProperty(xts.Point(), receiver, "rank");
	 }
	 
	 public XTerm makeRectTerm(XVar receiver) {
		 return makeProperty(xts.Region(), receiver, "rect");
	 }
	
	 public Type addRectConstraint(Type type, XVar receiver) {
			XTerm v = makeRectTerm(receiver);
			return X10TypeMixin.addTerm(type, v);
		 
	 }
	 
	 public Type addRankConstraint(Type type, XVar receiver, int n, X10TypeSystem ts) {
			XTerm v = makeRegionRankTerm(receiver);
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
	public For makeForLoop(Position pos, 
			X10Formal formal, Expr low, Expr high, Stmt body, 
			 X10Context context) {
		Position CG = Position.COMPILER_GENERATED;
		List<Stmt> inits = new ArrayList<Stmt>();
		// FIXME: use formal here directly, instead of local
		Expr local = makeLocalVar(CG, null, low, inits, context);
		Expr limit = makeLocalVar(CG, null, high, inits, context);
		Expr test = xnf.Binary(CG, local, Binary.LE, limit).type(xts.Boolean());
		Expr iters = xnf.Unary(CG, local, Unary.POST_INC).type(xts.Int());
		Stmt formalInit = makeLocalVar(CG, formal.localDef(), local);
		Block block = xnf.Block(CG, formalInit,body);
		List<ForInit> inits2 = new ArrayList<ForInit>();
		for (int i = 0; i < inits.size(); i++)
		    inits2.add((ForInit) inits.get(i));
		List<ForUpdate> itersL = new ArrayList<ForUpdate>();
		itersL.add(xnf.Eval(CG, iters));
		For node = xnf.For(pos, inits2, test, itersL, block);
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
	public  Stmt makeLocalVar(Position pos, LocalDef li, Expr e) {
		final TypeNode tn = xnf.CanonicalTypeNode(pos, li.type());
		final LocalDecl ld = xnf.LocalDecl(pos, xnf.FlagsNode(pos, li.flags()), tn, 
				xnf.Id(pos,li.name()), e).localDef(li);
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
	
	public Expr makeLocalVar(Position pos, Flags flags, Expr  e, List<Stmt> stmtList,
			 X10Context xc) {
		Expr result = null;
		if (flags == null) {
			flags = Flags.NONE;
		}
		// has been converted to a variable reference.
		final Type type = e.type();
		if (! xts.typeEquals(type, xts.Void(), xc)) {
			final Name varName = xc.getNewVarName();
			final TypeNode tn = xnf.CanonicalTypeNode(pos,type);
			final LocalDef li = xts.localDef(pos, flags, Types.ref(type), varName);
			final Id varId = xnf.Id(pos, varName);
			final LocalDecl ld = xnf.LocalDecl(pos, xnf.FlagsNode(pos, flags), tn, varId, e).localDef(li);
			final Local ldRef = (Local) xnf.Local(pos, varId).localInstance(li.asInstance()).type(type);
			stmtList.add(ld);
			result=ldRef;
		}
		return result;
	}
	
	/**
	 * Make a field access for r.name. Throw a SemanticException if such a field does not exist.
	 * @param pos
	 * @param r
	 * @param name
	 * @param context
	 * @return
	 * @throws SemanticException
	 */
	public Expr makeFieldAccess(Position pos, Receiver r, Name name, X10Context context) throws SemanticException {
		FieldInstance fi = xts.findField(r.type(), xts.FieldMatcher(r.type(), name, context));
		 Expr result = xnf.Field(pos, r, xnf.Id(pos, name)).fieldInstance(fi)
		 .type(fi.type());
		 return result;
	}
	
	public Call makeStaticCall(Position pos, 
			Type receiver, 
			Name name,
			Type returnType,
			X10Context xc) throws SemanticException {
		return makeStaticCall(pos, receiver, name, Collections.EMPTY_LIST, 
				Collections.EMPTY_LIST, returnType, xc);
	}
	
	public Call makeStaticCall(Position pos, 
			Type receiver, 
			Name name,
			List<Expr> args,
			Type returnType,
			X10Context xc) throws SemanticException {
		return makeStaticCall(pos, receiver, name, Collections.EMPTY_LIST, args, returnType, xc);
	}

	public Call makeStaticCall(Position pos, 
			Type receiver, 
			Name name,
			List<Expr> args,
			Type returnType,
			List<Type> argTypes,
			X10Context xc) throws SemanticException {
		return makeStaticCall(pos, receiver, name, Collections.EMPTY_LIST, args, 
				returnType, argTypes, xc);
	}
	public Call makeStaticCall(Position pos, Type receiver, Name name,
			List<TypeNode> typeArgsN, 
			List<Expr> args,
			Type returnType, 
			X10Context xc) throws SemanticException {
		List<Type> argT = new ArrayList<Type>();
        for (Expr t: args) argT.add(t.type());
		return makeStaticCall(pos, receiver, name, typeArgsN, args,  returnType, argT, xc);
	}
	/**
	 * Return a static call constructed from the given data.
	 * @param pos
	 * @param receiver
	 * @param name
	 * @param typeArgsN
	 * @param args
	 * @param xnf
	 * @param xts
	 * @param xc
	 * @return
	 * @throws SemanticException if no method can be located with this data
	 */
	public Call makeStaticCall(Position pos, 
			Type receiver, 
			Name name,
			List<TypeNode> typeArgsN, 
			List<Expr> args,
			Type returnType,
			List<Type> argTypes,
			X10Context xc) throws SemanticException {
		
        List<Type> typeArgs = new ArrayList<Type>();
        for (TypeNode t : typeArgsN) typeArgs.add(t.type());
        MethodInstance mi = xts.findMethod(receiver,
                xts.MethodMatcher(receiver, name, typeArgs, argTypes, xc));
        Call result= (Call) xnf.X10Call(pos, 
        		xnf.CanonicalTypeNode(pos, receiver),
                xnf.Id(pos, name), 
                typeArgsN,
                args)
                .methodInstance(mi)
                .type(returnType);
        return result;
		
	}
	
	public Closure makeClosure(Position pos, Type retType, Block body,
			 X10Context context) {
		return makeClosure(pos, retType, Collections.EMPTY_LIST, body, context);
	}
	 public Closure makeClosure(Position pos, Type retType, List<Formal> parms, Block body,
			 X10Context context) {
	        List<Ref<? extends Type>> fTypes = new ArrayList<Ref<? extends Type>>();
	        List<LocalDef> fNames = new ArrayList<LocalDef>();
	        for (Formal f : parms) {
	            fTypes.add(Types.ref(f.type().type()));
	            fNames.add(f.localDef());
	        }
	        ClosureDef cDef = xts.closureDef(pos, Types.ref(context.currentClass()),
	                Types.ref(context.currentCode().asInstance()),
	                Types.ref(retType), 
	            //    Collections.EMPTY_LIST,
	                fTypes, 
	                (XRoot) null, 
	                fNames, 
	                null, 
	             //   null, 
	                Collections.EMPTY_LIST);
	        Closure closure = (Closure) xnf.Closure(pos, //Collections.EMPTY_LIST,
	                parms, 
	                null, 
	                xnf.CanonicalTypeNode(pos, retType),
	                Collections.EMPTY_LIST, body)
	                .closureDef(cDef)
	                .type(xts.closureAnonymousClassDef(cDef).asType());
	        return closure;
	    }
	 
	 public Block toBlock(Stmt body) {
		   return body instanceof Block ? (Block) body : xnf.Block(body.position(), body);
	 }
	 
	 public Field firstPlace() {
		 XConstraint c = new XConstraint_c();
		 XTerm id = makeProperty(xts.Int(), c.self(), "id");
		 try {
			 c.addBinding(id, XTerms.makeLit(0));
			 Type type = X10TypeMixin.xclause(xts.Place(), c);
			 return makeStaticField(Position.COMPILER_GENERATED, xts.Place(), 
					 Name.make("FIRST_PLACE"), type, new X10Context_c(xts));
		 } catch (XFailure z) {
			 // wont happen
		 } catch (SemanticException z) {
			 // wont happen
		 }
		 return null;
		
	 }
	 public Field makeStaticField(Position pos, 
				Type receiver, 
				Name name,
				Type returnType,
				X10Context xc) throws SemanticException {
		
	        FieldInstance fi = xts.findField(receiver,
	                xts.FieldMatcher(receiver, name, xc));
	       Field result=  (Field) xnf.Field(pos, 
	        		xnf.CanonicalTypeNode(pos, receiver),
	                xnf.Id(pos, name))
	                .fieldInstance(fi)
	                .type(returnType);
	        return result;
			
		}

}
