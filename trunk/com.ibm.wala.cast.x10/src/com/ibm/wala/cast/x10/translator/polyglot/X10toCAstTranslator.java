/*
 * Created on Sep 8, 2005
 */
package com.ibm.domo.ast.x10.translator.polyglot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import polyglot.ast.ArrayInit;
import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.ext.x10.ast.*;
import polyglot.ext.x10.types.ClosureType;
import polyglot.ext.x10.types.FutureType;
import polyglot.types.LocalInstance;
import polyglot.types.MethodInstance;
import polyglot.types.ReferenceType;
import polyglot.types.Type;

import com.ibm.domo.ast.x10.translator.X10CAstEntity;
import com.ibm.domo.ast.x10.translator.X10CastNode;
import com.ibm.wala.cast.ir.translator.AstTranslator;
import com.ibm.wala.cast.java.translator.polyglot.PolyglotJava2CAstTranslator;
import com.ibm.wala.cast.java.translator.polyglot.PolyglotTypeDictionary;
import com.ibm.wala.cast.java.translator.polyglot.TranslatingVisitor;
import com.ibm.wala.cast.tree.CAstControlFlowMap;
import com.ibm.wala.cast.tree.CAstEntity;
import com.ibm.wala.cast.tree.CAstNode;
import com.ibm.wala.cast.tree.CAstNodeTypeMap;
import com.ibm.wala.cast.tree.CAstSourcePositionMap;
import com.ibm.wala.cast.tree.CAstSymbol;
import com.ibm.wala.cast.tree.CAstType;
import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;
import com.ibm.wala.cast.tree.impl.CAstSymbolImpl;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.shrikeBT.IInvokeInstruction;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.Descriptor;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.Selector;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.Atom;
import com.ibm.wala.util.IteratorPlusOne;
import com.ibm.wala.util.debug.Assertions;

public class X10toCAstTranslator extends PolyglotJava2CAstTranslator {
    public X10toCAstTranslator(ClassLoaderReference clr, NodeFactory nf, X10ExtensionInfo extInfo) {
	super(clr, nf, extInfo.typeSystem(), extInfo.getIdentityMapper());
    }

    protected TranslatingVisitor createTranslator() {
        return new X10TranslatingVisitorImpl();
    }

    protected PolyglotTypeDictionary createTypeDict() {
	return new X10TypeDictionary(fTypeSystem, this);
    }

    protected CAstEntity walkAsyncEntity(final Node rootNode, final Node bodyNode, final WalkContext context) {
	Map<CAstNode,CAstEntity> childEntities= new HashMap<CAstNode,CAstEntity>();
	final CodeBodyContext asyncContext= new CodeBodyContext(context, childEntities);
	final CAstNode bodyAST= walkNodes(bodyNode, asyncContext);

	return new AsyncEntity(childEntities, rootNode, asyncContext, bodyAST);
    }

    protected CAstEntity walkClosureEntity(final Closure rootNode, final Node bodyNode, final WalkContext context) {
	Map<CAstNode,CAstEntity> childEntities= new HashMap<CAstNode,CAstEntity>();
	final CodeBodyContext closureContext= new CodeBodyContext(context, childEntities);
	final CAstNode bodyAST= walkNodes(bodyNode, closureContext);

	return new ClosureBodyEntity(childEntities, rootNode, closureContext, bodyAST, context.getEnclosingType());
    }

    private final class AsyncBodyType implements CAstType.Method {
	private final Node fNode;

	private final Type declaringType;

	private AsyncBodyType(Node node, Type declaringType) {
	    super();
	    fNode= node;
	    this.declaringType = declaringType;
	}

	public CAstType getReturnType() {
	    return getTypeDict().getCAstTypeFor(
		    (fNode instanceof Future) ?
			    ((Future) fNode).type() : fTypeSystem.Void());
	}

	public List getArgumentTypes() {
	    return Collections.EMPTY_LIST;
	}

	public Collection getExceptionTypes() {
	    // TODO should figure out what exceptions can really be thrown by the body
	    return Collections.EMPTY_LIST;
	}

	public int getArgumentCount() {
	    return 0;
	}

