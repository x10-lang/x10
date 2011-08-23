package x10.visit;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import polyglot.ast.ArrayAccessAssign_c;
import polyglot.ast.Assign;
import polyglot.ast.Block;
import polyglot.ast.Block_c;
import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Catch;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassMember;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.FieldDecl;
import polyglot.ast.FlagsNode;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.Local_c;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Special.Kind;
import polyglot.ast.Special_c;
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
import polyglot.types.MethodDef;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.Ref_c;
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
import x10.ast.Atomic;
import x10.ast.Atomic_c;
import x10.ast.Finish_c;
import x10.ast.SettableAssign_c;
import x10.ast.X10Call;
import x10.ast.X10Call_c;
import x10.ast.X10CanonicalTypeNode_c;
import x10.ast.X10ClassDecl_c;
import x10.ast.X10ConstructorDecl_c;
import x10.ast.X10FieldAssign_c;
import x10.ast.X10FieldDecl_c;
import x10.ast.X10Field_c;
import x10.ast.X10LocalAssign_c;
import x10.ast.X10LocalDecl_c;
import x10.ast.X10Local_c;
import x10.ast.X10MethodDecl;
import x10.ast.X10MethodDecl_c;
import x10.ast.X10New_c;
import x10.types.AsyncDef;
import x10.types.MethodInstance;
import x10.types.X10ClassDef;
import x10.types.X10ClassDef_c;
import x10.types.X10ClassType;
import x10.types.X10ConstructorDef;
import x10.types.X10ConstructorInstance;
import x10.types.X10FieldDef;
import x10.types.X10MethodDef;
import x10.types.X10ParsedClassType_c;
import x10.util.AltSynthesizer;
import x10.util.Synthesizer;

/**
 * This class translates X10 data-centric synchronization constructs into
 * corresponding X10 code, specially:
 * 1. all class that contains atomic fields implements an interface:
 *       x10.lang.Atomics
 * 2. add a lock field $lock into every class that contains atomic fields
 * 3. add a method:
 *      public OrderedLock get$lock() {
 *        return $lock;
 *      }
 *    to each class that contains atomic fields
 * 4. for each constructor in a class that contains atomic fields, translate
 *    public def this(args) {
 *       S
 *    }
 *    
 *    make a copy
 *    
 *    public def this(args, OrderedLock lock) {
 *        this(args);
 *        assert lock != null;
 *        this.$lock = lock;
 *    }
 *    
 *    #FIXME  the internal method should be lock-free (the current implementation
 *             does not conform to this, since we first add lock then make a copy)
 *    also make a copy for each method, renaming as internal..
 *    
 *--------------------the above 4 methods should be visited in 1 round ----------------
 *
 * 5.translate each constructor call as:
 *   new (atomicplus Class)(args);
 *   to:
 *   new (atomicplus Class)(args, container.get$Lock());
 *   
 *   translate each constructor for raw object as:
 *   new Class(args);
 *   to:
 *   new Class(args, new OrderedLock());
 *   
 *   no change to call super(), since each class maintain the lock itself
 *   
 *   also no change to this(), since every class maintain its own lock
 *   
 * 6. acquire lock before invoking each public method
 *    public def method(unitfor a,  atomicplus b) {
 *        S
 *    }
 *     to:
 *    public def method(unitfor a, atomicplus b) {
 *        $lock = this.get$Lock();
 *        $lock1 = a.get$Lock();
 *        $lock2 = b.get$Lock();
 *        sort all the needed locks
 *        try {
 *           $lock.acquire();
 *           $lock1.acquire();
 *           $lock2.acquire();
 *           S
 *        } finally {
 *           $lock.release();
 *           $lock1.release();
 *           $lock2.release();
 *        }
 *    }
 *    
 *    For efficiency, make a copy of each method, a copy called
 *    public def method_internal(unitfor a, atomicplus b) {
 *      S
 *    }
 *   
 * 7. inside a method, add lock for each declared local fields
 * 
 *     if it is field contain atomic fields like : var a:(atomicplus A) = new (atomicplus A)();
 *        declare the lock as: $lock = a.get$lock();
 *     or else, allocate a lock for it:  $lock = new OrderedLock();
 *     
 *     Analyze the async scope to place:
 *     try {
 *         acquire locks
 *     }finally {
 *         release lock
 *     }
 *     
 *     at the right place
 * 8. several possible optimizations
 *    8.1 field-read write optimization (split a lock?)
 *    8.2 may-parallel, if a method never execute parallelly with others, no lock is needed
 *    8.3 single read/write, no lock is needed
 * */
public class X10AtomicityTranslator extends ContextVisitor {
	private final Synthesizer synth;
    private final AltSynthesizer altsynth;
    public X10AtomicityTranslator(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        synth = new Synthesizer(nf, ts);
        altsynth = new AltSynthesizer(ts, nf);
    }
    private static final String LOCK_NAME = "lock";
    private static final String LOCAL_LOCK = "_local_lock";
    private static final String FIELD_ASYNC_LOCK = "_field_async_lock";
    private static final String CONST_FORMAL_NAME = "paramLock";
    private static final String INTERNAL = "_internal_no_lock";
    private static final String LOCK_LIST = "locklist";
    
    private static final QName ORDERED_LOCK = QName.make("x10.util.concurrent.OrderedLock");
    private static final QName ATOMICS = QName.make("x10.util.concurrent.Atomic");
    private static final QName X10_LIST = QName.make("x10.util.List"); 
    private static final QName X10_ARRAYLIST = QName.make("x10.util.ArrayList");
    
    private static final Name LIST_ADD = Name.make("add");
    private static final Name GET_ORDERED_LOCK = Name.make("getOrderedLock");
    private static final Name ACQUIRE_LOCKS = Name.make("acquireLocksInOrder");
    private static final Name RELEASE_LOCKS = Name.make("releaseLocks");
    private static final Name PARAM_LOCK_NAME = Name.make(CONST_FORMAL_NAME);
    
    @Override
    public Node override(Node parent, Node n) { 
    	return super.override(parent, n);
    }
    
    @Override
    protected NodeVisitor enterCall(Node parent, Node n) {
    	
    	return this;
    }
    
    @Override
    /**
     * The only place this visitor manipulates the X10 AST
     * */
    public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
    	//if the class decl has atomic fields then:
    	// 0. inherits an Atomic interface
    	// 1. add a lock field and a get lock method
    	// 2. it also rewrites all the constructor declarations to take a lock arg
    	// 3. it copies each method, and make a lock-free internal copy
    	//FIXME item 3 is not correct, since modifiying each method is before modifying
    	//      its container class declaration.
    	if(n instanceof X10ClassDecl_c) {
    		X10ClassDecl_c x10clz = (X10ClassDecl_c)n;
    		return this.visitX10ClassDecl(x10clz);
    	}
    	
    	//rewrite constructor calls, to take a lock object as an additional arg
    	if(n instanceof X10New_c) {
    		X10New_c x10newc = (X10New_c)n;
    		return this.visitX10New(x10newc);
    	}
    	
    	//add locks at begin/end of the method to provide atomicity
    	//allocate locks for fields that maybe accessed in async blocks
    	if(n instanceof X10MethodDecl_c) {
    		X10MethodDecl_c x10method = (X10MethodDecl_c)n;
    		return this.visitX10Method(x10method);
    	}
    	
    	//adding local variable locks/field locks to the async block
    	if(n instanceof Async_c) {
//    		Async_c async = (Async_c)n;
//    		return this.visitAsync_c(async);
    	}
    	
    	//allocate locals for local variables declared as atomic
    	if(n instanceof Block_c) {
    		Block_c block = (Block_c)n;
    		return this.visitBlock_c(block);
    	}
    	
