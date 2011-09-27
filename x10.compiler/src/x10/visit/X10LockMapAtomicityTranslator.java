package x10.visit;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import polyglot.ast.Assign;
import polyglot.ast.Block;
import polyglot.ast.Block_c;
import polyglot.ast.Call;
import polyglot.ast.Catch;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassMember;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.FieldAssign;
import polyglot.ast.FieldDecl;
import polyglot.ast.Field_c;
import polyglot.ast.FlagsNode;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.IntLit;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.Try;
import polyglot.ast.TypeNode;
import polyglot.frontend.Job;
import polyglot.types.ConstructorDef;
import polyglot.types.ContainerType;
import polyglot.types.Context;
import polyglot.types.FieldDef;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.VarDef;
import polyglot.types.VarInstance;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.Async_c;
import x10.ast.AtEach_c;
import x10.ast.AtHomeStmt_c;
import x10.ast.AtStmt_c;
import x10.ast.Atomic_c;
import x10.ast.X10Call;
import x10.ast.X10ClassDecl_c;
import x10.ast.X10ConstructorDecl_c;
import x10.ast.X10LocalDecl_c;
import x10.ast.X10MethodDecl;
import x10.ast.X10MethodDecl_c;
import x10.ast.X10New_c;
import x10.types.EnvironmentCapture;
import x10.types.MethodInstance;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10CodeDef;
import x10.types.X10ConstructorDef;
import x10.types.X10ConstructorInstance;
import x10.types.X10FieldDef;
import x10.types.X10LocalDef;
import x10.types.X10MethodDef;
import x10.types.X10MethodDef_c;
import x10.types.X10ParsedClassType_c;
import x10.util.AltSynthesizer;
import x10.util.Synthesizer;
import x10.util.X10TypeUtils;

/**
 * The visitor class to translate atomic sections to using locks.
 * It only treats atomic section as a unit of work. The atomic section
 * is the unit of work for user-specified atomic sets.
 * 
 * When associating a class with a lock, this visitor inserts a unique
 * (integer) lock id into class instead of directly using a lock field,
 * due to serialization reasons (a lock can not be serialized). The same
 * is for allocating locks for local variables.
 * 
 * The runtime class (x10.util.concurrent.OrderedLock) maintains a global
 * lock map to map each lock id to the lock object. When a lock is needed,
 * an object is query this global lock map to fetch the lock by its id.
 *
 * @author Sai Zhang (szhang@cs.washington.edu)
 *
 */
public class X10LockMapAtomicityTranslator extends ContextVisitor {
	private final Synthesizer synth;
    private final AltSynthesizer altsynth;
	public X10LockMapAtomicityTranslator(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
		synth = new Synthesizer(nf, ts);
        altsynth = new AltSynthesizer(ts, nf);
	}

	//the name suffix for locks allocated for local variables
    private static final String LOCAL_VAR_LOCK = "_var_local_lock";
    //the name suffix for the additional lock parameter of each constructor
    private static final String CONST_FORMAL_NAME = "paramLock";
    private static final Name PARAM_LOCK_NAME = Name.make(CONST_FORMAL_NAME);
    
    //the lock name for the non-static class lock (see <code>visitX10ClassDecl</code>)
	private static final String OBJECT_LOCK_ID = "object_lock_id";
	//the lock name for the static class lock for protecting static methods
	private static final String CLASS_LOCK_ID = "class_lock_id";
	//the lock list name for storing all needed locks (for acquiring or releasing)
	private static final String LOCK_LIST = "_locklist";
    
	//all type names that will be used through the transformation
    private static final QName ORDERED_LOCK = QName.make("x10.util.concurrent.OrderedLock");
    private static final QName ATOMICS = QName.make("x10.util.concurrent.Atomic");
    private static final QName ORDERED_LOCK_ID = QName.make("Int");
    private static final QName X10_LIST = QName.make("x10.util.List"); 
    private static final QName X10_ARRAYLIST = QName.make("x10.util.ArrayList");
    
    //all method names that will be used through the transformation
    //all lock operations are in class x10.util.concurrent.OrderedLock
    private static final Name LIST_ADD = Name.make("add");
    private static final Name GET_ORDERED_LOCK = Name.make("getOrderedLock");
    //the following method are in x10.util.concurrent.OrderedLock class
    private static final Name GET_STATIC_ORDERED_LOCK = Name.make("getStaticOrderedLock");
    private static final Name CREATE_NEW_OBJ_LOCK = Name.make("createNewLock");
    private static final Name GET_INDEX = Name.make("getIndex");
    private static final Name CREATE_OBJ_LOCK = Name.make("createNewLockID");
    private static final Name CREATE_CLASS_LOCK = Name.make("createNewLockID");
    private static final Name GET_OBJ_LOCK = Name.make("getLock");
    private static final Name GET_CLASS_LOCK = Name.make("getLock");
    //the push/pop atomic methods. This must be added when transforming atomic
    //sections to use locks. It is used to make sure async is not inside atomic sections.
    private static final Name PUSH_ATOMIC = Name.make("pushAtomic");
    private static final Name POP_ATOMIC = Name.make("popAtomic");
    //a few shortcuts for lock acquiring.
    //If the number of locks (need to acquire) is <= 3, just use the shortcut,
    //instead of creating a list, adding all locks to the list, and then acquiring
    //all locks. This is useful, if a few locks are frequently acquired and released.
    private static final int SHORTCUT_NUM = 3;
    private static final Name ACQUIRE_LOCKS = Name.make("acquireLocks");
    private static final Name RELEASE_LOCKS = Name.make("releaseLocks");
    private static final Name ACQUIRE_1_LOCK = Name.make("acquireSingleLock");
    private static final Name RELEASE_1_LOCK = Name.make("releaseSingleLock");
    private static final Name ACQUIRE_2_LOCKS = Name.make("acquireTwoLocks");
    private static final Name RELEASE_2_LOCKS = Name.make("releaseTwoLocks");
    private static final Name ACQUIRE_3_LOCKS = Name.make("acquireThreeLocks");
    private static final Name RELEASE_3_LOCKS = Name.make("releaseThreeLocks");
    
    @Override
    public Node override(Node parent, Node n) { 
    	return super.override(parent, n);
    }
    
    @Override
    protected NodeVisitor enterCall(Node parent, Node n) {
    	return this;
    }

    @Override
    public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
    	/**
    	 * For each class, do the following transformations:
    	 * 1. implement the x10.util.concurrent.Atomic interface
    	 * 2. add two lock fields (one static, and other is non-static). The static lock
    	 *    is for protecting (atomic) static methods being accessed by multiple threads,
    	 *    while the non-static one is for protecting class fields and non-atomic
    	 *    methods being accessed by multiple threads.
    	 * 3. remove all atomic notations after the whole class being processed.
    	 * */
    	if(n instanceof X10ClassDecl_c) {
    		X10ClassDecl_c x10clz = (X10ClassDecl_c)n;
    		//remove all atomic notations after replacing atomic sections with proper locks
    		Node node= this.visitX10ClassDecl(x10clz);
    		node = node.visit(new X10AtomicRemover(this.job, this.ts, this.nf));
    		return node;
    	}
    	/**
    	 * Rewrite constructor calls by taking an additional lock argument. Two cases:
    	 *   1. for raw object creation:
    	 *       A a = new A()   becomes  A a = new A(OrderedLock.createNewLock());
    	 *   2. for linked object creation:
    	 *       linked A a = new linked A()  becomes A a = new A(this.getOrderedLock());
    	 **/
    	if(n instanceof X10New_c) {
    		X10New_c x10newc = (X10New_c)n;
    		return this.visitX10New(x10newc);
    	}
    	/**
    	 * 1. transform atomic method by using proper locks
    	 * 2. add lock declaration for each primitive formal parameter, and parameters
    	 *    whose types have not been associated with a lock. e.g., the compiler skips
    	 *    all x10 lib class, like Array, so for the following code which uses Array
    	 *    as a parameter:
    	 *       foo(b:Array[Int] {
    	 *          atomic(b) {...}
    	 *       }
    	 *    the compiler will allocate a local lock for b, such as:
    	 *       foo(b:Array[Int] {
    	 *          lock_for_b: OrderedLock = OrderedLock.createLock();
    	 *          atomic(b) {...}
    	 *       }
    	 *    
    	 *   Please refer to <code>X10TypeUtils.skipProcessingClass</code> to know which
    	 *   classes are skipped
    	 */  
    	if(n instanceof X10MethodDecl_c) {
    		X10MethodDecl_c x10method = (X10MethodDecl_c)n;
    		return this.visitX10Method(x10method);
    	}
    	
    	/**
    	 * Add locks for local variables that are accessed in an atomic section, e.g.,
    	 *   foo() {
    	 *      var a:Int = 0;
    	 *      async { atomic(a) { a++; }}
    	 *      async { atomic(a) { a++; }}
    	 *   }
    	 * To protect var a, a local lock must be added, like:
    	 *   foo() {
    	 *      var a:Int = 0;
    	 *      var lock_for_a:OrderedLock = OrderedLock.createLock();
    	 *      async { atomic(a) { a++; }}
    	 *      async { atomic(a) { a++; }}
    	 *   }
    	 * */
    	if(n instanceof Block_c) {
    		Block_c block = (Block_c)n;
    		return this.visitBlock_c(block);
    	}
    	