	public String getName() {
	    return "<activity>";
	}

	public Collection getSupertypes() {
	    return Collections.singleton(getTypeDict().getCAstTypeFor(fTypeSystem.Object()));
	}

	public CAstType getDeclaringType() {
	    return getTypeDict().getCAstTypeFor(declaringType);
	}
    }

    private final class AsyncEntity extends CodeBodyEntity {
	private final CodeBodyContext fContext;

	private final CAstNode fBodyast;

	private final CAstSourcePositionMap.Position fPosition;

	private final AsyncBodyType fBodyType;

	private AsyncEntity(Map<CAstNode,CAstEntity> entities, Node node, CodeBodyContext context, CAstNode bodyast) {
	    super(entities);
	    fPosition= makePosition(node.position());
	    fContext= context;
	    fBodyast= bodyast;
	    fBodyType= new AsyncBodyType(node, fContext.getEnclosingType());
	}

	public CAstSourcePositionMap.Position getPosition() {
	  return fPosition;
	}

	public int getKind() {
	    return X10CAstEntity.ASYNC_BODY;
	}

	public String getName() {
	    return "<activity " + fPosition.getURL() + ":" + fPosition.getFirstLine() + ":" + fPosition.getFirstCol() + ">";
	}

	public String[] getArgumentNames() {
	    return new String[] { "<place>" };
	}

	public CAstNode[] getArgumentDefaults() {
	  return new CAstNode[0];
	}

	public int getArgumentCount() {
	    return 1;
	}

	public CAstNode getAST() {
	    return fBodyast;
	}

	public CAstControlFlowMap getControlFlow() {
	    return fContext.cfg();
	}

	public CAstSourcePositionMap getSourceMap() {
	    return fContext.pos();
	}

	public CAstNodeTypeMap getNodeTypeMap() {
	    return fContext.getNodeTypeMap();
	}

	public Collection getQualifiers() {
	    return Collections.EMPTY_LIST;
	}

	public CAstType getType() {
	    return fBodyType;
	}
	public String toString() {
	    return getName();
	}
    }

    class X10TranslatingVisitorImpl extends JavaTranslatingVisitorImpl implements X10TranslatorVisitor {
	public CAstNode visit(Async a, WalkContext context) {
	    CAstEntity bodyEntity= walkAsyncEntity(a, a.body(), context);

	    CAstNode asyncNode= fFactory.makeNode(X10CastNode.ASYNC_INVOKE,
		    walkNodes(a.place(), context),
		    // FUNCTION_EXPR will translate to a type wrapping the single method with the given body
		    fFactory.makeNode(CAstNode.FUNCTION_EXPR, fFactory.makeConstant(bodyEntity)));

	    context.addScopedEntity(asyncNode, bodyEntity);
	    return asyncNode;
	}

	public CAstNode visit(Finish f, WalkContext context) {
	    return fFactory.makeNode(CAstNode.UNWIND, 
		    fFactory.makeNode(CAstNode.BLOCK_STMT, 
			    fFactory.makeNode(X10CastNode.FINISH_ENTER),
			    walkNodes(f.body(), context)),
		    fFactory.makeNode(X10CastNode.FINISH_EXIT));
	}

	
	public CAstNode visit(ForEach f, WalkContext context) {
	    CAstEntity bodyEntity= walkAsyncEntity(f, f.body(), context);

	    final CAstNode bodyNode= fFactory.makeNode(X10CastNode.ASYNC_INVOKE,
					    fFactory.makeNode(X10CastNode.HERE),
					    // FUNCTION_EXPR will translate to a type wrapping the single method with the given body
					    fFactory.makeNode(CAstNode.FUNCTION_EXPR, fFactory.makeConstant(bodyEntity)));

	    context.addScopedEntity(bodyNode, bodyEntity);
	    return walkRegionIterator(f, bodyNode, context);
	}

	private CAstNode walkRegionIterator(X10Loop loop, final CAstNode bodyNode, WalkContext context) {
	    return walkRegionIterator(loop.formal(), bodyNode, walkNodes(loop.domain(), context), context);
	}

