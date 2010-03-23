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

package x10.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassMember;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldDecl;
import polyglot.ast.FlagsNode;
import polyglot.ast.For;
import polyglot.ast.ForInit;
import polyglot.ast.ForUpdate;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.ConstructorDef;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.Position;
import x10.ast.Closure;
import x10.ast.X10ClassDecl;
import x10.ast.X10ClassDecl_c;
import x10.ast.X10ConstructorDecl;
import x10.ast.X10Formal;
import x10.ast.X10NodeFactory;
import x10.constraint.XFailure;
import x10.constraint.XName;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10Context;
import x10.types.X10Context_c;
import x10.types.X10Def;
import x10.types.X10FieldInstance;
import x10.types.X10Flags;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.X10TypeSystem_c;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CConstraint_c;

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
   * @param ct  -- The class to which this code has to be added
   * @param flags -- The flags for the method
   * @param name -- The name of the method
   * @param fmls -- A list of LocalDefs specifying the parameters to the method.
   * @param returnType -- The return type of this method.
   * @param trow  -- The types of throwables from the method.
   * @param block -- The body of the method
   * @return  this, with the method added.
   * 
   * TODO: Ensure that type parameters and a guard can be supplied as well.
   */
	
	 public  X10ClassDecl_c addSyntheticMethod(X10ClassDecl_c ct, Flags flags, 
			 Name name, List<LocalDef> fmls, 
			 Type returnType, List<Type> trow, Block block) {
		 assert ct.classDef() != null;
	     MethodDecl result = makeSyntheticMethod(ct, flags, name,fmls, returnType, trow, block);
	     ClassBody b = ct.body();
	    	b = b.addMember(result);
	    	ct.classDef().addMethod(result.methodDef());
	    	return (X10ClassDecl_c) ct.body(b);
	 }
	 
	  /**
	   * Create a synthetic MethodDecl from the given data and return
	   * the MethodDecl 
	   * Should be called after the class has been type-checked.
	   * 
	   * @param ct  -- The class to which this code has to be added
	   * @param flags -- The flags for the method
	   * @param name -- The name of the method
	   * @param fmls -- A list of LocalDefs specifying the parameters to the method.
	   * @param returnType -- The return type of this method.
	   * @param trow  -- The types of throwables from the method.
	   * @param block -- The body of the method
	   * @return  this, with the method added.
	   * 
	   */
	public  MethodDecl makeSyntheticMethod(X10ClassDecl_c ct, Flags flags, 
			 Name name, List<LocalDef> fmls, 
			 Type returnType, List<Type> trow, Block block) {
	    	
	    	
	    	Position CG = Position.COMPILER_GENERATED;
	    	List<Expr> args = new ArrayList<Expr>(); //FIXME: what's the usage of the args?
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
	    	
	    	result = result.methodDef(rmi);
	    return result;
	    }
	 
	 public static XTerm makeProperty(Type type, XVar receiver, String name) {
		 X10FieldInstance fi = 
				X10TypeMixin.getProperty(type, Name.make(name));
		 if (fi == null)
			 return null;
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
	
	/**
	 * Make a field to field assign: leftReceiver.leftName = rightReceiver.rightName
	 * @param pos
	 * @param leftReceiver
	 * @param leftName
	 * @param rightReceiver
	 * @param rightName
         * @param context
	 * @return
	 * @throws SemanticException 
	 */
	public Expr makeFieldToFieldAssign(Position pos, Receiver leftReceiver, Name leftName,
	                                   Receiver rightReceiver, Name rightName, X10Context context) throws SemanticException{
	    

            Expr rightExpr = makeFieldAccess(pos, rightReceiver, rightName, context);

            Field field = makeStaticField(pos, leftReceiver.type(), 
                                                leftName,
                                                xts.Int(),
                                                context);
            // assign
            Expr assign = xnf.FieldAssign(pos, 
                    leftReceiver, xnf.Id(pos, leftName), 
                    Assign.ASSIGN, rightExpr)
                    .fieldInstance(field.fieldInstance())
                    .type(rightExpr.type());
	    
	    
	    return assign;
	}
	
	/**
	 * Make a local to field assign: leftReceiver.leftName = local (localName/localType)
	 * @param pos
	 * @param leftReceiver
	 * @param leftName
	 * @param localName
	 * @param localType
	 * @param localFlags
         * @param context
	 * @return
	 * @throws SemanticException 
	 */
	public Expr makeLocalToFieldAssign(Position pos, Receiver leftReceiver, Name leftName,
                                           Name localName, Type localType, Flags localFlags, X10Context context) throws SemanticException{
	    
	    
            LocalDef ldef = xts.localDef(pos, localFlags, Types.ref(localType), localName);
            Expr init = xnf.Local(pos, xnf.Id(pos, localName)).localInstance(ldef.asInstance()).type(localType);
            
            Field field = makeStaticField(pos, leftReceiver.type(), 
                                          leftName,
                                          xts.Int(),
                                          context);
            Expr assign = xnf.FieldAssign(pos, leftReceiver, xnf.Id(pos, leftName), Assign.ASSIGN, init)
                        .fieldInstance(field.fieldInstance())
                        .type(localType);
	    
	    return assign;
	}
	
	/**
	 * Make a field to local assign: local(localName/localType) = rightReceiver.rightName
	 * @param pos
	 * @param localName
	 * @param localType
	 * @param rightReceiver
	 * @param rightName
         * @param context
	 * @return
	 * @throws SemanticException 
	 */
	public Expr makeFieldToLocalAssign(Position pos, Name localName, Type localType, Flags localFlags, Receiver rightReceiver, Name rightName
	                                   , X10Context context) throws SemanticException{
	    
	    //FIXME: need check whether this method returns correct expr
	    //right 
	    Expr rightExpr = makeFieldAccess(pos, rightReceiver, rightName, context);
	    
	    //locals
	    LocalDef ldef = xts.localDef(pos, localFlags, Types.ref(localType), localName);
            Local local = xnf.Local(pos, xnf.Id(pos, localName)).localInstance(ldef.asInstance());
	    
            //assign
            Expr assign = xnf.LocalAssign(pos, local, Assign.ASSIGN, rightExpr).type(rightExpr.type());
            
	    return assign;
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
	
	public Call makeInstanceCall(Position pos, 
			Receiver receiver, 
			Name name,
			List<TypeNode> typeArgsN, 
			List<Expr> args,
			Type returnType,
			List<Type> argTypes,
			X10Context xc) throws SemanticException {
		
        List<Type> typeArgs = new ArrayList<Type>();
        for (TypeNode t : typeArgsN) typeArgs.add(t.type());
        MethodInstance mi = xts.findMethod(receiver.type(),
                xts.MethodMatcher(receiver.type(), name, typeArgs, argTypes, xc));
        Call result= (Call) xnf.X10Call(pos, 
        		receiver,
                xnf.Id(pos, name), 
                typeArgsN,
                args)
                .methodInstance(mi)
                .type(returnType);
        return result;
		
	}
	/**
	 * Return a synthesized AST node for (parms):retType => body, at the given position and context.
	 * @param pos
	 * @param retType
	 * @param parms
	 * @param body
	 * @param context
	 * @return
	 */
	public Closure makeClosure(Position pos, Type retType, List<Formal> parms, Block body, X10Context context) {
		return ClosureSynthesizer.makeClosure((X10TypeSystem_c) xts, xnf, pos, retType, parms, body, context);
	}
	
	/**
	 * Return a synthesized AST node for ():retType => body, at the given position and context.
	 * @param pos
	 * @param retType
	 * @param parms
	 * @param body
	 * @param context
	 * @return
	 */
	
	public Closure makeClosure(Position pos, Type retType, Block body, X10Context context) {
		return makeClosure(pos, retType, Collections.EMPTY_LIST, body, context);
	}
	 
	 
	public Block toBlock(Stmt body) {
		return body instanceof Block ? (Block) body : xnf.Block(body.position(), body);
	}

	public Field firstPlace() {
		CConstraint c = new CConstraint_c();
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

	/**
	 * Return the number of annotation properties.
	 * @param def
	 * @param name
	 * @return 
	 */
	public int getPropertyNum(X10Def def, String name) throws SemanticException {
		QName qName = QName.make(name);
		Type t = (Type) xts.systemResolver().find(qName);
		List<Type> ts = def.annotationsMatching(t);
		for (Type at : ts) {
			Type bt = X10TypeMixin.baseType(at);
			if (bt instanceof X10ClassType) {
				X10ClassType act = (X10ClassType) bt;
				return act.propertyInitializers().size();
			}
		}
		return 0;		
	}
	/**
	 * Return the value of annotation properties for a given property and location index.
	 * @param def
	 * @param name
	 * @param index
	 * @return 
	 */
	public String getPropertyValue(X10Def def, String name, int index) throws SemanticException {
		QName qName = QName.make(name);
		Type t = (Type) xts.systemResolver().find(qName);
		List<Type> ts = def.annotationsMatching(t);
		for (Type at : ts) {
			Type bt = X10TypeMixin.baseType(at);
			if (bt instanceof X10ClassType) {
				X10ClassType act = (X10ClassType) bt; 
				if (index < act.propertyInitializers().size()) {
					Expr e = act.propertyInitializer(index);
					if (e.isConstant() && e.constantValue() instanceof String) {
						return (String) e.constantValue();
					}   
				}   
			}   
		}
		return null;
	}   
	/**
	 * Add a field to a class.
	 * @param cDecl
	 * @param name
	 * @param flag
	 * @param t
	 * @param p
	 * @return 
	 */	
	public X10ClassDecl addClassField(Position p, X10ClassDecl cDecl, Name name, Flags flag, Type t) {
		X10ClassDef cDef = (X10ClassDef) cDecl.classDef();
		Id id = xnf.Id(p, name);  
		CanonicalTypeNode tnode = xnf.CanonicalTypeNode(p, t); 
		FlagsNode fnode = xnf.FlagsNode(p, flag);
		FieldDef fDef = xts.fieldDef(p, Types.ref(cDef.asType()), flag, Types.ref(t), name); 
		FieldDecl fDecl = xnf.FieldDecl(p, fnode, tnode, id).fieldDef(fDef);
		cDef.addField(fDef);
		List<ClassMember> cm = new ArrayList<ClassMember>();
		cm.addAll(cDecl.body().members());
		cm.add(fDecl);
		ClassBody cb = cDecl.body();
		return (X10ClassDecl) cDecl.classDef(cDef).body(cb.members(cm));
	}   
	
	
    /**
     * Insert inner classes to the given x10 class. Return a new class with the inner classes added in.
     * 
     * @param cDecl
     *            The original class with parallel methods
     * @param innerClasses
     *            the inner classes to be inserted into the class
     * @return A newly created class with inner classes as members
     */
    public X10ClassDecl addInnerClasses(X10ClassDecl cDecl, Set<X10ClassDecl> innerClasses) {

        List<ClassMember> cMembers = new ArrayList<ClassMember>();
        ClassBody body = cDecl.body();
        cMembers.addAll(body.members());

        X10ClassDef cDef = (X10ClassDef) cDecl.classDef();

        for (X10ClassDecl icDecl : innerClasses) {
            X10ClassDef icDef = (X10ClassDef) icDecl.classDef();
            icDef.kind(ClassDef.MEMBER);
            icDef.setPackage(cDef.package_());
            icDef.outer(Types.<ClassDef> ref(cDef));

            cMembers.add(icDecl);
            cDef.addMemberClass(Types.<ClassType> ref(icDef.asType()));

        }

        return (X10ClassDecl) cDecl.body(body.members(cMembers));
    }
	
	
	public FieldDef findFieldDef(ClassDef cDef, Name fName) throws SemanticException {
	    for (FieldDef df : cDef.fields()) {
	        if (fName.equals(df.name())) {
	            return df;
	        }
	    }
	    return null;
	}
	/**
	 * Create a copy constructor decl.
	 * @param cDecl
	 * @param name
	 * @param flag
	 * @param t
	 * @param p
	 * @param context
	 * @return X10ConstructorDec
	 * @throws SemanticException 
	 */	
    public X10ConstructorDecl makeClassCopyConstructor(Position p, X10ClassDecl cDecl, List<Name> fieldName,
                                                       List<Name> parmName, List<Type> parmType, List<Flags> parmFlags,
                                                       X10Context context) throws SemanticException {
        X10ClassDef cDef = (X10ClassDef) cDecl.classDef();

        // super constructor def (noarg)
        ConstructorDef sDef = xts.findConstructor(
                                                  cDecl.superClass().type(),
                                                  xts.ConstructorMatcher(cDecl.superClass().type(), Collections
                                                          .<Type> emptyList(), context)).def();
        Block block = xnf.Block(p, xnf.SuperCall(p, Collections.<Expr> emptyList())
                .constructorInstance(sDef.asInstance()));

        List<Formal> fList = new ArrayList<Formal>();
        List<Ref<? extends Type>> ftList = new ArrayList<Ref<? extends Type>>();
        // field assign
        for (int i = 0; i < fieldName.size(); i++) {
            Name fName = fieldName.get(i);
            Name pName = parmName.get(i);
            Type pType = parmType.get(i);
            Flags pFlags = parmFlags.get(i);
            Id fid = xnf.Id(p, fName);
            // Field selector
            Receiver receiver = xnf.This(p).type(cDef.asType());
            // Field field = makeStaticField(p, special, fName, pType, context);
            FieldDef fDef = findFieldDef(cDef, fName);
            // Assign
            LocalDef ldef = xts.localDef(p, pFlags, Types.ref(pType), pName);
            Expr init = xnf.Local(p, xnf.Id(p, pName)).localInstance(ldef.asInstance()).type(pType);
            Expr assign = xnf.FieldAssign(p, receiver, fid, Assign.ASSIGN, init).fieldInstance(fDef.asInstance())
                    .type(pType);
            Formal f = xnf.Formal(p, xnf.FlagsNode(p, pFlags), xnf.CanonicalTypeNode(p, pType), xnf.Id(p, pName))
                    .localDef(ldef);
            fList.add(f);
            ftList.add(Types.ref(pType));
            block = block.append(xnf.Eval(p, assign));
        }

        // constructor
        X10ConstructorDecl xd = (X10ConstructorDecl) xnf.ConstructorDecl(p, xnf.FlagsNode(p, X10Flags.PRIVATE), cDecl
                .name(), fList, Collections.<TypeNode> emptyList(), block);
        xd.typeParameters(cDecl.typeParameters());
        xd.returnType(xnf.CanonicalTypeNode(p, cDef.asType()));

        ConstructorDef xDef = xts.constructorDef(p, Types.ref(cDef.asType()), X10Flags.PRIVATE, ftList, Collections
                .<Ref<? extends Type>> emptyList());

        return (X10ConstructorDecl) xd.constructorDef(xDef);
    }

    /**
     * Create a class's constructor with the input parameters. Return a new class with the new constructor.
     * 
     * @param cDecl
     * @param parmName
     * @param parmtype
     * @param parmFlags
     * @param stmts     Statements of the constructor. It should contains the call to super class's constructor.
     * @param context
     * @return X10ClassDecl
     * @throws SemanticException
     */
    public X10ClassDecl addClassConstructor(Position p,
                                            X10ClassDecl cDecl,
                                            List<Name> parmName,
                                            List<Type> parmType,
                                            List<Flags> parmFlags,
                                            List<Stmt> stmts,
                                            X10Context context) throws SemanticException {
        X10ClassDef cDef = (X10ClassDef) cDecl.classDef();
        ClassType cType = cDef.asType();

      
                                
        // reference to formal
        List<Formal> fList = new ArrayList<Formal>();
        List<Ref<? extends Type>> frList = new ArrayList<Ref<? extends Type>>();
       
        for (int i=0; i<parmName.size(); i++) {
            Name pName = parmName.get(i);
            Type pType = parmType.get(i);
            Flags pFlags = parmFlags.get(i);
            // reference
            LocalDef ldef = xts.localDef(p, pFlags, Types.ref(pType), pName);
            Expr ref = xnf.Local(p, xnf.Id(p, pName)).localInstance(ldef.asInstance()).type(pType);
            Formal f = xnf.Formal(p, xnf.FlagsNode(p, pFlags), 
                    xnf.CanonicalTypeNode(p, pType), 
                    xnf.Id(p, pName)).localDef(ldef);
            fList.add(f);
            frList.add(Types.ref(pType));
        }

        Block block = xnf.Block(p,
                stmts);
             
        // constructor 
        X10ConstructorDecl xd = (X10ConstructorDecl) xnf.ConstructorDecl(p,
                xnf.FlagsNode(p, X10Flags.PUBLIC),
                cDecl.name(),
                fList,                              // formal types
                Collections.<TypeNode>emptyList(),  // throw types
                block);
        xd.typeParameters(cDecl.typeParameters());
        xd.returnType(xnf.CanonicalTypeNode(p, cDef.asType()));

        ConstructorDef xDef = xts.constructorDef(p,
                Types.ref(cDef.asType()),
                X10Flags.PUBLIC,
                frList,                                         // formal types
                Collections.<Ref<? extends Type>>emptyList());  // throw types

        List<ClassMember> cm = new ArrayList<ClassMember>();
        ClassBody cb = cDecl.body();
        cm.addAll(cb.members());
        cm.add(xd.constructorDef(xDef));
        cDef.addConstructor(xDef);
                
        return (X10ClassDecl)cDecl.classDef(cDef).body(cb.members(cm));
    }
    
    
    /**
     * Create a formal from given type/name/flags
     * @param pos
     * @param formalType
     * @param formalName
     * @param formalFlags
     * @return
     */
    public Formal createFormal(Position pos, Type formalType, Name formalName, Flags formalFlags){
        LocalDef ldef = xts.localDef(pos, formalFlags, Types.ref(formalType), formalName);
        Expr ref = xnf.Local(pos, xnf.Id(pos, formalName)).localInstance(ldef.asInstance()).type(formalType);
        Formal f = xnf.Formal(pos, xnf.FlagsNode(pos, formalFlags), 
                xnf.CanonicalTypeNode(pos, formalType), 
                xnf.Id(pos, formalName)).localDef(ldef);
        return f;
    }

    /**
     * 
     * Use the input parameters to generate the corresponding super call with all parameters
     * 
     * @param p
     * @param cDecl
     * @param parmName
     * @param parmType
     * @param parmFlags
     * @param context
     * @return
     * @throws SemanticException
     */
    public Stmt makeSuperCallStatement(Position p, X10ClassDecl cDecl,
                                       List<Name> parmName,
                                       List<Type> parmType,
                                       List<Flags> parmFlags,
                                       X10Context context) throws SemanticException{
        X10ClassDef cDef = (X10ClassDef) cDecl.classDef();
        ClassType cType = cDef.asType();

        // Find the right super constructor: def (args)
        Type sType = cDecl.superClass().type();
        Type scType = PlaceChecker.AddIsHereClause(sType, context);
       
        ConstructorDef sDef = xts.findConstructor(sType,    // receiver's type
                xts.ConstructorMatcher(sType, 
                        Collections.singletonList(scType),  // constraint's type (!)
                        context)).def();
                                
        // reference to formal
        List<Expr> eSuper = new ArrayList<Expr>();      
        
        //find the right 
        for (int i=0; i<parmName.size(); i++) {
            Name pName = parmName.get(i);
            Type pType = parmType.get(i);
            Flags pFlags = parmFlags.get(i);
            // reference
            LocalDef ldef = xts.localDef(p, pFlags, Types.ref(pType), pName);
            Expr ref = xnf.Local(p, xnf.Id(p, pName)).localInstance(ldef.asInstance()).type(pType);
            eSuper.add(ref);
        }

        return xnf.SuperCall(p, eSuper).constructorInstance(sDef.asInstance());
    }
    
    /**
     * Create a copy constructor decl.
     * @param cDecl
     * @param parmName
     * @param parmtype
     * @param parmFlags
     * @param context
     * @return X10ClassDecl
     * @throws SemanticException 
     */ 
    public X10ClassDecl addClassSuperConstructor(Position p,
            X10ClassDecl cDecl,
            List<Name> parmName,
            List<Type> parmType,
            List<Flags> parmFlags,
            X10Context context) throws SemanticException {
       
        Stmt s = makeSuperCallStatement(p, cDecl, parmName, parmType, parmFlags, context);                   
        return addClassConstructor(p, cDecl, parmName, parmType, parmFlags, Collections.singletonList(s), context);
    }	
	/**
     * Create a method decl.
     * @param cDecl
     * @param flag
     * @param returnType
     * @param name 
     * @param formals
     * @param throwTypes
     * @param body
     * @return X10ClassDecl
     * @throws SemanticException 
     */ 
    public X10ClassDecl addClassMethod(Position p, 
            X10ClassDecl cDecl, 
            Flags flag,
            Type returnType, 
            Name name,
            List<Formal> formals,
            List<Type> throwTypes,
            Block body) throws SemanticException {
        
        X10ClassDef cDef = (X10ClassDef) cDecl.classDef();
        
        // Method Decl
        List<TypeNode> throwTypeNodes = new ArrayList<TypeNode>();
        for (Type t : throwTypes) {
            throwTypeNodes.add(xnf.CanonicalTypeNode(p, t));
        }
        FlagsNode flagNode = xnf.FlagsNode(p, flag);
        TypeNode returnTypeNode = xnf.CanonicalTypeNode(p, returnType);
        
        MethodDecl mDecl = xnf.MethodDecl(p, flagNode, returnTypeNode, xnf.Id(p, name), 
                formals, throwTypeNodes, body);
        // Method def
        List<Ref<? extends Type>> formalTypeRefs = new ArrayList<Ref<? extends Type>>();
        List<Ref<? extends Type>> throwTypeRefs = new ArrayList<Ref<? extends Type>>();
        for (Formal f : formals) {
            formalTypeRefs.add(f.type().typeRef());
        }
        for (Type t : throwTypes) {
            throwTypeRefs.add(Types.ref(t));
        }
        MethodDef mDef = xts.methodDef(p, 
                Types.ref(cDef.asType()),                
                flag, 
                Types.ref(returnType), 
                name, 
                formalTypeRefs, 
                throwTypeRefs);
        mDecl = mDecl.methodDef(mDef); //Need set the method def to the method instance
        // Add to Class
        
        cDef.addMethod(mDef);
        List<ClassMember> cm = new ArrayList<ClassMember>();
        cm.addAll(cDecl.body().members());
        cm.add(mDecl);
        ClassBody cb = cDecl.body();
        return (X10ClassDecl) cDecl.classDef(cDef).body(cb.members(cm));
    }
    
    
    
    /**
     * Create a class decl, with no constructor
     * 
     * @param p
     * @param flag
     * @param kind
     * @param name
     * @param supert
     * @param interfaces
     * @param context
     * @return
     * @throws SemanticException
     */
    public X10ClassDecl createClass(Position p, Flags flag, ClassDef.Kind kind, Name name, Type supert,
                                    List<Type> interfaces, X10Context context) throws SemanticException {

        FlagsNode fNode = xnf.FlagsNode(p, flag);
        Id id = xnf.Id(p, name);
        TypeNode superTN = (TypeNode) xnf.CanonicalTypeNode(p, supert);
        List<ClassMember> cmembers = new ArrayList<ClassMember>();
        ClassBody body = xnf.ClassBody(p, cmembers);
        List<TypeNode> interfaceTN = new ArrayList<TypeNode>();
        for (Type t : interfaces) {
            interfaceTN.add((TypeNode) xnf.CanonicalTypeNode(p, t));
        }

        X10ClassDecl cDecl = (X10ClassDecl) xnf.ClassDecl(p, fNode, id, superTN, interfaceTN, body);

        X10ClassDef cDef = (X10ClassDef) xts.createClassDef();
        cDef.name(name);
        cDef.setFlags(flag);
        cDef.kind(kind); // important to set kind

        return (X10ClassDecl) cDecl.classDef(cDef);
    }
    
    
    /**
     * Create a class decl, and add a default constructor to it.
     * Return the class with the default constructor
     * @param flag
     * @param kind : TOP_LEVEL, LOCAL, MEMBER?
     * @param name 
     * @param supert
     * @param interfaces
     * @return X10ClassDecl
     * @throws SemanticException 
     */ 
    public X10ClassDecl createClassWithConstructor(Position p, 
            Flags flag,
            ClassDef.Kind kind,
            Name name,
            Type supert,
            List<Type> interfaces,
            X10Context context) throws SemanticException {

       
        X10ClassDecl cDecl = createClass(p, flag, kind, name, supert, interfaces, context);
        X10ClassDef cDef = (X10ClassDef) cDecl.classDef();
        
        //add default constructor
        X10ConstructorDecl xd = (X10ConstructorDecl) xnf.ConstructorDecl(p,
                xnf.FlagsNode(p, X10Flags.PUBLIC),
                cDecl.name(),
                Collections.<Formal>emptyList(),
                Collections.<TypeNode>emptyList(),
                xnf.Block(p));
        xd.typeParameters(cDecl.typeParameters());
        xd.returnType(xnf.CanonicalTypeNode(p, cDef.asType()));

        ConstructorDef xDef = xts.constructorDef(p,
                Types.ref(cDef.asType()),
                X10Flags.PRIVATE,
                Collections.<Ref<? extends Type>>emptyList(),
                Collections.<Ref<? extends Type>>emptyList());

        List<ClassMember> cm = new ArrayList<ClassMember>();
        cm.add(xd.constructorDef(xDef));
        ClassBody cb = cDecl.body();
        cDef.addConstructor(xDef);
        
        return (X10ClassDecl) cDecl.body(cb.members(cm));
      
    }
              
}
