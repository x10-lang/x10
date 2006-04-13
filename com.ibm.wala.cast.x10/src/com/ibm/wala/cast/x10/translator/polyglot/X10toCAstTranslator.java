/*
 * Created on Sep 8, 2005
 */
package com.ibm.domo.ast.x10.translator.polyglot;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.ext.x10.ast.*;
import polyglot.ext.x10.types.FutureType;
import polyglot.types.LocalInstance;
import polyglot.types.MethodInstance;
import polyglot.types.ReferenceType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;

import com.ibm.capa.ast.CAstControlFlowMap;
import com.ibm.capa.ast.CAstEntity;
import com.ibm.capa.ast.CAstNode;
import com.ibm.capa.ast.CAstNodeTypeMap;
import com.ibm.capa.ast.CAstSourcePositionMap;
import com.ibm.capa.ast.CAstType;
import com.ibm.capa.impl.debug.Assertions;
import com.ibm.domo.ast.java.translator.polyglot.PolyglotJava2CAstTranslator;
import com.ibm.domo.ast.java.translator.polyglot.PolyglotTypeDictionary;
import com.ibm.domo.ast.java.translator.polyglot.TranslatingVisitor;
import com.ibm.domo.ast.x10.translator.X10CAstEntity;
import com.ibm.domo.ast.x10.translator.X10CastNode;
import com.ibm.domo.types.ClassLoaderReference;
import com.ibm.domo.types.TypeReference;
import com.ibm.domo.util.IteratorPlusOne;

public class X10toCAstTranslator extends PolyglotJava2CAstTranslator {
    public X10toCAstTranslator(ClassLoaderReference clr, NodeFactory nf, TypeSystem ts) {
	super(clr, nf, ts);
    }

    protected TranslatingVisitor createTranslator() {
        return new X10TranslatingVisitorImpl();
    }

    protected PolyglotTypeDictionary createTypeDict() {
	return new X10TypeDictionary(fTypeSystem, this);
    }

    protected CAstEntity walkAsyncEntity(final Node rootNode, final Node bodyNode, final WalkContext context) {
	Map/*<CAstNode,CAstEntity>*/ childEntities= new HashMap();
	final CodeBodyContext asyncContext= new CodeBodyContext(context, childEntities);
	final CAstNode bodyAST= walkNodes(bodyNode, asyncContext);

	return new AsyncEntity(childEntities, rootNode, asyncContext, bodyAST);
    }

    private final class AsyncBodyType implements CAstType.Method {
	private final Node fNode;

	private final CodeBodyContext fContext;

	private AsyncBodyType(Node node, CodeBodyContext context) {
	    super();
	    fNode= node;
	    fContext= context;
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
	    return getTypeDict().getCAstTypeFor(fContext.getEnclosingMethod().container());
	}
    }

    private final class AsyncEntity extends CodeBodyEntity {
	private final Node fNode;

	private final CodeBodyContext fContext;

	private final CAstNode fBodyast;

	private AsyncEntity(Map/*<CAstNode,CAstEntity>*/ entities, Node node, CodeBodyContext context, CAstNode bodyast) {
	    super(entities);
	    fNode= node;
	    fContext= context;
	    fBodyast= bodyast;
	}

	public int getKind() {
	    return X10CAstEntity.ASYNC_BODY;
	}

	public String getName() {
	    return "<activity " + fNode.position().file() + ":" + fNode.position().line() + ":" + fNode.position().column() + ">";
	}

	public String[] getArgumentNames() {
	    return new String[] { "<place>" };
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
	    return new AsyncBodyType(fNode, fContext);
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
		    fFactory.makeConstant(bodyEntity));

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
					    fFactory.makeConstant(bodyEntity));