	private CAstNode walkRegionIterator(Formal formal, final CAstNode bodyNode, CAstNode domainNode, WalkContext context) {
	    X10Formal x10Formal= (X10Formal) formal;
	    LocalInstance[] vars = x10Formal.localInstances();
	    CAstNode[] varDecls = new CAstNode[vars.length + 1];
	    for (int i = 0; i < vars.length; i++)
		varDecls[i] = fFactory.makeNode(CAstNode.DECL_STMT,
		  fFactory.makeConstant(new CAstSymbolImpl(vars[i].name(), vars[i].flags().isFinal())),
		  fFactory.makeNode(CAstNode.ARRAY_REF, fFactory.makeNode(CAstNode.VAR, fFactory.makeConstant(formal.name())),
		    fFactory.makeConstant(TypeReference.Int),
		    fFactory.makeConstant(i)));
	    varDecls[vars.length] = bodyNode;

	    return fFactory.makeNode(CAstNode.LOCAL_SCOPE,
		fFactory.makeNode(CAstNode.BLOCK_STMT,
		    fFactory.makeNode(CAstNode.DECL_STMT, fFactory.makeConstant(new CAstSymbolImpl("iter tmp", false)),
		      fFactory.makeNode(X10CastNode.REGION_ITER_INIT, domainNode)),
		    fFactory.makeNode(CAstNode.LOOP,
			fFactory.makeNode(X10CastNode.REGION_ITER_HASNEXT,
			    fFactory.makeNode(CAstNode.VAR, fFactory.makeConstant("iter tmp"))),
			fFactory.makeNode(CAstNode.BLOCK_STMT,
			    fFactory.makeNode(CAstNode.DECL_STMT, walkNodes(formal, context),
				fFactory.makeNode(X10CastNode.REGION_ITER_NEXT, fFactory.makeNode(CAstNode.VAR, fFactory.makeConstant("iter tmp")))),
			    varDecls))));
	}

	public CAstNode visit(AtEach a, WalkContext context) {
	    CAstEntity bodyEntity= walkAsyncEntity(a, a.body(), context);

	    Expr domain= a.domain();
	    Type type= domain.type();
	    CAstNode dist;

	    if (type.isArray())
		dist= fFactory.makeNode(X10CastNode.ARRAY_DISTRIBUTION, walkNodes(domain, context));
	    else
		dist= walkNodes(domain, context);

	    final CAstNode bodyNode=
		fFactory.makeNode(X10CastNode.ASYNC_INVOKE,
			fFactory.makeNode(X10CastNode.PLACE_OF_POINT, fFactory.makeNode(CAstNode.VAR, fFactory.makeConstant("dist temp")), walkNodes(a.formal(), context)),
			    // FUNCTION_EXPR will translate to a type wrapping the single method with the given body
			fFactory.makeNode(CAstNode.FUNCTION_EXPR, fFactory.makeConstant(bodyEntity)));

	    context.addScopedEntity(bodyNode, bodyEntity);
	    return fFactory.makeNode(CAstNode.LOCAL_SCOPE,
		fFactory.makeNode(CAstNode.BLOCK_STMT,
			fFactory.makeNode(CAstNode.DECL_STMT, 
			  fFactory.makeConstant(new CAstSymbolImpl("dist temp", true)),
			  dist),
			walkRegionIterator(a.formal(), bodyNode, fFactory.makeNode(CAstNode.VAR, fFactory.makeConstant("dist temp")), context)));
	}

	public CAstNode visit(Future f, WalkContext context) {
	    CAstEntity bodyEntity= walkAsyncEntity(f, f.body(), context);
	    CAstNode bodyNode= fFactory.makeNode(X10CastNode.ASYNC_INVOKE,
		    walkNodes(f.place(), context),
		    // FUNCTION_EXPR will translate to a type wrapping the single method with the given body
		    fFactory.makeNode(CAstNode.FUNCTION_EXPR, fFactory.makeConstant(bodyEntity)));

	    context.addScopedEntity(bodyNode, bodyEntity);
	    return bodyNode;
	}

