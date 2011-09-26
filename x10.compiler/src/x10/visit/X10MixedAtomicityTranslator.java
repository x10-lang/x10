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
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.FieldAssign;
import polyglot.ast.FieldDecl;
import polyglot.ast.FlagsNode;
import polyglot.ast.Formal;
import polyglot.ast.Id;
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
import polyglot.types.MethodDef;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.Async_c;
import x10.ast.Atomic_c;
import x10.ast.X10Call;
import x10.ast.X10ClassDecl_c;
import x10.ast.X10ConstructorDecl_c;
import x10.ast.X10FieldDecl_c;
import x10.ast.X10Field_c;
import x10.ast.X10LocalDecl_c;
import x10.ast.X10MethodDecl;
import x10.ast.X10MethodDecl_c;
import x10.ast.X10New_c;
import x10.types.MethodInstance;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10CodeDef;
import x10.types.X10ConstructorDef;
import x10.types.X10ConstructorInstance;
import x10.types.X10FieldDef;
import x10.types.X10MethodDef;
import x10.types.X10MethodDef_c;
import x10.types.X10ParsedClassType_c;
import x10.util.AltSynthesizer;
import x10.util.Synthesizer;
import x10.util.X10TypeUtils;


/**
 * An experimental implementation. All functionalities implemented
 * in this class is superseded by <code>X10LockMapAtomicityTranslator</code>.
 * Please use <code>X10LockMapAtomicityTranslator</code> instead.
 * 
 * The only difference is this class tries to infer every accessed
 * field accessed in an atomic section as protected fields  or var.
 * This is not really the programmers' intention.
 *
 * @author Sai Zhang (szhang@cs.washington.edu)
 *
 */

@Deprecated
public class X10MixedAtomicityTranslator extends ContextVisitor {
	
	private final Synthesizer synth;
    private final AltSynthesizer altsynth;
    public X10MixedAtomicityTranslator(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        synth = new Synthesizer(nf, ts);
        altsynth = new AltSynthesizer(ts, nf);
    }
    
    private static final String ATOMICSET_LOCK_NAME = "atomicset_lock";
    private static final String STATIC_LOCK_NAME = "static_lock";
    private static final String LOCAL_VAR_LOCK = "_var_local_lock";
    private static final String FIELD_LOCAL_LOCK = "_field_local_lock";
    private static final String CONST_FORMAL_NAME = "paramLock";
    private static final String LOCK_LIST = "locklist";
    private static final String ATOMIC_METHOD_LOCK = "atomic_method_lock_";
    
    private static final QName ORDERED_LOCK = QName.make("x10.util.concurrent.OrderedLock");
    private static final QName ATOMICS = QName.make("x10.util.concurrent.Atomic");
    private static final QName X10_LIST = QName.make("x10.util.List"); 
    private static final QName X10_ARRAYLIST = QName.make("x10.util.ArrayList");
    
    private static final Name LIST_ADD = Name.make("add");
    private static final Name GET_ORDERED_LOCK = Name.make("getOrderedLock");
    private static final Name GET_STATIC_ORDERED_LOCK = Name.make("getStaticOrderedLock");
    private static final Name ACQUIRE_LOCKS = Name.make("acquireLocksInOrder");
    private static final Name RELEASE_LOCKS = Name.make("releaseLocks");
    private static final Name PARAM_LOCK_NAME = Name.make(CONST_FORMAL_NAME);
    
    private static final Name PUSH_ATOMIC = Name.make("pushAtomic");
    private static final Name POP_ATOMIC = Name.make("popAtomic");
    
    private static final Name ENTER_ATOMIC = Name.make("enterAtomic");
    private static final Name EXIT_ATOMIC = Name.make("exitAtomic");
    
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
     * The only place in this visitor that manipulates the X10 AST
     * */
    public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
    	//if the class decl has atomic fields then:
    	// 0. inherits an Atomic interface
    	// 1. add a lock field and a get lock method
    	// 2. it also rewrites all the constructor declarations to take a lock arg
    	// 3. allocate a static lock if this class contains static (atomic? ?) field
    	//    or static method, also initialize the static lock
    	if(n instanceof X10ClassDecl_c) {
    		X10ClassDecl_c x10clz = (X10ClassDecl_c)n;
    		return this.visitX10ClassDecl(x10clz);
    	}
    	