    	/**
    	 * Rewrite the atomic section, acquiring the proper locks for it. e.g.,
    	 *    atomic(a, b, this) {  do something }
    	 *       becomes:
    	 *    try {
    	 *       acquire locks for a, b, and this
    	 *       do something
    	 *    } finally {
    	 *       release locks for a, b, and this
    	 *    }
    	 * */
    	if(n instanceof Atomic_c) {
    		Atomic_c atomic = (Atomic_c)n;
    		return this.visitAtomic_c(atomic);
    	}
    	/**
    	 * The following four cases are similar, to add referred lock variable to the
    	 * async/atstmt_c/ateach_c/athomestmt_c closure.
    	 * #FIXME not sure if any other cases are missed
    	 * */
    	if(n instanceof Async_c) {
    		Async_c async = (Async_c)n;
    		return this.visitAsync_c(async);
    	}
    	if(n instanceof AtStmt_c) {
    		AtStmt_c atstmt = (AtStmt_c)n;
    		return this.visitAtStmt_c(atstmt);
    	}
    	if(n instanceof AtEach_c) {
    		AtEach_c ateach = (AtEach_c)n;
    		return this.visitAtEach_c(ateach);
    	}
    	if(n instanceof AtHomeStmt_c) {
    		AtHomeStmt_c athome = (AtHomeStmt_c)n;
    		return this.visitAtHomeStmt_c(athome);
    	}
    	