	public CAstNode visit(Call c, WalkContext wc) {
	    MethodInstance methodInstance= c.methodInstance();
	    ReferenceType methodOwner= methodInstance.container();

	    if (methodOwner instanceof FutureType) {
		FutureType type= (FutureType) methodOwner;
		TypeReference typeRef= TypeReference.findOrCreate(fClassLoaderRef, fIdentityMapper.typeToTypeID(type.base()));

		return fFactory.makeNode(X10CastNode.FORCE, walkNodes(c.target(), wc), fFactory.makeConstant(typeRef));
	    } else
		return super.visit(c, wc);
	}

	public CAstNode visit(Region r, WalkContext context) {
	    // NOOP for now; Region nodes don't actually get generated by the front-end; what's
	    // generated by the parser is a call to a region factory factory method.
	    return null;
	}

	public CAstNode visit(Range r, WalkContext context) {
	    // TODO Auto-generated method stub
	    return null;
	}

	public CAstNode visit(Point p, WalkContext context) {
	    // TODO Auto-generated method stub
	    return null;
	}

	public CAstNode visit(Here h, WalkContext context) {
	    return fFactory.makeNode(X10CastNode.HERE);
	}

	public CAstNode visit(Next n, WalkContext context) {
	    return fFactory.makeNode(X10CastNode.NEXT);
	}

	public CAstNode visit(PlaceCast pc, WalkContext context) {
	    // TODO Auto-generated method stub
	    return null;
	}

	public CAstNode visit(When w, WalkContext context) {
	    When_c when= (When_c) w;
//          List/*<When.Branch>*/ branches= when.branches();
	    List/*<Expr>*/ exprs= when.exprs();
	    List/*<Stmt>*/ stmts= when.stmts();
	    // In the fullness of time, some analyses may want to have "when" constructs
	    // clearly marked in a more declarative fashion, but for now, this has the
	    // advantage of making the operational semantics clear, with minimal extra
	    // machinery.
            Assertions._assert(exprs.size() == stmts.size());
	    CAstNode[] whenClauses= new CAstNode[exprs.size()+1];

	    
	    CAstNode whenExit= fFactory.makeNode(CAstNode.LABEL_STMT,
		    fFactory.makeConstant("when exit"),
		    fFactory.makeNode(CAstNode.EMPTY));

	    context.cfg().map(whenExit, whenExit);

	    int idx= 0;
            Iterator stmtIter= new IteratorPlusOne(stmts.iterator(), when.stmt());
	    for(Iterator exprIter= new IteratorPlusOne(exprs.iterator(), when.expr()); exprIter.hasNext(); idx++) {
//		Branch b= (Branch) iter.next();
                Expr expr= (Expr) exprIter.next();
                Stmt stmt= (Stmt) stmtIter.next();

		CAstNode whenBreak= fFactory.makeNode(CAstNode.GOTO);

		whenClauses[idx]= fFactory.makeNode(CAstNode.IF_STMT,
			walkNodes(expr, context),
			fFactory.makeNode(CAstNode.BLOCK_STMT,
				walkNodes(stmt, context),
				whenBreak));
		context.cfg().map(whenBreak, whenBreak);
		context.cfg().add(whenBreak, whenExit, null);
	    }
	    return fFactory.makeNode(CAstNode.BLOCK_STMT,
		    fFactory.makeNode(CAstNode.LOOP,
			    fFactory.makeConstant(true),
			    wrapBodyInAtomic(fFactory.makeNode(CAstNode.BLOCK_STMT, whenClauses), w, context)),
	            whenExit);

	    // Alternative, quasi-declarative representation:
//	    CAstNode[] branchNodes= new CAstNode[branches.size()*2];
//
//	    int idx= 0;
//	    for(Iterator iter= branches.iterator(); iter.hasNext(); idx += 2) {
//		Branch b= (Branch) iter.next();
//
//		branchNodes[idx]= walkNodes(b.expr(), context);
//		branchNodes[idx+1]= walkNodes(b.stmt(), context);
//	    }
//	    return fFactory.makeNode(X10CastNode.WHEN, branchNodes);
	}

	public CAstNode visit(X10Formal f, WalkContext context) {
	    return fFactory.makeConstant(new CAstSymbolImpl(f.name(), true));
	}