    	//rewrite constructor calls, to take a lock object as an additional arg
    	//for class containing atomic fields.
    	if(n instanceof X10New_c) {
    		X10New_c x10newc = (X10New_c)n;
    		return this.visitX10New(x10newc);
    	}
    	
    	//1. if the method is decorated as atomic, then acquire locks for it
    	//2. allocate locks for fields that is not atomic, but may be
    	//    accessed in atomic blocks (this is not needed, right?, only for atomic fields?)
    	//    #NOT implemented at all.
    	//3. acquire the static lock for it, if decorated with atomic
    	//   (add a type checking rule, that no linked is allowed inside)
    	
    	// We do not need to visit the method, since atomic method will be translated into an atomic section
    	// visiting atomic sections is suffcient
    	if(n instanceof X10MethodDecl_c) {
    		X10MethodDecl_c x10method = (X10MethodDecl_c)n;
    		return this.visitX10Method(x10method);
    	}
    	
    	//1. allocate locals for atomic local vars that may be accessed in an atomic section
    	//2. do not allocate locks for vars that have the class type containing
    	//   atomic fields
    	if(n instanceof Block_c) {
    		Block_c block = (Block_c)n;
    		return this.visitBlock_c(block);
    	}
    	
    	//acquire locks for the atomic section. All accessed variables.
    	//Call var.getLock() if it is a class type with atomic fields, or else
    	//get the locally allocated lock, if it is locally defined
    	if(n instanceof Atomic_c) {
    		Atomic_c atomic = (Atomic_c)n;
    		return this.visitAtomic_c(atomic);
    	}
    	
