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

public class X10LockMapAtomicityTranslator extends ContextVisitor {
	private final Synthesizer synth;
    private final AltSynthesizer altsynth;
	public X10LockMapAtomicityTranslator(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
		synth = new Synthesizer(nf, ts);
        altsynth = new AltSynthesizer(ts, nf);
	}

    private static final String LOCAL_VAR_LOCK = "_var_local_lock";
    private static final String CONST_FORMAL_NAME = "paramLock";
    private static final Name PARAM_LOCK_NAME = Name.make(CONST_FORMAL_NAME);
    
	private static final String OBJECT_LOCK_ID = "object_lock_id";
	private static final String CLASS_LOCK_ID = "class_lock_id";
    
    private static final QName ORDERED_LOCK = QName.make("x10.util.concurrent.OrderedLock");
    private static final QName ORDERED_LOCK_ID = QName.make("Int");
    private static final QName X10_LIST = QName.make("x10.util.List"); 
    private static final QName X10_ARRAYLIST = QName.make("x10.util.ArrayList");
    
    private static final Name LIST_ADD = Name.make("add");
    private static final Name GET_ORDERED_LOCK = Name.make("getOrderedLock");
    private static final Name GET_STATIC_ORDERED_LOCK = Name.make("getStaticOrderedLock");
    private static final Name CREATE_NEW_OBJ_LOCK = Name.make("createAndStoreObjectLock");
    private static final Name GET_INDEX = Name.make("getIndex");
    
    private static final Name ACQUIRE_LOCKS = Name.make("acquireLocksInOrder");
    private static final Name RELEASE_LOCKS = Name.make("releaseLocks");
    private static final Name ACQUIRE_1_LOCK = Name.make("acquireSingleLock");
    private static final Name RELEASE_1_LOCK = Name.make("releaseSingleLock");
    private static final Name ACQUIRE_2_LOCKS = Name.make("acquireTwoLocks");
    private static final Name RELEASE_2_LOCKS = Name.make("releaseTwoLocks");
    private static final Name ACQUIRE_3_LOCKS = Name.make("acquireThreeLocks");
    private static final Name RELEASE_3_LOCKS = Name.make("releaseThreeLocks");
    
    private static final Name CREATE_OBJ_LOCK = Name.make("createObjectLock");
    private static final Name CREATE_CLASS_LOCK = Name.make("createClassLock");
    private static final Name GET_OBJ_LOCK = Name.make("getObjectLock");
    private static final Name GET_CLASS_LOCK = Name.make("getClassLock");
    
    private static final Name PUSH_ATOMIC = Name.make("pushAtomic");
    private static final Name POP_ATOMIC = Name.make("popAtomic");
    
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
    	//add two int lock ids to the class, one for object id, the other for lock id
    	//remove the atomic symbol after visiting the class
    	if(n instanceof X10ClassDecl_c) {
    		X10ClassDecl_c x10clz = (X10ClassDecl_c)n;
    		Node node= this.visitX10ClassDecl(x10clz);
    		node = node.visit(new X10AtomicRemover(this.job, this.ts, this.nf));
    		return node;
    	}
    	//for those constructor of class that is associated with a lock id, rewrite the
    	//constructor calls.
    	if(n instanceof X10New_c) {
    		X10New_c x10newc = (X10New_c)n;
    		return this.visitX10New(x10newc);
    	}
    	//add lock declarations for formal parameters that are not associated with a lock id
    	if(n instanceof X10MethodDecl_c) {
    		X10MethodDecl_c x10method = (X10MethodDecl_c)n;
    		return this.visitX10Method(x10method);
    	}
    	//add lock ids for local vars that are accessed inside atomic sections
    	if(n instanceof Block_c) {
    		Block_c block = (Block_c)n;
    		return this.visitBlock_c(block);
    	}
    	//rewrite the atomic section, acquire/release locks
    	if(n instanceof Atomic_c) {
    		Atomic_c atomic = (Atomic_c)n;
    		return this.visitAtomic_c(atomic);
    	}
    	//add referred lock id vars to its environment
    	if(n instanceof Async_c) {
    		Async_c async = (Async_c)n;
    		return this.visitAsync_c(async);
    	}
    	//add referred lock id vars to its environment
    	if(n instanceof AtStmt_c) {
    		AtStmt_c atstmt = (AtStmt_c)n;
    		return this.visitAtStmt_c(atstmt);
    	}
    	
    	//add referred lock id vars to its environment
    	if(n instanceof AtEach_c) {
    		AtEach_c ateach = (AtEach_c)n;
    		return this.visitAtEach_c(ateach);
    	}
    	//add referred lock id vars to its environment
    	if(n instanceof AtHomeStmt_c) {
    		AtHomeStmt_c athome = (AtHomeStmt_c)n;
    		return this.visitAtHomeStmt_c(athome);
    	}
    	