	public CAstNode visit(Clocked c, WalkContext context) {
	    // TODO Auto-generated method stub
	    return null;
	}

	public CAstNode visit(Await a, WalkContext context) {
	    // TODO Auto-generated method stub
	    return null;
	}

	public CAstNode visit(Atomic a, WalkContext context) {
	    final CAstNode bodyNode= walkNodes(a.body(), context);
	    return wrapBodyInAtomic(bodyNode, a, context);
	}

	private CAstNode wrapBodyInAtomic(final CAstNode bodyNode, Node n, WalkContext wc) {
	    return makeNode(wc, n, CAstNode.UNWIND,
		    makeNode(wc, n, CAstNode.BLOCK_STMT, 
			    makeNode(wc, X10CastNode.ATOMIC_ENTER, n.position().startOf()),
			    bodyNode),
		    makeNode(wc, X10CastNode.ATOMIC_EXIT, n.position().startOf()));
	}

	public CAstNode visit(X10ArrayAccess aa, WalkContext wc) {
	    TypeReference eltTypeRef = fIdentityMapper.getTypeRef(aa.type());
	    CAstNode[] children= new CAstNode[aa.index().size()+2];

	    int idx= 0;
	    children[idx++]= walkNodes(aa.array(), wc);
	    children[idx++]= fFactory.makeConstant(eltTypeRef);
	    for(Iterator iter= aa.index().iterator(); iter.hasNext(); ) {
		Expr index= (Expr) iter.next();
		children[idx++]= walkNodes(index, wc);
	    }
	    return makeNode(wc, fFactory, aa, CAstNode.ARRAY_REF, children);
	}

	public CAstNode visit(X10ArrayAccess1 aa, WalkContext wc) {
	    Expr index= aa.index();
	    TypeReference eltTypeRef = fIdentityMapper.getTypeRef(aa.type());
	    CAstNode[] children= new CAstNode[3];

	    int idx= 0;
	    children[idx++]= walkNodes(aa.array(), wc);
	    children[idx++]= fFactory.makeConstant(eltTypeRef);
	    children[idx++]= walkNodes(index, wc);

	    return makeNode(wc, fFactory, aa, CAstNode.ARRAY_REF, children);
	}