    	return n;
    }
    
    private Node visitX10ClassDecl(X10ClassDecl_c n) {
    	boolean hasAtomicFields = n.classDef().hasAtomicFields(); //including static fields
    	if(!hasAtomicFields) {
    		return n;
    	}
    	
    	//copy the original interface, and add a new one
    	//note that n.interfaces() returns an immutable list
    	List<TypeNode> interfaces = new LinkedList<TypeNode>();
    	interfaces.addAll(n.interfaces());
    	//the atomic interface
		Type atomicInterface = X10TypeUtils.lookUpType(ts, ATOMICS);
		assert atomicInterface != null;
		//add the interface to the class declarations
		Position pos = n.position();
		TypeNode intf = nf.CanonicalTypeNode(pos, atomicInterface);
		interfaces.add(intf);
		n = (X10ClassDecl_c) n.interfaces(interfaces);

		//add a lock field with type OrderedLock for each class
		List<ClassMember> members = n.body().members();
		X10ClassType containerClass = n.classDef().asType();
		
		//create a list to store new class members
		List<ClassMember> newMembers = new LinkedList<ClassMember>();

		//get the lock type
		Type lockType =  X10TypeUtils.lookUpType(ts, ORDERED_LOCK);
		assert lockType != null;
		//create the field OrderedLock:$lock = null; (init value)
		Name lockName = Context.makeFreshName(ATOMICSET_LOCK_NAME);
		FieldDecl lockFieldDecl = X10TypeUtils.createFieldDecl(nf, ts, pos, ts.Private(), lockType,
				lockName, containerClass, nf.NullLit(pos).type(ts.Null()));
		
		//create the method getLock() {return this.$lock; }
		Receiver thiz = nf.This(pos).type(containerClass);
		Expr lockField = nf.Field(pos, thiz, lockFieldDecl.name()).fieldInstance(lockFieldDecl.fieldDef().asInstance()).type(lockType);    
		Stmt lockRetStmt = nf.Return(pos, lockField);
		X10MethodDecl getLockMethodDecl = X10TypeUtils.createX10MethodDecl(nf, ts, pos, ts.Public(), containerClass,
				lockType, GET_ORDERED_LOCK, Collections.<Stmt>singletonList(lockRetStmt));
		//add the lock field and get lock method
		newMembers.add(lockFieldDecl);
		newMembers.add(getLockMethodDecl);
		
		if(X10TypeUtils.hasStaticAtomicField(n.classDef())) {
		  //create the static lock field  private static OrderedLock lock = new OrderedLock();
		  Name staticLockName = Context.makeFreshName(STATIC_LOCK_NAME);
		  Expr newStaticLock = X10TypeUtils.createNewObjectByDefaultConstructor(nf, ts, this.context, pos, lockType);
		  FieldDecl staticLockFieldDecl = X10TypeUtils.createFieldDecl(nf, ts, pos, ts.Private().Static(), lockType,
				staticLockName, containerClass, newStaticLock);
		
		  //create a static method getStaticOrderedLock
		  Receiver thizClass = nf.CanonicalTypeNode(pos, containerClass);
    	  Expr staticField = nf.Field(pos, thizClass, staticLockFieldDecl.name()).fieldInstance(staticLockFieldDecl.fieldDef().asInstance()).type(lockType);    
    	  Stmt staticLockRetStmt = nf.Return(pos, staticField);
    	  X10MethodDecl staticGetLockMethodDecl = X10TypeUtils.createX10MethodDecl(nf, ts, pos, ts.Public().Static(), containerClass,
    			lockType, GET_STATIC_ORDERED_LOCK, Collections.<Stmt>singletonList(staticLockRetStmt));
		  newMembers.add(staticLockFieldDecl);
		  newMembers.add(staticGetLockMethodDecl);
		}
		
		//add existing method
		FieldDef fd = lockFieldDecl.fieldDef();
		List<X10ConstructorDef> ciDefs = new LinkedList<X10ConstructorDef>();
		for(ClassMember member : members) {
			newMembers.add(member);
			Position mpos = member.position();
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
				List<Ref<? extends Type>> newFormalTypes = new LinkedList<Ref<? extends Type>>();
				newFormalTypes.addAll(x10ci.formalTypes());
				newFormalTypes.add(Types.ref(lockType));
				ConstructorDef ci = ts.constructorDef(mpos, Types.ref(x10ci.container().get().toClass()),
						x10ci.flags(), newFormalTypes, x10ci.offerType());
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
				//add new constructors
				ciDefs.add(newConstructor.constructorDef());
			}
		}
		//set the class body
		ClassBody newBody = n.body().members(newMembers);
		n = (X10ClassDecl_c) n.body(newBody);
		
		
		return n;
    }
    
    private Node visitX10Method(X10MethodDecl_c n) throws SemanticException {
    	Position pos = n.position();
    	X10ParsedClassType_c clazzType = Types.fetchX10ClassType(n.methodDef().container().get());
    	// not a class type? is that true in x10?
    	//skip this method if it is automatically generated or not in a class, or is an abstract method
    	if(pos.isCompilerGenerated() || clazzType == null || n.body() == null) {
    		return n;
    	}
    	
    	if(!X10TypeUtils.hasAtomic(n.flags())) {
    		return n;
    	}
    	
    	//do the same thing as the atomic block
    	X10MethodDef_c x10method = (X10MethodDef_c)n.methodDef();
    	List<LocalDef> paramNeedToLock = this.getFormalsThatNeedLocks(x10method.formalNames(), x10method.formalTypes());
    	//get all accessed atomic fields
    	AtomicLocalAndFieldAccessVisitor visitor = new AtomicLocalAndFieldAccessVisitor();
    	n.body().visit(visitor);
    	//only need to know the accessed fields
    	Set<X10Field_c> x10fields = visitor.getAllX10FieldsForEscapedFieldDefs();

		System.out.println("Atomic method: " + n.name());
		System.out.println("locks acquired: ");
		System.out.println("   params: " + paramNeedToLock);
		System.out.println("   field: " + x10fields);
		System.out.println("   is static: " + n.flags().flags().contains(Flags.STATIC));
		System.out.println();
//		if(x10fields.isEmpty() && paramNeedToLock.isEmpty()) {
//			//just return
//			System.out.println("- no lock needed for method: " + n.name());
//			System.out.println();
//			Flags noAtomic = n.flags().flags().clearAtomic();
//	    	FlagsNode fn = nf.FlagsNode(pos, noAtomic);
//	    	
//	    	return n.flags(fn); 
//		}
		
    	Type lockType = X10TypeUtils.lookUpType(ts, ORDERED_LOCK);
		LocalDecl emptyLockList = this.createEmptyLockListDeclaration(pos);
		
		//grab locks from formals
		List<Stmt> addMethodFormalsToLockStmts = this.addLocksForFormals(pos, lockType, paramNeedToLock,
				emptyLockList.name(), emptyLockList);
		//grab locks from fields (both static and non-static fields)
		List<Stmt> addFieldsToLockStmts = this.addAtomicSetLockbyFieldToList(pos, lockType, x10fields,
					emptyLockList.name(), emptyLockList, false);
		
		//it is either this.getOrderedLock or Class.getOrderedLock
//		List<Stmt> atomicSetLock = new LinkedList<Stmt>();
		
		//do not forget
		Stmt lockAcquireStmt = this.lockAcquireStatement(pos, lockType, emptyLockList, emptyLockList.name());
		Stmt lockReleaseStmt = this.lockReleaseStatement(pos, lockType, emptyLockList, emptyLockList.name());
    	
		//push and pop atomic
		Stmt pushAtomic = nf.Eval(pos, call(pos, PUSH_ATOMIC, ts.Void()));
		Stmt popAtomic = nf.Eval(pos, call(pos, POP_ATOMIC, ts.Void()));
		
		//assembly the code block
    	List<Stmt> tryStatements = new LinkedList<Stmt>();
    	tryStatements.add(lockAcquireStmt);
    	tryStatements.add(pushAtomic);
    	tryStatements.add(n.body());
    	
    	Block tryBlock = nf.Block(pos, tryStatements);
    	Block finallyBlock = nf.Block(pos, popAtomic, lockReleaseStmt);
    	
    	Try tryStmt = nf.Try(pos, tryBlock, Collections.<Catch>emptyList(), finallyBlock);
    	
    	List<Stmt> statements = new LinkedList<Stmt>();
    	statements.add(emptyLockList);
    	statements.addAll(addMethodFormalsToLockStmts);
    	statements.addAll(addFieldsToLockStmts);
    	statements.add(tryStmt);
    	
    	Block newBlock = nf.Block(pos, statements);
    	
    	//get rid of the atomic keyword in method declaration
    	X10MethodDecl_c methodDecl = (X10MethodDecl_c) n.body(newBlock);
    	Flags noAtomic = methodDecl.flags().flags().clearAtomic();
    	FlagsNode fn = nf.FlagsNode(pos, noAtomic);
    	
//    	System.out.println("  after process, the flags is: " + fn);
    	
    	return methodDecl.flags(fn);
    }
    
    private Node visitX10New(X10New_c n) {
    	X10ParsedClassType_c clazzType = Types.fetchX10ClassType(n.constructorInstance().returnType());
    	Position pos = n.position();
    	//skip calling the constructors of a class without atomic fields
    	if(!clazzType.def().hasAtomicFields()) {
    		return n;
    	}
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
    	//  1. new (linked C)(args)   ==> new (liked C)(args,  container.getOrderedLock());
    	//  2. new C(args)  ==>  new C(args, new OrderedLock()); 
    	FlagsNode flagNode = n.objectType().getFlagsNode();
    	if(flagNode != null && flagNode.flags().contains(/*Flags.ATOMICPLUS*/ Flags.TYPE_FLAG)) {
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
	    	Expr newLock = X10TypeUtils.createNewObjectByDefaultConstructor(nf, ts, this.context, pos, lockType) ;
	    	newArguments.add(newLock);
	    	n = (X10New_c) n.arguments(newArguments);
    	}
    	
    	return n;
    }
    
    private Node visitBlock_c(Block_c block) {
    	List<Stmt> statements = block.statements();
    	//create a list of statements, adding a lock for each atomic field
    	Type lockType = X10TypeUtils.lookUpType(ts, ORDERED_LOCK);
    	List<Stmt> newStatements = new LinkedList<Stmt>();
    	for(Stmt stmt : statements) {
    		newStatements.add(stmt);
    		//create a lock field, and add the local declaration statement
    		//just after it #FIXME is this complete?
    		if(stmt instanceof X10LocalDecl_c) {
    			X10LocalDecl_c localDecl = (X10LocalDecl_c)stmt;
    			if(X10TypeUtils.isAtomicLocalDecl(localDecl)) {
    				Position pos = stmt.position();
    				//create new OrderedLock();
    		    	Expr newLock = 	X10TypeUtils.createNewObjectByDefaultConstructor(nf, ts, this.context, pos, lockType);
    		    	//create local field assignment
    		    	Name lockName = Context.makeFreshName( getLocalVarLockName(localDecl.name().toString()));
    		    	
                    final LocalDecl ld = X10TypeUtils.createLocalDecl(nf, ts, pos, ts.Final(), lockType, lockName, newLock);
                    
                    //add to the statement
                    newStatements.add(ld);
    			}
    		}
    	}
    	//if there is no change, just return the original node
    	if(statements.size() == newStatements.size()) {
    		return block;
    	}
    	
    	//some changes
    	block = (Block_c) block.statements(newStatements);
    	
    	return block;
    }
    
    //--------------------all untility methods below------------------
    
    private X10MethodDecl_c acquireLocksForAtomicMethod(X10MethodDecl_c n, int locknum, X10ParsedClassType_c clazzType) {
    	//for non-static field, use this.getOrderedLock(), otherwise, use ClassName.getOrderedLock();
    	boolean isStatic = X10TypeUtils.hasFlag(n.flags(), Flags.STATIC);
    	
    	Type lockType = X10TypeUtils.lookUpType(ts, ORDERED_LOCK);
    	List<Name> freshlocknames = new LinkedList<Name>();
    	for(int i = 0; i < locknum; i++) {
    	    freshlocknames.add(Context.makeFreshName(ATOMIC_METHOD_LOCK + i));
    	}
    	//all lock declaration statements
    	List<LocalDecl> lockDeclStmts = new LinkedList<LocalDecl>();
    	
    	//in this order, the first lock is for this, then each argument
    	int currentlockindex = 0;
    	if(clazzType.def().hasAtomicFields()) {
    		Position pos = n.position();
    		//create a statement  var lock:OrderedLock = this.getOrderedLock();
    		Name lockName = freshlocknames.get(currentlockindex);
            final LocalDef li = ts.localDef(pos, ts.Final(), Types.ref(lockType), lockName);
            final Id varId = nf.Id(pos, lockName);
            //depends on whether this method is static or not
            X10Call getLockCall = null;
            Name methodName = null;
            X10MethodDef methodDef = null;
            if(!isStatic) {
              getLockCall = nf.X10Call(pos, nf.This(pos).type(n.memberDef().container().get()),
    				nf.Id(pos, GET_ORDERED_LOCK), Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList());
			  methodDef = ts.methodDef(n.position(), Types.ref(clazzType),
		           ts.Public(), Types.ref(lockType), GET_ORDERED_LOCK,
		            Collections.<Ref<? extends Type>>emptyList(),  null);
            } else {
              getLockCall = nf.X10Call(pos, nf.CanonicalTypeNode(pos, clazzType),
            		  nf.Id(pos, GET_STATIC_ORDERED_LOCK), Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList());
              methodDef = ts.methodDef(n.position(), Types.ref(clazzType),
   		           ts.Public(), Types.ref(lockType), GET_STATIC_ORDERED_LOCK,
		            Collections.<Ref<? extends Type>>emptyList(),  null);
            }
			
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
    		if(f.type().getFlagsNode() != null && f.type().getFlagsNode().flags().contains(Flags.TYPE_FLAG/*Flags.ATOMICPLUS*/)) {
    			needLock = true;
    		}
    		if(needLock) {
    			Position pos = f.position();
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
    	Position pos = n.position();
    	//create a local lock for each allocated lock
    	List<Local> allLocks = new LinkedList<Local>();
    	for(LocalDecl ld : lockDeclStmts) {
    		final Local local = (Local) nf.Local(pos, ld.name()).localInstance(ld.localDef().asInstance()).type(lockType);
    		allLocks.add(local);
    	}
    	//create statement  var lock_list:List<OrderedLock> = new ArrayList<OrderedLock>();
    	List<Stmt> lockListCreation = new LinkedList<Stmt>();

    	X10ParsedClassType_c listType = (X10ParsedClassType_c)X10TypeUtils.lookUpType(ts, X10_LIST);
    	listType = (X10ParsedClassType_c) listType.typeArguments(Collections.<Type>singletonList(lockType));
    	X10ParsedClassType_c arrayListType = (X10ParsedClassType_c)X10TypeUtils.lookUpType(ts, X10_ARRAYLIST);
    	arrayListType = (X10ParsedClassType_c)arrayListType.typeArguments(Collections.<Type>singletonList(lockType));
    	TypeNode arrayListTypeNode = nf.CanonicalTypeNode(pos, arrayListType);
        X10ConstructorInstance arrayListCI = X10TypeUtils.findDefaultConstructor(ts, this.context, arrayListType);
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
        	MethodInstance mi = X10TypeUtils.findMethod(ts, this.context, listType, LIST_ADD,
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
    	
    	List<Stmt> methodBodyStmts = new LinkedList<Stmt>();
    	methodBodyStmts.addAll(methodBody.statements());
    	
    	//DO NOT NEED, already been done when visiting the atomic block
    	//get rid of the atomic blocks here
//    	if(X10TypeUtils.hasAtomic(n.flags())) {
//    		assert methodBody.statements().size() == 1;
//    		Stmt stmt = methodBody.statements().get(0);
//    		assert stmt instanceof Atomic_c;
//    		Atomic_c atomic_c = (Atomic_c)stmt;
//    		methodBodyStmts.add(atomic_c.body());
//    	} else {
//    		methodBodyStmts.addAll(methodBody.statements());	
//    	}
    	
    	List<Stmt> tryStatements = new LinkedList<Stmt>();
    	tryStatements.addAll(lockListAddition);
    	tryStatements.add(acquireLockStatement);
    	tryStatements.addAll(methodBodyStmts);
    	
    	Block tryBlock = nf.Block(pos, tryStatements);
    	Block finallyBlock = nf.Block(pos, releaseLockStatement);
    	
    	Try tryNode = nf.Try(pos, tryBlock, Collections.<Catch>emptyList(), finallyBlock);
    	
    	List<Stmt> statements = new LinkedList<Stmt>();
    	statements.addAll(lockDeclStmts);
    	statements.addAll(lockListCreation);
    	statements.add(tryNode);
    	
    	Block newBlock = nf.Block(pos, statements);
    	n = (X10MethodDecl_c) n.body(newBlock);
    	
    	return n;
    }
    
    //FIXME this implementation may not be compatible with existing atomics
    // the atomic keyword will be removed, if there is no atomic fields accessed inside
    private Node visitAtomic_c(Atomic_c atomic) throws SemanticException {
    	
    	System.out.println("@X10MixedAtomicityTranslator, IDs: " + atomic.identifiers);
    	
    	X10CodeDef codeDef = this.context.currentCode();
//    	if(codeDef.position().isCompilerGenerated()) {
//    		System.out.println("the atomic block is generated by compiler! ignore now!");
//    		atomic.prettyPrint(System.out);
//    		return atomic;
//    	}
    	boolean isInConstructor = false;
    	boolean isInStaticMethod = false;
    	List<LocalDef> paramNeedToLock = new LinkedList<LocalDef>();
    	if(codeDef instanceof X10ConstructorDef) {
    		isInConstructor = true;
    		X10ConstructorDef constDef = (X10ConstructorDef)codeDef;
    		paramNeedToLock.addAll(this.getFormalsThatNeedLocks(constDef.formalNames(), constDef.formalTypes()));
    	} else if (codeDef instanceof X10MethodDef_c) {
    		Flags flags = ((X10MethodDef_c) codeDef).flags();
    		if(flags.contains(Flags.STATIC)) {
    			isInStaticMethod = true;
    		}
    		X10MethodDef_c x10method = ((X10MethodDef_c) codeDef);
    		paramNeedToLock.addAll(this.getFormalsThatNeedLocks(x10method.formalNames(), x10method.formalTypes()));
    	} else {
    		throw new RuntimeException("An atomic section inside: " + codeDef.getClass()
    				+ " has not been implemented yet!");
    	}
    	
    	//get all accessed fields inside atomic
    	AtomicLocalAndFieldAccessVisitor visitor = new AtomicLocalAndFieldAccessVisitor();
    	atomic.visit(visitor);
    
    	//here is the algorithm to acquire the lock:
    	//1. all atomic local variables -> acquire their allocated locks
    	//2. all non-static atomic fields  -> acqurie their locks through: field.getOrderedLock();
    	//3. static atomic fields -> acquire theirlocks through Class.getStaticOrderedLock()
    	//4. acquire locks for paraemter which is decorated with unitfor and linked
    	//5. for constructors, do not acquire this.getLock
    	
    	//(actually, no need for the following, if a field is accessed through this, just acquire this.getOrderedLock())
    	//4. if in constructor, add no more locks
    	//   if in static method, add ThisClass.getStaticOrderedLock()
    	
    	//acquire locks for the atomic section and then remove it
    	//XXX FIXME potential improvement space when accessing different fields
    	//Fix the constructor problem, no atomicset lock is needed.
    	Set<LocalDef> localDefs = visitor.escapedLocalDefs();
    	Set<X10Field_c> x10fields = visitor.getAllX10FieldsForEscapedFieldDefs();

		atomic.prettyPrint(System.out);
		System.out.println("locks acquired: ");
		System.out.println("   localDefs: " + localDefs);
		System.out.println("   params: " + paramNeedToLock);
		System.out.println("   field: " + x10fields);
		
		if(localDefs.isEmpty() && paramNeedToLock.isEmpty() && x10fields.isEmpty()) {
			System.out.println(" - not lock is needed for this atomic section");
			System.out.println();
			return atomic.body;
		}
    	
    	Position pos = atomic.position();
    	Type lockType = X10TypeUtils.lookUpType(ts, ORDERED_LOCK);
		LocalDecl emptyLockList = this.createEmptyLockListDeclaration(pos);
		//grab local var locks
		List<Stmt> addLocalsToLockStmts = this.addLocalDefToList(pos, lockType, localDefs,
				emptyLockList.name(), emptyLockList);
		//grab locks from formals
		List<Stmt> addMethodFormalsToLockStmts = this.addLocksForFormals(pos, lockType, paramNeedToLock,
				emptyLockList.name(), emptyLockList);
		//grab locks from fields (both static and non-static fields)
		List<Stmt> addFieldsToLockStmts = 
//			this.addFieldDefToList(pos, lockType, fieldDefs, emptyLockList.name(), emptyLockList);
			//including both static or non-static fields
			this.addAtomicSetLockbyFieldToList(pos, lockType, x10fields,
					emptyLockList.name(), emptyLockList, isInConstructor);
		
		//do not forget
		Stmt lockAcquireStmt = this.lockAcquireStatement(pos, lockType, emptyLockList, emptyLockList.name());
		Stmt lockReleaseStmt = this.lockReleaseStatement(pos, lockType, emptyLockList, emptyLockList.name());
    	
		//push and pop atomic
		Stmt pushAtomic = nf.Eval(pos, call(pos, PUSH_ATOMIC, ts.Void()));
		Stmt popAtomic = nf.Eval(pos, call(pos, POP_ATOMIC, ts.Void()));
		
		//assembly the code block
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
    	statements.addAll(addFieldsToLockStmts);
    	statements.add(tryStmt);
    	
    	Block newBlock = nf.Block(pos, statements);
    	
    	return newBlock;
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
    
    private int getNeededLockNum(X10MethodDecl_c n, X10ClassDef clazzDef) {
    	int locknum = 0;
    	if(clazzDef.hasAtomicFields()) {
    		locknum++;
    	}
    	for(Formal f : n.formals()) {
    		if(f.flags() != null && f.flags().flags().contains(Flags.UNITFOR)) {
    			locknum++;
    			continue;
    		}
    		if(f.type().getFlagsNode() != null && f.type().getFlagsNode().flags().contains(Flags.TYPE_FLAG/*Flags.ATOMICPLUS*/)) {
    			locknum++;
    			continue;
    		}
    	}
    	return locknum;
    }
    
    private String getLocalVarLockName(String localName) {
    	return localName + LOCAL_VAR_LOCK;
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
    
  //call emptyList.add(localdef)
    private List<Stmt> addLocalDefToList(Position pos, Type lockType, Set<LocalDef> localDefs,
    		Id listID, LocalDecl emptyList) {
    	List<Stmt> stmts = new LinkedList<Stmt>();
    	X10ParsedClassType_c listType = (X10ParsedClassType_c)X10TypeUtils.lookUpType(ts, X10_LIST);
    	listType = (X10ParsedClassType_c) listType.typeArguments(Collections.<Type>singletonList(lockType));
    	MethodInstance addmi = X10TypeUtils.findMethod(ts, this.context, listType, LIST_ADD,
				Collections.<Type>singletonList(nf.X10CanonicalTypeNode(pos, lockType).type()));
    	for(LocalDef localDef : localDefs) {
    		Name localLockName = Context.makeFreshName( getLocalVarLockName(localDef.name().toString()));
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
    
    private List<Stmt> addLocksForFormals(Position pos, Type lockType, List<LocalDef> formalDefs, Id listID,
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
    
    private List<Stmt> addAtomicSetLockbyFieldToList(Position pos, Type lockType, Set<X10Field_c> fields,
    		Id listID, LocalDecl emptyList, boolean isInConstructor) {
    	List<Stmt> stmts = new LinkedList<Stmt>();
    	X10ParsedClassType_c listType = (X10ParsedClassType_c)X10TypeUtils.lookUpType(ts, X10_LIST);
    	listType = (X10ParsedClassType_c) listType.typeArguments(Collections.<Type>singletonList(lockType));
    	MethodInstance addmi = X10TypeUtils.findMethod(ts, this.context, listType, LIST_ADD,
				Collections.<Type>singletonList(nf.X10CanonicalTypeNode(pos, lockType).type()));
    	for(X10Field_c field : fields) {
    		FieldDef fieldDef = field.fieldInstance().def();
    		//skip all non-static fields in constructor
    		if(!fieldDef.flags().contains(Flags.STATIC) && isInConstructor) {
    			continue;
    		}
    		
    		pos = field.position();
    		ContainerType clazzType = fieldDef.container().get();
    		X10Call getLockCall = null;
    		X10MethodDef methodDef = null;
    		if(fieldDef.flags().contains(Flags.STATIC)) {
    			//static field
    			//make a call ContainerType.getStaticOrderedLock();
    			getLockCall = nf.X10Call(pos, nf.CanonicalTypeNode(pos, clazzType),
              		  nf.Id(pos, GET_STATIC_ORDERED_LOCK), Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList());
                methodDef = ts.methodDef(pos, Types.ref(clazzType),
     		           ts.Public(), Types.ref(lockType), GET_STATIC_ORDERED_LOCK,
  		            Collections.<Ref<? extends Type>>emptyList(),  null);
    		} else {
    			//non-static fields
    			getLockCall = nf.X10Call(pos, field.target(),
        				nf.Id(pos, GET_ORDERED_LOCK), Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList());
    			methodDef = ts.methodDef(pos, Types.ref(clazzType),
    		           ts.Public(), Types.ref(lockType), GET_ORDERED_LOCK,
    		            Collections.<Ref<? extends Type>>emptyList(),  null);
    			
    		}
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
    
  //the lock acquire statement
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
    
    //the lock release statement
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
    
    private List<LocalDef> getFormalsThatNeedLocks(List<LocalDef> localdefs, List<Ref<? extends Type>> localtypes) {
    	List<LocalDef> retDefs = new LinkedList<LocalDef>();
    	
    	assert localdefs.size() == localtypes.size();
    	
    	for(int i = 0; i < localdefs.size(); i++) {
    		LocalDef def = localdefs.get(i);
    		Type type = localtypes.get(i).get();
    		if(def.flags() != null && def.flags().contains(Flags.UNITFOR)) {
    			retDefs.add(def);
    		} else {
    			X10ParsedClassType_c clazztype = Types.fetchX10ClassType(type);
    			if(clazztype != null && clazztype.getAtomicContext() != null) {
    				retDefs.add(def);
    			}
    		}
    	}
    	
    	return retDefs;
    }
    
    private Expr call(Position pos, Name name, Type returnType) throws SemanticException {
    	return synth.makeStaticCall(pos, ts.Runtime(), name, returnType, context());
    }
}