	    context.addScopedEntity(bodyNode, bodyEntity);
	    return walkRegionIterator(f, bodyNode, context);
	}

	private CAstNode walkRegionIterator(X10Loop loop, final CAstNode bodyNode, WalkContext context) {
	    return walkRegionIterator(loop, bodyNode, walkNodes(loop.domain(), context), context);
	}

	private CAstNode walkRegionIterator(X10Loop loop, final CAstNode bodyNode, CAstNode domainNode, WalkContext context) {
//	    Expr regionExpr= loop.domain();
	    X10Formal formal= (X10Formal)loop.formal();
	    LocalInstance[] vars = formal.localInstances();
	    CAstNode[] varDecls = new CAstNode[vars.length + 1];
	    for (int i = 0; i < vars.length; i++)
		varDecls[i] = fFactory.makeNode(CAstNode.DECL_STMT,
		    fFactory.makeNode(CAstNode.VAR, fFactory.makeConstant(vars[i].name())),
			fFactory.makeNode(CAstNode.ARRAY_REF, walkNodes(formal, context),
			    fFactory.makeConstant(TypeReference.Int),
			    fFactory.makeConstant(i)));
	    varDecls[vars.length] = bodyNode;

	    return fFactory.makeNode(CAstNode.LOCAL_SCOPE,
		fFactory.makeNode(CAstNode.BLOCK_STMT,
		    fFactory.makeNode(CAstNode.DECL_STMT, fFactory.makeNode(CAstNode.VAR, fFactory.makeConstant("iter tmp")),
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
			fFactory.makeConstant(bodyEntity));

	    context.addScopedEntity(bodyNode, bodyEntity);
	    return fFactory.makeNode(CAstNode.LOCAL_SCOPE,
		fFactory.makeNode(CAstNode.BLOCK_STMT,
			fFactory.makeNode(CAstNode.DECL_STMT, fFactory.makeNode(CAstNode.VAR, fFactory.makeConstant("dist temp")), dist),
			walkRegionIterator(a, bodyNode, fFactory.makeNode(CAstNode.VAR, fFactory.makeConstant("dist temp")), context)));
	}

	public CAstNode visit(Future f, WalkContext context) {
	    CAstEntity bodyEntity= walkAsyncEntity(f, f.body(), context);
	    CAstNode bodyNode= fFactory.makeNode(X10CastNode.ASYNC_INVOKE,
		    walkNodes(f.place(), context),
		    fFactory.makeConstant(bodyEntity));

	    context.addScopedEntity(bodyNode, bodyEntity);
	    return bodyNode;
	}

	public CAstNode visit(Call c, WalkContext wc) {
	    MethodInstance methodInstance= c.methodInstance();
	    ReferenceType methodOwner= methodInstance.container();

	    if (methodOwner instanceof FutureType) {
		FutureType type= (FutureType) methodOwner;
		TypeReference typeRef= TypeReference.findOrCreate(fClassLoaderRef, typeToTypeID(type.base()));

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
			    wrapBodyInAtomic(fFactory.makeNode(CAstNode.BLOCK_STMT, whenClauses))),
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
	    // Parser currently expands the various types of formal parameters in constructs
	    // such as foreach, ateach, and "enhanced for".
	    Assertions.UNREACHABLE("X10toCAstTranslator.visit(X10Formal) called?");
	    return null;
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
	    return wrapBodyInAtomic(bodyNode);
	}

	private CAstNode wrapBodyInAtomic(final CAstNode bodyNode) {
	    return fFactory.makeNode(CAstNode.UNWIND, 
		    fFactory.makeNode(CAstNode.BLOCK_STMT, 
			    fFactory.makeNode(X10CastNode.ATOMIC_ENTER),
			    bodyNode),
		    fFactory.makeNode(X10CastNode.ATOMIC_EXIT));
	}

	public CAstNode visit(ArrayConstructor ac, WalkContext context) {
	    // TODO Auto-generated method stub
	    return null;
	}

	public CAstNode visit(GenParameterExpr gpe, WalkContext context) {
	    // TODO Auto-generated method stub
	    return null;
	}

	public CAstNode visit(ForLoop_c f, WalkContext context) {
	    return walkRegionIterator(f, walkNodes(f.body(), context), context);
	}
    }

    protected CAstNode walkNodes(Node n, WalkContext context) {
	if (n == null) return fFactory.makeNode(CAstNode.EMPTY);
	return X10ASTTraverser.visit(n, (X10TranslatorVisitor) getTranslator(), context);
    }
}