	public CAstNode visit(ArrayConstructor ac, WalkContext wc) {
	    Expr dist= ac.distribution();
	    Expr init= ac.initializer();
	    Type arrayType= ac.type();
	    // TODO Filter arrayType so that e.g. x10.lang.IntReferenceArray becomes int[] so
	    // that WALA doesn't complain that an array type doesn't seem to be an array type.
	    TypeReference arrayTypeRef= fIdentityMapper.getTypeRef(arrayType);
	    Type baseType= ac.arrayBaseType().type();
	    TypeReference baseTypeRef= fIdentityMapper.getTypeRef(baseType);

	    if (init instanceof Closure) {
		Closure closure= (Closure) init;
		Formal formal1= (Formal) closure.formals().get(0); // The closure for an array ctor init always has a single argument
		// Turn this construct into an array allocation followed by a region
		// iteration whose body calls the initializer and assigns the result
		// to the corresponding array slot.
		//
		// BLOCK_EXPR [
		//     ASSIGN [ arrayTmp, NEW [ type, dist.region ] ],
		//     for(point p: dist.region) {
		//         ASSIGN [ ARRAY_REF [ arrayTmp, p ], CALL [ closure, p ] ]
		//     }
		//     tmp
		// ]
		//
		CAstNode closureNode= walkNodes(closure, wc);
		String arrayTempName= "array temp";
		String distTempName= "dist temp";
		CAstSymbol arrayTemp= new AstTranslator.InternalCAstSymbol(arrayTempName, true);
		CAstSymbol distTemp= new AstTranslator.InternalCAstSymbol(distTempName, true);
		CAstNode distDeclNode=
			makeNode(wc, dist, CAstNode.DECL_STMT,
					fFactory.makeConstant(distTemp),
					walkNodes(dist, wc));
		CAstNode arrayNewNode= 
			makeNode(wc, ac, CAstNode.DECL_STMT,
				fFactory.makeConstant(arrayTemp),
				makeNode(wc, ac, CAstNode.NEW,
					fFactory.makeConstant(arrayTypeRef),
					makeNode(wc, fFactory, dist, CAstNode.VAR, fFactory.makeConstant(distTempName))));
		int dummyPC = 0; // Just wrap the kind of call; the "rear end" won't care about anything else...
		MethodReference closureRef= createMethodRefForClosure(closure);
		CallSiteReference closureCallSiteRef= CallSiteReference.make(dummyPC, closureRef, IInvokeInstruction.Dispatch.VIRTUAL);
		CAstNode arrayElemInit= makeNode(wc, closure, CAstNode.BLOCK_EXPR,
			makeNode(wc, formal1, CAstNode.ASSIGN,
				makeNode(wc, closure, CAstNode.ARRAY_REF,
					makeNode(wc, closure, CAstNode.VAR, fFactory.makeConstant(arrayTempName)),
					fFactory.makeConstant(baseTypeRef),
					makeNode(wc, fFactory, formal1, CAstNode.VAR, fFactory.makeConstant(formal1.name()))),
				makeNode(wc, closure, CAstNode.CALL,
					closureNode,
					fFactory.makeConstant(closureCallSiteRef),
					makeNode(wc, fFactory, formal1, CAstNode.VAR, fFactory.makeConstant(formal1.name())))));

		CAstNode loopBody=
			walkRegionIterator(formal1, arrayElemInit,
					makeNode(wc, fFactory, dist, CAstNode.VAR, fFactory.makeConstant(distTempName)), wc);

		return makeNode(wc, closure, CAstNode.BLOCK_EXPR, // NEED CAstNode.LOCAL_SCOPE or make "array temp" names unique
			distDeclNode,
			arrayNewNode,
			loopBody,
			makeNode(wc, fFactory, formal1, CAstNode.VAR, fFactory.makeConstant(arrayTempName)));
	    } else if (init instanceof ArrayInit) {
		ArrayInit arrayInit= (ArrayInit) init;
		// ARRAY_NEW [ type, dist.region, walkNodes(init, wc) ]
		CAstNode[] eltNodes= new CAstNode[arrayInit.elements().size()+1];
		int idx= 0;
		eltNodes[idx++]= makeNode(wc, ac, CAstNode.NEW,
			fFactory.makeConstant(baseTypeRef),
			walkNodes(dist, wc));
		for(Iterator iter= arrayInit.elements().iterator(); iter.hasNext(); ) {
		    Expr elem= (Expr) iter.next();
		    eltNodes[idx++]= walkNodes(elem, wc);
		}
		return makeNode(wc, init, CAstNode.ARRAY_LITERAL, eltNodes);
	    } else {
		Assertions.UNREACHABLE("ArrayConstructor has non-closure, non-ArrayInit initializer of type " + init.getClass());
		return null;
	    }
	}

	private String castNameForType(Type type) {
	    return getTypeDict().getCAstTypeFor(type).getName();
	}

	private MethodReference createMethodRefForClosure(Closure closure) {
	    List formals= closure.formals();
	    TypeName[] argTypes= new TypeName[formals.size()];
	    for(int i= 0; i < argTypes.length; i++) {
		Formal f= (Formal) formals.get(i);
		argTypes[i]= TypeName.findOrCreate(castNameForType(f.type().type()));
	    }
	    TypeName retType= TypeName.findOrCreate(castNameForType(closure.returnType().type()));
	    MethodReference closureRef= MethodReference.findOrCreate(
		    TypeReference.findOrCreate(fClassLoaderRef, "Lclosure" + new PolyglotSourcePosition(closure.position())),
		    new Selector(Atom.findOrCreateAsciiAtom("invoke"), Descriptor.findOrCreate(argTypes, retType)));
	    return closureRef;
	}

	public CAstNode visit(GenParameterExpr gpe, WalkContext context) {
	    // TODO Auto-generated method stub
	    return null;
	}

	public CAstNode visit(ForLoop f, WalkContext context) {
	    return walkRegionIterator(f, walkNodes(f.body(), context), context);
	}