    	if(n instanceof Atomic_c) {
    		Atomic_c atomic = (Atomic_c)n;
    		return this.visitAtomic_c(atomic);
    	}
    	
    	return n;
    }
    
    
    private Node visitX10ClassDecl(X10ClassDecl_c n) {
    	//skip class declaration without atomic fields
    	boolean hasAtomicFields = n.classDef().hasAtomicFields();
    	if(!hasAtomicFields) {
    		return n;
    	}
    	//skip abstract class, should not, because it may implement concrete method
//    	if(n.classDef().flags().contains(Flags.ABSTRACT)) {
//    		return n;
//    	}
    	//copy the original interface, and add a new one
    	//note that n.interfaces() returns an immutable list
    	List<TypeNode> interfaces = new LinkedList<TypeNode>();
    	for(TypeNode inter : n.interfaces()) {
    		interfaces.add(inter);
    	}
    	//the atomic interface
		Type atomicInterface = this.lookUpType(ATOMICS);
		assert atomicInterface != null;
		//add the interface to the class declarations
		Position pos = n.position();
		TypeNode intf = nf.CanonicalTypeNode(pos, atomicInterface);
		interfaces.add(intf);
		n = (X10ClassDecl_c) n.interfaces(interfaces);
			
		//add a lock field with type OrderedLock for each class
		List<ClassMember> members = n.body().members();
		X10ClassType containerClass = n.classDef().asType();
			
		//get the lock type
		Type lockType =  this.lookUpType(ORDERED_LOCK);
		assert lockType != null;
		//create the field OrderedLock:$lock = null; (init value)
		Name lockName = Context.makeFreshName(LOCK_NAME);
		Id lockId = nf.Id(pos, lockName);
		TypeNode lockTypeNode = nf.CanonicalTypeNode(pos, lockType);
		X10FieldDef fd = ts.fieldDef(pos, Types.ref(containerClass), ts.Private(),
					Types.ref(lockType), lockName);	
		FieldDecl fDecl = nf.FieldDecl(pos, nf.FlagsNode(pos, ts.Private()),
					lockTypeNode, lockId, nf.NullLit(pos).type(ts.Null())).fieldDef(fd);
			
		//create the method getLock() {return this.$lock; }
		Name methodName = GET_ORDERED_LOCK;
		Id methodId = nf.Id(pos, methodName);
		TypeNode retTypeNode = nf.CanonicalTypeNode(pos, lockType);
		X10MethodDef methodDef = ts.methodDef(pos, Types.ref(containerClass),
		           ts.Public(), Types.ref(lockType), methodName,
		            Collections.<Ref<? extends Type>>emptyList(),  null);
		Receiver special = nf.This(pos).type(containerClass);
		Expr field = nf.Field(pos, special, lockId).fieldInstance(fd.asInstance()).type(lockType);    
		Stmt retStmt = nf.Return(pos, field);
		Block body = nf.Block(pos, retStmt);
		X10MethodDecl methodDecl = nf.MethodDecl(pos, nf.FlagsNode(pos, ts.Public()),
					retTypeNode, methodId, Collections.<Formal>emptyList(), body).methodDef(methodDef);
		
		//create a list to store new class members
		List<ClassMember> newMembers = new LinkedList<ClassMember>();
		for(ClassMember member : members) {
			newMembers.add(member);
			Position mpos = member.position();
			//create a copy for each method
			if(member instanceof X10MethodDecl_c) {
				if(!member.position().isCompilerGenerated()) {
				    X10MethodDecl_c x10method = (X10MethodDecl_c)member;
				    Name repl_name = Name.make(x10method.name().toString() + INTERNAL);
				    Id internalMethodId = nf.Id(member.position(), repl_name);
					//the replicated method
					X10MethodDecl_c repl_method = x10method.name(internalMethodId);
					X10MethodDef x10def = x10method.methodDef();
					MethodDef mi = ts.methodDef(mpos, x10def.container(), x10def.flags(), x10def.returnType(),
					  		repl_name, x10def.formalTypes(), x10def.offerType());
					repl_method = repl_method.methodDef(mi);
					//add the replicated method to the new member list    
					newMembers.add(repl_method);
				}
			}
			//modify each constructor
			if(member instanceof X10ConstructorDecl_c) {
				X10ConstructorDecl_c x10const = (X10ConstructorDecl_c)member;
				//let it takes an additional argument, OrderedLock
				//transform:  this(args ) ---> 
				//     this(args, lock:OrderedLock) { statement,  this.lock = lock; }
				List<Formal> newFormals = new LinkedList<Formal>();
				for(Formal f : x10const.formals()) {
					newFormals.add(f);
				}
			    //add the new OrderedLock type parameter
				Id paramId = nf.Id(mpos, PARAM_LOCK_NAME);
				TypeNode paramTypeNode = nf.CanonicalTypeNode(mpos, lockType);
				LocalDef li = ts.localDef(mpos, Flags.FINAL, Types.ref(lockType), PARAM_LOCK_NAME);
				Formal lockFormal = nf.Formal(mpos, nf.FlagsNode(mpos, Flags.FINAL), paramTypeNode, paramId).localDef(li);
				//add to the parameter list
				newFormals.add(lockFormal);
				//create the new constructor def
				ConstructorDef x10ci = x10const.constructorDef();
				ConstructorDef ci = ts.constructorDef(mpos, Types.ref(x10ci.container().get().toClass()),
						x10ci.flags(), x10ci.formalTypes(), x10ci.offerType());
				//modifiy the body, and add statement this.$lock = param_lock;	
				Block newBody = x10const.body();
				Expr local = nf.Local(mpos, paramId).localInstance(li.asInstance()).type(lockType);
				FieldAssign fieldAssign =  nf.FieldAssign(mpos, nf.This(mpos).type(fd.asInstance().container()),
						nf.Id(mpos, fd.name()), Assign.ASSIGN, local);
				fieldAssign = (FieldAssign) fieldAssign.type(fd.asInstance().type());
				fieldAssign = fieldAssign.fieldInstance(fd.asInstance());
				fieldAssign = fieldAssign.targetImplicit(false);
				Eval fieldAssingExpr = nf.Eval(mpos, fieldAssign);
				newBody = newBody.append(fieldAssingExpr);
				
				//create the new constructor
				X10ConstructorDecl_c newConstructor = (X10ConstructorDecl_c) nf.ConstructorDecl(mpos,
								x10const.flags(), x10const.name(), newFormals, newBody).constructorDef(ci);
				//add the new constructor the memeber list
				newMembers.add(newConstructor);
			}
		}	
		//add the previously-created field / method declarations
		newMembers.add(0, fDecl);
		newMembers.add(0, methodDecl);
		//set the class body
		ClassBody newBody = n.body().members(newMembers);
		n = (X10ClassDecl_c) n.body(newBody);
		
		return n;
    }
  
    private Node visitX10New(X10New_c n) {
    	X10ParsedClassType_c clazzType = Types.fetchX10ClassType(n.constructorInstance().returnType());
    	Position pos = n.position();
    	//skip calling the constructors of a class without atomic fields
    	if(!clazzType.def().hasAtomicFields()) {
    		return n;
    	}
    	//the type of OrderedLock, will be used as an additional argument
    	Type lockType = lookUpType(ORDERED_LOCK);
    	assert lockType != null;
    	
    	//construct the new constructor that will be called
    	//note that at this point, the new constructor may not exist in the class declaration.
    	//but it does not matter.
		X10ConstructorInstance x10ci = n.constructorInstance();
		List<Type> newTypes = new LinkedList<Type>();
		newTypes.addAll(x10ci.formalTypes());
		//add the additional lock type
    	newTypes.add(lockType);
    	//change the constructor definition
    	List<Ref<? extends Type>> refs = this.typesToTypeRefs(newTypes);
    	X10ConstructorDef cdef = ts.constructorDef(pos, Types.ref(clazzType), x10ci.flags(), refs, x10ci.offerType());
    	X10ConstructorInstance ci = ts.createConstructorInstance(pos, Types.ref(cdef));
    	ci = ci.formalTypes(newTypes);
    	n = (X10New_c) n.constructorInstance(ci);
    	
    	//change the constructor in two different cases
    	//  1. new (atomicplus C)(args)   ==> new (atomicplus C)(args,  container.getOrderedLock());
    	//  2. new C(args)  ==>  new C(args, new OrderedLock()); 
    	FlagsNode flagNode = n.objectType().getFlagsNode();
    	if(flagNode != null && flagNode.flags().contains(Flags.ATOMICPLUS)) {
    		List<Expr> newArguments = new LinkedList<Expr>();
    		newArguments.addAll(n.arguments());
    		//change the newcall
    		X10Call getLockCall = nf.X10Call(pos, nf.This(pos).type(ci.container()),
    				nf.Id(pos, GET_ORDERED_LOCK), Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList());
    		Name methodName = GET_ORDERED_LOCK;
			X10MethodDef methodDef = ts.methodDef(n.position(), Types.ref(clazzType),
		           ts.Public(), Types.ref(lockType), methodName,
		            Collections.<Ref<? extends Type>>emptyList(),  null);
    		getLockCall = getLockCall.methodInstance(methodDef.asInstance());
    		Expr getLockExpr = getLockCall.type(lockType);
    		newArguments.add(getLockExpr);
    		n = (X10New_c)n.arguments(newArguments);
    	} else {
	    	List<Expr> newArguments = new LinkedList<Expr>();
	    	newArguments.addAll(n.arguments());
	    	TypeNode typeNode = nf.CanonicalTypeNode(n.position(), lockType);
	        X10ConstructorInstance lockCI = findDefaultConstructor(lockType);
	    	Expr newLock = nf.New(n.position(), typeNode,
	    			Collections.<Expr>emptyList()).constructorInstance(lockCI).type(lockType);
	    	newArguments.add(newLock);
	    	n = (X10New_c) n.arguments(newArguments);
    	}
    	
    	return n;
    }
    
    /** convert rules
     * before:
     *    method(unitfor a, atomicplus b) {
     *         Statements
     *    }
     *    
     * after:
     * 
     *   method(unitfor a, atomicplus b) {
     *      lock1:OrderedLock = this.getOrderedLock();
     *      lock2:OrderedLock = a.getOrderedLock();
     *      lock3:OrderedLock = b.getOrderedLock();
     *      lockList:List = new LinkedList();
     *      lockList.add(lock1);
     *      lockList.add(lock2);
     *      lockList.add(lock3);
     *      
     *      try {
     *          OrderedLock.acquireLocksInOrder(lockList);
     *          Statements
     *      } finally {
     *          OrderedLock.releaseAll(lockList);
     *      }
     *   }
     *   
     *   
     *   #FIXME
     *   problem in dealing with nested async-finish constructs
     **/
    private Node visitX10Method(X10MethodDecl_c n) {
    	Position pos = n.position();
    	if(pos.isCompilerGenerated()) {
    		return n;
    	}
    	
    	X10ParsedClassType_c clazzType = Types.fetchX10ClassType(n.methodDef().container().get());
    	//#TODO not a class type? is that true in x10?
    	if(clazzType == null) {
    		return n;
    	}
    	//skip abstract method
    	if(n.body() == null) {
    		return n;
    	}
    	//first let's count how many locks we need
    	int locknum = 0;
    	if(clazzType.def().hasAtomicFields()) {
    		locknum++;
    	}
    	for(Formal f : n.formals()) {
    		if(f.flags() != null && f.flags().flags().contains(Flags.UNITFOR)) {
    			locknum++;
    			continue;
    		}
    		if(f.type().getFlagsNode() != null && f.type().getFlagsNode().flags().contains(Flags.ATOMICPLUS)) {
    			locknum++;
    			continue;
    		}
    	}
    	
    	//if no lock is needed, we do not need to make any change
    	//what about accessing a field inside async? In fact, that is OK.
    	//If the field is not atomic, that is fine. other wise we the class
    	//must contain at least one atomic field
    	if(locknum == 0) {
    		return n;
    	}
    	
    	//first allocate locks
    	Type lockType = this.lookUpType(ORDERED_LOCK);
    	List<Name> freshlocknames = new LinkedList<Name>();
    	for(int i = 0; i < locknum; i++) {
    	    freshlocknames.add(Context.makeFreshName("methodlock" + i));
    	}
    	//all lock declaration statements
    	List<LocalDecl> lockDeclStmts = new LinkedList<LocalDecl>();
    	
    	//in this order, the first lock is for this, then each argument
    	int currentlockindex = 0;
    	if(clazzType.def().hasAtomicFields()) {
    		//create a statement  var lock:OrderedLock = this.getOrderedLock();
    		Name lockName = freshlocknames.get(currentlockindex);
            final LocalDef li = ts.localDef(pos, ts.Final(), Types.ref(lockType), lockName);
            final Id varId = nf.Id(pos, lockName);
            X10Call getLockCall = nf.X10Call(pos, nf.This(pos).type(n.memberDef().container().get()),
    				nf.Id(pos, GET_ORDERED_LOCK), Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList());
    		Name methodName = GET_ORDERED_LOCK;
			X10MethodDef methodDef = ts.methodDef(n.position(), Types.ref(clazzType),
		           ts.Public(), Types.ref(lockType), methodName,
		            Collections.<Ref<? extends Type>>emptyList(),  null);
			getLockCall = (X10Call) getLockCall.type(lockType);
    		getLockCall = getLockCall.methodInstance(methodDef.asInstance());
            final Expr init = getLockCall;
            final LocalDecl ld = nf.LocalDecl(pos, nf.FlagsNode(pos, ts.Final()),
            		nf.CanonicalTypeNode(pos, lockType), varId, init).
            		localDef(li);
    		//increase the lock index, and then add the ld to the lock declaration list
            lockDeclStmts.add(ld);
    		currentlockindex++;
    	}
    	for(Formal f : n.formals()) {
    		boolean needLock = false;
    		if(f.flags() != null && f.flags().flags().contains(Flags.UNITFOR)) {
    			needLock = true;
    		}
    		if(f.type().getFlagsNode() != null && f.type().getFlagsNode().flags().contains(Flags.ATOMICPLUS)) {
    			needLock = true;
    		}
    		if(needLock) {
    			//create a statement var lock:OrderedLock = f.getOrderedLock();
    			Name lockName = freshlocknames.get(currentlockindex);
    			final Type type = lockType;
                final LocalDef li = ts.localDef(pos, ts.Final(), Types.ref(type), lockName);
                final Id varId = nf.Id(pos, lockName);
                Local local = (Local) nf.Local(pos, f.name()).type(f.type().type());
                local = local.localInstance(li.asInstance());
                Receiver receiver =  local; //  nf.This(pos).type(n.memberDef().container().get());
                X10Call getLockCall = nf.X10Call(pos,  receiver,
        				nf.Id(pos, GET_ORDERED_LOCK), Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList());
                getLockCall = (X10Call) getLockCall.type(lockType);
        		Name methodName = GET_ORDERED_LOCK;
    			X10MethodDef methodDef = ts.methodDef(n.position(), Types.ref(clazzType),
    		           ts.Public(), Types.ref(lockType), methodName,
    		            Collections.<Ref<? extends Type>>emptyList(),  null);
        		getLockCall = getLockCall.methodInstance(methodDef.asInstance());
                final LocalDecl ld = nf.LocalDecl(pos, nf.FlagsNode(pos, ts.Final()),
                		nf.CanonicalTypeNode(pos, type), varId, getLockCall).
                		localDef(li);
    			//increase the lock index, and then add to the lock declaration list
    			lockDeclStmts.add(ld);
    			currentlockindex++;
    		}
    	}
    	//check the correctness
    	assert freshlocknames.size() == lockDeclStmts.size();
    	
    	/* create a lock list, and add all needed locks, like:
    	 * 
    	 * var locks:List[OrderedLock] = new ArrayList[OrderedLock]();
		 * locks.add(lock1);
		 * locks.add(lock2);
		 * locks.add(lock3);
       	 * locks.add(lock4);
		 * locks.add(lock5);
    	 * */
    	//create a local lock for each allocated lock
    	List<Local> allLocks = new LinkedList<Local>();
    	for(LocalDecl ld : lockDeclStmts) {
    		final Local local = (Local) nf.Local(pos, ld.name()).localInstance(ld.localDef().asInstance()).type(lockType);
    		allLocks.add(local);
    	}
    	//create statement  var lock_list:List<OrderedLock> = new ArrayList<OrderedLock>();
    	List<Stmt> lockListCreation = new LinkedList<Stmt>();

    	X10ParsedClassType_c listType = (X10ParsedClassType_c)this.lookUpType(X10_LIST);
    	listType = (X10ParsedClassType_c) listType.typeArguments(Collections.<Type>singletonList(lockType));
    	X10ParsedClassType_c arrayListType = (X10ParsedClassType_c)this.lookUpType(X10_ARRAYLIST);
    	arrayListType = (X10ParsedClassType_c)arrayListType.typeArguments(Collections.<Type>singletonList(lockType));
    	TypeNode arrayListTypeNode = nf.CanonicalTypeNode(pos, arrayListType);
        X10ConstructorInstance arrayListCI = findDefaultConstructor(arrayListType);
        //create an empty list
    	Expr newArrayList = nf.New(n.position(), arrayListTypeNode,
    			Collections.<Expr>emptyList()).constructorInstance(arrayListCI).type(lockType);
    	Name listName = Context.makeFreshName(LOCK_LIST);
    	Id listID = nf.Id(pos, listName);
    	LocalDef li = ts.localDef(pos, ts.Final(), Types.ref(listType), listName);
        LocalDecl emptyList = nf.LocalDecl(pos,
        		nf.FlagsNode(pos, ts.Final()), nf.CanonicalTypeNode(pos, listType),
        		listID, newArrayList).localDef(li);
        lockListCreation.add(emptyList);
        //create statement to add each lock element into the list
        List<Stmt> lockListAddition = new LinkedList<Stmt>();
        for(Local lock : allLocks) {
        	Local local = (Local) nf.Local(pos, listID).type(emptyList.type().type());
        	Receiver receiver = local.localInstance(li.asInstance());
        	List<TypeNode> typeArgs = new LinkedList<TypeNode>();
        	typeArgs.add(nf.CanonicalTypeNode(pos, lockType));
        	List<Expr> args = Collections.<Expr>singletonList(lock.type(lockType));
        	Id addID = nf.Id(pos, LIST_ADD);
        	X10Call addLockCall = nf.X10Call(pos, receiver, addID, typeArgs, args);
        	addLockCall = (X10Call) addLockCall.type(ts.Boolean());
        	MethodInstance mi = this.findMethodInThisContext(listType, LIST_ADD,
        			Collections.<Type>singletonList(nf.X10CanonicalTypeNode(pos, lockType).type()));
        	addLockCall = addLockCall.methodInstance(mi);
        	//the lock addition statement
        	Stmt addLock = nf.Eval(pos, addLockCall);
        	lockListAddition.add(addLock);
        }
    	
    	//the statement to acquire locks
        List<TypeNode> argTypeNode = new LinkedList<TypeNode>();
    	List<Expr> lockList = new LinkedList<Expr>();
    	lockList.add(nf.Local(pos, listID).localInstance(emptyList.localDef().asInstance()).type(listType));
    	Call acquireCall = this.staticCall(pos, lockType, ACQUIRE_LOCKS, argTypeNode, lockList, ts.Void(), this.context()); 
    	Stmt acquireLockStatement = nf.Eval(pos, acquireCall);

    	//create the lock releasing method call statement
    	Call releaseCall = this.staticCall(pos, lockType, RELEASE_LOCKS, argTypeNode, lockList, ts.Void(), this.context());
    	Stmt releaseLockStatement = nf.Eval(pos, releaseCall);
    	
    	//to assemble the method body
    	Block methodBody = n.body();
    	
    	List<Stmt> tryStatements = new LinkedList<Stmt>();
    	tryStatements.addAll(lockListAddition);
    	tryStatements.add(acquireLockStatement);
    	tryStatements.addAll(methodBody.statements());
    	
    	Block tryBlock = nf.Block(pos, tryStatements);
    	Block finallyBlock = nf.Block(pos, releaseLockStatement);
    	
    	Try tryNode = nf.Try(pos, tryBlock, Collections.<Catch>emptyList(), finallyBlock);
    	
    	List<Stmt> statements = new LinkedList<Stmt>();
    	statements.addAll(lockDeclStmts);
    	statements.addAll(lockListCreation);
    	statements.addAll(this.fieldAsyncLockCreation(methodBody));
    	statements.add(tryNode);
    	
    	Block newBlock = nf.Block(pos, statements);
    	n = (X10MethodDecl_c) n.body(newBlock);
    	
    	return n;
    }
    
    
    private Node visitBlock_c(Block_c block) {
    	List<Stmt> statements = block.statements();
    	//create a list of statements, adding a lock for each atomic field
    	Type lockType = this.lookUpType(ORDERED_LOCK);
    	List<Stmt> newStatements = new LinkedList<Stmt>();
    	for(Stmt stmt : statements) {
    		newStatements.add(stmt);
    		//create a lock field, and add the local declaration statement
    		//just after it #FIXME is this complete?
    		if(stmt instanceof X10LocalDecl_c) {
    			X10LocalDecl_c localDecl = (X10LocalDecl_c)stmt;
    			if(localDecl.flags() != null && localDecl.flags().flags().contains(Flags.ATOMIC)) {
    				Position pos = stmt.position();
    				//create new OrderedLock();
    				TypeNode typeNode = nf.CanonicalTypeNode(pos, lockType);
    		        X10ConstructorInstance lockCI = findDefaultConstructor(lockType);
    		    	Expr newLock = nf.New(pos, typeNode, Collections.<Expr>emptyList()).constructorInstance(lockCI).type(lockType);
    		    	//create local field assignment
    		    	Name lockName = Context.makeFreshName( getLocalLockName(localDecl.name().toString()));
    		    	
                    final LocalDef li = ts.localDef(pos, ts.Final(), Types.ref(lockType), lockName);
                    final Id varId = nf.Id(pos, lockName);
                    final LocalDecl ld = nf.LocalDecl(pos, nf.FlagsNode(pos, ts.Final()),
                    		nf.CanonicalTypeNode(pos, lockType), varId, newLock).
                    		localDef(li);
                    //add to the statement
                    newStatements.add(ld);
    			}
    		}
    	}
    	//no change?
    	if(statements.size() == newStatements.size()) {
    		return block;
    	}
    	
    	//some changes
    	block = (Block_c) block.statements(newStatements);
    	
    	return block;
    }
    
    /**
     * In X10, an async block can not access a local variable without finish
     * but it can access a field without finish
     * 
     * so, add both field / local locks inside async
     * */
    private Node visitAsync_c(Async_c async) {
    	Stmt body = async.body();
    	LocalAndFieldAccessVisitor visitor = new LocalAndFieldAccessVisitor();
    	body.visit(visitor);
    	//get the escaped local fields as well as field defs which need explicit locks
    	Set<LocalDef> localDefs = visitor.escapedLocalDefs();
    	Set<FieldDef> fieldDefs = visitor.escapedFieldDefs();
    	//no lock is needed
    	if(localDefs.isEmpty() && fieldDefs.isEmpty()) {
    		return async;
    	}
    	
    	//places to add locks
    	/* It transforms the code as follows:
    	 * 
    	 * Before:
    	 * async {
    	 *   Stmt;
    	 * }
    	 * 
    	 * After:
    	 * async {
    	 *    lockList:List<OrderedLock> = new ArrayList<OrderedLock>();
    	 *    lockList.add(local1);
    	 *    lockList.add(local2);
    	 *    lockList.add(field1);
    	 *    lockList.add(field2);
    	 *    try {
    	 *       OrderedLock.acquireLocksInOrder(lockList);
    	 *       Stmt;
    	 *    } finally {
    	 *       OrderedLock.releaseAllLocks(lockList);
    	 *    }
    	 * }
    	 * 
    	 * NOTE an important issue in the implementation is the code MUST explicitly
    	 * add variance instance to the Async Def.
    	 * */
    	//get all necessary types
    	Type lockType = this.lookUpType(ORDERED_LOCK);
    	Position pos = async.position();
    	X10ParsedClassType_c listType = (X10ParsedClassType_c)this.lookUpType(X10_LIST);
    	listType = (X10ParsedClassType_c) listType.typeArguments(Collections.<Type>singletonList(lockType));
    	X10ParsedClassType_c arrayListType = (X10ParsedClassType_c)this.lookUpType(X10_ARRAYLIST);
    	arrayListType = (X10ParsedClassType_c)arrayListType.typeArguments(Collections.<Type>singletonList(lockType));
    	
    	//the variance instance need to add to the async def
    	List<VarInstance<? extends VarDef>> referredVars = new LinkedList<VarInstance<? extends VarDef>>();
    	
    	//the statements adding to the async block
    	List<Stmt> lockListInit = new LinkedList<Stmt>();
    	TypeNode arrayListTypeNode = nf.CanonicalTypeNode(pos, arrayListType);
        X10ConstructorInstance arrayListCI = findDefaultConstructor(arrayListType);
        //create an empty list
    	Expr newArrayList = nf.New(pos, arrayListTypeNode,
    			Collections.<Expr>emptyList()).constructorInstance(arrayListCI).type(lockType);
    	Name listName = Name.make(Context.getNewVarName().toString() + "_locklist");
    	Id listID = nf.Id(pos, listName);
    	LocalDef li = ts.localDef(pos, ts.Final(), Types.ref(listType), listName);
        LocalDecl emptyList = nf.LocalDecl(pos,
        		nf.FlagsNode(pos, ts.Final()), nf.CanonicalTypeNode(pos, listType),
        		listID, newArrayList).localDef(li);
        lockListInit.add(emptyList);
        MethodInstance addmi = this.findMethodInThisContext(listType, LIST_ADD,
				Collections.<Type>singletonList(nf.X10CanonicalTypeNode(pos, lockType).type()));
        //create locks that are needed
    	for(LocalDef localDef : localDefs) {
    		Name localLockName = Context.makeFreshName( getLocalLockName(localDef.name().toString()));
    		Id localLockId = nf.Id(pos, localLockName);
            final LocalDef localLockDef = ts.localDef(pos, ts.Final(), Types.ref(lockType), localLockName);
    		final Local local = (Local) nf.Local(pos, localLockId).localInstance(localLockDef.asInstance()).
    		             type(lockType);
    		Local listLocal = nf.Local(pos, listID).localInstance(emptyList.localDef().asInstance());
    		Receiver receiver = listLocal.type(emptyList.type().type());
        	List<TypeNode> typeArgs = new LinkedList<TypeNode>();
        	typeArgs.add(nf.CanonicalTypeNode(pos, lockType));
        	List<Expr> args = Collections.<Expr>singletonList(local.type(lockType));
        	Id addID = nf.Id(pos, LIST_ADD);
        	X10Call addLockCall = nf.X10Call(pos, receiver, addID, typeArgs, args);
        	addLockCall = (X10Call) addLockCall.type(ts.Boolean());
        	
        	List<Ref<? extends Type>> addArgTypes = new LinkedList<Ref<? extends Type>>();
        	addArgTypes.add(Types.ref(lockType));
        	
        	addLockCall = addLockCall.methodInstance(addmi);
        	Stmt addLock = nf.Eval(pos, addLockCall);
    		//add to the list
        	lockListInit.add(addLock);
        	//add to the referred var instances
        	referredVars.add(localLockDef.asInstance());
    	}
    	
    	for(FieldDef fieldDef : fieldDefs) {
    		X10ClassType classType = this.context.currentClass();
    		Name fieldLockName = Context.makeFreshName(this.getFieldAsyncLockName(fieldDef.name().toString()));
    		Id fieldLockId = nf.Id(pos, fieldLockName);
    		final LocalDef localLockDef = ts.localDef(pos, ts.Final(), Types.ref(lockType), fieldLockName);
  		    final Local local = (Local) nf.Local(pos, fieldLockId).localInstance(localLockDef.asInstance()).
  		             type(lockType);
  		    Local listLocal = nf.Local(pos, listID).localInstance(emptyList.localDef().asInstance());
  		    Receiver receiver = listLocal.type(emptyList.type().type());
      	    List<TypeNode> typeArgs = new LinkedList<TypeNode>();
      	    typeArgs.add(nf.CanonicalTypeNode(pos, lockType));
      	    List<Expr> args = Collections.<Expr>singletonList(local.type(lockType));
      	    Id addID = nf.Id(pos, LIST_ADD);
      	    X10Call addLockCall = nf.X10Call(pos, receiver, addID, typeArgs, args);
      	    addLockCall = (X10Call) addLockCall.type(ts.Boolean());
      	    List<Ref<? extends Type>> addArgTypes = new LinkedList<Ref<? extends Type>>();
      	    addArgTypes.add(Types.ref(lockType));
      	    addLockCall = addLockCall.methodInstance(addmi);
      	    Stmt addLock = nf.Eval(pos, addLockCall);
     		//add to the list
         	lockListInit.add(addLock);
         	//add to the referred var instances
         	referredVars.add(localLockDef.asInstance());
    	}
    	
    	//lock-acquire statement
    	List<TypeNode> argTypeNode = new LinkedList<TypeNode>();
    	List<Expr> lockList = new LinkedList<Expr>();
    	lockList.add(nf.Local(pos, listID).localInstance(emptyList.localDef().asInstance()).type(listType));
    	Call acquireCall = this.staticCall(pos, lockType, ACQUIRE_LOCKS, argTypeNode, lockList, ts.Void(), this.context()); 
    	Stmt acquireLockStatement = nf.Eval(pos, acquireCall);
    	
    	//lock-release statement
    	Call releaseCall = this.staticCall(pos, lockType, RELEASE_LOCKS, argTypeNode, lockList, ts.Void(), this.context());
    	Stmt releaseLockStatement = nf.Eval(pos, releaseCall);
    	
    	//assemble
    	List<Stmt> tryStatements = new LinkedList<Stmt>();
    	tryStatements.add(acquireLockStatement);
    	tryStatements.add(async.body());
    	
    	Block tryBlock = nf.Block(pos, tryStatements);
    	Block finallyBlock = nf.Block(pos, releaseLockStatement);
    	
    	Try tryStmt = nf.Try(pos, tryBlock, Collections.<Catch>emptyList(), finallyBlock);
    	
    	List<Stmt> statements = new LinkedList<Stmt>();
    	statements.addAll(lockListInit);
    	statements.add(tryStmt);
    	
    	Block newBlock = nf.Block(pos, statements);
    	
    	async = (Async_c) async.body(newBlock);

    	//important, do not forget to add captured environment element
    	List<VarInstance<? extends VarDef>> existingvars = async.asyncDef().capturedEnvironment();    	
    	List<VarInstance<? extends VarDef>> vars = new LinkedList<VarInstance<? extends VarDef>>();
    	vars.addAll(existingvars);
    	vars.addAll(referredVars);
    	//MUST explicitly set the environment variables
    	async.asyncDef().setCapturedEnvironment(vars);
    	
    	return async;
    }
    
    /** first analyze the lock it needs, then translate it from
     *   atomic S
     * into
     *   var locklist:List = new ArrayList();
     *   locklist.add(lock1);
     *   locklist.add(lock2);
     *   ...
     *   try {
     *      OrderedLock.acquireLock();
     *      S
     *   } finally {
     *      OrderedLock.releaseAllLocks();
     *   }
     *   
     *   TODO improvement space. only need to check reassignment
     *   instead of method calls. Method calls are already well
     *   synchronized
     */
    private Node visitAtomic_c(Atomic_c atomic) {
    	//visitor variables that are declared with atomic
//    	LocalAndFieldAccessVisitor visitor = new LocalAndFieldAccessVisitor();
    	
    	LocalAndFieldReassignVisitor visitor = new LocalAndFieldReassignVisitor();
    	
    	atomic.body.visit(visitor);
    	//check if we need this atomics
    	if(!visitor.hasEscaped()) {
    		//System.out.println("get rid of the atomic keyword, no escaped vars.");
    		//no need for the atomic notation
    		//return atomic.body;
    		return atomic;
    	} else {
    		
    		//get the escaped local fields as well as field defs which need explicit locks
        	Set<LocalDef> localDefs = visitor.escapedLocalDefs();
        	Set<FieldDef> fieldDefs = visitor.escapedFieldDefs();
        	
        	System.out.println("inferring locks for the atomic section: ");
        	System.out.println("   local defs: " + localDefs);
        	System.out.println("   field defs: " + fieldDefs);
        	
    		//start to acquire locks
        	Position pos = atomic.position();
        	Type lockType = this.lookUpType(ORDERED_LOCK);
    		LocalDecl emptyLockList = this.createEmptyLockListDeclaration(pos);
    		List<Stmt> addLocalsToLockStmts = this.addLocalDefToList(pos, lockType, localDefs,
    				emptyLockList.name(), emptyLockList);
    		List<Stmt> addFieldsToLockStmts = this.addFieldDefToList(pos, lockType, fieldDefs,
    				emptyLockList.name(), emptyLockList);
    		Stmt lockAcquireStmt = this.lockAcquireStatement(pos, lockType, emptyLockList, emptyLockList.name());
    		Stmt lockReleaseStmt = this.lockReleaseStatement(pos, lockType, emptyLockList, emptyLockList.name());
    		
    		//assembly the code block
    		//assemble
        	List<Stmt> tryStatements = new LinkedList<Stmt>();
        	tryStatements.add(lockAcquireStmt);
        	tryStatements.add(atomic.body());
        	
        	Block tryBlock = nf.Block(pos, tryStatements);
        	Block finallyBlock = nf.Block(pos, lockReleaseStmt);
        	
        	Try tryStmt = nf.Try(pos, tryBlock, Collections.<Catch>emptyList(), finallyBlock);
        	
        	List<Stmt> statements = new LinkedList<Stmt>();
        	statements.add(emptyLockList);
        	statements.addAll(addLocalsToLockStmts);
        	statements.addAll(addFieldsToLockStmts);
        	statements.add(tryStmt);
        	
        	Block newBlock = nf.Block(pos, statements);
        	
        	//get rid of the atomic keyword
        	return newBlock;
    		
    	}
    }
    
    class AsyncVisitor extends NodeVisitor {
    	public final LocalAndFieldAccessVisitor realVisitor = new LocalAndFieldAccessVisitor();
    	
    	@Override
    	public Node leave(Node old, Node n, NodeVisitor v) {
    		if(n instanceof Async_c) {
    			n.visit(realVisitor);
    		}
    		//not modify the ast node
    		return super.leave(old, n, v);
    	}
    	public Set<FieldDef> escapedFieldDefs() {
    		return realVisitor.escapedFieldDefs();
    	}
    }
    
    class LocalAndFieldReassignVisitor extends NodeVisitor {
    	public final Set<LocalDef> atomicLocalReassigns = new LinkedHashSet<LocalDef>();
		public final Set<LocalDef> atomicLocalDecls = new LinkedHashSet<LocalDef>();
		
		public final Set<FieldDef> atomicFieldReassigns = new LinkedHashSet<FieldDef>();
		public final Set<FieldDef> atomicFieldDecls = new LinkedHashSet<FieldDef>();
		
		@Override
    	public Node leave(Node old, Node n, NodeVisitor v) {
    		
    		if(n instanceof X10LocalDecl_c) {
    			X10LocalDecl_c x10localdecl = (X10LocalDecl_c)n;
    			if(x10localdecl.localDef().flags() != null
    					&& x10localdecl.localDef().flags().contains(Flags.ATOMIC)) {
    				atomicLocalDecls.add(x10localdecl.localDef());
    			}
    		}
    		if(n instanceof X10FieldDecl_c) {
    			X10FieldDecl_c x10fieldDecl = (X10FieldDecl_c)n;
    			X10FieldDef def = x10fieldDecl.fieldDef();
    			if(def.flags() != null && def.flags().contains(Flags.ATOMIC)) {
    				atomicFieldDecls.add(def);
    			}
    		}
    		//TODO is it complete?
    		//see the assignment
    		if(n instanceof X10FieldAssign_c) {
    			X10FieldAssign_c fieldAssign = (X10FieldAssign_c)n;
    			FieldDef def = fieldAssign.fieldInstance().def();
    			if(def.flags() != null && def.flags().contains(Flags.ATOMIC)) {
    				atomicFieldReassigns.add(def);
    			}
    		}
    		if(n instanceof X10LocalAssign_c) {
    			X10LocalAssign_c localAssign = (X10LocalAssign_c)n;
    			LocalDef def = localAssign.local().localInstance().def();
    			if(def.flags() != null && def.flags().contains(Flags.ATOMIC)) {
    				atomicLocalReassigns.add(def);
    			}
    		}
    		//FIXME not sure is it correct
    		if(n instanceof ArrayAccessAssign_c) {
    			ArrayAccessAssign_c arrayAssign = (ArrayAccessAssign_c)n;
    			Expr array_expr = arrayAssign.array();
    			if(array_expr instanceof X10Local_c) {
    				X10Local_c x10local = (X10Local_c)array_expr;
    				LocalDef def = x10local.localInstance().def();
    				if(def.flags() != null && def.flags().contains(Flags.ATOMIC)) {
    					atomicLocalReassigns.add(def);
    				}
    			}
    			if(array_expr instanceof X10Field_c) {
    				X10Field_c x10field = (X10Field_c)array_expr;
    				FieldDef def = x10field.fieldInstance().def();
    				if(def.flags() != null && def.flags().contains(Flags.ATOMIC)) {
    					atomicFieldReassigns.add(def);
    				}
    			}
    		}
    		if(n instanceof SettableAssign_c) {
    			//Do we need to implement it
    		}
            return super.leave(old, n, v);
		}
		
		//get the escaped local defs and fields
    	public Set<LocalDef> escapedLocalDefs() {
    		Set<LocalDef> escaped = new LinkedHashSet<LocalDef>();
    		escaped.addAll(atomicLocalReassigns);
    		escaped.removeAll(atomicLocalDecls);
    		return escaped;
    	}
    	public Set<FieldDef> escapedFieldDefs() {
    		Set<FieldDef> escaped = new LinkedHashSet<FieldDef>();
    		escaped.addAll(atomicFieldReassigns);
    		escaped.removeAll(atomicFieldDecls);
    		return escaped;
    	}
    	public boolean hasEscaped() {
    		return !this.escapedFieldDefs().isEmpty() || !this.escapedLocalDefs().isEmpty();
    	}
    }
    
    class LocalAndFieldAccessVisitor extends NodeVisitor {

		public final Set<LocalDef> atomicLocalRefs = new LinkedHashSet<LocalDef>();
		public final Set<LocalDef> atomicLocalDecls = new LinkedHashSet<LocalDef>();
		
		public final Set<FieldDef> atomicFieldDefs = new LinkedHashSet<FieldDef>();
		public final Set<FieldDef> atomicFieldDecls = new LinkedHashSet<FieldDef>();
    	
    	@Override
    	public Node leave(Node old, Node n, NodeVisitor v) {
    		if(n instanceof X10Local_c) {
    			X10Local_c x10local = (X10Local_c)n;
    			if(x10local.localInstance() != null && x10local.localInstance().def() != null) {
    			  if(x10local.localInstance().def().flags() != null
    					  && x10local.localInstance().def().flags().contains(Flags.ATOMIC)) {
    				//System.out.println("   --- Local def has flags: " + x10local);
    				atomicLocalRefs.add(x10local.localInstance().def());
    			  }
    			}
    		}
    		if(n instanceof X10LocalDecl_c) {
    			X10LocalDecl_c x10localdecl = (X10LocalDecl_c)n;
    			if(x10localdecl.localDef().flags() != null
    					&& x10localdecl.localDef().flags().contains(Flags.ATOMIC)) {
    				atomicLocalDecls.add(x10localdecl.localDef());
    			}
    		}
    		if(n instanceof X10Field_c) {
    			X10Field_c x10field = (X10Field_c)n;
    			if(x10field.fieldInstance() != null && x10field.fieldInstance().def() != null) {
        		  FieldDef def = x10field.fieldInstance().def();
    			  if(def.flags() != null && def.flags().contains(Flags.ATOMIC)) {
    				atomicFieldDefs.add(def);
    			  }
    			}
    		}
    		if(n instanceof X10FieldDecl_c) {
    			X10FieldDecl_c x10fieldDecl = (X10FieldDecl_c)n;
    			X10FieldDef def = x10fieldDecl.fieldDef();
    			if(def.flags() != null && def.flags().contains(Flags.ATOMIC)) {
    				atomicFieldDecls.add(def);
    			}
    		}
            return super.leave(old, n, v);
        }
    	
    	public Set<LocalDef> escapedLocalDefs() {
    		Set<LocalDef> escaped = new LinkedHashSet<LocalDef>();
    		escaped.addAll(atomicLocalRefs);
    		escaped.removeAll(atomicLocalDecls);
    		return escaped;
    	}
    	public Set<FieldDef> escapedFieldDefs() {
    		Set<FieldDef> escaped = new LinkedHashSet<FieldDef>();
    		escaped.addAll(atomicFieldDefs);
    		escaped.removeAll(atomicFieldDecls);
    		return escaped;
    	}
    	public boolean hasEscaped() {
    		return !this.escapedFieldDefs().isEmpty() || !this.escapedLocalDefs().isEmpty();
    	}
    }
    
    private String getLocalLockName(String localName) {
    	return localName + LOCAL_LOCK;
    }
    
    private String getFieldAsyncLockName(String fieldName) {
    	return fieldName + FIELD_ASYNC_LOCK;
    }
    
    private List<Stmt> fieldAsyncLockCreation(Block methodBody) {
    	List<Stmt> statements = new LinkedList<Stmt>();
    	LocalAndFieldAccessVisitor visitor = new LocalAndFieldAccessVisitor();
//    	AsyncVisitor visitor = new AsyncVisitor();
    	methodBody.visit(visitor);
    	Set<FieldDef> lockFields = visitor.escapedFieldDefs();
    	if(!lockFields.isEmpty()) {
    		Position pos = methodBody.position();
    		Type lockType = this.lookUpType(ORDERED_LOCK);
    		//create locks for each field which maybe accessed in async
    		//var fieldAsyncLock:OrderedLock = new OrderedLock();
    		for(FieldDef lockField : lockFields) {
    			//init the lock acquisition statements
    			TypeNode typeNode = nf.CanonicalTypeNode(pos, lockType);
		        X10ConstructorInstance lockCI = findDefaultConstructor(lockType);
		    	Expr newLock = nf.New(pos, typeNode, Collections.<Expr>emptyList()).constructorInstance(lockCI).type(lockType);
		    	//create local field assignment
		    	String asyncLockStr = this.getFieldAsyncLockName(lockField.name().toString());
		    	Name asyncLockName = Context.makeFreshName(asyncLockStr);
                final LocalDef li = ts.localDef(pos, ts.Final(), Types.ref(lockType), asyncLockName);
                Id asyncLockId = nf.Id(pos, asyncLockName);
                final LocalDecl ld = nf.LocalDecl(pos, nf.FlagsNode(pos, ts.Final()),
                		nf.CanonicalTypeNode(pos, lockType), asyncLockId, newLock).
                		localDef(li);
    			statements.add(ld);
    		}
    	}
    	return statements;
    }
    
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
    
    private Type lookUpType(QName qname) {
    	try {
			return ts.systemResolver().findOne(qname);
		} catch (SemanticException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
    }
    
    private X10ConstructorInstance findDefaultConstructor(Type t) {
    	try {
			return ts.findConstructor(t, ts.ConstructorMatcher(t, Collections.<Type>emptyList(), context()));
		} catch (SemanticException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
    }
    
    private MethodInstance findMethodInThisContext(Type container, Name name, List<Type> argTypes) {
    	try {
			Collection<MethodInstance> collections = ts.findMethods(container, ts.MethodMatcher(container, name, 
					argTypes, this.context));
			assert collections.size() == 1;
			
			List<MethodInstance> mis = new LinkedList<MethodInstance>();
			mis.addAll(collections);
			assert mis.size() == 1;
			
			return mis.get(0);
		} catch (SemanticException e) {
			throw new RuntimeException(e);
		}
    }
    
    //create an empty list declaration statement
    private LocalDecl createEmptyLockListDeclaration(Position pos) {
    	Type lockType = this.lookUpType(ORDERED_LOCK);
    	X10ParsedClassType_c listType = (X10ParsedClassType_c)this.lookUpType(X10_LIST);
    	listType = (X10ParsedClassType_c) listType.typeArguments(Collections.<Type>singletonList(lockType));
    	X10ParsedClassType_c arrayListType = (X10ParsedClassType_c)this.lookUpType(X10_ARRAYLIST);
    	arrayListType = (X10ParsedClassType_c)arrayListType.typeArguments(Collections.<Type>singletonList(lockType));
    	//create the list
    	TypeNode arrayListTypeNode = nf.CanonicalTypeNode(pos, arrayListType);
        X10ConstructorInstance arrayListCI = findDefaultConstructor(arrayListType);
        //create an empty list
    	Expr newArrayList = nf.New(pos, arrayListTypeNode,
    			Collections.<Expr>emptyList()).constructorInstance(arrayListCI).type(lockType);
    	Name listName = Name.make(Context.getNewVarName().toString() + "_locklist");
    	Id listID = nf.Id(pos, listName);
    	LocalDef li = ts.localDef(pos, ts.Final(), Types.ref(listType), listName);
        LocalDecl emptyList = nf.LocalDecl(pos,
        		nf.FlagsNode(pos, ts.Final()), nf.CanonicalTypeNode(pos, listType),
        		listID, newArrayList).localDef(li);
        return emptyList;
    }
    
    //call emptyList.add(localdef)
    private List<Stmt> addLocalDefToList(Position pos, Type lockType, Set<LocalDef> localDefs,
    		Id listID, LocalDecl emptyList) {
    	List<Stmt> stmts = new LinkedList<Stmt>();
    	X10ParsedClassType_c listType = (X10ParsedClassType_c)this.lookUpType(X10_LIST);
    	listType = (X10ParsedClassType_c) listType.typeArguments(Collections.<Type>singletonList(lockType));
    	MethodInstance addmi = this.findMethodInThisContext(listType, LIST_ADD,
				Collections.<Type>singletonList(nf.X10CanonicalTypeNode(pos, lockType).type()));
    	for(LocalDef localDef : localDefs) {
    		Name localLockName = Context.makeFreshName( getLocalLockName(localDef.name().toString()));
    		Id localLockId = nf.Id(pos, localLockName);
            final LocalDef localLockDef = ts.localDef(pos, ts.Final(), Types.ref(lockType), localLockName);
    		final Local local = (Local) nf.Local(pos, localLockId).localInstance(localLockDef.asInstance()).
    		             type(lockType);
    		Local listLocal = nf.Local(pos, listID).localInstance(emptyList.localDef().asInstance());
    		Receiver receiver = listLocal.type(emptyList.type().type());
        	List<TypeNode> typeArgs = new LinkedList<TypeNode>();
        	typeArgs.add(nf.CanonicalTypeNode(pos, lockType));
        	List<Expr> args = Collections.<Expr>singletonList(local.type(lockType));
        	Id addID = nf.Id(pos, LIST_ADD);
        	X10Call addLockCall = nf.X10Call(pos, receiver, addID, typeArgs, args);
        	addLockCall = (X10Call) addLockCall.type(ts.Boolean());
        	
        	List<Ref<? extends Type>> addArgTypes = new LinkedList<Ref<? extends Type>>();
        	addArgTypes.add(Types.ref(lockType));
        	
        	addLockCall = addLockCall.methodInstance(addmi);
        	Stmt addLock = nf.Eval(pos, addLockCall);
    		//add to the list
        	stmts.add(addLock);
    	}
    	
    	return stmts;
    }
    
    //add local field to list
    private List<Stmt> addFieldDefToList(Position pos, Type lockType, Set<FieldDef> fieldDefs,
    		Id listID, LocalDecl emptyList) {
    	List<Stmt> stmts = new LinkedList<Stmt>();
    	X10ParsedClassType_c listType = (X10ParsedClassType_c)this.lookUpType(X10_LIST);
    	listType = (X10ParsedClassType_c) listType.typeArguments(Collections.<Type>singletonList(lockType));
    	MethodInstance addmi = this.findMethodInThisContext(listType, LIST_ADD,
				Collections.<Type>singletonList(nf.X10CanonicalTypeNode(pos, lockType).type()));
    	for(FieldDef fieldDef : fieldDefs) {
    		X10ClassType classType = this.context.currentClass();
    		Name fieldLockName = Context.makeFreshName(this.getFieldAsyncLockName(fieldDef.name().toString()));
    		Id fieldLockId = nf.Id(pos, fieldLockName);
    		final LocalDef localLockDef = ts.localDef(pos, ts.Final(), Types.ref(lockType), fieldLockName);
  		    final Local local = (Local) nf.Local(pos, fieldLockId).localInstance(localLockDef.asInstance()).
  		             type(lockType);
  		    Local listLocal = nf.Local(pos, listID).localInstance(emptyList.localDef().asInstance());
  		    Receiver receiver = listLocal.type(emptyList.type().type());
      	    List<TypeNode> typeArgs = new LinkedList<TypeNode>();
      	    typeArgs.add(nf.CanonicalTypeNode(pos, lockType));
      	    List<Expr> args = Collections.<Expr>singletonList(local.type(lockType));
      	    Id addID = nf.Id(pos, LIST_ADD);
      	    X10Call addLockCall = nf.X10Call(pos, receiver, addID, typeArgs, args);
      	    addLockCall = (X10Call) addLockCall.type(ts.Boolean());
      	    List<Ref<? extends Type>> addArgTypes = new LinkedList<Ref<? extends Type>>();
      	    addArgTypes.add(Types.ref(lockType));
      	    addLockCall = addLockCall.methodInstance(addmi);
      	    Stmt addLock = nf.Eval(pos, addLockCall);
     		//add to the list
      	    stmts.add(addLock);
    	}
    	return stmts;
    }
    
    //the lock acquire statement
    private Stmt lockAcquireStatement(Position pos, Type lockType, LocalDecl emptyList, Id listID) {
    	X10ParsedClassType_c listType = (X10ParsedClassType_c)this.lookUpType(X10_LIST);
    	listType = (X10ParsedClassType_c) listType.typeArguments(Collections.<Type>singletonList(lockType));

    	//lock-acquire statement
    	List<TypeNode> argTypeNode = new LinkedList<TypeNode>();
    	List<Expr> lockList = new LinkedList<Expr>();
    	lockList.add(nf.Local(pos, listID).localInstance(emptyList.localDef().asInstance()).type(listType));
    	Call acquireCall = this.staticCall(pos, lockType, ACQUIRE_LOCKS, argTypeNode, lockList, ts.Void(), this.context()); 
    	Stmt acquireLockStatement = nf.Eval(pos, acquireCall);
    	
    	return acquireLockStatement;
    }
    
    //the lock release statement
    private Stmt lockReleaseStatement(Position pos, Type lockType, LocalDecl emptyList, Id listID) {
    	X10ParsedClassType_c listType = (X10ParsedClassType_c)this.lookUpType(X10_LIST);
    	listType = (X10ParsedClassType_c) listType.typeArguments(Collections.<Type>singletonList(lockType));

    	//lock-release statement
    	List<TypeNode> argTypeNode = new LinkedList<TypeNode>();
    	List<Expr> lockList = new LinkedList<Expr>();
    	lockList.add(nf.Local(pos, listID).localInstance(emptyList.localDef().asInstance()).type(listType));
    	Call releaseCall = this.staticCall(pos, lockType, RELEASE_LOCKS, argTypeNode, lockList, ts.Void(), this.context());
    	Stmt releaseLockStatement = nf.Eval(pos, releaseCall);
    	
    	return releaseLockStatement;
    }
    
    //utility methods
    private List<Ref<? extends Type>> typesToTypeRefs(List<Type> types) {
    	List<Ref<? extends Type>> refs = new LinkedList<Ref<? extends Type>>();
    	for(Type t : types) {
    		refs.add(Types.ref(t));
    	}
    	return refs;
    }
    
    private boolean isPublic(Flags flags) {
    	return flags.contains(Flags.PUBLIC);
    }
    
    private boolean isAtomic(Flags flags) {
    	return flags.contains(Flags.ATOMIC);
    }
    
    private boolean isAtomicPlus(Flags flags) {
    	return flags.contains(Flags.ATOMICPLUS);
    }
    
    private boolean isCompilerGenerated(Node node) {
    	return node.position().isCompilerGenerated();
    }
}