    	return n;
    }
    
    /*************All below are specific visiting method for transformation*************/
    
    private Node visitX10ClassDecl(X10ClassDecl_c n) throws SemanticException {
    	X10ClassDef classDef = n.classDef();
    	if(X10TypeUtils.skipProcessingClass(classDef.asType())) {
    		return n;
    	}
    	Position pos = n.position();
    	
    	//Let the class implement the x10.util.concurrent.Atomic interface
    	List<TypeNode> interfaces = new LinkedList<TypeNode>();
    	interfaces.addAll(n.interfaces());
		Type atomicInterface = X10TypeUtils.lookUpType(ts, ATOMICS);
		assert atomicInterface != null;
		TypeNode intf = nf.CanonicalTypeNode(pos, atomicInterface);
		interfaces.add(intf);
		n = (X10ClassDecl_c) n.interfaces(interfaces);
    	//if the class is interface, no need to add concrete implemeted method
    	boolean isInterface = (n.flags() != null && n.flags().flags().contains(Flags.INTERFACE));
    	if(isInterface) {
    		return n;
    	}
    	
		//add two lock fields to the class (one non-static, and the other is static), as
    	//well as their corresponding get method.
    	//Note that we can only add the lock id (int type), since a lock can not be serialized.
		List<ClassMember> members = n.body().members();
		X10ClassType containerClass = n.classDef().asType();
		List<ClassMember> newMembers = new LinkedList<ClassMember>();
		Type orderLockClass =  X10TypeUtils.lookUpType(ts, ORDERED_LOCK);
		Type lockTypeId =  X10TypeUtils.lookUpType(ts, ORDERED_LOCK_ID);
		assert lockTypeId != null;
		//create the non-static lock field $object_lock_id
		Name lockIdName = Context.makeFreshName(OBJECT_LOCK_ID);
		FieldDecl lockIdFieldDecl = X10TypeUtils.createFieldDecl(nf, ts, pos, ts.Private(), lockTypeId,
				lockIdName, containerClass, nf.IntLit(pos, IntLit.INT, -1L).type(ts.Int()));
		//create the method getOrderedLock() {return OrderedLock.getObjectLock(this.$object_lock_id); }
		Receiver thiz = nf.This(pos).type(containerClass);
		Expr lockIdField = nf.Field(pos, thiz, lockIdFieldDecl.name()).fieldInstance(lockIdFieldDecl.fieldDef().asInstance()).type(lockTypeId);  
		Expr returnLock = this.synth.makeStaticCall(pos, orderLockClass, GET_OBJ_LOCK,
				Collections.<Expr>singletonList(lockIdField), orderLockClass, this.context());
		Stmt lockRetStmt = nf.Return(pos, returnLock);
		X10MethodDecl getLockMethodDecl = X10TypeUtils.createX10MethodDecl(nf, ts, pos, ts.Public(), containerClass,
				orderLockClass, GET_ORDERED_LOCK, Collections.<Stmt>singletonList(lockRetStmt));
		//add the non-static lock and its get method to the class member list
		newMembers.add(lockIdFieldDecl);
		newMembers.add(getLockMethodDecl);
		
		//create the static lock field  $class_lock_id = OrderedLock.createClassLock();
		Name staticLockIdName = Context.makeFreshName(CLASS_LOCK_ID);
		Expr createClassLock = this.synth.makeStaticCall(pos, orderLockClass, CREATE_CLASS_LOCK, lockTypeId, this.context());
		FieldDecl staticLockFieldDecl = X10TypeUtils.createFieldDecl(nf, ts, pos, ts.Private().Static(), lockTypeId,
				staticLockIdName, containerClass, createClassLock);
		//create the corresponding static method getStaticOrdered { return $class_lock_id; }
		Receiver thizClass = nf.CanonicalTypeNode(pos, containerClass);
    	Expr staticField = nf.Field(pos, thizClass, staticLockFieldDecl.name()).fieldInstance(staticLockFieldDecl.fieldDef().asInstance()).type(lockTypeId);
    	Expr returnClassLock = this.synth.makeStaticCall(pos, orderLockClass, GET_CLASS_LOCK,
    			Collections.<Expr>singletonList(staticField), orderLockClass, this.context());
    	Stmt staticLockRetStmt = nf.Return(pos, returnClassLock);
    	X10MethodDecl staticGetLockMethodDecl = X10TypeUtils.createX10MethodDecl(nf, ts, pos, ts.Public().Static(), containerClass,
    			orderLockClass, GET_STATIC_ORDERED_LOCK, Collections.<Stmt>singletonList(staticLockRetStmt));
    	//add the static lock field and its get method to the class list
		newMembers.add(staticLockFieldDecl);
		newMembers.add(staticGetLockMethodDecl);
		
		//For each constructor, create a copy by adding an additional lock field; and
		//keep other class members untouched. e.g.,
		//  class A {
		//     this() {...}
		//     this(Int) {...}
		//  }
		//     becomes:
     	//  class A {
		//     this() {...}
		//     this(OrderedLock l) {...this.$object_lock = l;}
		//     this(Int) {...}
		//     this(Int, OrderedLock) {...this.$object_lock = l;}
		//  }
		FieldDef fd = lockIdFieldDecl.fieldDef();
		for(ClassMember member : members) {
			newMembers.add(member);
			Position mpos = member.position();
			if(member instanceof X10ConstructorDecl_c) {
				X10ConstructorDecl_c x10const = (X10ConstructorDecl_c)member;
				List<Formal> newFormals = new LinkedList<Formal>();
				for(Formal f : x10const.formals()) {
					newFormals.add(f);
				}
			    //add the new OrderedLock formal  parameter
				Id paramId = nf.Id(mpos, PARAM_LOCK_NAME);
				TypeNode paramTypeNode = nf.CanonicalTypeNode(mpos, orderLockClass);
				LocalDef li = ts.localDef(mpos, Flags.FINAL, Types.ref(orderLockClass), PARAM_LOCK_NAME);
				Formal lockFormal = nf.Formal(mpos, nf.FlagsNode(mpos, Flags.FINAL), paramTypeNode, paramId).localDef(li);
				newFormals.add(lockFormal);
				//create the new constructor def
				//FIXME this may lead to the coonstructor def used in <code>visitNew_c</code>
				//be a different one, and cause problems when doing inner class removal. See <code>InnerClassRemover</code>
				ConstructorDef x10ci = x10const.constructorDef();
				List<Ref<? extends Type>> newFormalTypes = new LinkedList<Ref<? extends Type>>();
				newFormalTypes.addAll(x10ci.formalTypes());
				newFormalTypes.add(Types.ref(orderLockClass));
				//create a def, FIXME this is the root of the above problem, it should use the same def
				ConstructorDef ci = ts.constructorDef(mpos, Types.ref(x10ci.container().get().toClass()),
						x10ci.flags(), newFormalTypes, x10ci.offerType());
				//modifiy the existing constructor body by adding statement this.$lockid = param_lock.getIndex();	
				Block newBody = x10const.body();
				Local local = (Local) nf.Local(mpos, paramId).localInstance(li.asInstance()).type(orderLockClass);
				Receiver localreceiver = local;
	    		X10Call getLockIdCall =
	    			nf.X10Call(pos, localreceiver,
	    				nf.Id(pos, GET_INDEX), Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList());
	    		X10MethodDef methodDef = ts.methodDef(pos, Types.ref((ContainerType)orderLockClass),
			           ts.Public(), Types.ref(lockTypeId), GET_INDEX,
			            Collections.<Ref<? extends Type>>emptyList(),  null);
	    		getLockIdCall = (X10Call) getLockIdCall.type(ts.Int());
	    		getLockIdCall = getLockIdCall.methodInstance(methodDef.asInstance());
				FieldAssign fieldAssign =  nf.FieldAssign(mpos, nf.This(mpos).type(fd.asInstance().container()),
						nf.Id(mpos, fd.name()), Assign.ASSIGN, getLockIdCall);
				fieldAssign = (FieldAssign) fieldAssign.type(fd.asInstance().type());
				fieldAssign = fieldAssign.fieldInstance(fd.asInstance());
				fieldAssign = fieldAssign.targetImplicit(false);
				Eval fieldAssingExpr = nf.Eval(mpos, fieldAssign);
				newBody = newBody.append(fieldAssingExpr);
				
				//create the new constructor, and add to the class member list
				X10ConstructorDecl_c newConstructor = (X10ConstructorDecl_c) nf.ConstructorDecl(mpos,
								x10const.flags(), x10const.name(), newFormals, newBody).constructorDef(ci);
				newMembers.add(newConstructor);
			}
		}
		//set the class body
		ClassBody newBody = n.body().members(newMembers);
		n = (X10ClassDecl_c) n.body(newBody);
		
		return n;
    }
    
    private Node visitX10New(X10New_c n) throws SemanticException {
    	X10ParsedClassType_c clazzType = Types.fetchX10ClassType(n.constructorInstance().returnType());
    	//if the class is not associated with a lock, skip processing its constructor call
    	if(X10TypeUtils.skipProcessingClass(clazzType)) {
    		return n;
    	}
    	
    	Position pos = n.position();
    	Type lockType = X10TypeUtils.lookUpType(ts, ORDERED_LOCK);
    	assert lockType != null;
    	
    	//construct the new constructor that will be called.
    	//FIXME this may lead to a different copy of constructor def as added in <code>visitX10ClassDecl</code>
    	//Note that, at this point, the new constructor has not been added to the AST yet. The visitor
    	//visits the visitX10ClassDecl last.
		X10ConstructorInstance x10ci = n.constructorInstance();
		List<Type> newTypes = new LinkedList<Type>();
		newTypes.addAll(x10ci.formalTypes());
    	newTypes.add(lockType);
    	List<Ref<? extends Type>> refs = X10TypeUtils.typesToTypeRefs(newTypes);
    	X10ConstructorDef cdef = ts.constructorDef(pos, Types.ref(clazzType), x10ci.flags(), refs, x10ci.offerType());
    	X10ConstructorInstance ci = ts.createConstructorInstance(pos, Types.ref(cdef));
    	ci = ci.formalTypes(newTypes);
    	n = (X10New_c) n.constructorInstance(ci);
    	
    	//change the constructor in two different cases
    	//  1. new linked C(args)   ==> new linked C(args,  container.getOrderedLock());
    	//  2. new C(args)  ==>  new C(args, CreateNewLock); 
    	FlagsNode flagNode = n.objectType().getFlagsNode();
    	if(flagNode != null && flagNode.flags().contains(Flags.TYPE_FLAG)) {
    		List<Expr> newArguments = new LinkedList<Expr>();
    		newArguments.addAll(n.arguments());
    		//if linked in a static method, it needs to use the static class lock,
    		//otherwise, use the object lock.
    		boolean isInStaticMethod = this.isInStaticMethod();
    		X10Call getLockCall = null;
    		X10MethodDef methodDef = null;
    		if(!isInStaticMethod) {
    		    getLockCall = nf.X10Call(pos, nf.This(pos).type(ci.container()),
    				nf.Id(pos, GET_ORDERED_LOCK), Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList());
			    methodDef = ts.methodDef(n.position(), Types.ref(clazzType),
		           ts.Public(), Types.ref(lockType), GET_ORDERED_LOCK,
		            Collections.<Ref<? extends Type>>emptyList(),  null);
    		} else {
    			getLockCall = nf.X10Call(pos, nf.CanonicalTypeNode(pos, ci.container()), nf.Id(pos, GET_STATIC_ORDERED_LOCK),
    					Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList());
    			methodDef = ts.methodDef(pos, Types.ref(clazzType),
      		           ts.Public(), Types.ref(lockType), GET_STATIC_ORDERED_LOCK,
   		            Collections.<Ref<? extends Type>>emptyList(),  null);  
    		}
    		getLockCall = getLockCall.methodInstance(methodDef.asInstance());
    		Expr getLockExpr = getLockCall.type(lockType);
    		newArguments.add(getLockExpr);
    		n = (X10New_c)n.arguments(newArguments);
    	} else {
	    	List<Expr> newArguments = new LinkedList<Expr>();
	    	newArguments.addAll(n.arguments());
	    	Call createCall = this.synth.makeStaticCall(pos, lockType, CREATE_NEW_OBJ_LOCK, lockType, this.context());
	    	newArguments.add(createCall);
	    	n = (X10New_c) n.arguments(newArguments);
    	}
    	
    	return n;
    }
    
    private Node visitX10Method(X10MethodDecl_c n) throws SemanticException {
    	if(n.body() == null) { //skip abstract method
    		return n;
    	}
    	List<Stmt> newStatements = new LinkedList<Stmt>();
    	Type lockType = X10TypeUtils.lookUpType(ts, ORDERED_LOCK);
    	Type lockIdType = X10TypeUtils.lookUpType(ts, ORDERED_LOCK_ID);
    	
    	//get a list of LocalDefs that are accessed in var declarations of the atomic section
    	//that is get the "vars" from the "atomic(vars) {...}"
    	AtomicLocalAndFieldAccessVisitor visitor = new AtomicLocalAndFieldAccessVisitor();
    	n.body().visit(visitor);
    	Set<X10LocalDef> localsInAtomics = visitor.localsInAtomic;
    	
    	//For method parameters that have no lock associated, but is still accessed
    	//in the atomic section, the compiler needs to allocate a lock for it, such as:
    	//   foo(b:Array[Int] {
    	//      atomic(b) {...}
    	//   }
    	//      becomes:
        //   foo(b:Array[Int] {
    	//      lock_for_b:OrderedLock = OrderedLock.createNewLock();
    	//      atomic(b) {...}
    	//   }
    	List<Formal> formals = n.formals();
    	for(Formal formal : formals) {
    		//if the parameter is not accessed from an atomic section, no lock is needed
    		if(!localsInAtomics.contains(formal.localDef())) {
    			continue;
    		}
		    Position pos = formal.position();
    		Type type = formal.type().type();
    		X10ParsedClassType_c clazzType = Types.fetchX10ClassType(type);
    		//skip the parameter with non-class type like parameterized type: T t
    		if(clazzType == null) {
    			continue;
    		}
    		//allocate lock for parameters that are not associated with a lock
    		if(X10TypeUtils.skipProcessingClass(type)) {
       		    //create new OrderedLock();
        	    Expr newLockId = this.synth.makeStaticCall(pos, lockType, CREATE_OBJ_LOCK, lockIdType, this.context());
        	    Name lockName = Context.makeFreshName( getLocalVarLockName(formal.name().toString()));
                final LocalDecl ld = X10TypeUtils.createLocalDecl(nf, ts, pos, ts.Final(), lockIdType, lockName, newLockId);
                newStatements.add(ld);
    		}
    	}
		//add existing statements
    	newStatements.addAll(n.body().statements());
        Block newBlock = nf.Block(n.position(), newStatements);
        
        /**
         * The following code is to transform atomic method to use locks
         * */
        if(n.flags().flags().contains(Flags.ATOMIC)) {
        	List<X10LocalDef> x10formalThatHaveLocks = new LinkedList<X10LocalDef>();
        	for(Formal formal : formals) {
        		LocalDef def = formal.localDef();
        		if(!X10TypeUtils.skipProcessingClass(def.type().get())) {
        			x10formalThatHaveLocks.add((X10LocalDef)def);
        		}
        	}
        	boolean isStatic = n.flags().flags().contains(Flags.STATIC);
        	Position pos = n.position();
        	//compute the total number of locks need to acquire. For each atomic method,
        	//the compiler need to acquire all associated locks from its formal parameter,
        	//and the this.lock. (or the static class lock, depends on whether the current
        	//code static or not)
        	int lockNum = x10formalThatHaveLocks.size() + 1; //number of formal + this lock
        	//must add these 2 calls to make sure no async is inside atomic
    		Stmt pushAtomic = nf.Eval(pos, call(pos, PUSH_ATOMIC, ts.Void()));
    		Stmt popAtomic = nf.Eval(pos, call(pos, POP_ATOMIC, ts.Void()));
    		//if there is only a few locks (<=3) need to acquire, we use shortcut methods,
    		//otherwise, we creates a lock list to store locks.
        	if(lockNum <= SHORTCUT_NUM) {
        		Stmt lockAcquireStmt = this.acquireLocksAtOnce(pos, lockType, null, lockNum,
        				x10formalThatHaveLocks, Collections.<LocalDef>emptyList(), isStatic, true);
        		Stmt lockReleaseStmt = this.releaseLocksAtOnce(pos, lockType, null, lockNum,
        				x10formalThatHaveLocks, Collections.<LocalDef>emptyList(), isStatic, true);
        		
        		//create the try catch block, translate the method like:
        		// method {  body }  =>  method { try {acquire all locks, body } finally {release all locks} }
        		// more specifically (suppose only 1 lock is needed to acquire):
        		//  method { body }
        		//     becomes:
        		//  method {
        		//    try {
        		//       OrderedLock.acquireSingleLock(lock);
        		//       body;
        		//    } finally {
        		//       OrderedLock.releaseSingleLock(lock);
        		//    }
        		//  }
        		List<Stmt> tryStatements = new LinkedList<Stmt>();
        		tryStatements.add(lockAcquireStmt);
        		tryStatements.add(pushAtomic);
        		tryStatements.addAll(newBlock.statements());
        		
        		Block tryBlock = nf.Block(pos, tryStatements);
            	Block finallyBlock = nf.Block(pos, popAtomic, lockReleaseStmt);
            	Try tryStmt = nf.Try(pos, tryBlock, Collections.<Catch>emptyList(), finallyBlock);
            	
            	List<Stmt> statements = new LinkedList<Stmt>();
            	statements.add(tryStmt);
            	
            	newBlock = nf.Block(pos, statements);
        	} else {
        		//create a list object, and put all locks in it, it will produce the code like:
        		// method { body }
        		//    becomes:
        		// method {
        		//    lockList:List[OrderedLock] = new ArrayList[OrderedLock];
        		//    lockList.add(lock1);
        		//    lockList.add(lock2);
        		//    ...
        	    //    try {
        		//       OrderedLock.acquireLocks(lockList);
        		//         body
        		//    } finally {
        		//       OrderedLock.releaseLocks(lockList);
        		//    }
            	LocalDecl emptyLockList = this.createEmptyLockList(pos);
            	Stmt acquireClassLock = this.addClassLockToLockList(pos, lockType, emptyLockList.name(), emptyLockList, isStatic);
            	List<Stmt> addMethodFormalsToLockStmts = this.addLocksForFormals(pos, lockType, x10formalThatHaveLocks,
        				emptyLockList.name(), emptyLockList);
            	Stmt lockAcquireStmt = this.lockAcquireStatement(pos, lockType, emptyLockList, emptyLockList.name());
        		Stmt lockReleaseStmt = this.lockReleaseStatement(pos, lockType, emptyLockList, emptyLockList.name());
            	
        		//assemble the try-catch code block
            	List<Stmt> tryStatements = new LinkedList<Stmt>();
            	tryStatements.add(lockAcquireStmt);
            	tryStatements.add(pushAtomic);
            	tryStatements.addAll(newBlock.statements());
            	
            	Block tryBlock = nf.Block(pos, tryStatements);
            	Block finallyBlock = nf.Block(pos, popAtomic, lockReleaseStmt);
            	Try tryStmt = nf.Try(pos, tryBlock, Collections.<Catch>emptyList(), finallyBlock);
            	
            	List<Stmt> statements = new LinkedList<Stmt>();
            	statements.add(emptyLockList);
            	statements.add(acquireClassLock);
            	statements.addAll(addMethodFormalsToLockStmts);
            	statements.add(tryStmt);
            	
            	//create a new block here
            	newBlock = nf.Block(pos, statements);
        	}
        	
        	//remove the atomic flag in method
        	n = n.flags(nf.FlagsNode(pos, n.flags().flags().clearAtomic()));
//        	System.out.println("process atomic method in X10LinkedAtomicityTranslator, pos: " + n.position());
//        	System.out.println("  remove atomic: " + n.position());
        }
    	
    	return n.body(newBlock);
    }
    
    private Node visitBlock_c(Block_c block) throws SemanticException {
    	List<Stmt> statements = block.statements();
    	//use a visitor to fetch all local vars that are declared in an atomic section
    	//e.g.,  all locals in "vars":  atomic(vars) {...}
    	//Note, vars can also be any fields, formal parameters. 
    	AtomicLocalAndFieldAccessVisitor visitor = new AtomicLocalAndFieldAccessVisitor();
    	block.visit(visitor);
    	
    	//use a visitor to fetch all lock locals that are used to protect each local var
    	//NOTE: this is confusing, because at this point, the atomic section has already
    	//       been visited before the locks are allocated. So, we need to fetch the
    	//       exactly lock def from the atomic section, and declare the lock for it.
    	X10AtomicLockLocalCollector collector = new X10AtomicLockLocalCollector();
    	block.visit(collector);
    	
    	//all local vars used defined in atomic section
    	Set<X10LocalDef> localsInAtomic = visitor.localsInAtomic; 
    	//all lock locals that are already been used
    	Set<LocalDef> lockLocalDefs = collector.getLockLocals();
    	
    	//create a list of statements, adding a lock for each atomic field
    	Type lockType = X10TypeUtils.lookUpType(ts, ORDERED_LOCK);
    	Type lockIdType = X10TypeUtils.lookUpType(ts, ORDERED_LOCK_ID);
    	List<Stmt> newStatements = new LinkedList<Stmt>();
    	for(Stmt stmt : statements) {
    		newStatements.add(stmt);
    		//create a lock declaration statement for each local variable accessed in the atomic section
    		//#FIXME is this complete?
    		if(stmt instanceof X10LocalDecl_c) {
    			//only allocate a lock for the var if it is accessed in an atomic section
    			X10LocalDecl_c localDecl = (X10LocalDecl_c)stmt;
    			if(localsInAtomic.contains(localDecl.localDef())) 
    			{
    				LocalDef def = findLockLocalDef(localDecl.localDef().name().toString(), lockLocalDefs);
    				assert def != null;
    				Position pos = stmt.position();
    				//for local associated with locks, just grab its lock via "getOrderedLock"
    				//otherwise, allocate a lock for it.
    				Type localDefType = localDecl.declType();
    				Expr lockExpression = null; //return an int
    				if(X10TypeUtils.isSkippedOrPrimitiveType(localDefType)) {
    					//the variable is not associated with a lock, then create a lock for it
    					//it creates code:  lock_id:Int = OrderedLock.createNewLockID(); 
    					//Note cannot create a lock object here, since Lock is not serializable.
    				    lockExpression = this.synth.makeStaticCall(pos, lockType, CREATE_OBJ_LOCK, lockIdType, this.context());
    				} else {
    					//otherwise, grab a lock by creating the code like:
    					// lock_id = OrderedLock.getIndex(local.getOrderedLock());
    	                Local local = (Local) nf.Local(pos, localDecl.name()).type(localDefType);
    	                local = local.localInstance(localDecl.localDef().asInstance());
    	                X10Call getLockCall = nf.X10Call(pos,  local,
    	        				nf.Id(pos, GET_ORDERED_LOCK), Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList());
    	                getLockCall = (X10Call) getLockCall.type(lockType);
    	    			X10MethodDef methodDef = ts.methodDef(pos, Types.ref(Types.fetchX10ClassType(localDefType)),
    	    		           ts.Public(), Types.ref(lockType), GET_ORDERED_LOCK,
    	    		            Collections.<Ref<? extends Type>>emptyList(),  null);
    	        		getLockCall = getLockCall.methodInstance(methodDef.asInstance());
    	        		X10Call getIndexCall = nf.X10Call(pos,  getLockCall,
    	        				nf.Id(pos, GET_INDEX), Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList());
    	        		getIndexCall = (X10Call)getIndexCall.type(lockIdType);
    	        		X10MethodDef getIndexMethodDef = ts.methodDef(pos, Types.ref((ContainerType)lockType),
     	    		           ts.Public(), Types.ref(lockIdType), GET_INDEX,
     	    		            Collections.<Ref<? extends Type>>emptyList(),  null);
    	        		getIndexCall = getIndexCall.methodInstance(getIndexMethodDef.asInstance());
    					lockExpression = getIndexCall;
    				}
    				
    				assert lockExpression != null;
    		    	//create a fresh local variable to store the lock id
    		    	Name lockName = Context.makeFreshName( getLocalVarLockName(localDecl.name().toString()));
    		        final Id varId = nf.Id(pos, def.name());
    		        final LocalDecl ld = nf.LocalDecl(pos, nf.FlagsNode(pos, ts.Final()),
    		        		nf.CanonicalTypeNode(pos, def.type().get()), varId, lockExpression).
    		        		localDef(def);
    		        
                    newStatements.add(ld);
    			}
    		}
    	}
    	//change the statements in the block
    	block = (Block_c) block.statements(newStatements);
    	
    	return block;
    }
    
    //Grab locks for the atomic section. For the sake of efficiency, if the locks need to grab
    //is less than a defined value (SHORTCUT_NUM), we grab locks directly using the shortcut
    //methods inside class OrderedLock; otherwise, we first create a list, put all locks to that
    //list, and then grab all locks in the list. The later case is much slower when (only a few)
    //locks are frequently grabbed and released.
    private Node visitAtomic_c(Atomic_c atomic) throws SemanticException {
    	Stmt body = atomic.body; //it is a block_c type for sure
    	//return if there are no variables associated with this atomic section
    	//that is, using the old-fashion atomic set:  atomic {...}
    	if(atomic.identifiers == null || atomic.identifiers.isEmpty()) {
    		return atomic;
    	}
    	//for the atomic section of new syntax: atomic(vars) {...}. We get formal parameters
    	//that are used in "vars", the local variables in "vars", and all "fields" in vars.
    	//We get a list formal parameter names of the current context. Using name for comparison
    	//is sufficient.
    	List<String> formalNames = X10TypeUtils.getAllFormalNames(this.context.currentCode());
    	//get the list of local and formal vars (in x10 compiler, these 2 kinds of vars are not distinguished)
    	List<X10LocalDef> localAndFormalInAtomic = atomic.getLocalsInAtomic();
    	//get the fields
    	List<X10FieldDef> fieldsInAtomic = atomic.getFieldsInAtomic();
    	//divided the local def into two parts: 
    	//  1. locals and formal parameters that are not associated with locks
    	//  2. formal parameters than are associated with locks
    	//  3. fields that have associated with locks
    	//Here is an example:
    	//   class Bar {
    	//     var d:Int = 1;
    	//     var e:A = new A();
    	//     public def foo(a:A, b:Array[Int]) {
    	//       var c:Int = 0;
    	//       atomic(a, b, c, d, e) {... do something ..} 
    	//     }
    	//  }
    	//Before classification:
    	//   localAndFormalInAtomic = {a, b, c}
    	//   fieldsInAtomic = {d, e}
    	//After classification:
    	//   formalsThatHaveLocks = {a}
    	//   localsAndFormalWithoutLock = {d, b}
    	//   fieldsHaveLock = {e}
    	List<X10LocalDef> localsAndFormalWithoutLock = new LinkedList<X10LocalDef>();
    	List<X10LocalDef> formalsThatHaveLocks = new LinkedList<X10LocalDef>();
    	for(X10LocalDef localdef : localAndFormalInAtomic) {
    		if(formalNames.contains(localdef.name().toString())
    				&& /*have lock*/ !X10TypeUtils.skipClassByName(localdef.type().get().toString())) {
    			formalsThatHaveLocks.add(localdef);
    		} else {
    			localsAndFormalWithoutLock.add(localdef);
    		}
    	}
    	List<X10FieldDef> fieldsHaveLock = new LinkedList<X10FieldDef>();
    	for(X10FieldDef fieldDef : fieldsInAtomic) {
    		//add to the fields that have lock
    		if(X10TypeUtils.isPrimitiveType(fieldDef.type().get())
    				|| X10TypeUtils.skipClassByName(fieldDef.type().get().fullName().toString())) {
    			continue;
    		} else {
    			fieldsHaveLock.add(fieldDef);
    		}
    	}
    	//check if any fields without associated locks are accessed. if so, we must
    	//acquire the class lock for protection. Also, if the programmers explicitly
    	//specify this inside atomic section, the class lock should aslo be acquired.
    	//Note that, the static class lock is never needed, since all static fields in
    	//X10 are final.
    	boolean accessPrimitiveOrSkippedClass = false;
    	if(fieldsHaveLock.size() != fieldsInAtomic.size() || atomic.hasThisInAtomic()) {
    		accessPrimitiveOrSkippedClass = true;
    	}
    	
    	Position pos = atomic.position();
    	Type lockType = X10TypeUtils.lookUpType(ts, ORDERED_LOCK);
    	Type lockIdType = X10TypeUtils.lookUpType(ts, ORDERED_LOCK_ID);
    	//the lock number we need to acquire
    	int lockNum = localsAndFormalWithoutLock.size() + formalsThatHaveLocks.size() + (accessPrimitiveOrSkippedClass ? 1 : 0);
    	//create the atomic section push/pop statements to avoid use async inside an atomic section
		Stmt pushAtomic = nf.Eval(pos, call(pos, PUSH_ATOMIC, ts.Void()));
		Stmt popAtomic = nf.Eval(pos, call(pos, POP_ATOMIC, ts.Void()));
		//If only a few locks are needed, use the shortcut method to acquire directly.
		//  It generated the code like:
		//    OrderedLock.acquireSingleLock(lock1) (or acquire 2, 3 locks depends on the lockNum)
		//
		//Else, we first create a lock list, add every lock, and grab it. It generates code like:
		//  lockList:List[OrderedLock] = new ArrayList[OrderedLock]();
		//  lockList.add(l1);
		//  lockList.add(l2); ....
		//  OrderedLock.acquireLocks(lockList);
    	if(lockNum <= SHORTCUT_NUM) {
    		List<LocalDef> lockDefsForVarsWithoutLock = new LinkedList<LocalDef>();
    		for(X10LocalDef localWithoutLock : localsAndFormalWithoutLock) {
    			Name localLockName = Context.makeFreshName( getLocalVarLockName(localWithoutLock.name().toString()));
        		Id localLockId = nf.Id(pos, localLockName);
                final LocalDef localLockDef = ts.localDef(pos, ts.Final(), Types.ref(lockIdType), localLockName);
                lockDefsForVarsWithoutLock.add(localLockDef);
    		}
    		//create lock acquire statement, e.g., OrderedLock.acquireSingleLock(lock1);
    		Stmt acquireAllLocks = this.acquireLocksAtOnce(pos, lockType, lockIdType, lockNum, formalsThatHaveLocks, lockDefsForVarsWithoutLock,
    				false, accessPrimitiveOrSkippedClass);
    		//create lock release statement, e.g., OrderedLock.releaseSingeLock(lock1);
    		Stmt releaseAllLocks = this.releaseLocksAtOnce(pos, lockType, lockIdType, lockNum, formalsThatHaveLocks, lockDefsForVarsWithoutLock,
    				false, accessPrimitiveOrSkippedClass);
        	List<Stmt> tryStatements = new LinkedList<Stmt>();
        	tryStatements.add(acquireAllLocks);
        	//must push atomic, to make sure no async inside atomic
        	tryStatements.add(pushAtomic);
        	tryStatements.add(atomic.body());
        	Block tryBlock = nf.Block(pos, tryStatements);
        	Block finallyBlock = nf.Block(pos, popAtomic, releaseAllLocks);
        	Try tryStmt = nf.Try(pos, tryBlock, Collections.<Catch>emptyList(), finallyBlock);
        	List<Stmt> statements = new LinkedList<Stmt>();
        	statements.add(tryStmt);
        	
        	Block newBlock = nf.Block(pos, statements);
        	atomic = (Atomic_c) atomic.body(newBlock);
    	} else {
    		//it creates: lockList:List[OrderedLock] = new ArrayList[OrderedLock]();
    		LocalDecl locklist = this.createEmptyLockList(pos);
    		//add the compiler-allocated locks for locals/formals without locks to the lock list
    		//it creates:  lockList.add(OrderedLock.getLock(localAndFormalWithoutLock))
        	List<Stmt> grabLockForVarsWithoutLocks = this.addLocalDefToList(pos, lockType, lockIdType, localsAndFormalWithoutLock,
    				locklist.name(), locklist);
    		//add the locks associated with formals to the lock list.
        	//it creates:  lockList.add(formalsThatHaveLock.getOrderedLock());
    		List<Stmt> grabLockForFormalsWithLocks = this.addLocksForFormals(pos, lockType, formalsThatHaveLocks,
    				locklist.name(), locklist);

    		//We need to grab this lock, if (1) this is included inthe atomic section, (2) some primitive
    		//   fields or skipped fields are accessed in the atomic section
    		//note: we do not need to grab the static lock, in X10 all static fields are by default final.
    		Stmt grabThisLock = null;
    		if(accessPrimitiveOrSkippedClass || atomic.hasThisInAtomic()) {
    			grabThisLock = this.addClassLockToLockList(pos, lockType, locklist.name(), locklist, false);
    		}

    		//adds locks for fields that have locks
    		//it creates: lockList.add(fieldsHaveLock.getOrderedLock);
    		List<Stmt> grabLockForFieldWithLocks = this.addFieldLockToLockList(pos, lockType,
    				fieldsHaveLock, locklist.name(), locklist);
    		
    		//create the lock acquiring and releasing statements
    		//it creates:  OrderedLock.acquireLocks(lockList)
    		//             OrderedLock.releaseLocks(lockList);
    		Stmt lockAcquireStmt = this.lockAcquireStatement(pos, lockType, locklist, locklist.name());
    		Stmt lockReleaseStmt = this.lockReleaseStatement(pos, lockType, locklist, locklist.name());
    		
    		//assemble the code block
        	List<Stmt> tryStatements = new LinkedList<Stmt>();
        	tryStatements.add(lockAcquireStmt);
        	tryStatements.add(pushAtomic);
        	tryStatements.add(atomic.body());
        	
        	Block tryBlock = nf.Block(pos, tryStatements);
        	Block finallyBlock = nf.Block(pos, popAtomic, lockReleaseStmt);
        	Try tryStmt = nf.Try(pos, tryBlock, Collections.<Catch>emptyList(), finallyBlock);
        	
        	List<Stmt> statements = new LinkedList<Stmt>();
        	statements.add(locklist);
        	statements.addAll(grabLockForVarsWithoutLocks);
        	statements.addAll(grabLockForFormalsWithLocks);
        	if(grabThisLock != null) {
        	    statements.add(grabThisLock);
        	}
        	statements.addAll(grabLockForFieldWithLocks);
        	statements.add(tryStmt);
        	
        	Block newBlock = nf.Block(pos, statements);
        	atomic = (Atomic_c) atomic.body(newBlock);
    	}
    	
    	//System.out.println("process atomic in X10LockMapAtomicityTranslator, pos: " + pos);
    	
    	return atomic; //note, will get rid of atomic notation after visiting all classes
    }
    
    /**
     * The following 4 methods are largely repetitive. They adds the newly-added lock variable
     * into the environment of an Async_c, AtStmt_c, AtEachc, and AtHomeStmt_c
     * */
    private Node visitAsync_c(Async_c async) {
    	//only fetch locals that are not defined inside async, but used in atomic sections
    	X10AtomicLockLocalCollector collector = new X10AtomicLockLocalCollector();
    	async.visit(collector);
    	Set<LocalDef> localLocks = collector.getEscapedLockLocals();
    	
    	List<VarInstance<? extends VarDef>> referredVars = new LinkedList<VarInstance<? extends VarDef>>();
    	Position pos = async.position();
    	//must use the same reference! since in ClosureRemover, it use Object.equals for comparison
    	for(LocalDef localDef : localLocks) {
    		referredVars.add(localDef.asInstance());
    	}
    	//important, do not forget to add captured environment element
    	List<VarInstance<? extends VarDef>> existingvars = async.asyncDef().capturedEnvironment();    	
    	List<VarInstance<? extends VarDef>> vars = new LinkedList<VarInstance<? extends VarDef>>();
    	vars.addAll(referredVars);
    	vars.addAll(existingvars);
    	//MUST explicitly set the environment variables
    	async.asyncDef().setCapturedEnvironment(vars);
    	
    	return async;
    }
    private Node visitAtStmt_c(AtStmt_c atStmt) {
    	List<VarInstance<? extends VarDef>> vars = new LinkedList<VarInstance<? extends VarDef>>();
    	//the newly-referred vars
    	List<VarInstance<? extends VarDef>> referredVars = new LinkedList<VarInstance<? extends VarDef>>();
    	
    	X10AtomicLockLocalCollector collector = new X10AtomicLockLocalCollector();
    	atStmt.visit(collector);
    	Set<LocalDef> localLocks = collector.getEscapedLockLocals(); 
    	for(LocalDef localDef : localLocks) {
    		referredVars.add(localDef.asInstance());
    	}
    	
    	vars.addAll(referredVars);
    	vars.addAll(atStmt.atDef().capturedEnvironment());
    	
    	atStmt.atDef().setCapturedEnvironment(vars);
    	return atStmt;
    }
    private Node visitAtEach_c(AtEach_c ateach) {
    	List<VarInstance<? extends VarDef>> vars = new LinkedList<VarInstance<? extends VarDef>>();
    	//the newly-referred vars
    	List<VarInstance<? extends VarDef>> referredVars = new LinkedList<VarInstance<? extends VarDef>>();
    	
    	X10AtomicLockLocalCollector collector = new X10AtomicLockLocalCollector();
    	ateach.visit(collector);
    	Set<LocalDef> localLocks = collector.getEscapedLockLocals(); 
    	for(LocalDef localDef : localLocks) {
    		referredVars.add(localDef.asInstance());
    	}
    	
    	vars.addAll(referredVars);
    	vars.addAll(ateach.atDef().capturedEnvironment());
    	
    	ateach.atDef().setCapturedEnvironment(vars);
    	return ateach;
    }
    private Node visitAtHomeStmt_c(AtHomeStmt_c athome) {
    	List<VarInstance<? extends VarDef>> vars = new LinkedList<VarInstance<? extends VarDef>>();
    	//the newly-referred vars
    	List<VarInstance<? extends VarDef>> referredVars = new LinkedList<VarInstance<? extends VarDef>>();
    	
    	X10AtomicLockLocalCollector collector = new X10AtomicLockLocalCollector();
    	athome.visit(collector);
    	Set<LocalDef> localLocks = collector.getEscapedLockLocals(); 
    	for(LocalDef localDef : localLocks) {
    		referredVars.add(localDef.asInstance());
    	}
    	
    	vars.addAll(referredVars);
    	vars.addAll(athome.atDef().capturedEnvironment());
    	
    	athome.atDef().setCapturedEnvironment(vars);
    	return athome;
    }
    
    //Create the name for a local lock variable
    public static String getLocalVarLockName(String localName) {
    	return localName + LOCAL_VAR_LOCK;
    }
    
    //Check if the given name is a local lock variable?
    public static boolean isLocalLock(String name) {
    	return name.indexOf(LOCAL_VAR_LOCK) != -1;
    }
    
    //To see if a set of LocalDefs contain a given variable
    //It just checks by name currently.
    private LocalDef findLockLocalDef(String varName, Set<LocalDef> lockDefs) {
    	String lockName = getLocalVarLockName(varName);
    	for(LocalDef lockDef : lockDefs) {
    		if(lockDef.name().toString().indexOf(lockName) != -1) {
    			return lockDef;
    		}
    	}
    	return null;
    }
    
    //Create an empty lock list declaration
    //  var lockList:List[OrderedLock] = new ArrayList[OrderedLock]();
    private LocalDecl createEmptyLockList(Position pos) {
    	Type lockType = X10TypeUtils.lookUpType(ts, ORDERED_LOCK);
    	X10ParsedClassType_c listType = (X10ParsedClassType_c)X10TypeUtils.lookUpType(ts, X10_LIST);
    	listType = (X10ParsedClassType_c) listType.typeArguments(Collections.<Type>singletonList(lockType));
    	X10ParsedClassType_c arrayListType = (X10ParsedClassType_c)X10TypeUtils.lookUpType(ts, X10_ARRAYLIST);
    	arrayListType = (X10ParsedClassType_c)arrayListType.typeArguments(Collections.<Type>singletonList(lockType));
    	TypeNode arrayListTypeNode = nf.CanonicalTypeNode(pos, arrayListType);
        X10ConstructorInstance arrayListCI = X10TypeUtils.findDefaultConstructor(ts, this.context, arrayListType);
    	Expr newArrayList = nf.New(pos, arrayListTypeNode,
    			Collections.<Expr>emptyList()).constructorInstance(arrayListCI).type(lockType);
    	Name listName = Name.make(Context.getNewVarName().toString() + LOCK_LIST);
    	Id listID = nf.Id(pos, listName);
    	LocalDef li = ts.localDef(pos, ts.Final(), Types.ref(listType), listName);
        LocalDecl emptyList = nf.LocalDecl(pos,
        		nf.FlagsNode(pos, ts.Final()), nf.CanonicalTypeNode(pos, listType),
        		listID, newArrayList).localDef(li);
        return emptyList;
    }
    
    //Add locks associated with the local var declarations to the lock.
    //Note the local def here is int type representing a lock id, the generated code looks like:
    //  lockList.add(OrderedLock.getLock(localdef));
    //The lockList parameter is the lock list to which locks are added to
    private List<Stmt> addLocalDefToList(Position pos, Type lockType, Type lockIdType, List<X10LocalDef> localDefs,
    		Id listID, LocalDecl lockList) throws SemanticException {
    	List<Stmt> stmts = new LinkedList<Stmt>();
    	X10ParsedClassType_c listType = (X10ParsedClassType_c)X10TypeUtils.lookUpType(ts, X10_LIST);
    	listType = (X10ParsedClassType_c) listType.typeArguments(Collections.<Type>singletonList(lockType));
    	MethodInstance addmi = X10TypeUtils.findMethod(ts, this.context, listType, LIST_ADD,
				Collections.<Type>singletonList(nf.X10CanonicalTypeNode(pos, lockType).type()));
    	for(LocalDef localDef : localDefs) {
    		//create method call: OrderedLock.getLock(local)
    		Name localLockName = Context.makeFreshName( getLocalVarLockName(localDef.name().toString()));
    		Id localLockId = nf.Id(pos, localLockName);
            final LocalDef localLockDef = ts.localDef(pos, ts.Final(), Types.ref(lockIdType), localLockName);
    		final Local localid = (Local) nf.Local(pos, localLockId).localInstance(localLockDef.asInstance()).
    		             type(lockIdType);
    		Call objLock = this.synth.makeStaticCall(pos, lockType, GET_OBJ_LOCK, Collections.<Expr>singletonList(localid.type(lockIdType)),
    				lockType, this.context());
    		//add the lock to the list, create: lockList.add(OrderedLock.getLock(local));
    		Local listLocal = nf.Local(pos, listID).localInstance(lockList.localDef().asInstance());
    		Receiver receiver = listLocal.type(lockList.type().type());
        	List<TypeNode> typeArgs = new LinkedList<TypeNode>();
        	typeArgs.add(nf.CanonicalTypeNode(pos, lockType));
        	List<Expr> args = Collections.<Expr>singletonList(objLock.type(lockType));
        	Id addID = nf.Id(pos, LIST_ADD);
        	X10Call addLockCall = nf.X10Call(pos, receiver, addID, typeArgs, args);
        	addLockCall = (X10Call) addLockCall.type(ts.Boolean());
        	List<Ref<? extends Type>> addArgTypes = new LinkedList<Ref<? extends Type>>();
        	addArgTypes.add(Types.ref(lockType));
        	addLockCall = addLockCall.methodInstance(addmi);
        	//add the lock acquire statement
        	Stmt addLock = nf.Eval(pos, addLockCall);
        	stmts.add(addLock);
    	}
    	return stmts;
    }
    
    //Add the locks associated with formal parameters tot he locklist. Note, each formalDef must associate
    //with an OrderedLock. The generated code looks like:
    //  lockList.add(formalDef.getOrderedLock());
    //The lockList parameter is the lock list to which locks are added to
    //FIXME much of the code is identical with <code>addFieldLockToLockList</code>. but not easy to remove such cloned code.
    private List<Stmt> addLocksForFormals(Position pos, Type lockType, List<X10LocalDef> formalDefs, Id listID,
    		LocalDecl lockList) {
    	List<Stmt> stmts = new LinkedList<Stmt>();
    	X10ParsedClassType_c listType = (X10ParsedClassType_c)X10TypeUtils.lookUpType(ts, X10_LIST);
    	listType = (X10ParsedClassType_c) listType.typeArguments(Collections.<Type>singletonList(lockType));
    	MethodInstance addmi = X10TypeUtils.findMethod(ts, this.context, listType, LIST_ADD,
				Collections.<Type>singletonList(nf.X10CanonicalTypeNode(pos, lockType).type()));
    	for(LocalDef localDef : formalDefs) {
    		pos = localDef.position();
    		Type localType = localDef.type().get();
    		assert localType instanceof ContainerType;
    		ContainerType clazzType = (ContainerType)localType;
    		Local l = nf.Local(pos, nf.Id(pos, localDef.name())).localInstance(localDef.asInstance());
    		
    		Receiver localreceiver = l.type(clazzType);
    		X10Call getLockCall = nf.X10Call(pos, localreceiver,
    				nf.Id(pos, GET_ORDERED_LOCK), Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList());
    		X10MethodDef methodDef = ts.methodDef(pos, Types.ref(clazzType),
		           ts.Public(), Types.ref(lockType), GET_ORDERED_LOCK,
		            Collections.<Ref<? extends Type>>emptyList(),  null);
		    getLockCall = (X10Call) getLockCall.type(lockType);
		    getLockCall = getLockCall.methodInstance(methodDef.asInstance());
    		Local listLocal = nf.Local(pos, listID).localInstance(lockList.localDef().asInstance());
    		Receiver receiver = listLocal.type(lockList.type().type());
        	List<TypeNode> typeArgs = new LinkedList<TypeNode>();
        	typeArgs.add(nf.CanonicalTypeNode(pos, lockType));
        	List<Expr> args = Collections.<Expr>singletonList(getLockCall.type(lockType));
        	Id addID = nf.Id(pos, LIST_ADD);
        	X10Call addLockCall = nf.X10Call(pos, receiver, addID, typeArgs, args);
        	addLockCall = (X10Call) addLockCall.type(ts.Boolean());
        	List<Ref<? extends Type>> addArgTypes = new LinkedList<Ref<? extends Type>>();
        	addArgTypes.add(Types.ref(lockType));
        	addLockCall = addLockCall.methodInstance(addmi);
        	
        	Stmt addLock = nf.Eval(pos, addLockCall);
        	stmts.add(addLock);
    	}
    	
    	return stmts;
    }
    
    //For each class fields that are associated with locks, add its lock to the locklist, e.g.,
    //  locklist.add(field.getOrderedLock());
    //The lockList parameter is the lock list to which locks are added to
    private List<Stmt> addFieldLockToLockList(Position pos, Type lockType, List<X10FieldDef> fieldDefs,
    		Id listID, LocalDecl lockList) {
    	List<Stmt> stmts = new LinkedList<Stmt>();
    	X10ParsedClassType_c listType = (X10ParsedClassType_c)X10TypeUtils.lookUpType(ts, X10_LIST);
    	listType = (X10ParsedClassType_c) listType.typeArguments(Collections.<Type>singletonList(lockType));
    	MethodInstance addmi = X10TypeUtils.findMethod(ts, this.context, listType, LIST_ADD,
				Collections.<Type>singletonList(nf.X10CanonicalTypeNode(pos, lockType).type()));
    	//get the lock of each field
    	for(X10FieldDef fieldDef : fieldDefs) {
    		pos = fieldDef.position();
    		ContainerType clazzType = fieldDef.container().get();
    		Field_c field = (Field_c) nf.Field(pos, (Receiver)nf.This(pos).type(clazzType), nf.Id(pos, fieldDef.name()));
    		field = (Field_c) field.fieldInstance(fieldDef.asInstance());
    		field = (Field_c) field.type(fieldDef.type().get());
    		X10Call getLockCall = nf.X10Call(pos, field,
        				nf.Id(pos, GET_ORDERED_LOCK), Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList());
    		X10MethodDef methodDef = ts.methodDef(pos, Types.ref(clazzType),
    		           ts.Public(), Types.ref(lockType), GET_ORDERED_LOCK,
    		            Collections.<Ref<? extends Type>>emptyList(),  null);
    		//set the method call type, and method def instance
    		getLockCall = (X10Call) getLockCall.type(lockType);
  		    getLockCall = getLockCall.methodInstance(methodDef.asInstance());
    		Local listLocal = nf.Local(pos, listID).localInstance(lockList.localDef().asInstance());
    		Receiver receiver = listLocal.type(lockList.type().type());
        	List<TypeNode> typeArgs = new LinkedList<TypeNode>();
        	typeArgs.add(nf.CanonicalTypeNode(pos, lockType));
        	List<Expr> args = Collections.<Expr>singletonList(getLockCall.type(lockType));
        	Id addID = nf.Id(pos, LIST_ADD);
        	X10Call addLockCall = nf.X10Call(pos, receiver, addID, typeArgs, args);
        	addLockCall = (X10Call) addLockCall.type(ts.Boolean());
        	//set formal types, method instance
        	List<Ref<? extends Type>> addArgTypes = new LinkedList<Ref<? extends Type>>();
        	addArgTypes.add(Types.ref(lockType));
        	addLockCall = addLockCall.methodInstance(addmi);
        	
        	Stmt addLock = nf.Eval(pos, addLockCall);
        	stmts.add(addLock);
    	}
    	return stmts;
    }
    
    //Acquire the class lock (can be this.getOrderedLock, or Class.getOrderedLock), and add to the lock list
    //It generates the following code:
    //  lockList.add(this.getOrderedLock());   or  lockList.add(Class.getStaticOrderedLock());
    //depends on if the current code is a static method or not
    //The lockList parameter is the lock list to which locks are added to
    private Stmt addClassLockToLockList(Position pos, Type lockType, Id listID, LocalDecl lockList, boolean isStatic) {
    	List<Stmt> stmts = new LinkedList<Stmt>();
    	X10ParsedClassType_c listType = (X10ParsedClassType_c)X10TypeUtils.lookUpType(ts, X10_LIST);
    	listType = (X10ParsedClassType_c) listType.typeArguments(Collections.<Type>singletonList(lockType));
    	MethodInstance addmi = X10TypeUtils.findMethod(ts, this.context, listType, LIST_ADD,
				Collections.<Type>singletonList(nf.X10CanonicalTypeNode(pos, lockType).type()));
    	//the type of current class
    	Type containerClass = this.context.currentClass();
    	assert containerClass instanceof ContainerType;
    	ContainerType clazzType = (ContainerType)containerClass;
    	//fetch lock through either this.getOrderedLock(), or ClassName.getOrderedLock()
    	Receiver thiz = null;
    	X10Call getLockCall = null;
    	X10MethodDef methodDef = null;
    	if(!isStatic) {
    	    thiz = nf.This(pos).type(containerClass);
    	    getLockCall = nf.X10Call(pos, thiz,
    			nf.Id(pos, GET_ORDERED_LOCK), Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList());
    	    methodDef = ts.methodDef(pos, Types.ref(clazzType),
		          ts.Public(), Types.ref(lockType), GET_ORDERED_LOCK,
		           Collections.<Ref<? extends Type>>emptyList(),  null);
    	} else {
    		thiz = nf.CanonicalTypeNode(pos, containerClass);
    		getLockCall = nf.X10Call(pos, thiz, nf.Id(pos, GET_STATIC_ORDERED_LOCK),
					Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList());
			methodDef = ts.methodDef(pos, Types.ref(clazzType),
  		           ts.Public(), Types.ref(lockType), GET_STATIC_ORDERED_LOCK,
		            Collections.<Ref<? extends Type>>emptyList(),  null); 
    	}
		getLockCall = (X10Call) getLockCall.type(lockType);
		getLockCall = getLockCall.methodInstance(methodDef.asInstance());
    	//add the lock to the lock list
    	Local listLocal = nf.Local(pos, listID).localInstance(lockList.localDef().asInstance());
    	Receiver receiver = listLocal.type(lockList.type().type());
        List<TypeNode> typeArgs = new LinkedList<TypeNode>();
        typeArgs.add(nf.CanonicalTypeNode(pos, lockType));
        List<Expr> args = Collections.<Expr>singletonList(getLockCall.type(lockType));
        Id addID = nf.Id(pos, LIST_ADD);
        X10Call addLockCall = nf.X10Call(pos, receiver, addID, typeArgs, args);
        addLockCall = (X10Call) addLockCall.type(ts.Boolean());
        //set the argument, type, and method instance
        List<Ref<? extends Type>> addArgTypes = new LinkedList<Ref<? extends Type>>();
        addArgTypes.add(Types.ref(lockType));
        addLockCall = addLockCall.methodInstance(addmi);
        
        Stmt addLock = nf.Eval(pos, addLockCall);
        return addLock;
    }
    
    //The following 3 methods are shortcuts to acquire/release a few locks (1 -- 3 locks)
    //It will generated code like:
    //  OrderedLock.acquireSingleLock(lock1), or OrderedLock.acquireTwoLocks(lock1, lock2), or OrderedLock.acquireThreeLocks(lock1, lock2, lock3)
    //depends on how many locks are specified in the parameter <code>lockNum</code>
    private Stmt acquireLocksAtOnce(Position pos, Type lockType, Type lockIdType, int lockNum, List<X10LocalDef> localWithLocks,
    		List<LocalDef> localWithoutLocks, boolean isStatic, boolean acquireClassLock) throws SemanticException {
    	return acquireOrReleaseLocks(pos, lockType, lockIdType, lockNum, localWithLocks, localWithoutLocks, isStatic,
    			true, acquireClassLock);
    }
    //It will generated code like:
    //  OrderedLock.releaseSingleLock(lock1), or OrderedLock.releaseTwoLocks(lock1, lock2), or OrderedLock.releaseThreeLocks(lock1, lock2, lock3)
    //depends on how many locks are specified in the parameter <code>lockNum</code>
    private Stmt releaseLocksAtOnce(Position pos, Type lockType, Type lockIdType, int lockNum, List<X10LocalDef> localWithLocks,
    		List<LocalDef> localWithoutLocks, boolean isStatic, boolean acquireClassLock) throws SemanticException {
    	return acquireOrReleaseLocks(pos, lockType, lockIdType, lockNum, localWithLocks, localWithoutLocks, isStatic,
    			false, acquireClassLock);
    }
    private Stmt acquireOrReleaseLocks(Position pos, Type lockType, Type lockIdType, int lockNum,
    		List<X10LocalDef> localWithLocks, List<LocalDef> localWithoutLocks, boolean isStatic, boolean isAcquiring
    		, boolean acquireClassLock) throws SemanticException {
    	assert lockNum <= SHORTCUT_NUM;
    	assert lockNum == localWithLocks.size() + localWithoutLocks.size() + (acquireClassLock ? 1 : 0);
    	Name methodName = null;
    	//choose different shortcut method according to how many locks are needed
    	if(lockNum == 1) {
    		methodName = isAcquiring ? ACQUIRE_1_LOCK : RELEASE_1_LOCK;
    	} else if (lockNum == 2) {
    		methodName = isAcquiring ? ACQUIRE_2_LOCKS : RELEASE_2_LOCKS;
    	} else {
    		methodName = isAcquiring ? ACQUIRE_3_LOCKS : RELEASE_3_LOCKS;
    	}
    	
    	List<TypeNode> argTypeNode = new LinkedList<TypeNode>();
    	List<Expr> locks = new LinkedList<Expr>();
    	Type containerClass = this.context.currentClass();
    	assert containerClass instanceof ContainerType;
    	ContainerType clazzType = (ContainerType)containerClass;
    	Receiver thiz = null;
    	X10Call getLockCall = null;
    	X10MethodDef methodDef = null;
    	//if we need to acquire the this.lock or class lock
    	if(acquireClassLock) {
    	  if(isStatic) {
    		thiz = nf.CanonicalTypeNode(pos, containerClass);
    		getLockCall = nf.X10Call(pos, thiz, nf.Id(pos, GET_STATIC_ORDERED_LOCK),
					Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList());
			methodDef = ts.methodDef(pos, Types.ref(clazzType),
  		           ts.Public(), Types.ref(lockType), GET_STATIC_ORDERED_LOCK,
		            Collections.<Ref<? extends Type>>emptyList(),  null); 
    	  } else {
    		thiz = nf.This(pos).type(containerClass);
    	    getLockCall = nf.X10Call(pos, thiz,
    			nf.Id(pos, GET_ORDERED_LOCK), Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList());
    	    methodDef = ts.methodDef(pos, Types.ref(clazzType),
		          ts.Public(), Types.ref(lockType), GET_ORDERED_LOCK,
		           Collections.<Ref<? extends Type>>emptyList(),  null);
    	  }
    	  getLockCall = getLockCall.methodInstance(methodDef.asInstance());
  		  getLockCall = (X10Call) getLockCall.type(lockType);
  		  locks.add(getLockCall);
    	}
		//get locks for local variables (including paramaters) whose types have been
    	//associated with a lock. It creates code:  "lockWithLock.getOrderedLock()" as argument
		for(X10LocalDef localWithLock : localWithLocks) {
			Type localType = localWithLock.type().get();
    		assert localType instanceof ContainerType;
    		clazzType = (ContainerType)localType;
    		Local l = nf.Local(pos, nf.Id(pos, localWithLock.name())).localInstance(localWithLock.asInstance());
    		Receiver localreceiver = l.type(clazzType);
    		getLockCall = nf.X10Call(pos, localreceiver,
    				nf.Id(pos, GET_ORDERED_LOCK), Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList());
    		methodDef = ts.methodDef(pos, Types.ref(clazzType),
		           ts.Public(), Types.ref(lockType), GET_ORDERED_LOCK,
		            Collections.<Ref<? extends Type>>emptyList(),  null);
		    getLockCall = (X10Call) getLockCall.type(lockType);
		    getLockCall = getLockCall.methodInstance(methodDef.asInstance());
		    //add to the arg list
		    locks.add(getLockCall);
		}
		//get locks for local variables (including parameters) whose types have not
		//been associated with a lock. It create code like:  OrderedLock.getLock(lockDefForVarWithoutLock)
		//NOTE: the localWithoutLocks is a list of localdef with int type (the lock id), while the
		//      localWithLocks is a list of localdef with its own type.
		for(LocalDef lockDefForVarWithoutLock : localWithoutLocks) {
    		Id localLockId = nf.Id(pos, lockDefForVarWithoutLock.name());
    		final Local localid = (Local) nf.Local(pos, localLockId).localInstance(lockDefForVarWithoutLock.asInstance()).
    		             type(lockIdType);
    		Call objLock = this.synth.makeStaticCall(pos, lockType, GET_OBJ_LOCK, Collections.<Expr>singletonList(localid.type(lockIdType)),
    				lockType, this.context());
    		objLock = (Call) objLock.type(lockType);
    		locks.add(objLock);
		}
		//assert the length is equal
		assert locks.size() == lockNum;
    	//make a static call to acquire/release all locks
    	Call acquireCall = this.staticCall(pos, lockType, methodName, argTypeNode, locks, ts.Void(), this.context());
    	Stmt acquireLockStmt = nf.Eval(pos, acquireCall);
    	
    	return acquireLockStmt;
    }
    
    //Create the lock acquiring statement:
    //   OrderedLock.acquireLocksInOrder(locklist);
    private Stmt lockAcquireStatement(Position pos, Type lockType, LocalDecl lockList, Id listID) {
    	X10ParsedClassType_c listType = (X10ParsedClassType_c)X10TypeUtils.lookUpType(ts, X10_LIST);
    	listType = (X10ParsedClassType_c) listType.typeArguments(Collections.<Type>singletonList(lockType));

    	//lock-acquire statement
    	List<TypeNode> argTypeNode = new LinkedList<TypeNode>();
    	List<Expr> argList = new LinkedList<Expr>();
    	argList.add(nf.Local(pos, listID).localInstance(lockList.localDef().asInstance()).type(listType));
    	Call acquireCall = this.staticCall(pos, lockType, ACQUIRE_LOCKS, argTypeNode, argList, ts.Void(), this.context()); 
    	Stmt acquireLockStatement = nf.Eval(pos, acquireCall);
    	
    	return acquireLockStatement;
    }
    
    //Create the lock releasing statement:
    //   OrderedLock.releaseLocks(lockList);
    private Stmt lockReleaseStatement(Position pos, Type lockType, LocalDecl lockList, Id listID) {
    	X10ParsedClassType_c listType = (X10ParsedClassType_c)X10TypeUtils.lookUpType(ts, X10_LIST);
    	listType = (X10ParsedClassType_c) listType.typeArguments(Collections.<Type>singletonList(lockType));

    	//lock-release statement
    	List<TypeNode> argTypeNode = new LinkedList<TypeNode>();
    	List<Expr> argList = new LinkedList<Expr>();
    	argList.add(nf.Local(pos, listID).localInstance(lockList.localDef().asInstance()).type(listType));
    	Call releaseCall = this.staticCall(pos, lockType, RELEASE_LOCKS, argTypeNode, argList, ts.Void(), this.context());
    	Stmt releaseLockStatement = nf.Eval(pos, releaseCall);
    	
    	return releaseLockStatement;
    }
    //A utility method to make a static call
    private Call staticCall(Position pos, Type receiverType, Name methodName, 
    		List<TypeNode> typeArgsN,
    		List<Expr> exprs,
    		Type retType, Context context) {
    	try {
			return synth.makeStaticCall(pos, receiverType, methodName, typeArgsN,
					exprs, ts.Void(), this.context());
		} catch (SemanticException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
    }
    private Expr call(Position pos, Name name, Type returnType) throws SemanticException {
    	return synth.makeStaticCall(pos, ts.Runtime(), name, returnType, context());
    }
    //check if the current context is in a static method
    private boolean isInStaticMethod() {
    	X10CodeDef currentCode = this.context.currentCode();
    	if(currentCode instanceof X10MethodDef_c) {
    		X10MethodDef_c methodDef = (X10MethodDef_c)currentCode;
    		return methodDef.flags() != null && methodDef.flags().contains(Flags.STATIC);
    	}
    	return false;
    }
}