	public CAstNode visit(Closure closure, WalkContext wc) {
	    CAstEntity bodyEntity= walkClosureEntity(closure, closure.body(), wc);
	    CAstNode closureNode= fFactory.makeNode(CAstNode.FUNCTION_EXPR, fFactory.makeConstant(bodyEntity));

	    wc.addScopedEntity(closureNode, bodyEntity);
	    return closureNode;
	}
    }

    private final class ClosureBodyEntity extends CodeBodyEntity {
	private final CodeBodyContext fContext;

	private final CAstNode fBodyAst;

	private final CAstSourcePositionMap.Position fPosition;

	private final ClosureBodyType fBodyType;

	private final CAstType fEnclosingType;

	public ClosureBodyEntity(Map<CAstNode,CAstEntity> entities, Closure node, CodeBodyContext context, CAstNode bodyAst, Type enclosingType) {
	    super(entities);
	    fContext= context;
	    fBodyAst= bodyAst;
	    fPosition= makePosition(node.position());
	    fEnclosingType= getTypeDict().getCAstTypeFor(enclosingType);
	    fBodyType= new ClosureBodyType((ClosureType) node.type(), fEnclosingType);
	}
	public CAstNode getAST() {
	    return fBodyAst;
	}

	public int getArgumentCount() {
	    return fBodyType.getArgumentCount();
	}

	public CAstNode[] getArgumentDefaults() {
	    return new CAstNode[0];
	}

	public String[] getArgumentNames() {
	    return new String[] { "p" }; // TODO where to get the arg names?
	}

	public CAstControlFlowMap getControlFlow() {
	    return fContext.cfg();
	}

	public int getKind() {
	    return X10CAstEntity.CLOSURE_BODY;
	}

	public String getName() {
	    return "invoke";
	}

	public CAstNodeTypeMap getNodeTypeMap() {
	    return fContext.getNodeTypeMap();
	}

	public Position getPosition() {
	    return fPosition;
	}

	public Collection getQualifiers() {
	    return Collections.EMPTY_LIST;
	}

	public CAstSourcePositionMap getSourceMap() {
	    return fContext.pos();
	}

	public CAstType getType() {
	    return fBodyType;
	}
    }

    private final class ClosureBodyType implements CAstType.Method {
	private final ClosureType closureType;

	private List<CAstType> argTypes;

	private List<CAstType> excTypes;

	private CAstType returnType;

	private CAstType enclosingType;

	public ClosureBodyType(ClosureType cType, CAstType enclosingType) {
	    closureType= cType;
	    this.enclosingType= enclosingType;
	}

	public int getArgumentCount() {
	    return closureType.argumentTypes().size();
	}

	private List<CAstType> mapTypes(List<Type> types) {
	    List<CAstType> castTypes= new ArrayList<CAstType>();
	    for(Iterator iter= types.iterator(); iter.hasNext(); ) {
		Type type= (Type) iter.next();
		CAstType castType= getTypeDict().getCAstTypeFor(type);
		castTypes.add(castType);
	    }
	    return castTypes;
	}

	public List getArgumentTypes() {
	    if (argTypes == null) {
		argTypes= mapTypes(closureType.argumentTypes());
	    }
	    return argTypes;
	}

	public Collection getExceptionTypes() {
	    if (excTypes == null) {
		excTypes= mapTypes(closureType.throwTypes());
	    }
	    return excTypes;
	}

	public CAstType getReturnType() {
	    if (returnType == null) {
		returnType= getTypeDict().getCAstTypeFor(closureType.returnType());
	    }
	    return returnType;
	}

	public String getName() {
	    return ""; // Closures have no names
	}

	public Collection getSupertypes() {
	    return Collections.EMPTY_LIST;
	}

	public CAstType getDeclaringType() {
	    return enclosingType;
	}
    }

    protected CAstNode walkNodes(Node n, WalkContext context) {
	if (n == null) return fFactory.makeNode(CAstNode.EMPTY);
	return X10ASTTraverser.visit(n, (X10TranslatorVisitor) getTranslator(), context);
    }
}