    	return n;
    }
    
    private Node visitX10ClassDecl(X10ClassDecl_c n) throws SemanticException {
    	X10ClassDef classDef = n.classDef();
    	if(X10TypeUtils.skipProcessingClass(classDef.asType())) {
    		System.out.println("Skip x10 lib class: " + n.name() + " at: " + n.position());
    		return n;
    	}
    	Position pos = n.position();
		
		//create a new constructor for each existing one, and add
		//that to the class member list
		List<ClassMember> members = n.body().members();
		X10ClassType containerClass = n.classDef().asType();
		List<ClassMember> newMembers = new LinkedList<ClassMember>();
		Type orderLockClass =  X10TypeUtils.lookUpType(ts, ORDERED_LOCK);
		Type lockTypeId =  X10TypeUtils.lookUpType(ts, ORDERED_LOCK_ID);
		assert lockTypeId != null;
		
		//create the field OrderedLock:$object_lock_id = -1; (init value)
		Name lockIdName = Context.makeFreshName(OBJECT_LOCK_ID);
		FieldDecl lockIdFieldDecl = X10TypeUtils.createFieldDecl(nf, ts, pos, ts.Private(), lockTypeId,
				lockIdName, containerClass, nf.IntLit(pos, IntLit.INT, -1L).type(ts.Int()));
		
		//create the method getOrderedLock() {return OrderedLock.getObjectLock(this.$object_lock_id); }
		Receiver thiz = nf.This(pos).type(containerClass);
		Expr lockIdField = nf.Field(pos, thiz, lockIdFieldDecl.name()).fieldInstance(lockIdFieldDecl.fieldDef().asInstance()).type(lockTypeId);  
		Expr returnLock = this.synth.makeStaticCall(pos, orderLockClass, GET_OBJ_LOCK, Collections.<Expr>singletonList(lockIdField), orderLockClass, this.context());
		Stmt lockRetStmt = nf.Return(pos, returnLock);
		X10MethodDecl getLockMethodDecl = X10TypeUtils.createX10MethodDecl(nf, ts, pos, ts.Public(), containerClass,
				orderLockClass, GET_ORDERED_LOCK, Collections.<Stmt>singletonList(lockRetStmt));
		
		//add the lock field and get lock method to the class member list
		newMembers.add(lockIdFieldDecl);
		newMembers.add(getLockMethodDecl);
		
		//create the static lock field  private static Int class_lock_id = OrderedLock.createClassLock();
		Name staticLockIdName = Context.makeFreshName(CLASS_LOCK_ID);
		Expr createClassLock = this.synth.makeStaticCall(pos, orderLockClass, CREATE_CLASS_LOCK, lockTypeId, this.context());
		FieldDecl staticLockFieldDecl = X10TypeUtils.createFieldDecl(nf, ts, pos, ts.Private().Static(), lockTypeId,
				staticLockIdName, containerClass, createClassLock);
		
		//create a static method getStaticOrdered
		Receiver thizClass = nf.CanonicalTypeNode(pos, containerClass);
    	Expr staticField = nf.Field(pos, thizClass, staticLockFieldDecl.name()).fieldInstance(staticLockFieldDecl.fieldDef().asInstance()).type(lockTypeId);
    	Expr returnClassLock = this.synth.makeStaticCall(pos, orderLockClass, GET_CLASS_LOCK, Collections.<Expr>singletonList(staticField), orderLockClass, this.context());
    	Stmt staticLockRetStmt = nf.Return(pos, returnClassLock);
    	X10MethodDecl staticGetLockMethodDecl = X10TypeUtils.createX10MethodDecl(nf, ts, pos, ts.Public().Static(), containerClass,
    			orderLockClass, GET_STATIC_ORDERED_LOCK, Collections.<Stmt>singletonList(staticLockRetStmt));
    	
    	//add the static lock field and get lock method to the class member list
		newMembers.add(staticLockFieldDecl);
		newMembers.add(staticGetLockMethodDecl);
		
		//add existing method
		FieldDef fd = lockIdFieldDecl.fieldDef();
		for(ClassMember member : members) {
			newMembers.add(member);
			Position mpos = member.position();
			//add a new constructor
			if(member instanceof X10ConstructorDecl_c) {
				X10ConstructorDecl_c x10const = (X10ConstructorDecl_c)member;
				//let it take an additional argument, OrderedLock
				//transform:  this(args ) ---> 
				//     this(args, lock:OrderedLock) { statement,  this.lock = lock; }
				List<Formal> newFormals = new LinkedList<Formal>();
				for(Formal f : x10const.formals()) {
					newFormals.add(f);
				}
			    //add the new OrderedLock type parameter
				Id paramId = nf.Id(mpos, PARAM_LOCK_NAME);
				TypeNode paramTypeNode = nf.CanonicalTypeNode(mpos, orderLockClass);
				LocalDef li = ts.localDef(mpos, Flags.FINAL, Types.ref(orderLockClass), PARAM_LOCK_NAME);
				Formal lockFormal = nf.Formal(mpos, nf.FlagsNode(mpos, Flags.FINAL), paramTypeNode, paramId).localDef(li);
				//add to the parameter list
				newFormals.add(lockFormal);
				//create the new constructor def
				ConstructorDef x10ci = x10const.constructorDef();
				List<Ref<? extends Type>> newFormalTypes = new LinkedList<Ref<? extends Type>>();
				newFormalTypes.addAll(x10ci.formalTypes());
				newFormalTypes.add(Types.ref(orderLockClass));
				
				ConstructorDef ci = ts.constructorDef(mpos, Types.ref(x10ci.container().get().toClass()),
						x10ci.flags(), newFormalTypes, x10ci.offerType());
				
				//modifiy the body, and add statement this.$lockid = param_lock.getIndex();	
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
				
			    //Expr getLockId = (Expr) nf.Return(mpos, getLockCall);
				FieldAssign fieldAssign =  nf.FieldAssign(mpos, nf.This(mpos).type(fd.asInstance().container()),
						nf.Id(mpos, fd.name()), Assign.ASSIGN, getLockIdCall);
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
		//set the class body
		ClassBody newBody = n.body().members(newMembers);
		n = (X10ClassDecl_c) n.body(newBody);
		
		return n;
    }
    
    private Node visitX10New(X10New_c n) throws SemanticException {
    	X10ParsedClassType_c clazzType = Types.fetchX10ClassType(n.constructorInstance().returnType());
    	//skip calling the constructors of a class being skipped
    	if(X10TypeUtils.skipProcessingClass(clazzType)) {
    		return n;
    	}
    	
    	Position pos = n.position();
    	//the type of OrderedLock, will be used as an additional argument
    	Type lockType = X10TypeUtils.lookUpType(ts, ORDERED_LOCK);
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
    	List<Ref<? extends Type>> refs = X10TypeUtils.typesToTypeRefs(newTypes);

    	X10ConstructorDef cdef = ts.constructorDef(pos, Types.ref(clazzType), x10ci.flags(), refs, x10ci.offerType());
    	X10ConstructorInstance ci = ts.createConstructorInstance(pos, Types.ref(cdef));
    	ci = ci.formalTypes(newTypes);
    	n = (X10New_c) n.constructorInstance(ci);
    	
    	//change the constructor in two different cases
    	//  1. new (atomicplus C)(args)   ==> new (atomicplus C)(args,  container.getOrderedLock());
    	//  2. new C(args)  ==>  new C(args, CreateNewLock); 
    	FlagsNode flagNode = n.objectType().getFlagsNode();
    	if(flagNode != null && flagNode.flags().contains(/*Flags.ATOMICPLUS*/ Flags.TYPE_FLAG)) {
    		List<Expr> newArguments = new LinkedList<Expr>();
    		newArguments.addAll(n.arguments());
    		//check is it in a static method
    		boolean isInStaticMethod = this.isInStaticMethod();
    		//change the newcall
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
    		//in create the lock statement
    		getLockCall = getLockCall.methodInstance(methodDef.asInstance());
    		Expr getLockExpr = getLockCall.type(lockType);
    		newArguments.add(getLockExpr);
    		n = (X10New_c)n.arguments(newArguments);
    	} else {
	    	List<Expr> newArguments = new LinkedList<Expr>();
	    	newArguments.addAll(n.arguments());
	    	
	    	//create a new lock by calling OrderedLock.createAndStoreObjectLock
	    	Call createCall = this.synth.makeStaticCall(pos, lockType, CREATE_NEW_OBJ_LOCK, lockType, this.context());
	    	
	    	newArguments.add(createCall);
	    	n = (X10New_c) n.arguments(newArguments);
    	}
    	
    	return n;
    }
    
    private Node visitX10Method(X10MethodDecl_c n) throws SemanticException {
    	if(n.body() == null) {
    		return n;
    	}
    	List<Stmt> newStatements = new LinkedList<Stmt>();
    	Type lockType = X10TypeUtils.lookUpType(ts, ORDERED_LOCK);
    	Type lockIdType = X10TypeUtils.lookUpType(ts, ORDERED_LOCK_ID);
    	
    	//get a list of locals that are accessed in atomic sections
    	AtomicLocalAndFieldAccessVisitor visitor = new AtomicLocalAndFieldAccessVisitor();
    	n.body().visit(visitor);
    	Set<X10LocalDef> localsInAtomics = visitor.localsInAtomic;
    	
    	//add local declaration for formals that do not have locks
    	List<Formal> formals = n.formals();
    	for(Formal formal : formals) {
    		//if the parameter is not accessed from an atomic section, no lock is needed
    		if(!localsInAtomics.contains(formal.localDef())) {
    			continue;
    		}
		    Position pos = formal.position();
    		Type type = formal.type().type();
    		X10ParsedClassType_c clazzType = Types.fetchX10ClassType(type);
    		//skip the parameter with non-class type
    		if(clazzType == null) {
    			continue;
    		}
    		//Note, only allocate locks for parameters that do not have an associated lock
    		//parameter with associated locks will be acquired via "formal.getOrderedLock"
    		//before it is used.
    		if(X10TypeUtils.skipProcessingClass(type)) {
       		    //create new OrderedLock();
        	    Expr newLockId =  //X10TypeUtils.createNewObjectByDefaultConstructor(nf, ts, this.context, pos, lockType);
        	        this.synth.makeStaticCall(pos, lockType, CREATE_OBJ_LOCK, lockIdType, this.context());
        	    //create local field assignment
        	    Name lockName = Context.makeFreshName( getLocalVarLockName(formal.name().toString()));
        	    //create the local declaration statement
                final LocalDecl ld = X10TypeUtils.createLocalDecl(nf, ts, pos, ts.Final(), lockIdType, lockName, newLockId);
                newStatements.add(ld);
    		}
    	}
		//add other statements
    	newStatements.addAll(n.body().statements());
        Block newBlock = nf.Block(n.position(), newStatements);
        
        //acquiring and releasing locks for atomic methods
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
        	//see the lock number
        	int lockNum = x10formalThatHaveLocks.size() + 1; //number of formal + this lock
        	//create the atomic section push/pop statements to avoid use async inside an atomic section
    		Stmt pushAtomic = nf.Eval(pos, call(pos, PUSH_ATOMIC, ts.Void()));
    		Stmt popAtomic = nf.Eval(pos, call(pos, POP_ATOMIC, ts.Void()));
    		
        	if(lockNum <= 3) {
        		//use the shortcut, just acquire the lock without creating a lock list
        		Stmt lockAcquireStmt = this.acquireLocksAtOnce(pos, lockType, lockNum, x10formalThatHaveLocks, isStatic);
        		//x10formalThatHaveLocks, and this //put the lock acquire statement here
        		Stmt lockReleaseStmt = this.releaseLocksAtOnce(pos, lockType, lockNum, x10formalThatHaveLocks, isStatic);
        		//put the lock acquire statement here
        		
        		List<Stmt> tryStatements = new LinkedList<Stmt>();
        		tryStatements.add(lockAcquireStmt);
        		tryStatements.add(pushAtomic);
        		tryStatements.addAll(newBlock.statements());
        		
        		Block tryBlock = nf.Block(pos, tryStatements);
            	Block finallyBlock = nf.Block(pos, popAtomic, lockReleaseStmt);
            	Try tryStmt = nf.Try(pos, tryBlock, Collections.<Catch>emptyList(), finallyBlock);
            	
            	List<Stmt> statements = new LinkedList<Stmt>();
            	statements.add(tryStmt);
            	
            	//create a new block here
            	newBlock = nf.Block(pos, statements);
        		
        	} else {
        		//acquire class lock, and all parameters without an asociated lock
            	
            	LocalDecl emptyLockList = this.createEmptyLockListDeclaration(pos);
            	Stmt acquireClassLock = this.acquireClassOrderedLock(pos, lockType, emptyLockList.name(), emptyLockList, isStatic);
            	List<Stmt> addMethodFormalsToLockStmts = this.addLocksForFormals(pos, lockType, x10formalThatHaveLocks,
        				emptyLockList.name(), emptyLockList);
            	
            	Stmt lockAcquireStmt = this.lockAcquireStatement(pos, lockType, emptyLockList, emptyLockList.name());
        		Stmt lockReleaseStmt = this.lockReleaseStatement(pos, lockType, emptyLockList, emptyLockList.name());
            	
        		//assemble the code block
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
        	System.out.println("process atomic method in X10LinkedAtomicityTranslator, pos: " + n.position());
        	System.out.println("  remove atomic: " + n.position());
        }
    	
    	return n.body(newBlock);
    }
    
    private Node visitBlock_c(Block_c block) throws SemanticException {
    	List<Stmt> statements = block.statements();
    	AtomicLocalAndFieldAccessVisitor visitor = new AtomicLocalAndFieldAccessVisitor();
    	block.visit(visitor);
    	X10AtomicLockLocalCollector collector = new X10AtomicLockLocalCollector();
    	block.visit(collector);
    	
    	Set<X10LocalDef> localsInAtomic = visitor.localsInAtomic; 
    	Set<LocalDef> lockLocalDefs = collector.getLockLocals();
    	
    	//create a list of statements, adding a lock for each atomic field
    	Type lockType = X10TypeUtils.lookUpType(ts, ORDERED_LOCK);
    	Type lockIdType = X10TypeUtils.lookUpType(ts, ORDERED_LOCK_ID);
    	List<Stmt> newStatements = new LinkedList<Stmt>();
    	for(Stmt stmt : statements) {
    		newStatements.add(stmt);
    		//create a lock field, and add the local declaration statement
    		//just after it #FIXME is this complete?
    		if(stmt instanceof X10LocalDecl_c) {
    			//only allocate a lock for the var if it is accessed in an atomic section
    			X10LocalDecl_c localDecl = (X10LocalDecl_c)stmt;
    			if(localsInAtomic.contains(localDecl.localDef())) 
    			{
    				//it must be an integer
    				LocalDef def = findLockLocalDef(localDecl.localDef().name().toString(), lockLocalDefs);
    				assert def != null;
    				Position pos = stmt.position();
    				//for local associated with locks, just grab its lock via "getOrderedLock"
    				//otherwise, allocate a lock for it.
    				Type localDefType = localDecl.declType();
    				Expr lockExpression = null; //return an int
    				if(X10TypeUtils.isSkippedOrPrimitiveType(localDefType)) {
    				    //lockExpression = this.synth.makeStaticCall(pos, lockType, CREATE_NEW_OBJ_LOCK, lockType, this.context);
    				    	//X10TypeUtils.createNewObjectByDefaultConstructor(nf, ts, this.context, pos, lockType);
    					//System.out.println(lockIdType + ",  ");
    				    lockExpression = this.synth.makeStaticCall(pos, lockType, CREATE_OBJ_LOCK, lockIdType, this.context());
    				} else {
    	                Local local = (Local) nf.Local(pos, localDecl.name()).type(localDefType);
    	                local = local.localInstance(localDecl.localDef().asInstance());
    	                X10Call getLockCall = nf.X10Call(pos,  local,
    	        				nf.Id(pos, GET_ORDERED_LOCK), Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList());
    	                getLockCall = (X10Call) getLockCall.type(lockType);
    	    			X10MethodDef methodDef = ts.methodDef(pos, Types.ref(Types.fetchX10ClassType(localDefType)),
    	    		           ts.Public(), Types.ref(lockType), GET_ORDERED_LOCK,
    	    		            Collections.<Ref<? extends Type>>emptyList(),  null);
    	        		getLockCall = getLockCall.methodInstance(methodDef.asInstance());
    	        		
    	        		//get the lock index
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
    		    	//create local field assignment
    		    	Name lockName = Context.makeFreshName( getLocalVarLockName(localDecl.name().toString()));
    		        final Id varId = nf.Id(pos, def.name());
    		        final LocalDecl ld = nf.LocalDecl(pos, nf.FlagsNode(pos, ts.Final()),
    		        		nf.CanonicalTypeNode(pos, def.type().get()), varId, lockExpression).
    		        		localDef(def);
                    //add to the statement
                    newStatements.add(ld);
    			}
    		}
    	}
    	//change the statements in the block
    	block = (Block_c) block.statements(newStatements);
    	
    	return block;
    }
    
    private Node visitAtomic_c(Atomic_c atomic) throws SemanticException {
    	Stmt body = atomic.body; //it is a block_c type for sure
    	//return if there are no variables associated with this atomic section
    	if(atomic.identifiers == null || atomic.identifiers.isEmpty()) {
    		return atomic;
    	}
    	//get all formal declarations
    	List<String> formalNames = X10TypeUtils.getAllFormalNames(this.context.currentCode());
    	
    	List<X10LocalDef> localdefs = atomic.getLocalsInAtomic();
    	List<X10FieldDef> x10fields = atomic.getFieldsInAtomic();
    	
    	//further divide x10locals into 2 parts, local vars, and formals
    	List<X10LocalDef> x10locals = new LinkedList<X10LocalDef>();
    	List<X10LocalDef> x10formalThatHaveLocks = new LinkedList<X10LocalDef>();
    	for(X10LocalDef localdef : localdefs) {
    		if(formalNames.contains(localdef.name().toString())
    				&& /*have lock*/ !X10TypeUtils.skipClassByName(localdef.type().get().toString())) {
    			x10formalThatHaveLocks.add(localdef);
    		} else {
    			x10locals.add(localdef);
    		}
    	}
    	//divide x10fields into 2 parts, static fields, and non-static fields
    	//List<X10FieldDef> x10staticFields = new LinkedList<X10FieldDef>();
    	//List<X10FieldDef> x10nonStaticFields = new LinkedList<X10FieldDef>();
    	boolean accessPrimitiveOrSkippedClass = false;
    	//also classify the fields that have locks to one group.
    	List<X10FieldDef> x10FieldsHaveLock = new LinkedList<X10FieldDef>();
    	for(X10FieldDef fieldDef : x10fields) {
    		if(fieldDef.flags().contains(Flags.STATIC)) {
    			//x10staticFields.add(fieldDef);
    		} else {
    			//x10nonStaticFields.add(fieldDef);
    		}
    		//add to the fields that have lock
    		if(X10TypeUtils.isPrimitiveType(fieldDef.type().get())
    				|| X10TypeUtils.skipClassByName(fieldDef.type().get().fullName().toString())) {
    			continue;
    		} else {
    			x10FieldsHaveLock.add(fieldDef);
    		}
    	}
    	
    	if(x10FieldsHaveLock.size() != x10fields.size()) {
    		accessPrimitiveOrSkippedClass = true;
    	}
    	
    	Position pos = atomic.position();
    	Type lockType = X10TypeUtils.lookUpType(ts, ORDERED_LOCK);
    	Type lockIdType = X10TypeUtils.lookUpType(ts, ORDERED_LOCK_ID);
    	LocalDecl emptyLockList = this.createEmptyLockListDeclaration(pos);

    	//local defs
    	List<Stmt> addLocalsToLockStmts = this.addLocalDefToList(pos, lockType, lockIdType, x10locals,
				emptyLockList.name(), emptyLockList);
		//grab locks from formals
		List<Stmt> addMethodFormalsToLockStmts = this.addLocksForFormals(pos, lockType, x10formalThatHaveLocks,
				emptyLockList.name(), emptyLockList);

		//grab locks from fields (both static and non-static fields)
		//FIXME first acquire this lock (is it needed?)
		//note: we do not need to grab the static lock, in X10 all static fields are by default final.
		Stmt acquireThisLocal = null;
		
		//get it
		
//		if(!x10nonStaticFields.isEmpty()) {
//			boolean hasSkippedClass = false;
//			for(X10FieldDef field : x10nonStaticFields) {
//				if(X10TypeUtils.isSkippedOrPrimitiveType(field.type().get())) {
//					hasSkippedClass = true;
//					break;
//				}
//			}
//			if(hasSkippedClass) {
//			    acquireThisLocal = this.acquireClassOrderedLock(pos, lockType, emptyLockList.name(), emptyLockList, false);
//			}
//		}
		if(accessPrimitiveOrSkippedClass || atomic.hasThisInAtomic()) {
			acquireThisLocal = this.acquireClassOrderedLock(pos, lockType, emptyLockList.name(), emptyLockList, false);
		}

		//grab locks for fields that have locks
		List<Stmt> acquireLocksForEachField = this.addAtomicSetLockbyFieldToList(pos, lockType,
				x10FieldsHaveLock, emptyLockList.name(), emptyLockList);
		
		//create the lock acquiring and releasing statements
		Stmt lockAcquireStmt = this.lockAcquireStatement(pos, lockType, emptyLockList, emptyLockList.name());
		Stmt lockReleaseStmt = this.lockReleaseStatement(pos, lockType, emptyLockList, emptyLockList.name());
    	
    	//create the atomic section push/pop statements to avoid use async inside an atomic section
		Stmt pushAtomic = nf.Eval(pos, call(pos, PUSH_ATOMIC, ts.Void()));
		Stmt popAtomic = nf.Eval(pos, call(pos, POP_ATOMIC, ts.Void()));
		
		//assemble the code block
    	List<Stmt> tryStatements = new LinkedList<Stmt>();
    	tryStatements.add(lockAcquireStmt);
    	tryStatements.add(pushAtomic);
    	tryStatements.add(atomic.body());
    	
    	Block tryBlock = nf.Block(pos, tryStatements);
    	Block finallyBlock = nf.Block(pos, popAtomic, lockReleaseStmt);
    	Try tryStmt = nf.Try(pos, tryBlock, Collections.<Catch>emptyList(), finallyBlock);
    	
    	List<Stmt> statements = new LinkedList<Stmt>();
    	statements.add(emptyLockList);
    	statements.addAll(addLocalsToLockStmts);
    	statements.addAll(addMethodFormalsToLockStmts);
    	if(acquireThisLocal != null) {
    	    statements.add(acquireThisLocal);
    	}
    	statements.addAll(acquireLocksForEachField);
    	statements.add(tryStmt);
    	
    	System.out.println("process atomic in X10LockMapAtomicityTranslator, pos: " + pos);
    	
    	Block newBlock = nf.Block(pos, statements);
    	return atomic.body(newBlock); //note, will get rid of this in visiting blocks, ugly hack!
    }
    
    private Node visitAsync_c(Async_c async) {
    	//only fetch locals that are not defined inside async, but used in atomic sections
    	X10AtomicLockLocalCollector collector = new X10AtomicLockLocalCollector();
    	async.visit(collector);
    	Set<LocalDef> localLocks = collector.getEscapedLockLocals();
    	
    	List<VarInstance<? extends VarDef>> referredVars = new LinkedList<VarInstance<? extends VarDef>>();
    	Position pos = async.position();
    	//must use the same reference! since in ClosureRemover, it use Object.equals to compare
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
    
    //ugly repetition, need to remove (but not easy)
    private Node visitAtEach_c(AtEach_c ateach) {
    	System.out.println("visting at each ....");
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
    	System.out.println("visting at each home stmt ....");
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
    
    public static String getLocalVarLockName(String localName) {
    	return localName + LOCAL_VAR_LOCK;
    }
    
    public static boolean isLocalLock(String name) {
    	return name.indexOf(LOCAL_VAR_LOCK) != -1;
    }
    
    public static LocalDef findLockLocalDef(String varName, Set<LocalDef> lockDefs) {
    	String lockName = getLocalVarLockName(varName);
    	for(LocalDef lockDef : lockDefs) {
    		if(lockDef.name().toString().indexOf(lockName) != -1) {
    			return lockDef;
    		}
    	}
    	return null;
    }
    
    private List<X10LocalDef> getLocalsInAtomicSection(List<Stmt> stmts) {
    	List<X10LocalDef> locals = new LinkedList<X10LocalDef>();
    	for(Stmt stmt : stmts) {
    		if(stmt instanceof Atomic_c) {
    			System.out.println(" @getLocalsInAtomicSection---  " + stmt + ",  type: " + stmt.getClass());
    			Atomic_c atomic = (Atomic_c)stmt;
    			locals.addAll(atomic.getLocalsInAtomic());
    			System.out.println("     adding locals: " + atomic.getLocalsInAtomic());
    		}
    	}
    	return locals;
    }
    
    //create an empty list declaration statement
    private LocalDecl createEmptyLockListDeclaration(Position pos) {
    	Type lockType = X10TypeUtils.lookUpType(ts, ORDERED_LOCK);
    	X10ParsedClassType_c listType = (X10ParsedClassType_c)X10TypeUtils.lookUpType(ts, X10_LIST);
    	listType = (X10ParsedClassType_c) listType.typeArguments(Collections.<Type>singletonList(lockType));
    	X10ParsedClassType_c arrayListType = (X10ParsedClassType_c)X10TypeUtils.lookUpType(ts, X10_ARRAYLIST);
    	arrayListType = (X10ParsedClassType_c)arrayListType.typeArguments(Collections.<Type>singletonList(lockType));
    	//create the list
    	TypeNode arrayListTypeNode = nf.CanonicalTypeNode(pos, arrayListType);
        X10ConstructorInstance arrayListCI = X10TypeUtils.findDefaultConstructor(ts, this.context, arrayListType);
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
    
    //add the lock for local variables to the empty lock list
    private List<Stmt> addLocalDefToList(Position pos, Type lockType, Type lockIdType, List<X10LocalDef> localDefs,
    		Id listID, LocalDecl emptyList) throws SemanticException {
    	List<Stmt> stmts = new LinkedList<Stmt>();
    	X10ParsedClassType_c listType = (X10ParsedClassType_c)X10TypeUtils.lookUpType(ts, X10_LIST);
    	listType = (X10ParsedClassType_c) listType.typeArguments(Collections.<Type>singletonList(lockType));
    	MethodInstance addmi = X10TypeUtils.findMethod(ts, this.context, listType, LIST_ADD,
				Collections.<Type>singletonList(nf.X10CanonicalTypeNode(pos, lockType).type()));
    	for(LocalDef localDef : localDefs) {
    		//Call OrderedLock.getObjectLock(local)
    		Name localLockName = Context.makeFreshName( getLocalVarLockName(localDef.name().toString()));
    		Id localLockId = nf.Id(pos, localLockName);
            final LocalDef localLockDef = ts.localDef(pos, ts.Final(), Types.ref(lockIdType), localLockName);
    		final Local localid = (Local) nf.Local(pos, localLockId).localInstance(localLockDef.asInstance()).
    		             type(lockIdType);
    		//create a lock object
    		Call objLock = this.synth.makeStaticCall(pos, lockType, GET_OBJ_LOCK, Collections.<Expr>singletonList(localid.type(lockIdType)),
    				lockType, this.context());
    		//add the lock to the list
    		Local listLocal = nf.Local(pos, listID).localInstance(emptyList.localDef().asInstance());
    		Receiver receiver = listLocal.type(emptyList.type().type());
        	List<TypeNode> typeArgs = new LinkedList<TypeNode>();
        	typeArgs.add(nf.CanonicalTypeNode(pos, lockType));
        	List<Expr> args = Collections.<Expr>singletonList(objLock.type(lockType));
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
    
    //add the lock for formals to the empty lock list
    private List<Stmt> addLocksForFormals(Position pos, Type lockType, List<X10LocalDef> formalDefs, Id listID,
    		LocalDecl emptyList) {
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
		    
    		//add to the list
    		Local listLocal = nf.Local(pos, listID).localInstance(emptyList.localDef().asInstance());
    		Receiver receiver = listLocal.type(emptyList.type().type());
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
    		//add to the list
        	stmts.add(addLock);
    	}
    	
    	return stmts;
    }
    
    //grab field.getOrderedLock for each accessed field
    private List<Stmt> addAtomicSetLockbyFieldToList(Position pos, Type lockType, List<X10FieldDef> fieldDefs,
    		Id listID, LocalDecl emptyList) {
    	List<Stmt> stmts = new LinkedList<Stmt>();
    	X10ParsedClassType_c listType = (X10ParsedClassType_c)X10TypeUtils.lookUpType(ts, X10_LIST);
    	listType = (X10ParsedClassType_c) listType.typeArguments(Collections.<Type>singletonList(lockType));
    	MethodInstance addmi = X10TypeUtils.findMethod(ts, this.context, listType, LIST_ADD,
				Collections.<Type>singletonList(nf.X10CanonicalTypeNode(pos, lockType).type()));
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
    			
    		getLockCall = (X10Call) getLockCall.type(lockType);
  		    getLockCall = getLockCall.methodInstance(methodDef.asInstance());
    		//add the static lock
    		Local listLocal = nf.Local(pos, listID).localInstance(emptyList.localDef().asInstance());
    		Receiver receiver = listLocal.type(emptyList.type().type());
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
    		//add to the list
        	stmts.add(addLock);
    	}
    	return stmts;
    }
    
    //acquire class locks
    private Stmt acquireClassOrderedLock(Position pos, Type lockType, Id listID, LocalDecl emptyList, boolean isStatic) {
    	List<Stmt> stmts = new LinkedList<Stmt>();
    	X10ParsedClassType_c listType = (X10ParsedClassType_c)X10TypeUtils.lookUpType(ts, X10_LIST);
    	listType = (X10ParsedClassType_c) listType.typeArguments(Collections.<Type>singletonList(lockType));
    	MethodInstance addmi = X10TypeUtils.findMethod(ts, this.context, listType, LIST_ADD,
				Collections.<Type>singletonList(nf.X10CanonicalTypeNode(pos, lockType).type()));
    	
    	Type containerClass = this.context.currentClass();
    	assert containerClass instanceof ContainerType;
    	ContainerType clazzType = (ContainerType)containerClass;
    	//Local l = nf.Local(pos, nf.Id(pos, localDef.name())).localInstance(localDef.asInstance());
    		
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
		    
    	//add to the list
    	Local listLocal = nf.Local(pos, listID).localInstance(emptyList.localDef().asInstance());
    	Receiver receiver = listLocal.type(emptyList.type().type());
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
    		
        return addLock;
    }
    
    private Stmt acquireLocksAtOnce(Position pos, Type lockType, int lockNum, List<X10LocalDef> localWithLocks, boolean isStatic) {
    	return acquireOrReleaseLocks(pos, lockType, lockNum, localWithLocks, isStatic, true);
    }
    
    private Stmt releaseLocksAtOnce(Position pos, Type lockType, int lockNum, List<X10LocalDef> localWithLocks, boolean isStatic) {
    	return acquireOrReleaseLocks(pos, lockType, lockNum, localWithLocks, isStatic, false);
    }
    
    private Stmt acquireOrReleaseLocks(Position pos, Type lockType, int lockNum, List<X10LocalDef> localWithLocks, boolean isStatic, boolean isAcquiring) {
    	assert lockNum <= 3;
    	assert lockNum == localWithLocks.size() + 1;
    	Name methodName = null;
    	if(lockNum == 1) {
    		methodName = isAcquiring ? ACQUIRE_1_LOCK : RELEASE_1_LOCK;
    	} else if (lockNum == 2) {
    		methodName = isAcquiring ? ACQUIRE_2_LOCKS : RELEASE_2_LOCKS;
    	} else {
    		methodName = isAcquiring ? ACQUIRE_3_LOCKS : RELEASE_3_LOCKS;
    	}
    	
    	List<TypeNode> argTypeNode = new LinkedList<TypeNode>();
    	List<Expr> locks = new LinkedList<Expr>();
    	
    	//first add the class lock
    	Type containerClass = this.context.currentClass();
    	assert containerClass instanceof ContainerType;
    	ContainerType clazzType = (ContainerType)containerClass;
    	Receiver thiz = null;
    	X10Call getLockCall = null;
    	X10MethodDef methodDef = null;
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
		//add to the arg list
		locks.add(getLockCall);
		
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
		
		//assert the length is equal
		assert locks.size() == lockNum;
    	
    	Call acquireCall = this.staticCall(pos, lockType, methodName, argTypeNode, locks, ts.Void(), this.context());
    	Stmt acquireLockStmt = nf.Eval(pos, acquireCall);
    	
    	return acquireLockStmt;
    }
    
    //create the lock acquiring statement
    private Stmt lockAcquireStatement(Position pos, Type lockType, LocalDecl emptyList, Id listID) {
    	X10ParsedClassType_c listType = (X10ParsedClassType_c)X10TypeUtils.lookUpType(ts, X10_LIST);
    	listType = (X10ParsedClassType_c) listType.typeArguments(Collections.<Type>singletonList(lockType));

    	//lock-acquire statement
    	List<TypeNode> argTypeNode = new LinkedList<TypeNode>();
    	List<Expr> lockList = new LinkedList<Expr>();
    	lockList.add(nf.Local(pos, listID).localInstance(emptyList.localDef().asInstance()).type(listType));
    	Call acquireCall = this.staticCall(pos, lockType, ACQUIRE_LOCKS, argTypeNode, lockList, ts.Void(), this.context()); 
    	Stmt acquireLockStatement = nf.Eval(pos, acquireCall);
    	
    	return acquireLockStatement;
    }
    
    //create the lock releasing statement
    private Stmt lockReleaseStatement(Position pos, Type lockType, LocalDecl emptyList, Id listID) {
    	X10ParsedClassType_c listType = (X10ParsedClassType_c)X10TypeUtils.lookUpType(ts, X10_LIST);
    	listType = (X10ParsedClassType_c) listType.typeArguments(Collections.<Type>singletonList(lockType));

    	//lock-release statement
    	List<TypeNode> argTypeNode = new LinkedList<TypeNode>();
    	List<Expr> lockList = new LinkedList<Expr>();
    	lockList.add(nf.Local(pos, listID).localInstance(emptyList.localDef().asInstance()).type(listType));
    	Call releaseCall = this.staticCall(pos, lockType, RELEASE_LOCKS, argTypeNode, lockList, ts.Void(), this.context());
    	Stmt releaseLockStatement = nf.Eval(pos, releaseCall);
    	
    	return releaseLockStatement;
    }
    
    //utility method to sythensize a static method call
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
    
    private boolean isInStaticMethod() {
    	X10CodeDef currentCode = this.context.currentCode();
    	if(currentCode instanceof X10MethodDef_c) {
    		X10MethodDef_c methodDef = (X10MethodDef_c)currentCode;
    		return methodDef.flags() != null && methodDef.flags().contains(Flags.STATIC);
    	}
    	return false;
    }
}
